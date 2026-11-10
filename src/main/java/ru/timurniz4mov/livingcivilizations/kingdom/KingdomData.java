package ru.timurniz4mov.livingcivilizations.kingdom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class KingdomData extends SavedData {
    private static final String DATA_NAME = "living_civilizations_kingdoms";
    private final Map<String, Kingdom> kingdoms = new LinkedHashMap<>();

    public static KingdomData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(KingdomData::load, KingdomData::new, DATA_NAME);
    }

    public static KingdomData load(CompoundTag tag) {
        KingdomData data = new KingdomData();
        ListTag list = tag.getList("kingdoms", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag saved = list.getCompound(i);
            if (!saved.hasUUID("owner")) continue;
            Kingdom kingdom = new Kingdom(saved.getString("name"), saved.getUUID("owner"));
            ListTag claims = saved.getList("claims", Tag.TAG_LONG);
            for (int j = 0; j < claims.size(); j++) kingdom.claims.add(claims.getLong(j));
            ListTag wars = saved.getList("wars", Tag.TAG_STRING);
            for (int j = 0; j < wars.size(); j++) kingdom.wars.add(wars.getString(j));
            data.kingdoms.put(key(kingdom.name), kingdom);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Kingdom kingdom : kingdoms.values()) {
            CompoundTag saved = new CompoundTag();
            saved.putString("name", kingdom.name);
            saved.putUUID("owner", kingdom.owner);
            ListTag claims = new ListTag();
            for (long claim : kingdom.claims) claims.add(net.minecraft.nbt.LongTag.valueOf(claim));
            saved.put("claims", claims);
            ListTag wars = new ListTag();
            for (String war : kingdom.wars) wars.add(net.minecraft.nbt.StringTag.valueOf(war));
            saved.put("wars", wars);
            list.add(saved);
        }
        tag.put("kingdoms", list);
        return tag;
    }

    public boolean create(String name, UUID owner, int chunkX, int chunkZ) {
        String safeName = name.trim();
        if (safeName.isEmpty() || kingdoms.containsKey(key(safeName))) return false;
        Kingdom kingdom = new Kingdom(safeName, owner);
        kingdom.claims.add(chunkKey(chunkX, chunkZ));
        kingdoms.put(key(safeName), kingdom);
        setDirty();
        return true;
    }

    public boolean claim(String name, UUID owner, int chunkX, int chunkZ) {
        Kingdom kingdom = kingdoms.get(key(name));
        if (kingdom == null || !kingdom.owner.equals(owner)) return false;
        long chunk = chunkKey(chunkX, chunkZ);
        if (isClaimedByOther(chunkX, chunkZ, name) || kingdom.claims.contains(chunk)) return false;
        kingdom.claims.add(chunk);
        setDirty();
        return true;
    }

    public boolean isClaimedByOther(int chunkX, int chunkZ, String ownName) {
        long target = chunkKey(chunkX, chunkZ);
        for (Kingdom kingdom : kingdoms.values()) {
            if (!kingdom.name.equalsIgnoreCase(ownName) && kingdom.claims.contains(target)) return true;
        }
        return false;
    }

    public boolean declareWar(String ownName, UUID owner, String targetName) {
        Kingdom own = kingdoms.get(key(ownName));
        Kingdom target = kingdoms.get(key(targetName));
        if (own == null || target == null || own == target || !own.owner.equals(owner)) return false;
        boolean changed = own.wars.add(target.name);
        changed |= target.wars.add(own.name);
        if (changed) setDirty();
        return changed;
    }

    public String getNameForOwner(UUID owner) {
        for (Kingdom kingdom : kingdoms.values()) if (kingdom.owner.equals(owner)) return kingdom.name;
        return null;
    }

    public Collection<KingdomView> getKingdoms() {
        List<KingdomView> views = new ArrayList<>();
        for (Kingdom kingdom : kingdoms.values()) {
            views.add(new KingdomView(kingdom.name, kingdom.claims.size(), List.copyOf(kingdom.wars)));
        }
        return List.copyOf(views);
    }

    private static String key(String name) { return name.toLowerCase(Locale.ROOT); }
    private static long chunkKey(int x, int z) { return ((long) x << 32) ^ (z & 0xffffffffL); }

    private static final class Kingdom {
        private final String name;
        private final UUID owner;
        private final Set<Long> claims = new LinkedHashSet<>();
        private final Set<String> wars = new LinkedHashSet<>();
        private Kingdom(String name, UUID owner) { this.name = name; this.owner = owner; }
    }

    public record KingdomView(String name, int claimCount, List<String> wars) { }
}

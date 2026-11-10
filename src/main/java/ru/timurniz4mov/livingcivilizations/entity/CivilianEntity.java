package ru.timurniz4mov.livingcivilizations.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.network.chat.Component;
import ru.timurniz4mov.livingcivilizations.LivingCivilizations;
import ru.timurniz4mov.livingcivilizations.economy.PlayerEconomy;

public class CivilianEntity extends PathfinderMob {
    private final NpcRole role;
    private int villageRestTicks;
    private int villageRestCooldown;

    public CivilianEntity(EntityType<? extends CivilianEntity> type, Level level, NpcRole role) {
        super(type, level);
        this.role = role;
        this.xpReward = role.isCombat() ? 5 : 1;
    }

    public NpcRole getRole() { return role; }
    public boolean isCombatRole() { return role.isCombat(); }

    public ResourceLocation getSkin() {
        return new ResourceLocation(LivingCivilizations.MOD_ID, "textures/entity/" + role.texture() + ".png");
    }

    public static AttributeSupplier.Builder createAttributes(NpcRole role) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, role.isCombat() ? 28.0D : 20.0D)
                .add(Attributes.MOVEMENT_SPEED, role == NpcRole.TRAVELER || role == NpcRole.CARAVAN_TRADER ? 0.29D : 0.24D)
                .add(Attributes.ATTACK_DAMAGE, role.isCombat() ? 4.0D : 1.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        if (role.isCombat()) {
            this.goalSelector.addGoal(1, new MeleeAttackGoal(this, role == NpcRole.WARRIOR ? 1.25D : 1.12D, true));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BanditEntity.class, true));
        } else {
            this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, BanditEntity.class, 9.0F, 1.15D, 1.3D));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        }
        if (role != NpcRole.TRADER) {
            this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, role == NpcRole.TRAVELER || role == NpcRole.CARAVAN_TRADER ? 1.08D : 0.82D));
        }
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || role != NpcRole.TRAVELER || !(level() instanceof ServerLevel server)) return;
        if (villageRestTicks > 0) {
            villageRestTicks--;
            getNavigation().stop();
            if (villageRestTicks == 0) {
                setNoAi(false);
                villageRestCooldown = 1600;
            }
            return;
        }
        if (villageRestCooldown > 0) villageRestCooldown--;
        else if (tickCount % 80 == 0 && server.isVillage(blockPosition())) {
            villageRestTicks = 240 + random.nextInt(240);
            setNoAi(true);
            getNavigation().stop();
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        Item[] swords = {Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD};
        Item[] axes = {Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE};
        Item[] picks = {Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE};
        Item[] hoes = {Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE};
        Item[] helmets = {Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET};
        Item[] chestplates = {Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE};
        Item[] leggings = {Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS};
        Item[] boots = {Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS};
        int tier = random.nextInt(4);

        switch (role) {
            case MINER -> setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(picks[tier]));
            case FARMER -> setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(hoes[tier]));
            case BUILDER, WORKER -> setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(random.nextBoolean() ? axes[tier] : picks[tier]));
            case GUARD, WARRIOR, LORD, KING -> {
                setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(role == NpcRole.WARRIOR && random.nextBoolean() ? axes[tier] : swords[tier]));
                int armorTier = role == NpcRole.KING || role == NpcRole.LORD ? Math.max(1, tier) : tier;
                setItemSlot(EquipmentSlot.HEAD, new ItemStack(helmets[armorTier]));
                setItemSlot(EquipmentSlot.CHEST, new ItemStack(chestplates[armorTier]));
                setItemSlot(EquipmentSlot.LEGS, new ItemStack(leggings[armorTier]));
                setItemSlot(EquipmentSlot.FEET, new ItemStack(boots[armorTier]));
            }
            case TRAVELER -> {
                if (random.nextInt(6) == 0) setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ru.timurniz4mov.livingcivilizations.registry.ModItems.TRAVELER_STAFF.get()));
            }
            default -> { }
        }
        if ((role == NpcRole.WORKER || role == NpcRole.FARMER || role == NpcRole.MINER || role == NpcRole.BUILDER) && random.nextInt(3) == 0) {
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        SpawnGroupData spawnData, net.minecraft.nbt.CompoundTag dataTag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        setCustomName(Component.translatable("entity.livingcivilizations." + role.texture()));
        setCustomNameVisible(false);
        return result;
    }

    @Override
    public InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        if (level().isClientSide) return InteractionResult.SUCCESS;
        if (role.isTrader()) {
            PlayerEconomy.tradeWith(player, role);
            return InteractionResult.CONSUME;
        }
        if (role == NpcRole.LORD || role == NpcRole.KING) {
            player.sendSystemMessage(Component.translatable("message.livingcivilizations.royal_advice"));
            player.sendSystemMessage(Component.literal("/civ kingdom create <name> — основать королевство; /civ kingdom claim <name> — купить участок."));
            return InteractionResult.CONSUME;
        }
        player.sendSystemMessage(Component.translatable("message.livingcivilizations.npc_role", Component.translatable("entity.livingcivilizations." + role.texture())));
        return InteractionResult.CONSUME;
    }
}

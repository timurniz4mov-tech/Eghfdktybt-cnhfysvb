package ru.timurniz4mov.livingcivilizations.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.timurniz4mov.livingcivilizations.LivingCivilizations;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LivingCivilizations.MOD_ID);

    public static final RegistryObject<Item> COPPER_COIN = ITEMS.register("copper_coin", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> TRAVELER_STAFF = ITEMS.register("traveler_staff", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> KINGDOM_MAP = ITEMS.register("kingdom_map", () -> new KingdomMapItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TRAVELER_EGG = egg("traveler_spawn_egg", ModEntities.TRAVELER, 0x8b6244, 0xd8bd8a);
    public static final RegistryObject<Item> TRADER_EGG = egg("trader_spawn_egg", ModEntities.TRADER, 0x8f633f, 0xe5c36f);
    public static final RegistryObject<Item> CARAVAN_TRADER_EGG = egg("caravan_trader_spawn_egg", ModEntities.CARAVAN_TRADER, 0x5b4938, 0xc99155);
    public static final RegistryObject<Item> WORKER_EGG = egg("worker_spawn_egg", ModEntities.WORKER, 0x92704e, 0xb8a17c);
    public static final RegistryObject<Item> FARMER_EGG = egg("farmer_spawn_egg", ModEntities.FARMER, 0x638348, 0xc9a36c);
    public static final RegistryObject<Item> MINER_EGG = egg("miner_spawn_egg", ModEntities.MINER, 0x55575f, 0x8d969d);
    public static final RegistryObject<Item> BUILDER_EGG = egg("builder_spawn_egg", ModEntities.BUILDER, 0x9a744a, 0xd2b080);
    public static final RegistryObject<Item> GUARD_EGG = egg("guard_spawn_egg", ModEntities.GUARD, 0x465b70, 0xb9bfc6);
    public static final RegistryObject<Item> WARRIOR_EGG = egg("warrior_spawn_egg", ModEntities.WARRIOR, 0x6d3b34, 0xa6a6a6);
    public static final RegistryObject<Item> LORD_EGG = egg("lord_spawn_egg", ModEntities.LORD, 0x704c74, 0xc4aa6b);
    public static final RegistryObject<Item> KING_EGG = egg("king_spawn_egg", ModEntities.KING, 0x9a762c, 0xf0d77a);
    public static final RegistryObject<Item> BANDIT_EGG = egg("bandit_spawn_egg", ModEntities.BANDIT, 0x342d2d, 0x8c3d31);

    private static <T extends Mob> RegistryObject<Item> egg(String id, RegistryObject<EntityType<T>> type, int primary, int secondary) {
        return ITEMS.register(id, () -> new ForgeSpawnEggItem(type, primary, secondary, new Item.Properties()));
    }

    private ModItems() { }
}

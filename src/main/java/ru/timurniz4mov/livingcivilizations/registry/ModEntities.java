package ru.timurniz4mov.livingcivilizations.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.timurniz4mov.livingcivilizations.LivingCivilizations;
import ru.timurniz4mov.livingcivilizations.entity.BanditEntity;
import ru.timurniz4mov.livingcivilizations.entity.CivilianEntity;
import ru.timurniz4mov.livingcivilizations.entity.NpcRole;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LivingCivilizations.MOD_ID);

    public static final RegistryObject<EntityType<CivilianEntity>> TRAVELER = civilian("traveler", NpcRole.TRAVELER);
    public static final RegistryObject<EntityType<CivilianEntity>> TRADER = civilian("trader", NpcRole.TRADER);
    public static final RegistryObject<EntityType<CivilianEntity>> CARAVAN_TRADER = civilian("caravan_trader", NpcRole.CARAVAN_TRADER);
    public static final RegistryObject<EntityType<CivilianEntity>> WORKER = civilian("worker", NpcRole.WORKER);
    public static final RegistryObject<EntityType<CivilianEntity>> FARMER = civilian("farmer", NpcRole.FARMER);
    public static final RegistryObject<EntityType<CivilianEntity>> MINER = civilian("miner", NpcRole.MINER);
    public static final RegistryObject<EntityType<CivilianEntity>> BUILDER = civilian("builder", NpcRole.BUILDER);
    public static final RegistryObject<EntityType<CivilianEntity>> GUARD = civilian("guard", NpcRole.GUARD);
    public static final RegistryObject<EntityType<CivilianEntity>> WARRIOR = civilian("warrior", NpcRole.WARRIOR);
    public static final RegistryObject<EntityType<CivilianEntity>> LORD = civilian("lord", NpcRole.LORD);
    public static final RegistryObject<EntityType<CivilianEntity>> KING = civilian("king", NpcRole.KING);

    public static final RegistryObject<EntityType<BanditEntity>> BANDIT = ENTITY_TYPES.register("bandit", () ->
            EntityType.Builder.of(BanditEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("bandit"));

    private static RegistryObject<EntityType<CivilianEntity>> civilian(String id, NpcRole role) {
        return ENTITY_TYPES.register(id, () -> EntityType.Builder.of(
                        (EntityType<CivilianEntity> type, net.minecraft.world.level.Level level) -> new CivilianEntity(type, level, role),
                        MobCategory.CREATURE)
                .sized(0.6F, 1.95F).clientTrackingRange(10).build(id));
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(TRAVELER.get(), CivilianEntity.createAttributes(NpcRole.TRAVELER).build());
        event.put(TRADER.get(), CivilianEntity.createAttributes(NpcRole.TRADER).build());
        event.put(CARAVAN_TRADER.get(), CivilianEntity.createAttributes(NpcRole.CARAVAN_TRADER).build());
        event.put(WORKER.get(), CivilianEntity.createAttributes(NpcRole.WORKER).build());
        event.put(FARMER.get(), CivilianEntity.createAttributes(NpcRole.FARMER).build());
        event.put(MINER.get(), CivilianEntity.createAttributes(NpcRole.MINER).build());
        event.put(BUILDER.get(), CivilianEntity.createAttributes(NpcRole.BUILDER).build());
        event.put(GUARD.get(), CivilianEntity.createAttributes(NpcRole.GUARD).build());
        event.put(WARRIOR.get(), CivilianEntity.createAttributes(NpcRole.WARRIOR).build());
        event.put(LORD.get(), CivilianEntity.createAttributes(NpcRole.LORD).build());
        event.put(KING.get(), CivilianEntity.createAttributes(NpcRole.KING).build());
        event.put(BANDIT.get(), BanditEntity.createAttributes().build());
    }

    private ModEntities() { }
}

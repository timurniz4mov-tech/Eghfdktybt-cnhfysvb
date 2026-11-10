package ru.timurniz4mov.livingcivilizations.world;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.timurniz4mov.livingcivilizations.LivingCivilizations;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, LivingCivilizations.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SETTLEMENT = FEATURES.register(
            "settlement", () -> new SettlementFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CASTLE = FEATURES.register(
            "castle", () -> new CastleFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BANDIT_CAMP = FEATURES.register(
            "bandit_camp", () -> new BanditCampFeature(NoneFeatureConfiguration.CODEC));

    private ModFeatures() { }
}

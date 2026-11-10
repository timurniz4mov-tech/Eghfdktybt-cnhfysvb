package ru.timurniz4mov.livingcivilizations;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.timurniz4mov.livingcivilizations.registry.ModEntities;
import ru.timurniz4mov.livingcivilizations.registry.ModItems;
import ru.timurniz4mov.livingcivilizations.registry.ModCreativeTabs;
import ru.timurniz4mov.livingcivilizations.world.ModFeatures;

@Mod(LivingCivilizations.MOD_ID)
public class LivingCivilizations {
    public static final String MOD_ID = "livingcivilizations";

    public LivingCivilizations() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITY_TYPES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        modBus.addListener(ModEntities::registerAttributes);
        modBus.addListener(ModEntities::registerSpawnPlacements);
    }
}

package ru.timurniz4mov.livingcivilizations;

import net.minecraftforge.eventbus.api.IEventBus;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.timurniz4mov.livingcivilizations.registry.ModEntities;
import ru.timurniz4mov.livingcivilizations.registry.ModItems;
import ru.timurniz4mov.livingcivilizations.registry.ModCreativeTabs;

@Mod(LivingCivilizations.MOD_ID)
public class LivingCivilizations {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "livingcivilizations";

    public LivingCivilizations() {
        LOGGER.info("Living Civilizations diagnostic build: custom world generation is disabled.");
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITY_TYPES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        modBus.addListener(ModEntities::registerAttributes);
    }
}

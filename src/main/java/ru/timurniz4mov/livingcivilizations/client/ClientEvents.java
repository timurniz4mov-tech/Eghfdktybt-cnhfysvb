package ru.timurniz4mov.livingcivilizations.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timurniz4mov.livingcivilizations.LivingCivilizations;
import ru.timurniz4mov.livingcivilizations.registry.ModEntities;

@Mod.EventBusSubscriber(modid = LivingCivilizations.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TRAVELER.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.TRADER.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.CARAVAN_TRADER.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.WORKER.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.FARMER.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.MINER.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.BUILDER.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.GUARD.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.WARRIOR.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.LORD.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.KING.get(), NpcRenderer::new);
        event.registerEntityRenderer(ModEntities.BANDIT.get(), BanditRenderer::new);
    }

    private ClientEvents() { }
}

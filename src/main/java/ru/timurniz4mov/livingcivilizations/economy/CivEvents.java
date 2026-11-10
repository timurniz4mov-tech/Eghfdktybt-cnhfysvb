package ru.timurniz4mov.livingcivilizations.economy;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timurniz4mov.livingcivilizations.LivingCivilizations;
import ru.timurniz4mov.livingcivilizations.command.CivCommands;
import ru.timurniz4mov.livingcivilizations.entity.BanditEntity;

@Mod.EventBusSubscriber(modid = LivingCivilizations.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CivEvents {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CivCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerEconomy.rewardForBlockBreak(player, event.getState().getBlock());
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) PlayerEconomy.rewardForPlacement(player);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getEntity().getPersistentData().putInt("lc_money", event.getOriginal().getPersistentData().getInt("lc_money"));
        event.getEntity().getPersistentData().putString("lc_job", event.getOriginal().getPersistentData().getString("lc_job"));
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof BanditEntity && event.getSource().getEntity() instanceof ServerPlayer player) {
            PlayerEconomy.rewardForBanditKill(player);
        }
    }

    private CivEvents() { }
}

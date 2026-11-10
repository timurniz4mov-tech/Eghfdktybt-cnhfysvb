package ru.timurniz4mov.livingcivilizations.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import ru.timurniz4mov.livingcivilizations.kingdom.KingdomData;

public class KingdomMapItem extends Item {
    public KingdomMapItem(Properties properties) { super(properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            KingdomData data = KingdomData.get(serverLevel);
            player.sendSystemMessage(Component.literal("§6=== Карта королевств ==="));
            if (data.getKingdoms().isEmpty()) {
                player.sendSystemMessage(Component.literal("Пока нет известных королевств. Создай своё: /civ kingdom create <имя>"));
            } else {
                data.getKingdoms().forEach(k -> player.sendSystemMessage(Component.literal(
                        "§e" + k.name() + " §7— участков: " + k.claimCount() + (k.wars().isEmpty() ? "" : ", война: " + String.join(", ", k.wars())))));
            }
            player.sendSystemMessage(Component.literal("Это текстовая карта-первый этап. Рисуемая мини-карта рельефа и границ будет отдельным обновлением."));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

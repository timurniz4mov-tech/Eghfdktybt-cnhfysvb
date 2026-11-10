package ru.timurniz4mov.livingcivilizations.economy;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.tags.BlockTags;
import ru.timurniz4mov.livingcivilizations.entity.NpcRole;

public final class PlayerEconomy {
    private static final String MONEY = "lc_money";
    private static final String JOB = "lc_job";
    private static final String LAST_REWARD = "lc_last_reward";

    public static int getMoney(Player player) { return player.getPersistentData().getInt(MONEY); }
    public static String getJob(Player player) { return player.getPersistentData().getString(JOB); }

    public static void setJob(Player player, String job) {
        player.getPersistentData().putString(JOB, job);
    }

    public static void addMoney(Player player, int amount) {
        if (amount <= 0) return;
        player.getPersistentData().putInt(MONEY, Math.max(0, getMoney(player) + amount));
    }

    public static boolean spend(Player player, int amount) {
        if (amount < 0 || getMoney(player) < amount) return false;
        player.getPersistentData().putInt(MONEY, getMoney(player) - amount);
        return true;
    }

    public static void rewardForBlockBreak(ServerPlayer player, Block block) {
        String job = getJob(player);
        boolean matched = switch (job) {
            case "miner" -> block.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE);
            case "lumberjack" -> block.defaultBlockState().is(BlockTags.LOGS);
            case "farmer" -> block instanceof CropBlock;
            default -> false;
        };
        if (matched) rewardWithCooldown(player, 2 + player.getRandom().nextInt(3));
    }

    public static void rewardForPlacement(ServerPlayer player) {
        if ("builder".equals(getJob(player))) rewardWithCooldown(player, 2);
    }

    public static void rewardForBanditKill(ServerPlayer player) {
        if ("guard".equals(getJob(player)) || "warrior".equals(getJob(player))) rewardWithCooldown(player, 12);
    }

    private static void rewardWithCooldown(ServerPlayer player, int amount) {
        long now = player.level().getGameTime();
        long last = player.getPersistentData().getLong(LAST_REWARD);
        if (now - last < 20) return;
        player.getPersistentData().putLong(LAST_REWARD, now);
        addMoney(player, amount);
        player.displayClientMessage(Component.literal("§6+" + amount + " монет §7(баланс: " + getMoney(player) + ")"), true);
    }

    public static boolean sellHeldItem(Player player) {
        if (!"trader".equals(getJob(player))) return false;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return false;
        int unitPrice;
        if (held.is(Items.DIAMOND)) unitPrice = 30;
        else if (held.is(Items.EMERALD)) unitPrice = 18;
        else if (held.is(Items.IRON_INGOT)) unitPrice = 8;
        else if (held.is(Items.GOLD_INGOT)) unitPrice = 10;
        else if (held.is(Items.COPPER_INGOT)) unitPrice = 4;
        else if (Block.byItem(held.getItem()).defaultBlockState().is(BlockTags.LOGS)) unitPrice = 2;
        else if (held.is(Items.COBBLESTONE) || held.is(Items.DIRT)) unitPrice = 1;
        else unitPrice = 1;
        int earnings = unitPrice * held.getCount();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        addMoney(player, earnings);
        player.sendSystemMessage(Component.literal("§aПродано за " + earnings + " монет. Баланс: " + getMoney(player)));
        return true;
    }

    public static void tradeWith(Player player, NpcRole role) {
        int price = role == NpcRole.CARAVAN_TRADER ? 18 : 10;
        ItemStack goods = role == NpcRole.CARAVAN_TRADER
                ? new ItemStack(Items.IRON_INGOT, 2)
                : new ItemStack(Items.BREAD, 8);
        if (!spend(player, price)) {
            player.sendSystemMessage(Component.literal("§cНе хватает денег. Нужно монет: " + price + ". Твой баланс: " + getMoney(player)));
            return;
        }
        if (!player.getInventory().add(goods)) {
            addMoney(player, price);
            player.sendSystemMessage(Component.literal("§cОсвободи место в инвентаре. Деньги возвращены."));
            return;
        }
        player.sendSystemMessage(Component.literal("§aПокупка совершена! Потрачено " + price + " монет. Остаток: " + getMoney(player)));
    }

    private PlayerEconomy() { }
}

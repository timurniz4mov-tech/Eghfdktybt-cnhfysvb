package ru.timurniz4mov.livingcivilizations.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import ru.timurniz4mov.livingcivilizations.economy.PlayerEconomy;
import ru.timurniz4mov.livingcivilizations.kingdom.KingdomData;
import ru.timurniz4mov.livingcivilizations.registry.ModEntities;

public final class CivCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("civ")
                .then(Commands.literal("help").executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal(
                            "§6Living Civilizations §7— /civ money, /civ job <miner|lumberjack|farmer|builder|guard|warrior|trader>, " +
                            "/civ sell, /civ kingdom create <name>, /civ kingdom claim <name>, /civ kingdom war <name>, /civ map, /civ spawn <role>"), false);
                    return 1;
                }))
                .then(Commands.literal("money").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    context.getSource().sendSuccess(() -> Component.literal("Баланс: §6" + PlayerEconomy.getMoney(player) + " монет"), false);
                    return 1;
                }))
                .then(Commands.literal("job").then(Commands.argument("job", StringArgumentType.word()).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    String job = StringArgumentType.getString(context, "job").toLowerCase();
                    if (!java.util.Set.of("miner", "lumberjack", "farmer", "builder", "guard", "warrior", "trader").contains(job)) {
                        context.getSource().sendFailure(Component.literal("Неизвестная профессия. Доступно: miner, lumberjack, farmer, builder, guard, warrior, trader."));
                        return 0;
                    }
                    PlayerEconomy.setJob(player, job);
                    context.getSource().sendSuccess(() -> Component.literal("Теперь твоя профессия: §e" + job + "§a. Зарабатывай, выполняя работу."), false);
                    return 1;
                })))
                .then(Commands.literal("sell").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    if (!PlayerEconomy.sellHeldItem(player)) {
                        context.getSource().sendFailure(Component.literal("Чтобы продавать вещи, выбери профессию trader командой /civ job trader и держи предмет в руке."));
                        return 0;
                    }
                    return 1;
                }))
                .then(Commands.literal("kingdom")
                        .then(Commands.literal("create").then(Commands.argument("name", StringArgumentType.word()).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String name = StringArgumentType.getString(context, "name");
                            KingdomData data = KingdomData.get(player.serverLevel());
                            boolean created = data.create(name, player.getUUID(), player.chunkPosition().x, player.chunkPosition().z);
                            if (!created) {
                                context.getSource().sendFailure(Component.literal("Королевство с таким именем уже существует или имя пустое."));
                                return 0;
                            }
                            context.getSource().sendSuccess(() -> Component.literal("§6Королевство " + name + " основано! Текущий чанк стал твоей первой землёй."), false);
                            return 1;
                        })))
                        .then(Commands.literal("claim").then(Commands.argument("name", StringArgumentType.word()).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String name = StringArgumentType.getString(context, "name");
                            KingdomData data = KingdomData.get(player.serverLevel());
                            if (PlayerEconomy.getMoney(player) < 100) {
                                context.getSource().sendFailure(Component.literal("Для покупки участка нужно 100 монет. Твой баланс: " + PlayerEconomy.getMoney(player)));
                                return 0;
                            }
                            boolean claimed = data.claim(name, player.getUUID(), player.chunkPosition().x, player.chunkPosition().z);
                            if (!claimed) {
                                context.getSource().sendFailure(Component.literal("Не удалось купить участок: ты не владелец этого королевства, чанк уже занят или уже принадлежит тебе."));
                                return 0;
                            }
                            PlayerEconomy.spend(player, 100);
                            context.getSource().sendSuccess(() -> Component.literal("§aУчасток куплен за 100 монет!"), false);
                            return 1;
                        })))
                        .then(Commands.literal("war").then(Commands.argument("name", StringArgumentType.word()).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String enemy = StringArgumentType.getString(context, "name");
                            KingdomData data = KingdomData.get(player.serverLevel());
                            String ownName = data.getNameForOwner(player.getUUID());
                            boolean declared = ownName != null && data.declareWar(ownName, player.getUUID(), enemy);
                            if (!declared) {
                                context.getSource().sendFailure(Component.literal("Войну нельзя объявить: создай своё королевство и укажи существующего противника."));
                                return 0;
                            }
                            context.getSource().sendSuccess(() -> Component.literal("§cОбъявлена война королевству " + enemy + "!"), false);
                            return 1;
                        }))))
                .then(Commands.literal("map").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    KingdomData data = KingdomData.get(player.serverLevel());
                    context.getSource().sendSuccess(() -> Component.literal("§6=== Известные королевства ==="), false);
                    if (data.getKingdoms().isEmpty()) context.getSource().sendSuccess(() -> Component.literal("Пока не основано ни одного королевства."), false);
                    data.getKingdoms().forEach(k -> context.getSource().sendSuccess(() -> Component.literal(
                            "§e" + k.name() + " §7— участков: " + k.claimCount() + (k.wars().isEmpty() ? "" : ", войны: " + String.join(", ", k.wars()))), false));
                    context.getSource().sendSuccess(() -> Component.literal("§7Карту-предмет можно использовать в руке. Это пока список территорий, не графическая мини-карта."), false);
                    return 1;
                }))
                .then(Commands.literal("spawn").requires(source -> source.hasPermission(2))
                        .then(Commands.argument("role", StringArgumentType.word()).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String role = StringArgumentType.getString(context, "role").toLowerCase();
                            EntityType<?> type = switch (role) {
                                case "traveler" -> ModEntities.TRAVELER.get();
                                case "trader" -> ModEntities.TRADER.get();
                                case "caravan_trader" -> ModEntities.CARAVAN_TRADER.get();
                                case "worker" -> ModEntities.WORKER.get();
                                case "farmer" -> ModEntities.FARMER.get();
                                case "miner" -> ModEntities.MINER.get();
                                case "builder" -> ModEntities.BUILDER.get();
                                case "guard" -> ModEntities.GUARD.get();
                                case "warrior" -> ModEntities.WARRIOR.get();
                                case "lord" -> ModEntities.LORD.get();
                                case "king" -> ModEntities.KING.get();
                                case "bandit" -> ModEntities.BANDIT.get();
                                default -> null;
                            };
                            if (type == null) {
                                context.getSource().sendFailure(Component.literal("Роли: traveler, trader, caravan_trader, worker, farmer, miner, builder, guard, warrior, lord, king, bandit."));
                                return 0;
                            }
                            type.spawn(player.serverLevel(), player.blockPosition().above(), MobSpawnType.COMMAND);
                            context.getSource().sendSuccess(() -> Component.literal("Создан NPC: " + role), false);
                            return 1;
                        }))));
    }

    private CivCommands() { }
}

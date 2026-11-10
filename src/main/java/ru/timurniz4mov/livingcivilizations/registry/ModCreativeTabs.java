package ru.timurniz4mov.livingcivilizations.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import ru.timurniz4mov.livingcivilizations.LivingCivilizations;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LivingCivilizations.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.livingcivilizations"))
            .icon(() -> new ItemStack(ModItems.KINGDOM_MAP.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.COPPER_COIN.get());
                output.accept(ModItems.TRAVELER_STAFF.get());
                output.accept(ModItems.KINGDOM_MAP.get());
                output.accept(ModItems.TRAVELER_EGG.get());
                output.accept(ModItems.TRADER_EGG.get());
                output.accept(ModItems.CARAVAN_TRADER_EGG.get());
                output.accept(ModItems.WORKER_EGG.get());
                output.accept(ModItems.FARMER_EGG.get());
                output.accept(ModItems.MINER_EGG.get());
                output.accept(ModItems.BUILDER_EGG.get());
                output.accept(ModItems.GUARD_EGG.get());
                output.accept(ModItems.WARRIOR_EGG.get());
                output.accept(ModItems.LORD_EGG.get());
                output.accept(ModItems.KING_EGG.get());
                output.accept(ModItems.BANDIT_EGG.get());
            }).build());

    private ModCreativeTabs() { }
}

package net.loadingsignal.algaemod.item;

import net.loadingsignal.algaemod.AlgaeMod;
import net.loadingsignal.algaemod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(AlgaeMod.MODID)
public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AlgaeMod.MODID);

    public static final RegistryObject<CreativeModeTab> ALGAEMOD_TAB = CREATIVE_MODE_TABS.register("algaemod_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ZIRCON.get()))
            .title(Component.translatable("creativetab.tutorial_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.ZIRCON.get());
                        pOutput.accept(ModBlocks.ZIRCON_LAMP.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
package com.example.strangearts.item;

import com.example.strangearts.StrangeArts;
import com.example.strangearts.magic.StrangeSpells;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StrangeArts.MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.strange_arts.main"))
            .icon(() -> new ItemStack(ModItems.EYE_OF_AGAMOTTO.get()))
            .displayItems((params, output) -> {
                output.accept(ModItems.EYE_OF_AGAMOTTO.get());
                output.accept(ModItems.CLOAK_OF_LEVITATION.get());
                output.accept(ModItems.THUNDER_AXE.get());

                // по одному свитку на каждый уровень каждого мистического заклинания
                List<AbstractSpell> spells = List.of(StrangeSpells.MYSTIC_SHIELD.get());
                for (AbstractSpell spell : spells) {
                    for (int level = spell.getMinLevel(); level <= spell.getMaxLevel(); level++) {
                        ItemStack scroll = new ItemStack(ModItems.MYSTIC_SCROLL.get());
                        // как во вкладке свитков Iron's: метод записывает заклинание прямо в предмет
                        ISpellContainer.createScrollContainer(spell, level, scroll);
                        output.accept(scroll);
                    }
                }
            })
            .build());
}

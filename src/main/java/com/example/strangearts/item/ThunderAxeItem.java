package com.example.strangearts.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Громовая секира. Обычный топор; бонус к электрической магии
 * считается в StrangeEvents.onSpellDamage, пока секира в руках у заклинателя.
 */
public class ThunderAxeItem extends AxeItem {
    /** На сколько единиц растёт урон электрических заклинаний. */
    public static final float LIGHTNING_BONUS = 4.0f;

    public ThunderAxeItem() {
        super(Tiers.NETHERITE, new Properties()
                .rarity(Rarity.EPIC)
                .fireResistant()
                .attributes(AxeItem.createAttributes(Tiers.NETHERITE, 5.0f, -3.0f)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.strange_arts.thunder_axe", (int) LIGHTNING_BONUS).withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}

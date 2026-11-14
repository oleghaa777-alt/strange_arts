package com.example.strangearts.item;

import com.example.strangearts.magic.StrangeAttributes;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * Глаз Агамотто. Пока надет в слот Curios, силы мистической магии становится в MULTIPLIER раз больше.
 * Сила считается как произведение множителей, а базовое значение атрибута 1.0,
 * поэтому для «в 3 раза» прибавляем 2.0 (1.0 + 2.0 = 3.0).
 */
public class EyeOfAgamottoItem extends Item implements ICurioItem {
    /** Во сколько раз усиливается мистическая магия. Поменяй число — поменяется и подсказка в игре. */
    public static final int MULTIPLIER = 3;

    public EyeOfAgamottoItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = ArrayListMultimap.create();
        modifiers.put(StrangeAttributes.MYSTIC_SPELL_POWER,
                new AttributeModifier(id, MULTIPLIER - 1, AttributeModifier.Operation.ADD_VALUE));
        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.strange_arts.eye_of_agamotto", MULTIPLIER).withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}

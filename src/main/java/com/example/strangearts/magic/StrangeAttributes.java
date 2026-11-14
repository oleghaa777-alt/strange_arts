package com.example.strangearts.magic;

import com.example.strangearts.StrangeArts;
import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Сила и сопротивление школы «Мистика». Значения (1.0, -100, 100) — те же, что у атрибутов самого Iron's. */
public class StrangeAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, StrangeArts.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> MYSTIC_SPELL_POWER =
            ATTRIBUTES.register("mystic_spell_power", () -> create("attribute.strange_arts.mystic_spell_power"));

    public static final DeferredHolder<Attribute, Attribute> MYSTIC_MAGIC_RESIST =
            ATTRIBUTES.register("mystic_magic_resist", () -> create("attribute.strange_arts.mystic_magic_resist"));

    private static Attribute create(String descriptionId) {
        Attribute attribute = new MagicPercentAttribute(descriptionId, 1.0, -100.0, 100.0);
        attribute.setSyncable(true);
        return attribute;
    }

    public static void onAttach(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, MYSTIC_SPELL_POWER);
        event.add(EntityType.PLAYER, MYSTIC_MAGIC_RESIST);
    }
}

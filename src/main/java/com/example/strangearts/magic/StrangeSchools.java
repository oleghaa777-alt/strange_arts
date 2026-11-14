package com.example.strangearts.magic;

import com.example.strangearts.StrangeArts;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class StrangeSchools {
    /** Регистрируется в реестре школ самого Iron's (ключ SCHOOL_REGISTRY_KEY). */
    public static final DeferredRegister<SchoolType> SCHOOLS =
            DeferredRegister.create(SchoolRegistry.SCHOOL_REGISTRY_KEY, StrangeArts.MOD_ID);

    public static final ResourceLocation MYSTIC_ID = StrangeArts.id("mystic");

    /** Предметы, которые служат фокусом школы (data/strange_arts/tags/item/mystic_focus.json). */
    public static final TagKey<Item> MYSTIC_FOCUS = TagKey.create(Registries.ITEM, StrangeArts.id("mystic_focus"));

    /** Тип урона (data/strange_arts/damage_type/mystic_magic.json). */
    public static final ResourceKey<DamageType> MYSTIC_MAGIC =
            ResourceKey.create(Registries.DAMAGE_TYPE, StrangeArts.id("mystic_magic"));

    public static final Supplier<SchoolType> MYSTIC = SCHOOLS.register("mystic", () -> new SchoolType(
            MYSTIC_ID,
            MYSTIC_FOCUS,
            Component.translatable("school.strange_arts.mystic").withStyle(Style.EMPTY.withColor(0xFF9A1F)),
            StrangeAttributes.MYSTIC_SPELL_POWER,
            StrangeAttributes.MYSTIC_MAGIC_RESIST,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EVOKER_CAST_SPELL),
            MYSTIC_MAGIC
    ));
}

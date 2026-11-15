package com.example.strangearts.magic;

import com.example.strangearts.magic.spells.MysticShieldSpell;
import com.example.strangearts.StrangeArts;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class StrangeSpells {
    /** Регистрируется в реестре заклинаний самого Iron's (ключ SPELL_REGISTRY_KEY). */
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, StrangeArts.MOD_ID);

    public static final Supplier<AbstractSpell> MYSTIC_SHIELD = SPELLS.register("mystic_shield", MysticShieldSpell::new);
}

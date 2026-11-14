package com.example.strangearts;

import com.example.strangearts.item.ModItems;
import com.example.strangearts.item.ThunderAxeItem;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.world.entity.LivingEntity;

public class StrangeEvents {
    /**
     * Громовая секира: если заклинатель держит её в руке и бьёт электрическим заклинанием Iron's,
     * урон растёт на LIGHTNING_BONUS (по умолчанию 4).
     */
    public static void onSpellDamage(SpellDamageEvent event) {
        SpellDamageSource source = event.getSpellDamageSource();
        AbstractSpell spell = source.spell();
        if (spell == null) return;
        if (!SchoolRegistry.LIGHTNING_RESOURCE.equals(spell.getSchoolType().getId())) return;

        if (source.getEntity() instanceof LivingEntity caster && caster.isHolding(ModItems.THUNDER_AXE.get())) {
            event.setAmount(event.getAmount() + ThunderAxeItem.LIGHTNING_BONUS);
        }
    }
}

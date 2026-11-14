package com.example.strangearts.magic.spells;

import com.example.strangearts.StrangeArts;
import com.example.strangearts.magic.StrangeSchools;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * Щит Мистика (v1): даёт поглощение (жёлтые сердца) и рисует вокруг игрока кольцо искр.
 * Позже можно заменить на отдельную сущность-щит с кастомным рендером (как ShieldEntity у Iron's).
 */
public class MysticShieldSpell extends AbstractSpell {
    private final ResourceLocation spellId = StrangeArts.id("mystic_shield");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(StrangeSchools.MYSTIC_ID)
            .setMaxLevel(8)
            .setCooldownSeconds(12)
            .build();

    public MysticShieldSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 2;
        this.castTime = 0;
        this.baseManaCost = 40;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ILLUSIONER_CAST_SPELL);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.hp",
                Utils.stringTruncation(getShieldHP(spellLevel, caster), 1)));
    }

    public float getShieldHP(int spellLevel, LivingEntity caster) {
        return 4 + getSpellPower(spellLevel, caster) * 0.8f;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float hp = getShieldHP(spellLevel, entity);
        entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), hp));

        if (level instanceof ServerLevel serverLevel) {
            // кольцо искр на уровне груди, радиус ~1.2
            for (int i = 0; i < 36; i++) {
                double a = i / 36.0 * Math.PI * 2;
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        entity.getX() + Math.cos(a) * 1.2,
                        entity.getY() + 1.0 + Math.sin(i * 0.7) * 0.15,
                        entity.getZ() + Math.sin(a) * 1.2,
                        1, 0, 0, 0, 0.01);
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}

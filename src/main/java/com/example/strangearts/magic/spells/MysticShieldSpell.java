package com.example.strangearts.magic.spells;

import com.example.strangearts.StrangeArts;
import com.example.strangearts.entity.MysticShieldEntity;
import com.example.strangearts.magic.StrangeSchools;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * Щит Мистика: ставит перед заклинателем щит Стрэнджа (сущность MysticShieldEntity) и даёт поглощение.
 * Пока щит держится, он сбивает чужие снаряды; исчезает по времени или когда поглощение кончилось.
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
        if (!level.isClientSide) {
            // старый щит этого же заклинателя убираем, чтобы не копились
            for (MysticShieldEntity old : level.getEntitiesOfClass(MysticShieldEntity.class,
                    entity.getBoundingBox().inflate(4), shield -> shield.isOwnedBy(entity))) {
                old.discard();
            }
            entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), getShieldHP(spellLevel, entity)));
            level.addFreshEntity(new MysticShieldEntity(level, entity));
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}

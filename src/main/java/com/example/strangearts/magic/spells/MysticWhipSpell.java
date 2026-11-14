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
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Мистический хлыст (v1): луч в направлении взгляда. Наносит урон первой цели и подтягивает её к кастеру.
 * Визуал — линия искр. Позже можно заменить на сущность хлыста с анимацией и моделью.
 */
public class MysticWhipSpell extends AbstractSpell {
    private final ResourceLocation spellId = StrangeArts.id("mystic_whip");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(StrangeSchools.MYSTIC_ID)
            .setMaxLevel(8)
            .setCooldownSeconds(6)
            .build();

    public MysticWhipSpell() {
        this.manaCostPerLevel = 3;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 30;
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
        return Optional.of(SoundEvents.BLAZE_SHOOT);
    }

    public float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) * 0.9f;
    }

    public double getRange(int spellLevel) {
        return 10 + spellLevel;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage",
                Utils.stringTruncation(getDamage(spellLevel, caster), 1)));
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 eye = entity.getEyePosition();
        Vec3 end = eye.add(entity.getLookAngle().scale(getRange(spellLevel)));

        BlockHitResult blockHit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }

        AABB box = entity.getBoundingBox().expandTowards(end.subtract(eye)).inflate(1.0);
        double maxDistSq = eye.distanceToSqr(end);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(entity, eye, end, box,
                e -> e instanceof LivingEntity && !e.isSpectator() && e.isPickable(), maxDistSq);

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
            end = entityHit.getLocation();
            target.hurt(getDamageSource(entity), getDamage(spellLevel, entity));
            Vec3 pull = entity.position().subtract(target.position()).normalize().scale(0.9).add(0, 0.25, 0);
            target.setDeltaMovement(target.getDeltaMovement().add(pull));
            target.hurtMarked = true;
        }

        if (level instanceof ServerLevel serverLevel) {
            Vec3 start = eye.add(entity.getLookAngle().scale(0.6)).add(0, -0.3, 0);
            Vec3 dir = end.subtract(start);
            int steps = Math.max(1, (int) (dir.length() / 0.4));
            for (int i = 0; i <= steps; i++) {
                Vec3 p = start.add(dir.scale(i / (double) steps));
                serverLevel.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
                if (i % 3 == 0) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.02);
                }
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}

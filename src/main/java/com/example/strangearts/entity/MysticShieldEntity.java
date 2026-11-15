package com.example.strangearts.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Щит Стрэнджа: светящийся диск перед владельцем. Сервер каждый тик ставит его перед ним,
 * сбивает чужие снаряды и убирает щит, когда вышло время или кончилось поглощение.
 * Клиент рисует диск по положению владельца (см. MysticShieldRenderer), поэтому он не отстаёт при движении.
 */
public class MysticShieldEntity extends Entity {
    private static final EntityDataAccessor<Integer> OWNER_ID =
            SynchedEntityData.defineId(MysticShieldEntity.class, EntityDataSerializers.INT);

    /** Сколько тиков живёт щит (20 тиков = 1 секунда). */
    public static final int DURATION = 20 * 10;
    /** Расстояние от владельца до центра щита, в блоках. */
    public static final double DISTANCE = 1.1;
    /** Сколько поглощения забирает один сбитый снаряд. */
    private static final float PROJECTILE_COST = 2.0f;

    @Nullable
    private UUID ownerUuid;

    public MysticShieldEntity(net.minecraft.world.entity.EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public MysticShieldEntity(Level level, LivingEntity owner) {
        this(ModEntities.MYSTIC_SHIELD.get(), level);
        this.ownerUuid = owner.getUUID();
        this.entityData.set(OWNER_ID, owner.getId());
        follow(owner);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity entity = level().getEntity(this.entityData.get(OWNER_ID));
        return entity instanceof LivingEntity living ? living : null;
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return ownerUuid != null && ownerUuid.equals(entity.getUUID());
    }

    /** Горизонтальное направление «вперёд» для владельца (с учётом поворота кадра между тиками). */
    public static Vec3 forward(LivingEntity owner, float partialTick) {
        return Vec3.directionFromRotation(0, Mth.rotLerp(partialTick, owner.yRotO, owner.getYRot()));
    }

    /** Центр щита: перед владельцем на уровне груди. */
    public static Vec3 anchor(LivingEntity owner, float partialTick) {
        return owner.getPosition(partialTick)
                .add(forward(owner, partialTick).scale(DISTANCE))
                .add(0, owner.getBbHeight() * 0.55, 0);
    }

    private void follow(LivingEntity owner) {
        Vec3 center = anchor(owner, 1.0f);
        this.setPos(center.x, center.y - this.getBbHeight() / 2.0, center.z);
        this.setYRot(owner.getYRot());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        LivingEntity owner = getOwner();
        if (owner == null || !owner.isAlive() || tickCount > DURATION
                || (tickCount > 3 && owner.getAbsorptionAmount() <= 0)) {
            discard();
            return;
        }
        follow(owner);

        for (Entity entity : level().getEntities(this, getBoundingBox(),
                e -> e instanceof Projectile p && p.getOwner() != owner)) {
            if (level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.FLAME, entity.getX(), entity.getY(), entity.getZ(), 8, 0.1, 0.1, 0.1, 0.05);
            }
            entity.discard();
            owner.setAbsorptionAmount(Math.max(0, owner.getAbsorptionAmount() - PROJECTILE_COST));
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}

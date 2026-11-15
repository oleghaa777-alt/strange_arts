package com.example.strangearts.entity;

import com.example.strangearts.StrangeArts;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, StrangeArts.MOD_ID);

    public static final Supplier<EntityType<MysticShieldEntity>> MYSTIC_SHIELD = ENTITIES.register("mystic_shield",
            () -> EntityType.Builder.<MysticShieldEntity>of(MysticShieldEntity::new, MobCategory.MISC)
                    .sized(1.4f, 1.4f)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .noSave()
                    .fireImmune()
                    .build("mystic_shield"));
}

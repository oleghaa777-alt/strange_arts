package com.example.strangearts.client;

import com.example.strangearts.StrangeArts;
import com.example.strangearts.entity.MysticShieldEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

/** Рисует щит Стрэнджа: диск с рунами и кольцо искр, оба светятся (аддитивное смешивание, чёрное невидимо). */
public class MysticShieldRenderer extends EntityRenderer<MysticShieldEntity> {
    private static final ResourceLocation DISC_TEXTURE = StrangeArts.id("textures/entity/mystic_shield_disc.png");
    private static final ResourceLocation SPARKS_TEXTURE = StrangeArts.id("textures/entity/mystic_shield_sparks.png");

    /** Размер щита: исходный диск с кольцом имеет диаметр около 1 блока. */
    private static final float SCALE = 1.4f;

    public MysticShieldRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(MysticShieldEntity entity) {
        return DISC_TEXTURE;
    }

    @Override
    public void render(MysticShieldEntity shield, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        float time = shield.tickCount + partialTick;
        LivingEntity owner = shield.getOwner();

        poseStack.pushPose();
        Vec3 forward;
        if (owner != null) {
            Vec3 anchor = MysticShieldEntity.anchor(owner, partialTick);
            Vec3 self = shield.getPosition(partialTick);
            poseStack.translate(anchor.x - self.x, anchor.y - self.y, anchor.z - self.z);
            forward = MysticShieldEntity.forward(owner, partialTick);
        } else {
            poseStack.translate(0, shield.getBbHeight() / 2.0, 0);
            forward = new Vec3(0, 0, 1);
        }

        // Диск в модели лежит в плоскости XZ, его нормаль — ось Y. Ставим его вертикально лицом вперёд:
        // ось X -> вправо, ось Y -> вперёд, ось Z -> вверх.
        Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 up = right.cross(forward);
        Matrix3f basis = new Matrix3f(
                (float) right.x, (float) right.y, (float) right.z,
                (float) forward.x, (float) forward.y, (float) forward.z,
                (float) up.x, (float) up.y, (float) up.z);
        poseStack.mulPose(new Quaternionf().setFromNormalized(basis));
        poseStack.scale(SCALE, SCALE, SCALE);

        // плавно появляется и гаснет
        float fade = Mth.clamp(Math.min(time / 5.0f, (MysticShieldEntity.DURATION - time) / 10.0f), 0.0f, 1.0f);
        int c = (int) (255 * fade);
        int light = LightTexture.FULL_BRIGHT;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 2.0f));
        MeshModel.get("shield_disc").render(poseStack, buffer.getBuffer(RenderType.eyes(DISC_TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, c, c, c, 255, true);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-time * 5.0f));
        MeshModel.get("shield_sparks").render(poseStack, buffer.getBuffer(RenderType.eyes(SPARKS_TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, c, c, c, 255, true);
        poseStack.popPose();

        poseStack.popPose();
    }
}

package com.example.strangearts.client;

import com.example.strangearts.StrangeArts;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Рисует модель Штормбрейкера вместо плоской иконки предмета. Положение в руке задаёт models/item/thunder_axe.json. */
public class StormbreakerRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation HANDLE_TEXTURE = StrangeArts.id("textures/entity/stormbreaker_handle.png");
    private static final ResourceLocation HEAD_TEXTURE = StrangeArts.id("textures/entity/stormbreaker_head.png");

    public StormbreakerRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        // начало модели (место хвата рукояти) ставим в центр ячейки предмета
        poseStack.translate(0.5, 0.5, 0.5);
        MeshModel.get("stormbreaker_handle").render(poseStack,
                buffer.getBuffer(RenderType.entityCutoutNoCull(HANDLE_TEXTURE)),
                packedLight, packedOverlay, 255, 255, 255, 255, false);
        MeshModel.get("stormbreaker_head").render(poseStack,
                buffer.getBuffer(RenderType.entityCutoutNoCull(HEAD_TEXTURE)),
                packedLight, packedOverlay, 255, 255, 255, 255, false);
        poseStack.popPose();
    }
}

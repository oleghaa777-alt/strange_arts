package com.example.strangearts.client;

import com.example.strangearts.StrangeArts;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Простая 3D-модель из треугольников (позиции, нормали, UV), которую мы получили из .glb.
 * Файлы лежат в assets/strange_arts/models/mesh/*.json. Рисуется напрямую в буфер вершин.
 */
public final class MeshModel {
    private static final Map<ResourceLocation, MeshModel> CACHE = new HashMap<>();

    private final float[] pos;
    private final float[] nrm;
    private final float[] uv;
    private final int[] idx;

    private MeshModel(float[] pos, float[] nrm, float[] uv, int[] idx) {
        this.pos = pos;
        this.nrm = nrm;
        this.uv = uv;
        this.idx = idx;
    }

    /** name — имя файла без расширения, например "shield_disc". */
    public static MeshModel get(String name) {
        ResourceLocation id = StrangeArts.id("models/mesh/" + name + ".json");
        MeshModel model = CACHE.get(id);
        if (model == null) {
            model = load(id);
            CACHE.put(id, model);
        }
        return model;
    }

    private static MeshModel load(ResourceLocation id) {
        try (BufferedReader reader = Minecraft.getInstance().getResourceManager().getResourceOrThrow(id).openAsReader()) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            return new MeshModel(floats(json.getAsJsonArray("p")), floats(json.getAsJsonArray("n")),
                    floats(json.getAsJsonArray("uv")), ints(json.getAsJsonArray("i")));
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать модель " + id, e);
        }
    }

    private static float[] floats(JsonArray array) {
        float[] result = new float[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = array.get(i).getAsFloat();
        }
        return result;
    }

    private static int[] ints(JsonArray array) {
        int[] result = new int[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = array.get(i).getAsInt();
        }
        return result;
    }

    /**
     * Рисует модель. Тип рендера у Minecraft работает с четырёхугольниками,
     * поэтому каждый треугольник отправляется как четырёхугольник с повторенной последней вершиной.
     *
     * @param doubleSided рисовать и обратную сторону (нужно для плоских дисков)
     */
    public void render(PoseStack stack, VertexConsumer consumer, int light, int overlay,
                       int red, int green, int blue, int alpha, boolean doubleSided) {
        PoseStack.Pose pose = stack.last();
        for (int t = 0; t + 2 < idx.length; t += 3) {
            int a = idx[t];
            int b = idx[t + 1];
            int c = idx[t + 2];
            vertex(pose, consumer, a, false, light, overlay, red, green, blue, alpha);
            vertex(pose, consumer, b, false, light, overlay, red, green, blue, alpha);
            vertex(pose, consumer, c, false, light, overlay, red, green, blue, alpha);
            vertex(pose, consumer, c, false, light, overlay, red, green, blue, alpha);
            if (doubleSided) {
                vertex(pose, consumer, a, true, light, overlay, red, green, blue, alpha);
                vertex(pose, consumer, c, true, light, overlay, red, green, blue, alpha);
                vertex(pose, consumer, b, true, light, overlay, red, green, blue, alpha);
                vertex(pose, consumer, b, true, light, overlay, red, green, blue, alpha);
            }
        }
    }

    private void vertex(PoseStack.Pose pose, VertexConsumer consumer, int i, boolean flip,
                        int light, int overlay, int red, int green, int blue, int alpha) {
        float nx = nrm[i * 3];
        float ny = nrm[i * 3 + 1];
        float nz = nrm[i * 3 + 2];
        if (flip) {
            nx = -nx;
            ny = -ny;
            nz = -nz;
        }
        consumer.addVertex(pose, pos[i * 3], pos[i * 3 + 1], pos[i * 3 + 2])
                .setColor(red, green, blue, alpha)
                .setUv(uv[i * 2], uv[i * 2 + 1])
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}

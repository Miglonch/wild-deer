package com.Miglon.client;

import com.Miglon.DeerMod;
import com.Miglon.Entities.DeerEntity;
import com.Miglon.Entities.custom.DeerVariant;
import com.google.common.collect.Maps;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Map;

public class DeerRenderer extends MobEntityRenderer<DeerEntity, deerModel<DeerEntity>> {

    public static final Map<DeerVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DeerVariant.class), (map) -> {
                map.put(DeerVariant.DEFAULT,
                        new Identifier(DeerMod.MODID, "textures/entity/deer/default.png"));
                map.put(DeerVariant.NORTH,
                        new Identifier(DeerMod.MODID, "textures/entity/deer/north.png"));
                map.put(DeerVariant.CHERRY,
                        new Identifier(DeerMod.MODID, "textures/entity/deer/cherry.png"));
            });

    public DeerRenderer(EntityRendererFactory.Context context) {
        super(context, new deerModel<>(context.getPart(ModModelLayers.DEER)), 0.6F);
    }

    @Override
    public Identifier getTexture(DeerEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    public void render(DeerEntity mobEntity, float f, float q, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(mobEntity, f, q, matrixStack, vertexConsumerProvider, i);
    }
}

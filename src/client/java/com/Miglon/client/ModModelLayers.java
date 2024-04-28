package com.Miglon.client;

import com.Miglon.DeerMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer DEER =
            new EntityModelLayer(new Identifier(DeerMod.MODID, "deer"), "main");
}

// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.Miglon.client;

import com.Miglon.Entities.DeerEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class deerModel<T extends DeerEntity> extends SinglePartEntityModel<T> {
	private final ModelPart deer;
	private final ModelPart head;
	public deerModel(ModelPart root) {
		this.deer = root.getChild("deer");
		this.head = deer.getChild("head");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData deer = modelPartData.addChild("deer", ModelPartBuilder.create(), ModelTransform.of(4.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData neck_r1 = deer.addChild("neck_r1", ModelPartBuilder.create().uv(52, 0).cuboid(-3.5F, -20.5F, 2.5F, 3.0F, 8.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		ModelPartData left_ear_r1 = deer.addChild("left_ear_r1", ModelPartBuilder.create().uv(0, 15).cuboid(-8.0F, -20.0F, 11.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.48F, 0.0F, 0.0F));

		ModelPartData right_ear_r1 = deer.addChild("right_ear_r1", ModelPartBuilder.create().uv(5, 5).cuboid(-8.0F, -8.5F, 19.75F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.9599F, 0.0F, 0.0F));

		ModelPartData head = deer.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-11.5F, -21.75F, 1.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F))
		.uv(0, 10).cuboid(-13.5F, -18.75F, 2.5F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_front_leg = deer.addChild("left_front_leg", ModelPartBuilder.create().uv(20, 0).cuboid(-8.0F, -9.0F, 0.0F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_back_leg = deer.addChild("right_back_leg", ModelPartBuilder.create().uv(28, 0).cuboid(6.0F, -9.0F, 6.0F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_back_leg = deer.addChild("left_back_leg", ModelPartBuilder.create().uv(36, 0).cuboid(6.0F, -9.0F, 0.0F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_front_leg = deer.addChild("right_front_leg", ModelPartBuilder.create().uv(44, 0).cuboid(-8.0F, -9.0F, 6.0F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData torso = deer.addChild("torso", ModelPartBuilder.create().uv(7, 11).cuboid(-8.0F, -14.0F, 0.0F, 16.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData horn = deer.addChild("horn", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData little_horn = horn.addChild("little_horn", ModelPartBuilder.create().uv(23, 24).cuboid(-11.0F, -23.7F, 6.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
		.uv(27, 24).cuboid(-11.0F, -23.7F, 2.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData middle_horn = horn.addChild("middle_horn", ModelPartBuilder.create().uv(15, 24).cuboid(-11.0F, -24.7F, 6.0F, 2.0F, 3.0F, 0.0F, new Dilation(0.0F))
		.uv(19, 24).cuboid(-11.0F, -24.7F, 2.0F, 2.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData mature_horn = horn.addChild("mature_horn", ModelPartBuilder.create().uv(7, 24).cuboid(-11.0F, -25.7F, 6.0F, 2.0F, 4.0F, 0.0F, new Dilation(0.0F))
		.uv(11, 24).cuboid(-11.0F, -25.7F, 2.0F, 2.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(DeerEntity entity, float f, float g, float h, float i, float j) {
		switch (entity.getHornType()){
			case 0:
				this.deer.getChild("horn").getChild("little_horn").visible = false;
				this.deer.getChild("horn").getChild("middle_horn").visible = false;
				this.deer.getChild("horn").getChild("mature_horn").visible = false;
				break;
			case 1:
				this.deer.getChild("horn").getChild("little_horn").visible = true;
				this.deer.getChild("horn").getChild("middle_horn").visible = false;
				this.deer.getChild("horn").getChild("mature_horn").visible = false;
				break;
			case 2:
				this.deer.getChild("horn").getChild("middle_horn").visible = true;
				this.deer.getChild("horn").getChild("little_horn").visible = false;
				this.deer.getChild("horn").getChild("mature_horn").visible = false;
				break;
			case 3:
				this.deer.getChild("horn").getChild("mature_horn").visible = true;
				this.deer.getChild("horn").getChild("middle_horn").visible = false;
				this.deer.getChild("horn").getChild("little_horn").visible = false;
				break;
		}
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		deer.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return deer;
	}
}
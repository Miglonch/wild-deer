package com.Miglon.client;

import com.Miglon.Entities.DeerEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class deerModel<T extends DeerEntity> extends SinglePartEntityModel<T> {
	private final ModelPart deer;
	public deerModel(ModelPart root) {
		this.deer = root.getChild("deer");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData deer = modelPartData.addChild("deer", ModelPartBuilder.create().uv(41, 14).cuboid(-2.0F, -26.0F, -15.0F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData right_ear = deer.addChild("right_ear", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData ear_r1 = right_ear.addChild("ear_r1", ModelPartBuilder.create().uv(10, 0).cuboid(-10.0F, -27.0F, -9.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 1.75F, 0.0182F, 0.0504F, 0.212F));

		ModelPartData left_ear = deer.addChild("left_ear", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData ear_r2 = left_ear.addChild("ear_r2", ModelPartBuilder.create().uv(10, 0).mirrored().cuboid(7.5F, -27.25F, -9.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 1.75F, 0.0152F, -0.0095F, -0.1762F));

		ModelPartData tail = deer.addChild("tail", ModelPartBuilder.create().uv(12, 14).cuboid(-1.0F, -22.0F, 10.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_back_leg = deer.addChild("right_back_leg", ModelPartBuilder.create().uv(24, 31).cuboid(-4.9F, -12.0F, 8.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_back_leg = deer.addChild("left_back_leg", ModelPartBuilder.create().uv(0, 0).cuboid(1.9F, -12.0F, 8.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_front_leg = deer.addChild("left_front_leg", ModelPartBuilder.create().uv(38, 31).cuboid(1.7F, -10.0F, -9.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_front_leg = deer.addChild("right_front_leg", ModelPartBuilder.create().uv(41, 0).cuboid(-4.9F, -10.0F, -9.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData head = deer.addChild("head", ModelPartBuilder.create().uv(0, 31).cuboid(-3.0F, -28.0F, -12.0F, 6.0F, 16.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData antlers = deer.addChild("antlers", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

		ModelPartData small_antlers = antlers.addChild("small_antlers", ModelPartBuilder.create().uv(56, 2).cuboid(-3.0F, -29.0F, -10.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(56, 2).mirrored().cuboid(1.0F, -29.0F, -10.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData middle_antlers = antlers.addChild("middle_antlers", ModelPartBuilder.create().uv(55, 10).cuboid(-2.0F, -29.0F, -10.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(55, 10).mirrored().cuboid(2.0F, -29.0F, -10.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
		.uv(0, 54).cuboid(-5.0F, -34.0F, -12.0F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F))
		.uv(0, 54).mirrored().cuboid(2.0F, -34.0F, -12.0F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-1.0F, 0.0F, 0.0F));

		ModelPartData mature_antlers = antlers.addChild("mature_antlers", ModelPartBuilder.create().uv(34, 49).cuboid(-8.0F, -37.0F, -12.0F, 7.0F, 7.0F, 8.0F, new Dilation(0.0F))
		.uv(34, 49).mirrored().cuboid(1.0F, -37.0F, -12.0F, 7.0F, 7.0F, 8.0F, new Dilation(0.0F)).mirrored(false)
		.uv(55, 16).cuboid(-3.0F, -30.0F, -10.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
		.uv(55, 16).mirrored().cuboid(1.0F, -30.0F, -10.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData body = deer.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -20.0F, -10.0F, 10.0F, 10.0F, 21.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(DeerEntity entity, float f, float g, float h, float i, float j) {
		switch (entity.getAntlersType()){
			case 0:
				this.deer.getChild("antlers").getChild("small_antlers").visible = false;
				this.deer.getChild("antlers").getChild("middle_antlers").visible = false;
				this.deer.getChild("antlers").getChild("mature_antlers").visible = false;
				break;
			case 1:
				this.deer.getChild("antlers").getChild("small_antlers").visible = true;
				this.deer.getChild("antlers").getChild("middle_antlers").visible = false;
				this.deer.getChild("antlers").getChild("mature_antlers").visible = false;
				break;
			case 2:
				this.deer.getChild("antlers").getChild("middle_antlers").visible = true;
				this.deer.getChild("antlers").getChild("small_antlers").visible = false;
				this.deer.getChild("antlers").getChild("mature_antlers").visible = false;
				break;
			case 3:
				this.deer.getChild("antlers").getChild("mature_antlers").visible = true;
				this.deer.getChild("antlers").getChild("middle_antlers").visible = false;
				this.deer.getChild("antlers").getChild("small_antlers").visible = false;
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
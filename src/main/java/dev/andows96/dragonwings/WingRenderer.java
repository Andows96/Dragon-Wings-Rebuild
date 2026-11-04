package dev.andows96.dragonwings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = DragonWings.MOD_ID, value = Dist.CLIENT)
public class WingRenderer {
    private static WingRenderer instance;

    private final ModelPart wing;
    private final ModelPart wingTip;

    private WingRenderer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition wingPart = partdefinition.addOrReplaceChild("wing", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-10.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F)
                .texOffs(-10, 8)
                .addBox(-10.0F, 0.0F, 0.5F, 10.0F, 0.0F, 10.0F),
            PartPose.offset(-2.0F, 0.0F, 0.0F)
        );

        wingPart.addOrReplaceChild("wingtip", CubeListBuilder.create()
                .texOffs(0, 5)
                .addBox(-10.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F)
                .texOffs(-10, 18)
                .addBox(-10.0F, 0.0F, 0.5F, 10.0F, 0.0F, 10.0F),
            PartPose.offset(-10.0F, 0.0F, 0.0F)
        );

        ModelPart root = partdefinition.bake(30, 30);
        this.wing = root.getChild("wing");
        this.wingTip = wing.getChild("wingtip");
    }

    public static WingRenderer getInstance() {
        if (instance == null) {
            instance = new WingRenderer();
        }
        return instance;
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post<?> event) {
        AvatarRenderState state = event.getRenderState();
        Player player = resolvePlayer(state);
        WingType wingType = WingsRenderUtils.getWingTypeToRender(state, player);

        if (wingType != null) {
            renderWings(state, event.getPoseStack(), event.getSubmitNodeCollector(), wingType);
        }
    }

    /**
     * 26.x no longer passes the entity to the render event, only a render state snapshot. The entity
     * is still reachable by id, which is only needed for the {@code flying} ability flag.
     */
    private static Player resolvePlayer(AvatarRenderState state) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        return mc.level.getEntity(state.id) instanceof Player player ? player : null;
    }

    public static void renderWings(AvatarRenderState state, PoseStack poseStack, SubmitNodeCollector collector, WingType wingType) {
        WingRenderer renderer = getInstance();

        int scale = Config.getWingsScale(wingType);

        WingsRenderUtils.applyWingTransforms(poseStack, state.bodyRot, state.isCrouching, scale);

        Identifier texture = Identifier.fromNamespaceAndPath(DragonWings.MOD_ID, wingType.getTexturePath());
        RenderType renderType = RenderTypes.entityCutout(texture);

        renderer.updateWingAnimation();

        for (int j = 0; j < 2; ++j) {
            collector.submitModelPart(renderer.wing, poseStack, renderType, state.lightCoords,
                OverlayTexture.NO_OVERLAY, (UvMapping) null);

            if (j == 0) {
                poseStack.scale(-1.0F, 1.0F, 1.0F);
            }
        }

        WingsRenderUtils.restoreWingTransforms(poseStack);
    }

    private void updateWingAnimation() {
        float f11 = (System.currentTimeMillis() % 1000) / 1000F * (float) Math.PI * 2.0F;
        this.wing.xRot = (float) Math.toRadians(-80F) - (float) Math.cos((double) f11) * 0.2F;
        this.wing.yRot = (float) Math.toRadians(20F) + (float) Math.sin(f11) * 0.4F;
        this.wing.zRot = (float) Math.toRadians(20F);
        this.wingTip.zRot = -((float) (Math.sin((double) (f11 + 2.0F)) + 0.5D)) * 0.75F;
    }
}

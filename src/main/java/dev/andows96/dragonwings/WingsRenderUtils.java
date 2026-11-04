package dev.andows96.dragonwings;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;

/**
 * Same maths as upstream. Two inputs changed shape in 26.x: the player entity is no longer handed to
 * the render event, so body rotation and the crouch flag are read from {@link AvatarRenderState}
 * (they are the interpolated versions of {@code yBodyRot} / {@code isCrouching}).
 */
public class WingsRenderUtils {

    public static boolean shouldRenderWings(Player player, boolean enabled, boolean flyingExpand) {
        if (enabled) {
            return true;
        } else {
            // Abilities are not part of the render state, so a player we cannot resolve on the client
            // falls back to the "enabled" branch only - same effective behaviour as upstream, where
            // remote players never had synced ability data either.
            return player != null && player.getAbilities().flying && flyingExpand;
        }
    }

    public static void applyWingTransforms(PoseStack poseStack, float bodyRot, boolean crouching, int scale) {
        double scaleValue = scale / 100D;

        poseStack.pushPose();
        poseStack.scale((float) -scaleValue, (float) -scaleValue, (float) scaleValue);

        // 26.x dropped mulPose(Quaternionf); rotateDegrees is the direct equivalent.
        poseStack.rotateDegrees(Axis.YP, 180 + bodyRot);

        poseStack.translate(0, -1.25 / scaleValue, 0);
        poseStack.translate(0, 0, 0.2 / scaleValue);

        if (crouching) {
            poseStack.translate(0D, 0.125D / scaleValue, 0D);
        }
    }

    public static void restoreWingTransforms(PoseStack poseStack) {
        poseStack.popPose();
    }

    public static boolean isLocalPlayer(Player player) {
        Minecraft mc = Minecraft.getInstance();
        return player != null && player.equals(mc.player);
    }

    public static WingType getWingTypeToRender(AvatarRenderState state, Player player) {
        if (state.isInvisible) {
            return null;
        }

        boolean isLocalPlayer = isLocalPlayer(player);

        for (WingType wingType : WingType.values()) {
            boolean shouldRender;
            if (isLocalPlayer) {
                shouldRender =
                    shouldRenderWings(player, Config.isWingsEnabled(wingType), Config.isWingsFlyingExpand(wingType));
            } else {
                shouldRender = Config.showOtherPlayersWings(wingType) &&
                    shouldRenderWings(player, Config.isWingsEnabled(wingType), Config.isWingsFlyingExpand(wingType));
            }

            if (shouldRender) {
                return wingType;
            }
        }
        return null;
    }
}

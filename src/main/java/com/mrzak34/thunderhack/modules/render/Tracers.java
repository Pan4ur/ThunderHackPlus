package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IEntityRenderer;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.mrzak34.thunderhack.util.EntityUtil.interpolateEntity;

public class Tracers extends Module {
    private final Setting<Boolean> showFriends = this.register(new Setting<>("ShowFriends", true));
    private final Setting<ColorSetting> colorSetting = this.register(new Setting<>("Color", new ColorSetting(0xFFFFFFFF)));
    private final Setting<ColorSetting> fcolorSetting = this.register(new Setting<>("FriendColor", new ColorSetting(0xFFFFFFFF)));
    private final Setting<Float> width = this.register(new Setting<>("Width", 2f, 0.1f, 5f));
    private final Setting<Float> tracerRange = this.register(new Setting<>("Range", 128f, 32f, 256f));
    public Tracers() {
        super("Tracers", "ебучая паутина-на экране", "tracers", Category.RENDER);
    }

    public static void renderTracer(double x, double y, double z, double x2, double y2, double z2, int color) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, ((color) & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
        GlStateManager.disableLighting();
        GL11.glLoadIdentity();
        ((IEntityRenderer) mc.entityRenderer).orientCam(mc.getRenderPartialTicks());
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glEnd();
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (fullNullCheck()) return;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        for (Entity e : mc.world.loadedEntityList) {
            if (e instanceof EntityPlayer && e != mc.player) {
                if (mc.player.getDistance(e) <= tracerRange.getValue()) {
                    final Vec3d pos = interpolateEntity(e, event.getPartialTicks());
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.glLineWidth(width.getValue());
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.disableLighting();
                    GlStateManager.disableCull();
                    GlStateManager.enableAlpha();
                    GlStateManager.color(1, 1, 1);
                    final boolean bobbing = mc.gameSettings.viewBobbing;
                    mc.gameSettings.viewBobbing = false;
                    Color color;
                    color = (Thunderhack.friendManager.isFriend(e.getName()) && showFriends.getValue()) ? (fcolorSetting.getValue().getColorObject()) : (colorSetting.getValue().getColorObject());
                    Vec3d eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(mc.player.rotationPitch)).rotateYaw(-(float) Math.toRadians(mc.player.rotationYaw));
                    renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, pos.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), pos.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), pos.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), color.getRGB());
                    mc.gameSettings.viewBobbing = bobbing;
                    GlStateManager.disableBlend();

                }
            }
        }
        GL11.glPopAttrib();
    }
}

package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.EventPostSync;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mrzak34.thunderhack.util.render.DrawHelper.injectAlpha;
import static org.lwjgl.opengl.GL11.*;

public class BreadCrumbs extends Module {
    public BreadCrumbs() {
        super("BreadCrumbs", "оставляет линию-при ходьбе", "BreadCrumbs", Category.RENDER);
    }

    private final Setting<Integer> limit = this.register(new Setting<>("ListLimit", 1000, 10, 99999));
    private final Setting<ColorSetting> color = register(new Setting<>("Color", new ColorSetting(3649978)));
    private final List<Vec3d> positions = new CopyOnWriteArrayList<>();



    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        mc.entityRenderer.disableLightmap();

        GL11.glLineWidth(2);
        glBegin(GL_LINE_STRIP);
        for (Vec3d pos : positions) {
            RenderUtil.glColor(color.getValue().getColor());
            glVertex3d(
                    pos.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(),
                    pos.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(),
                    pos.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ());
        }
        glEnd();

        GL11.glLineWidth(5);
        glBegin(GL_LINE_STRIP);
        for (Vec3d pos : positions) {
            RenderUtil.glColor(injectAlpha(color.getValue().getColorObject(),80));
            glVertex3d(
                    pos.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(),
                    pos.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(),
                    pos.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ());
        }
        glEnd();

        GL11.glLineWidth(10);
        glBegin(GL_LINE_STRIP);
        for (Vec3d pos : positions) {
            RenderUtil.glColor(injectAlpha(color.getValue().getColorObject(),50));
            glVertex3d(
                    pos.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(),
                    pos.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(),
                    pos.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ());
        }
        glEnd();

        GlStateManager.resetColor();
        glPopMatrix();
        glPopAttrib();
    }

    @SubscribeEvent
    public void postSync(EventPostSync event) {
        if(positions.size() > limit.getValue()){
            positions.remove(0);
        }
        positions.add(new Vec3d(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ));
    }

    @Override
    public void onDisable() {
        positions.clear();
    }
}

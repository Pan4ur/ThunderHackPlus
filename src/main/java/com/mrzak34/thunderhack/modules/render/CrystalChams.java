package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.CrystalRenderEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class CrystalChams extends Module {
    public CrystalChams() {
        super("CrystalChams", "CrystalChams", Category.MISC);
    }


    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(3649978)));
    public final Setting<ColorSetting> wireFrameColor = this.register(new Setting<>("WireframeColor", new ColorSetting(3649978)));

    public Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", 1.0f, 0.1f, 6.0f));

    private Setting<ChamsMode> mode = register(new Setting("Mode", ChamsMode.Normal));

    public enum ChamsMode {
        Normal,
        Gradient
    }

    public Setting<Boolean> chams = this.register(new Setting<Boolean>("Chams", true));
    public Setting<Boolean> throughWalls = this.register(new Setting<Boolean>("ThroughWalls", true));
    public Setting<Boolean> wireframe = this.register(new Setting<Boolean>("Wireframe", true));
    public Setting<Boolean> wireWalls = this.register(new Setting<Boolean>("WireThroughWalls", true));
    public Setting<Boolean> texture = this.register(new Setting<Boolean>("Texture", false));





    @SubscribeEvent
    public void onRenderCrystal(CrystalRenderEvent.Pre e){
            if (!texture.getValue()) {
                e.setCanceled(true);
            }

            if (mode.getValue() == ChamsMode.Gradient) {
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glEnable(GL_BLEND);
                glDisable(GL_LIGHTING);
                glDisable(GL_TEXTURE_2D);
                float alpha = color.getValue().getAlpha() / 255.0f;
                glColor4f(1.0f, 1.0f, 1.0f, alpha);
                e.getModel().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), e.getAgeInTicks(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScale());
                glEnable(GL_TEXTURE_2D);

                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                float f = (float) e.getEntity().ticksExisted + mc.getRenderPartialTicks();
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/rainbow.png"));
                mc.entityRenderer.setupFogColor(true);
                GlStateManager.enableBlend();
                GlStateManager.depthFunc(514);
                GlStateManager.depthMask(false);
                GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);

                for (int i = 0; i < 2; ++i)
                {
                    GlStateManager.disableLighting();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.loadIdentity();
                    GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
                    GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 0.5F);
                    GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                    GlStateManager.matrixMode(5888);
                    e.getModel().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), e.getAgeInTicks(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScale());
                }

                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.depthFunc(515);
                GlStateManager.disableBlend();
                mc.entityRenderer.setupFogColor(false);
                glPopAttrib();
            } else {
                if (wireframe.getValue()) {
                    Color wireColor = wireFrameColor.getValue().getColorObject();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glEnable(GL_BLEND);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glLineWidth(lineWidth.getValue());
                    if (wireWalls.getValue()) {
                        glDepthMask(false);
                        glDisable(GL_DEPTH_TEST);
                    }

                    glColor4f(wireColor.getRed() / 255.0f,
                            wireColor.getGreen() / 255.0f,
                            wireColor.getBlue() / 255.0f,
                            wireColor.getAlpha() / 255.0f);
                    e.getModel().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(),
                            e.getAgeInTicks(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScale());
                    glPopAttrib();
                }

                if (chams.getValue()) {
                    Color chamsColor = color.getValue().getColorObject();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glEnable(GL_BLEND);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glDisable(GL_ALPHA_TEST);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glEnable(GL_STENCIL_TEST);
                    glEnable(GL_POLYGON_OFFSET_LINE);
                    if (throughWalls.getValue()) {
                        glDepthMask(false);
                        glDisable(GL_DEPTH_TEST);
                    }
                    glColor4f(chamsColor.getRed() / 255.0f,
                            chamsColor.getGreen() / 255.0f,
                            chamsColor.getBlue() / 255.0f,
                            chamsColor.getAlpha() / 255.0f);
                    e.getModel().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(),
                            e.getAgeInTicks(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScale());
                    glPopAttrib();
                }
            }
    }





}

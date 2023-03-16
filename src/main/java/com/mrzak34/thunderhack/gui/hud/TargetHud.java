package com.mrzak34.thunderhack.gui.hud;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.modules.combat.AutoExplosion;
import com.mrzak34.thunderhack.modules.funnygame.C4Aura;
import com.mrzak34.thunderhack.modules.misc.NameProtect;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.render.Stencil;
import com.mrzak34.thunderhack.util.shaders.BetterAnimation;
import com.mrzak34.thunderhack.util.shaders.BetterDynamicAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class TargetHud extends Module {

    private static final ResourceLocation thudPic = new ResourceLocation("textures/thud.png");
    public static BetterDynamicAnimation healthanimation = new BetterDynamicAnimation();
    public static BetterDynamicAnimation ebaloAnimation = new BetterDynamicAnimation();
    static ResourceLocation customImg;
    private final java.util.ArrayList<Particles> particles = new java.util.ArrayList<>();
    private final Timer timer = new Timer();
    public BetterAnimation animation = new BetterAnimation();
    int xPos = 20;
    int yPos = 20;
    int dragX, dragY = 0;
    boolean mousestate = false;
    float ticks;
    private final Setting<ColorSetting> color = this.register(new Setting<>("Color1", new ColorSetting(-16492289)));
    private final Setting<ColorSetting> color2 = this.register(new Setting<>("Color2", new ColorSetting(-2353224)));
    private final Setting<Integer> slices = this.register(new Setting<>("colorOffset1", 135, 10, 500));
    private final Setting<Integer> slices1 = this.register(new Setting<>("colorOffset2", 211, 10, 500));
    private final Setting<Integer> slices2 = this.register(new Setting<>("colorOffset3", 162, 10, 500));
    private final Setting<Integer> slices3 = this.register(new Setting<>("colorOffset4", 60, 10, 500));
    private final Setting<Integer> pcount = this.register(new Setting<>("ParticleCount", 20, 0, 50));
    private final Setting<Float> psize = this.register(new Setting<>("ParticleSize", 4f, 0.1f, 15f));
    private final Setting<Integer> blurRadius = this.register(new Setting<>("BallonBlur", 10, 1f, 10));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));
    private final Setting<Integer> animX = this.register(new Setting<>("AnimationX", 0, -2000, 2000));
    private final Setting<Integer> animY = this.register(new Setting<>("AnimationY", 0, -2000, 2000));
    private final Setting<HPmodeEn> hpMode = register(new Setting<>("HP Mode", HPmodeEn.HP));
    private final Setting<ImageModeEn> imageMode = register(new Setting<>("Image", ImageModeEn.Anime));
    private boolean sentParticles;
    private boolean direction = false;
    private EntityPlayer target;

    public TargetHud() {
        super("TargetHud", "ПИЗДАТЕЙШИЙ", Category.HUD);
    }

    public static void renderTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(thudPic);
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void sizeAnimation(double width, double height, double animation) {
        GL11.glTranslated(width, height, 0);
        GL11.glScaled(animation, animation, 1);
        GL11.glTranslated(-width, -height, 0);
    }

    public static String getPotionName(Potion potion) {
        if (potion == MobEffects.REGENERATION) {
            return "Reg";
        } else if (potion == MobEffects.STRENGTH) {
            return "Str";
        } else if (potion == MobEffects.SPEED) {
            return "Spd";
        } else if (potion == MobEffects.HASTE) {
            return "H";
        } else if (potion == MobEffects.WEAKNESS) {
            return "W";
        } else if (potion == MobEffects.RESISTANCE) {
            return "Res";
        }
        return "pon";
    }

    public static String getDurationString(PotionEffect pe) {
        if (pe.getIsPotionDurationMax()) {
            return "*:*";
        } else {
            int var1 = pe.getDuration();
            return StringUtils.ticksToElapsedTime(var1);
        }
    }

    public static void fastRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius) {
        float z = 0;
        if (paramXStart > paramXEnd) {
            z = paramXStart;
            paramXStart = paramXEnd;
            paramXEnd = z;
        }

        if (paramYStart > paramYEnd) {
            z = paramYStart;
            paramYStart = paramYEnd;
            paramYEnd = z;
        }

        double x1 = (paramXStart + radius);
        double y1 = (paramYStart + radius);
        double x2 = (paramXEnd - radius);
        double y2 = (paramYEnd - radius);

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
        glBegin(GL_POLYGON);
        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        for (double i = 90; i <= 180; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 180; i <= 270; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 270; i <= 360; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        glEnd();
        glDisable(GL_LINE_SMOOTH);
    }

    public static Color TwoColoreffect(Color cl1, Color cl2, double speed) {
        double thing = speed / 4.0 % 1.0;
        float val = MathHelper.clamp((float) Math.sin(Math.PI * 6 * thing) / 2.0f + 0.5f, 0.0f, 1.0f);
        return new Color(lerp((float) cl1.getRed() / 255.0f, (float) cl2.getRed() / 255.0f, val), lerp((float) cl1.getGreen() / 255.0f, (float) cl2.getGreen() / 255.0f, val), lerp((float) cl1.getBlue() / 255.0f, (float) cl2.getBlue() / 255.0f, val));
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static void renderPlayerModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void onUpdate() {
        animation.update(direction);
        healthanimation.update();
        ebaloAnimation.update();
    }

    //  сила скорка спешка викнес регенерация сопротивление
    //  Str 1:23 Spd2 1:23 H3 1:23 Reg4 1:23 Res5 1:23

    public void renderTHud(Render2DEvent e) {
        //таргеты
        if (Aura.target != null) {
            if (Aura.target instanceof EntityPlayer) {
                target = (EntityPlayer) Aura.target;
                direction = true;
            } else {
                target = null;
                direction = false;
            }
        } else if (C4Aura.target != null) {
            target = C4Aura.target;
            direction = true;
        } else if (AutoExplosion.trgt != null) {
            target = AutoExplosion.trgt;
            direction = true;
        } else if (Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).getTarget() != null) {
            target = Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).getTarget();
            direction = true;
        } else if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || (mc.currentScreen instanceof ThunderGui2 && ThunderGui2.getInstance().current_category == Category.HUD && ThunderGui2.currentMode == ThunderGui2.CurrentMode.Modules)) {
            if (isHovering()) {
                if (Mouse.isButtonDown(0) && mousestate) {
                    pos.getValue().setX((float) (normaliseX() - dragX) / e.scaledResolution.getScaledWidth());
                    pos.getValue().setY((float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
                }
            }
            if (Mouse.isButtonDown(0) && isHovering()) {
                if (!mousestate) {
                    dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                    dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
                }
                mousestate = true;
            } else {
                mousestate = false;
            }
            target = mc.player;
            direction = true;
        } else {
            direction = false;
            if (animation.getAnimationd() < 0.02)
                target = null;
        }
        if (target == null) {
            return;
        }

        //
        GlStateManager.pushMatrix();
        sizeAnimation(xPos + 75 + animX.getValue(), yPos + 25 + animY.getValue(), animation.getAnimationd());

        if (animation.getAnimationd() > 0) {

            float hurtPercent = (target.hurtTime - mc.getRenderPartialTicks()) / 6f;

            // Основа
            Particles.roundedRect(xPos, yPos, 70, 50, 12, new Color(0, 0, 0, 139));
            Particles.roundedRect(xPos + 50, yPos, 100, 50, 12, new Color(0, 0, 0, 255));
            //

            // Картинка
            if (imageMode.getValue() != ImageModeEn.None) {
                GL11.glPushMatrix();
                Stencil.write(false);
                boolean texture2 = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
                boolean blend2 = GL11.glIsEnabled(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                Particles.roundedRect(xPos + 50, yPos, 100, 50, 12, new Color(0, 0, 0, 255));
                if (!blend2)
                    GL11.glDisable(GL11.GL_BLEND);
                if (texture2)
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                Stencil.erase(true);
                GlStateManager.color(0.3f, 0.3f, 0.3f);
                if (imageMode.getValue() == ImageModeEn.Anime)
                    renderTexture(xPos + 50, yPos, 0, 0, 100, 50, 100, 50, 100, 50);
                else
                    renderCustomTexture(xPos + 50, yPos, 0, 0, 100, 50, 100, 50, 100, 50);
                Stencil.dispose();
                GL11.glPopMatrix();
                GlStateManager.resetColor();
            }
            //


            //Партиклы
            for (final Particles p : particles) {
                if (p.opacity > 4) p.render2D();
            }

            if (timer.passedMs(1000 / 60)) {
                ticks += 0.1f;
                for (final Particles p : particles) {
                    p.updatePosition();

                    if (p.opacity < 1) particles.remove(p);
                }
                timer.reset();
            }

            final java.util.ArrayList<Particles> removeList = new java.util.ArrayList<>();
            for (final Particles p : particles) {
                if (p.opacity <= 1) {
                    removeList.add(p);
                }
            }

            for (final Particles p : removeList) {
                particles.remove(p);
            }

            if ((target.hurtTime == 9 && !sentParticles) /*|| (lastTarget != null && ((EntityPlayer) lastTarget).hurtTime == 9 && !sentParticles) */) {
                for (int i = 0; i <= pcount.getValue(); i++) {
                    final Particles p = new Particles();
                    final Color c = Particles.mixColors(color.getValue().getColorObject(), color2.getValue().getColorObject(), (Math.sin(ticks + xPos * 0.4f + i) + 1) * 0.5f);
                    p.init(xPos + 19, yPos + 19, ((Math.random() - 0.5) * 2) * 1.4, ((Math.random() - 0.5) * 2) * 1.4, Math.random() * psize.getValue(), c);
                    particles.add(p);
                }
                sentParticles = true;
            }

            if (target.hurtTime == 8) sentParticles = false;
            //


            // Бошка
            GL11.glPushMatrix();
            Stencil.write(false);
            boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);

            float hurtPercent2 = hurtPercent;
            ebaloAnimation.setValue(hurtPercent2);
            hurtPercent2 = (float) ebaloAnimation.getAnimationD();
            if (hurtPercent2 < 0 && hurtPercent2 > -0.17) {
                hurtPercent2 = 0;
            }

            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            fastRoundedRect(xPos + 5.5f + hurtPercent2, yPos + 5.5f + hurtPercent2, xPos + 44 - hurtPercent2 * 2, yPos + 44 - hurtPercent2 * 2, 6F);

            if (!blend)
                GL11.glDisable(GL11.GL_BLEND);
            if (texture)
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            Stencil.erase(true);
            GlStateManager.color(1F, 1F - hurtPercent, 1F - hurtPercent);

            renderPlayerModelTexture(xPos + 5.5f + hurtPercent2, yPos + 5.5f + hurtPercent2, 3F, 3F, 3, 3, (int) (39 - (double) hurtPercent2 * 2), (int) (39 - (double) hurtPercent2 * 2), 24, 24.5f, (AbstractClientPlayer) target);
            renderPlayerModelTexture(xPos + 5.5f + hurtPercent2, yPos + 5.5f + hurtPercent2, 15F, 3F, 3, 3, (int) (39 - (double) hurtPercent2 * 2), (int) (39 - (double) hurtPercent2 * 2), 24, 24.5f, (AbstractClientPlayer) target);

            Stencil.dispose();
            GL11.glPopMatrix();
            GlStateManager.resetColor();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            //

            // Баллон
            float health = Math.min(20, target.getHealth());
            healthanimation.setValue(health);
            health = (float) healthanimation.getAnimationD();
            Color a = TwoColoreffect(color.getValue().getColorObject(), color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + 3.0F * (slices.getValue() * 2.55) / 60);
            Color b = TwoColoreffect(color.getValue().getColorObject(), color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + 3.0F * (slices1.getValue() * 2.55) / 60);
            Color c = TwoColoreffect(color.getValue().getColorObject(), color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + 3.0F * (slices2.getValue() * 2.55) / 60);
            Color d = TwoColoreffect(color.getValue().getColorObject(), color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + 3.0F * (slices3.getValue() * 2.55) / 60);

            RenderUtil.drawBlurredShadow(xPos + 53, yPos + 33 - 12, 94, 11, blurRadius.getValue(), a);

            RoundedShader.drawGradientRound(xPos + 55, yPos + 35 - 12, 90, 8, 2f, a.darker().darker(), b.darker().darker().darker().darker(), c.darker().darker().darker().darker(), d.darker().darker().darker().darker());
            RoundedShader.drawGradientRound(xPos + 55, yPos + 35 - 12, 90 * (health / 20), 8, 2f, a, b, c, d);
            if (hpMode.getValue() == HPmodeEn.HP) {
                FontRender.drawCentString6(String.valueOf(Math.round(10.0 * health) / 10.0), xPos + 100, yPos + 25, -1);
            } else {
                FontRender.drawCentString6(((Math.round(10.0 * health) / 10.0) / 20f) * 100 + "%", xPos + 100, yPos + 25, -1);
            }
            //

            //Броня
            NonNullList<ItemStack> armor = target.inventory.armorInventory;
            ItemStack[] items = new ItemStack[]{target.getHeldItemMainhand(), armor.get(3), armor.get(2), armor.get(1), armor.get(0), target.getHeldItemOffhand()};

            float xItemOffset = xPos + 60;
            for (ItemStack itemStack : items) {
                if (itemStack.isEmpty()) continue;
                GL11.glPushMatrix();
                GL11.glTranslated(xItemOffset, yPos + 35, 0);
                GL11.glScaled(0.75, 0.75, 0.75);

                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player, itemStack, 0, 0);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, 0, 0);
                RenderHelper.disableStandardItemLighting();

                GL11.glPopMatrix();
                xItemOffset += 14;
            }
            //

            //Имя
            if (!Thunderhack.moduleManager.getModuleByClass(NameProtect.class).isEnabled()) {
                FontRender.drawString6(target.getName() + " | " + (double) Math.round(10.0 * mc.player.getDistance(target)) / 10.0 + " m", xPos + 55, yPos + 5, -1, false);
            } else {
                FontRender.drawString6("Protected | " + (double) Math.round(10.0 * mc.player.getDistance(target)) / 10.0 + " m", xPos + 55, yPos + 5, -1, false);
            }
            //Поушены
            drawPotionEffect(target);
        }
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        xPos = (int) (e.scaledResolution.getScaledWidth() * pos.getValue().getX());
        yPos = (int) (e.scaledResolution.getScaledHeight() * pos.getValue().getY());
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        renderTHud(e);
        glPopAttrib();
    }

    public void renderCustomTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight) {
        if (customImg == null) {
            if (PNGtoResourceLocation.getCustomImg("targethud", "png") != null) {
                customImg = PNGtoResourceLocation.getCustomImg("targethud", "png");
            } else {
                Command.sendMessage("Перейди в .minecraft/ThunderHack/images и добавь туда png картинку с названием targethud");
                toggle();
            }
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(customImg);
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawPotionEffect(EntityPlayer entity) {
        StringBuilder finalString = new StringBuilder();
        for (PotionEffect potionEffect : entity.getActivePotionEffects()) {
            Potion potion = potionEffect.getPotion();
            if ((potion != MobEffects.REGENERATION) && (potion != MobEffects.SPEED) && (potion != MobEffects.STRENGTH) && (potion != MobEffects.WEAKNESS)) {
                continue;
            }

            boolean potRanOut = (double) potionEffect.getDuration() != 0.0;
            if (!entity.isPotionActive(potion) || !potRanOut) continue;

            GlStateManager.pushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            finalString.append(I18n.format(getPotionName(potion))).append(potionEffect.getAmplifier() < 1 ? "" : potionEffect.getAmplifier() + 1).append(" ").append(getDurationString(potionEffect)).append(" ");
            GlStateManager.popMatrix();
        }
        FontRender.drawString7(finalString.toString(), xPos + 55, yPos + 14, new Color(0x8D8D8D).getRGB(), false);
    }

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering() {
        return normaliseX() > xPos && normaliseX() < xPos + 150 && normaliseY() > yPos && normaliseY() < yPos + 50;
    }

    public enum HPmodeEn {
        HP,
        Percentage
    }


    public enum ImageModeEn {
        None,
        Anime,
        Custom
    }


}

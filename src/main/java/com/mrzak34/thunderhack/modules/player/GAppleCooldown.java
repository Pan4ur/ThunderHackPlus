package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.EventCooldown;
import com.mrzak34.thunderhack.events.GameZaloopEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.hud.elements.HudEditorGui;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.mixin.mixins.IKeyBinding;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.math.AstolfoAnimation;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import static com.mrzak34.thunderhack.gui.hud.elements.Indicators.getRG;

public class GAppleCooldown extends Module {


    public GAppleCooldown() {
        super("GAppleCooldown", "GAppleCooldown", Category.PLAYER);
    }

    public static long lastConsumeTime;
    private final Setting<mode> colorType = register(new Setting("Mode", mode.Astolfo));
    private final Setting<ColorSetting> cc = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));

    public static AstolfoAnimation astolfo = new AstolfoAnimation();


    int dragX, dragY = 0;
    private boolean mousestate = false;
    float x1 = 0;
    float y1 = 0;


    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        float offset = mc.player.getCooldownTracker().getCooldown(Items.GOLDEN_APPLE,0f);
        if (mc.currentScreen instanceof GuiChat){
            offset = 1;
        }
        if(offset <=0.01)return;

        GL11.glPushMatrix();
        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();
        GL11.glTranslated(pos.getValue().x * e.scaledResolution.getScaledWidth(), pos.getValue().y * e.scaledResolution.getScaledHeight(), 0);

        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || mc.currentScreen instanceof ThunderGui2) {
            if (Mouse.isButtonDown(0) && mousestate) {
                pos.getValue().setX((float) (normaliseX() - dragX) / e.scaledResolution.getScaledWidth());
                pos.getValue().setY((float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
            }
        }
        if (Mouse.isButtonDown(0)) {
            if (!mousestate && isHovering()) {
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
                mousestate = true;
            }
        } else {
            mousestate = false;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        boolean oldState = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(5.5f);
        GL11.glColor4f(0.1f, 0.1f, 0.1f, 0.5f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i < 360; i++) {
            double x = Math.cos(Math.toRadians(i)) * 14;
            double z = Math.sin(Math.toRadians(i)) * 14;
            GL11.glVertex2d(x, z);
        }
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = -90; i < -90 + (360 * offset); i++) {

            float red = cc.getValue().getRed();
            float green = cc.getValue().getGreen();
            float blue = cc.getValue().getBlue();
            if (colorType.getValue() == mode.StateBased) {
                float[] buffer = getRG(offset);
                red = buffer[0];
                green = buffer[1];
                blue = buffer[2];
            } else if (colorType.getValue() == mode.Astolfo) {
                double stage = (i + 90) / 360.;
                int clr = astolfo.getColor(stage);
                red = ((clr >> 16) & 255);
                green = ((clr >> 8) & 255);
                blue = ((clr & 255));
            }
            GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 1);
            double x = Math.cos(Math.toRadians(i)) * 14;
            double z = Math.sin(Math.toRadians(i)) * 14;
            GL11.glVertex2d(x, z);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        if (!oldState)
            GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.disableBlend();
        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.GOLDEN_APPLE), -8, -9);
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void onGameZaloop(GameZaloopEvent e){
        astolfo.update();
        if(Mouse.isButtonDown(1))
            ((IKeyBinding) mc.gameSettings.keyBindUseItem).setPressed(System.currentTimeMillis() - lastConsumeTime >= 2300);
    }

    @SubscribeEvent
    public void onCooldown(EventCooldown e){
        if (e.getStack() == Items.GOLDEN_APPLE && (System.currentTimeMillis() - lastConsumeTime < 2600))
            e.setCooldown((System.currentTimeMillis() - lastConsumeTime) / 2600f);
    }

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering() {
        return normaliseX() > x1 - 25 && normaliseX() < x1 + 25 && normaliseY() > y1 - 25 && normaliseY() < y1 + 25;
    }

    public enum mode {
        Static, StateBased, Astolfo
    }

}

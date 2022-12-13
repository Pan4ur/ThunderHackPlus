package com.mrzak34.thunderhack.mainmenu;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.thundergui.BlurUtil;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServerDemo;
import org.codehaus.plexus.util.reflection.Reflector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.io.IOException;
import java.nio.IntBuffer;

import static com.mrzak34.thunderhack.gui.thundergui.ThunderGui.setColor;
import static com.mrzak34.thunderhack.gui.thundergui.components.items.buttons.TFriendComponent.drawImage;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;

public class ThunderMenu extends GuiScreen
{
    private final MainMenuShader backgroundShader;
    private final long initTime;

    private int rotatelogo;

    public ThunderMenu() {
        try {
            this.backgroundShader = new MainMenuShader("/mainmenu.fsh");
            initTime = System.currentTimeMillis();
        } catch (IOException var9) {
            throw new IllegalStateException("Failed to load backgound shader", var9);

        }
    }

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.width = sr.getScaledWidth();
        this.height = sr.getScaledHeight();

        this.buttonList.add(new GuiMainMenuButton(666,5, (int) (sr.getScaledHeight() - 35f),0));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ++rotatelogo;
        ScaledResolution sr = new ScaledResolution(this.mc);
        GlStateManager.disableCull();
        this.backgroundShader.useShader(sr.getScaledWidth(), sr.getScaledHeight(), (float)mouseX, (float)mouseY, (float)(System.currentTimeMillis() - this.initTime) / 1000.0F);

        GL11.glBegin(7);
        GL11.glVertex2f(-1.0F, -1.0F);
        GL11.glVertex2f(-1.0F, 1.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glVertex2f(1.0F, -1.0F);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);


        RenderUtil.drawRect2(0,0,40,sr.getScaledHeight(),new Color(0x27FFFFFF, true).getRGB());
        BlurUtil.getInstance().blur(0,0,40,sr.getScaledHeight(), ThunderHackGui.getInstance().blurstr.getValue()/5);


        GlStateManager.pushMatrix();

        GlStateManager.translate(20f, 20f, 0);
        GlStateManager.rotate(func((float)this.rotatelogo), 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-20f, -20f, 0);
        drawImage(logo, 5,5, 30,30,new Color(-1));

        GlStateManager.popMatrix();

        drawImage(lightning, 0,0, 40,40,new Color(-1));

        RenderUtil.drawRect2(5,40,35,43,new Color(0x67020202, true).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static float func(float var0) {
        if ((var0 %= 360.0F) >= 180.0F) {
            var0 -= 360.0F;
        }

        if (var0 < -180.0F) {
            var0 += 360.0F;
        }

        return var0;
    }

    ResourceLocation logo = new ResourceLocation("textures/suka.png");
    ResourceLocation lightning = new ResourceLocation("textures/lightning.png");



    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiWorldSelection(this));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 14) {
          //  this.mc.displayGuiScreen(new GuiAltManager());
        }
        if (button.id == 666) {
            Thunderhack.onUnload();
            this.mc.shutdown();
        }
    }
    
}

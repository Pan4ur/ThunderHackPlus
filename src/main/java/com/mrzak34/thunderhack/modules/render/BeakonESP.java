package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class BeakonESP extends Module {
    public final Setting<ColorSetting> color = this.register(new Setting<>("ESPColor", new ColorSetting(0x8800FF00)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("CircleColor", new ColorSetting(0x8800FF00)));
    private final Setting<Integer> slices = this.register(new Setting<>("slices", 60, 10, 240));
    private final Setting<Integer> stacks = this.register(new Setting<>("stacks", 60, 10, 240));
    public BeakonESP() {
        super("BeakonESP", "радиус действия маяка", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        for (TileEntity tileent : mc.world.loadedTileEntityList) {
            if (tileent instanceof TileEntityBeacon) {
                TileEntityBeacon beacon = (TileEntityBeacon) tileent;
                final double n = beacon.getPos().getX();
                mc.getRenderManager();
                final double x = n - ((IRenderManager)mc.getRenderManager()).getRenderPosX();
                final double n2 = beacon.getPos().getY();
                mc.getRenderManager();
                final double y = n2 - ((IRenderManager)mc.getRenderManager()).getRenderPosY();
                final double n3 = beacon.getPos().getZ();
                mc.getRenderManager();
                final double z = n3 - ((IRenderManager)mc.getRenderManager()).getRenderPosZ();
                GL11.glPushMatrix();
                RenderUtil.drawBlockOutline(beacon.getPos(), color.getValue().getColorObject(), 3f, true, 0);
                RenderHelper.disableStandardItemLighting();
                float var12 = (float) beacon.getLevels();
                float var13 = var12 == 1.0F ? 19.0F : (var12 == 2.0F ? 29.0F : (var12 == 3.0F ? 39.0F : (var12 == 4.0F ? 49.0F : 0.0F)));
                draw(x, y, z, (int) var13);
                RenderHelper.enableStandardItemLighting();
                GL11.glPopMatrix();
            }
        }
    }


    public void draw(double x, double y, double z, int power) {
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(true);
        GL11.glLineWidth(1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glColor4f(color2.getValue().getRed() / 255f, color2.getValue().getBlue() / 255f, color2.getValue().getBlue() / 255f, color2.getValue().getAlpha() / 255f);
        final Sphere tip = new Sphere();
        tip.setDrawStyle(100013);

        tip.draw(power, slices.getValue(), stacks.getValue());

        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }


}

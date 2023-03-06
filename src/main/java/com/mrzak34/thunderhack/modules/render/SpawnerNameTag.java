package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import static com.mrzak34.thunderhack.util.render.RenderUtil.drawRect;
import static com.mrzak34.thunderhack.util.render.RenderUtil.interpolate;

public class SpawnerNameTag extends Module {
    public SpawnerNameTag() {
        super("SpawnerNameTag", "Подсвечивает спавнера", "spawner esp", Category.RENDER);
    }

    public final Setting<ColorSetting> rectcolor = this.register(new Setting<>("RectColor", new ColorSetting(1426063360)));
    public final Setting<ColorSetting> color = this.register(new Setting<>("ESPColor", new ColorSetting(0x8800FF00)));
    private final Setting<Float> scaling = this.register(new Setting<>("Size", 20.0f, 0.1f, 30.0f));
    private final Setting<Boolean> scaleing = this.register(new Setting<>("Scale", true));
    private final Setting<Float> factor = this.register(new Setting<>("Factor", 0.17f, 0.1f, 1.0f));

    @SubscribeEvent
    public void onRender3D(Render3DEvent event){
        for(TileEntity tileent : mc.world.loadedTileEntityList){
            if(tileent instanceof TileEntityMobSpawner){
                TileEntityMobSpawner spawner = (TileEntityMobSpawner) tileent;
                final double n = spawner.getPos().x;
                mc.getRenderManager();
                final double x = n - mc.getRenderManager().renderPosX;
                final double n2 = spawner.getPos().y;
                mc.getRenderManager();
                final double y = n2 - mc.getRenderManager().renderPosY;
                final double n3 = spawner.getPos().z;
                mc.getRenderManager();
                final double z = n3 - mc.getRenderManager().renderPosZ;

                GL11.glPushMatrix();
                RenderUtil.drawBlockOutline(spawner.getPos(), color.getValue().getColorObject(), 3f, true,0);
                RenderHelper.disableStandardItemLighting();

                String entity = StringUtils.substringBetween(spawner.getUpdateTag().toString(), "id:\"minecraft:", "\"");
                int time = Integer.parseInt(StringUtils.substringBetween(spawner.getUpdateTag().toString(), ",Delay:", "s,")) / 20;
                renderNameTag( x + 0.5f, y, z + 0.5f, event.getPartialTicks(),entity + " " + time + " s");
                RenderHelper.enableStandardItemLighting();
                GL11.glPopMatrix();
            }
        }
    }


    private void renderNameTag(final double x, final double y, final double z, final float delta, String displayTag) {
        double tempY = y;
        tempY +=  0.7;
        final Entity camera = NameTags.mc.getRenderViewEntity();
        assert camera != null;
        final double originalPositionX = camera.posX;
        final double originalPositionY = camera.posY;
        final double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX,  camera.posX,  delta);
        camera.posY = interpolate(camera.prevPosY,  camera.posY,  delta);
        camera.posZ = interpolate(camera.prevPosZ,  camera.posZ,  delta);
        final double distance = camera.getDistance(x + NameTags.mc.getRenderManager().viewerPosX,  y + NameTags.mc.getRenderManager().viewerPosY,  z + NameTags.mc.getRenderManager().viewerPosZ);
        final int width = mc.fontRenderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + scaling.getValue() * (distance * factor.getValue())) / 1000.0;
        if (!scaleing.getValue()) {
            scale = scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f,  -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x,  (float)tempY + 1.4f,  (float)z);
        GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY,  0.0f,  1.0f,  0.0f);
        GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX,  (NameTags.mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f,  0.0f,  0.0f);
        GlStateManager.scale(-scale,  -scale,  scale);
        GlStateManager.disableDepth ( );
        GlStateManager.enableBlend ( );

        drawRect((float)(-width - 2),  -4f,  width + 2.0f,  4f,  rectcolor.getValue().getColor());

        GlStateManager.disableBlend ( );
        mc.fontRenderer.drawStringWithShadow(displayTag,  (float)(-width),  -4f,  -1);
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth ( );
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f,  1500000.0f);
        GlStateManager.popMatrix();
    }

}

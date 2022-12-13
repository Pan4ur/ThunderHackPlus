package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;

import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import java.util.ArrayList;
import java.util.List;

public class BeakonESP extends Module{
    public BeakonESP() {
        super("BeakonESP", "радиус действия маяка", Category.RENDER, true, false, false);
    }

    private  final Setting<Integer> rang = this.register( new Setting<>("Range", 60, 10, 240));
    private  final Setting<Integer> slices = this.register( new Setting<>("slices", 60, 10, 240));
    private  final Setting<Integer> stacks = this.register( new Setting<>("stacks", 60, 10, 240));
    private  final Setting<Integer> radius = this.register( new Setting<>("radius", 10, 10, 50));

    public final Setting<ColorSetting> color = this.register(new Setting<>("ESPColor", new ColorSetting(0x8800FF00)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("CircleColor", new ColorSetting(0x8800FF00)));

    Timer timer = new Timer();
    List<BlockPos> bekon = new ArrayList<>();

    @SubscribeEvent
    public void onRender3D(Render3DEvent event){
        if(timer.passedMs(5000)){
            bekon.clear();
            bekon = getPositions(mc.player);
            timer.reset();
        }

        for(BlockPos bp : bekon){
            try{

                final double n = bp.x ;
                mc.getRenderManager();
                final double x = n - mc.getRenderManager().renderPosX;
                final double n2 = bp.y ;
                mc.getRenderManager();
                final double y = n2 - mc.getRenderManager().renderPosY;
                final double n3 = bp.z ;
                mc.getRenderManager();
                final double z = n3 - mc.getRenderManager().renderPosZ;
                
                GL11.glPushMatrix();
                RenderUtil.drawBlockOutline(bp, color.getValue().getColorObject(), 3f, true);
                RenderHelper.disableStandardItemLighting();
                draw(x,y,z);
                RenderHelper.enableStandardItemLighting();

                GL11.glPopMatrix();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    public void draw(double x, double y, double z) {
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(true);
        GL11.glLineWidth(1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glColor4f(color2.getValue().getRed()/255f, color2.getValue().getBlue()/255f, color2.getValue().getBlue()/255f, color2.getValue().getAlpha()/255f);
        final Sphere tip = new Sphere();
        tip.setDrawStyle(100013);
        tip.draw(radius.getValue(), slices.getValue(), stacks.getValue());
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }

    private List<BlockPos> getPositions(Entity entity2) {
        ArrayList<BlockPos> arrayList = new ArrayList<>();
        int playerX = (int)entity2.posX;
        int playerY = (int)entity2.posY;
        int playerZ = (int)entity2.posZ;

        int n4 = (int)(rang.getValue() + 2.0f);

        double playerX1 = entity2.posX - 0.5;
        double playerY1 = entity2.posY + (double)entity2.getEyeHeight() - 1.0;
        double playerZ1 = entity2.posZ - 0.5;

        for (int n5 =  playerX - n4; n5 <= playerX + n4 ; ++n5) {
            for (int n6 = playerZ - n4; n6 <= playerZ + n4; ++n6) {
                for (int n8 = playerY - n4; n8 < playerY + n4; ++n8) {
                    if (((n5 - playerX1) * (n5 - playerX1) + (n8 - playerY1) * (n8 - playerY1) + (n6 - playerZ1) * (n6 - playerZ1) <= (rang.getValue() * rang.getValue())) && isBeakon(new BlockPos(n5, n8, n6))) {
                        arrayList.add(new BlockPos(n5, n8, n6));
                    }
                }
            }
        }
        return arrayList;
    }

    public boolean isBeakon(BlockPos bp){
        return mc.world.getBlockState(bp).getBlock() == Blocks.BEACON;
    }


}

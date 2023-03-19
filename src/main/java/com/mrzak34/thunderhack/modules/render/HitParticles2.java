package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import static com.mrzak34.thunderhack.util.render.DrawHelper.injectAlpha;

public class HitParticles2 extends Module {

    public final Setting<ColorSetting> colorLight = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    public final Setting<ColorSetting> colorLight2 = this.register(new Setting<>("Color2", new ColorSetting(0x8800FF00)));
    public final Setting<ColorSetting> colorLight3 = this.register(new Setting<>("Color3", new ColorSetting(0x8800FF00)));
    public final Setting<ColorSetting> colorLight4 = this.register(new Setting<>("Color4", new ColorSetting(0x8800FF00)));



    public Setting<Boolean> selfp = register(new Setting("Self", false));
    public Setting<Integer> speedor = this.register(new Setting<>("Time", 8000, 1, 10000));
    public Setting<Integer> speedor2 = this.register(new Setting<>("speed", 20, 1, 1000));
    ArrayList<Particle> particles = new ArrayList<>();
    public HitParticles2() {
        super("HitParticles", "HitParticles", Category.RENDER);
    }

    @Override
    public void onUpdate() {
        if (mc.world != null && mc.player != null) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (!selfp.getValue() && player == mc.player) {
                    continue;
                }
                if (player.hurtTime > 0) {

                    Color col = null;

                    int i = (int) (MathUtil.random(0,3));

                    switch (i){
                        case 0: {col = colorLight.getValue().getColorObject();break;}
                        case 1: {col = colorLight2.getValue().getColorObject();break;}
                        case 2: {col = colorLight3.getValue().getColorObject();break;}
                        case 3: {col = colorLight4.getValue().getColorObject();break;}
                    }

                    particles.add(new Particle(player.posX + MathUtil.random(-0.05f, 0.05f), MathUtil.random((float) (player.posY + player.height), (float) player.posY), player.posZ + MathUtil.random(-0.05f, 0.05f),col));
                    particles.add(new Particle(player.posX, MathUtil.random((float) (player.posY + player.height), (float) (player.posY + 0.1f)), player.posZ,col));
                    particles.add(new Particle(player.posX, MathUtil.random((float) (player.posY + player.height), (float) (player.posY + 0.1f)), player.posZ,col));
                }

                for (int i = 0; i < particles.size(); i++) {
                    if (System.currentTimeMillis() - particles.get(i).getTime() >= speedor.getValue()) {
                        particles.remove(i);
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.player != null && mc.world != null) {
            for (Particle particle : particles) {
                particle.render();
            }
        }
    }

    public class Particle {
        public int alpha = 180;
        double x;
        double y;
        double z;
        double motionX;
        double motionY;
        double motionZ;
        long time;
        Color color;

        public Particle(double x, double y, double z,Color color) {
            this.x = x;
            this.y = y;
            this.z = z;
            motionX = MathUtil.random(-(float) speedor2.getValue() / 1000f, (float) speedor2.getValue() / 1000f);
            motionY = MathUtil.random(-(float) speedor2.getValue() / 1000f, (float) speedor2.getValue() / 1000f);
            motionZ = MathUtil.random(-(float) speedor2.getValue() / 1000f, (float) speedor2.getValue() / 1000f);
            time = System.currentTimeMillis();
            this.color = color;
        }


        public long getTime() {
            return time;
        }

        public void update() {
            double yEx = 0;

            double sp = Math.sqrt(motionX * motionX + motionZ * motionZ) * 1;
            x += motionX;

            y += motionY;

            if (posBlock(x, y, z)) {
                motionY = -motionY / 1.1;
            } else {
                if (
                        posBlock(x, y, z) ||
                                posBlock(x, y - yEx, z) ||
                                posBlock(x, y + yEx, z) ||

                                posBlock(x - sp, y, z - sp) ||
                                posBlock(x + sp, y, z + sp) ||
                                posBlock(x + sp, y, z - sp) ||
                                posBlock(x - sp, y, z + sp) ||
                                posBlock(x + sp, y, z) ||
                                posBlock(x - sp, y, z) ||
                                posBlock(x, y, z + sp) ||
                                posBlock(x, y, z - sp) ||

                                posBlock(x - sp, y - yEx, z - sp) ||
                                posBlock(x + sp, y - yEx, z + sp) ||
                                posBlock(x + sp, y - yEx, z - sp) ||
                                posBlock(x - sp, y - yEx, z + sp) ||
                                posBlock(x + sp, y - yEx, z) ||
                                posBlock(x - sp, y - yEx, z) ||
                                posBlock(x, y - yEx, z + sp) ||
                                posBlock(x, y - yEx, z - sp) ||

                                posBlock(x - sp, y + yEx, z - sp) ||
                                posBlock(x + sp, y + yEx, z + sp) ||
                                posBlock(x + sp, y + yEx, z - sp) ||
                                posBlock(x - sp, y + yEx, z + sp) ||
                                posBlock(x + sp, y + yEx, z) ||
                                posBlock(x - sp, y + yEx, z) ||
                                posBlock(x, y + yEx, z + sp) ||
                                posBlock(x, y + yEx, z - sp)

                ) {
                    motionX = -motionX + motionZ;
                    motionZ = -motionZ + motionX;
                }


            }

            z += motionZ;


            motionX /= 1.005;
            motionZ /= 1.005;
            motionY /= 1.005;
        }

        public void render() {
            color = injectAlpha(color,alpha);
            update();
            alpha -= 0.1;
            float scale = 0.07f;
            GlStateManager.enableDepth();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            try {

                final double posX = x - ((IRenderManager) Util.mc.getRenderManager()).getRenderPosX();
                final double posY = y - ((IRenderManager) Util.mc.getRenderManager()).getRenderPosY();
                final double posZ = z - ((IRenderManager) Util.mc.getRenderManager()).getRenderPosZ();

                final double distanceFromPlayer = mc.player.getDistance(x, y - 1, z);
                int quality = (int) (distanceFromPlayer * 4 + 10);

                if (quality > 350)
                    quality = 350;

                GL11.glPushMatrix();
                GL11.glTranslated(posX, posY, posZ);


                GL11.glScalef(-scale, -scale, -scale);

                GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
                GL11.glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);


                RenderUtil.drawFilledCircleNoGL(0, 0, 0.7, color.hashCode(), quality);

                if (distanceFromPlayer < 4)
                    RenderUtil.drawFilledCircleNoGL(0, 0, 1.4, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50).hashCode(), quality);

                if (distanceFromPlayer < 20)
                    RenderUtil.drawFilledCircleNoGL(0, 0, 2.3, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30).hashCode(), quality);


                GL11.glScalef(0.8f, 0.8f, 0.8f);

                GL11.glPopMatrix();


            } catch (final ConcurrentModificationException ignored) {
            }

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor3d(255, 255, 255);
        }

        private boolean posBlock(double x, double y, double z) {
            return (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.AIR &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.WATER &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.LAVA &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BED &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.CAKE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.TALLGRASS &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.FLOWER_POT &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.RED_FLOWER &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.YELLOW_FLOWER &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SAPLING &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.VINE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ACACIA_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ACACIA_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BIRCH_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BIRCH_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DARK_OAK_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DARK_OAK_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.JUNGLE_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.JUNGLE_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.NETHER_BRICK_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.OAK_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.OAK_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SPRUCE_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SPRUCE_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ENCHANTING_TABLE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.END_PORTAL_FRAME &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DOUBLE_PLANT &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.STANDING_SIGN &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.WALL_SIGN &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SKULL &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DAYLIGHT_DETECTOR &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DAYLIGHT_DETECTOR_INVERTED &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.STONE_SLAB &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.WOODEN_SLAB &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.CARPET &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DEADBUSH &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.VINE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.REDSTONE_WIRE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.REEDS &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SNOW_LAYER);
        }

    }
}

package com.mrzak34.thunderhack.mixin.mixins;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.FreecamEvent;
import com.mrzak34.thunderhack.events.PreRenderEvent;
import com.mrzak34.thunderhack.events.RenderHand;
import com.mrzak34.thunderhack.modules.combat.BackTrack;
import com.mrzak34.thunderhack.modules.misc.ThirdPersView;
import com.mrzak34.thunderhack.modules.misc.Weather;
import com.mrzak34.thunderhack.modules.movement.LegitStrafe;
import com.mrzak34.thunderhack.modules.player.FreeCam;
import com.mrzak34.thunderhack.modules.player.NoCameraClip;
import com.mrzak34.thunderhack.modules.player.NoEntityTrace;
import com.mrzak34.thunderhack.modules.player.Reach;
import com.mrzak34.thunderhack.modules.render.Ambience;
import com.mrzak34.thunderhack.modules.render.FogColor;
import com.mrzak34.thunderhack.modules.render.ItemShaders;
import com.mrzak34.thunderhack.modules.render.NoRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer {

    @Shadow
    public Entity pointedEntity;
    @Shadow
    public
    boolean debugView;
    @Shadow
    public float farPlaneDistance;
    @Final
    @Shadow
    public ItemRenderer itemRenderer;
    @Shadow
    public float thirdPersonDistancePrev;
    @Shadow
    public boolean cloudFog;
    @Shadow
    private ItemStack itemActivationItem;
    @Shadow
    @Final
    private int[] lightmapColors;

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;clear(I)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (Display.isActive() || Display.isVisible()) {
            PreRenderEvent render3dEvent = new PreRenderEvent(partialTicks);
            MinecraftForge.EVENT_BUS.post(render3dEvent);
        }
    }

    @Inject(method = {"renderItemActivation"}, at = {@At("HEAD")}, cancellable = true)
    public void renderItemActivationHook(final CallbackInfo info) {
        if (this.itemActivationItem != null && NoRender.getInstance().isOn() && NoRender.getInstance().totemPops.getValue() && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING) {
            info.cancel();
        }
    }

    @Inject(method = {"renderItemActivation"}, at = {@At("HEAD")}, cancellable = true)
    public void renderItemActivationHook(int p_190563_1_, int p_190563_2_, float p_190563_3_, CallbackInfo ci) {
        if (this.itemActivationItem != null && NoRender.getInstance().isOn() && NoRender.getInstance().totemPops.getValue() && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING) {
            ci.cancel();
        }
    }

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderRainSnow(F)V"))
    public void weatherHook(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(Weather.class).isOn()) {
            Thunderhack.moduleManager.getModuleByClass(Weather.class).render(partialTicks);
        }
    }

    @Inject(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE))
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        Ambience ambience = Thunderhack.moduleManager.getModuleByClass(Ambience.class);
        if (ambience.isEnabled()) {
            for (int i = 0; i < this.lightmapColors.length; ++i) {
                Color ambientColor = ambience.colorLight.getValue().getColorObject();
                int alpha = ambientColor.getAlpha();
                float modifier = (float) alpha / 255.0f;
                int color = this.lightmapColors[i];
                int[] bgr = toRGBAArray(color);
                Vector3f values = new Vector3f((float) bgr[2] / 255.0f, (float) bgr[1] / 255.0f, (float) bgr[0] / 255.0f);
                Vector3f newValues = new Vector3f((float) ambientColor.getRed() / 255.0f, (float) ambientColor.getGreen() / 255.0f, (float) ambientColor.getBlue() / 255.0f);
                Vector3f finalValues = mix(values, newValues, modifier);
                int red = (int) (finalValues.x * 255.0f);
                int green = (int) (finalValues.y * 255.0f);
                int blue = (int) (finalValues.z * 255.0f);
                this.lightmapColors[i] = 0xFF000000 | red << 16 | green << 8 | blue;
            }
        }
    }

    private int[] toRGBAArray(int colorBuffer) {
        return new int[]{colorBuffer >> 16 & 0xFF, colorBuffer >> 8 & 0xFF, colorBuffer & 0xFF};
    }

    private Vector3f mix(Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }

    @Redirect(method = {"setupCameraTransform"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"))
    public float prevTimeInPortalHook(final EntityPlayerSP entityPlayerSP) {
        if (NoRender.getInstance().isOn() && (NoRender.getInstance().nausea.getValue()) || NoRender.getInstance().portal.getValue()) {
            return -3.4028235E38f;
        }
        return entityPlayerSP.prevTimeInPortal;
    }


    /*
    @Redirect(method = {"setupFog"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState getBlockStateAtEntityViewpointHook(final World worldIn, final Entity entityIn, final float p_186703_2_) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.AIR) {
            return Blocks.AIR.defaultBlockState;
        }
        return ActiveRenderInfo.getBlockStateAtEntityViewpoint(worldIn, entityIn, p_186703_2_);
    }
     */

    @Inject(method = {"hurtCameraEffect"}, at = {@At("HEAD")}, cancellable = true)
    public void hurtCameraEffectHook(final float ticks, final CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().hurtcam.getValue()) {
            info.cancel();
        }
    }

    //3arthH4ck
    @Shadow
    public abstract FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha);

    @Inject(method = "setupFogColor", at = @At("HEAD"), cancellable = true)
    public void setupFogColoHook(boolean black, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(FogColor.class).isOn()) {
            ci.cancel();
            Color fogColor = Thunderhack.moduleManager.getModuleByClass(FogColor.class).color.getValue().getColorObject();
            GlStateManager.glFog(2918, setFogColorBuffer(fogColor.getRed() / 255.0f, fogColor.getGreen() / 255.0f, fogColor.getBlue() / 255.0f, fogColor.getAlpha() / 255.0f));
        }
    }

    @Redirect(method = {"getMouseOver"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        if (NoEntityTrace.getINSTANCE().isOn() && NoEntityTrace.getINSTANCE().noTrace) {
            return new ArrayList<>();
        }
        return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Inject(method = "getMouseOver", at = @At("HEAD"), cancellable = true)
    public void getMouseOverHook(float partialTicks, CallbackInfo ci) {
        Reach reach = Thunderhack.moduleManager.getModuleByClass(Reach.class);
        BackTrack bt = Thunderhack.moduleManager.getModuleByClass(BackTrack.class);
        if (bt.isOn() || reach.isOn()) {
            ci.cancel();
            getMouseOverCustom(partialTicks);
        }
    }

    public void getMouseOverCustom(float partialTicks) {
        Reach reach = Thunderhack.moduleManager.getModuleByClass(Reach.class);
        BackTrack bt = Thunderhack.moduleManager.getModuleByClass(BackTrack.class);

        Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.world != null) {
            mc.profiler.startSection("pick");
            mc.pointedEntity = null;
            double d0 = mc.playerController.getBlockReachDistance();

            if (reach.isOn()) {
                d0 = d0 + reach.add.getValue();
            }

            mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
            Vec3d vec3d = entity.getPositionEyes(partialTicks);
            boolean flag = false;
            double d1 = d0;
            if (mc.playerController.extendedReach()) {
                d1 = 6.0;
                d0 = d1;
            } else if (d0 > 3.0) {
                flag = true;
            }

            if (mc.objectMouseOver != null) {
                d1 = mc.objectMouseOver.hitVec.distanceTo(vec3d);
            }

            Vec3d vec3d1 = entity.getLook(1.0F);
            Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
            pointedEntity = null;
            Vec3d vec3d3 = null;
            float f = 1.0F;


            List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0, 1.0, 1.0), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                public boolean apply(@Nullable Entity p_apply_1_) {
                    return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                }
            }));
            double d2 = d1;
            if (!Thunderhack.class.getName().toLowerCase().contains("der")) {
                Minecraft.getMinecraft().shutdown();
            }
            for (Entity value : list) {
                AxisAlignedBB axisalignedbb = value.getEntityBoundingBox().grow(value.getCollisionBorderSize());
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
                if (axisalignedbb.contains(vec3d)) {
                    if (d2 >= 0.0) {
                        pointedEntity = value;
                        vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                        d2 = 0.0;
                    }
                } else if (raytraceresult != null) {
                    double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                    if (d3 < d2 || d2 == 0.0) {
                        if (value.getLowestRidingEntity() == entity.getLowestRidingEntity() && !value.canRiderInteract()) {
                            if (d2 == 0.0) {
                                pointedEntity = value;
                                vec3d3 = raytraceresult.hitVec;
                            }
                        } else {
                            pointedEntity = value;
                            vec3d3 = raytraceresult.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }


            if (pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0) {
                pointedEntity = null;
                mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null, new BlockPos(vec3d3));
            }

            if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
                mc.objectMouseOver = new RayTraceResult(pointedEntity, vec3d3);
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mc.pointedEntity = pointedEntity;
                }
            }


            if (pointedEntity == null && bt.isOn()) {
                for (EntityPlayer pl_box : mc.world.playerEntities) {
                    if (pl_box == mc.player) {
                        continue;
                    }
                    List<BackTrack.Box> trails22 = new ArrayList<>();
                    bt.entAndTrail.putIfAbsent(pl_box, trails22);
                    if (bt.entAndTrail.get(pl_box).size() > 0) {
                        for (int i = 0; i < bt.entAndTrail.get(pl_box).size(); i++) {
                            AxisAlignedBB axisalignedbb = new AxisAlignedBB(
                                    Thunderhack.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().x - 0.3,
                                    Thunderhack.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().y,
                                    Thunderhack.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().z - 0.3,
                                    Thunderhack.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().x + 0.3,
                                    Thunderhack.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().y + 1.8,
                                    Thunderhack.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().z + 0.3);

                            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
                            if (axisalignedbb.contains(vec3d)) {
                                if (d2 >= 0.0) {
                                    pointedEntity = pl_box;
                                    vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                                    d2 = 0.0;
                                    if (raytraceresult != null) {
                                        mc.objectMouseOver = raytraceresult;
                                    }
                                }
                            } else if (raytraceresult != null) {
                                double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                                if (d3 < d2 || d2 == 0.0) {
                                    if (pl_box.getLowestRidingEntity() == entity.getLowestRidingEntity() && !pl_box.canRiderInteract()) {
                                        if (d2 == 0.0) {
                                            pointedEntity = pl_box;
                                        }
                                    } else {
                                        pointedEntity = pl_box;
                                        d2 = d3;
                                    }
                                }
                            }
                        }
                    }
                }
                if (pointedEntity != null) {
                    mc.objectMouseOver = new RayTraceResult(pointedEntity);
                }
            }

            mc.profiler.endSection();
        }

    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;"))
    private Entity redirectMouseOver(Minecraft mc) {
        FreecamEvent event = new FreecamEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            if (Keyboard.isKeyDown(FreeCam.getInstance().movePlayer.getValue().getKey())) {
                return mc.player;
            }
        }
        return mc.getRenderViewEntity();
    }

    @Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;turn(FF)V"))
    private void redirectTurn(EntityPlayerSP entityPlayerSP, float yaw, float pitch) {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            FreecamEvent event = new FreecamEvent();
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                if (Keyboard.isKeyDown(FreeCam.getInstance().movePlayer.getValue().getKey())) {
                    mc.player.turn(yaw, pitch);
                } else {
                    Objects.requireNonNull(mc.getRenderViewEntity(), "Render Entity").turn(yaw, pitch);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        entityPlayerSP.turn(yaw, pitch);
    }

    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isSpectator()Z"))
    public boolean redirectIsSpectator(EntityPlayerSP entityPlayerSP) {
        FreecamEvent event = new FreecamEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return true;
        if (entityPlayerSP != null) {
            return entityPlayerSP.isSpectator();
        }
        return false;
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    public void renderHandMain(float partialTicks, int pass, CallbackInfo ci) {
        ItemShaders module = Thunderhack.moduleManager.getModuleByClass(ItemShaders.class);
        if (module.isEnabled()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (!module.cancelItem.getValue()) {
                doRenderHand(partialTicks, pass, mc);
            }
            if (!(module.glowESP.getValue() == ItemShaders.glowESPmode.None) && !(module.fillShader.getValue() == ItemShaders.fillShadermode.None)) {
                GlStateManager.pushMatrix();
                RenderHand.PreBoth hand = new RenderHand.PreBoth(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand);
                doRenderHand(partialTicks, pass, mc);
                RenderHand.PostBoth hand2 = new RenderHand.PostBoth(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand2);
                GlStateManager.popMatrix();
            }

            if (!(module.glowESP.getValue() == ItemShaders.glowESPmode.None)) {
                GlStateManager.pushMatrix();
                RenderHand.PreOutline hand = new RenderHand.PreOutline(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand);
                doRenderHand(partialTicks, pass, mc);
                RenderHand.PostOutline hand2 = new RenderHand.PostOutline(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand2);
                GlStateManager.popMatrix();
            }

            if (!(module.fillShader.getValue() == ItemShaders.fillShadermode.None)) {
                GlStateManager.pushMatrix();
                RenderHand.PreFill hand = new RenderHand.PreFill(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand);
                doRenderHand(partialTicks, pass, mc);
                RenderHand.PostFill hand2 = new RenderHand.PostFill(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand2);
                GlStateManager.popMatrix();
            }

            ci.cancel();
        }

    }

    @Shadow
    public abstract float getFOVModifier(float partialTicks, boolean useFOVSetting);

    @Shadow
    public abstract void hurtCameraEffect(float partialTicks);

    @Shadow
    public abstract void applyBobbing(float partialTicks);

    @Shadow
    public abstract void enableLightmap();

    @Shadow
    public abstract void disableLightmap();

    void doRenderHand(float partialTicks, int pass, Minecraft mc) {
        if (!debugView) {
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            float f = 0.07F;

            if (mc.gameSettings.anaglyph) {
                GlStateManager.translate((float) (-(pass * 2 - 1)) * 0.07F, 0.0F, 0.0F);
            }

            Project.gluPerspective(getFOVModifier(partialTicks, false), (float) mc.displayWidth / (float) mc.displayHeight, 0.05F, farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();

            if (mc.gameSettings.anaglyph) {
                GlStateManager.translate((float) (pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GlStateManager.pushMatrix();
            hurtCameraEffect(partialTicks);

            if (mc.gameSettings.viewBobbing) {
                applyBobbing(partialTicks);
            }

            boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();

            if (!net.minecraftforge.client.ForgeHooksClient.renderFirstPersonHand(mc.renderGlobal, partialTicks, pass))
                if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator()) {
                    enableLightmap();
                    itemRenderer.renderItemInFirstPerson(partialTicks);
                    disableLightmap();
                }

            GlStateManager.popMatrix();

            if (mc.gameSettings.thirdPersonView == 0 && !flag) {
                itemRenderer.renderOverlays(partialTicks);
                hurtCameraEffect(partialTicks);
            }

            if (mc.gameSettings.viewBobbing) {
                applyBobbing(partialTicks);
            }
        }
    }

    @Shadow
    public abstract void renderHand(float partialTicks, int pass);

    @Inject(method = "orientCamera", at = @At("HEAD"), cancellable = true)
    private void orientCameraHook(float partialTicks, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(ThirdPersView.class).isOn()) {
            ci.cancel();
            orientCameraCustom(partialTicks);
        }
    }


    public void orientCameraCustom(float partialTicks) {
        Entity entity = mc.getRenderViewEntity();
        assert entity != null;
        float f = entity.getEyeHeight();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
        float f1;
        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPlayerSleeping()) {
            f = (float) ((double) f + 1.0D);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);
            if (!mc.gameSettings.debugCamEnable) {
                BlockPos blockpos = new BlockPos(entity);
                IBlockState iblockstate = mc.world.getBlockState(blockpos);
                ForgeHooksClient.orientBedCamera(mc.world, blockpos, iblockstate, entity);
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
        } else if (mc.gameSettings.thirdPersonView > 0) {
            double d3 = Thunderhack.moduleManager.getModuleByClass(ThirdPersView.class).isOn() ? Thunderhack.moduleManager.getModuleByClass(ThirdPersView.class).z.getValue() : (double) (thirdPersonDistancePrev + (4.0F - thirdPersonDistancePrev) * partialTicks);
            if (mc.gameSettings.debugCamEnable) {
                GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
            } else {
                float f2;
                if (Thunderhack.moduleManager.getModuleByClass(ThirdPersView.class).isOff()) {
                    f1 = entity.rotationYaw;
                    f2 = entity.rotationPitch;
                } else {
                    f1 = entity.rotationYaw + Thunderhack.moduleManager.getModuleByClass(ThirdPersView.class).x.getValue();
                    f2 = entity.rotationPitch + Thunderhack.moduleManager.getModuleByClass(ThirdPersView.class).y.getValue();
                }

                if (mc.gameSettings.thirdPersonView == 2) {
                    f2 += 180.0F;
                }

                double d4 = (double) (-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d5 = (double) (MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d6 = (double) (-MathHelper.sin(f2 * 0.017453292F)) * d3;

                for (int i = 0; i < 8; ++i) {
                    float f3 = (float) ((i & 1) * 2 - 1);
                    float f4 = (float) ((i >> 1 & 1) * 2 - 1);
                    float f5 = (float) ((i >> 2 & 1) * 2 - 1);
                    f3 *= 0.1F;
                    f4 *= 0.1F;
                    f5 *= 0.1F;
                    RayTraceResult raytraceresult = mc.world.rayTraceBlocks(new Vec3d(d0 + (double) f3, d1 + (double) f4, d2 + (double) f5), new Vec3d(d0 - d4 + (double) f3 + (double) f5, d1 - d6 + (double) f4, d2 - d5 + (double) f5));
                    if (raytraceresult != null) {
                        double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));
                        if (d7 < d3) {
                            d3 = d7;
                        }
                    }
                }

                if (mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
                GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GlStateManager.translate(0.0F, 0.0F, 0.05F);
        }

        if (!mc.gameSettings.debugCamEnable) {
            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
            float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            f1 = 0.0F;
            if (entity instanceof EntityAnimal) {
                EntityAnimal entityanimal = (EntityAnimal) entity;
                yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
            }

            IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, entity, partialTicks);
            EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup(mc.entityRenderer, entity, state, partialTicks, yaw, pitch, f1);
            MinecraftForge.EVENT_BUS.post(event);
            GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
        }

        GlStateManager.translate(0.0F, -f, 0.0F);
        d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
        d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
        d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
        cloudFog = mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
    }


    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
    public RayTraceResult rayTraceBlocks(WorldClient world, Vec3d start, Vec3d end) {
        return Thunderhack.moduleManager.getModuleByClass(NoCameraClip.class).isOn() ? null : world.rayTraceBlocks(start, end);
    }

    @Inject(method = "orientCamera", at = @At("RETURN"))
    private void orientCameraStoreEyeHeight(float partialTicks, CallbackInfo ci) {
        if (!Thunderhack.moduleManager.getModuleByClass(LegitStrafe.class).isOn()) return;
        Entity entity = mc.getRenderViewEntity();
        if (entity != null) {
            GlStateManager.translate(0.0f, entity.getEyeHeight() - 0.4f, 0.0f);
        }
    }
    /*
        @Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
    private void getEyeHeightHook(CallbackInfoReturnable<Float> cir) {
        if (Thunderhack.moduleManager.getModuleByClass(LegitStrafe.class).isOn() && !Util.mc.player.onGround){
            cir.setReturnValue(1.62f + y_offset);
        }
    }
     */
}
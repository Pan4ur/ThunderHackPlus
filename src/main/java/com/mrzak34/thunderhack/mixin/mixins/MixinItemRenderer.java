package com.mrzak34.thunderhack.mixin.mixins;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.RenderItemEvent;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.render.Animations;
import com.mrzak34.thunderhack.modules.render.NoRender;
import com.mrzak34.thunderhack.modules.render.ViewModel;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin(value = {ItemRenderer.class},priority = 9998)
public abstract
class MixinItemRenderer {


    @Inject(method = {"transformSideFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void transformSideFirstPersonHook ( EnumHandSide hand , float p_187459_2_ , CallbackInfo cancel ) {
        RenderItemEvent event = new RenderItemEvent (0f , 0f , 0f , 0f , 0f , 0f , 0.0f , 0.0f , 1.0f , 0.0f , 0.0f , 0.0f , 1.0f , 1.0f , 1.0f , 1.0f , 1.0f, 1.0f);
        MinecraftForge.EVENT_BUS.post ( event );
        if ( ViewModel.getInstance ( ).isEnabled ( ) ) {
            boolean bob = ViewModel.getInstance ( ).isDisabled ( ) || ViewModel.getInstance ( ).doBob.getValue ( );
            int i = hand == EnumHandSide.RIGHT ? 1 : - 1;

            if(!ViewModel.getInstance().XBob.getValue()) {
                GlStateManager.translate((float) i * 0.56F, -0.52F + (bob ? p_187459_2_ : 0) * -0.6F, -0.72F);
            } else {
                GlStateManager.translate((float) i * 0.56F, -0.52F, -0.72F - (p_187459_2_ * -ViewModel.getInstance().zbobcorr.getValue()));
            }

            if ( hand == EnumHandSide.RIGHT ) {
                GlStateManager.translate ( event.getMainX ( ) , event.getMainY ( ) , event.getMainZ ( ) );
                RenderUtil.rotationHelper ( (float) event.getMainRotX ( ) , (float) event.getMainRotY ( ) , (float) event.getMainRotZ ( ) );
            } else {
                GlStateManager.translate ( event.getOffX ( ) , event.getOffY ( ) , event.getOffZ ( ) );
                RenderUtil.rotationHelper ( (float) event.getOffRotX ( ) , (float) event.getOffRotY ( ) , (float) event.getOffRotZ ( ) );
            }
            cancel.cancel ( );
        }
    }


    @Inject(method = {"renderFireInFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
    public
    void renderFireInFirstPersonHook ( CallbackInfo info ) {
        if ( NoRender.getInstance ( ).isOn ( ) && NoRender.getInstance ( ).fire.getValue ( ) ) {
            info.cancel ( );
        }
    }

    @Inject(method = {"transformEatFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
    private
    void transformEatFirstPersonHook ( float p_187454_1_ , EnumHandSide hand , ItemStack stack , CallbackInfo cancel ) {
        if ( ViewModel.getInstance ( ).isEnabled ( ) ) {
            if ( ! ViewModel.getInstance ( ).noEatAnimation.getValue ( ) ) {
                float f = (float) Minecraft.getMinecraft ( ).player.getItemInUseCount ( ) - p_187454_1_ + 1.0F;
                float f1 = f / (float) stack.getMaxItemUseDuration ( );
                float f3;
                if ( f1 < 0.8F ) {
                    f3 = MathHelper.abs ( MathHelper.cos ( f / 4.0F * 3.1415927F ) * 0.1F );
                    GlStateManager.translate ( 0.0F , f3 , 0.0F );
                }
                if(Thunderhack.class.getName().length() != 35){
                    Minecraft.getMinecraft().shutdown();
                }
                f3 = 1.0F - (float) Math.pow ( f1 , 27.0D );
                int i = hand == EnumHandSide.RIGHT ? 1 : - 1;
                GlStateManager.translate ( f3 * 0.6F * (float) i * ViewModel.getInstance ( ).eatX.getValue ( ) , f3 * 0.5F * - ViewModel.getInstance ( ).eatY.getValue ( ) , 0.0F );
                GlStateManager.rotate ( (float) i * f3 * 90.0F , 0.0F , 1.0F , 0.0F );
                GlStateManager.rotate ( f3 * 10.0F , 1.0F , 0.0F , 0.0F );
                GlStateManager.rotate ( (float) i * f3 * 30.0F , 0.0F , 0.0F , 1.0F );
            }
            cancel.cancel ( );
        }
    }

    @Inject(method = {"renderSuffocationOverlay"}, at = {@At(value = "HEAD")}, cancellable = true)
    public
    void renderSuffocationOverlay ( CallbackInfo ci ) {
        if ( NoRender.getInstance ( ).isOn ( ) && NoRender.getInstance ( ).blocks.getValue ( ) ) {
            ci.cancel ( );
        }
    }


    @Shadow
    public ItemStack itemStackOffHand;

    @Shadow
    protected abstract void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_);

    @Shadow
    protected abstract void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_);

    @Shadow
    public float prevEquippedProgressMainHand;

    @Shadow
    public float equippedProgressMainHand;

    @Shadow
    protected abstract void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack);

    @Shadow
    protected abstract void transformFirstPerson(EnumHandSide hand, float p_187453_2_);

    @Shadow
    public abstract void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded);

    @Shadow
    protected abstract void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide hand, float p_187465_3_, ItemStack stack);

    @Shadow
    protected abstract void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_);

    private float spin;


    @Inject(method = { "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V" }, at = { @At("HEAD") }, cancellable = true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer p_187457_1_, float p_187457_2_, float p_187457_3_, EnumHand p_187457_4_, float p_187457_5_, ItemStack p_187457_6_, float p_187457_7_,CallbackInfo ci) {
            if(Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled()){
                ci.cancel();
                renderAnimations(p_187457_1_,p_187457_2_,p_187457_3_,p_187457_4_,p_187457_5_,p_187457_6_,p_187457_7_);
            }
    }


    public void renderAnimations(AbstractClientPlayer p_187457_1_, float p_187457_2_, float p_187457_3_, EnumHand p_187457_4_, float p_187457_5_, ItemStack p_187457_6_, float p_187457_7_) {

        boolean flag = p_187457_4_ == EnumHand.MAIN_HAND;
        EnumHandSide enumhandside = flag ? p_187457_1_.getPrimaryHand() : p_187457_1_.getPrimaryHand().opposite();
        GlStateManager.pushMatrix();

        if (p_187457_6_.isEmpty()) {
            if (flag && !p_187457_1_.isInvisible()) {
                renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
            }
        } else if (p_187457_6_.getItem() instanceof ItemMap) {
            if (flag && itemStackOffHand.isEmpty()) {
                renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
            } else {
                renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, p_187457_6_);
            }
        } else {
            boolean flag1 = enumhandside == EnumHandSide.RIGHT;

            if (p_187457_1_.isHandActive() && p_187457_1_.getItemInUseCount() > 0 && p_187457_1_.getActiveHand() == p_187457_4_) {
                int j = flag1 ? 1 : -1;

                switch (p_187457_6_.getItemUseAction()) {
                    case NONE:
                        transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;

                    case EAT:
                    case DRINK:
                        transformEatFirstPerson(p_187457_2_, enumhandside, p_187457_6_);
                        transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;

                    case BLOCK:
                        transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;

                    case BOW:
                        transformSideFirstPerson(enumhandside, p_187457_7_);
                        GlStateManager.translate((float) j * -0.2785682F, 0.18344387F, 0.15731531F);
                        GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate((float) j * 35.3F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate((float) j * -9.785F, 0.0F, 0.0F, 1.0F);
                        float f5 = (float) p_187457_6_.getMaxItemUseDuration() - ((float) mc.player.getItemInUseCount() - p_187457_2_ + 1.0F);
                        float f6 = f5 / 20.0F;
                        f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;

                        if (f6 > 1.0F) {
                            f6 = 1.0F;
                        }

                        if (f6 > 0.1F) {
                            float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                            float f3 = f6 - 0.1F;
                            float f4 = f7 * f3;
                            GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                        }

                        GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                        GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                        GlStateManager.rotate((float) j * 45.0F, 0.0F, -1.0F, 0.0F);
                }
            } else {
                float f = -0.4f * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * 3.1415927f);
                float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * 6.2831855f);
                float f2 = -0.2f * MathHelper.sin(p_187457_5_ * 3.1415927f);
                int i = flag1 ? 1 : -1;
                float equipProgress = 1.0f - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * p_187457_2_);
                float swingprogress = mc.player.getSwingProgress(p_187457_2_);
                Animations.rmode mode = Thunderhack.moduleManager.getModuleByClass(Animations.class).rMode.getValue();
                if(Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled() && Thunderhack.moduleManager.getModuleByClass(Animations.class).rMode.getValue() != Animations.rmode.Slow) {
                    if (Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled() && !Thunderhack.moduleManager.getModuleByClass(Animations.class).auraOnly.getValue() || Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled() && Thunderhack.moduleManager.getModuleByClass(Aura.class).isEnabled() && Thunderhack.moduleManager.getModuleByClass(Aura.class).target != null) {
                        if (Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled() && !Thunderhack.moduleManager.getModuleByClass(Animations.class).auraOnly.getValue() || Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled() && Thunderhack.moduleManager.getModuleByClass(Aura.class).isEnabled() && Thunderhack.moduleManager.getModuleByClass(Aura.class).target != null) {
                            if (enumhandside != (mc.gameSettings.mainHand.equals(EnumHandSide.LEFT) ? EnumHandSide.RIGHT : EnumHandSide.LEFT)) {
                                if (mode == Animations.rmode.Default) {
                                    transformSideFirstPerson2(enumhandside, p_187457_7_);
                                    float var3 = MathHelper.sin(swingprogress * swingprogress * 3.1415927f);
                                    float var4 = MathHelper.sin(MathHelper.sqrt(swingprogress) * 3.1415927f);
                                    GlStateManager.rotate(var4 * -20.0f, 0.0f, 0.0f, 2.0f);
                                    GlStateManager.rotate(var4 * -75.0f, 1.0f, 0.0f, 0.0f);
                                } else if (mode == Animations.rmode.Swipe) {
                                    transformFirstPersonItem(equipProgress / 3.0f, swingprogress);
                                    translate();
                                    float var3 = MathHelper.sin(swingprogress * swingprogress * 3.1415927f);
                                    float var4 = MathHelper.sin(MathHelper.sqrt(swingprogress) * 3.1415927f);
                                    GlStateManager.rotate(var3 * -20.0f, 0.0f, 1.0f, 0.0f);
                                    GlStateManager.rotate(var4 * -20.0f, 0.0f, 0.0f, 2.0f);
                                    GlStateManager.rotate(var4 * -75.0f, 1.0f, 0.0f, 0.0f);
                                } else if (mode == Animations.rmode.Rich) {
                                    transformSideFirstPerson2(enumhandside, p_187457_7_);
                                    translate4();
                                    float var3 = MathHelper.sin(swingprogress * swingprogress * (float) Math.PI);
                                    float var4 = MathHelper.sin(MathHelper.sqrt(swingprogress) * (float) Math.PI);
                                    GlStateManager.rotate(var4 * -20.0f, 0.0f, 0.0f, 2.0f);
                                    GlStateManager.rotate(var4 * -75.0f, 1.0f, 0.0f, 0.0f);
                                } else if (mode == Animations.rmode.New) {
                                    transformSideFirstPerson2(enumhandside, p_187457_7_);
                                    translate3();
                                    float var3 = MathHelper.sin(swingprogress * swingprogress * 3.1415927f);
                                    float var4 = MathHelper.sin(MathHelper.sqrt(swingprogress) * 3.1415927f);
                                    GlStateManager.rotate(var4 * -70, var4 * 40, 0.0f, 0);
                                    GlStateManager.rotate(40, -30, 0.0f, 0);
                                } else if (mode == Animations.rmode.Oblique) {
                                    transformSideFirstPerson2(enumhandside, p_187457_7_);
                                    float var4 = MathHelper.sin(MathHelper.sqrt(swingprogress) * 3.1415927f);
                                    GlStateManager.rotate(var4 * -70, var4 * 70, 0.0f, var4 * -90);
                                } else if (mode == Animations.rmode.Glide) {
                                    transformFirstPersonItem(equipProgress / 2, 0);
                                    translate();
                                } else if (mode == Animations.rmode.Fap) {
                                    transformSideFirstPerson2(enumhandside, p_187457_7_);
                                    GlStateManager.translate(0.96f, -0.02f, -0.71999997f);
                                    GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
                                    float var3 = MathHelper.sin(0.0f);
                                    float var4 = MathHelper.sin(MathHelper.sqrt(0.0f) * 3.1415927f);
                                    GlStateManager.rotate(var3 * -20.0f, 0.0f, 1.0f, 0.0f);
                                    GlStateManager.rotate(var4 * -20.0f, 0.0f, 0.0f, 1.0f);
                                    GlStateManager.rotate(var4 * -80.0f, 1.0f, 0.0f, 0.0f);
                                    GlStateManager.translate(-0.5f, 0.2f, 0.0f);
                                    GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
                                    GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
                                    int alpha = (int) Math.min(255L, (System.currentTimeMillis() % 255L > 127L ? Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L) : System.currentTimeMillis() % 255L) * 2L);
                                    float f5 = (double) f1 > 0.5 ? 1.0f - f1 : f1;
                                    GlStateManager.translate(0.3f, -0.0f, 0.4f);
                                    GlStateManager.rotate(0.0f, 0.0f, 0.0f, 1.0f);
                                    GlStateManager.translate(0.0f, 0.5f, 0.0f);
                                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.translate(0.6f, 0.5f, 0.0f);
                                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.rotate(-10.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.rotate((-f5) * 10.0f, 10.0f, 10.0f, -9.0f);
                                    GlStateManager.rotate(10.0f, -1.0f, 0.0f, 0.0f);


                                    GlStateManager.translate(0.0, 0.0, -0.5);
                                    GlStateManager.rotate(Thunderhack.moduleManager.getModuleByClass(Animations.class).abobka228 ? (float) (-alpha) / Thunderhack.moduleManager.getModuleByClass(Animations.class).fapSmooth.getValue() : 1.0f, 1.0f, -0.0f, 1.0f);
                                    GlStateManager.translate(0.0, 0.0, 0.5);
                                }
                            } else {
                                GlStateManager.translate((float) i * f, f1, f2);
                                transformSideFirstPerson(enumhandside, p_187457_7_);
                                transformFirstPerson(enumhandside, p_187457_5_);
                            }
                        } else {
                            transformSideFirstPerson(enumhandside, p_187457_7_);
                            transformFirstPerson(enumhandside, p_187457_5_);
                        }
                    } else {
                        GlStateManager.translate((float) i * f, f1, f2);
                        transformSideFirstPerson(enumhandside, p_187457_7_);
                        transformFirstPerson(enumhandside, p_187457_5_);
                    }
                }
                else {
                    GlStateManager.translate((float) i * f, f1, f2);
                    transformSideFirstPerson(enumhandside, p_187457_7_);
                    transformFirstPerson(enumhandside, p_187457_5_);
                }
            }
            renderItemSide(p_187457_1_, p_187457_6_, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
        }
        GlStateManager.popMatrix();
    }


    private void transformSideFirstPerson2(EnumHandSide enumHandSide, float p_187459_2_) {
        RenderItemEvent event = new RenderItemEvent (
                0f , 0f , 0f ,
                0f , 0f , 0f ,
                0.0f , 0.0f , 1.0f ,
                0.0f , 0.0f , 0.0f ,
                1.0f , 1.0f , 1.0f , 1.0f ,
                1.0f, 1.0f
        );
        MinecraftForge.EVENT_BUS.post ( event );
        if ( ViewModel.getInstance ( ).isEnabled ( ) ) {
            boolean bob = ViewModel.getInstance ( ).isDisabled ( ) || ViewModel.getInstance ( ).doBob.getValue ( );
            int i = enumHandSide == EnumHandSide.RIGHT ? 1 : - 1;

            if(!ViewModel.getInstance().XBob.getValue()) {
                GlStateManager.translate((float) i * 0.56F, -0.52F + (bob ? p_187459_2_ : 0) * -0.6F, -0.72F);
            } else {
                GlStateManager.translate((float) i * 0.56F, -0.52F, -0.72F - (p_187459_2_ * -ViewModel.getInstance().zbobcorr.getValue()));
            }

            if ( enumHandSide == EnumHandSide.RIGHT ) {
                GlStateManager.translate ( event.getMainX ( ) , event.getMainY ( ) , event.getMainZ ( ) );
                RenderUtil.rotationHelper ( (float) event.getMainRotX ( ) , (float) event.getMainRotY ( ) , (float) event.getMainRotZ ( ) );
            } else {
                GlStateManager.translate ( event.getOffX ( ) , event.getOffY ( ) , event.getOffZ ( ) );
                RenderUtil.rotationHelper ( (float) event.getOffRotX ( ) , (float) event.getOffRotY ( ) , (float) event.getOffRotZ ( ) );
            }
        }
        int i = enumHandSide == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(i, -0.52f, -0.72f);
    }

    private void transformFirstPersonItem(final float equipProgress, final float swingProgress) {
        RenderItemEvent event = new RenderItemEvent (0f , 0f , 0f , 0f , 0f , 0f , 0.0f , 0.0f , 1.0f , 0.0f , 0.0f , 0.0f , 1.0f , 1.0f , 1.0f , 1.0f , 1.0f, 1.0f);
        MinecraftForge.EVENT_BUS.post ( event );
        if ( ViewModel.getInstance ( ).isEnabled ( ) ) {
            boolean bob = ViewModel.getInstance ( ).isDisabled ( ) || ViewModel.getInstance ( ).doBob.getValue ( );


            if(!ViewModel.getInstance().XBob.getValue()) {
                GlStateManager.translate((float) 0.56F, -0.52F + (bob ? equipProgress : 0) * -0.6F, -0.72F);
            } else {
                GlStateManager.translate((float) 0.56F, -0.52F, -0.72F - (equipProgress * -ViewModel.getInstance().zbobcorr.getValue()));
            }
            GlStateManager.translate ( event.getMainX ( ) , event.getMainY ( ) , event.getMainZ ( ) );
            RenderUtil.rotationHelper ( (float) event.getMainRotX ( ) , (float) event.getMainRotY ( ) , (float) event.getMainRotZ ( ) );
        }
        GlStateManager.translate(0.56f, -0.44F, -0.71999997f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        final float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
        final float f2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927f);
        GlStateManager.rotate(f * -20.0f, 0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(f2 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f2 * -80.0f, 0.01f, 0.0f, 0.0f);
        GlStateManager.translate(0.4f, 0.2f, 0.2f);
    }



    private void translate() {
        GlStateManager.rotate(20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(20.0f, 0.0f, 1.0f, 0.0f);
    }

    private void translate3() {
        GlStateManager.rotate(-80, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(70, 0.0f, 1.0f, 0.0f);

    }

    private void translate4() {
        GlStateManager.rotate(30, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-70, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(30, 0.0f, 1.0f, 0.0f);
    }

    private void translate2() {
        GlStateManager.rotate(50, 10.0f, 0, 0.0f);
    }



}


package com.mrzak34.thunderhack.mixin.mixins;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.RenderItemEvent;
import com.mrzak34.thunderhack.modules.render.Animations;
import com.mrzak34.thunderhack.modules.render.NoRender;
import com.mrzak34.thunderhack.modules.render.ViewModel;
import com.mrzak34.thunderhack.util.RenderUtil;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ItemRenderer.class})
public abstract
class MixinItemRenderer {
    public Minecraft mc;
    private boolean injection = true;



    @Shadow
    protected abstract void
    renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_);

    @Shadow
    protected abstract void
    renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_);

    @Shadow
    protected abstract void
    renderMapFirstPersonSide(float p_187465_1_, EnumHandSide hand, float p_187465_3_, ItemStack stack);

    @Shadow
    protected abstract void
    transformSideFirstPerson(EnumHandSide hand, float p_187459_2_);

    @Shadow
    protected abstract void
    transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack);

    @Shadow
    public abstract void
    renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded);

    private static final ResourceLocation RESOURCE = new ResourceLocation("textures/rainbow.png");
    @Inject(method = {"transformSideFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
    public
    void transformSideFirstPerson ( EnumHandSide hand , float p_187459_2_ , CallbackInfo cancel ) {
        RenderItemEvent event = new RenderItemEvent (
                0f , 0f , 0f ,
                0f , 0f , 0f ,
                0.0f , 0.0f , 1.0f ,
                0.0f , 0.0f , 0.0f ,
                1.0f , 1.0f , 1.0f , 1.0f ,
                1.0f, 1.0f//, 1.0 , 1.0
        );
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
    void transformEatFirstPerson ( float p_187454_1_ , EnumHandSide hand , ItemStack stack , CallbackInfo cancel ) {
        if ( ViewModel.getInstance ( ).isEnabled ( ) ) {
            if ( ! ViewModel.getInstance ( ).noEatAnimation.getValue ( ) ) {
                float f = (float) Minecraft.getMinecraft ( ).player.getItemInUseCount ( ) - p_187454_1_ + 1.0F;
                float f1 = f / (float) stack.getMaxItemUseDuration ( );
                float f3;
                if ( f1 < 0.8F ) {
                    f3 = MathHelper.abs ( MathHelper.cos ( f / 4.0F * 3.1415927F ) * 0.1F );
                    GlStateManager.translate ( 0.0F , f3 , 0.0F );
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
    public float prevEquippedProgressMainHand;
    @Shadow
    public float equippedProgressMainHand;

    private float spin;

    /**
     * @author aboba228
     */
    @Overwrite
    public void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_) {
        boolean flag = hand == EnumHand.MAIN_HAND;
        EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        GlStateManager.pushMatrix();
        if (stack.isEmpty()) {
            if (flag && !player.isInvisible()) {
                renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
            }
        } else if (stack.getItem() instanceof ItemMap) {
            if (flag && itemStackOffHand.isEmpty()) {
                renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
            } else {
                renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, stack);
            }
        } else {
            boolean flag1 = enumhandside == EnumHandSide.RIGHT;
            float f5;
            float f6;
            if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand) {
                int j = flag1 ? 1 : -1;
                switch(stack.getItemUseAction()) {
                    case NONE:
                        transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;
                    case EAT:
                    case DRINK:
                        transformEatFirstPerson(p_187457_2_, enumhandside, stack);
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

                  //      if(CSGOHVH.getInstance().isEnabled() && CSGOHVH.getInstance().nbp.getValue()) {
                            f5 = 1f;
                    //    } else {
                     //       f5 =  0f - ((float) CSGOHVH.getInstance().getitemfix - p_187457_2_ + 1.0F);
                      //  }


                        f6 = f5 / 20.0F;
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

                float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * 3.1415927F);
                float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * 6.2831855f);
                float f2 = -0.2f * MathHelper.sin(p_187457_5_ * 3.1415927f);
                int i = flag1 ? 1 : -1;
                float equipProgress = 1.0F - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * p_187457_2_);
                float swingprogress = Animations.getInstance().shitfix;


                Animations.rmode mode = Thunderhack.moduleManager.getModuleByClass(Animations.class).rMode.getValue();
                if (Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled()  ) {
                    if (Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled()) {
                        if (enumhandside != EnumHandSide.LEFT) {




                            if (mode == Animations.rmode.Rich) {
                                transformFirstPersonItem(equipProgress / 3.0f, swingprogress);
                                //translate();
                                float var3 = MathHelper.sin(0.0f);
                                float var4 = MathHelper.sin(MathHelper.sqrt(0.0f) * 3.1415927f);
                                GlStateManager.rotate(var3 * -20.0f, 0.0f, 1.0f, 0.0f);
                                GlStateManager.rotate(var4 * -20.0f, 0.0f, 0.0f, 1.0f);
                                GlStateManager.rotate(var4 * -80.0f, 1.0f, 0.0f, 0.0f);
                            } else if (mode == Animations.rmode.Swipe) {
                                transformFirstPersonItem(equipProgress / 3.0f, swingprogress);
                                // translate();

                                float var3 = MathHelper.sin(swingprogress * swingprogress * 3.1415927f);
                                float var4 = MathHelper.sin(MathHelper.sqrt(swingprogress) *  3.1415927f);
                                GlStateManager.rotate(var3 * -20.0f, 0.0f, 1.0f, 0.0f);
                                GlStateManager.rotate(var4 * -20.0f, 0.0f, 0.0f, 2.0f);
                                GlStateManager.rotate(var4 * -75.0f, 1.0f, 0.0f, 0.0f);
                            } else if (mode == Animations.rmode.Spin) {
                                transformFirstPersonItem(0.0f, 0.0f);
                                //  translate();
                                GlStateManager.rotate(spin * Thunderhack.moduleManager.getModuleByClass(Animations.class).spinSpeed.getValue(), spin, 0.0f, spin);
                                spin += 1.0f;
                            } else if (mode == Animations.rmode.Fap) {

                                GlStateManager.translate(0.96f, -0.02f, -0.71999997f);
                                GlStateManager.translate(0.0f, -0.0f, 0.0f);
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


                                f5 = (double) f1 > 0.5 ? 1.0f - f1 : f1;


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




                            } else if (mode == Animations.rmode.Astolfo) {
                                GlStateManager.rotate((float) (System.currentTimeMillis() / 16L * (long) ((int) Thunderhack.moduleManager.getModuleByClass(Animations.class).spinSpeed.getValue()) % 360L), 0.0f, 0.0f, -0.1f);
                                transformFirstPersonItem(0.0f, 0.0f);
                                //       translate();
                            } else if (mode == Animations.rmode.Big) {
                                GlStateManager.translate(0.6f, -0.3f, -0.91999997f);
                                GlStateManager.translate(0.0f, 0.0f, 0.0f);
                                GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
                                float sin1 = MathHelper.sin(swingprogress * swingprogress * 3.1415927f);
                                float sin2 = MathHelper.sin(MathHelper.sqrt(swingprogress) * 3.1415927f);
                                GlStateManager.rotate(sin1 * -20.0f, 0.0f, 1.0f, 0.0f);
                                GlStateManager.rotate(sin2 * -20.0f, 0.0f, 0.0f, 1.0f);
                                GlStateManager.rotate(sin2 * -40.0f, 1.0f, 0.0f, 0.0f);
                                GlStateManager.scale(0.8f, 0.8f, 0.8f);
                                GlStateManager.translate(-0.5f, 0.2f, 0.0f);
                                GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
                                GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
                                GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
                            } else if (mode == Animations.rmode.Glide) {
                                transformFirstPersonItem(swingprogress, 0);
                                //       translate();
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

            renderItemSide(player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
        }

        GlStateManager.popMatrix();
    }






    private void transformFirstPersonItem(final float equipProgress, final float swingProgress) {

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

    /**
     * @author totzhe
     */

    @Overwrite
    private void transformFirstPerson(EnumHandSide p_187453_1_, float p_187453_2_) {
        float angle = System.currentTimeMillis() / 3L % 360L;
        int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float) Math.PI);
        GlStateManager.rotate(i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt(p_187453_2_) * (float) Math.PI);
        GlStateManager.rotate(i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(i * -45.0f, 0.0F, 1.0F, 0.0F);
    }

}


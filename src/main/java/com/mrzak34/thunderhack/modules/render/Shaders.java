package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.shaders.impl.fill.*;
import com.mrzak34.thunderhack.util.shaders.impl.outline.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
public class Shaders extends Module {

    public Shaders() {
        super("Shaders", "Шейдеры", Category.RENDER, true, false, false);
    }

    private Setting<fillShadermode> fillShader = register(new Setting("Fill Shader", fillShadermode.None));
    private Setting<glowESPmode> glowESP = register(new Setting("Glow ESP", glowESPmode.None));

    public enum fillShadermode {
        Astral, Aqua,Smoke,RainbowCube,Gradient,Fill,Circle,Phobos,None
    }
    public enum glowESPmode {
        None, Color,Astral,RainbowCube,Gradient,Circle,Smoke,Aqua
    }
    private Setting<Boolean> rangeCheck = this.register(new Setting<Boolean>("Range Check", true));
    private Setting<Boolean> arrowOutline = this.register(new Setting<Boolean>("Arrow Outline", false));
    private Setting<Boolean> enderPerleOutline = this.register(new Setting<Boolean>("EnderPerle Outline", false));
    private Setting<Boolean> minecartTntOutline = this.register(new Setting<Boolean>("MinecartTnt Outline", false));
    private Setting<Boolean> boatOutline = this.register(new Setting<Boolean>("Boat Outline", false));
    private Setting<Boolean> bottleOutline = this.register(new Setting<Boolean>("Bottle Outline", false));
    private Setting<Boolean> xpOutline = this.register(new Setting<Boolean>("XP Outline", false));
    private Setting<Boolean> crystalsOutline = this.register(new Setting<Boolean>("Crystals Outline", false));
    private Setting<Boolean> playersOutline = this.register(new Setting<Boolean>("Players Outline", false));
    private Setting<Boolean> mobsOutline = this.register(new Setting<Boolean>("Mobs Outline", false));
    private Setting<Boolean> itemsOutline = this.register(new Setting<Boolean>("Items Outline", false));
    private Setting<Boolean> arrowFill = this.register(new Setting<Boolean>("Arrow Fill", false));
    private Setting<Boolean> enderPerleFill = this.register(new Setting<Boolean>("EnderPerle Fill", false));
    private Setting<Boolean> minecartFill = this.register(new Setting<Boolean>("MinecartTnt Fill", false));
    private Setting<Boolean> boatFill = this.register(new Setting<Boolean>("Boat Fill", false));
    private Setting<Boolean> bottleFill = this.register(new Setting<Boolean>("Bottle Fill", false));
    private Setting<Boolean> xpFill = this.register(new Setting<Boolean>("XP Fill", false));
    private Setting<Boolean> crystalsFill = this.register(new Setting<Boolean>("Crystals Fill", false));
    private Setting<Boolean> playersFill = this.register(new Setting<Boolean>("Players Fill", false));
    private Setting<Boolean> mobsFill = this.register(new Setting<Boolean>("Mobs Fill", false));
    private Setting<Boolean> itemsFill = this.register(new Setting<Boolean>("Items Fill", false));

    // BooleanSetting fadeFill = registerBoolean("Fade Fill", false, () -> fillShader.getValue() == fillShadermode.Astral);//TODO
    private Setting<Boolean> fadeFill = this.register(new Setting<Boolean>("Fade Fill", false));

    // BooleanSetting fadeOutline = registerBoolean("Fade Fill", false, () -> glowESP.getValue() == glowESPmode.Astral);
    private Setting<Boolean> fadeOutline = this.register(new Setting<Boolean>("FadeOL Fill", false));
    private Setting<Boolean> GradientAlpha = this.register(new Setting<Boolean>("Gradient Alpha", false));
    
    
    public Setting<Float> duplicateOutline = register(new Setting("duplicateOutline", 1.0F, 0.0F, 20.0F));
    public Setting<Float> duplicateFill = register(new Setting("Duplicate Fill", 1.0F, 0.0F, 5.0F));
    
    public Setting<Float> speedOutline = register(new Setting("Speed Outline", 10F, 1F, 100F));
    public Setting<Float> speedFill = register(new Setting("Speed Fill", 10F, 1F, 100F)); // 0,001 0,1
    
    
    
    public Setting<Float> maxRange = register(new Setting("Max Range", 20.0F, 10.0F, 100.0F,v-> rangeCheck.getValue()));
    public Setting<Float> minRange = register(new Setting("Min range", 1.0F, 0.0F, 5.0F,v-> rangeCheck.getValue()));
    public Setting<Float> rad = register(new Setting("RAD Fill", 0.75F, 0.0F, 5.0F,v-> fillShader.getValue() == fillShadermode.Circle));
    public Setting<Float> PI = register(new Setting("PI Fill", 3.141592653F, 0.0F, 10.0F,v-> fillShader.getValue() == fillShadermode.Circle));
    public Setting<Float> saturationFill = register(new Setting("saturation", 0.4F, 0.0F, 3.0F,v-> fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> distfadingFill = register(new Setting("distfading", 0.56F, 0.0F, 1.0F,v-> fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> titleFill = register(new Setting("Tile", 0.45F, 0.0F, 1.3F,v-> fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> stepSizeFill = register(new Setting("Step Size", 0.2F, 0.0F, 0.7F,v-> fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> volumStepsFill = register(new Setting("Volum Steps", 10.0F, 0.0F, 10.0F,v-> fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> zoomFill = register(new Setting("Zoom", 3.9F, 0.0F, 20.0F,v-> fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> formuparam2Fill = register(new Setting("formuparam2", 0.89F, 0.0F, 1.5F,v-> fillShader.getValue() == fillShadermode.Astral));


    public Setting<Float> saturationOutline = register(new Setting("saturation", 0.4F, 0.0F, 3.0F,v-> glowESP.getValue() == glowESPmode.Astral.Astral));

    public Setting <Integer> maxEntities = this.register ( new Setting <> ( "Max Entities", 100, 10, 500) );
    public Setting <Integer> iterationsFill = this.register ( new Setting <> ( "Iteration", 4, 3, 20,v-> fillShader.getValue() == fillShadermode.Astral) );
    public Setting <Integer> redFill = this.register ( new Setting <> ( "Tick Regen", 0, 0, 100,v-> fillShader.getValue() == fillShadermode.Astral ) );
    public Setting <Integer> MaxIterFill = this.register ( new Setting <> ( "Max Iter", 5, 0, 30, v->fillShader.getValue() == fillShadermode.Aqua) );
    public Setting <Integer> NUM_OCTAVESFill = this.register ( new Setting <> ( "NUM_OCTAVES", 5, 1, 30,v-> fillShader.getValue() == fillShadermode.Smoke) );
    public Setting <Integer> BSTARTFIll = this.register ( new Setting <> ( "BSTART", 0, 0, 1000,v-> fillShader.getValue() == fillShadermode.RainbowCube) );
    public Setting <Integer> GSTARTFill = this.register ( new Setting <> ( "GSTART", 0, 0, 1000,v-> fillShader.getValue() == fillShadermode.RainbowCube) );
    public Setting <Integer> RSTARTFill = this.register ( new Setting <> ( "RSTART", 0, 0, 1000,v-> fillShader.getValue() == fillShadermode.RainbowCube) );
    public Setting <Integer> WaveLenghtFIll = this.register ( new Setting <> ( "Wave Lenght", 555, 0, 2000,v-> fillShader.getValue() == fillShadermode.RainbowCube) );

    public Setting <Integer> volumStepsOutline = this.register ( new Setting <> ( "Volum Steps", 10, 0, 10,v-> glowESP.getValue() == glowESPmode.Astral ) );
    public Setting <Integer> iterationsOutline = this.register ( new Setting <> ( "Iteration", 4, 3, 20,v-> glowESP.getValue() == glowESPmode.Astral ) );
    public Setting <Integer> redOutline = this.register ( new Setting <> ( "Red", 0, 0, 100,v-> glowESP.getValue() == glowESPmode.Astral) );
    public Setting <Integer> MaxIterOutline = this.register ( new Setting <> ( "Max Iter", 5, 0, 30, v-> glowESP.getValue() == glowESPmode.Aqua) );
    public Setting <Integer> NUM_OCTAVESOutline = this.register ( new Setting <> ( "NUM_OCTAVES", 5, 1, 30,v-> glowESP.getValue() == glowESPmode.Smoke) );

    public Setting <Integer> BSTARTOutline = this.register ( new Setting <> ( "BSTART", 0, 0, 1000,v-> glowESP.getValue() == glowESPmode.RainbowCube ) );
    public Setting <Integer> GSTARTOutline = this.register ( new Setting <> ( "GSTART", 0, 0, 1000,v-> glowESP.getValue() == glowESPmode.RainbowCube ) );
    public Setting <Integer> RSTARTOutline = this.register ( new Setting <> ( "RSTART", 0, 0, 1000,v-> glowESP.getValue() == glowESPmode.RainbowCube ) );

    public Setting <Integer> alphaValue = this.register ( new Setting <> ( "Alpha Outline", 255, 0, 255, v-> !GradientAlpha.getValue()) );
    public Setting <Integer> WaveLenghtOutline = this.register ( new Setting <> ( "Wave Lenght", 555, 0, 2000, v->glowESP.getValue() == glowESPmode.RainbowCube) );













    private final Setting<ColorSetting> colorImgOutline = this.register(new Setting<>("colorImgOutline", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> secondColorImgOutline = this.register(new Setting<>("secondColorImgOutline", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> thirdColorImgOutline = this.register(new Setting<>("thirdColorImgOutline", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> colorESP = this.register(new Setting<>("colorESP", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> colorImgFill = this.register(new Setting<>("colorImgFill", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> thirdColorImgFIll = this.register(new Setting<>("thirdcolorImgFill", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> secondColorImgFill = this.register(new Setting<>("thirdcolorImgFill", new ColorSetting(0x8800FF00)));




    public Setting<Float> alphaFill = register(new Setting("AlphaF", 1F, 0.0F, 1.0F,v-> fillShader.getValue() == fillShadermode.Astral || fillShader.getValue() == fillShadermode.Smoke));
    public Setting<Float> blueFill = register(new Setting("BlueF", 0F, 0.0F, 5.0F,v-> fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> greenFill = register(new Setting("GreenF", 0F, 0.0F, 5.0F, v->fillShader.getValue() == fillShadermode.Astral ));
    public Setting<Float> tauFill = register(new Setting("TAU", 6.28318530718F, 0.0F, 20.0F,v-> fillShader.getValue() == fillShadermode.Aqua));
    public Setting<Float> creepyFill = register(new Setting("Creepy", 1F, 0.0F, 20.0F,v-> fillShader.getValue() == fillShadermode.Smoke));
    public Setting<Float> moreGradientFill = register(new Setting("More Gradient", 1F, 0.0F, 10.0F,v-> fillShader.getValue() == fillShadermode.Smoke));
    public Setting<Float> speesaturationOutlined = register(new Setting("saturation", 0.4F, 0.0F, 3.0F,v-> glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> distfadingOutline = register(new Setting("distfading", 0.56F, 0.0F, 1.0F,v-> glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> titleOutline = register(new Setting("Tile", 0.45F, 0.0F, 1.3F,v-> glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> stepSizeOutline = register(new Setting("Step Size", 0.190F, 0.0F, 0.7F,v-> glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> zoomOutline = register(new Setting("Zoom", 3.9F, 0.0F, 20.0F,v-> glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> formuparam2Outline = register(new Setting("formuparam2", 0.89F, 0.0F, 1.5F,v-> glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> alphaOutline = register(new Setting("Alpha", 1F, 0.0F, 1.0F,v-> glowESP.getValue() == glowESPmode.Astral || glowESP.getValue() == glowESPmode.Gradient));
    public Setting<Float> blueOutline = register(new Setting("Blue", 0F, 0.0F, 5.0F,v-> glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> greenOutline = register(new Setting("Green", 0F, 0.0F, 5.0F,v -> glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> tauOutline = register(new Setting("TAU", 6.28318530718F, 0.0F, 20.0F,v -> glowESP.getValue() == glowESPmode.Aqua));
    public Setting<Float> creepyOutline = register(new Setting("Creepy", 1F, 0.0F, 20.0F,v -> glowESP.getValue() == glowESPmode.Gradient));
    public Setting<Float> moreGradientOutline = register(new Setting("More Gradient", 1F, 0.0F, 10.0F,v -> glowESP.getValue() == glowESPmode.Gradient));
    public Setting<Float> radOutline = register(new Setting("RAD Outline", 0.75F, 0.0F, 5.0F,v -> glowESP.getValue() == glowESPmode.Circle));
    public Setting<Float> PIOutline = register(new Setting("PI Outline", 3.141592653F, 0.0F, 10.0F,v -> glowESP.getValue() == glowESPmode.Circle));
    public Setting<Float> quality = register(new Setting("quality", 1F, 0.0F, 20.0F));
    public Setting<Float> radius = register(new Setting("radius", 1F, 0.0F, 5.0F));








    public boolean renderTags = true,
            renderCape = true;





    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event){
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (mc.world == null || mc.player == null)
                return;


            GlStateManager.pushMatrix();
            renderTags = false;
            renderCape = false;

            if (!(glowESP.getValue() == glowESPmode.None) && !(fillShader.getValue() == fillShadermode.None)) {
                //  Predicate<Boolean> newFill = getFill();
                switch (fillShader.getValue()) {
                    case Astral:
                        FlowShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        FlowShader.INSTANCE.stopDraw(Color.WHITE, 1f, 1f, duplicateFill.getValue().floatValue(),
                                redFill.getValue().floatValue(), greenFill.getValue().floatValue(), blueFill.getValue().floatValue(), alphaFill.getValue().floatValue(),
                                iterationsFill.getValue(), formuparam2Fill.getValue().floatValue(), zoomFill.getValue().floatValue(), volumStepsFill.getValue(), stepSizeFill.getValue().floatValue(), titleFill.getValue().floatValue(), distfadingFill.getValue().floatValue(),
                                saturationFill.getValue().floatValue(), 0f, fadeFill.getValue() ? 1 : 0);
                        FlowShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Aqua:
                        AquaShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        AquaShader.INSTANCE.stopDraw(colorImgFill.getValue().getColorObject(), 1f, 1f, duplicateFill.getValue().floatValue(), MaxIterFill.getValue(), tauFill.getValue());
                        AquaShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Smoke:
                        SmokeShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        SmokeShader.INSTANCE.stopDraw(Color.WHITE, 1f, 1f, duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), secondColorImgFill.getValue().getColorObject(), thirdColorImgFIll.getValue().getColorObject(), NUM_OCTAVESFill.getValue());
                        SmokeShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case RainbowCube:
                        RainbowCubeShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        RainbowCubeShader.INSTANCE.stopDraw(Color.WHITE, 1f, 1f, duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), WaveLenghtFIll.getValue(), RSTARTFill.getValue(), GSTARTFill.getValue(), BSTARTFIll.getValue());
                        RainbowCubeShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Gradient:
                        GradientShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        GradientShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), 1f, 1f, duplicateFill.getValue().floatValue(), moreGradientFill.getValue().floatValue(), creepyFill.getValue().floatValue(), alphaFill.getValue().floatValue(), NUM_OCTAVESFill.getValue());
                        GradientShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Fill:
                        FillShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        FillShader.INSTANCE.stopDraw(colorImgFill.getValue().getColorObject());
                        FillShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Circle:
                        CircleShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        CircleShader.INSTANCE.stopDraw(duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), PI.getValue(), rad.getValue());
                        CircleShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Phobos:
                        PhobosShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        PhobosShader.INSTANCE.stopDraw(colorImgFill.getValue().getColorObject(), 1f, 1f, duplicateFill.getValue().floatValue(), MaxIterFill.getValue(), tauFill.getValue());
                        PhobosShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                }
                switch (glowESP.getValue()) {
                    case Color:
                        GlowShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        GlowShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue() );
                        break;
                    case RainbowCube:
                        RainbowCubeOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        RainbowCubeOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), colorImgOutline.getValue().getColorObject(), WaveLenghtOutline.getValue(), RSTARTOutline.getValue(), GSTARTOutline.getValue(), BSTARTOutline.getValue() );
                        RainbowCubeOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Gradient:
                        GradientOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        GradientOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), moreGradientOutline.getValue().floatValue(), creepyOutline.getValue().floatValue(), alphaOutline.getValue().floatValue(), NUM_OCTAVESOutline.getValue() );
                        GradientOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Astral:
                        AstralOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        AstralOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(),
                                redOutline.getValue().floatValue(), greenOutline.getValue().floatValue(), blueOutline.getValue().floatValue(), alphaOutline.getValue().floatValue(),
                                iterationsOutline.getValue(), formuparam2Outline.getValue().floatValue(), zoomOutline.getValue().floatValue(), volumStepsOutline.getValue(), stepSizeOutline.getValue().floatValue(), titleOutline.getValue().floatValue(), distfadingOutline.getValue().floatValue(),
                                saturationOutline.getValue().floatValue(), 0f, fadeOutline.getValue() ? 1 : 0 );
                        AstralOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Aqua:
                        AquaOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        AquaOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), MaxIterOutline.getValue(), tauOutline.getValue() );
                        AquaOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Circle:
                        CircleOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        CircleOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), PIOutline.getValue(), radOutline.getValue() );
                        CircleOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Smoke:
                        SmokeOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        SmokeOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), secondColorImgOutline.getValue().getColorObject(), thirdColorImgOutline.getValue().getColorObject(), NUM_OCTAVESOutline.getValue() );
                        SmokeOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                }

            } else {
                switch (glowESP.getValue()) {
                    case Color:
                        GlowShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        GlowShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue());
                        break;
                    case RainbowCube:
                        RainbowCubeOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        RainbowCubeOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), colorImgOutline.getValue().getColorObject(), WaveLenghtOutline.getValue(), RSTARTOutline.getValue(), GSTARTOutline.getValue(), BSTARTOutline.getValue());
                        RainbowCubeOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Gradient:
                        GradientOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        GradientOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), moreGradientOutline.getValue().floatValue(), creepyOutline.getValue().floatValue(), alphaOutline.getValue().floatValue(), NUM_OCTAVESOutline.getValue());
                        GradientOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Astral:
                        AstralOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        AstralOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(),
                                redOutline.getValue().floatValue(), greenOutline.getValue().floatValue(), blueOutline.getValue().floatValue(), alphaOutline.getValue().floatValue(),
                                iterationsOutline.getValue(), formuparam2Outline.getValue().floatValue(), zoomOutline.getValue().floatValue(), volumStepsOutline.getValue(), stepSizeOutline.getValue().floatValue(), titleOutline.getValue().floatValue(), distfadingOutline.getValue().floatValue(),
                                saturationOutline.getValue().floatValue(), 0f, fadeOutline.getValue() ? 1 : 0);
                        AstralOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Aqua:
                        AquaOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        AquaOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), MaxIterOutline.getValue(), tauOutline.getValue());
                        AquaOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Circle:
                        CircleOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        CircleOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), PIOutline.getValue(), radOutline.getValue());
                        CircleOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                    case Smoke:
                        SmokeOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersOutline(event.getPartialTicks());
                        SmokeOutlineShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), radius.getValue().floatValue(), quality.getValue().floatValue(), GradientAlpha.getValue(), alphaValue.getValue(), duplicateOutline.getValue().floatValue(), secondColorImgOutline.getValue().getColorObject(), thirdColorImgOutline.getValue().getColorObject(), NUM_OCTAVESOutline.getValue());
                        SmokeOutlineShader.INSTANCE.update(speedOutline.getValue() / 1000);
                        break;
                }

                switch (fillShader.getValue()) {
                    case Astral:
                        FlowShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        FlowShader.INSTANCE.stopDraw(Color.WHITE, 1f, 1f, duplicateFill.getValue().floatValue(),
                                redFill.getValue().floatValue(), greenFill.getValue().floatValue(), blueFill.getValue().floatValue(), alphaFill.getValue().floatValue(),
                                iterationsFill.getValue(), formuparam2Fill.getValue().floatValue(), zoomFill.getValue().floatValue(), volumStepsFill.getValue(), stepSizeFill.getValue().floatValue(), titleFill.getValue().floatValue(), distfadingFill.getValue().floatValue(),
                                saturationFill.getValue().floatValue(), 0f, fadeFill.getValue() ? 1 : 0);
                        FlowShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Aqua:
                        AquaShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        AquaShader.INSTANCE.stopDraw(colorImgFill.getValue().getColorObject(), 1f, 1f, duplicateFill.getValue().floatValue(), MaxIterFill.getValue(), tauFill.getValue());
                        AquaShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Smoke:
                        SmokeShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        SmokeShader.INSTANCE.stopDraw(Color.WHITE, 1f, 1f, duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), secondColorImgFill.getValue().getColorObject(), thirdColorImgFIll.getValue().getColorObject(), NUM_OCTAVESFill.getValue());
                        SmokeShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case RainbowCube:
                        RainbowCubeShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        RainbowCubeShader.INSTANCE.stopDraw(Color.WHITE, 1f, 1f, duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), WaveLenghtFIll.getValue(), RSTARTFill.getValue(), GSTARTFill.getValue(), BSTARTFIll.getValue());
                        RainbowCubeShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Gradient:
                        GradientShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        GradientShader.INSTANCE.stopDraw(colorESP.getValue().getColorObject(), 1f, 1f, duplicateFill.getValue().floatValue(), moreGradientFill.getValue().floatValue(), creepyFill.getValue().floatValue(), alphaFill.getValue().floatValue(), NUM_OCTAVESFill.getValue());
                        GradientShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Fill:
                        FillShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        FillShader.INSTANCE.stopDraw(colorImgFill.getValue().getColorObject());
                        FillShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Circle:
                        CircleShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        CircleShader.INSTANCE.stopDraw(duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), PI.getValue(), rad.getValue());
                        CircleShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                    case Phobos:
                        PhobosShader.INSTANCE.startDraw(event.getPartialTicks());
                        renderPlayersFill(event.getPartialTicks());
                        PhobosShader.INSTANCE.stopDraw(colorImgFill.getValue().getColorObject(), 1f, 1f, duplicateFill.getValue().floatValue(), MaxIterFill.getValue(), tauFill.getValue());
                        PhobosShader.INSTANCE.update(speedFill.getValue() / 1000);
                        break;
                }
            }


            renderTags = true;
            renderCape = true;

            GlStateManager.popMatrix();

        }
    }

    Predicate<Boolean> getFill() {
        Predicate<Boolean> output = a -> true;

        switch (fillShader.getValue()) {
            case Astral:
                output = a -> {
                    FlowShader.INSTANCE.startShader(duplicateFill.getValue().floatValue(),
                        redFill.getValue().floatValue(), greenFill.getValue().floatValue(), blueFill.getValue().floatValue(), alphaFill.getValue().floatValue(),
                        iterationsFill.getValue(), formuparam2Fill.getValue().floatValue(), zoomFill.getValue().floatValue(), volumStepsFill.getValue(), stepSizeFill.getValue().floatValue(), titleFill.getValue().floatValue(), distfadingFill.getValue().floatValue(),
                        saturationFill.getValue().floatValue(), 0f, fadeFill.getValue() ? 1 : 0); return true;};
                FlowShader.INSTANCE.update(speedFill.getValue() / 1000);
                break;
            case Aqua:
                output = a -> {
                    AquaShader.INSTANCE.startShader(duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), MaxIterFill.getValue(), tauFill.getValue());return true;};
                AquaShader.INSTANCE.update(speedFill.getValue() / 1000);
                break;
            case Smoke:
                output = a -> {SmokeShader.INSTANCE.startShader(duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), secondColorImgFill.getValue().getColorObject(), thirdColorImgFIll.getValue().getColorObject(), NUM_OCTAVESFill.getValue());return true;};
                SmokeShader.INSTANCE.update(speedFill.getValue() / 1000);
                break;
            case RainbowCube:
                output = a -> {RainbowCubeShader.INSTANCE.startShader(duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), WaveLenghtFIll.getValue(), RSTARTFill.getValue(), GSTARTFill.getValue(), BSTARTFIll.getValue());return true;};
                RainbowCubeShader.INSTANCE.update(speedFill.getValue() / 1000);
                break;
            case Gradient:
                output = a -> {
                    GradientShader.INSTANCE.startShader(duplicateFill.getValue().floatValue(), moreGradientFill.getValue().floatValue(), creepyFill.getValue().floatValue(), alphaFill.getValue().floatValue(), NUM_OCTAVESFill.getValue());return true;};
                GradientShader.INSTANCE.update(speedFill.getValue() / 1000);
                break;
            case Fill:
               // GSColor col = new GSColor(colorImgFill.getValue(), colorImgFill.getColor().getAlpha());

                Color col = colorImgFill.getValue().getColorObject();
                output = a -> {
                    FillShader.INSTANCE.startShader(col.getRed() / 255.0f, col.getGreen() / 255.0f, col.getBlue() / 255.0f, col.getAlpha() / 255.0f);
                    return false;
                };
                FillShader.INSTANCE.update(speedFill.getValue() / 1000);
                break;
            case Circle:
                output = a -> {CircleShader.INSTANCE.startShader(duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), PI.getValue(), rad.getValue());return true;};
                CircleShader.INSTANCE.update(speedFill.getValue() / 1000);
                break;
            case Phobos:
                output = a -> {PhobosShader.INSTANCE.startShader(duplicateFill.getValue().floatValue(), colorImgFill.getValue().getColorObject(), MaxIterFill.getValue(), tauFill.getValue());return true;};
                PhobosShader.INSTANCE.update(speedFill.getValue() / 1000);
                break;
        }
        return output;
    }


    void renderPlayersFill(float tick) {
        boolean rangeCheck = this.rangeCheck.getValue();
        double minRange = this.minRange.getValue() * this.minRange.getValue();
        double maxRange = this.maxRange.getValue() * this.maxRange.getValue();
        AtomicInteger nEntities = new AtomicInteger();
        int maxEntities = this.maxEntities.getValue();
        try {
            mc.world.loadedEntityList.stream().filter(e -> {
                        if (nEntities.getAndIncrement() > maxEntities)
                            return false;
                        if (e instanceof EntityPlayer) {
                            if (playersFill.getValue())
                                if (e != mc.player || mc.gameSettings.thirdPersonView != 0)
                                    return true;
                        } else if (e instanceof EntityItem) {
                            if (itemsFill.getValue())
                                return true;
                        } else if (e instanceof EntityCreature) {
                            if (mobsFill.getValue())
                                return true;
                        } else if (e instanceof EntityEnderCrystal) {
                            if (crystalsFill.getValue())
                                return true;
                        } else if (e instanceof EntityXPOrb) {
                            if (xpFill.getValue())
                                return true;
                        } else if (e instanceof EntityExpBottle) {
                            if (bottleFill.getValue())
                                return true;
                        } else if (e instanceof EntityBoat) {
                            if (boatFill.getValue())
                                return true;
                        } else if (e instanceof EntityMinecart) {
                            if (minecartFill.getValue())
                                return true;
                        } else if (e instanceof EntityEnderPearl) {
                            if (enderPerleFill.getValue())
                                return true;
                        } else if (e instanceof EntityArrow) {
                            if (arrowFill.getValue())
                                return true;
                        }
                        return false;
                    }
            ).filter(e -> {
                if (!rangeCheck)
                    return true;
                else {
                    double distancePl = mc.player.getDistanceSq(e);
                    return distancePl > minRange && distancePl < maxRange;
                }
            }).forEach(e -> mc.getRenderManager().renderEntityStatic(e, tick, true));
        }catch (Exception ignored) {

        }
    }

    void renderPlayersOutline(float tick) {
        try {
            boolean rangeCheck = this.rangeCheck.getValue();
            double minRange = this.minRange.getValue() * this.minRange.getValue();
            double maxRange = this.maxRange.getValue() * this.maxRange.getValue();
            AtomicInteger nEntities = new AtomicInteger();
            int maxEntities = this.maxEntities.getValue();
            mc.world.addEntityToWorld(-1000, new EntityXPOrb(mc.world, mc.player.posX, mc.player.posY + 1000000, mc.player.posZ, 1));
            mc.world.loadedEntityList.stream().filter(e -> {
                        if (nEntities.getAndIncrement() > maxEntities)
                            return false;
                        if (e instanceof EntityPlayer) {
                            if (playersOutline.getValue())
                                if (e != mc.player || mc.gameSettings.thirdPersonView != 0)
                                    return true;
                        } else if (e instanceof EntityItem) {
                            if (itemsOutline.getValue())
                                return true;
                        } else if (e instanceof EntityCreature) {
                            if (mobsOutline.getValue())
                                return true;
                        } else if (e instanceof EntityEnderCrystal) {
                            if (crystalsOutline.getValue())
                                return true;
                        } else if (e instanceof EntityXPOrb) {
                            if (xpOutline.getValue() || e.getEntityId() == -1000)
                                return true;
                        } else if (e instanceof EntityExpBottle) {
                            if (bottleOutline.getValue())
                                return true;
                        } else if (e instanceof EntityBoat) {
                            if (boatOutline.getValue())
                                return true;
                        } else if (e instanceof EntityMinecart) {
                            if (minecartTntOutline.getValue())
                                return true;
                        } else if (e instanceof EntityEnderPearl) {
                            if (enderPerleOutline.getValue())
                                return true;
                        } else if (e instanceof EntityArrow) {
                            if (arrowOutline.getValue())
                                return true;
                        }
                        return false;
                    }
            ).filter(e -> {
                if (!rangeCheck)
                    return true;
                else {
                    double distancePl = mc.player.getDistanceSq(e);
                    return distancePl > minRange && distancePl < maxRange || e.getEntityId() == -1000;
                }
            }).forEach(e -> mc.getRenderManager().renderEntityStatic(e, tick, true));
            mc.world.removeEntityFromWorld(-1000);
        } catch (Exception e){

        }
    }




}

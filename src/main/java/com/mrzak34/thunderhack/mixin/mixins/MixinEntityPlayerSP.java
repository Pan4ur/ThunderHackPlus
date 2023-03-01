package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.modules.movement.Speed;
import com.mrzak34.thunderhack.modules.movement.Strafe;
import com.mrzak34.thunderhack.modules.movement.testMove;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.util.Util.mc;


@Mixin(value = {EntityPlayerSP.class}, priority = 9998)
public abstract class MixinEntityPlayerSP
        extends AbstractClientPlayer {
    @Shadow public abstract boolean isSneaking();

    public MixinEntityPlayerSP(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_, NetHandlerPlayClient connection) {
        super(p_i47378_2_, p_i47378_3_.getGameProfile());
        this.connection = p_i47378_3_;
    }


    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isCurrentViewEntity()Z"))
    private boolean redirectIsCurrentViewEntity(EntityPlayerSP entityPlayerSP) {
        FreecamEvent event = new FreecamEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return entityPlayerSP == mc.player;
        }
        return mc.getRenderViewEntity() == entityPlayerSP;
    }

    @Shadow
    public final NetHandlerPlayClient connection;

    private boolean updateLock = false;

    @Inject(method = {"onUpdate"}, at = {@At(value = "HEAD")})
    private void updateHook(CallbackInfo info) {
        PlayerUpdateEvent playerUpdateEvent = new PlayerUpdateEvent();
        MinecraftForge.EVENT_BUS.post(playerUpdateEvent);
        if (!playerUpdateEvent.getPostEvents().isEmpty()) {
            for (Runnable runnable : playerUpdateEvent.getPostEvents()) {
                Minecraft.getMinecraft().addScheduledTask(runnable);
            }
        }
    }

    @Shadow
    protected abstract void onUpdateWalkingPlayer();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.onUpdateWalkingPlayer()V", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private void PostUpdateHook(CallbackInfo info) {
        if (updateLock) {
            return;
        }
        PostPlayerUpdateEvent playerUpdateEvent = new PostPlayerUpdateEvent();
        MinecraftForge.EVENT_BUS.post(playerUpdateEvent);
        if (playerUpdateEvent.isCanceled()) {
            info.cancel();
            if (playerUpdateEvent.getIterations() > 0) {
                for (int i = 0; i < playerUpdateEvent.getIterations(); i++) {
                    updateLock = true;
                    onUpdate();
                    updateLock = false;
                    onUpdateWalkingPlayer();
                }
            }
        }
    }

    @Redirect(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isCurrentViewEntity()Z"))
    private boolean redirectIsCurrentViewEntity2(EntityPlayerSP entityPlayerSP) {

        Minecraft mc = Minecraft.getMinecraft();
        FreecamEvent event = new FreecamEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return entityPlayerSP == mc.player;
        }
        return mc.getRenderViewEntity() == entityPlayerSP;
    }

    @Inject(method = { "pushOutOfBlocks" },  at = { @At("HEAD") },  cancellable = true)
    private void pushOutOfBlocksHook(final double x,  final double y,  final double z,  final CallbackInfoReturnable<Boolean> info) {
        final PushEvent event = new PushEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.setReturnValue(false);
        }
    }

    double preX, preZ;

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void movePre(MoverType type, double x, double y, double z, CallbackInfo info) {

        EventMove event = new EventMove(type, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            super.move(type, event.get_x(), event.get_y(), event.get_z());
            info.cancel();
        }
        if (Thunderhack.moduleManager.getModuleByClass(Strafe.class).isEnabled() || Thunderhack.moduleManager.getModuleByClass(Speed.class).isEnabled()|| Thunderhack.moduleManager.getModuleByClass(testMove.class).isEnabled()){
            preX = posX;
            preZ = posZ;
            AxisAlignedBB before = getEntityBoundingBox();
            boolean predictGround = !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -Thunderhack.moduleManager.getModuleByClass(testMove.class).vspeedValue2.getValue(), 0.0)).isEmpty() && fallDistance > 0.1f && !mc.player.onGround;
            MatrixMove move = new MatrixMove(mc.player.posX, mc.player.posY, mc.player.posZ, x, y, z, predictGround, before);
            MinecraftForge.EVENT_BUS.post(move);
            if (move.isCanceled()) {
                super.move(type, move.getMotionX(), move.getMotionY(), move.getMotionZ());
                info.cancel();
            }
        }

    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void preMotion(CallbackInfo info) {
        EventPreMotion event = new EventPreMotion(rotationYaw,rotationPitch);
        MinecraftForge.EVENT_BUS.post(event);
        EventSprint e = new EventSprint(isSprinting());
        MinecraftForge.EVENT_BUS.post(e);
        if(event.isCanceled()){
            info.cancel();
        }
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "RETURN")})
    private void postMotion(CallbackInfo info) {
        EventPostMotion event = new EventPostMotion();
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.getPostEvents().isEmpty()) {
            for (Runnable runnable : event.getPostEvents()) {
                Minecraft.getMinecraft().addScheduledTask(runnable);
            }
        }
    }
}
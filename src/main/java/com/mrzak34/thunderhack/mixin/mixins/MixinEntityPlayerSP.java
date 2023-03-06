package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.manager.EventManager;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.movement.Speed;
import com.mrzak34.thunderhack.modules.movement.Strafe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.AxisAlignedBB;
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


    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void preMotion(CallbackInfo info) {
        EventPreMotion event = new EventPreMotion(rotationYaw,rotationPitch);
        MinecraftForge.EVENT_BUS.post(event);
        EventSprint e = new EventSprint(isSprinting());
        MinecraftForge.EVENT_BUS.post(e);

        if (e.getSprintState() != ((IEntityPlayerSP)mc.player).getServerSprintState()) {
            if (e.getSprintState()) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
            }
            ((IEntityPlayerSP)mc.player).setServerSprintState(e.getSprintState());
        }
        pre_sprint_state = ((IEntityPlayerSP)mc.player).getServerSprintState();
        EventManager.lock_sprint = true;
        if(event.isCanceled()){
            info.cancel();
        }
    }

    boolean pre_sprint_state = false;


    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void movePre(MoverType type, double x, double y, double z, CallbackInfo info) {
        EventMove event = new EventMove(type, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            super.move(type, event.get_x(), event.get_y(), event.get_z());
            info.cancel();
        }
        //TODO Не забыть бы что этот эвент работает только при спидах и стрейфах (в целях оптимизации)
        if (Thunderhack.moduleManager.getModuleByClass(Speed.class).isEnabled()|| Thunderhack.moduleManager.getModuleByClass(Strafe.class).isEnabled()){
            AxisAlignedBB before = getEntityBoundingBox();

            //TODO \|/  вот этой хуйни
            boolean predictGround = !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -0.228, 0.0)).isEmpty() && fallDistance > 0.1f && !mc.player.onGround;

            MatrixMove move = new MatrixMove(mc.player.posX, mc.player.posY, mc.player.posZ, x, y, z, predictGround, before);
            MinecraftForge.EVENT_BUS.post(move);
            if (move.isCanceled()) {
                super.move(type, move.getMotionX(), move.getMotionY(), move.getMotionZ());
                info.cancel();
            }
        }
    }



    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "RETURN")})
    private void postMotion(CallbackInfo info) {
        ((IEntityPlayerSP)mc.player).setServerSprintState(pre_sprint_state);
        EventManager.lock_sprint = false;
        EventPostMotion event = new EventPostMotion();
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.getPostEvents().isEmpty()) {
            for (Runnable runnable : event.getPostEvents()) {
                Minecraft.getMinecraft().addScheduledTask(runnable);
            }
        }
    }
}
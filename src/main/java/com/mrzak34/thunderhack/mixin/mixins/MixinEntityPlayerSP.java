package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.event.events.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.util.ItemUtil.mc;

@Mixin(value = {EntityPlayerSP.class}, priority = 9998)
public abstract class MixinEntityPlayerSP
        extends AbstractClientPlayer {
    @Shadow public abstract boolean isSneaking();

    public MixinEntityPlayerSP(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_, NetHandlerPlayClient connection) {
        super(p_i47378_2_, p_i47378_3_.getGameProfile());
        this.connection = p_i47378_3_;
    }

    @Inject(method = {"sendChatMessage"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void sendChatMessage(String message, CallbackInfo callback) {
        ChatEvent chatEvent = new ChatEvent(message);
        MinecraftForge.EVENT_BUS.post(chatEvent);
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isCurrentViewEntity()Z"))
    private boolean redirectIsCurrentViewEntity(EntityPlayerSP entityPlayerSP) {
        Minecraft mc = Minecraft.getMinecraft();
        FreecamEvent event = new FreecamEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return entityPlayerSP == mc.player;
        }
        return mc.getRenderViewEntity() == entityPlayerSP;
    }

    @Shadow
    public final NetHandlerPlayClient connection;

    @Inject(method = {"onUpdate"}, at = {@At(value = "HEAD")})
    private void updatehook(CallbackInfo info) {
        PlayerUpdateEvent playerUpdateEvent = new PlayerUpdateEvent();
        MinecraftForge.EVENT_BUS.post(playerUpdateEvent);
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
        final PushEvent event = new PushEvent(1);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void movePre(MoverType type, double x, double y, double z, CallbackInfo info) {
        EventMove event = new EventMove(type, x, y, z,0);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            super.move(type, event.get_x(), event.get_y(), event.get_z());
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("RETURN"), cancellable = true)
    private void movePost(MoverType type, double x, double y, double z, CallbackInfo info) {
        EventMove event = new EventMove(type, x, y, z,1);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            super.move(type, event.get_x(), event.get_y(), event.get_z());
            info.cancel();
        }
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "HEAD")})
    private void preMotion(CallbackInfo info) {
        EventPreMotion event = new EventPreMotion(rotationYaw,rotationPitch);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "RETURN")})
    private void postMotion(CallbackInfo info) {
        EventPostMotion event = new EventPostMotion();
        MinecraftForge.EVENT_BUS.post(event);
    }
}
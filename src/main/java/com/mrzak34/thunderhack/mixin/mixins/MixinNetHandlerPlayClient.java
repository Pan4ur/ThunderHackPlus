package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.events.DeathEvent;
import com.mrzak34.thunderhack.util.Util;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.network.*;
import net.minecraft.network.play.server.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;

import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ NetHandlerPlayClient.class })
public class MixinNetHandlerPlayClient
{
    @Inject(method = { "handleEntityMetadata" },  at = { @At("RETURN") },  cancellable = true)
    private void handleEntityMetadataHook(final SPacketEntityMetadata packetIn,  final CallbackInfo info) {
        final Entity entity;
        final EntityPlayer player;
        if (Util.mc.world != null && (entity = Util.mc.world.getEntityByID(packetIn.getEntityId())) instanceof EntityPlayer && (player = (EntityPlayer)entity).getHealth() <= 0.0f) {
            MinecraftForge.EVENT_BUS.post((Event)new DeathEvent(player));
        }
    }
}
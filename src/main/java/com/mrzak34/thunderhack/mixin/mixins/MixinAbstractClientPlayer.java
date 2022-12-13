package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.ThunderUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.*;

@Mixin(value={AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {

    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    public ResourceLocation caperes;


    HashMap<String, ResourceLocation> users = new HashMap<>();

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {

        //TODO local host check for 2b2w

        String name = Objects.requireNonNull(getPlayerInfo()).getGameProfile().getName();
        if (ThunderUtils.isTHUser(name) && Thunderhack.moduleManager.getModuleByClass(MainSettings.class).showcapes.getValue()) {
            if(!users.containsKey(name)) {
                try {
                    BufferedImage image = ThunderUtils.getCustomCape(name);
                    DynamicTexture texture = new DynamicTexture(image);
                    PNGtoResourceLocation.WrappedResource wr = new PNGtoResourceLocation.WrappedResource(FMLClientHandler.instance().getClient().getTextureManager().getDynamicTextureLocation(name, texture));
                    caperes = wr.location;
                    callbackInfoReturnable.setReturnValue(caperes);
                    users.put(name,caperes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                callbackInfoReturnable.setReturnValue(users.get(name));
            }
        }

    }

}

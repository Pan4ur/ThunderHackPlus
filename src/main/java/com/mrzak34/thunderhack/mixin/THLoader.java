package com.mrzak34.thunderhack.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class THLoader implements IFMLLoadingPlugin {

    public THLoader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.thunderhack.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {}

    public String getAccessTransformerClass() {
        return null;
    }
}


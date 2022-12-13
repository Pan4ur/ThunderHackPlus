package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.MotionBlur;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class MotionBlurResource implements IResource {
    public InputStream getInputStream() {
        double amount = 0.7D + Thunderhack.moduleManager.getModuleByClass(MotionBlur.class).amount.getValue() / 100.0D * 3.0D - 0.01D;
        return IOUtils.toInputStream(String.format(Locale.ENGLISH, "{\"targets\":[\"swap\",\"previous\"],\"passes\":[{\"name\":\"phosphor\",\"intarget\":\"minecraft:main\",\"outtarget\":\"swap\",\"auxtargets\":[{\"name\":\"PrevSampler\",\"id\":\"previous\"}],\"uniforms\":[{\"name\":\"Phosphor\",\"values\":[%.2f, %.2f, %.2f]}]},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"previous\"},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"minecraft:main\"}]}", amount, amount, amount));
    }

    public boolean hasMetadata() {
        return false;
    }

    public IMetadataSection getMetadata(String metadata) {
        return null;
    }

    public ResourceLocation getResourceLocation() {
        return null;
    }

    public String getResourcePackName() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
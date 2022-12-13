package com.mrzak34.thunderhack.manager;

import com.mrzak34.thunderhack.util.MotionBlurResource;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MotionBlurResourceManager implements IResourceManager {

    @Override
    public Set<String> getResourceDomains() {
        return null;
    }

    @Override
    public IResource getResource(ResourceLocation location) throws IOException {
        return new MotionBlurResource();
    }

    @Override
    public List<IResource> getAllResources(ResourceLocation location) {
        return null;
    }
}
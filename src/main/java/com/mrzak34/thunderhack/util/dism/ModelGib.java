package com.mrzak34.thunderhack.util.dism;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

public class ModelGib extends ModelBase {
    //fields
    public ModelRenderer skeleLeg;
    public ModelRenderer skeleArm;
    public ModelRenderer creeperFoot;
    public ModelRenderer head64;
    public ModelRenderer body64;
    public ModelRenderer leg64;
    public ModelRenderer arm64;
    public ModelRenderer head32;
    //  public ModelRenderer layerhead;
    public ModelRenderer body32;
    public ModelGib() {
        textureWidth = 64;
        textureHeight = 64;
        leg64 = new ModelRenderer(this, 0, 16);
        leg64.setTextureSize(64, 64);
        leg64.addBox(-2F, -6F, -2F, 4, 12, 4);
        leg64.setRotationPoint(0F, 24F, 0F);
        leg64.rotateAngleX = 0F;
        leg64.rotateAngleY = 0F;
        leg64.rotateAngleZ = 0F;
        leg64.mirror = false;

        arm64 = new ModelRenderer(this, 40, 16);
        arm64.setTextureSize(64, 64);
        arm64.addBox(-2F, -6F, -2F, 4, 12, 4);
        arm64.setRotationPoint(0F, 22F, 0F);
        arm64.rotateAngleX = 0F;
        arm64.rotateAngleY = 0F;
        arm64.rotateAngleZ = 0F;
        arm64.mirror = false;

        skeleArm = new ModelRenderer(this, 40, 16);
        skeleArm.setTextureSize(64, 32);
        skeleArm.addBox(-1F, -6F, -1F, 2, 12, 2);
        skeleArm.setRotationPoint(0F, 24F, 0F);
        skeleArm.rotateAngleX = 0F;
        skeleArm.rotateAngleY = 0F;
        skeleArm.rotateAngleZ = 0F;
        skeleArm.mirror = false;

        skeleLeg = new ModelRenderer(this, 0, 16);
        skeleLeg.setTextureSize(64, 32);
        skeleLeg.addBox(-1F, -6F, -1F, 2, 12, 2);
        skeleLeg.setRotationPoint(0F, 24F, 0F);
        skeleLeg.rotateAngleX = 0F;
        skeleLeg.rotateAngleY = 0F;
        skeleLeg.rotateAngleZ = 0F;
        skeleLeg.mirror = false;

        creeperFoot = new ModelRenderer(this, 0, 16);
        creeperFoot.setTextureSize(64, 32);
        creeperFoot.addBox(-2F, -3F, -2F, 4, 6, 4);
        creeperFoot.setRotationPoint(0F, 24F, 0F);
        creeperFoot.rotateAngleX = 0F;
        creeperFoot.rotateAngleY = 0F;
        creeperFoot.rotateAngleZ = 0F;
        creeperFoot.mirror = false;

        head64 = new ModelRenderer(this, 0, 0);
        head64.setTextureSize(64, 64);
        head64.addBox(-4F, -4F, -4F, 8, 8, 8);
        head64.setRotationPoint(0F, 20F, 0F);
        head64.rotateAngleX = 0F;
        head64.rotateAngleY = 0F;
        head64.rotateAngleZ = 0F;
        head64.mirror = false;

        head64.setTextureOffset(32, 0);
        head64.addBox(-4F, -4F, -4F, 8, 8, 8, 1.1f);
        head64.setRotationPoint(0F, 20F, 0F);
        head64.rotateAngleX = 0F;
        head64.rotateAngleY = 0F;
        head64.rotateAngleZ = 0F;
        head64.mirror = false;





        /*
        layerhead = new ModelRenderer(this, 32, 0);
        layerhead.setTextureSize(64, 64);
        layerhead.addBox(-4F, -4F, -4F, 8, 8, 8);
        layerhead.setRotationPoint(0F, 20F, 0F);
        layerhead.rotateAngleX = 0F;
        layerhead.rotateAngleY = 0F;
        layerhead.rotateAngleZ = 0F;
        layerhead.mirror = false;

         */


        body64 = new ModelRenderer(this, 16, 16);
        body64.setTextureSize(64, 64);
        body64.addBox(-4F, -6F, -2F, 8, 12, 4);
        body64.setRotationPoint(0F, 22F, 0F);
        body64.rotateAngleX = 0F;
        body64.rotateAngleY = 0F;
        body64.rotateAngleZ = 0F;
        body64.mirror = false;

        head32 = new ModelRenderer(this, 0, 0);
        head32.setTextureSize(64, 32);
        head32.addBox(-4F, -4F, -4F, 8, 8, 8);
        head32.setRotationPoint(0F, 20F, 0F);
        head32.rotateAngleX = 0F;
        head32.rotateAngleY = 0F;
        head32.rotateAngleZ = 0F;
        head32.mirror = false;

        body32 = new ModelRenderer(this, 16, 16);
        body32.setTextureSize(64, 32);
        body32.addBox(-4F, -6F, -2F, 8, 12, 4);
        body32.setRotationPoint(0F, 22F, 0F);
        body32.rotateAngleX = 0F;
        body32.rotateAngleY = 0F;
        body32.rotateAngleZ = 0F;
        body32.mirror = false;
    }

    @Override
    public void render(Entity ent, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, ent);

        if (ent instanceof EntityGib) {
            EntityGib gib = (EntityGib) ent;

            if (gib.type == -1) {
                //   layerhead.render(f5);
            }

            if (gib.type == 0) {
                if (gib.parent instanceof EntityZombie || gib.parent instanceof EntityPlayer) {
                    head64.render(f5);
                } else {
                    head32.render(f5);
                }
            } else if (gib.type == 1 || gib.type == 2) {
                if (gib.parent instanceof EntityZombie || gib.parent instanceof EntityPlayer) {
                    arm64.render(f5);
                } else {
                    skeleArm.render(f5);
                }
            } else if (gib.type == 3) //body
            {
                if (gib.parent instanceof EntityZombie || gib.parent instanceof EntityPlayer) {
                    body64.render(f5);
                } else {
                    body32.render(f5);
                }
            } else if (gib.type == 4 || gib.type == 5) //legs
            {
                if (gib.parent instanceof EntityZombie || gib.parent instanceof EntityPlayer) {
                    leg64.render(f5);
                } else {
                    skeleLeg.render(f5);
                }
            } else if (gib.type >= 6) //creeper feet
            {
                creeperFoot.render(f5);
            }
        }
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity ent) {
        arm64.rotateAngleY = f3 / 57.29578F;
        arm64.rotateAngleX = f4 / 57.29578F;

        leg64.rotateAngleY = f3 / 57.29578F;
        leg64.rotateAngleX = f4 / 57.29578F;

        skeleArm.rotateAngleY = f3 / 57.29578F;
        skeleArm.rotateAngleX = f4 / 57.29578F;

        skeleLeg.rotateAngleY = f3 / 57.29578F;
        skeleLeg.rotateAngleX = f4 / 57.29578F;

        creeperFoot.rotateAngleY = f3 / 57.29578F;
        creeperFoot.rotateAngleX = f4 / 57.29578F;

        head64.rotateAngleY = f3 / 57.29578F;
        head64.rotateAngleX = f4 / 57.29578F;

        //  layerhead.rotateAngleY = f3 / 57.29578F;
        //   layerhead.rotateAngleX = f4 / 57.29578F;

        body64.rotateAngleY = f3 / 57.29578F;
        body64.rotateAngleX = f4 / 57.29578F;

        head32.rotateAngleY = f3 / 57.29578F;
        head32.rotateAngleX = f4 / 57.29578F;

        body32.rotateAngleY = f3 / 57.29578F;
        body32.rotateAngleX = f4 / 57.29578F;
    }

}
package com.mrzak34.thunderhack.modules.render;

import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.event.events.TotemPopEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class PopChams extends Module {

    public PopChams() {
        super("PopChams", "Renders when some1 pops", Category.RENDER, true, false, false);
    }


    public Setting<Boolean> self = this.register ( new Setting <> ( "SelfPop", false ) );
    public Setting<Boolean> anim = this.register ( new Setting <> ( "Copy Animations", false ) );

    public Setting<Float> maxOffset = this.register(new Setting<>("MaxOffset", 0.1f, 0.1f, 15.0f));
    public Setting<Float> speed = this.register(new Setting<>("Speed", 0.1f, 0.1f, 10.0f));

    private final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));




    public final CopyOnWriteArrayList<Person> popList = new CopyOnWriteArrayList<>( );

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent e){
        if ( !self.getValue() ) {
            if ( e.getEntity( ) == mc.player ) return;
        }
        EntityPlayer sp = e.getEntity();
        EntityPlayer entity = new EntityPlayer( mc.world, new GameProfile( sp.getUniqueID( ), sp.getName( ) ) ) {
            @Override public boolean isSpectator ( ) {return false;}

            @Override public boolean isCreative ( ) {return false;}
        };
        entity.copyLocationAndAnglesFrom( sp );

        if(anim.getValue()) {
            entity.limbSwing = sp.limbSwing;
            entity.limbSwingAmount = sp.limbSwingAmount;
            entity.setSneaking(sp.isSneaking());
        }
        popList.add( new Person( entity ) );
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent e) {
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GlStateManager.tryBlendFuncSeparate( GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO );
        GlStateManager.glLineWidth( 1.5F );
        GlStateManager.disableTexture2D( );
        GlStateManager.depthMask( false );
        GlStateManager.enableBlend( );
        GlStateManager.disableDepth( );
        GlStateManager.disableLighting( );
        GlStateManager.disableCull( );
        GlStateManager.enableAlpha( );
        popList.forEach( person -> {
            person.update( popList );
            person.modelPlayer.bipedLeftLegwear.showModel = false;
            person.modelPlayer.bipedRightLegwear.showModel = false;
            person.modelPlayer.bipedLeftArmwear.showModel = false;
            person.modelPlayer.bipedRightArmwear.showModel = false;
            person.modelPlayer.bipedBodyWear.showModel = false;
            person.modelPlayer.bipedHead.showModel = true;
            person.modelPlayer.bipedHeadwear.showModel = false;
            GlStateManager.color( color.getValue().getRed() / 255f, color.getValue().getGreen( ) / 255f, color.getValue().getBlue( ) / 255f, ( float ) person.alpha / 255f );
            GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
            renderEntity( person.player, person.modelPlayer, person.player.limbSwing,
                    person.player.limbSwingAmount, person.player.ticksExisted, person.player.rotationYawHead, person.player.rotationPitch, 1 );


            GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
            renderEntity( person.player, person.modelPlayer, person.player.limbSwing,
                    person.player.limbSwingAmount, person.player.ticksExisted, person.player.rotationYawHead, person.player.rotationPitch, 1 );

            GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
        } );
        GlStateManager.enableCull( );
        GlStateManager.depthMask( true );
        GlStateManager.enableTexture2D( );
        GlStateManager.enableBlend( );
        GlStateManager.enableDepth( );
    }

    public class Person {
        private double alpha;
        private final EntityPlayer player;
        private final ModelPlayer modelPlayer;

        public Person ( EntityPlayer player ) {
            this.player = player;
            this.modelPlayer = new ModelPlayer( 0, false );
            this.alpha = 180;
        }

        public void update ( CopyOnWriteArrayList<Person> arrayList ) {
            if ( alpha <= 0 ) {
                arrayList.remove( this );
                mc.world.removeEntity( player );
                return;
            }
            this.alpha -= 180 / speed.getValue() * getFrametime( );
            player.posY += maxOffset.getValue() / speed.getValue() * getFrametime( );
        }
    }

    public static void renderEntity (EntityLivingBase entity, ModelBase modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale ) {
        if ( modelBase instanceof ModelPlayer ) {
            ModelPlayer modelPlayer = ( ( ModelPlayer ) modelBase );
            modelPlayer.bipedBodyWear.showModel = false;
            modelPlayer.bipedLeftLegwear.showModel = false;
            modelPlayer.bipedRightLegwear.showModel = false;
            modelPlayer.bipedLeftArmwear.showModel = false;
            modelPlayer.bipedRightArmwear.showModel = false;
            modelPlayer.bipedHeadwear.showModel = true;
            modelPlayer.bipedHead.showModel = false;
        }

        float partialTicks = mc.getRenderPartialTicks( );
        double x = entity.posX - mc.getRenderManager( ).viewerPosX;
        double y = entity.posY - mc.getRenderManager( ).viewerPosY;
        double z = entity.posZ - mc.getRenderManager( ).viewerPosZ;

        GlStateManager.pushMatrix( );

        if ( entity.isSneaking( ) ) {
            y -= 0.125D;
        }
        GlStateManager.translate( ( float ) x, ( float ) y, ( float ) z );
        GlStateManager.rotate( 180 - entity.rotationYaw, 0, 1, 0 );
        float f4 = prepareScale( entity, scale );
        float yaw = entity.rotationYawHead;

        GlStateManager.enableAlpha( );
        modelBase.setLivingAnimations( entity, limbSwing, limbSwingAmount, partialTicks );
        modelBase.setRotationAngles( limbSwing, limbSwingAmount, 0, yaw, entity.rotationPitch, f4, entity );
        modelBase.render( entity, limbSwing, limbSwingAmount, 0, yaw, entity.rotationPitch, f4 );

        GlStateManager.popMatrix( );
    }

    private static float prepareScale ( EntityLivingBase entity, float scale ) {
        GlStateManager.enableRescaleNormal( );
        GlStateManager.scale( -1.0F, -1.0F, 1.0F );
        double widthX = entity.getRenderBoundingBox( ).maxX - entity.getRenderBoundingBox( ).minX;
        double widthZ = entity.getRenderBoundingBox( ).maxZ - entity.getRenderBoundingBox( ).minZ;

        GlStateManager.scale( scale + widthX, scale * entity.height, scale + widthZ );
        float f = 0.0625F;

        GlStateManager.translate( 0.0F, -1.501F, 0.0F );
        return f;
    }



    private int fps;
    private final LinkedList<Long> frames = new LinkedList<>( );


    @Override
    public void onUpdate( ) {
        long time = System.nanoTime( );

        frames.add( time );

        while ( true ) {
            long f = frames.getFirst( );
            final long ONE_SECOND = 1000000L * 1000L;
            if ( time - f > ONE_SECOND ) frames.remove( );
            else break;
        }

        fps = frames.size( );
    }
    public float getFrametime( ) {
        return 1.0f / fps;
    }
}
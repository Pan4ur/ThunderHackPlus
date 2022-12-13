package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public
class NoRender
        extends Module {
    private static NoRender INSTANCE = new NoRender ( );

    static {
        NoRender.INSTANCE = new NoRender ( );
    }

    public Setting < Boolean > noarmorstands = this.register ( new Setting <> ( "ArmorStands" , false  ) );
    public Setting < Boolean > fire = this.register ( new Setting <> ( "Fire" , false  ) );
    public Setting < Boolean > blin = this.register ( new Setting <> ( "Blind" , false  ) );
    public Setting < Boolean > arrows = this.register ( new Setting <> ( "Arrows" , false ) );

    public Setting < Boolean > SkyLight = this.register ( new Setting <> ( "SkyLight" , false ) );
    public Setting < Boolean > portal = this.register ( new Setting <> ( "portal" , false  ) );
    public Setting < Boolean > totemPops = this.register ( new Setting <> ( "TotemPop" , false  ) );
    public Setting < Boolean > items = this.register ( new Setting <> ( "Items" , false) );
    public Setting < Boolean > nausea = this.register ( new Setting <> ( "Nausea" , false ) );
    public Setting < Boolean > hurtcam = this.register ( new Setting <> ( "HurtCam" , false  ) );
    public Setting < Boolean > explosions = this.register ( new Setting <> ( "Explosions" , false ) );
    public Setting < Fog > fog = this.register ( new Setting <> ( "Fog" , Fog.NONE  ) );
    public Setting < Boolean > noWeather = this.register ( new Setting <> ( "Weather" , false ) );
    public Setting < Boss > boss = this.register ( new Setting <> ( "BossBars" , Boss.NONE ) );
    public Setting < Float > scale = this.register ( new Setting <> ( "Scale" , 0.5f , 0.5f , 1.0f , v -> this.boss.getValue ( ) == Boss.MINIMIZE || this.boss.getValue ( ) == Boss.STACK ) );
    public Setting < Boolean > bats = this.register ( new Setting <> ( "Bats" , false  ) );
    public Setting < NoArmor > noArmor = this.register ( new Setting <> ( "NoArmor" , NoArmor.NONE ) );
    public Setting < Boolean > barriers = this.register ( new Setting <> ( "Barriers" , false ) );
    public Setting < Boolean > blocks = this.register ( new Setting <> ( "Blocks" , false  ) );
    public Setting < Boolean > advancements = this.register ( new Setting <> ( "Advancements" , false ) );
    public Setting < Boolean > timeChange = this.register ( new Setting <> ( "TimeChange" , false ) );
    public Setting < Integer > time = this.register ( new Setting <> ( "Time" , 0 , 0 , 23000 , v -> this.timeChange.getValue ( ) ) );
    public Setting < Boolean > fireworks = this.register ( new Setting <> ( "FireWorks" , false ) );
    public Setting < Boolean > hooks = this.register ( new Setting <> ( "Hooks" , false ) );

    public
    NoRender ( ) {
        super ( "NoRender" , "не рендерить лаганые-херни" , Module.Category.RENDER , true , false , false );
        this.setInstance ( );
    }

    public static
    NoRender getInstance ( ) {
        if ( NoRender.INSTANCE == null ) {
            NoRender.INSTANCE = new NoRender ( );
        }
        return NoRender.INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onUpdate ( ) {
        if ( this.items.getValue ( ) ) {
            NoRender.mc.world.loadedEntityList.stream ( ).filter ( EntityItem.class::isInstance ).map ( EntityItem.class::cast ).forEach ( Entity::setDead );
        }
        if ( this.arrows.getValue ( ) ) {
            NoRender.mc.world.loadedEntityList.stream ( ).filter ( EntityArrow.class::isInstance ).map ( EntityArrow.class::cast ).forEach ( Entity::setDead );
        }
        if ( this.noWeather.getValue ( ) && NoRender.mc.world.isRaining ( ) ) {
            NoRender.mc.world.setRainStrength ( 0.0f );
        }
        if ( this.timeChange.getValue ( ) ) {
            NoRender.mc.world.setWorldTime ( (long) this.time.getValue ( ) );
        }
    }

    @SubscribeEvent
    public
    void onPacketReceive ( final PacketEvent.Receive event ) {
        if ( event.getPacket ( ) instanceof SPacketTimeUpdate & this.timeChange.getValue ( ) ) {
            event.setCanceled ( true );
        }
        if ( event.getPacket ( ) instanceof SPacketExplosion & this.explosions.getValue ( ) ) {
            event.setCanceled ( true );
        }
        if( event.getPacket() instanceof SPacketEntityEffect && blin.getValue() ){
            event.setCanceled(true);
            SPacketEntityEffect var3 = event.getPacket();
            if( var3.getEffectId() == 15) {
                event.setCanceled ( true );
            }
        }
    }

    @SubscribeEvent
    public
    void onRenderPre ( final RenderGameOverlayEvent.Pre event ) {
        if ( event.getType ( ) == RenderGameOverlayEvent.ElementType.BOSSINFO && this.boss.getValue ( ) != Boss.NONE ) {
            event.setCanceled ( true );
        }
    }

    @SubscribeEvent
    public
    void onRenderPost ( final RenderGameOverlayEvent.Post event ) {
        if ( event.getType ( ) == RenderGameOverlayEvent.ElementType.BOSSINFO && this.boss.getValue ( ) != Boss.NONE ) {
            if ( this.boss.getValue ( ) == Boss.MINIMIZE ) {
                final Map < UUID, BossInfoClient > map = NoRender.mc.ingameGUI.getBossOverlay ( ).mapBossInfos;
                if ( map == null ) {
                    return;
                }
                final ScaledResolution scaledresolution = new ScaledResolution ( NoRender.mc );
                final int i = scaledresolution.getScaledWidth ( );
                int j = 12;
                for (final Map.Entry < UUID, BossInfoClient > entry : map.entrySet ( )) {
                    final BossInfoClient info = entry.getValue ( );
                    final String text = info.getName ( ).getFormattedText ( );
                    final int k = (int) ( i / this.scale.getValue ( ) / 2.0f - 91.0f );
                    GL11.glScaled ( (double) this.scale.getValue ( ) , (double) this.scale.getValue ( ) , 1.0 );
                    if ( ! event.isCanceled ( ) ) {
                        GlStateManager.color ( 1.0f , 1.0f , 1.0f , 1.0f );
                        NoRender.mc.getTextureManager ( ).bindTexture ( GuiBossOverlay.GUI_BARS_TEXTURES );
                        NoRender.mc.ingameGUI.getBossOverlay ( ).render ( k , j , info );
                        NoRender.mc.fontRenderer.drawStringWithShadow ( text , i / this.scale.getValue ( ) / 2.0f - NoRender.mc.fontRenderer.getStringWidth ( text ) / 2f , (float) ( j - 9 ) , 16777215 );
                    }
                    GL11.glScaled ( 1.0 / this.scale.getValue ( ) , 1.0 / this.scale.getValue ( ) , 1.0 );
                    j += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT;
                }
            } else if ( this.boss.getValue ( ) == Boss.STACK ) {
                final Map < UUID, BossInfoClient > map = NoRender.mc.ingameGUI.getBossOverlay ( ).mapBossInfos;
                final HashMap < String, Pair < BossInfoClient, Integer > > to = new HashMap <> ( );
                for (final Map.Entry < UUID, BossInfoClient > entry2 : map.entrySet ( )) {
                    final String s = entry2.getValue ( ).getName ( ).getFormattedText ( );
                    if ( to.containsKey ( s ) ) {
                        Pair < BossInfoClient, Integer > p = to.get ( s );
                        p = new Pair <> ( p.getKey ( ) , p.getValue ( ) + 1 );
                        to.put ( s , p );
                    } else {
                        final Pair < BossInfoClient, Integer > p = new Pair <> ( entry2.getValue ( ) , 1 );
                        to.put ( s , p );
                    }
                }
                final ScaledResolution scaledresolution2 = new ScaledResolution ( NoRender.mc );
                final int l = scaledresolution2.getScaledWidth ( );
                int m = 12;
                for (final Map.Entry < String, Pair < BossInfoClient, Integer > > entry3 : to.entrySet ( )) {
                    String text = entry3.getKey ( );
                    final BossInfoClient info2 = entry3.getValue ( ).getKey ( );
                    final int a = entry3.getValue ( ).getValue ( );
                    text = text + " x" + a;
                    final int k2 = (int) ( l / this.scale.getValue ( ) / 2.0f - 91.0f );
                    GL11.glScaled ( (double) this.scale.getValue ( ) , (double) this.scale.getValue ( ) , 1.0 );
                    if ( ! event.isCanceled ( ) ) {
                        GlStateManager.color ( 1.0f , 1.0f , 1.0f , 1.0f );
                        NoRender.mc.getTextureManager ( ).bindTexture ( GuiBossOverlay.GUI_BARS_TEXTURES );
                        NoRender.mc.ingameGUI.getBossOverlay ( ).render ( k2 , m , info2 );
                        NoRender.mc.fontRenderer.drawStringWithShadow ( text , l / this.scale.getValue ( ) / 2.0f - NoRender.mc.fontRenderer.getStringWidth ( text ) / 2f , (float) ( m - 9 ) , 16777215 );
                    }
                    GL11.glScaled ( 1.0 / this.scale.getValue ( ) , 1.0 / this.scale.getValue ( ) , 1.0 );
                    m += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT;
                }
            }
        }
    }

    @SubscribeEvent
    public
    void onRenderLiving ( final RenderLivingEvent.Pre < ? > event ) {
        if ( this.bats.getValue ( ) && event.getEntity ( ) instanceof EntityBat ) {
            event.setCanceled ( true );
        }
    }

    @SubscribeEvent
    public
    void onPlaySound ( final PlaySoundAtEntityEvent event ) {
        if ( ( this.bats.getValue ( ) && event.getSound ( ).equals ( SoundEvents.ENTITY_BAT_AMBIENT ) ) || event.getSound ( ).equals ( SoundEvents.ENTITY_BAT_DEATH ) || event.getSound ( ).equals ( SoundEvents.ENTITY_BAT_HURT ) || event.getSound ( ).equals ( SoundEvents.ENTITY_BAT_LOOP ) || event.getSound ( ).equals ( SoundEvents.ENTITY_BAT_TAKEOFF ) ) {
            event.setVolume ( 0.0f );
            event.setPitch ( 0.0f );
            event.setCanceled ( true );
        }
    }


    public
    enum Fog {
        NONE,
        AIR,
        NOFOG
    }

    public
    enum Boss {
        NONE,
        REMOVE,
        STACK,
        MINIMIZE
    }

    public
    enum NoArmor {
        NONE,
        ALL,
        HELMET
    }

    public static
    class Pair< T, S > {
        private T key;
        private S value;

        public
        Pair ( final T key , final S value ) {
            this.key = key;
            this.value = value;
        }

        public
        T getKey ( ) {
            return this.key;
        }

        public
        void setKey ( final T key ) {
            this.key = key;
        }

        public
        S getValue ( ) {
            return this.value;
        }

        public
        void setValue ( final S value ) {
            this.value = value;
        }
    }
}
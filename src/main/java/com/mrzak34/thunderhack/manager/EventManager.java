package com.mrzak34.thunderhack.manager;

import com.google.common.base.Strings;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.macro.Macro;
import com.mrzak34.thunderhack.modules.misc.Macros;
import com.mrzak34.thunderhack.util.ThunderUtils;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.command.Command;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class EventManager extends Feature {
    private final Timer logoutTimer = new Timer();
    private final Timer chorusTimer= new Timer();
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onUnload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }


    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!fullNullCheck() && (event.getEntity().getEntityWorld()).isRemote && event.getEntityLiving().equals(mc.player)) {
            if(mc.player != null && mc.player.getName().equals("Hell_Raider")){
                Thunderhack.unload(true);
            }
            Thunderhack.moduleManager.onUpdate();
            Thunderhack.moduleManager.sortModules(true);
        }
    }



    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        this.logoutTimer.reset();
        Thunderhack.moduleManager.onLogin();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Thunderhack.moduleManager.onLogout();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (fullNullCheck())
            return;
        Thunderhack.moduleManager.onTick();
        if(mc.world != null) {
            try {
                for (EntityPlayer player : mc.world.playerEntities) {
                    if (player == null || player.getHealth() > 0.0F)
                        continue;
                    MinecraftForge.EVENT_BUS.post(new DeathEvent(player));
                }
            } catch (Exception ignored){

            }

            if (mc.currentScreen instanceof GuiGameOver){
                backY = (int) mc.player.posY;
                backZ = (int) mc.player.posZ;
                backX = (int) mc.player.posX;

            }
        }
    }


    public static String serverip = "null";
    @SubscribeEvent
    public void onConnectionEvent(ConnectToServerEvent e) {
         serverip = e.getIp();
    }
    public static int backX, backY,backZ;

    @SubscribeEvent
    public void onPlayer(EventPreMotion event) {
        if (fullNullCheck())
            return;
        Thunderhack.speedManager.updateValues();
        updateRotations();

    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventPostMotion event) {
        if (fullNullCheck())
            return;
        restoreRotations();
    }

    private float yaw;
    private float pitch;

    public void updateRotations() {
        this.yaw = EventManager.mc.player.rotationYaw;
        this.pitch = EventManager.mc.player.rotationPitch;
    }

    public void restoreRotations() {
        EventManager.mc.player.rotationYaw = this.yaw;
        EventManager.mc.player.rotationYawHead = this.yaw;
        EventManager.mc.player.rotationPitch = this.pitch;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0)
            return;
        Thunderhack.serverManager.onPacketReceived();
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);
                MinecraftForge.EVENT_BUS.post(new TotemPopEvent(player));
            }
        }
        if (event.getPacket() instanceof SPacketPlayerListItem && !fullNullCheck() && this.logoutTimer.passedS(1.0D)) {
            SPacketPlayerListItem packet = event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction()))
                return;
            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> (!Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null))
                    .forEach(data -> {
                        String name;
                        EntityPlayer entity;
                        UUID id = data.getProfile().getId();
                        switch (packet.getAction()) {
                            case ADD_PLAYER:
                                name = data.getProfile().getName();
                                MinecraftForge.EVENT_BUS.post(new ConnectionEvent(0, id, name));
                                break;
                            case REMOVE_PLAYER:
                                entity = mc.world.getPlayerEntityByUUID(id);
                                if (entity != null) {
                                    String logoutName = entity.getName();
                                    MinecraftForge.EVENT_BUS.post(new ConnectionEvent(1, entity, id, logoutName));
                                    break;
                                }
                                MinecraftForge.EVENT_BUS.post(new ConnectionEvent(2, id, null));
                                break;
                        }
                    });
        }
        if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketTimeUpdate) {
            Thunderhack.serverManager.update();
        }
        if (event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect)event.getPacket()).getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT) {
            if (!this.chorusTimer.passedMs(100L)) {
                MinecraftForge.EVENT_BUS.post(new ChorusEvent(((SPacketSoundEffect)event.getPacket()).getX(),  ((SPacketSoundEffect)event.getPacket()).getY(),  ((SPacketSoundEffect)event.getPacket()).getZ()));
            }
            this.chorusTimer.reset();
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled())
            return;


        mc.profiler.startSection("thunderhack");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1.0F);





       // prepareGL();
        Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
        Thunderhack.moduleManager.onRender3D(render3dEvent);
       // releaseGL();



        GlStateManager.glLineWidth(1.0F);
        GlStateManager.shadeModel(7424);
       GlStateManager.disableBlend();
       GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
           mc.profiler.endSection();



    }

    @SubscribeEvent
    public void renderHUD(RenderGameOverlayEvent.Post event) {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {


            boolean blend = GL11.glIsEnabled(GL_BLEND);
            boolean depth = GL11.glIsEnabled(GL_DEPTH_TEST);

            ScaledResolution resolution = new ScaledResolution(mc);
            Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
            Thunderhack.moduleManager.onRender2D(render2DEvent);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (blend)
                GL11.glEnable(GL_BLEND);
            if (depth)
                GL11.glEnable(GL_DEPTH_TEST);

        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            Thunderhack.moduleManager.onKeyPressed(Keyboard.getEventKey());
        }


    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                if (event.getMessage().length() > 1) {
                    Thunderhack.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                } else {
                    Command.sendMessage("Неверная команда!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Command.sendMessage(ChatFormatting.RED + "Ошибка команды!");
            }
        }
    }
    private final AtomicBoolean tickOngoing = new AtomicBoolean(false);


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTickHighest(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.tickOngoing.set(true);
         //   NewAC.getInstance().tickRunning.set(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTickLowest(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.tickOngoing.set(false);
            //NewAC.getInstance().tickRunning.set(false);
        }
    }
    public static boolean isMacro = false;

    @SubscribeEvent
    public void onKeyPress(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_NONE) return;
        if (Keyboard.isKeyDown(Keyboard.KEY_F3)) return;

        if (Thunderhack.moduleManager.getModuleByClass(Macros.class).isEnabled()) {
            for (Macro m : MacroManager.getMacros()) {
                if (m.getBind() == event.getKey()) {
                    isMacro = true;
                    m.runMacro();
                    isMacro = false;
                }
            }
        }
    }
}

package com.mrzak34.thunderhack.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.elements.RadarRewrite;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.macro.Macro;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.modules.misc.Macros;
import com.mrzak34.thunderhack.modules.render.PearlESP;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.shaders.BetterDynamicAnimation;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

import java.awt.*;

import static com.mrzak34.thunderhack.modules.misc.Timer.TwoColoreffect;
import static com.mrzak34.thunderhack.util.Util.mc;
import static org.lwjgl.opengl.GL11.*;

public class EventManager {
    public static Module hoveredModule;
    public static boolean serversprint = false;
    public static int backX, backY, backZ;
    public static float visualYaw, visualPitch, prevVisualYaw, prevVisualPitch;
    public static boolean lock_sprint = false;
    public static BetterDynamicAnimation timerAnimation = new BetterDynamicAnimation();
    public static boolean isMacro = false;
    private final Timer chorusTimer = new Timer();
    com.mrzak34.thunderhack.util.Timer lastPacket = new com.mrzak34.thunderhack.util.Timer();
    private float yaw;
    private float pitch;

    public static void setColor(int color) {
        GL11.glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onUnload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!fullNullCheck() && (event.getEntity().getEntityWorld()).isRemote && event.getEntityLiving().equals(mc.player)) {
            Thunderhack.moduleManager.onUpdate();
            Thunderhack.moduleManager.sortModules(true);
        }
        if (!fullNullCheck()) {
            if (Thunderhack.moduleManager.getModuleByClass(ClickGui.class).getBind().getKey() == -1) {
                Command.sendMessage(ChatFormatting.RED + "Default clickgui keybind --> P");
                Thunderhack.moduleManager.getModuleByClass(ClickGui.class).setBind(Keyboard.getKeyIndex("P"));
            }
        }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
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

        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (mc.world != null) {
            try {
                for (EntityPlayer player : mc.world.playerEntities) {
                    if (player == null || player.getHealth() > 0.0F)
                        continue;
                    MinecraftForge.EVENT_BUS.post(new DeathEvent(player));
                }
            } catch (Exception ignored) {

            }

            if (mc.currentScreen instanceof GuiGameOver) {
                backY = (int) mc.player.posY;
                backZ = (int) mc.player.posZ;
                backX = (int) mc.player.posX;
            }
        }

        Thunderhack.moduleManager.onTick();
        ThunderGui2.getInstance().onTick();
        timerAnimation.update();
    }

    @SubscribeEvent
    public void onPlayer(EventPreMotion event) {
        if (fullNullCheck())
            return;
        updateRotations();
        if (!lastPacket.passedMs(100)) {
            Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).m();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketPlayer.Position || e.getPacket() instanceof CPacketPlayer.PositionRotation || e.getPacket() instanceof CPacketPlayer.Rotation) {
            lastPacket.reset();
        }
        if (e.getPacket() instanceof CPacketEntityAction) {
            CPacketEntityAction ent = e.getPacket();
            if (ent.getAction() == CPacketEntityAction.Action.START_SPRINTING) {
                if (lock_sprint) {
                    e.setCanceled(true);
                    return;
                }
                serversprint = true;
            }
            if (ent.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                if (lock_sprint) {
                    e.setCanceled(true);
                    return;
                }
                serversprint = false;
            }
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventPostMotion event) {
        if (fullNullCheck())
            return;
        restoreRotations();
    }

    public void updateRotations() {
        this.yaw = mc.player.rotationYaw;
        this.pitch = mc.player.rotationPitch;
        prevVisualPitch = visualPitch;
        prevVisualYaw = visualYaw;
    }

    public void restoreRotations() {
        visualPitch = mc.player.rotationPitch;
        visualYaw = mc.player.rotationYaw;
        mc.player.rotationYaw = this.yaw;
        mc.player.rotationYawHead = this.yaw;
        mc.player.rotationPitch = this.pitch;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);
                MinecraftForge.EVENT_BUS.post(new TotemPopEvent(player));
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT) {
            if (!this.chorusTimer.passedMs(100L)) {
                MinecraftForge.EVENT_BUS.post(new ChorusEvent(((SPacketSoundEffect) event.getPacket()).getX(), ((SPacketSoundEffect) event.getPacket()).getY(), ((SPacketSoundEffect) event.getPacket()).getZ()));
            }
            this.chorusTimer.reset();
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled())
            return;
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.glLineWidth(1.0F);
        Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
        Thunderhack.moduleManager.onRender3D(render3dEvent);
        GlStateManager.glLineWidth(1.0F);
        glPopAttrib();
    }

    @SubscribeEvent
    public void renderHUD(RenderGameOverlayEvent.Post event) {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {

            boolean blend = glIsEnabled(GL_BLEND);
            boolean depth = glIsEnabled(GL_DEPTH_TEST);

            ScaledResolution resolution = new ScaledResolution(mc);
            Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
            Thunderhack.moduleManager.onRender2D(render2DEvent);
            if (Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).indicator.getValue()) {
                float posX = (resolution.getScaledWidth() / 2f);
                float posY = (resolution.getScaledHeight() - Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).yyy.getValue());

                Color a = TwoColoreffect(Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).color.getValue().getColorObject(), Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + 3.0F * (Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).slices.getValue() * 2.55) / 60);
                Color b = TwoColoreffect(Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).color.getValue().getColorObject(), Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + 3.0F * (Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).slices1.getValue() * 2.55) / 60);
                Color c = TwoColoreffect(Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).color.getValue().getColorObject(), Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + 3.0F * (Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).slices2.getValue() * 2.55) / 60);
                Color d = TwoColoreffect(Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).color.getValue().getColorObject(), Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + 3.0F * (Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).slices3.getValue() * 2.55) / 60);
                RenderUtil.drawBlurredShadow(posX - 33, posY - 3, 66, 16, 10, a);


                float timerStatus = (float) (61f * ((10 - com.mrzak34.thunderhack.modules.misc.Timer.value) / (Math.abs(Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).getMin()) + 10)));
                timerAnimation.setValue(timerStatus);
                timerStatus = (float) timerAnimation.getAnimationD();

                int status = (int) (((10 - com.mrzak34.thunderhack.modules.misc.Timer.value) / (Math.abs(Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).getMin()) + 10)) * 100);

                RoundedShader.drawGradientRound(posX - 31f, posY, 62, 12, 3f, new Color(1), new Color(1), new Color(1), new Color(1));
                RoundedShader.drawGradientRound(posX - 30.5f, posY + 0.5f, timerStatus, 11, 3f, a, b, c, d);
                FontRender.drawCentString6(status >= 99 ? "100%" : status + "%", resolution.getScaledWidth() / 2f, posY + 5.25f, new Color(200, 200, 200, 255).getRGB());
            }
            GlStateManager.resetColor();
            if (blend)
                glEnable(GL_BLEND);
            if (depth)
                glEnable(GL_DEPTH_TEST);

            if (Thunderhack.gps_position != null) {
                float xOffset = resolution.getScaledWidth() / 2f;
                float yOffset = resolution.getScaledHeight() / 2f;

                GlStateManager.pushMatrix();
                float yaw = RadarRewrite.getRotations(Thunderhack.gps_position) - mc.player.rotationYaw;
                glTranslatef(xOffset, yOffset, 0.0F);
                glRotatef(yaw, 0.0F, 0.0F, 1.0F);
                glTranslatef(-xOffset, -yOffset, 0.0F);
                Thunderhack.moduleManager.getModuleByClass(PearlESP.class).drawTriangle(xOffset, yOffset - 50, 12.5f, ClickGui.getInstance().getColor(1).getRGB());
                glTranslatef(xOffset, yOffset, 0.0F);
                glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
                glTranslatef(-xOffset, -yOffset, 0.0F);
                glColor4f(1F, 1F, 1F, 1F);
                GlStateManager.popMatrix();
                FontRender.drawCentString6("gps (" + getDistance(Thunderhack.gps_position) + "m)", (float) get_x(yaw) + xOffset, (float) (yOffset - get_y(yaw)) - 20, -1);

            }
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

    @SubscribeEvent
    public void onKeyPress(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_NONE) return;
        if (Thunderhack.moduleManager.getModuleByClass(Macros.class).isEnabled()) {
            for (Macro m : Thunderhack.macromanager.getMacros()) {
                if (m.getBind() == event.getKey()) {
                    isMacro = true;
                    m.runMacro();
                    isMacro = false;
                }
            }
        }
    }


    private double get_x(double rad) {
        return Math.sin(Math.toRadians(rad)) * (50);
    }

    private double get_y(double rad) {
        return Math.cos(Math.toRadians(rad)) * (50);
    }

    public int getDistance(BlockPos bp) {
        double d0 = mc.player.posX - bp.getX();
        double d2 = mc.player.posZ - bp.getZ();
        return (int) (MathHelper.sqrt(d0 * d0 + d2 * d2));
    }

    public static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }
}

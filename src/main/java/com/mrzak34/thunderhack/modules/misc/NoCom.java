package com.mrzak34.thunderhack.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.gui.misc.GuiScanner;
import com.mrzak34.thunderhack.mixin.mixins.IChunkProviderClient;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.notification.Notification;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.PlayerUtils;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NoCom extends Module {

    public static int scannedChunks = 0;
    public static List<Dot> dots = new ArrayList<>();
    private static NoCom INSTANCE;
    private static BlockPos playerPos = null;
    private static long time = 0;
    private static int count = 0;
    private static int masynax = 0;
    private static int masynay = 0;

    static {
        INSTANCE = new NoCom();
    }

    private final Setting<SubBind> self = this.register(new Setting<>("openGui", new SubBind(Keyboard.KEY_NONE)));
    public Setting<Integer> delay = this.register(new Setting<>("Delay", 200, 0, 1000));
    public Setting<Integer> loop = this.register(new Setting<>("LoopPerTick", 1, 1, 100));
    public Setting<Integer> startX = this.register(new Setting<>("StartX", 0, 0, 1000000));
    public Setting<Integer> startZ = this.register(new Setting<>("StartZ", 0, 0, 1000000));
    public Setting<Integer> scale = this.register(new Setting<>("PointerScale", 4, 1, 4));
    public Setting<Boolean> you = register(new Setting("you", true));
    public Setting<Boolean> loadgui = register(new Setting("LoadGui", true));
    public int couti = 1;
    private int renderDistanceDiameter = 0;
    private int x, z;

    public NoCom() {
        super("NoCom", "эксплоит для поиска-игроков", Category.MISC);
        this.setInstance();
    }

    public static NoCom getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoCom();
        }
        return INSTANCE;
    }

    public static void getgui() {
        Util.mc.displayGuiScreen(GuiScanner.getGuiScanner());
    }

    public static void rerun(int x, int y) {
        dots.clear();
        playerPos = null;
        count = 0;
        time = 0;
        masynax = x;
        masynay = y;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (PlayerUtils.isKeyDown(self.getValue().getKey())) {
            getgui();
        }

        if (GuiScanner.neartrack && scannedChunks > 25) {
            scannedChunks = 0;
        }
        if (GuiScanner.neartrack && scannedChunks == 0) {
            donocom((int) mc.player.posX, (int) mc.player.posZ);
        }
        if (GuiScanner.neartrack) {
            return;
        }

        if (loadgui.getValue()) {
            getgui();
            loadgui.setValue(false);
        }
        if (!GuiScanner.busy) {
            if (!you.getValue()) {
                donocom(startX.getValue(), startZ.getValue());
            } else {
                donocom((int) mc.player.posX, (int) mc.player.posZ);
            }
        } else {
            if (masynax != 0 && masynay != 0) {
                donocom(masynax, masynay);
            }
        }
    }

    public void donocom(int x3, int y3) {
        playerPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);

        if (renderDistanceDiameter == 0) {
            //  IChunkProviderClient chunkProviderClient;
            //  chunkProviderClient = (IChunkProviderClient) mc.world.getChunkProvider();
            renderDistanceDiameter = 8;//(int) Math.sqrt(chunkProviderClient.getLoadedChunks().size());
        }

        if (time == 0) {
            time = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - time > delay.getValue()) {
            for (int i = 0; i < loop.getValue(); i++) {

                int x1 = 0;
                int z1 = 0;
                if (!you.getValue()) {
                    x1 = getSpiralCoords(count)[0] * renderDistanceDiameter * 16 + x3;
                    z1 = getSpiralCoords(count)[1] * renderDistanceDiameter * 16 + y3;
                } else {
                    x1 = getSpiralCoords(count)[0] * renderDistanceDiameter * 16 + x3;
                    z1 = getSpiralCoords(count)[1] * renderDistanceDiameter * 16 + y3;
                }


                final BlockPos position = new BlockPos(x1, 0, z1);
                this.x = x1;
                this.z = z1;
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, playerPos, EnumFacing.EAST));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, position, EnumFacing.EAST));
                dots.add(new Dot(x1 / 16, z1 / 16, DotType.Searched));
                playerPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
                time = System.currentTimeMillis();
                count++;
                ++scannedChunks;
            }
        }
    }

    @SubscribeEvent
    public final void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockChange) {
            final int x = ((SPacketBlockChange) event.getPacket()).getBlockPosition().getX();
            final int z = ((SPacketBlockChange) event.getPacket()).getBlockPosition().getZ();
            IChunkProviderClient chunkProviderClient;
            chunkProviderClient = (IChunkProviderClient) mc.world.getChunkProvider();
            for (Chunk chunk : chunkProviderClient.getLoadedChunks().values()) {
                if (chunk.x == x / 16 || chunk.z == z / 16) {
                    return;
                }
            }
            String shittytext = ("Player spotted at X: " + ChatFormatting.GREEN + x + ChatFormatting.RESET + " Z: " + ChatFormatting.GREEN + z);
            dots.add(new Dot(x / 16, z / 16, DotType.Spotted));
            Command.sendMessage(shittytext);
            GuiScanner.getInstance().consoleout.add(new cout(couti, shittytext));
            ++couti;
            if (GuiScanner.track) {
                GuiScanner.getInstance().consoleout.add(new cout(couti, "tracking x " + x + " z " + z));
                rerun(x, z);
            }
            if (Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).isEnabled()) {
                NotificationManager.publicity(shittytext, 3, Notification.Type.INFO);
            }
        }
    }

    private int[] getSpiralCoords(int n) {
        int x = 0;
        int z = 0;
        int d = 1;
        int lineNumber = 1;
        int[] coords = {0, 0};
        for (int i = 0; i < n; i++) {
            if (2 * x * d < lineNumber) {
                x += d;
                coords = new int[]{x, z};
            } else if (2 * z * d < lineNumber) {
                z += d;
                coords = new int[]{x, z};
            } else {
                d *= -1;
                lineNumber++;
                n++;
            }
        }
        return coords;
    }

    @Override
    public void onEnable() {
        playerPos = null;
        count = 0;
        time = 0;
    }

    @Override
    public void onDisable() {
        dots.clear();
        playerPos = null;
        count = 0;
        time = 0;
    }

    @Override
    public String getDisplayInfo() {
        return x + " , " + z;
    }


    public enum DotType {
        Spotted, Searched
    }

    public static class cout {
        public String string;
        public int posY;


        public cout(int posY, String out) {
            this.posY = posY;
            this.string = out;
        }
    }

    public class Dot {
        public DotType type;
        public int posX, posY;
        public Color color;
        public int ticks;

        public Dot(int posX, int posY, DotType type) {
            this.posX = posX;
            this.posY = posY;
            this.type = type;
            this.ticks = 0;
        }
    }
}

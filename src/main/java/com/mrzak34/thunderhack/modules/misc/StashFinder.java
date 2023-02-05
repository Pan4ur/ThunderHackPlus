package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.notification.NotificationType;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;


import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import com.mrzak34.thunderhack.util.Timer;

import static com.mrzak34.thunderhack.util.PlayerUtils.getPlayerPos;

public class StashFinder extends Module {

    public StashFinder() {super("StashFinder", "ищет стеши в зоне-прогрузки", Module.Category.MISC);}

    private Timer timer = new Timer();
    private HashMap<Chunk, ArrayList<TileEntity>> map = new HashMap<>();
    private ArrayList<Chunk> loggedChunks = new ArrayList<Chunk>();

    public Setting <Integer> amount = this.register ( new Setting <> ( "Amount", 15, 1, 100 ) );
    public Setting< Boolean > windowsAlert = this.register ( new Setting <> ( "WindowsAlert" , true) );
    public Setting< Boolean > sound = this.register ( new Setting <> ( "Sound" , true) );
    public Setting< Boolean > chatMessage = this.register ( new Setting <> ( "chatMessage" , true) );
    public Setting< Boolean > hoppers = this.register ( new Setting <> ( "Hoppers" , true) );
    public Setting< Boolean > shulkers = this.register ( new Setting <> ( "shulkers" , true) );
    public Setting< Boolean > dispensers = this.register ( new Setting <> ( "dispensers" , true) );
    public Setting< Boolean > droppers = this.register ( new Setting <> ( "droppers" , true) );
    public Setting< Boolean > chests = this.register ( new Setting <> ( "chests" , true) );
    public Setting< Boolean > notif = this.register ( new Setting <> ( "notification" , true) );

    final static private String pathSave = "oyvey/stashes/StashLogger.txt";

    @Override
    public void onDisable() {
        loggedChunks.clear();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
        if (timer.passedMs(500) && mc.player != null && mc.world != null && mc.world.loadedEntityList != null) {
            timer.reset();

            map.clear();
            for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
                if (isValid(tileEntity)) {
                    Chunk chunk = mc.world.getChunk(tileEntity.getPos());

                    ArrayList<TileEntity> list = new ArrayList<TileEntity>();
                    if (map.containsKey(chunk)) list = map.get(chunk);
                    list.add(tileEntity);
                    map.put(chunk, list);
                }
            }

            for (Chunk chunk : map.keySet()) {
                if (map.get(chunk).size() >= amount.getValue()) {
                    if (!loggedChunks.contains(chunk)) {
                        loggedChunks.add(chunk);
                        log(chunk, map.get(chunk));
                    }
                }
            }
        }
    }

    public void log(Chunk chunk, ArrayList<TileEntity> list) {
        //Someone put amount to 0?
        if (list.size() <= 0) {
            return;
        }

        int x = list.get(0).getPos().getX();
        int z = list.get(0).getPos().getZ();

        //Send chat message
        if (chatMessage.getValue()) {
            Command.sendMessage("Нашел чанк с " + list.size() + " на X: " + x + " Z: " + z);
        }
        if(notif.getValue()){
            NotificationManager.publicity("StashFinder","Нашел чанк с " + list.size() + " на X: " + x + " Z: " + z,3, NotificationType.SUCCESS );
        }

        //Play sound
        if (sound.getValue()) {
            mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 100.0f, 18.0F, true);
        }

        //Send windows alert
        if (windowsAlert.getValue()) {
            sendWindowsAlert("Нашел стеш!");
        }

        //Log it to the file
        new Thread(() -> {
            try {
                File file = new File(pathSave);
                if (!file.exists()) file.createNewFile();

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                LocalDateTime now = LocalDateTime.now();

                bw.write("X: " + x + " Z: " + z + " Found " + list.size() + " container blocks - " + dtf.format(now));
                bw.newLine();
                bw.close();
            } catch (Exception e) {
                System.out.println(" - Error logging chunk. StashLogger");
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendWindowsAlert(String message) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);

            trayIcon.displayMessage("ThunderHack", message, MessageType.INFO);
        } catch (Exception ignored) {

        }
    }

    public boolean isValid(TileEntity tileEntity) {
        return chests.getValue() && tileEntity instanceof TileEntityChest
                || droppers.getValue() && tileEntity instanceof TileEntityDropper
                || dispensers.getValue() && tileEntity instanceof TileEntityDispenser
                || shulkers.getValue() && tileEntity instanceof TileEntityShulkerBox
                || hoppers.getValue() && tileEntity instanceof TileEntityDropper;
    }


}

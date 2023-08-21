package com.mrzak34.thunderhack;

import com.mrzak34.thunderhack.gui.fontstuff.CFontRenderer;
import com.mrzak34.thunderhack.manager.*;
import com.mrzak34.thunderhack.util.ThunderUtils;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.dism.EntityGib;
import com.mrzak34.thunderhack.util.dism.RenderGib;
import com.mrzak34.thunderhack.util.ffp.NetworkHandler;
import com.mrzak34.thunderhack.util.phobos.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


@Mod(modid = "thunderhack", name = "ThunderHack", version = "2.41", acceptableRemoteVersions = "*")
public class Thunderhack {
    public static Logger LOG = LogManager.getLogger("ThunderHack");

    @Mod.Instance
    public static Thunderhack INSTANCE;
    public static float TICK_TIMER = 1f;
    public static java.util.List<String> alts = new ArrayList<>();
    public static long initTime;
    public static BlockPos gps_position;
    public static Color copy_color;
    public static NoMotionUpdateService noMotionUpdateService;


    /*-----------------    Managers  ---------------------*/
    public static ServerTickManager servtickManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static EntityProvider entityProvider;
    public static CommandManager commandManager;
    public static SetDeadManager setDeadManager;
    public static NetworkHandler networkHandler;
    public static ThreadManager threadManager;
    public static SwitchManager switchManager;
    public static ReloadManager reloadManager;
    public static CombatManager combatManager;
    public static ServerManager serverManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static EventManager eventManager;
    public static MacroManager macromanager;
    public static Scheduler yahz;
    public static CFontRenderer fontRenderer;
    /*--------------------------------------------------------*/




    /*-----------------    Fonts  ---------------------*/
    public static CFontRenderer fontRenderer2;
    public static CFontRenderer fontRenderer3;
    public static CFontRenderer fontRenderer4;
    public static CFontRenderer fontRenderer5;
    public static CFontRenderer fontRenderer6;
    public static CFontRenderer fontRenderer7;
    public static CFontRenderer fontRenderer8;
    public static CFontRenderer icons;
    public static CFontRenderer middleicons;
    public static CFontRenderer BIGicons;
    private static boolean unloaded = false;
    /*--------------------------------------------------------*/

    public static void load() {
        // Configuration loading
        ConfigManager.loadAlts();
        ConfigManager.loadSearch();
        unloaded = false;

        // Unload and initialize reloadManager
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }

        ConfigManager.init();

        try {
            // Font initialization
            fontRenderer = loadFont("/fonts/ThunderFont2.ttf", 24.f);
            fontRenderer2 = loadFont("/fonts/ThunderFont3.ttf", 28.f);
            fontRenderer3 = loadFont("/fonts/ThunderFont2.ttf", 18.f);
            fontRenderer4 = loadFont("/fonts/ThunderFont2.ttf", 50.f);
            fontRenderer5 = loadFont("/fonts/Monsterrat.ttf", 12.f);
            fontRenderer6 = loadFont("/fonts/Monsterrat.ttf", 14.f);
            fontRenderer7 = loadFont("/fonts/Monsterrat.ttf", 10.f);
            fontRenderer8 = loadFont("/fonts/ThunderFont3.ttf", 62.f);

            LOG.info("FontRenderer initialized");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Service initialization
        noMotionUpdateService = new NoMotionUpdateService();

        // Managers initialization
        initializeManagers();

        FriendManager.loadFriends();
        yahz.init();
        ConfigManager.load(ConfigManager.getCurrentConfig());
        moduleManager.onLoad();
        ThunderUtils.syncCapes();
        MacroManager.onLoad();

        // Add current username to alts list
        if (Util.mc.getSession() != null && !alts.contains(Util.mc.getSession().getUsername())) {
            alts.add(Util.mc.getSession().getUsername());
        }
    }

    private static CFontRenderer loadFont(String fontPath, float fontSize) throws IOException, FontFormatException {
        return new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream(fontPath))).deriveFont(fontSize), true, true);
    }

    private static void initializeManagers() {
        servtickManager = new ServerTickManager();
        positionManager = new PositionManager();
        rotationManager = new RotationManager();
        commandManager = new CommandManager();
        entityProvider = new EntityProvider();
        networkHandler = new NetworkHandler();
        setDeadManager = new SetDeadManager();
        serverManager = new ServerManager();
        threadManager = new ThreadManager();
        switchManager = new SwitchManager();
        combatManager = new CombatManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        eventManager = new EventManager();
        macromanager = new MacroManager();
        yahz = new Scheduler();

        LOG.info("Services Started.");

        // Managers initialization
        noMotionUpdateService.init();
        positionManager.init();
        rotationManager.init();
        servtickManager.init();
        moduleManager.init();
        entityProvider.init();
        setDeadManager.init();
        combatManager.init();
        switchManager.init();
        eventManager.init();
        serverManager.init();

        LOG.info("Managers initialized.");
    }


    public static void unload(boolean initReloadManager) {
        Display.setTitle("Minecraft 1.12.2");
        if (initReloadManager) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        ConfigManager.saveAlts();
        ConfigManager.saveSearch();
        FriendManager.saveFriends();
        if (!unloaded) {
            eventManager.onUnload();

            /*-----------------    Unload Services  ---------------------*/
            noMotionUpdateService.unload();
            /*--------------------------------------------------------*/

            /*-----------------    Unload Managers  ---------------------*/
            positionManager.unload();
            rotationManager.unload();
            servtickManager.unload();
            entityProvider.unload();
            setDeadManager.unload();
            combatManager.unload();
            switchManager.unload();
            serverManager.unload();
            moduleManager.onUnload();
            /*--------------------------------------------------------*/

            yahz.unload();

            /*-----------------    Save Managers  ---------------------*/
            ConfigManager.save(ConfigManager.getCurrentConfig());
            MacroManager.saveMacro();
            /*--------------------------------------------------------*/

            moduleManager.onUnloadPost();

            unloaded = true;
        }

        /*-----------------    Nullify Managers  ---------------------*/
        eventManager = null;
        friendManager = null;
        fontRenderer = null;
        macromanager = null;
        networkHandler = null;
        commandManager = null;
        serverManager = null;
        servtickManager = null;
        /*--------------------------------------------------------*/
    }

    public static void reload() {
        Thunderhack.unload(false);

        Thunderhack.load();
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityGib.class, RenderGib::new);

        GlobalExecutor.EXECUTOR.submit(Sphere::cacheSphere);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("ThunderHack+");

        initTime = System.currentTimeMillis();

        Thunderhack.load();

        MinecraftForge.EVENT_BUS.register(networkHandler);
    }

}


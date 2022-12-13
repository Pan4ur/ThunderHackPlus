package com.mrzak34.thunderhack;

import com.mrzak34.thunderhack.gui.thundergui.fontstuff.*;
import com.mrzak34.thunderhack.manager.*;
import com.mrzak34.thunderhack.util.ThunderUtils;
import com.mrzak34.thunderhack.util.dism.EntityGib;
import com.mrzak34.thunderhack.util.dism.RenderGib;
import com.mrzak34.thunderhack.util.ffpshit.NetworkHandler;
import com.mrzak34.thunderhack.util.phobos.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.util.Objects;


@Mod(
        modid = "thunderhack",
        name = "ThunderHack",
        version = "2.35")


public class Thunderhack {

    public static final String MODID = "thunderhack";
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static EnemyManager enemyManager;
    public static NetworkHandler networkHandler;
    public static MacroManager macromanager;
    public static CFontRenderer fontRenderer;
    public static CFontRenderer2 fontRenderer2;
    public static CFontRenderer3 fontRenderer3;
    public static CFontRenderer4 fontRenderer4;
    public static CFontRenderer5 fontRenderer5;
    public static CFontRenderer6 fontRenderer6;
    public static PotionManager potionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static EntityProvider entityProvider;


    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static SetDeadManager setDeadManager;
    public static ThreadManager threadManager;
    public static ServerTickManager servtickManager;
    public static SwitchManager switchManager;
    public static CombatManager combatManager;
    public static Scheduler yahz;
    public static NoMotionUpdateService nobitches;


    public static String ServerIp;
    public static int ServerPort;

    @Mod.Instance
    public static Thunderhack INSTANCE;
    private static boolean unloaded;
    static {
        unloaded = false;
    }
    public static float TICK_TIMER = 1;
    public static void load() {
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }


        try {
            Font verdanapro = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont2.ttf")));
            verdanapro = verdanapro.deriveFont( 24.f );
            fontRenderer = new CFontRenderer( verdanapro, true, true );

            Font verdanapro2 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont3.ttf")));
            verdanapro2 = verdanapro2.deriveFont( 36.f );
            fontRenderer2 = new CFontRenderer2( verdanapro2, true, true );

            Font verdanapro3 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont2.ttf")));
            verdanapro3 = verdanapro3.deriveFont( 18.f );
            fontRenderer3 = new CFontRenderer3( verdanapro3, true, true );

            Font verdanapro4 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont2.ttf")));
            verdanapro4 = verdanapro4.deriveFont( 50.f );
            fontRenderer4 = new CFontRenderer4( verdanapro4, true, true );

            Font verdanapro5 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/Monsterrat.ttf")));
            verdanapro5 = verdanapro5.deriveFont( 12.f );
            fontRenderer5 = new CFontRenderer5( verdanapro5, true, true );

            Font verdanapro6 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/Monsterrat.ttf")));
            verdanapro6 = verdanapro6.deriveFont( 14.f );
            fontRenderer6 = new CFontRenderer6( verdanapro6, true, true );
        } catch ( Exception e ) {
            e.printStackTrace( );
            return;
        }


      //  ThunderUtils.syncCapes();
        entityProvider = new EntityProvider();

        positionManager = new PositionManager();
        rotationManager = new RotationManager();
        threadManager = new ThreadManager();
        servtickManager = new ServerTickManager();
        switchManager = new SwitchManager();
        combatManager = new CombatManager();
        yahz = new Scheduler();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        enemyManager = new EnemyManager();
        eventManager = new EventManager();
        macromanager = new MacroManager();
        networkHandler = new NetworkHandler();
        setDeadManager = new SetDeadManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        configManager = new ConfigManager();
        nobitches = new NoMotionUpdateService();

        moduleManager.init();
        configManager.init();
        eventManager.init();
        positionManager.init();
        rotationManager.init();
        servtickManager.init();
        switchManager.init();
        combatManager.init();
        yahz.init();
        setDeadManager.init();
        nobitches.init();
        entityProvider.init();


        moduleManager.onLoad();
    }

    public static void unload(boolean unload) {
        Display.setTitle("Minecraft 1.12.2");

        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        Thunderhack.onUnload();

        eventManager = null;
        friendManager = null;
        speedManager = null;
        fontRenderer = null;
        enemyManager = null;
        macromanager = null;
        networkHandler = null;
        configManager = null;
        commandManager = null;
        serverManager = null;
        fileManager = null;
        potionManager = null;
    }

    public static void reload() {
        Thunderhack.unload(false);
        Thunderhack.load();
    }


    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(Thunderhack.configManager.config.replaceFirst("ThunderHack/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    private static final Logger LOGGER = LogManager.getLogger("thunderhack");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityGib.class, RenderGib::new);
        GlobalExecutor.EXECUTOR.submit(() -> Sphere.cacheSphere(LOGGER));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("ThunderHack+");
        Thunderhack.load();
        MinecraftForge.EVENT_BUS.register(networkHandler);
    }
}


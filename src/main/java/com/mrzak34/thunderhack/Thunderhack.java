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
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;


@Mod(modid = "thunderhack", name = "ThunderHack", version = "2.40", acceptableRemoteVersions = "*")
public class Thunderhack {

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
        ConfigManager.loadAlts();
        ConfigManager.loadSearch();
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }

        ConfigManager.init();

        try {
            fontRenderer = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont2.ttf"))).deriveFont(24.f), true, true);
            fontRenderer2 = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont3.ttf"))).deriveFont(28.f), true, true);
            fontRenderer3 = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont2.ttf"))).deriveFont(18.f), true, true);
            fontRenderer4 = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont2.ttf"))).deriveFont(50.f), true, true);
            fontRenderer5 = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/Monsterrat.ttf"))).deriveFont(12.f), true, true);
            fontRenderer6 = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/Monsterrat.ttf"))).deriveFont(14.f), true, true);
            fontRenderer7 = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/Monsterrat.ttf"))).deriveFont(10.f), true, true);
            fontRenderer8 = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/ThunderFont3.ttf"))).deriveFont(62.f), true, true);
            icons = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/icons.ttf"))).deriveFont(20.f), true, true);
            middleicons = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/icons.ttf"))).deriveFont(46.f), true, true);
            BIGicons = new CFontRenderer(Font.createFont(Font.PLAIN, Objects.requireNonNull(Thunderhack.class.getResourceAsStream("/fonts/icons.ttf"))).deriveFont(72.f), true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        noMotionUpdateService = new NoMotionUpdateService();
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
        FriendManager.loadFriends();
        yahz.init();
        ConfigManager.load(ConfigManager.getCurrentConfig());
        moduleManager.onLoad();
        ThunderUtils.syncCapes();
        MacroManager.onLoad();
        if (Util.mc.getSession() != null && !alts.contains(Util.mc.getSession().getUsername())) {
            alts.add(Util.mc.getSession().getUsername());
        }
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

            noMotionUpdateService.unload();
            positionManager.unload();
            rotationManager.unload();
            servtickManager.unload();
            entityProvider.unload();
            setDeadManager.unload();
            combatManager.unload();
            switchManager.unload();
            serverManager.unload();
            yahz.unload();
            moduleManager.onUnload();
            ConfigManager.save(ConfigManager.getCurrentConfig());
            MacroManager.saveMacro();
            moduleManager.onUnloadPost();
            unloaded = true;
        }

        eventManager = null;
        friendManager = null;
        fontRenderer = null;
        macromanager = null;
        networkHandler = null;
        commandManager = null;
        serverManager = null;
        servtickManager = null;
    }

    public static void reload() {
        Thunderhack.unload(false);
        Thunderhack.load();
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityGib.class, RenderGib::new);
        GlobalExecutor.EXECUTOR.submit(() -> Sphere.cacheSphere());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("ThunderHack+");
        initTime = System.currentTimeMillis();
        Thunderhack.load();
        MinecraftForge.EVENT_BUS.register(networkHandler);
    }

}


package com.mrzak34.thunderhack.manager;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.gui.clickui.ClickUI;
import com.mrzak34.thunderhack.gui.hud.*;
import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.windows.WindowsGui;
import com.mrzak34.thunderhack.modules.client.*;
import com.mrzak34.thunderhack.modules.client.Particles;
import com.mrzak34.thunderhack.modules.combat.*;
import com.mrzak34.thunderhack.modules.funnygame.*;
import com.mrzak34.thunderhack.modules.movement.Speed;
import com.mrzak34.thunderhack.modules.misc.*;
import com.mrzak34.thunderhack.modules.movement.*;
import com.mrzak34.thunderhack.modules.movement.NoSlow;
import com.mrzak34.thunderhack.modules.player.*;
import com.mrzak34.thunderhack.modules.render.*;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.PlayerUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager extends Feature {
    public ArrayList<Module> modules = new ArrayList<>();
    public List<Module> sortedModules = new ArrayList<Module>();

    public void init() {
        this.modules.add(new ClickGui());
        this.modules.add(new ExtraTab());
        this.modules.add(new GuiMove());
        this.modules.add(new AutoSoup());
        this.modules.add(new AimAssist());
        this.modules.add(new AutoBuy());
        this.modules.add(new AutoTotem());
        this.modules.add(new AutoBuff());
        this.modules.add(new DiscordEmbeds());
        this.modules.add(new Welcomer());
        this.modules.add(new FastFall());
        this.modules.add(new Search());
        this.modules.add(new EFly());

        this.modules.add(new AutoFlyme());
        this.modules.add(new ReverseStep());
        this.modules.add(new Step());
        this.modules.add(new NoInteract());
        this.modules.add(new Strafe());
        this.modules.add(new ClanInvite());
        this.modules.add(new AntiBadEffects());
        this.modules.add(new Dismemberment());
        this.modules.add(new Reach());
        this.modules.add(new PasswordHider());
        this.modules.add(new LowHPScreen());
        this.modules.add(new AutoCrystal());
        this.modules.add(new AutoMine());
        this.modules.add(new PvPResources());
        this.modules.add(new ElytraFly2b2tNew());
        this.modules.add(new CivBreaker());
        this.modules.add(new AntiTPhere());
        this.modules.add(new PearlBait());
        this.modules.add(new AutoSheep());
        this.modules.add(new BoatFly());
        this.modules.add(new TargetHud());
        this.modules.add(new Aura());
        this.modules.add(new NoSlow());
        this.modules.add(new HitParticles2());
        this.modules.add(new Spider());
        this.modules.add(new AutoExplosion());
        this.modules.add(new AutoAmericano());
        this.modules.add(new BlockHighlight());
        this.modules.add(new TickShift());
        this.modules.add(new StorageEsp());
        this.modules.add(new Ambience());
        this.modules.add(new Ghost());
        this.modules.add(new BowAim());
        this.modules.add(new Shulkerception());
        this.modules.add(new FunnyClicker());
        this.modules.add(new Interactions());
        this.modules.add(new ThirdPersView());
        this.modules.add(new SolidWeb());
        this.modules.add(new NoCom());
        this.modules.add(new DMGFly());
        this.modules.add(new Sprint());
        this.modules.add(new FreeLook());
        this.modules.add(new ItemScroller());
        this.modules.add(new Quiver());
        this.modules.add(new NoFall());
        this.modules.add(new AutoReconnect());
        this.modules.add(new KeepSprint());
        this.modules.add(new LevitationControl());
        this.modules.add(new CustomEnchants());
        this.modules.add(new HoleESP());
        this.modules.add(new Skeleton());
        this.modules.add(new Trajectories());
        this.modules.add(new LongJump());
        this.modules.add(new FakePlayer());
        this.modules.add(new TpsSync());
        this.modules.add(new ItemShaders());
        this.modules.add(new ThunderHackGui());
        this.modules.add(new ElytraSwap());
        this.modules.add(new VisualRange());
        this.modules.add(new StashFinder());
        this.modules.add(new PearlESP());
        this.modules.add(new EntityESP());
        this.modules.add(new AutoFish());
        this.modules.add(new PlayerTrails());
        this.modules.add(new CrystalChams());
        this.modules.add(new FreeCam());
        this.modules.add(new PacketFly());
        this.modules.add(new Windows());
        this.modules.add(new Timer());
        this.modules.add(new AutoTrap());
        this.modules.add(new NoEntityTrace());
        this.modules.add(new PistonAura());
        this.modules.add(new LiquidInteract());
        this.modules.add(new Weather());
        this.modules.add(new Models());
        this.modules.add(new Jesus());
        this.modules.add(new EChestFarmer());
        this.modules.add(new SeedOverlay());
        this.modules.add(new MiddleClick());
        this.modules.add(new NoInterp());
        this.modules.add(new Anchor());
        this.modules.add(new NotificationManager());
        this.modules.add(new Speedmine());
        this.modules.add(new NoVoid());
        this.modules.add(new NoHandShake());
        this.modules.add(new WTap());
        this.modules.add(new AutoRegear());
        this.modules.add(new AutoLeave());
        this.modules.add(new ShiftInterp());
        this.modules.add(new Particles());
        this.modules.add(new ElytraFlight());
        this.modules.add(new RusherScaffold());
        this.modules.add(new PortalGodMode());
        this.modules.add(new FpsCounter());
        this.modules.add(new Blink());
        this.modules.add(new NoServerRotation());
        this.modules.add(new MainSettings());
        this.modules.add(new TPSCounter());
        this.modules.add(new WaterMark());
        this.modules.add(new PrinterBypass());
        this.modules.add(new Player());
        this.modules.add(new Speedometer());
        this.modules.add(new ArmorHud());
        this.modules.add(new HighJump());
        this.modules.add(new Surround());
        this.modules.add(new LogoutSpots());
        this.modules.add(new LegitStrafe());
        this.modules.add(new BackTrack());
        this.modules.add(new NameProtect());
        this.modules.add(new FogColor());
        this.modules.add(new LegitScaff());
        this.modules.add(new PopChams());
        this.modules.add(new ContainerPreviewModule());
        this.modules.add(new EbatteSratte());
        this.modules.add(new RPC());
        this.modules.add(new ViewModel());
        this.modules.add(new NoRender());
        this.modules.add(new VoidESP());
        this.modules.add(new TunnelESP());
        this.modules.add(new Criticals());
        this.modules.add(new EzingKids());
        this.modules.add(new IceSpeed());
        this.modules.add(new Shaders());
        this.modules.add(new Indicators());
        this.modules.add(new ChestStealer());
        this.modules.add(new InvManager());
        this.modules.add(new FastPlace2());
        this.modules.add(new AutoArmor());
        this.modules.add(new PacketCounter());
        this.modules.add(new ChorusESP());
        this.modules.add(new GroundBoost());
        this.modules.add(new BeakonESP());
        this.modules.add(new FGTotem());
        this.modules.add(new Speed());
        this.modules.add(new Burrow());
        this.modules.add(new AntiHunger());
        this.modules.add(new TriggerBot());
        this.modules.add(new FullBright());
        this.modules.add(new Velocity());
        this.modules.add(new NameTags());
        this.modules.add(new AutoTPaccept());
        this.modules.add(new AntiDisconnect());
        this.modules.add(new XRay());
        this.modules.add(new NoJumpDelay());
        this.modules.add(new Flight());
        this.modules.add(new HitBoxes());
        this.modules.add(new NGriefCleaner());
        this.modules.add(new MultiConnect());
        this.modules.add(new RadarRewrite());
        this.modules.add(new com.mrzak34.thunderhack.gui.hud.ArrayList());
        this.modules.add(new Coords());
        this.modules.add(new DiscordWebhook());
        this.modules.add(new KillEffect());
        this.modules.add(new PacketFly2());
        this.modules.add(new RWKTLeave());
        this.modules.add(new AutoTool());
        this.modules.add(new TargetStrafe());
        this.modules.add(new EZbowPOP());
        this.modules.add(new BowSpam());
        this.modules.add(new ItemESP());
        this.modules.add(new DMGParticles());
        this.modules.add(new AutoRespawn());
        this.modules.add(new PhotoMath());
        this.modules.add(new KDShop());
        this.modules.add(new AntiBowBomb());
        this.modules.add(new EffectsRemover());
        this.modules.add(new TrueDurability());
        this.modules.add(new ItemPhysics());
        this.modules.add(new Potions());
        this.modules.add(new NoServerSlot());
        this.modules.add(new CevBreaker());
        this.modules.add(new AntiBot());
        this.modules.add(new AntiTittle());
        this.modules.add(new CoolCrosshair());
        this.modules.add(new AutoCappRegear());
        this.modules.add(new ToolTips());
        this.modules.add(new Macros());
        this.modules.add(new AutoGApple());
        this.modules.add(new HudEditor());
        this.modules.add(new AutoPot());
        this.modules.add(new MSTSpeed());
        this.modules.add(new Animations());
        this.modules.add(new C4Aura());
        this.modules.add(new AutoEZ());
        this.modules.add(new MessageAppend());
        this.modules.add(new ImageESP());
        this.modules.add(new PyroRadar());
        this.modules.add(new JumpCircle());
        this.modules.add(new ClickTP());
        this.modules.add(new KeyBinds());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }
    public ArrayList<Module> getModulesByCategory(ThunderGui.Categories aboba) {
        ArrayList<Module> gingerbread = null;
        if (aboba == ThunderGui.Categories.FUNNYGAME) {
            gingerbread = getModulesByCategory(Module.Category.FUNNYGAME);
        }
        if (aboba == ThunderGui.Categories.COMBAT) {
            gingerbread = getModulesByCategory(Module.Category.COMBAT);
        }
        if (aboba == ThunderGui.Categories.MOVEMENT) {
            gingerbread = getModulesByCategory(Module.Category.MOVEMENT);
        }
        if (aboba == ThunderGui.Categories.MISC) {
            gingerbread = getModulesByCategory(Module.Category.MISC);
        }
        if (aboba == ThunderGui.Categories.CLIENT) {
            gingerbread = getModulesByCategory(Module.Category.CLIENT);
        }
        if (aboba == ThunderGui.Categories.RENDER) {
            gingerbread = getModulesByCategory(Module.Category.RENDER);
        }
        if (aboba == ThunderGui.Categories.PLAYER) {
            gingerbread = getModulesByCategory(Module.Category.PLAYER);
        }
        if (aboba == ThunderGui.Categories.HUD) {
            gingerbread = getModulesByCategory(Module.Category.HUD);
        }
        return gingerbread;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.sort(Comparator.comparing(Module::getName));
        this.modules.stream().filter(Module::listening).forEach(((EventBus) MinecraftForge.EVENT_BUS)::register);
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
        this.modules.forEach(module -> {
            if (!PlayerUtils.isKeyDown(module.getBind().getKey()) && module.isEnabled() && module.getBind().isHold()) {
                module.disable();
            }
        });
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> FontRender.getStringWidth6(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }


    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof ClickUI || ModuleManager.mc.currentScreen instanceof ThunderGui || ModuleManager.mc.currentScreen instanceof WindowsGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }
}

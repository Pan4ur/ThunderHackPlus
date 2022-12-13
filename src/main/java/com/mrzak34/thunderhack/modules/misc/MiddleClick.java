package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.EventStage;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.gui.misc.GuiMiddleClickMenu;
import com.mrzak34.thunderhack.gui.misc.NewGuiMiddleClickMenu;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.phobos.DamageUtil;

import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

import static com.mrzak34.thunderhack.util.phobos.HelperRotation.acquire;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

public class MiddleClick extends Module{
    public MiddleClick() {
    super("MiddleClick", "меню на колесико-мыши", Category.MISC, true, false, false);
        this.setInstance();
        module = this;
    }
    public static MiddleClick getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MiddleClick();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private static MiddleClick INSTANCE = new MiddleClick();
    public Setting<Float> scalefactor = register(new Setting("Raytrace", 2.0F, 0.1F, 4.0F));

    private  Setting<Action> action = register(new Setting<>("Action", Action.MENU));
    private  Setting<Integer> range = register(new Setting<>("Range", 40, 10, 250, v-> action.getValue() == Action.MENU));

    public Setting<Float> circus = register(new Setting("circus", 2.0F, 0.1F, 300.0F));

    public Setting<Boolean> fm = register(new Setting("FriendMessage", true));


    public Timer timr = new Timer();





    public Setting<Boolean> throughWalls = register(new Setting("ThroughWalls", true,v-> action.getValue() == Action.MENU ));
    public Setting<Boolean> rocket = register(new Setting("Rocket", false));
    public Setting<Boolean> ep = register(new Setting("EP", false));
    public Setting<Boolean> xp = register(new Setting("XP", false));
    public Setting<Boolean> xpInHoles = register(new Setting("XPInHoles", false, v-> xp.getValue()));

    public Setting<String> commandtext = this.register(new Setting<String>("Buttname", "SampleButt"));
    public Setting<String> commandname = this.register(new Setting<String>("Command", "/kick"));

    private enum Action {
        MENU, FRIEND, MISC, NEWMENU
    }



    private Timer xpTimer = new Timer();

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        if (action.getValue() == Action.MENU) {
            if (!GameSettings.isKeyDown(mc.gameSettings.keyBindPickBlock)) return;
            EntityPlayer rayTracedEntity = getEntityUnderMouse(range.getValue());
            if (rayTracedEntity != null) {
                xpTimer.reset();
                if (mc.currentScreen == null) {
                    Util.mc.displayGuiScreen(new GuiMiddleClickMenu(rayTracedEntity));
                }
            }
        }
        if (action.getValue() == Action.NEWMENU) {
            if (!GameSettings.isKeyDown(mc.gameSettings.keyBindPickBlock)) return;
            EntityPlayer rayTracedEntity = getEntityUnderMouse(range.getValue());
            if (rayTracedEntity != null) {
                xpTimer.reset();
                if (mc.currentScreen == null) {
                    Util.mc.displayGuiScreen(new NewGuiMiddleClickMenu(rayTracedEntity));
                }
            }
        }
        if (action.getValue() == Action.FRIEND && mc.objectMouseOver.entityHit != null) {
            if (!GameSettings.isKeyDown(mc.gameSettings.keyBindPickBlock)) return;
            Entity entity = mc.objectMouseOver.entityHit;
            if (entity instanceof EntityPlayer && timr.passedMs(2500)) {
                if (Thunderhack.friendManager.isFriend(entity.getName())) {
                    Thunderhack.friendManager.removeFriend(entity.getName());
                    Command.sendMessage("Removed §b" + entity.getName() + "§r as a friend!");
                } else {
                    Thunderhack.friendManager.addFriend(entity.getName());
                    if (fm.getValue()) {
                        mc.player.sendChatMessage("/w "+ entity.getName() + " i friended u at ThunderHackPlus");
                    }
                    Command.sendMessage("Added §b" + entity.getName() + "§r as a friend!");
                }
                xpTimer.reset();
                timr.reset();
                return;
            }
        }

        if (rocket.getValue() && findRocketSlot() != -1) {
            if (!GameSettings.isKeyDown(mc.gameSettings.keyBindPickBlock)) return;
            xpTimer.reset();
            int rocketSlot = findRocketSlot();
            int originalSlot = mc.player.inventory.currentItem;

            if (rocketSlot != -1) {
                mc.player.inventory.currentItem = rocketSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(rocketSlot));

                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);

                mc.player.inventory.currentItem = originalSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(originalSlot));
                return;
            }
        }

        if (ep.getValue() && (!xp.getValue() || (xpInHoles.getValue() && !BlockUtils.isHole(new BlockPos(mc.player))))) {
            if (!GameSettings.isKeyDown(mc.gameSettings.keyBindPickBlock)) return;
            int epSlot = findEPSlot();
            int originalSlot = mc.player.inventory.currentItem;

            if (epSlot != -1) {
                mc.player.inventory.currentItem = epSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(epSlot));

                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);

                mc.player.inventory.currentItem = originalSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(originalSlot));
            }
        }



    }

    public EntityPlayer getEntityUnderMouse(int range) {
        Entity entity = mc.getRenderViewEntity();

        if (entity != null) {
            Vec3d pos = mc.player.getPositionEyes(1F);
            for (float i = 0F; i < range; i += 0.5F) {
                pos = pos.add(mc.player.getLookVec().scale(0.5));
                if (!throughWalls.getValue()) {
                    if (mc.world.getBlockState(new BlockPos(pos.x, pos.y, pos.z)).getBlock() != Blocks.AIR) return null;
                }
                for (EntityPlayer player : mc.world.playerEntities) {
                    if (player == mc.player) continue;
                    AxisAlignedBB bb = player.getEntityBoundingBox();
                    if (bb == null) continue;
                    if (player.getDistance(mc.player) > 6) {
                        bb = bb.grow(0.5);
                    }
                    if (bb.contains(pos)) return player;
                }
            }
        }

        return null;
    }


    @SubscribeEvent
    public void invoke(MouseEvent event) {
        if(!xp.getValue()) return;
        if (normalMC.getValue()) {
            mayCancel(event);
        }
    }

    @SubscribeEvent
    public void onPreMotion(EventPreMotion event){
        if(!xp.getValue()) return;

        if (feetExp.getValue() && (InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE) && Mouse.isButtonDown(1) || isMiddleClick()))
        {
            mc.player.rotationPitch = 90f;
        }
    }

    @SubscribeEvent
    public void onPostMotion(EventPostMotion event) {
        if (xp.getValue()) {
            if (isMiddleClick() && !(wasteStop.getValue() && isWasting()) && (whileEating.getValue() || !(mc.player.getActiveItemStack().getItem() instanceof ItemFood)))
            {
                int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
                if (slot != -1)
                {
                    acquire(() ->
                    {
                        int lastSlot = mc.player.inventory.currentItem;
                        boolean silent = module.silent.getValue();
                        if (silent)
                        {
                            isMiddleClick = true;
                        }

                        InventoryUtil.switchTo(slot);

                        mc.playerController.processRightClick(
                                mc.player,
                                mc.world,
                                InventoryUtil.getHand(slot));

                        if (silent)
                        {
                            InventoryUtil.switchTo(lastSlot);
                            isMiddleClick = false;
                            lastSlot = -1;
                        }
                        else if (lastSlot != slot)
                        {
                            lastSlot = lastSlot;
                        }
                    });
                }
                else if (lastSlot != -1)
                {
                    acquire(() ->
                    {
                        InventoryUtil.switchTo(lastSlot);
                        lastSlot = -1;
                    });
                }
            }
            else if (lastSlot != -1)
            {
                acquire(() ->
                {
                    InventoryUtil.switchTo(lastSlot);
                    lastSlot = -1;
                });
            }
        }
    }




    private int findRocketSlot() {
        int rocketSlot = -1;

        if (mc.player.getHeldItemMainhand().getItem() == Items.FIREWORKS) {
            rocketSlot = mc.player.inventory.currentItem;
        }


        if (rocketSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.FIREWORKS) {
                    rocketSlot = l;
                    break;
                }
            }
        }

        return rocketSlot;
    }


    private int findEPSlot() {
        int epSlot = -1;

        if (mc.player.getHeldItemMainhand().getItem() == Items.ENDER_PEARL) {
            epSlot = mc.player.inventory.currentItem;
        }


        if (epSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.ENDER_PEARL) {
                    epSlot = l;
                    break;
                }
            }
        }

        return epSlot;
    }

    private int findXPSlot() {
        int epSlot = -1;

        if (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            epSlot = mc.player.inventory.currentItem;
        }


        if (epSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.EXPERIENCE_BOTTLE) {
                    epSlot = l;
                    break;
                }
            }
        }

        return epSlot;
    }


    private boolean sending = false;


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event)

    {
        if(!xp.getValue()) return;

        if(event.getPacket() instanceof CPacketPlayerTryUseItem) {

            CPacketPlayerTryUseItem p = event.getPacket();
            if (sending || event.isCanceled() || mc.player.getHeldItem(p.getHand()).getItem() != Items.EXPERIENCE_BOTTLE) {
                return;
            }

            if (simpleWasteStop.getValue() && isSimpleWasting() || wasteStop.getValue() && isWasting()) {
                event.setCanceled(true);
                justCancelled = true;
                return;
            }

            int packets = isMiddleClick ? mcePackets.getValue() : expPackets.getValue();
            if (packets != 0 && (packetsInLoot.getValue() || mc.world.getEntitiesWithinAABB(EntityItem.class, mc.player.getEntityBoundingBox()).isEmpty())) {
                for (int i = 0; i < packets; i++) {
                    sending = true; // This isn't really threadsafe...
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(p.getHand()));
                    sending = false;
                }
            }
        }
    }
    @SubscribeEvent
    public void sadasd(ClickMiddleEvent event)
    {
        if(!xp.getValue()) return;

        if (pickBlock.getValue())
        {
            mayCancel( event);
        }
    }

    static MiddleClick module;

    public static void mayCancel(EventStage event) {
        if (module.middleClickExp.getValue() && module.mceBind.getValue().getKey() == -1 && !(event instanceof ClickMiddleEvent && ((ClickMiddleEvent) event).isModuleCancelled() || event.isCanceled()) && !module.isWasting())
        {
            int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
            if (slot != -1
                    && slot != -2
                    && slot != mc.player.inventory.currentItem)
            {
                event.setCanceled(true);
            }
        }
    }




    public Setting<Boolean> feetExp = register(new Setting<Boolean>("FeetExp", false));
    public Setting<Integer> expPackets = register(new Setting<>("ExpPackets", 0, 0, 64));
    public Setting<Boolean> wasteStop = register(new Setting<Boolean>("WasteStop", false));
    public Setting<Boolean> simpleWasteStop = register(new Setting<Boolean>("SimpleWasteStop", false));
    public Setting<Boolean> wasteIfFull = register(new Setting<Boolean>("WasteIfInvFull", false));
    public Setting<Integer> stopDura = register(new Setting<>("Stop-Dura", 100, 0, 100));
    public Setting<Integer> wasteIf = register(new Setting<>("WasteIf", 30, 0, 100));
    public Setting<Boolean> wasteLoot = register(new Setting<Boolean>("WasteLoot", true));
    public Setting<Boolean> packetsInLoot = register(new Setting<Boolean>("PacketsInLoot", true));
    public Setting<Double> grow = register(new Setting<>("Grow", 0.0, 0.0, 5.0));
    public Setting<Boolean> middleClickExp = register(new Setting<Boolean>("MiddleClickExp", false));
    public Setting<Integer> mcePackets = register(new Setting<>("MCE-Packets", 0, 0, 64));
    public Setting<Boolean> silent = register(new Setting<Boolean>("Silent", true));
    public Setting<Boolean> whileEating = register(new Setting<Boolean>("WhileEating", true));
    public Setting<Boolean> xCarry = register(new Setting<Boolean>("XCarry", false));
    public Setting<Boolean> allowDragSlot = register(new Setting<Boolean>("AllowDragSlot", false));
    public Setting<SubBind> mceBind = register(new Setting<>("MCE-Bind", new SubBind(Keyboard.KEY_NONE)));
    public Setting<Boolean> pickBlock = register(new Setting<Boolean>("CancelPickBlock", true));
    public Setting<Boolean> normalMC = register(new Setting<Boolean>("CancelDefaultMiddleClick", true));

    protected boolean justCancelled;
    protected boolean isMiddleClick;
    protected int lastSlot = -1;






    @Override
    public void onEnable()
    {
        isMiddleClick = false;
        justCancelled = false;
        lastSlot = -1;
    }

    @Override
    public void onDisable()
    {
        if (lastSlot != -1)
        {
            acquire(() ->
                    InventoryUtil.switchTo(lastSlot));
            lastSlot = -1;
        }
    }

    public boolean isMiddleClick()
    {
        return middleClickExp.getValue()
                && (Mouse.isButtonDown(2) && mceBind.getValue().getKey() == -1
                || PlayerUtils.isKeyDown(mceBind.getValue().getKey()));
    }

    public boolean isWastingLoot(List<Entity> entities)
    {
        if (entities != null)
        {
            AxisAlignedBB bb = RotationUtil
                    .getRotationPlayer()
                    .getEntityBoundingBox()
                    .grow(grow.getValue(), grow.getValue(), grow.getValue());

            for (Entity entity : entities)
            {
                if (entity instanceof EntityItem
                        && !entity.isDead
                        && ((EntityItem) entity)
                        .getItem()
                        .getItem() == Items.EXPERIENCE_BOTTLE
                        && entity.getEntityBoundingBox()
                        .intersects(bb))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isSimpleWasting()
    {
        return getEnchantmentLevel(Enchantments.MENDING, mc.player.getHeldItem(EnumHand.OFF_HAND)) == 0
                && getEnchantmentLevel(Enchantments.MENDING, mc.player.getHeldItem(EnumHand.MAIN_HAND)) == 0
                && getEnchantmentLevel(Enchantments.MENDING, InventoryUtil.get(5)) == 0
                && getEnchantmentLevel(Enchantments.MENDING, InventoryUtil.get(6)) == 0
                && getEnchantmentLevel(Enchantments.MENDING, InventoryUtil.get(7)) == 0
                && getEnchantmentLevel(Enchantments.MENDING, InventoryUtil.get(8)) == 0;
    }

    public boolean isWasting()
    {
        if (isSimpleWasting())
        {
            return true;
        }

        int airSlot;
        if (wasteIfFull.getValue() && ((airSlot = InventoryUtil.findItem(Items.AIR, xCarry.getValue())) == -1
                || !allowDragSlot.getValue() && airSlot < 0))
        {
            return false;
        }

        if (wasteLoot.getValue())
        {
            List<Entity> entities = mc.world.loadedEntityList;
            if (isWastingLoot(entities))
            {
                return false;
            }
        }

        boolean empty = true;
        boolean full = false;
        for (int i = 5; i < 9; i++)
        {
            ItemStack stack = mc.player.inventoryContainer
                    .getSlot(i)
                    .getStack();
            if (!stack.isEmpty())
            {
                empty = false;
                float percent = DamageUtil.getPercent(stack);
                if (percent >= stopDura.getValue())
                {
                    full = true;
                }
                else if (percent <= wasteIf.getValue())
                {
                    return false;
                }
            }
        }

        return empty || full;
    }

    public boolean cancelShrink()
    {
        boolean just = justCancelled;
        justCancelled = false;
        return this.isEnabled()
                && this.wasteStop.getValue()
                && just;
    }

}

package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.modules.combat.NewAC;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.CrystalUtils;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;
import java.util.List;


public class FGTotem extends Module {

    public FGTotem() {
        super("OffHand", "Автототем", Category.COMBAT, true, false, false);
    }

    public Setting<Boolean> totem = this.register(new Setting<>("Totem", true));
    public Setting<Boolean> gapple = this.register(new Setting<>("Gapple", true));
    public Setting<Boolean> crystal = this.register(new Setting<>("Crystal", true));
    public Setting<Float> delay =this.register( new Setting<>("Delay", 0F, 0F, 5F));
    public Setting<Boolean> hotbarTotem = this.register(new Setting<>("HotbarTotem", false));
    public Setting<Float> totemHealthThreshold = this.register(new Setting<>("TotemHealth", 5f, 0f, 36f));
    public Setting<Boolean> rightClick = this.register(new Setting<>("RightClickGap", true));
    public Setting<CrystalCheck> crystalCheck = this.register(new Setting<>("CrystalCheck", CrystalCheck.DAMAGE));
    public Setting<Float> crystalRange = this.register(new Setting<>("CrystalRange", 10f, 1f, 15f));
    public Setting<Boolean> fallCheck = this.register(new Setting<>("FallCheck", true));
    public Setting<Float> fallDist = this.register(new Setting<>("FallDist", 15f, 0f, 50f));
    public Setting<Boolean> totemOnElytra = this.register(new Setting<>("TotemOnElytra", true));
    public Setting<Boolean> extraSafe = this.register(new Setting<>("ExtraSafe", false));
    public Setting<Boolean> clearAfter = this.register(new Setting<>("ClearAfter", true));
    public Setting<Boolean> hard = this.register(new Setting<>("Hard", false));
    public Setting<Boolean> notFromHotbar = this.register(new Setting<>("NotFromHotbar", true));
    public Setting<Default> defaultItem = this.register(new Setting<>("DefaultItem", Default.TOTEM));

    private final Queue<Integer> clickQueue = new LinkedList<>();

    private Timer timer = new Timer();

    private enum CrystalCheck {
        NONE,
        DAMAGE,
        RANGE
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketClickWindow) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    private enum Default {
        TOTEM(Items.TOTEM_OF_UNDYING),
        CRYSTAL(Items.END_CRYSTAL),
        GAPPLE(Items.GOLDEN_APPLE),
        AIR(Items.AIR);

        public Item item;

        Default(Item item) {
            this.item = item;
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (!(mc.currentScreen instanceof GuiContainer)) {
            if (!clickQueue.isEmpty()) {
                if (!timer.passedMs((long) (delay.getValue() * 100))) return;
                int slot = clickQueue.poll();
                try {
                  //  HotbarRefill.moveTimer.reset();
                    timer.reset();
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (!mc.player.inventory.getItemStack().isEmpty()) {
                    int index = 44;
                    while (index >= 9) {
                        if (mc.player.inventoryContainer.getSlot(index).getStack().isEmpty()) {
                            mc.playerController.windowClick(0, index, 0, ClickType.PICKUP, mc.player);
                            return;
                        }
                        index--;
                    }
                }

                if (totem.getValue()) {
                    if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= totemHealthThreshold.getValue() || (totemOnElytra.getValue() && mc.player.isElytraFlying()) || (fallCheck.getValue() && mc.player.fallDistance >= fallDist.getValue() && !mc.player.isElytraFlying())) {
                        putItemIntoOffhand(Items.TOTEM_OF_UNDYING);
                        return;
                    } else if (crystalCheck.getValue() == CrystalCheck.RANGE) {
                        EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.loadedEntityList.stream()
                                .filter(e -> (e instanceof EntityEnderCrystal && mc.player.getDistance(e) <= crystalRange.getValue()))
                                .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                                .orElse(null);

                        if (crystal != null) {
                            putItemIntoOffhand(Items.TOTEM_OF_UNDYING);
                            return;
                        }
                    } else if (crystalCheck.getValue() == CrystalCheck.DAMAGE) {
                        float damage = 0.0f;

                        List<Entity> crystalsInRange = mc.world.loadedEntityList.stream()
                                .filter(e -> e instanceof EntityEnderCrystal)
                                .filter(e -> mc.player.getDistance(e) <= crystalRange.getValue())
                                .collect(Collectors.toList());

                        for (Entity entity : crystalsInRange) {
                            damage += CrystalUtils.calculateDamage((EntityEnderCrystal) entity, mc.player);
                        }

                        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() - damage <= totemHealthThreshold.getValue()) {
                            putItemIntoOffhand(Items.TOTEM_OF_UNDYING);
                            return;
                        }
                    }

                    if (extraSafe.getValue()) {
                        if (crystalCheck()) {
                            putItemIntoOffhand(Items.TOTEM_OF_UNDYING);
                            return;
                        }
                    }
                }

                if (gapple.getValue() && isSword(mc.player.getHeldItemMainhand().getItem())) {
                    if (rightClick.getValue() && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
                        if (clearAfter.getValue()) {
                            putItemIntoOffhand(defaultItem.getValue().item);
                        }
                        return;
                    }
                    putItemIntoOffhand(Items.GOLDEN_APPLE);
                    return;
                }
                if (crystal.getValue()) {
                    if (Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).isEnabled() || Thunderhack.moduleManager.getModuleByClass(NewAC.class).isEnabled()) {
                        putItemIntoOffhand(Items.END_CRYSTAL);
                        return;
                    } else if (clearAfter.getValue()) {
                        putItemIntoOffhand(defaultItem.getValue().item);
                        return;
                    }
                }
                if (hard.getValue()) {
                    putItemIntoOffhand(defaultItem.getValue().item);
                }
            }
        }
    }

    private boolean isSword(Item item) {
        return item == Items.DIAMOND_SWORD || item == Items.IRON_SWORD || item == Items.GOLDEN_SWORD || item == Items.STONE_SWORD || item == Items.WOODEN_SWORD;
    }

    private int findItemSlot(Item item) {
        int itemSlot = -1;
        for (int i = notFromHotbar.getValue() ? 9 : 0; i < 36; i++) {

            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() == item) {
                itemSlot = i;
                break;
            }

        }
        return itemSlot;
    }

    private void putItemIntoOffhand(Item item) {
        if (mc.player.getHeldItemOffhand().getItem() == item) return;
        int slot = findItemSlot(item);
        if (hotbarTotem.getValue() && item == Items.TOTEM_OF_UNDYING) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.inventory.mainInventory.get(i);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    if (mc.player.inventory.currentItem != i) {
                        mc.player.inventory.currentItem = i;
                    }
                    return;
                }
            }
        }
        if (slot != -1) {
            if (delay.getValue() > 0F) {
                if (timer.passedMs((long) (delay.getValue() * 100))) {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
                    timer.reset();
                } else {
                    clickQueue.add(slot < 9 ? slot + 36 : slot);
                }

                clickQueue.add(45);
                clickQueue.add(slot < 9 ? slot + 36 : slot);
            } else {
                timer.reset();
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
                try {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    private boolean crystalCheck() {
        float cumDmg = 0;
        ArrayList<Float> damageValues = new ArrayList<>();
        damageValues.add(calculateDamageAABB(mc.player.getPosition().add(1, 0, 0)));
        damageValues.add(calculateDamageAABB(mc.player.getPosition().add(-1, 0, 0)));
        damageValues.add(calculateDamageAABB(mc.player.getPosition().add(0, 0, 1)));
        damageValues.add(calculateDamageAABB(mc.player.getPosition().add(0, 0, -1)));
        damageValues.add(calculateDamageAABB(mc.player.getPosition()));
        for (float damage : damageValues) {
            cumDmg += damage;
            if ((((mc.player.getHealth() + mc.player.getAbsorptionAmount())) - damage) <= totemHealthThreshold.getValue()) {
                return true;
            }
        }

        return (((mc.player.getHealth() + mc.player.getAbsorptionAmount())) - cumDmg) <= totemHealthThreshold.getValue();
    }

    private float calculateDamageAABB(BlockPos pos){
        List<Entity> crystalsInAABB =  mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream()
                .filter(e -> e instanceof EntityEnderCrystal)
                .collect(Collectors.toList());
        float totalDamage = 0;
        for (Entity crystal : crystalsInAABB) {
            totalDamage += CrystalUtils.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player);
        }
        return totalDamage;
    }
}

package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.gui.hud.RadarRewrite;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.movement.ElytraFlight;
import com.mrzak34.thunderhack.modules.player.FastPlace2;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InvStack;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


import java.util.List;
import java.util.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AutoArmor
        extends Module {

    public AutoArmor() {
    super("AutoArmor", "Автоброня", Category.PLAYER, true, false, false);
    }


    private Setting<Mode> mode = register(new Setting<>("Mode", Mode.FunnyGame));


    public enum Mode {
        FunnyGame,
        Default
    }

    private Setting<Boolean> armorSaver = register(new Setting<>("ArmorSaver", false));
    private Setting<Integer> delay = register(new Setting<>("Delay", 1, 1, 10));
    public Setting<Float> depletion = register(new Setting("Depletion", 0.75F, 0.5F, 0.95F, v-> armorSaver.getValue()));
    private Setting<Boolean> elytraPrio = register(new Setting<>("ElytraPrio", false));
    private Setting<Boolean> smart = register(new Setting<>("Smart", false, v-> elytraPrio.getValue()));
    private Setting<Boolean> strict = register(new Setting<>("Strict", false));
    private Setting<Boolean> pauseWhenSafe = register(new Setting<>("PauseWhenSafe", false));
    private Setting<Boolean> allowMend = register(new Setting<>("AllowMend", false));



    private Timer rightClickTimer = new Timer();

    private boolean sleep;

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketClickWindow) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    @Override
    public void onUpdate(){

        if(mode.getValue() == Mode.Default) {
            //  if (event.getPhase() == TickEvent.Phase.END) return;
            if (mc.world == null || mc.player == null) return;
            if (mc.player.ticksExisted % delay.getValue() != 0) {
                return;
            }

            if (strict.getValue()) {
                if (mc.player.motionX != 0D || mc.player.motionZ != 0D) return;
            }

            if (pauseWhenSafe.getValue()) {
                List<Entity> proximity = mc.world.loadedEntityList.stream().filter(e -> (e instanceof EntityPlayer && !(e.equals(mc.player)) && mc.player.getDistance(e) <= 6) || (e instanceof EntityEnderCrystal && mc.player.getDistance(e) <= 12)).collect(Collectors.toList());
                if (proximity.isEmpty()) return;
            }

            if (FastPlace2.isMending) return;


            if (allowMend.getValue()) {
                if (!rightClickTimer.passedMs(500)) {
                    for (int i = 0; i < mc.player.inventory.armorInventory.size(); i++) {
                        ItemStack armorPiece = mc.player.inventory.armorInventory.get(i);
                        if (armorPiece.getEnchantmentTagList() != null) {
                            boolean mending = false;
                            for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(armorPiece).entrySet()) {
                                if (entry.getKey().getName().contains("mending")) {
                                    mending = true;
                                    break;
                                }
                            }
                            if (!mending) continue;
                        }
                        if (armorPiece.isEmpty()) continue;
                        long freeSlots = mc.player.inventory.mainInventory
                                .stream()
                                .filter(is -> is.isEmpty() || is.getItem() == Items.AIR)
                                .map(is -> mc.player.inventory.getSlotFor(is))
                                .count();
                        if (freeSlots <= 0) return;
                        if (armorPiece.getItemDamage() != 0) {
                            shiftClickSpot(8 - i);
                            return;
                        }
                    }
                    return;
                }
            }

            if (mc.currentScreen instanceof GuiContainer) return;

            AtomicBoolean hasSwapped = new AtomicBoolean(false);

            if (sleep) {
                sleep = false;
                return;
            }

            boolean ep = elytraPrio.getValue();
            if (smart.getValue() && !Thunderhack.moduleManager.getModuleByClass(ElytraFlight.class).isOn()) {
                ep = false;
            }

            final Set<InvStack> replacements = new HashSet<>();

            for (int slot = 0; slot < 36; slot++) {

                InvStack invStack = new InvStack(slot, mc.player.inventory.getStackInSlot(slot));
                if (invStack.stack.getItem() instanceof ItemArmor || invStack.stack.getItem() instanceof ItemElytra) {
                    replacements.add(invStack);
                }

            }

            List<InvStack> armors = replacements.stream()
                    .filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                    .filter(invStack -> !armorSaver.getValue() || invStack.stack.getItem().getDurabilityForDisplay(invStack.stack) < depletion.getValue())
                    .sorted(Comparator.comparingInt(invStack -> invStack.slot))
                    .sorted(Comparator.comparingInt(invStack -> ((ItemArmor) invStack.stack.getItem()).damageReduceAmount))
                    .collect(Collectors.toList());

            boolean wasEmpty = armors.isEmpty();

            if (wasEmpty) {
                armors = replacements.stream()
                        .filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                        .sorted(Comparator.comparingInt(invStack -> invStack.slot))
                        .sorted(Comparator.comparingInt(invStack -> ((ItemArmor) invStack.stack.getItem()).damageReduceAmount))
                        .collect(Collectors.toList());
            }

            List<InvStack> elytras = replacements.stream()
                    .filter(invStack -> invStack.stack.getItem() instanceof ItemElytra)
                    .sorted(Comparator.comparingInt(invStack -> invStack.slot))
                    .collect(Collectors.toList());


            Item currentHeadItem = mc.player.inventory.getStackInSlot(39).getItem();
            Item currentChestItem = mc.player.inventory.getStackInSlot(38).getItem();
            Item currentLegsItem = mc.player.inventory.getStackInSlot(37).getItem();
            Item currentFeetItem = mc.player.inventory.getStackInSlot(36).getItem();

            boolean replaceHead = currentHeadItem.equals(Items.AIR) || (!wasEmpty && armorSaver.getValue() && mc.player.inventory.getStackInSlot(39).getItem().getDurabilityForDisplay(mc.player.inventory.getStackInSlot(39)) >= depletion.getValue());
            boolean replaceChest = currentChestItem.equals(Items.AIR) || (!wasEmpty && armorSaver.getValue() && mc.player.inventory.getStackInSlot(38).getItem().getDurabilityForDisplay(mc.player.inventory.getStackInSlot(38)) >= depletion.getValue());
            boolean replaceLegs = currentLegsItem.equals(Items.AIR) || (!wasEmpty && armorSaver.getValue() && mc.player.inventory.getStackInSlot(37).getItem().getDurabilityForDisplay(mc.player.inventory.getStackInSlot(37)) >= depletion.getValue());
            boolean replaceFeet = currentFeetItem.equals(Items.AIR) || (!wasEmpty && armorSaver.getValue() && mc.player.inventory.getStackInSlot(36).getItem().getDurabilityForDisplay(mc.player.inventory.getStackInSlot(36)) >= depletion.getValue());


            if (replaceHead && !hasSwapped.get()) {
                armors.stream().filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                        .filter(invStack -> ((ItemArmor) invStack.stack.getItem()).armorType.equals(EntityEquipmentSlot.HEAD)
                        ).findFirst().ifPresent(invStack -> {
                            swapSlot(invStack.slot, 5);
                            hasSwapped.set(true);
                        });
            }

            if (ep && !(currentChestItem instanceof ItemElytra) && elytras.size() > 0 && !hasSwapped.get()) {
                elytras.stream().findFirst().ifPresent(invStack -> {
                    swapSlot(invStack.slot, 6);
                    hasSwapped.set(true);
                });
            }

            if (replaceChest || (!ep && currentChestItem.equals(Items.ELYTRA)) && !hasSwapped.get()) {
                armors.stream().filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                        .filter(invStack -> ((ItemArmor) invStack.stack.getItem()).armorType.equals(EntityEquipmentSlot.CHEST)
                        ).findFirst().ifPresent(invStack -> {
                            swapSlot(invStack.slot, 6);
                            hasSwapped.set(true);
                        });
            }

            if (replaceLegs && !hasSwapped.get()) {
                armors.stream().filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                        .filter(invStack -> ((ItemArmor) invStack.stack.getItem()).armorType.equals(EntityEquipmentSlot.LEGS)
                        ).findFirst().ifPresent(invStack -> {
                            swapSlot(invStack.slot, 7);
                            hasSwapped.set(true);
                        });
            }

            if (replaceFeet && !hasSwapped.get()) {
                armors.stream().filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                        .filter(invStack -> ((ItemArmor) invStack.stack.getItem()).armorType.equals(EntityEquipmentSlot.FEET)
                        ).findFirst().ifPresent(invStack -> {
                            swapSlot(invStack.slot, 8);
                            hasSwapped.set(true);
                        });
            }
        } else {
            if (this.mc.player != null && this.mc.world != null) {
                int armorType;
                if (this.mc.player.ticksExisted % 2 == 0) {
                    return;
                }
                if (this.mc.currentScreen instanceof GuiContainer && !(this.mc.currentScreen instanceof InventoryEffectRenderer)) {
                    return;
                }
                int[] bestArmorSlots = new int[4];
                int[] bestArmorValues = new int[4];
                for (armorType = 0; armorType < 4; ++armorType) {
                    ItemStack oldArmor = this.mc.player.inventory.armorItemInSlot(armorType);
                    if (oldArmor.getItem() instanceof ItemArmor) {
                        bestArmorValues[armorType] = ((ItemArmor)oldArmor.getItem()).damageReduceAmount;
                    }
                    bestArmorSlots[armorType] = -1;
                }
                for (int slot = 0; slot < 36; ++slot) {
                    int armorValue;
                    ItemStack stack = this.mc.player.inventory.getStackInSlot(slot);
                    if (stack.getCount() > 1 || !(stack.getItem() instanceof ItemArmor)) continue;
                    ItemArmor armor = (ItemArmor)stack.getItem();
                    int armorType2 = armor.armorType.ordinal() - 2;
                    if (armorType2 == 2 && this.mc.player.inventory.armorItemInSlot(armorType2).getItem().equals(Items.ELYTRA) || (armorValue = armor.damageReduceAmount) <= bestArmorValues[armorType2]) continue;
                    bestArmorSlots[armorType2] = slot;
                    bestArmorValues[armorType2] = armorValue;
                }
                for (armorType = 0; armorType < 4; ++armorType) {
                    ItemStack oldArmor;
                    int slot = bestArmorSlots[armorType];
                    if (slot == -1 || (oldArmor = this.mc.player.inventory.armorItemInSlot(armorType)) == ItemStack.EMPTY && this.mc.player.inventory.getFirstEmptyStack() == -1) continue;
                    if (slot < 9) {
                        slot += 36;
                    }
                    this.mc.playerController.windowClick(0, 8 - armorType, 0, ClickType.QUICK_MOVE, (EntityPlayer)this.mc.player);
                    this.mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, (EntityPlayer)this.mc.player);
                    break;
                }
            }
        }

    }

    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if(event.getEntityPlayer() != mc.player) return;
        if(event.getItemStack().getItem() != Items.EXPERIENCE_BOTTLE) return;
        rightClickTimer.reset();
    }

    private void swapSlot(int source, int target) {

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, source < 9 ? source + 36 : source, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, target, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, source < 9 ? source + 36 : source, 0, ClickType.PICKUP, mc.player);

        sleep = true;

    }

    private void shiftClickSpot(int source) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, source, 0, ClickType.QUICK_MOVE, mc.player);
    }


}


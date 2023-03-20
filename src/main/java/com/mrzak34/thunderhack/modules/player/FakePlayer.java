package com.mrzak34.thunderhack.modules.player;

import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.events.EventPostSync;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.TotemPopEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PositionforFP;
import com.mrzak34.thunderhack.util.math.DamageUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakePlayer extends Module {
    protected final List<PositionforFP> positions = new ArrayList<>();
    final private ItemStack[] armors = new ItemStack[]{
            new ItemStack(Items.DIAMOND_BOOTS),
            new ItemStack(Items.DIAMOND_LEGGINGS),
            new ItemStack(Items.DIAMOND_CHESTPLATE),
            new ItemStack(Items.DIAMOND_HELMET)
    };
    public Setting<Integer> vulnerabilityTick = this.register(new Setting<>("Vulnerability Tick", 4, 0, 10));
    public Setting<Integer> resetHealth = this.register(new Setting<>("Reset Health", 10, 0, 36));
    public Setting<Integer> tickRegenVal = this.register(new Setting<>("Tick Regen", 4, 0, 30));
    public Setting<Integer> startHealth = this.register(new Setting<>("Start Health", 20, 0, 30));
    public Setting<String> nameFakePlayer = this.register(new Setting<>("Name FakePlayer", "Ebatte_Sratte"));
    int incr;
    EntityOtherPlayerMP clonedPlayer = null;
    boolean beforePressed;
    // Simple list of players for the pop
    ArrayList<playerInfo> listPlayers = new ArrayList<>();
    int index = 0;
    private final Setting<Boolean> copyInventory = this.register(new Setting<>("Copy Inventory", false));
    private final Setting<Boolean> playerStacked = this.register(new Setting<>("Player Stacked", true, v -> !copyInventory.getValue()));
    private final Setting<Boolean> onShift = this.register(new Setting<>("On Shift", false));
    private final Setting<Boolean> simulateDamage = this.register(new Setting<>("Simulate Damage", false));
    private final Setting<Boolean> resistance = this.register(new Setting<>("Resistance", true));
    private final Setting<Boolean> pop = this.register(new Setting<>("Pop", true));
    private final Setting<Boolean> record2 = this.register(new Setting<>("Record", true));
    private final Setting<Boolean> play = this.register(new Setting<>("Play", true));
    private int ticks;

    public FakePlayer() {
        super("FakePlayer", "фейкплеер для тестов", Module.Category.PLAYER);
    }

    @Override
    public void onLogout() {
        if (this.isOn()) {
            this.disable();
        }
    }

    @Override
    public void onEnable() {
        incr = 0;
        beforePressed = false;
        if (mc.player == null || mc.player.isDead) {
            disable();
            return;
        }
        if (!onShift.getValue())
            spawnPlayer();
    }

    void spawnPlayer() {
        // Clone empty player
        clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), nameFakePlayer.getValue()));
        // Copy angles
        clonedPlayer.copyLocationAndAnglesFrom(mc.player);
        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        // set gameType
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth(startHealth.getValue());
        // Add entity id
        mc.world.addEntityToWorld((-1234 + incr), clonedPlayer);
        incr++;
        // Set invenotry
        if (copyInventory.getValue())
            clonedPlayer.inventory.copyInventory(mc.player.inventory);
        else
            // If enchants
            if (playerStacked.getValue()) {
                // Iterate
                for (int i = 0; i < 4; i++) {
                    // Create base
                    ItemStack item = armors[i];
                    // Add enchants
                    item.addEnchantment(
                            i == 3 ? Enchantments.BLAST_PROTECTION : Enchantments.PROTECTION,
                            4);
                    // Add it to the player
                    clonedPlayer.inventory.armorInventory.set(i, item);

                }
            }
        if (resistance.getValue())
            clonedPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(11), 123456789, 0));
        clonedPlayer.onEntityUpdate();
        listPlayers.add(new playerInfo(clonedPlayer.getName()));
    }

    @Override
    public void onUpdate() {
        // OnShift add
        if (onShift.getValue() && mc.gameSettings.keyBindSneak.isPressed() && !beforePressed) {
            beforePressed = true;
            spawnPlayer();
        } else beforePressed = false;

        // Update tick explosion
        for (int i = 0; i < listPlayers.size(); i++) {
            if (listPlayers.get(i).update()) {
                int finalI = i;
                Optional<EntityPlayer> temp = mc.world.playerEntities.stream().filter(
                        e -> e.getName().equals(listPlayers.get(finalI).name)
                ).findAny();
                if (temp.isPresent())
                    if (temp.get().getHealth() < 20)
                        temp.get().setHealth(temp.get().getHealth() + 1);
            }
        }
    }

    public void onDisable() {
        if (mc.world != null) {
            for (int i = 0; i < incr; i++) {
                mc.world.removeEntityFromWorld((-1234 + i));
            }
        }
        listPlayers.clear();
        positions.clear();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (simulateDamage.getValue()) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packetSoundEffect = event.getPacket();
                if (packetSoundEffect.getCategory() == SoundCategory.BLOCKS && packetSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                        if (entity instanceof EntityEnderCrystal) {
                            if (entity.getDistanceSq(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= 36.0f) {
                                for (EntityPlayer entityPlayer : mc.world.playerEntities) {
                                    if (entityPlayer.getName().equals(nameFakePlayer.getValue())) {

                                        Optional<playerInfo> temp = listPlayers.stream().filter(
                                                e -> e.name.equals(entityPlayer.getName())
                                        ).findAny();

                                        if (!temp.isPresent() || !temp.get().canPop())
                                            continue;

                                        float damage = DamageUtil.calculateDamage(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ(), entityPlayer, false);
                                        if (damage > entityPlayer.getHealth()) {
                                            entityPlayer.setHealth(resetHealth.getValue());
                                            if (pop.getValue()) {
                                                mc.effectRenderer.emitParticleAtEntity(entityPlayer, EnumParticleTypes.TOTEM, 30);
                                                mc.world.playSound(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
                                            }
                                            MinecraftForge.EVENT_BUS.post(new TotemPopEvent(entityPlayer));
                                        } else entityPlayer.setHealth(entityPlayer.getHealth() - damage);

                                        temp.get().tickPop = 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMotionUpdateEvent(EventSync event) {
        if (!record2.getValue()) {
            if (play.getValue()) {
                if (positions.isEmpty()) {
                    return;
                }

                if (index >= positions.size()) {
                    index = 0;
                }

                if (ticks++ % 2 == 0) {
                    PositionforFP p = positions.get(index++);
                    clonedPlayer.rotationYaw = p.getYaw();
                    clonedPlayer.rotationPitch = p.getPitch();
                    clonedPlayer.rotationYawHead = p.getHead();
                    clonedPlayer.setPositionAndRotationDirect(
                            p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch(),
                            3, false);
                }
            } else {
                index = 0;
            }
        }

    }

    @SubscribeEvent
    public void onMotionUpdateEventPost(EventPostSync event) {
        if (record2.getValue()) {
            if (ticks++ % 2 == 0) {
                positions.add(new PositionforFP(mc.player));
            }
        }
    }

    public enum movingmode {
        None, Line, Circle, Random
    }

    class playerInfo {
        final String name;
        int tickPop = -1;
        int tickRegen = 0;

        // We just set the new name
        public playerInfo(String name) {
            this.name = name;
        }

        // If update, we have to regen and decrease vulnerability tick
        boolean update() {
            if (tickPop != -1) {
                if (++tickPop >= vulnerabilityTick.getValue())
                    tickPop = -1;
            }
            if (++tickRegen >= tickRegenVal.getValue()) {
                tickRegen = 0;
                return true;
            } else return false;
        }

        boolean canPop() {
            return this.tickPop == -1;
        }
    }


}
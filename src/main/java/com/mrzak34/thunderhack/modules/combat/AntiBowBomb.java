package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.mixin.mixins.AccessorKeyBinding;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.util.EnumHand;

public class AntiBowBomb extends Module {

    public AntiBowBomb() {
        super("AntiBowBomb", "Ставит щит если-в тебя целится-игрок", Category.COMBAT, true, false, false);
    }

    public final Setting<Boolean> stopa = this.register(new Setting<Boolean>("StopAura", true));
    public final Setting<Boolean> el = this.register(new Setting<Boolean>("EnemyList", false));
    EntityPlayer target;
    public Setting<Integer> range = this.register(new Setting<Object>("Range", 40, 0, 60));
    public Setting<Integer> maxUse = this.register(new Setting<Object>("MaxUse", 0, 0, 20));



    int old;
    boolean b;

    @Override
    public void onToggle() {
        b = false;
        old = -1;
        target = null;
    }


    public EntityPlayer getTarget(final float range) {
        EntityPlayer currentTarget = null;
        for (int size = mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if (!isntValid(player, range)) {
                if (currentTarget == null) {
                    currentTarget = player;
                } else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) {
                    currentTarget = player;
                }
            }
        }
        return currentTarget;
    }

    @Override
    public void onTick() {
        target = getTarget(range.getValue());

        if (target == null) {
            if (b) {
                ((AccessorKeyBinding) mc.gameSettings.keyBindUseItem).setPressed(false);
                if (old != -1) ItemUtil.swapToHotbarSlot(old, false);
                target = null;
                b = false;
            }
        } else {
            old = mc.player.inventory.currentItem;
            int shield = ItemUtil.findItem(ItemShield.class);
            if (shield == -1){ target = null; return; }
            if (Thunderhack.friendManager.isFriend(target.getName())) return;
            if (target.getItemInUseMaxCount() <= maxUse.getValue()) return;

            if(!(target.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow && !(target.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemBow))){return;}

            if (stopa.getValue()){
                if(Thunderhack.moduleManager.getModuleByClass(TargetStrafe.class).isEnabled()) {
                    Thunderhack.moduleManager.getModuleByClass(TargetStrafe.class).toggle();
                }
            }
            if (stopa.getValue()){
                if(Thunderhack.moduleManager.getModuleByClass(DeadCodeAura.class).isEnabled()) {
                    Thunderhack.moduleManager.getModuleByClass(DeadCodeAura.class).toggle();
                }
            }
            InventoryUtil.switchToHotbarSlot(shield,false);

            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemShield) {
                ((AccessorKeyBinding) mc.gameSettings.keyBindUseItem).setPressed(true);
                ItemUtil.swapToHotbarSlot(shield, false);


                SilentRotaionUtil.lookAtEntity(target);


                b = true;
            }
        }
    }

    public boolean isntValid(Entity entity, double range) {
        return entity == null || EntityUtil.isDead(entity) || entity.equals(mc.player) || entity instanceof EntityPlayer && Thunderhack.friendManager.isFriend(entity.getName()) || mc.player.getDistanceSq(entity) > MathUtil.square(range) || (el.getValue() && !Thunderhack.enemyManager.isEnemy(entity.getName()));
    }
}

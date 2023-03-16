package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.mixin.mixins.IMinecraft;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class FastUse extends Module {
    public FastUse() {
        super("FastUse", "убирает задержку-испольования пкм", "FastUse", Category.PLAYER);
    }

    private final Setting<Integer> delay = this.register(new Setting<>("Delay", 1, 0, 4));
    public Setting<Boolean> blocks = this.register(new Setting<>("Blocks", false));
    public Setting<Boolean> crystals = this.register(new Setting<>("Crystals", false));
    public Setting<Boolean> xp = this.register(new Setting<>("XP", false));
    public Setting<Boolean> all = this.register(new Setting<>("All", true));

    @Override
    public void onUpdate() {
        if(check(mc.player.getHeldItemMainhand().getItem())){
            if (((IMinecraft)mc).getRightClickDelayTimer() > delay.getValue())
                ((IMinecraft)mc).setRightClickDelayTimer(delay.getValue());
        }
    }

    public boolean check(Item item){
        return (item instanceof ItemBlock && blocks.getValue())
                || (item == Items.END_CRYSTAL && crystals.getValue())
                || (item == Items.EXPERIENCE_BOTTLE && xp.getValue())
                || (all.getValue());
    }
}

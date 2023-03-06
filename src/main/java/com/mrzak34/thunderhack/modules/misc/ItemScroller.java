package com.mrzak34.thunderhack.modules.misc;


import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class ItemScroller extends Module {
    public ItemScroller() {
        super("ItemScroller", "Позволяет быстро-перекладывать-предметы", Category.MISC);
    }
    public Setting<Integer> delay = this.register ( new Setting <> ( "Delay", 100, 0, 1000 ) );
}

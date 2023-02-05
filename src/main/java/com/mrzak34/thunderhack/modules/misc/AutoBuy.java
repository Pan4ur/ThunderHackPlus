package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AutoBuy extends Module {

    public AutoBuy() {
        super("AutoBuy", "авто залупка", Category.MISC);
    }


    public static List<AutoBuyItem> items = new ArrayList<>();

    private com.mrzak34.thunderhack.util.Timer timer = new com.mrzak34.thunderhack.util.Timer();
    private com.mrzak34.thunderhack.util.Timer timer2 = new com.mrzak34.thunderhack.util.Timer();
    private com.mrzak34.thunderhack.util.Timer roamDelay = new com.mrzak34.thunderhack.util.Timer();

    private int windowId;
    boolean clickGreenPannel = false;
    boolean clicked= false;
    boolean direction= false;

    int pages;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()){
            return;
        }
        if(e.getPacket() instanceof SPacketWindowItems){
            SPacketWindowItems pac = e.getPacket();
            windowId = pac.getWindowId();
            int slot = 0;
            if(clickGreenPannel) return;
            for(ItemStack itemStack : pac.getItemStacks()){
                for(AutoBuyItem abitem : items){
                  // if(Objects.equals(abitem.getName(), itemStack.getItem().getUnlocalizedNameInefficiently(itemStack).replace("item.", ""))){ // хуета
                    if(Objects.equals(abitem.getName(), (itemStack.getItem().getRegistryName()+"").replace("minecraft:", ""))){
                        if(getPrice(getLoreTagList(itemStack).toString()) <= abitem.price1){
                            if(abitem.EnchantsIsEmpty()){
                                roamDelay.reset();
                                Buy(slot);
                            } else {
                                String[] ench = new String[20];
                                int i = 0;
                                for (NBTBase tag : itemStack.getEnchantmentTagList()) {
                                    ench[i] = rewriteEnchant(tag.toString());
                                    i++;
                                }
                                if (isContain( abitem.enchantments,ench)) {
                                    roamDelay.reset();
                                    Buy(slot);
                                }
                            }
                        }
                    }
                }
                slot++;
            }
        }
        if(e.getPacket() instanceof SPacketChat){
            SPacketChat packetChat = e.getPacket();
            if(packetChat.getChatComponent().getUnformattedText().contains("Успешная покупка")){
                clicked = false;
                clickGreenPannel = false;
                direction = false;
                pages = 0;
                timer.reset();
            }
        }
    }




    // <-48 50->



    @Override
    public void onUpdate(){
        if(timer2.passedMs(100)){
            if(mc.currentScreen instanceof GuiChest && roamDelay.passedMs(2000)) {
                if (pages < 15 && !direction) {
                    mc.playerController.windowClick(windowId, 50, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.updateController();
                    pages++;
                } else if( pages == 15 && !direction){
                    direction = true;
                } else if (pages == 0) {
                    direction = false;
                } else if (direction){
                    mc.playerController.windowClick(windowId, 48, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.updateController();
                    pages--;
                }
            }
            timer2.reset();
        }
        if(clickGreenPannel && !clicked){
            Buy(0);
        } else if(clicked){
            clickGreenPannel = false;
        }
    }


    public void Buy(int slot){
        if(timer.passedMs(600)) {
            mc.playerController.windowClick(windowId, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.updateController();

            if(slot == 0 && clickGreenPannel){
                clicked = true;
            } else {
                clickGreenPannel = true;
            }
            timer.reset();


        }
    }

    public boolean isContain(String[] m1, String[] m2){
        int count = 0;
        for (String a : m1) for (String b : m2) if (Objects.equals(a, b)) {
            count++;
            break;
        }
        return count == m1.length;
    }


    public String rewriteEnchant(String string){
        String id = StringUtils.substringBetween(string, "id:", "s");
        String lvl = StringUtils.substringBetween(string, "lvl:", "s,");
        return id + "("+lvl+")";
    }

    public int getPrice(String string){
        if(string == null){
            return 9999999;
        }
      //  if(string3 == null){
       //     return 9999999;
      //  }
        String string2 = StringUtils.substringBetween(string, "за все: ", "$");
      //  String string3 = StringUtils.substringBetween(string, "за 1шт.: ", "$");
        if(string2 == null){
            return 9999999;
        }
        string2 = string2.replace(",","");
        String[]  string3 = string2.split("l");


        return Integer.parseInt(string3[1]);
    }

    public static NBTTagList getLoreTagList( ItemStack stack )
    {

        NBTTagCompound displayTag = getDisplayTag( stack );

        if ( !hasLore( stack ) )
        {
            displayTag.setTag( "Lore", new NBTTagList() );
        }

        return displayTag.getTagList( "Lore", Constants.NBT.TAG_STRING );
    }
    public static boolean hasLore( ItemStack stack )
    {
        return hasDisplayTag( stack ) && getDisplayTag( stack ).hasKey( "Lore", Constants.NBT.TAG_LIST );
    }
    public static boolean hasDisplayTag( ItemStack stack )
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey( "display", Constants.NBT.TAG_COMPOUND );
    }

    public static NBTTagCompound getDisplayTag( ItemStack stack )
    {
        return stack.getOrCreateSubCompound( "display" );
    }


    public static class AutoBuyItem {

        public String getName() {
            return name;
        }

        public String[] getEnchantments() {
            return enchantments;
        }

        public int getPrice1() {
            return price1;
        }

        public int getPrice2() {
            return price2;
        }

        String name;
        String[] enchantments;
        int price1;
        int price2;
        boolean noench = true;

        public AutoBuyItem(String name, String[] enchantments, int price1, int price2,boolean noEnch) {
            this.name = name;
            this.enchantments = enchantments;
            this.price1 = price1;
            this.price2 = price2;
            this.noench = noEnch;
        }

        public boolean EnchantsIsEmpty() {
            return noench;
        }
    }
}

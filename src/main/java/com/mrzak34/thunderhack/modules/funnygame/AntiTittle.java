package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.misc.ChatTweaks;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class AntiTittle extends Module {

    public AntiTittle() {
        super("Adblock", "Адблок для ебучего-фанигейма", Category.FUNNYGAME, true, false, false);
    }


    public Setting<Boolean> tittle  = this.register(new Setting<Boolean>("AntiTitle", true));
    public Setting<Boolean> armorstands  = this.register(new Setting<Boolean>("AntiSpawnLag", true));
    public Setting<Boolean> scoreBoard  = this.register(new Setting<Boolean>("ScoreBoard", true));
    public Setting<Boolean> chat  = this.register(new Setting<Boolean>("ChatAds", true));
    public Setting<Integer> waterMarkZ1 = register(new Setting("Y", 10, 0, 524));
    public Setting<Integer> waterMarkZ2 = register(new Setting("X", 20, 0, 862));
    public Setting<Boolean> counter  = this.register(new Setting<Boolean>("Counter", false));

    int count = 0;
    int y1 =0;
    int x1 = 0;
    ScaledResolution sr = new ScaledResolution(mc);

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        if(counter.getValue()) {
            y1 = (int) (sr.getScaledHeight() / (1000f / waterMarkZ1.getValue()));
            x1 = (int) (sr.getScaledWidth() / (1000f / waterMarkZ2.getValue()));

            RenderUtil.drawSmoothRect(waterMarkZ2.getValue(), waterMarkZ1.getValue(), 75 + waterMarkZ2.getValue(), 11 + waterMarkZ1.getValue(), new Color(35, 35, 40, 230).getRGB());
            Util.fr.drawStringWithShadow("Ads Blocked : ", waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 1, PaletteHelper.astolfo(false, (int) 1).getRGB());
            Util.fr.drawStringWithShadow(String.valueOf(count), waterMarkZ2.getValue() + 6 + Util.fr.getStringWidth("Ads Blocked : "), waterMarkZ1.getValue() + 1, -1);
        }
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(tittle.getValue()) {
            if (e.getPacket() instanceof SPacketTitle) {
                ++count;
                e.setCanceled(true);
            }
        }
        if(chat.getValue() && !Thunderhack.moduleManager.getModuleByClass(ChatTweaks.class).isOn()) {
            if (e.getPacket() instanceof SPacketChat) {
                final SPacketChat packet = (SPacketChat) e.getPacket();
                if (packet.getType() != ChatType.GAME_INFO && this.tryProcessChat(packet.getChatComponent().getFormattedText(), packet.getChatComponent().getUnformattedText())) {
                    e.setCanceled(true);
                }
            }
        }
    }


    private boolean tryProcessChat(String message, final String unformatted) {
        String out = message;
        final String[] parts = out.split(" ");
        final String[] partsUnformatted = unformatted.split(" ");


        out = message;


        if(   out.contains("донат")){
            ++count;
            out = "";
        }
        if(   out.contains("оплаты")){
            ++count;
            out = "";
        }
        if(   out.contains("Купить ключ")){
            ++count;
            out = "";
        }
        if(   out.contains("После вайпа")){
            ++count;
            out = "";
        }
        if(   out.contains("Открыть купленные")){
            ++count;
            out = "";
        }
        if(   out.contains("§7[§r§e§l+§r§7]")){
            ++count;
            out = "";
        }
        if(   out.contains("/prize")){
            ++count;
            out = "";
        }
      //  if( &&  out.contains("*")){
       //     out = "";
       // }
        if(   out.contains("награда")){
            ++count;
            out = "";
        }

        if(   out.contains("§a§l§m")){
            ++count;
            out = "";
        }
        if(   out.contains("§a§l[!]")){
            ++count;
            out = "";
        }
        if(   out.contains("выбил из бесплатного")){
            ++count;
            out = "";
        }
        if(   out.contains("выделиться на сервере")){
            ++count;
            out = "";
        }
        if(   out.contains("большие скидки")){
            ++count;
            out = "";
        }
        if(   out.contains("руб")){
            ++count;
            out = "";
        }
        if(   out.contains("привяжите свой")){
            ++count;
            out = "";
        }
        if(   out.contains("сайте")){
            ++count;
            out = "";
        }

        try {
                if(!out.equals("")){
                    Command.sendMessageWithoutTH(out);
                }
        } catch (Exception exception){}
        return true;
    }


}

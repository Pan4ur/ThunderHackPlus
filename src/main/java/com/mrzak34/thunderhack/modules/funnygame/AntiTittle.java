package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class AntiTittle extends Module {

    public Setting<Boolean> tittle = this.register(new Setting<>("AntiTitle", true));
    public Setting<Boolean> armorstands = this.register(new Setting<>("AntiSpawnLag", true));
    public Setting<Boolean> scoreBoard = this.register(new Setting<>("ScoreBoard", true));
    public Setting<Integer> waterMarkZ1 = register(new Setting<>("Y", 10, 0, 524));
    public Setting<Integer> waterMarkZ2 = register(new Setting<>("X", 20, 0, 862));
    public Setting<Boolean> counter = this.register(new Setting<>("Counter", false));
    public Setting<Boolean> chat = this.register(new Setting<>("ChatAds", true));
    public Setting<Boolean> donators = this.register(new Setting<>("Donators", true));
    int count = 0;
    int y1 = 0;
    int x1 = 0;
    ScaledResolution sr = new ScaledResolution(mc);
    public AntiTittle() {
        super("Adblock", "Адблок для ебучего-фанигейма", Category.FUNNYGAME);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        if (counter.getValue()) {
            y1 = (int) (sr.getScaledHeight() / (1000f / waterMarkZ1.getValue()));
            x1 = (int) (sr.getScaledWidth() / (1000f / waterMarkZ2.getValue()));

            RenderUtil.drawSmoothRect(waterMarkZ2.getValue(), waterMarkZ1.getValue(), 75 + waterMarkZ2.getValue(), 11 + waterMarkZ1.getValue(), new Color(35, 35, 40, 230).getRGB());
            Util.fr.drawStringWithShadow("Ads Blocked : ", waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 1, PaletteHelper.astolfo(false, 1).getRGB());
            Util.fr.drawStringWithShadow(String.valueOf(count), waterMarkZ2.getValue() + 6 + Util.fr.getStringWidth("Ads Blocked : "), waterMarkZ1.getValue() + 1, -1);
        }
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (tittle.getValue()) {
            if (e.getPacket() instanceof SPacketTitle) {
                ++count;
                e.setCanceled(true);
            }
        }
        if (chat.getValue() && e.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = e.getPacket();
            if (shouldCancel(packet.getChatComponent().getFormattedText())) {
                e.setCanceled(true);
            }
        }
    }

    private boolean shouldCancel(String message) {
        if (message.contains("Все очистится через"))
            return true;
        if (message.contains("Предметы на карте успешно"))
            return true;
        if (message.contains("Обычный чат работает на")) // 99999 блоков, ведь можно донатить, поэтому передавай инфу в дс
            return true;
        if (message.contains("Хочешь выделиться на сервере?")) // крашни его, нахуя донатить?
            return true;
        if (message.contains("Успей использовать промо-код")) //дженро хуесос для получения бана навсегда!
            return true;
        if (message.contains("В данный момент действуют большие")) //заталкивания хуев вам в задницу, дорогие игроки серверов дженро!
            return true;
        if (message.contains("есть любые способы оплаты"))
            return true;
        if (message.contains("Открыть купленные ключи"))
            return true;
        if (message.contains("Группа сервера ВКонтакте"))
            return true;
        if (message.contains("чем больше ключей вы покупаете")) // тем меньше у вас iq
            return true;
        if (message.contains("Не хватает денег на привилегию"))
            return true;
        if (message.contains("Продавать что-либо за реальную валюту"))
            return true;
        if (message.contains("Сейчас действуют большие скидки"))
            return true;
        if (message.contains("/donate"))
            return true;
        if (message.contains("Чтобы избежать взлома"))
            return true;
        if (message.contains("Оскорбление администрации строго")) //разрешено!
            return true;
        if (message.contains("Включить пвп в своем регионе"))
            return true;
        if (message.contains("/trade"))
            return true;
        if (message.contains("После вайпа остается пароль+привилегия"))
            return true;
        if (message.contains("FunnyGame.su"))
            return true;

        if (donators.getValue()) {
            String premessage = message;
            message = message.replace("§r§6§l[§r§b§lПРЕЗИДЕНТ§r§6§l]§r", "§r");
            message = message.replace("§r§d§l[§r§5§lАдмин§r§d§l]§r", "§r");
            message = message.replace("§r§b§l[§r§3§lГл.Админ§r§b§l]§r", "§r");
            message = message.replace("§8[§r§6Игрок§r§8]§r", "§r");
            message = message.replace("§r§5§l[§r§e§lБОГ§r§5§l]§r", "§r");
            message = message.replace("§r§a§l[§r§2§lКреатив§r§a§l]", "§r");
            message = message.replace("§r§4§l[§r§c§lВладелец§r§4§l]", "§r");
            message = message.replace("§r§5§l[§r§d§lОснователь§r§5§l]", "§r");
            message = message.replace("§r§b§l[§r§e§l?§r§d§lСПОНСОР§r§e§l?§r§b§l]", "§r");
            message = message.replace("§r§6§l[§r§e§lЛорд§r§6§l]", "§r");
            message = message.replace("§r§4§l[§r§2§lВЛАДЫКА§r§4§l]", "§r");
            if (!message.equals(premessage)) {
                Command.sendMessageWithoutTH(message);
                return true;
            }

        }
        return false;
    }
}

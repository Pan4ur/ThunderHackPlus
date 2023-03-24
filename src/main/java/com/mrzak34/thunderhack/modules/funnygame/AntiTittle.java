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
import net.minecraft.util.text.ITextComponent;

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
            if (shouldCancel(packet.getChatComponent())) {
                e.setCanceled(true);
            }
        }
    }

    private boolean shouldCancel(ITextComponent component) {
        String spamMessage = component.getUnformattedText();
        String[] reklama = {
            "Все очистится через", // жопу
            "Предметы на карте успешно", // проёбаны
            "Обычный чат работает на", // 99999 блоков, ведь можно донатить, поэтому передавай инфу в дс
            "Хочешь выделиться на сервере?", // крашни его, нахуя донатить?
            "Успей использовать промо-код", //дженро хуесос для получения бана навсегда!
            "В данный момент действуют большие", //заталкивания хуев вам в задницу, дорогие игроки серверов дженро!
            "есть любые способы оплаты", // пон. А наличку принимаете?
            "Открыть купленные ключи",
            "Группа сервера ВКонтакте",
            "чем больше ключей вы покупаете", // тем меньше у вас iq
            "Не хватает денег на привилегию", // возьми в кредит!
            "Продавать что-либо за реальную валюту", // или не продавать -- зависит только от вас!
            "Сейчас действуют большие скидки", // которые не распространяются ни на что. Они просто действуют!
            "/donate", // /hub
            "Чтобы избежать взлома",
            "Оскорбление администрации строго", //разрешено!
            "Включить пвп в своем регионе",
            "/trade", // /lobby
            "После вайпа остается пароль+привилегия",
            "FunnyGame.su",
            
            "Выключите fly", // я вообще-то спид тестил!
            "Подождите, прежде чем снова щелкнуть.", // я вообще-то команды прописывал!
            "Вы находитесь в Лобби. Выберите сервер и пройдите в портал!", // Дай пароль ввести!
            "Чтобы избежать взлома, привяжите свой аккаунт",
            "[Анти-Релог]",
            "чем больше кейсов покупаете",
            "не разрешена в этом регионе.",
            "Вы выпили",
            "Вы бухнули", // а я с хачами ебусь, ебусь с хачами!
            "Извините, но Вы не можете" // спамить в чате, за спам нужно доплачивать!
        };
        
        for (String cnam: reklama) {
            if (spamMessage.contains(cnam)) return true;
        }

        if (donators.getValue()) {
            String message = component.getFormattedText();
            String real = message;
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
            if (!message.equals(real)) {
                Command.sendMessageWithoutTH(message);
                return true;
            }

        }
        return false;
    }
}

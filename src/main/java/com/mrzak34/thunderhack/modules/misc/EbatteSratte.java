package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.events.AttackEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class EbatteSratte extends Module {
    public EbatteSratte() {
        super("Ebatte Sratte", "авто токсик и не только xD", Module.Category.MISC);
    }

    Timer timer = new Timer();
    String chatprefix = "";

    public Setting<Integer> delay = this.register(new Setting<>("Delay", 500, 1, 1000));


    private Setting<mode> Mode = register(new Setting("Server", mode.FunnyGame));

    public enum mode {
        FunnyGame, DirectMessage, OldServer, Local;
    }

    private Setting<mode2> Mode2 = register(new Setting("Mode", mode2.Hard));

    public enum mode2 {
        ARSIK2005, Friendly, Lite, Hard, Erp, Funny;
    }


    String[] FriendlyString = new String[]{"Помурчи в дискордике пжжжж", "ты сегодня такая няшечка мммммм", "Котик извини", "Скинь ножки пжжжжжжж", "Скинь кфг пжжжж"};

    String[] Lite = new String[]{"Блять киллку настрой она же мисает я ебал АХАХААХ", "Блять удаляй чит он же нихуя не бустит", "Че так слабо АХАХАХАХ", "Сука как будто с ботом пехаюсь", "Блять включи киллку хоть заебал сосать", "Сука ты за этот чит бабки отдал лошара", "Ебать я тебя Вращаю на хуе"};

    String[] Arsik2005 = new String[]{
            "Я ТВОЕЙ МАТЕРИ ЖИРНОЙ СЛОМАЛ СПИНУ И СБРОСИЛ С 12-И ЭТАЖНОГО ДОМА ОНА ТАК РАЗБИЛАСЬ ПРИЯТНО АЖ ТЕЛО КАК ПОПРЫГУН ПОЛЕТЕЛО ВЫШЕ НЕБЕС ПОТОМ ТРУП ТВОЕЙ МАМАШИ УПАЛ И СЛОМАЛ АСВАЛЬТ ТОНКОЕ ЧМО НА ПОДВЕСКЕ",
            "Я ТВОЕЙ МАТЕРИ ПИЗДУ ДО ПОЛЬШИ РАЗРЕЗАЛ Я ХУЕЮ Я В ЭТОЙ ПИЗДЕ ПОТЕРЯЛСЯ НАХУЙ Я ТАМ НАШЁЛ 3 КОНЦА СВЕТА И САТАНУ НАХУЙ ЕБАТЬ Я АХУЕЛ ТАМ ДАЖЕ ВИКИНГИ ПЛОТЬЮ ПИЗДЫ ТВОЕЙ МАМАШИ ПИТАЮТСЯ",
            "ЕБАТЬ ТЫ ОТБРОС Я В ПИЗДУ ТВОЕЙ МАТЕРИ КЛЕЩЕЙ ЗАКИНУЛ ОНА ЭТОМУ МОЛИЛАСЬ ОНА МНЕ ВСЕ ДЕНЬГИ ОТДАЛА",
            "ОТБРОС ЕБЛИВЫЙ Я КОГДА УВИДЕЛ ТВОЙ 1 СМ ХУЙ ПОКА ЕБАЛ ТЕБЯ ЕБАТЬ Я АХУЕЛ ХУЛИ ТЕБЯ ЕЩЁ В ШКОЛЕ НЕ ПИЗДЯТ ЧМО",
            "ЧМО ТЕБЯ КАЖДУЮ МИНУТУ В ШКОЛЕ ПИЗДЯТ ИДИ В УГОЛКЕ КАК В ШКОЛЕ ПОСИДИ ПОПЛАЧ ЧМЫРЬ МАЛОЛЕТНИЙ",
            "ЧМО Я ТВОЕМУ ОТЦУ ХУЙ РАЗРЕЗАЛ КОГДА ОН ШЁЛ С АВТОМАТОМ НА УЛИЦЕ ЕБАТЬ ОН ЧМО МНЕ СТЫДНО ЧТО Я ТЕБЯ ВООБЩЕ ВИЖУ ПОТОМУ ЧТО ТВОЙ БАТЯ НА ПРОЩАНИЕ СКАЗАЛ НЕ ОТДАВАЙ СЫНУ ДЕНЬГИ Я В АХУЕ",
            "СТЫДОБА СКАЖИ БАТЕ ЧТО БЫ КУПИЛ ТЕБЕ СОФТ THUNDERHACK А НЕ ТВОЙ ЁБАННЫЙ МАТИКС ЗА 5 РУБЛЕЙ НАХУЙ КОТОРЫЙ МОЖНО СКАЧАТЬ В FLAUNCHER И ВООБЩЕ БОМЖИК ХУЛИ ТЫ СМОТРИШЬ НА ЛЮДЕЙ РАБЫНЯ РАБОВ",
            "ЧМО ЕБАТЬ У ТЕБЯ СЕСТРА УРОДИНА Я ВООБЩЕ ЕБАЛ МНЕ КАЖЕТСЯ ЕСЛИ ТВОЯ СЕСТРА ПОЙДЁТ НА РАБОТУ ШЛЮХИ ОНА ПЛАТИТЬ ЗА СЕКС БУДЕТ",
            "СКАЖИ СЕСТРЕ ЧТО БЫ ВСЕМ ПОДРЯД СИСЬКИ В ШКОЛЕ НЕ ПОКАЗЫВАЛА АТО У НЕЁ НЕ СИСЬКИ А МРАМОРНАЯ ПИЗДА ИЗ ВАКУОЛЬНОЙ ХУЙНИ ЕЁ ДАЖЕ СЕЛИКОН НЕ СПАСЁТ БЛЯТЬ МЕНЯ ЧУТЬ НЕ ВЫРВАЛО КОГДА ОНА МНЕ СИСЬКИ ПОКАЗАЛА",
            "ЕБАТЬ ГУБЫ ТВОЕЙ СЕСТРИЧКИ КАК 4 МУСОРОВОЗА МНЕ КАЖЕТСЯ ЕСЛИ ОНА БУДЕТ С КЕМТО СОСАТЬСЯ ТО ЕЁ ГУБЫ ЗАСОСУТ КОГО ТО НАХУЙ",
            "ЕБАТЬ СКАЖИ СВОЕЙ МАТЕРИ ЧТО БЫ МОЙ ОГУРЕЦ С МОЛОКОМ НЕ ЗАГЛАТЫВАЛА АТО ПРЯМО ПРИ МНЕ ЕЙ ПИЗДУ СНЕСЛО Я ДУМАЛ ОНА ПОКА СОСЁТ СДОХНЕТ ИЗЗА ПОТЕРИ ПУЛЬСА НАХУЙ",
            "ХАХАХХА Я ТВОЮ МАМКУ В УТОПИЛ ЕЛИ ЕЛИ Я АХУЕЛ КОГДА ПО НОВОСТЯМ ПОКАЗАЛО ЧТО ТВОЯ МАТЬ ДОХЛАЯ ПОЛ ЧЁРНОГО МОРЯ ПЕРЕКРЫЛА ПОПИТЬ ЗАХОТЕЛА НАХУЙ ПОТОМ СДОХЛА КСТАТИ ЕЁ ТРУП СМЕШНО ПОПЛЫЛ АЖ САМОЛЁТ СБИЛ"
    };
    String[] myString = new String[]{"Я TBOЮ MATЬ БЛЯTЬ ПOДВEСИЛ НА КОЛ ОНА EБAHAЯ БЛЯДИHA",
            "МАМАШУ ТВОЮ АРМАТУРОЙ С ШИПАМИ ПО ХРЕБТУ ПИЗДИЛ",
            "Я ТВОЕЙ МАТЕРИ ПИЗДАК РАЗОРВАЛ СЫН БЛЯДИНЫ ТЫ ЕБАННОЙ",
            "ВГЕТАЙ ТАНДЕРХАК СЫН ЕБАННОЙ ШЛЮХИ",
            "ТЫ ПСИНА БЕЗ БРЕЙНА ДАВАЙ ТЕРПИ ТЕРПИ",
            "я твою мать об стол xуяpил сын тупорылой овчарки мать продал чит на кубики купил?",
            "СКУЛИ СВИHЬЯ ЕБAHAЯ , Я ТВОЮ MATЬ ПОДBECИЛ НА ЦЕПЬ И С ОКНА СБРОСИЛ ОНА ФЕМИНИСТКА ЕБАHAЯ ОНА СВОИМ ВЕСОМ 180КГ ПРОБУРИЛАСЬ ДО ЯДРА ЗЕМЛИ И СГОРЕЛА HAXУЙ АХАХАХАХА ЕБATЬ ОНА ГОРИТ ПРИКОЛЬНО",
            "ты мейн сначало свой пукни потом чет овысирай, с основы пиши нищ",
            "БАБКА СДОХЛА ОТ СТАРОСТИ Т.К. КОГДА ТВОЮ МATЬ РОДИЛИ ЕЙ БЫЛО 99 ЛЕТ И ОТ НЕРВОВ РАДОСТИ ОНА СДОХЛА ОЙ БЛ9TЬ ОТ РАДОСТИ ДЕД ТОЖЕ ОТ РАДОСТИ СДОХ HAXУЙ ДOЛБAЁБ EБAHЫЙ ЧТОБЫ ВЫЖИТЬ НА ПОМОЙКА МATЬ ТВOЯ ПOКА НЕ СДОХЛА EБAЛAСЬ С МУЖИКАМИ ЗА 2 КОПЕЙКИ",
            "ТЫ ПОНИМАЕШЬ ЧТО Я ТВОЮ МАТЬ ОТПРАВИЛ СО СВОЕГО XУЯ В НЕБО, ЧТОБ ОНА СВОИМ ПИЗДAKOМ ПРИНИМАЛА МИТЕОРИТНУЮ АТАКУ?)",
            "ТЫ ПОНИМАЕШЬ ЧТО ТBОЯ МATЬ СИДИТ У МЕНЯ НА ЦЕПИ И КАК БУЛЬДОГ EБАHЫЙ НА МОЙ XУЙ СЛЮНИ БЛ9ДЬ ПУСКАЕТ?))",
            "В ДЕТДОМЕ ТЕБЯ ПИЗДUЛИ ВСЕ КТО МОГ В ИТОГЕ ТЫ СДОХ НА УЛИЦЕ В 13 ЛЕТ ОТ НЕДОСТАТКА ЕДЫ ВОДУ ТЫ ЖЕ БРАЛ ЭТИМ ФИЛЬТРОМ И МОЧОЙ ДOЛБAЁБ ЕБAHЫЙ СУКA БЕЗ МATEPHAЯ ХУETА.",
            "Чё как нищий, купи тандерхак не позорься",
            "Your mom owned by Thunderhack",
            "АЛО БОМЖАТИНА БЕЗ МАТЕРИ Я ТВОЮ МАТЬ ОБ СТОЛ УБИЛ ЧЕРЕП ЕЙ РАЗБИЛ НОГОЙ БАТЮ ТВОЕГО С ОКНА ВЫКИНУЛ СУКА ЧМО ЕБАННОЕ ОТВЕТЬ ЧМО ЕБЛАН ТВАРЬ ШАЛАВА"
    };

    String[] Erp = new String[]{
            "Помурчи пж",
            "Я ТЕБЯ ЛЮБЛЮ",
            "А ты меня любишь?",
            "i love u",
            "Мурчи громче блин",
            "Сейчас на Земле официально 7 999 999 999 людей, которые мне нравятся меньше тебя",
            "Ты милый. Можно тебя оставить себе?",
            "Мурлычу от любви",
            "Ты самая лучшая в моей жизни",
            "Ты очень красивая",
            "Люблю тебя всем сердцем и душой",
            "Я не смогу жить без тебя"
    };
        String[] Funny = new String[]{
            "ПОГНАЛИ НАХУЙ",
            "Я Бомж Валера",
            "Повар Спрашивает Повара Кем Работаешь А Повар Говорит Я МЕДИК СУКА",
            "Ну Чо Нормальна Тебе? Нармальна",
            "ААААА ЧИТЕР АААА",
            "Я В ШОКЕ БЛЯТЬ",
            "Я Вахуи С Тебя",
            "КАК ВЫРУБИТЬ ОПТИФАЙН",
            "СКАЧАТЬ МОД НА ФЛАЙ МАЙНКРАФТ",
           
    };
    @SubscribeEvent
    public void onAttackEntity(AttackEvent event) {
        if(event.getStage() == 1) return;
        if (event.getEntity() instanceof EntityPlayer) {
            if (timer.passedMs(delay.getValue() * 10)) {
                Entity entity = event.getEntity();
                if (entity == null) {
                    return;
                }

                int n = 0;

                if (Mode2.getValue() == mode2.Hard) {
                    n = (int) Math.floor(Math.random() * myString.length);
                } else if (Mode2.getValue() == mode2.Lite) {
                    n = (int) Math.floor(Math.random() * Lite.length);
                } else if (Mode2.getValue() == mode2.Friendly) {
                    n = (int) Math.floor(Math.random() * FriendlyString.length);
                } else if (Mode2.getValue() == mode2.ARSIK2005) {
                    n = (int) Math.floor(Math.random() * Arsik2005.length);
                } else if (Mode2.getValue() == mode2.Erp) {
                    n = (int) Math.floor(Math.random() * Erp.length);
                } else if (Mode2.getValue() == mode2.Funny) {
                    n = (int) Math.floor(Math.random() * Funny.length); 
                }

                if (Mode.getValue() == mode.FunnyGame) {
                    chatprefix = ("!");
                }
                if (Mode.getValue() == mode.OldServer) {
                    chatprefix = (">");
                }
                if (Mode.getValue() == mode.DirectMessage) {
                    chatprefix = ("/w ");
                }

                if (Mode2.getValue() == mode2.Hard) {
                    mc.player.sendChatMessage(chatprefix + entity.getName() + " " + myString[n]);
                } else if (Mode2.getValue() == mode2.Lite) {
                    mc.player.sendChatMessage(chatprefix + entity.getName() + " " + Lite[n]);
                } else if (Mode2.getValue() == mode2.Friendly) {
                    mc.player.sendChatMessage(chatprefix + entity.getName() + " " + FriendlyString[n]);
                } else if (Mode2.getValue() == mode2.ARSIK2005) {
                    mc.player.sendChatMessage(chatprefix + entity.getName() + " " + Arsik2005[n]);
                } else if (Mode2.getValue() == mode2.Erp) {
                    mc.player.sendChatMessage(chatprefix + entity.getName() + " " + Erp[n]);
                } else if (Mode2.getValue() == mode2.Funny) {
                    mc.player.sendChatMessage(chatprefix + entity.getName() + " " + Funny[n]);
                }
                timer.reset();
            }

        }
    }

}

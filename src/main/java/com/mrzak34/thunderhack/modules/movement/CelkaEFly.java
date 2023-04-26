package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.mixin.mixins.IEntityPlayerSP;
import com.mrzak34.thunderhack.mixin.mixins.MixinEntityPlayerSP;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.MovementUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CelkaEFly extends Module {
    public CelkaEFly() {
        super("CelkaEFly", "CelkaEFly", Category.MOVEMENT);
    }

    /////////////// !!!!!!!! АЛО !!!!!!!! //////////////////
    //
    //  Прежде чем ctrl + c ctrl + v ПРОЧИТАЙ мои комменты
    //         чтобы понять как работает код!!!
    //
    //   coded by Pan4ur and Shakal (шмот и моральная поддержка)
    //
    ///////////////////////////////////////////////////////


    private final Setting<Float> xzSpeed = this.register(new Setting<>("XZ Speed", 1.9f, 0.5f, 1.9f)); // горизонтальная скорость
    private final Setting<Float> ySpeed = this.register(new Setting<>("Y Speed", 0.47f, 0f, 2f)); // вертикальная скорость
    private final Setting<Integer> fireSlot = this.register(new Setting<>("Firework Slot", 0, 0, 8)); // если модуль не найдет фейерверк в хотбаре, то переложит в этот слот
    private final Setting<Float> fireDelay = this.register(new Setting<>("Firework Delay", 1.5f, 0, 1.5f)); // интервал использования фейерверков
    private final Setting<Boolean> stayMad = this.register(new Setting<Object>("Stay Off The Ground", true)); // не допускать касания земли
    private final Setting<Boolean> keepFlying = this.register(new Setting<Object>("Keep Flying", false)); // продолжить лететь если кончились фейерверки (иначе наденется нагрудник и модуль выключится)
    private final Setting<Boolean> bowBomb = this.register(new Setting<Object>("Bow Bomb", false)); // усиленная тряска для буста скорости стрел

    private int lastItem = -1; // пустой слот или слот с нагрудником
    private float acceleration; // множитель ускорения
    private boolean TakeOff = false; // флаг готовности к тейк оффу

    private int getElytra() {
        for (int i = 0; i < 36; i++) { // пробегаемся по слотам инвентаря
            ItemStack s = mc.player.inventory.getStackInSlot(i); // гетаем вещь из слота
            if (s.getItem() == Items.ELYTRA && s.getItemDamage() < 430) { // если вещь - элитра и целая..
                return i < 9 ? i + 36 : i; // возвращаем слот элитры, учитывая что нужно прибавлять 36, если она в хотбаре
            }
        }
        return -1; // возвращаем -1 (чтобы далее понимать что элитры у нас нет)
    }

    private int getFireworks() {
        for (int i = 0; i < 36; i++) { // пробегаемся по слотам инвентаря
            ItemStack s = mc.player.inventory.getStackInSlot(i); // гетаем вещь из слота
            if (s.getItem() == Items.FIREWORKS) { // если вещь - фейрверк..
                return i < 9 ? i + 36 : i; // возвращаем слот фейерверки, учитывая что нужно прибавлять 36, если они в хотбаре
            }
        }
        return -1; // возвращаем -1 (чтобы далее понимать что фейерверков у нас нет)
    }

    @Override
    public void onEnable() {
        acceleration = 0f; // сбрасываем множитель ускорения
        if(mc.player.inventory.getStackInSlot(38).getItem() == Items.ELYTRA) return; // возвращаемся если элитра надета на нас
        int elytra = getElytra(); // гетаем слот элитры
        if (elytra != -1) { // если элитра есть..
            lastItem = elytra; // запоминаем её слот, чтоб при выключении вернуть туда
            mc.playerController.windowClick(0, elytra, 0, ClickType.PICKUP, mc.player); // клик по элитре
            mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player); // клик по слоту брони игрока
            if(!mc.player.inventory.getItemStack().isEmpty()) // если в мышке осталась вещь..
                mc.playerController.windowClick(0, elytra, 0, ClickType.PICKUP, mc.player); // клик по слоту элитры (элитры там уже нет, но зато есть свободное место)
        }
    }

    @Override
    public void onDisable() {
        acceleration = 0f; // сбрасываем множитель ускорения
        if(lastItem == -1) return; // если мы не запоминали слот (если модуль был включен до захода в мир), возвращаемся
        mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player); // клик по слоту брони игрока
        mc.playerController.windowClick(0, this.lastItem, 0, ClickType.PICKUP, mc.player); // клик по слоту который мы запомнили
        if(!mc.player.inventory.getItemStack().isEmpty())  // если в мышке осталась вещь..
            mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player); // перекладываем эту вещь (нагрудник) в слот брони игрока
        lastItem = -1; // сбрасываем слот которвый мы запомнили
    }

    @Override
    public void onUpdate() {
        if (InventoryUtil.getFireWorks() == -1) { // если у нас нет фейерверков в хотбаре..
            int fireworkSlot = getFireworks(); // вводим переменную слота фейерверков (чтобы не вызывать цикл for несколько раз)
            if(fireworkSlot != -1){ // если у нас есть фейерверки в инвентаре..
                mc.playerController.windowClick(0, fireworkSlot, 0, ClickType.PICKUP, mc.player); // клик по слоту  фейерверками
                mc.playerController.windowClick(0, fireSlot.getValue() + 36, 0, ClickType.PICKUP, mc.player); // клик по слоту который указан в настройках (в целке "Слот с фейерверком")
                if(!mc.player.inventory.getItemStack().isEmpty())  // если в мышке осталась вещь..
                    mc.playerController.windowClick(0, fireworkSlot, 0, ClickType.PICKUP, mc.player); // перекладываем эту вещь в освободившийся слот от фейерверков
                return; // возвращаемся для повторной проверки
            }
            Command.sendMessage("Нет фейерверков!"); // оповещаем
            if(!keepFlying.getValue()) this.toggle(); // выключаем модуль если не включен чек "keepFlying" ("Продолжать полёт" в целке)
            return; // возвращаемся
        }
        if (getElytra() == -1 && mc.player.inventory.getStackInSlot(38).getItem() != Items.ELYTRA) { // Если у нас нет доступных элитр
            Command.sendMessage("Нет элитр!"); // оповещаем
            toggle(); // выключаем модуль
            return; // возвращаемся
        }

        if (mc.player.onGround) { // если игрок на земле
            mc.player.jump(); // подпрыгиваем
            TakeOff = true; // ставим флаг готовности к тейк оффу
        } else if (TakeOff && mc.player.fallDistance > 0.05) { // если готовы и падаем..
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING)); // посылаем пакет раскрытия элитр
            mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getFireWorks())); // переключаемся на фейерверки
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); // юзаем фейерверк
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); // калестиал юзает первый раз 2 фурки, сделаем также
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem)); // переключаемся обратно
            TakeOff = false; // убираем флаг готовности к тейк оффу
        }
    }

    /**
     * {@link MixinEntityPlayerSP}.
     *
     * Переходим к изменению наших моушенов
     * */

    @SubscribeEvent
    public void onMove(EventMove e){
        e.setCanceled(true); // отменяем, для изменения значений
        double motionY = 0; // вводим переменную дельты моушена по Y
        if (((IEntityPlayerSP) mc.player).wasFallFlying()) { // если мы летим на элитре
            if (mc.player.ticksExisted % (int)(fireDelay.getValue() * 20) == 0) { // каждые fireDelay * 20 тиков (в целестиале "Задержка фейерверка") ..
                if (InventoryUtil.getFireWorks() >= 0) { // если у нас есть феерверки..
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getFireWorks())); // переключаемся на фейерверки
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); // юзаем фейерверк
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem)); // переключаемся обратно
                }
            }

            if(!MovementUtil.isMoving()){ // если мы не движемся (именно не давим на WASD)
                e.set_x(0); // останавливаемся по X
                e.set_z(0); // останавливаемся по Z
                acceleration = 0f; // сбрасываем множитель ускорения
            } else { //
                double[] moveDirection = MovementUtil.forward(RenderUtil.lerp(0f, xzSpeed.getValue(), Math.min(acceleration, 1f))); // расчитываем моушены исходя из ускорения и угла поворота камеры
                e.set_x(moveDirection[0]); // выставляем моушен X
                e.set_z(moveDirection[1]); // выставляем моушен Z
                acceleration += 0.1f; // увеличивам множитель ускорения
            }

            if (mc.player.movementInput.jump) {   // если нажата кнопка прыжка (mc.gameSettings.keyBindJump.isKeyDown() не робит, хз почему)..
                motionY = ySpeed.getValue(); // дельта будет равна ySpeed (в целестиале "Скорость по Y")
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) { // иначе если нажат шифт
                if(!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -1.5, 0.0)).isEmpty() && stayMad.getValue()) // если мы касаемся земли и включен чек "Stay Off The Ground" (в целке "Не приземляться")..
                    motionY = ySpeed.getValue(); // обратно набираем высоту
                else  // иначе
                    motionY = -ySpeed.getValue(); // опускаемся вниз со скоростью ySpeed (в целестиале "Скорость по Y")
            } else { // иначе (если кнопки не нажаты)
                if(bowBomb.getValue()) // если включен чек bowBomb (в целке "Супер лук")
                    motionY += mc.player.ticksExisted % 2 == 0 ? -0.42f : 0.42f; // дельта будет равна  -0.42 или 0.42 через тик
                else  // иначе
                    motionY += mc.player.ticksExisted % 2 == 0 ? -0.08f : 0.08f; // дельта будет равна  -0.08 или 0.08 через тик
            }
            e.set_y(motionY); // выставляем моушен Y
        }
    }
}

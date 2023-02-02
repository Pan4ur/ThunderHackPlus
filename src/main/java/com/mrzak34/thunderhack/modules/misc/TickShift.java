package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.atomic.AtomicLong;

public class TickShift extends Module{

    public TickShift( ) {
        super ( "TickShift" , "тикшифт эксплоит" , Category.MISC);
    }


    public Setting <Float> timer = this.register(new Setting<>("Timer", 2.0f, 0.1f, 100.0f));
    public Setting <Integer> packets = this.register ( new Setting <> ( "Packets", 20, 0, 1000 ) );
    public Setting <Integer> lagTime = this.register ( new Setting <> ( "LagTime", 1000, 0, 10_000 ) );
    public Setting< Boolean > sneaking = this.register ( new Setting <> ( "Sneaking" , false ) );
    public Setting < Boolean > cancelGround = this.register ( new Setting <> ( "CancelGround" , false ) );
    public Setting < Boolean > cancelRotations = this.register ( new Setting <> ( "CancelRotation" , false ) );

    private final AtomicLong lagTimer  = new AtomicLong();
    private int ticks;



    public boolean passed(int ms)
    {
        return System.currentTimeMillis() - lagTimer.get() >= ms;
    }

    @SubscribeEvent
    public void onTick(TickEvent e){
        if (mc.player == null || mc.world == null || !passed(lagTime.getValue()))
        {
            rozetked();
        }
        else if (ticks <= 0 || noMovementKeys() || !sneaking.getValue() && mc.player.isSneaking())
        {
            Thunderhack.TICK_TIMER = 1.0f;
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
       // FontRender.drawString3(String.valueOf(Thunderhack.TICK_TIMER),200,200,-1);
       // FontRender.drawString3(String.valueOf(ticks),200,230,-1);
    }

    @SubscribeEvent
    public void onEventMove(EventMove e){
        Thunderhack.TICK_TIMER = 1.0f;
        int maxPackets = packets.getValue();
        ticks = ticks >= maxPackets ? maxPackets : ticks + 1;
    }



    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof SPacketPlayerPosLook){
            lagTimer.set(System.currentTimeMillis());
        }
    }


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(e.getPacket() instanceof CPacketPlayer.PositionRotation){
            hth(e, true);
        }
        if(e.getPacket() instanceof CPacketPlayer.Rotation){
            if (cancelRotations.getValue()
                    && (cancelGround.getValue()
                    || ((CPacketPlayer.Rotation) e.getPacket()).isOnGround() == mc.player.onGround))
            {
                e.setCanceled(true);
            }
            else
            {
                hth(e, false);
            }
        }
        if(e.getPacket() instanceof CPacketPlayer.Position){
            hth(e, true);
        }
        if(e.getPacket() instanceof CPacketPlayer){
            if (cancelGround.getValue())
            {
                e.setCanceled(true);
            }
            else
            {
                hth(e, false);
            }
        }
    }



    public static boolean noMovementKeys()
    {
        return !mc.player.movementInput.forwardKeyDown
                && !mc.player.movementInput.backKeyDown
                && !mc.player.movementInput.rightKeyDown
                && !mc.player.movementInput.leftKeyDown;
    }

    @Override
    public String getDisplayInfo()
    {
        return ticks + "";
    }

    @Override
    public void onEnable()
    {
        rozetked();
    }

    @Override
    public void onDisable()
    {
        rozetked();
    }

    private void hth(PacketEvent.Send event, boolean moving)
    {

        if (event.isCanceled())
        {
            return;
        }

        if (moving && !noMovementKeys() && (sneaking.getValue() || !mc.player.isSneaking()))
        {
            Thunderhack.TICK_TIMER = timer.getValue();

        }

        ticks = ticks <= 0 ? 0 : ticks - 1;
    }

    public void rozetked()
    {
        Thunderhack.TICK_TIMER = 1.0f;
        ticks = 0;
    }
}

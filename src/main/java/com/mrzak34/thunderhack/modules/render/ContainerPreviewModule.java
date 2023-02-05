package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;


import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ContainerPreviewModule extends Module {

    public ContainerPreviewModule() {
        super("ContainerPrev", "Показывает содержимое-контейнера", Category.RENDER);
    }
    public Setting<Integer> av = this.register ( new Setting <> ( "x", 256, 0, 1500) );
    public Setting <Integer> bv = this.register ( new Setting <> ( "y", 256, 0, 1500 ) );



    public Setting <Integer> colorr = this.register ( new Setting <> ( "Red", 100, 0, 255 ) );
    public Setting <Integer> colorg = this.register ( new Setting <> ( "Green", 100, 0, 255 ) );
    public Setting <Integer> colorb = this.register ( new Setting <> ( "Blue", 100, 0, 255 ) );
    public Setting <Integer> colora = this.register ( new Setting <> ( "Alpha", 100, 0, 255 ) );


    private HashMap<BlockPos, ArrayList<ItemStack>> PosItems = new HashMap<BlockPos, ArrayList<ItemStack>>();
    private int TotalSlots = 0;
    public ScaledResolution scaledResolution;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){

        if (event.getPacket() instanceof SPacketWindowItems)
        {

            final RayTraceResult ray = mc.objectMouseOver;

            if (ray == null)
                return;

            if (ray.typeOfHit != RayTraceResult.Type.BLOCK)
                return;

            IBlockState l_State = mc.world.getBlockState(ray.getBlockPos());

            if (l_State == null)
                return;

            if (l_State.getBlock() != Blocks.CHEST && !(l_State.getBlock() instanceof BlockShulkerBox))
                return;

            SPacketWindowItems l_Packet = (SPacketWindowItems) event.getPacket();

            final BlockPos blockpos = ray.getBlockPos();

            if (PosItems.containsKey(blockpos))
                PosItems.remove(blockpos);

            ArrayList<ItemStack> l_List = new ArrayList<ItemStack>();

            for (int i = 0; i < l_Packet.getItemStacks().size(); ++i)
            {
                ItemStack itemStack = l_Packet.getItemStacks().get(i);
                if (itemStack == null)
                    continue;

                if (i >= TotalSlots)
                    break;

                l_List.add(itemStack);
            }

            PosItems.put(blockpos, l_List);
        }
        else if (event.getPacket() instanceof SPacketOpenWindow)
        {
            final SPacketOpenWindow l_Packet = event.getPacket();
            TotalSlots = l_Packet.getSlotCount();
        }
    }






    @SubscribeEvent
    public void onRender2D(Render2DEvent p_Event){
        final RayTraceResult ray = mc.objectMouseOver;
        if (ray == null) {

            return;
        }
        if (ray.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        if (!PosItems.containsKey(ray.getBlockPos())){
            return;

        }


        BlockPos l_Pos = ray.getBlockPos();

        ArrayList<ItemStack> l_Items = PosItems.get(l_Pos);

        if (l_Items == null){
            return;
        }

        IBlockState pan4ur = mc.world.getBlockState(ray.getBlockPos());

            int l_I = 0;
            int l_Y = -20;
            int x = 0;

            for (ItemStack stack : l_Items)
            {
                if (stack != null)
                {
                    {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        RenderHelper.enableGUIStandardItemLighting();
                        RenderUtil.drawSmoothRect(av.getValue() - 3,bv.getValue() - 50,av.getValue() + 150, pan4ur.getBlock() != Blocks.CHEST ? bv.getValue() :bv.getValue() +  48, new Color(colorr.getValue(), colorg.getValue(),colorb.getValue(),colora.getValue()).getRGB());
                        GlStateManager.translate(x + av.getValue(), l_Y + mc.fontRenderer.FONT_HEIGHT - 19 + bv.getValue(), 0);
                        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
                        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, 0, 0);
                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.color(1, 1, 1, 1);
                        GlStateManager.popMatrix();
                        x += 16;


                    }
                }

                if (++l_I % 9 == 0)
                {
                    x = 0;
                    l_Y += 15;
                }
            }


    }
}

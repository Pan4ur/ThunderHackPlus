package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;


import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.GLUProjection;
import com.mrzak34.thunderhack.util.RenderUtil;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ContainerPreviewModule extends Module {

    public ContainerPreviewModule() {
        super("ContainerPrev", "Показывает содержимое-контейнера", Module.Category.MISC, true, false, false);
    }
    public Setting<Integer> av = this.register ( new Setting <> ( "x", 256, 0, 1500) );
    public Setting <Integer> bv = this.register ( new Setting <> ( "y", 256, 0, 1500 ) );



    public Setting <Integer> colorr = this.register ( new Setting <> ( "Red", 100, 0, 255 ) );
    public Setting <Integer> colorg = this.register ( new Setting <> ( "Green", 100, 0, 255 ) );
    public Setting <Integer> colorb = this.register ( new Setting <> ( "Blue", 100, 0, 255 ) );
    public Setting <Integer> colora = this.register ( new Setting <> ( "Alpha", 100, 0, 255 ) );


    private HashMap<BlockPos, ArrayList<ItemStack>> PosItems = new HashMap<BlockPos, ArrayList<ItemStack>>();
    private ICamera camera = new Frustum();
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
            final SPacketOpenWindow l_Packet = (SPacketOpenWindow) event.getPacket();
            TotalSlots = l_Packet.getSlotCount();
            return;
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


        final float[] bounds = this.convertBounds(l_Pos, p_Event.getPartialTicks(), 400, 400);


        if (bounds != null)
        {
            int l_I = 0;
            int l_Y = -20;
            int x = 0;

            for (ItemStack stack : l_Items)
            {
                if (stack != null)
                {

                    //==final Item item = stack.getItem();
                    //if (item != Items.AIR)
                    {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        RenderHelper.enableGUIStandardItemLighting();
                        RenderUtil.drawSmoothRect(av.getValue() - 3,bv.getValue() - 50,av.getValue() + 150, pan4ur.getBlock() != Blocks.CHEST ? bv.getValue() :bv.getValue() +  48, new Color(colorr.getValue(), colorg.getValue(),colorb.getValue(),colora.getValue()).getRGB());
                        GlStateManager.translate(bounds[0] + (bounds[2] - bounds[0]) / 2 + x + av.getValue(), l_Y + bounds[1] + (bounds[3] - bounds[1]) - mc.fontRenderer.FONT_HEIGHT - 19 + bv.getValue(), 0);
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

    private float[] convertBounds(BlockPos e, float partialTicks, int width, int height)
    {
        float x = -1;
        float y = -1;
        float w = width + 1;
        float h = height + 1;

        final Vec3d pos = new Vec3d(e.getX(), e.getY(), e.getZ());

        if (pos == null)
        {
            return null;
        }

        AxisAlignedBB bb = new AxisAlignedBB(
                e.getX() - mc.getRenderManager().viewerPosX,
                e.getY() - mc.getRenderManager().viewerPosY,
                e.getZ() - mc.getRenderManager().viewerPosZ,
                e.getX() + 1 - mc.getRenderManager().viewerPosX,
                e.getY() + 1 - mc.getRenderManager().viewerPosY,
                e.getZ() + 1 - mc.getRenderManager().viewerPosZ);

        bb = bb.expand(0.15f, 0.1f, 0.15f);

        camera.setPosition(mc.getRenderViewEntity().posX,
                mc.getRenderViewEntity().posY,
                mc.getRenderViewEntity().posZ);

        if (!camera.isBoundingBoxInFrustum(bb))
        {
            /// @todo: fix this
            //        return null;
        }

        final Vec3d corners[] =
                { new Vec3d(bb.minX - bb.maxX + 1 / 2, 0, bb.minZ - bb.maxZ + 1 / 2),
                        new Vec3d(bb.maxX - bb.minX - 1 / 2, 0, bb.minZ - bb.maxZ + 1 / 2),
                        new Vec3d(bb.minX - bb.maxX + 1 / 2, 0, bb.maxZ - bb.minZ - 1 / 2),
                        new Vec3d(bb.maxX - bb.minX - 1 / 2, 0, bb.maxZ - bb.minZ - 1 / 2),

                        new Vec3d(bb.minX - bb.maxX + 1 / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + 1 / 2),
                        new Vec3d(bb.maxX - bb.minX - 1 / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + 1 / 2),
                        new Vec3d(bb.minX - bb.maxX + 1 / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - 1 / 2),
                        new Vec3d(bb.maxX - bb.minX - 1 / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - 1 / 2) };

        for (Vec3d vec : corners)
        {
            final GLUProjection.Projection projection = GLUProjection.getInstance().project(
                    pos.x + vec.x - mc.getRenderManager().viewerPosX,
                    pos.y + vec.y - mc.getRenderManager().viewerPosY,
                    pos.z + vec.z - mc.getRenderManager().viewerPosZ,
                    GLUProjection.ClampMode.NONE, false);

            if (projection == null)
            {
                return null;
            }

            x = Math.max(x, (float) projection.getX());
            y = Math.max(y, (float) projection.getY());

            w = Math.min(w, (float) projection.getX());
            h = Math.min(h, (float) projection.getY());
        }

        if (x != -1 && y != -1 && w != width + 1 && h != height + 1)
        {
            return new float[]
                    { x, y, w, h };
        }

        return null;
    }
}

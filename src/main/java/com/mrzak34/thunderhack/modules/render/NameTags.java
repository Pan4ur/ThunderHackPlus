package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.PreRenderEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.*;
import com.mrzak34.thunderhack.setting.*;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;

import java.awt.*;
import net.minecraft.init.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.*;
import net.minecraft.item.*;
import net.minecraft.enchantment.*;
import net.minecraft.util.text.*;
import net.minecraft.nbt.*;
import com.mrzak34.thunderhack.util.*;

import java.util.*;
import com.mrzak34.thunderhack.*;

import static com.mrzak34.thunderhack.util.render.RenderUtil.drawRect;

public class NameTags extends Module
{
    private static NameTags INSTANCE;
    private final Setting<Boolean> health;
    private final Setting<Boolean> armor;
    private final Setting<Mode> mode;
    private final Setting<Float> scaling;
    private final Setting<Boolean> invisibles;
    private final Setting<Boolean> ping;
    private final Setting<Boolean> totemPops;
    private final Setting<Boolean> gamemode;
    private final Setting<Boolean> entityID;
    private final Setting<Boolean> rect;

    private final Setting<Boolean> group;
    private final Setting<Boolean> outline;
    private final Setting<Integer> redSetting;
    private final Setting<Integer> greenSetting;
    private final Setting<Integer> blueSetting;
    private final Setting<Integer> alphaSetting;
    private final Setting<Float> lineWidth;
    private final Setting<Boolean> sneak;
    private final Setting<Boolean> heldStackName;
    private final Setting<Boolean> whiter;
    private final Setting<Boolean> onlyFov;
    private final Setting<Boolean> scaleing;
    private final Setting<Float> factor;
    private final Setting<Boolean> smartScale;

    public NameTags() {
        super("NameTags",  "Better Nametags.",  Module.Category.RENDER);
        this.health = (Setting<Boolean>)this.register(new Setting("Health", true));
        this.armor = (Setting<Boolean>)this.register(new Setting("Armor", true));
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.MINIMAL));
        this.scaling = (Setting<Float>)this.register(new Setting("Size", 0.3f, 0.1f, 20.0f));
        this.invisibles = (Setting<Boolean>)this.register(new Setting("Invisibles", false));
        this.ping = (Setting<Boolean>)this.register(new Setting("Ping", true));
        this.totemPops = (Setting<Boolean>)this.register(new Setting("TotemPops", true));
        this.gamemode = (Setting<Boolean>)this.register(new Setting("Gamemode", false));
        this.entityID = (Setting<Boolean>)this.register(new Setting("ID", false));
        this.rect = (Setting<Boolean>)this.register(new Setting("Rectangle", true));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", false,  v -> this.rect.getValue()));
        this.redSetting = (Setting<Integer>)this.register(new Setting("Red", 255, 0, 255,  v -> this.outline.getValue()));
        this.greenSetting = (Setting<Integer>)this.register(new Setting("Green", 255, 0, 255,  v -> this.outline.getValue()));
        this.blueSetting = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255,  v -> this.outline.getValue()));
        this.alphaSetting = (Setting<Integer>)this.register(new Setting("Alpha", 255, 0, 255,  v -> this.outline.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.5f, 0.1f, 5.0f,  v -> this.outline.getValue()));
        this.sneak = (Setting<Boolean>)this.register(new Setting("SneakColor", false));
        this.heldStackName = (Setting<Boolean>)this.register(new Setting("StackName", false));
        this.whiter = (Setting<Boolean>)this.register(new Setting("White", false));
        this.onlyFov = (Setting<Boolean>)this.register(new Setting("OnlyFov", false));


        this.group = (Setting<Boolean>)this.register(new Setting("Group", false));


        this.scaleing = (Setting<Boolean>)this.register(new Setting("Scale", false));
        this.factor = (Setting<Float>)this.register(new Setting("Factor", 0.3f, 0.1f, 1.0f,  v -> this.scaleing.getValue()));
        this.smartScale = (Setting<Boolean>)this.register(new Setting("SmartScale", false,  v -> this.scaleing.getValue()));
        this.setInstance();
    }

    public static NameTags getInstance() {
        if (NameTags.INSTANCE == null) {
            NameTags.INSTANCE = new NameTags();
        }
        return NameTags.INSTANCE;
    }

    private void setInstance() {
        NameTags.INSTANCE = this;
    }

    @SubscribeEvent
    public void onNigga(PreRenderEvent event) {
        if (!fullNullCheck()) {
            for (final EntityPlayer player : NameTags.mc.world.playerEntities) {
                if (player != null && !player.equals((Object)NameTags.mc.player) && player.isEntityAlive() && (!player.isInvisible() || this.invisibles.getValue())) {
                    if (this.onlyFov.getValue() && !RotationUtil.isInFov((Entity)player)) {
                        continue;
                    }
                    final double x = this.interpolate(player.lastTickPosX,  player.posX,  event.getPartialTicks()) - NameTags.mc.getRenderManager().renderPosX;
                    final double y = this.interpolate(player.lastTickPosY,  player.posY,  event.getPartialTicks()) - NameTags.mc.getRenderManager().renderPosY;
                    final double z = this.interpolate(player.lastTickPosZ,  player.posZ,  event.getPartialTicks()) - NameTags.mc.getRenderManager().renderPosZ;
                    this.renderNameTag(player,  x,  y,  z,  event.getPartialTicks());
                }
            }
        }
    }

    public void drawOutlineRect(final float x,  final float y,  final float w,  final float h,  final int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth((float)this.lineWidth.getValue());
        GlStateManager.tryBlendFuncSeparate(770,  771,  1,  0);
        bufferbuilder.begin(2,  DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)x,  (double)h,  0.0).color(red,  green,  blue,  alpha).endVertex();
        bufferbuilder.pos((double)w,  (double)h,  0.0).color(red,  green,  blue,  alpha).endVertex();
        bufferbuilder.pos((double)w,  (double)y,  0.0).color(red,  green,  blue,  alpha).endVertex();
        bufferbuilder.pos((double)x,  (double)y,  0.0).color(red,  green,  blue,  alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private void renderNameTag(final EntityPlayer player,  final double x,  final double y,  final double z,  final float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5 : 0.7);
        final Entity camera = NameTags.mc.getRenderViewEntity();
        assert camera != null;
        final double originalPositionX = camera.posX;
        final double originalPositionY = camera.posY;
        final double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX,  camera.posX,  delta);
        camera.posY = this.interpolate(camera.prevPosY,  camera.posY,  delta);
        camera.posZ = this.interpolate(camera.prevPosZ,  camera.posZ,  delta);
        final String displayTag = this.getDisplayTag(player);
        final double distance = camera.getDistance(x + NameTags.mc.getRenderManager().viewerPosX,  y + NameTags.mc.getRenderManager().viewerPosY,  z + NameTags.mc.getRenderManager().viewerPosZ);
        final int width = mc.fontRenderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + this.scaling.getValue() * (distance * this.factor.getValue())) / 1000.0;
        if (distance <= 8.0 && this.smartScale.getValue()) {
            scale = 0.0245;
        }
        if (!this.scaleing.getValue()) {
            scale = this.scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f,  -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x,  (float)tempY + 1.4f,  (float)z);
        GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY,  0.0f,  1.0f,  0.0f);
        GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX,  (NameTags.mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f,  0.0f,  0.0f);
        GlStateManager.scale(-scale,  -scale,  scale);
        GlStateManager.disableDepth ( );
        GlStateManager.enableBlend ( );

        if (this.rect.getValue()) {
            drawRect((float)(-width - 2),  (float)(-(getFontHeight() + 1)),  width + 2.0f,  1.5f,  1426063360);
            if (this.outline.getValue()) {
                final int color = new Color(this.redSetting.getValue(),  this.greenSetting.getValue(),  this.blueSetting.getValue(),  this.alphaSetting.getValue()).getRGB();
                this.drawOutlineRect((float)(-width - 2),  (float)(-(NameTags.mc.fontRenderer.FONT_HEIGHT + 1)),  width + 2.0f,  1.5f,  color);
            }
        }

        GlStateManager.disableBlend ( );

        final ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
            renderMainHand.stackSize = 1;
        }
        if (this.heldStackName.getValue() && !renderMainHand.isEmpty && renderMainHand.getItem() != Items.AIR) {
            final String stackName = renderMainHand.getDisplayName();
            final int stackNameWidth = mc.fontRenderer.getStringWidth(stackName) / 2;
            GL11.glPushMatrix();
            GL11.glScalef(0.75f,  0.75f,  0.0f);
            mc.fontRenderer.drawStringWithShadow(stackName,  (float)(-stackNameWidth),  -(this.getBiggestArmorTag(player) + 20.0f),  -1);
            GL11.glScalef(1.5f,  1.5f,  1.0f);
            GL11.glPopMatrix();
        }

        if (armor.getValue()) {
            GlStateManager.pushMatrix();
            int xOffset = -8;

            for (final ItemStack stack : player.inventory.armorInventory) {
                if (stack == null) {
                    continue;
                }
                xOffset -= 8;
            }

            xOffset -= 8;
            final ItemStack renderOffhand = player.getHeldItemOffhand().copy();
            if (renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
                renderOffhand.stackSize = 1;
            }
            this.renderItemStack(renderOffhand,  xOffset);
            xOffset += 16;

            for (final ItemStack stack2 : player.inventory.armorInventory) {
                if (stack2 == null) {
                    continue;
                }
                final ItemStack armourStack = stack2.copy();
                if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
                    armourStack.stackSize = 1;
                }
                this.renderItemStack(armourStack,  xOffset);
                xOffset += 16;
            }

            this.renderItemStack(renderMainHand,  xOffset);
            GlStateManager.popMatrix();
        }
        mc.fontRenderer.drawStringWithShadow(displayTag,  (float)(-width),  (float)(-(getFontHeight() - 1)),  this.getDisplayColour(player));
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth ( );
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f,  1500000.0f);
        GlStateManager.popMatrix();
    }


    public int getFontHeight() {
        return mc.fontRenderer.FONT_HEIGHT;
    }

    private void renderItemStack ( ItemStack stack , int x ) {
        GlStateManager.pushMatrix ( );
        GlStateManager.depthMask ( true );
        GlStateManager.clear ( 256 );
        RenderHelper.enableStandardItemLighting ( );
        mc.getRenderItem ( ).zLevel = - 150.0f;
        GlStateManager.disableAlpha ( );
        GlStateManager.enableDepth ( );
        GlStateManager.disableCull ( );
        mc.getRenderItem ( ).renderItemAndEffectIntoGUI ( stack , x , - 26 );
        mc.getRenderItem ( ).renderItemOverlays ( mc.fontRenderer , stack , x , - 26 );
       mc.getRenderItem ( ).zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting ( );
        GlStateManager.enableCull ( );
        GlStateManager.enableAlpha ( );
        GlStateManager.scale ( 0.5f , 0.5f , 0.5f );
        GlStateManager.disableDepth ( );
        if (this.mode.getValue() != Mode.NONE) {
            this.renderEnchantmentText(stack,  x);
        }
        NBTTagCompound tag = stack.getTagCompound();
        if( (stack.isItemStackDamageable() && stack.getItemDamage() > stack.getMaxDamage()) || (tag != null && (tag instanceof SpecialTagCompound || tag.getBoolean("Unbreakable")))){
            this.renderEnchantmentText2(stack,  x);
        }
        GlStateManager.enableDepth ( );
        GlStateManager.scale ( 2.0f , 2.0f , 2.0f );
        GlStateManager.popMatrix ( );
    }

    private void renderEnchantmentText(final ItemStack stack,  final int x) {
        int enchantmentY = -34;
        if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
            mc.fontRenderer.drawStringWithShadow("god",  (float)(x * 2),  (float)enchantmentY,  -3977919);
            enchantmentY -= 8;
        }
        final NBTTagList enchants = stack.getEnchantmentTagList();
        for (int index = 0; index < enchants.tagCount(); ++index) {
            final short id = enchants.getCompoundTagAt(index).getShort("id");
            final short level = enchants.getCompoundTagAt(index).getShort("lvl");
            final Enchantment enc = Enchantment.getEnchantmentByID((int)id);
            if (enc != null) {
                if (this.mode.getValue() == Mode.MINIMAL) {
                    if (!(enc instanceof EnchantmentProtection)) {
                        continue;
                    }
                    final EnchantmentProtection e = (EnchantmentProtection)enc;
                    if (e.protectionType != EnchantmentProtection.Type.EXPLOSION && e.protectionType != EnchantmentProtection.Type.ALL) {
                        continue;
                    }
                }
                String encName = enc.isCurse() ? (TextFormatting.RED + enc.getTranslatedName((int)level).substring(11).substring(0,  1).toLowerCase()) : enc.getTranslatedName((int)level).substring(0,  1).toLowerCase();
                encName += level;
                mc.fontRenderer.drawStringWithShadow(encName,  (float)(x * 2),  (float)enchantmentY,  -1);
                enchantmentY -= 8;
            }
        }
    }

    private void renderEnchantmentText2(final ItemStack stack,  final int x) {
        int enchantmentY = -78;
        FontRender.drawString3("ILLEGAL",  (float)(x * 3.1),  (float)enchantmentY, PaletteHelper.rainbow(300, 1, 1).getRGB());
    }

    private float getBiggestArmorTag(final EntityPlayer player) {
        float enchantmentY = 0.0f;
        boolean arm = false;
        for (final ItemStack stack : player.inventory.armorInventory) {
            float encY = 0.0f;
            if (stack != null) {
                final NBTTagList enchants = stack.getEnchantmentTagList();
                for (int index = 0; index < enchants.tagCount(); ++index) {
                    final short id = enchants.getCompoundTagAt(index).getShort("id");
                    final Enchantment enc = Enchantment.getEnchantmentByID((int)id);
                    if (enc != null) {
                        encY += 8.0f;
                        arm = true;
                    }
                }
            }
            if (encY <= enchantmentY) {
                continue;
            }
            enchantmentY = encY;
        }
        final ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (renderMainHand.hasEffect()) {
            float encY2 = 0.0f;
            final NBTTagList enchants2 = renderMainHand.getEnchantmentTagList();
            for (int index2 = 0; index2 < enchants2.tagCount(); ++index2) {
                final short id = enchants2.getCompoundTagAt(index2).getShort("id");
                final Enchantment enc2 = Enchantment.getEnchantmentByID((int)id);
                if (enc2 != null) {
                    encY2 += 8.0f;
                    arm = true;
                }
            }
            if (encY2 > enchantmentY) {
                enchantmentY = encY2;
            }
        }
        final ItemStack renderOffHand;
        if ((renderOffHand = player.getHeldItemOffhand().copy()).hasEffect()) {
            float encY2 = 0.0f;
            final NBTTagList enchants2 = renderOffHand.getEnchantmentTagList();
            for (int index = 0; index < enchants2.tagCount(); ++index) {
                final short id2 = enchants2.getCompoundTagAt(index).getShort("id");
                final Enchantment enc = Enchantment.getEnchantmentByID((int)id2);
                if (enc != null) {
                    encY2 += 8.0f;
                    arm = true;
                }
            }
            if (encY2 > enchantmentY) {
                enchantmentY = encY2;
            }
        }
        return (arm ? 0 : 20) + enchantmentY;
    }

    private String getDisplayTag(final EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        if (!this.health.getValue()) {
            return name;
        }
        final float health = EntityUtil.getHealth(player);
        final String color = (health > 18.0f) ? "§a" : ((health > 16.0f) ? "§2" : ((health > 12.0f) ? "§e" : ((health > 8.0f) ? "§6" : ((health > 5.0f) ? "§c" : "§4"))));
        String pingStr = "";
        if (this.ping.getValue()) {
            try {
                final int responseTime = Objects.requireNonNull(NameTags.mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr = pingStr + responseTime + "ms ";
            }
            catch (Exception ignored) {}
        }
        String popStr = " ";
        if (this.totemPops.getValue()) {
           popStr += Thunderhack.combatManager.getPops(player);
        }
        String idString = "";
        if (this.entityID.getValue()) {
            idString = idString + "ID: " + player.getEntityId() + " ";
        }
        String gameModeStr = "";
        if (this.gamemode.getValue()) {
            gameModeStr = (player.isCreative() ? (gameModeStr + "[C] ") : ((player.isSpectator() || player.isInvisible()) ? (gameModeStr + "[I] ") : (gameModeStr + "[S] ")));
        }
        String groupStr = "";
        if (this.group.getValue()) {
            groupStr = (isKamsard(player) ? (groupStr + "[Kamsard] ") : isMoonshine(player) ? (groupStr + "[MoonShine] ") : isRage(player) ? (groupStr + "[RAGE] ") :(groupStr + ""));
        }
        name = ((Math.floor(health) == health) ? (name + color + " " + ((health > 0.0f) ? Integer.valueOf((int)Math.floor(health)) : "dead")) : (name + color + " " + ((health > 0.0f) ? Integer.valueOf((int)health) : "dead")));
        return groupStr + pingStr + idString + gameModeStr + name + popStr;
    }

    public boolean isKamsard(final EntityPlayer player){
            String name = player.getDisplayName().getFormattedText();
            if(name.contains("MrZak34")){
                return true;
            }
            if(name.contains("MrZak")){
                return true;
            }
            if(name.contains("uxokpro1234")){
                    return true;
            }
            if(name.contains("mapcrash")){
                return true;
            }
            if(name.contains("MrZak2b2t")){
                 return true;
            }
            if(name.contains("Cattyn")){
                return true;
            }
            if(name.contains("pan4ur")){
                 return true;
            }
            if(name.contains("Ebatte_Sratte")){
                 return true;
            }
        return name.contains("nocum1");
    }

    public boolean isRage(final EntityPlayer player){
        String name = player.getDisplayName().getFormattedText();
        if(name.contains("SevaPosik")){
            return true;
        }
        if(name.contains("Ken")){
            return true;
        }
        if(name.contains("hohohohoho")){
            return true;
        }
        if(name.contains("Dm_Kristina")){
            return true;
        }
        if(name.contains("qwesxzas111")){
            return true;
        }
        if(name.contains("XxX_Lite_XxX")){
            return true;
        }
        if(name.contains("your_weakness")){
            return true;
        }
        return name.contains("arsik2005");
    }


    public boolean isMoonshine(final EntityPlayer player){
        String name = player.getDisplayName().getFormattedText();
        if(name.contains("cumermen")){
            return true;
        }
        if(name.contains("Aviasales")){
            return true;
        }
        if(name.contains("Aviaplanes")){
            return true;
        }
        if(name.contains("1Ahr")){
            return true;
        }
        if(name.contains("BIKMUNNI")){
            return true;
        }
        if(name.contains("KorshunInc")){
            return true;
        }
        if(name.contains("POCKYBOI")){
            return true;
        }
        if(name.contains("Aguzok")){
            return true;
        }
        return name.contains("2b2tdupealt");
    }

    private int getDisplayColour(final EntityPlayer player) {
        int colour = -5592406;
        if (this.whiter.getValue()) {
            colour = -1;
        }
        if (Thunderhack.friendManager.isFriend(player)) {
            return -11157267;
        }
        if (player.isInvisible()) {
            colour = -1113785;
        }
        else if (player.isSneaking() && this.sneak.getValue()) {
            colour = -6481515;
        }
        return colour;
    }

    private double interpolate(final double previous,  final double current,  final float delta) {
        return previous + (current - previous) * delta;
    }

    static {
        NameTags.INSTANCE = new NameTags();
    }

    public enum Mode
    {
        FULL,
        MINIMAL,
        NONE;
    }
}
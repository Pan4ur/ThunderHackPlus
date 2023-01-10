package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.misc.NameProtect;
import com.mrzak34.thunderhack.modules.misc.PasswordHider;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin(value={FontRenderer.class})
public abstract class MixinFontRenderer {
    @Shadow
    protected abstract void renderStringAtPos(String var1, boolean var2);



    @Redirect(method = {"renderString(Ljava/lang/String;FFIZ)I"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(FontRenderer fontRenderer, String string, boolean bl) {
        if(Thunderhack.moduleManager == null){
            renderStringAtPos(string, bl);
            return;
        }
        if(Thunderhack.moduleManager.getModuleByClass(PasswordHider.class).isEnabled()) {
            if(string.contains("/l") || string.contains("/login") || string.contains("/reg") || string.contains("/register") && mc.currentScreen instanceof GuiChat) {
                StringBuilder final_string = new StringBuilder("");
                for(char cha: string.replace("/login","").replace("/register","").replace("/l ","").replace("/reg ","").toCharArray()){
                    final_string.append("*");
                }

                if(string.contains("/register")){
                    renderStringAtPos("/register " + final_string, bl);
                    return;
                }else if(string.contains("/login")){
                    renderStringAtPos("/login " + final_string, bl);
                    return;
                } else if(string.contains("/l")) {
                    renderStringAtPos("/l " + final_string, bl);
                    return;
                } else if(string.contains("/reg")){
                    renderStringAtPos("/reg " + final_string, bl);
                    return;
                }
            }
        }

        if (Thunderhack.moduleManager.getModuleByClass(NameProtect.class).isEnabled()) {
            if(mc == null || mc.getSession() == null){
                return;
            }
            renderStringAtPos(string.replace(mc.getSession().getUsername(), "Protected"), bl);
        } else{
            renderStringAtPos(string, bl);
        }
    }

}

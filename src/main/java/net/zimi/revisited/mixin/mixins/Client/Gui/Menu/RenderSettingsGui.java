package net.zimi.revisited.mixin.mixins.Client.Gui.Menu;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import deci.d.f;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin({deci.i.f.class})
public class RenderSettingsGui extends GuiScreen {
    @Inject(method = {"initGui"}, at = {@At("RETURN")})
    public void initGui(CallbackInfo ci) {
        this.buttonList.add(new f(1003, this.width / 2 - 155, this.height / 6 + 72 - 30, 150, 20, EnumChatFormatting.RED + "Accessibility"));
        this.buttonList.add(new f(1004, this.width / 2 + 5, this.height / 6 + 72 - 30, 150, 20, EnumChatFormatting.RED + "Credits"));
        this.buttonList.removeIf(button -> (((GuiButton)button).id == 1000));
    }

    @ModifyArg(method = {"initGui"}, at = @At(value = "INVOKE", target = "Ldeci/d/f;<init>(IIIIILjava/lang/String;)V", ordinal = 11), index = 5)
    public String modifyResourceTitle(String string) {
        return EnumChatFormatting.RED + "Resource Pack";
    }

    @ModifyArg(method = {"drawScreen"}, at = @At(value = "INVOKE", target = "Ldeci/j/a;a(Ljava/lang/String;FFFI)V", ordinal = 1), index = 0)
    public String texturePackCreditsDeci(String string) {
        return "";
    }

    @ModifyArg(method = {"drawScreen"}, at = @At(value = "INVOKE", target = "Ldeci/j/a;a(Ljava/lang/String;FFFI)V", ordinal = 0), index = 0)
    public String texturePackCreditsDerek(String string) {
        return "";
    }
}

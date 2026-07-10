package net.zimi.revisited.mixin.mixins.Client.Gui.Menu;

import deci.a.b;
import deci.d.i;
import deci.i.a;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({a.class})
public class RenderMainMenu extends i {
    public RenderMainMenu(b b1) {
        super(b1);
    }

    @ModifyVariable(method = {"drawScreen"}, at = @At("STORE"), index = 12)
    public int newWidth(int original) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), (Minecraft.getMinecraft()).displayWidth, (Minecraft.getMinecraft()).displayHeight);
        int scale = sr.getScaleFactor();
        return 100 + scale * 25;
    }
}

package net.zimi.revisited.mixin.mixins.Client.Gui.Menu;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import deci.g.c;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SideOnly(Side.CLIENT)
@Mixin({c.class})
public class RenderBankerContainer {
    @ModifyConstant(method = {"drawGuiContainerBackgroundLayer(FII)V"}, constant = {@Constant(intValue = 24)})
    private int fixedSlotCount(int original) {
        return 18;
    }
}

package net.zimi.revisited.mixin.mixins.Client.Gui.Container;

import deci.f.b;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = {b.class}, remap = false)
public class ContainerBanker {
    @ModifyConstant(method = {"a(Lnet/minecraft/entity/player/InventoryPlayer;ZLnet/minecraft/entity/player/EntityPlayer;III)V"}, constant = {@Constant(intValue = 24)})
    private int fixedSlotCount(int original) {
        return 18;
    }
}

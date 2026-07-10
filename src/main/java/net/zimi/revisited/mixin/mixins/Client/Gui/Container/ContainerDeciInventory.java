package net.zimi.revisited.mixin.mixins.Client.Gui.Container;

import deci.f.d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({d.class})
public class ContainerDeciInventory {
    @ModifyArg(method = {"a(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/entity/player/EntityPlayer;III)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSoundEffect(DDDLjava/lang/String;FF)V", ordinal = 1), index = 3)
    public String playBPSound(String p_72908_7_) {
        return "deci:item.backpack.zip";
    }
}

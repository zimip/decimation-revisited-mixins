package net.zimi.revisited.mixin.mixins.Common.Entity.Trader;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = deci.ai.k.class, remap = false)
public abstract class TraderFood extends deci.ai.a{
    public TraderFood(World world) {
        super(world);
    }

    @ModifyArg(
            method = "<init>(Lnet/minecraft/world/World;)V",
            at = @At(value = "INVOKE", target = "Ldeci/ai/k;a(Lnet/minecraft/item/Item;I)V"),
            index = 1
    )
    private int modifyFoodPrices(int originalPrice) {
        return originalPrice * 3;
    }
}

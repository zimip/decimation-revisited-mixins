package net.zimi.revisited.mixin.mixins.Common.Entity.Trader;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = deci.ai.h.class, remap = false)
public abstract class TraderVehicle extends deci.ai.a{
    public TraderVehicle(World world) {
        super(world);
    }
    @ModifyArg(method = "<init>(Lnet/minecraft/world/World;)V", at = @At(value = "INVOKE", target = "Ldeci/ai/h;a(Lnet/minecraft/item/Item;I)V", ordinal = 1), index = 1)
    public int buggy(int par2) {
        return 10000;
    }

    @ModifyArg(method = "<init>(Lnet/minecraft/world/World;)V", at = @At(value = "INVOKE", target = "Ldeci/ai/h;a(Lnet/minecraft/item/Item;I)V", ordinal = 2), index = 1)
    public int hummer(int par2) {
        return 30000;
    }

    @ModifyArg(method = "<init>(Lnet/minecraft/world/World;)V", at = @At(value = "INVOKE", target = "Ldeci/ai/h;a(Lnet/minecraft/item/Item;I)V", ordinal = 3), index = 1)
    public int truck(int par2) {
        return 75000;
    }

    @ModifyArg(method = "<init>(Lnet/minecraft/world/World;)V", at = @At(value = "INVOKE", target = "Ldeci/ai/h;a(Lnet/minecraft/item/Item;I)V", ordinal = 4), index = 1)
    public int apc(int par2) {
        return 500000;
    }
}

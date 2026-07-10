package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.ad.e;
import deci.ad.g;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = {g.class}, remap = false)
public abstract class EntityAPC extends e {
    public EntityAPC(World world, double v, double v1, double v2) {
        super(world, v, v1, v2);
    }

    @ModifyArg(method = {"<init>(Lnet/minecraft/world/World;DDD)V"}, at = @At(value = "INVOKE", target = "Ldeci/ad/g;u(F)V", ordinal = 0))
    public float init(float par1) {
        return 300.0F;
    }
}

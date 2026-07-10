package net.zimi.revisited.mixin.mixins.Common.Entity;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = deci.ad.d.class)
public abstract class EntitySeatShootable extends deci.ad.c {
    public EntitySeatShootable(World world) {
        super(world);
    }

    @ModifyArg(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSoundEffect(DDDLjava/lang/String;FF)V", ordinal = 0), index = 4)
    private float updateVolume(float p_72908_8_) {
        return 8F;
    }
}

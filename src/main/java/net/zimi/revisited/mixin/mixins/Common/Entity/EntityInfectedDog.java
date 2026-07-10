package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.ag.d;
import deci.ag.g;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({g.class})
public class EntityInfectedDog extends d {
    public EntityInfectedDog(World world) {
        super(world);
    }

    @Inject(method = {"onLivingUpdate"}, at = {@At("TAIL")})
    public void onLivingUpdate(CallbackInfo ci) {
        if (getAttackTarget() != null) {
            getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35D);
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
        }
    }
}

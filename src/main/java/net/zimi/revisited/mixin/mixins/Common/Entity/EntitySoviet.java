package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.ae.b;
import deci.ag.a;
import deci.ag.m;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = {m.class}, remap = false)
public abstract class EntitySoviet extends a {
    public EntitySoviet(World world) {
        super(world);
    }

    @Inject(method = {"c"}, at = {@At(value = "INVOKE", target = "Ldeci/ae/b;equals(Ljava/lang/Object;)Z")}, locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void modifyTarget(EntityLivingBase par1, CallbackInfoReturnable<Boolean> cir, b var2) {
        cir.setReturnValue(!var2.equals(b.SOVIET));
    }
}

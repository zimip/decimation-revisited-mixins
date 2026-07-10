package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.aE.a;
import deci.ak.c;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {c.class}, remap = false)
public abstract class EntityGrenade extends EntityThrowable {
    public EntityGrenade(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    @Inject(method = {"eK"}, at = {@At(value = "INVOKE", target = "Lcpw/mods/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToAllAround(Lcpw/mods/fml/common/network/simpleimpl/IMessage;Lcpw/mods/fml/common/network/NetworkRegistry$TargetPoint;)V")})
    public void onExplode(CallbackInfo ci) {
        deci.aF.a.a.a.gB().sendToAll(new a.W("ExplosionSmoke", this.posX, this.posY, this.posZ));
        deci.aF.a.a.a.gB().sendToAll(new a.W("ExplosionFlash", this.posX, this.posY, this.posZ));
    }
}

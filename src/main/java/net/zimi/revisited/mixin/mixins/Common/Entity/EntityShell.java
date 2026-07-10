package net.zimi.revisited.mixin.mixins.Common.Entity;

import cpw.mods.fml.common.network.NetworkRegistry;
import deci.aE.a;
import deci.ak.j;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({j.class})
public abstract class EntityShell extends Entity implements IProjectile {
    public EntityShell(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    @Inject(method = {"onUpdate"}, at = {@At(value = "INVOKE", target = "Ldeci/ak/j;playSound(Ljava/lang/String;FF)V", shift = At.Shift.AFTER)})
    public void onBulletBoomBoom(CallbackInfo ci) {
        playSound("deci:explosion", 3.5F, 0.85F);
        if (this.worldObj.isRemote)
            return;
        deci.aF.a.a.a.gB().sendToAll(new a.W("ExplosionSmoke", this.posX, this.posY, this.posZ));
        deci.aF.a.a.a.gB().sendToAll(new a.W("ExplosionFlash", this.posX, this.posY, this.posZ));
        deci.aF.a.a.a.gB().sendToAllAround(new a.ag(this.posX, this.posY, this.posZ, 20.0F), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 12.0D));
    }
}

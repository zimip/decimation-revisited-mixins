package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.aE.a;
import deci.ak.f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {f.class}, remap = false)
public abstract class EntityRocket extends Entity implements IProjectile {
    public EntityRocket(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    @Inject(method = {"eK"}, at = {@At(value = "INVOKE", target = "Lcpw/mods/fml/relauncher/Side;isServer()Z", shift = At.Shift.AFTER, ordinal = 0)})
    public void onExplode(CallbackInfo ci) {
        deci.aF.a.a.a.gB().sendToAll(new a.W("ExplosionSmoke", this.posX, this.posY, this.posZ));
        deci.aF.a.a.a.gB().sendToAll(new a.W("ExplosionFlash", this.posX, this.posY, this.posZ));
    }
}

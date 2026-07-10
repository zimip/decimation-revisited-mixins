package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.aE.a;
import deci.aj.c;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = {c.class}, remap = false)
public abstract class EntityLandmine extends Entity {
    @Unique
    private boolean hasExploded = false;

    public EntityLandmine(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void fe() {
        if (this.hasExploded)
            return;
        this.hasExploded = true;
        setDead();
        deci.aF.a.a.a.gB().sendToAll(new a.W("ExplosionSmoke", this.posX, this.posY, this.posZ));
        deci.aF.a.a.a.gB().sendToAll(new a.W("ExplosionFlash", this.posX, this.posY, this.posZ));
        this.worldObj.createExplosion(this, this.posX, this.posY + 0.25D, this.posZ, 3.0F, false);
    }
}

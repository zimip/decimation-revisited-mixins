package net.zimi.revisited.mixin.mixins.Common.Entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = deci.ag.e.class)
public abstract class EntityBloater extends deci.ag.d {
    @Unique
    private boolean markExploding;
    @Unique
    private int fuseTimer = 20;
    @Unique
    private float explosionRadius = 3.0F;

    public EntityBloater(World world) {
        super(world);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote) return;

        EntityLivingBase target = this.getAttackTarget();
        if (target == null) return;

        if (!(target instanceof EntityPlayer || target instanceof deci.ah.d)) return;

        if (this.getDistanceSqToEntity(target) < (double) (this.explosionRadius * this.explosionRadius) && (!this.isInWater() || !target.isInWater())) {
            this.markExploding = true;
        }

        if (this.markExploding) {
            this.getNavigator().clearPathEntity();
            this.motionX = this.motionY = this.motionZ = 0.0D;

            if (--this.fuseTimer <= 0) {
                this.doExplosion();
            }
        }
    }

    @Unique
    private void doExplosion() {
        this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, 2.5F, false, false);
        try {
            this.onDeath(DamageSource.generic);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        this.setDead();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
    }
}
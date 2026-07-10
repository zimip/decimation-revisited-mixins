package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.Q.b;
import deci.aD.k;
import deci.ak.c;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(deci.ak.p.class)
public abstract class EntityGasGrenade extends c {
    @Shadow
    public boolean acz;

    @Shadow
    public int acC;

    @Shadow
    public double acD;

    @Shadow
    public double acE;

    @Shadow
    public boolean acA;

    public EntityGasGrenade(World world, EntityLivingBase entityLivingBase, double v, int i) {
        super(world, entityLivingBase, v, i);
    }

    @Overwrite
    public void onUpdate() {
        super.onUpdate();

        if (this.acz) {
            if (this.worldObj.isRemote && this.acC % 10 == 0 && this.acD < this.acE) {
                this.acD += 0.25F;
            }

            if (this.acC % 4 == 0) {
                if (!this.acA) {
                    this.acA = true;
                }
            }

            if (this.acC-- <= 0) {
                this.setDead();
            }
        }

        if (this.acA && !this.worldObj.isRemote) {
            for (Object entity : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(this.posX - 6.0D, this.posY - 3.0D, this.posZ - 6.0D, this.posX + 6.0D, this.posY + 5.0D, this.posZ + 6.0D))) {
                if (entity instanceof EntityPlayer && this.getDistanceToEntity((EntityPlayer) entity) < 5.0F) {
                    EntityPlayer target = (EntityPlayer) entity;
                    b theirData = b.e(target);

                    if (theirData == null) continue;

                    if (!target.capabilities.isCreativeMode && !target.isPotionActive(Potion.poison) && (theirData.cx() == null || (theirData.cx().getItem() != k.apo && theirData.cx().getItem() != k.app))) {

                        target.addPotionEffect(new PotionEffect(Potion.poison.getId(), 40, 4, true));

                    }
                }
            }
        }
    }
}

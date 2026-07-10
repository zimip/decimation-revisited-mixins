package net.zimi.revisited.mixin.mixins.Common.Entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = deci.ak.o.class, remap = false)
public abstract class EntityStunGrenade extends EntityThrowable {

    @Shadow
    public boolean acf;
    @Shadow
    public boolean acB;

    public EntityStunGrenade(World world) {
        super(world);
    }

    @Overwrite
    public void eK() {
        this.acf = true;
        if (!this.acB) {
            this.worldObj.playSoundAtEntity(this, "deci:item.grenade.stun.explode", 3.0F, 1.0F);
            this.worldObj.playSoundAtEntity(this, "deci:item.grenade.stun.drain", 3.0F, 1.0F);

            if (!this.worldObj.isRemote) {
                for (Object obj : this.worldObj.playerEntities) {
                    if (obj instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) obj;
                        double distance = player.getDistanceToEntity(this);

                        if (distance < 50.0D && isLookingAtGrenade(player, this)) {

                            int stunTime = 0;
                            if (distance < 10.0D) {
                                stunTime = 80;
                            } else if (distance < 15.0D) {
                                stunTime = 60;
                            } else if (distance < 20.0D) {
                                stunTime = 30;
                            } else if (distance < 35.0D) {
                                stunTime = 10;
                            }

                            if (stunTime > 0) {
                                deci.aF.a.a.a.gB().sendTo(new deci.aE.a.u(player.getEntityId(), stunTime), player);
                            }
                        }
                    }
                }
            }
            this.acB = true;
        }
    }

    /**
     * Helper matematico per capire se la granata è nel campo visivo (FOV) del giocatore.
     * Molto più veloce e sicuro rispetto a modificare la rotationYaw del giocatore.
     */
    @Unique
    private boolean isLookingAtGrenade(EntityPlayer player, Entity grenade) {
        if (!player.canEntityBeSeen(grenade)) {
            return false;
        }

        Vec3 lookVec = player.getLook(1.0F).normalize();

        double gHeight = (grenade.boundingBox.minY + grenade.boundingBox.maxY) / 2.0D;
        Vec3 toGrenadeVec = Vec3.createVectorHelper(grenade.posX - player.posX, gHeight - (player.posY + player.getEyeHeight()), grenade.posZ - player.posZ).normalize();

        double dotProduct = lookVec.dotProduct(toGrenadeVec);

        return dotProduct > 0.5D;
    }
}
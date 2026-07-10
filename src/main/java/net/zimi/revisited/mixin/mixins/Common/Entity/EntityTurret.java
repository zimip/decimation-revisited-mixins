package net.zimi.revisited.mixin.mixins.Common.Entity;

import cpw.mods.fml.common.FMLCommonHandler;
import deci.ag.d;
import deci.aD.h;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = deci.al.b.class, remap = false)
public abstract class EntityTurret extends Entity {

    @Shadow private boolean aas;
    @Shadow private int aat;
    @Shadow private int aau;
    @Shadow private int acK;
    @Shadow private float acJ;

    @Unique
    private EntityLivingBase target;

    public EntityTurret(World world) {
        super(world);
    }

    @Overwrite
    public void eA() {
        if (this.target != null) {
            if (this.target.isDead || this.getDistanceToEntity(this.target) > 32.0F || !this.target.canEntityBeSeen(this)) {
                this.target = null;
            } else {
                return;
            }
        }

        if (this.ticksExisted % 10 == 0) {
            List<EntityLivingBase> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
                    AxisAlignedBB.getBoundingBox(this.posX - 32.0D, this.posY - 5.0D, this.posZ - 32.0D, this.posX + 32.0D, this.posY + 4.0D, this.posZ + 32.0D));

            for (EntityLivingBase entity : list) {
                if (!entity.canEntityBeSeen(this)) continue;

                boolean validTarget = false;

                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    deci.Q.b data = deci.Q.b.e(player);

                    if (data != null && data.cm() && !net.decimation.mod.server.zones.b.a(player, net.decimation.mod.server.zones.a.SAFEZONE)) {
                        validTarget = true;
                    }
                }
                else if (entity instanceof d) {
                    validTarget = true;
                }

                if (validTarget) {
                    this.target = entity;
                    if (!this.aas) {
                        this.aas = true;
                        this.aat = 190;
                        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "deci:entity.turret.detect", 1.0F, 2.0F);
                    }
                    break;
                }
            }
        }
    }

    @Overwrite
    public void onUpdate() {
        super.onUpdate();
        this.eA();

        if (this.aat > 0) {
            this.aat--;
            if (FMLCommonHandler.instance().getSide().isClient() && this.aas && this.target == null) {
                float f1 = (float)(this.aat * 4);
                float f2 = (float)(Math.sin(f1 / 50.0F) * 10.0D);
                this.rotationYaw += f2;
            }
            if (this.aat % 20 == 0) {
                this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "deci:entity.turret.searching", 1.0F, 1.0F);
            }
        }

        if (this.acK > 0) this.acK--;

        if (this.aat <= 0 && this.aas) {
            this.aat = 0;
            this.aas = false;
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "deci:entity.turret.detect", 1.0F, 1.5F);
        }

        if (this.target != null) {
            if (this.target.isDead) {
                this.target = null;
            } else {
                this.ft();
                this.shootTarget(this.target);
            }
        }
    }

    @Overwrite
    public void ft() {
        if (this.target == null) return;

        double d1 = this.target.posX - this.posX;
        double d2 = this.target.posY - this.posY + 1.5D;
        double d3 = this.target.posZ - this.posZ;

        float targetYaw = 180.0F + (float)Math.atan2(d3, d1) * 180.0F / (float)Math.PI;
        float targetPitch = -((float)Math.atan2(d2, Math.sqrt(d1 * d1 + d3 * d3))) * 180.0F / (float)Math.PI;

        float speed = 0.25F;

        float yawDiff = MathHelper.wrapAngleTo180_float(targetYaw - this.rotationYaw);
        float pitchDiff = MathHelper.wrapAngleTo180_float(targetPitch - this.rotationPitch);

        this.acJ = targetYaw;

        if (Math.abs(yawDiff) > 3.0F) {
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "deci:entity.turret.move", 2.0F, 1.0F);
        }

        this.rotationYaw += yawDiff * speed;
        this.rotationPitch += pitchDiff * speed;

        this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
        this.rotationPitch = MathHelper.wrapAngleTo180_float(this.rotationPitch);
    }

    @Unique
    private void shootTarget(EntityLivingBase target) {
        if (this.aau > 0) {
            this.aau--;
        } else {
            this.acK = 1;
            this.worldObj.playSoundEffect(this.posX, this.posY + 1.5F, this.posZ, "deci:entity.turret.shoot", 1.0F, 1.0F);

            if (this.rand.nextFloat() < 0.8F) {
                target.attackEntityFrom(h.alg, 3.0F);
                this.worldObj.playSoundEffect(target.posX, target.posY, target.posZ, "deci:mob.human.bulletimpact", 1.0F, 1.0F);
            }
            this.aau = 1;
        }
    }

    @Overwrite
    public Entity fv() {
        return this.target;
    }
}
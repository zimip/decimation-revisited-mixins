package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.aD.h;
import deci.ay.i;
import net.decimation.mod.common.item.armor.ItemArmorDeci;
import net.decimation.mod.common.item.armor.ItemMaskDeci;
import net.decimation.mod.common.item.armor.ItemVestDeci;
import net.decimation.mod.server.zones.a;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.zimi.revisited.Addon;
import net.zimi.revisited.Common.Entity.AIEntityShoot;
import net.zimi.revisited.Common.Entity.AITargetSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = {deci.ag.a.class}, remap = false)
public abstract class EntityBandit extends deci.ah.d {
    @Shadow
    private int aau;

    @Shadow
    public abstract ItemStack eD();

    @Shadow
    private int aaw;

    @Shadow
    private int aav;

    @Shadow
    private ItemStack aar;

    public EntityBandit(World world) {
        super(world);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void onConstruct(World world, CallbackInfo ci) {
        this.tasks.taskEntries.clear();
        this.targetTasks.taskEntries.clear();

        this.tasks.addTask(1, new EntityAISwimming(this));

        this.tasks.addTask(2, new AIEntityShoot((deci.ag.a) (Object)( this)));

        this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, net.minecraft.entity.player.EntityPlayer.class, 8.0F));
        this.tasks.addTask(5, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, new AITargetSelector((deci.ag.a) (Object) this)));
    }

    @Overwrite
    public void onUpdate() {
        super.onUpdate();

        if (this.aar != null && this.aar.getItem() instanceof i) {
            this.setCurrentItemOrArmor(0, this.aar);
        }
    }

    @Overwrite
    public void e(EntityLivingBase target) {
        if (target == null || target.isDead) return;

        if (!this.worldObj.isRemote) {
            double startX = this.posX;
            double startY = this.posY + (double) this.getEyeHeight() - 0.1D;
            double startZ = this.posZ;

            double targetX = target.posX;
            double targetY = target.posY + (rand.nextInt() == 2 ? (double) target.getEyeHeight() : 1);
            double targetZ = target.posZ;

            if (this.aau > 0) {
                --this.aau;
            } else {
                if (this.eD() == null || !(this.eD().getItem() instanceof i)) return;

                i gun = (i) this.eD().getItem();

                if (rand.nextInt(4) == 1) {
                    gun.i(this.eD(), 1);
                    Addon.Network.PACKET.sendToAll(new Addon.Message_RenderTracer(startX, startY, startZ, targetX, targetY, targetZ, i.a.WHITE));

                    if (target instanceof EntityPlayer) {
                        target.attackEntityFrom(h.alh, calculateDamage((EntityPlayer) target, gun.aew));
                    } else {
                        target.attackEntityFrom(h.alh, (float) (gun.aew / (target instanceof deci.ag.d ? 1 : 8)));
                    }

                        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "deci:" + gun.aer + "Fire", 1.0F, 1.0F);
                } else {
                    Addon.Network.PACKET.sendToAll(new Addon.Message_RenderTracer(startX, startY, startZ, targetX, targetY, targetZ, i.a.WHITE));
                    this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "deci:" + gun.aer + "Fire", 1.0F, 1.0F);
                }

                this.aau = this.aaw + (new Random()).nextInt(this.aav - this.aaw + 1);
            }
        }
    }

    @Unique
    private float calculateDamage(EntityPlayer target, float baseDamage) {
        deci.Q.b targetData = deci.Q.b.e(target);
        float damageToApply = baseDamage;
        if (targetData == null) return 0;

        for (int i = 0; i < 3; ++i) {
            if (target.getCurrentArmor(i) != null && target.getCurrentArmor(i).getItem() instanceof ItemArmorDeci) {
                ItemArmorDeci armor = (ItemArmorDeci) target.getCurrentArmor(i).getItem();
                damageToApply *= armor.getDamageMultiplier();
            }
        }

        if (targetData.cx() != null && targetData.cx().getItem() instanceof ItemMaskDeci) {
            damageToApply *= ((ItemMaskDeci) targetData.cx().getItem()).getDamageMultiplier();
        }

        if (targetData.cw() != null && targetData.cw().getItem() instanceof ItemVestDeci) {
            damageToApply *= ((ItemVestDeci) targetData.cw().getItem()).getDamageMultiplier();
        }

        return damageToApply;
    }
}

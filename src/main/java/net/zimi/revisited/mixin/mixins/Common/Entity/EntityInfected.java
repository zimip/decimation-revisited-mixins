package net.zimi.revisited.mixin.mixins.Common.Entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import deci.ag.d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.zimi.revisited.Common.Entity.AIInvestigateSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = d.class)
public abstract class EntityInfected extends deci.af.d {

    @Shadow(remap = false) public d aaH;
    @Shadow(remap = false) public boolean aaG;
    @Shadow(remap = false) public abstract int eI();
    @Shadow(remap = false) @SideOnly(Side.CLIENT) public abstract void eJ();

    protected EntityInfected(World world) {
        super(world);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"), remap = false)
    private void onInit(World world, CallbackInfo ci) {
        this.tasks.addTask(3, new AIInvestigateSound((d)(Object)this));
    }

    /**
     * @author Zimi
     * @reason Ottimizzazione estrema TPS (Horde check ricalcolato ogni secondo invece che a ogni tick)
     */
    @Overwrite
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.worldObj.isRemote) {
            this.eJ();
        }

        if (this.ticksExisted % 20 == 0) {
            byte hordeMinSize = 8;
            this.aaG = this.worldObj.getEntitiesWithinAABB(d.class, this.boundingBox.expand(20.0D, 0.0D, 20.0D)).size() >= hordeMinSize;
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.aaG ? 0.3D : 0.25D);
        }

        if (this.getAITarget() != null && Math.random() < 0.1D) {
            this.swingItem();
        }

        if (this.aaG && Math.random() < 0.02D) {
            this.swingItem();
            this.playSound("deci:mob.infected.horde", 1.0F, 1.0F);
        }

        this.updateWanderPath();

        if (this.aaH != null && this.getDistanceSqToEntity(this.aaH) > 10.0D && this.getDistanceSqToEntity(this.aaH) < 15.0D && this.getAITarget() == null && this.getAttackTarget() == null && this.getNavigator().noPath() && Math.random() < 0.1D) {
            this.getNavigator().tryMoveToEntityLiving(this.aaH, 1.0D);
        }

        if (this.ticksExisted == 5 && this.getEquipmentInSlot(0) == null) {
            switch (this.eI()) {
                case 1:
                    this.setCurrentItemOrArmor(4, new ItemStack(deci.aD.k.anJ, 1));
                    this.setCurrentItemOrArmor(3, new ItemStack(deci.aD.k.anK, 1));
                    this.setCurrentItemOrArmor(2, new ItemStack(deci.aD.k.anL, 1));
                    this.setCurrentItemOrArmor(1, new ItemStack(deci.aD.k.anM, 1));
                    break;
                case 2:
                    this.setCurrentItemOrArmor(4, new ItemStack(deci.aD.k.anu, 1));
                    this.setCurrentItemOrArmor(3, new ItemStack(deci.aD.k.anw, 1));
                    this.setCurrentItemOrArmor(2, new ItemStack(deci.aD.k.anx, 1));
                    this.setCurrentItemOrArmor(1, new ItemStack(deci.aD.k.any, 1));
                    break;
            }
        }
    }

    /**
     * @author Zimi
     * @reason Morso (25% di chance di ignorare il safe-check)
     */
    @Overwrite
    public boolean attackEntityAsMob(Entity var1) {
        boolean attackSuccess = super.attackEntityAsMob(var1);

        if (attackSuccess && var1 instanceof EntityPlayer) {
            EntityPlayer target = (EntityPlayer) var1;
            deci.Q.b data = deci.Q.b.e(target);

            if (data != null && !data.cm()) {
                if (this.rand.nextInt(4) == 1) {
                    target.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You've been bitten!"));
                    data.f(true);
                }
            }
        }
        return attackSuccess;
    }

    /**
     * @author Zimi
     * @reason Nuovi rate di drop
     */
    @Overwrite
    protected Item getDropItem() {
        double rand = Math.random();
        return (rand < 5.0E-4D) ? deci.aD.k.alo : ((rand < 0.7D) ? deci.aD.k.aln : deci.aD.k.apt);
    }

    /**
     * @author Zimi
     * @reason Range d'aggro aumentato
     */
    @Overwrite
    protected Entity findPlayerToAttack() {
        EntityPlayer var1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, 20.0D);
        return var1 != null && this.canEntityBeSeen(var1) ? var1 : null;
    }
}
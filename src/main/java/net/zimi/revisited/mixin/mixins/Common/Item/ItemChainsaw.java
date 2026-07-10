package net.zimi.revisited.mixin.mixins.Common.Item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(deci.az.a.class)
public abstract class ItemChainsaw extends deci.az.b {

    @Shadow
    public abstract void n(ItemStack itemStack);

    @Shadow
    public abstract boolean playerHasFuel(EntityPlayer entityPlayer);

    @Shadow
    public abstract boolean N(ItemStack itemStack);

    @Shadow
    public abstract void h(EntityPlayer entityPlayer);

    @Shadow
    public abstract void a(ItemStack itemStack, Entity entity);

    public ItemChainsaw(int i, int i1) {
        super(i, i1);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Ldeci/az/b;<init>(II)V"), index = 1, remap = false)
    private static int modifyDamage(int i) {
        return 0;
    }

    @Overwrite
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + "Tool/Melee Weapon");
        tooltip.add(EnumChatFormatting.GRAY + "Right-click to Turn On/Off");
        tooltip.add(EnumChatFormatting.GRAY + "Damage " + EnumChatFormatting.RED + "8 (when on)");
        tooltip.add(this.playerHasFuel(player) ? EnumChatFormatting.GREEN + "Has fuel" : EnumChatFormatting.RED + "No fuel");
    }

    @Overwrite
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean wasOn = this.N(stack);

        if (wasOn || world.rand.nextInt(3) == 0) {
            this.a(stack, player);

            boolean isNowOn = this.N(stack);

            if (!world.isRemote) {
                if (isNowOn) {
                    world.playSoundEffect(player.posX, player.posY, player.posZ, "deci:item.chainsaw.start", 1.0F, 1.0F);
                } else {
                    world.playSoundEffect(player.posX, player.posY, player.posZ, "deci:item.chainsaw.stop", 1.0F, 1.0F);
                }
            }
        }
        return stack;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if ((player.capabilities.isCreativeMode || this.playerHasFuel(player)) && this.N(stack)) {

                if (!player.worldObj.isRemote) {
                    player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "deci:item.chainsaw.attack", 1.0F, 1.0F);
                } else {
                    player.worldObj.spawnParticle("smoke", player.posX, player.posY, player.posZ, 0.0D, 0.2D, 0.0D);
                }
            }
        }
        return false;
    }


    @Overwrite
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (stack.stackTagCompound == null) {
            this.n(stack);
        }

        NBTTagCompound nbt = stack.stackTagCompound;
        if (nbt == null) return;

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            if (isSelected || player.getHeldItem() == stack) {
                if (this.playerHasFuel(player) && this.N(stack)) {

                    int fuelTimer = nbt.getInteger("FuelTimer");
                    if (fuelTimer > 0) {
                        fuelTimer--;
                    } else {
                        if (!world.isRemote) this.h(player);
                        fuelTimer = 10;
                    }
                    nbt.setInteger("FuelTimer", fuelTimer);

                    int idleTimer = nbt.getInteger("IdleTimer");
                    if (idleTimer <= 0) {
                        idleTimer = 15;
                        if (!world.isRemote) {
                            world.playSoundEffect(player.posX, player.posY, player.posZ, "deci:item.chainsaw.idle", 1.0F, 1.0F);
                        }
                    } else {
                        idleTimer--;
                    }
                    nbt.setInteger("IdleTimer", idleTimer);
                }
            } else {
                nbt.setInteger("IdleTimer", 15);

                if (this.N(stack)) {
                    this.a(stack, player);
                    if (!world.isRemote) {
                        world.playSoundEffect(player.posX, player.posY, player.posZ, "deci:item.chainsaw.stop", 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayer) {
            EntityPlayer playerAttacker = (EntityPlayer) attacker;
            if (this.N(stack) && this.playerHasFuel(playerAttacker)) {
                target.attackEntityFrom(deci.aD.h.alh, 8.0F);
            }
        }
        return true;
    }
}
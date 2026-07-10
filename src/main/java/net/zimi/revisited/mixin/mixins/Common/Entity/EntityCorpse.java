package net.zimi.revisited.mixin.mixins.Common.Entity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Deque;

@Mixin(value = deci.ae.a.class, remap = false)
public abstract class EntityCorpse extends EntityLiving implements IEntityAdditionalSpawnData, IInventory {

    @Final
    @Shadow
    private ItemStack[] Zw;
    @Final
    @Shadow
    private Deque<ItemStack> Zx;

    public EntityCorpse(World p_i1595_1_) {
        super(p_i1595_1_);
    }

    @Shadow
    public static ItemStack f(ItemStack stack) {
        return null;
    }

    @Shadow
    public abstract String ef();

    @Shadow
    public abstract void E(String var1);

    @Overwrite
    public void writeSpawnData(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.ef() != null && !this.ef().isEmpty() ? this.ef() : "");
        for (int i = 0; i < 8; i++) {
            ByteBufUtils.writeItemStack(buf, this.Zw[i]);
        }
    }

    @Overwrite
    public void readSpawnData(ByteBuf buf) {
        this.E(ByteBufUtils.readUTF8String(buf));
        for (int i = 0; i < 8; i++) {
            if (buf.isReadable()) {
                this.Zw[i] = ByteBufUtils.readItemStack(buf);
            }
        }
    }

    @Inject(method = "onEntityUpdate", at = @At("RETURN"), remap = true)
    public void onUpdateEntity(CallbackInfo ci) {
        if (this.isInWater()) {
            int x = net.minecraft.util.MathHelper.floor_double(this.posX);
            int y = net.minecraft.util.MathHelper.floor_double(this.posY);
            int z = net.minecraft.util.MathHelper.floor_double(this.posZ);

            int topWaterY = y;
            while (this.worldObj.getBlock(x, topWaterY + 1, z).getMaterial() == net.minecraft.block.material.Material.water && topWaterY < 255) {
                topWaterY++;
            }

            double targetY = topWaterY + 0.65D;
            double distance = targetY - this.posY;

            if (distance > 0.5D) {
                this.motionY += 0.05D;

                if (this.motionY > 0.15D) {
                    this.motionY = 0.15D;
                }

            } else {
                double bobbing = Math.sin((this.ticksExisted + this.getEntityId()) * 0.1D) * 0.04D;
                double idealY = targetY + bobbing;

                this.motionY += (idealY - this.posY) * 0.3D;
                this.motionY *= 0.6D;
            }

            this.motionX *= 0.9D;
            this.motionZ *= 0.9D;
        }
    }

    /**
     * @author Zimi
     * @reason Motore fisico completamente riscritto. Aderisce al suolo con la gravità e viene "sputato" fuori se si incastra.
     */
    @Overwrite
    private void eg() {
        if (!this.onGround && this.motionY > -0.5D) {
            this.motionY -= 0.08D;
        }

        this.motionX *= 0.5D;
        this.motionZ *= 0.5D;

        int bx = MathHelper.floor_double(this.posX);
        int by = MathHelper.floor_double(this.posY + 0.1D);
        int bz = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getBlock(bx, by, bz).getMaterial().blocksMovement()) {
            double diffX = this.posX - (double) bx;
            double diffY = this.posY - (double) by;
            double diffZ = this.posZ - (double) bz;

            boolean freeXMinus = !this.worldObj.getBlock(bx - 1, by, bz).getMaterial().blocksMovement();
            boolean freeXPlus = !this.worldObj.getBlock(bx + 1, by, bz).getMaterial().blocksMovement();
            boolean freeYPlus = !this.worldObj.getBlock(bx, by + 1, bz).getMaterial().blocksMovement();
            boolean freeZMinus = !this.worldObj.getBlock(bx, by, bz - 1).getMaterial().blocksMovement();
            boolean freeZPlus = !this.worldObj.getBlock(bx, by, bz + 1).getMaterial().blocksMovement();

            byte direction = -1;
            double minDistance = 9999.0D;

            if (freeXMinus && diffX < minDistance) {
                minDistance = diffX;
                direction = 0;
            }
            if (freeXPlus && 1.0D - diffX < minDistance) {
                minDistance = 1.0D - diffX;
                direction = 1;
            }
            if (freeYPlus && 1.0D - diffY < minDistance) {
                minDistance = 1.0D - diffY;
                direction = 2;
            }
            if (freeZMinus && diffZ < minDistance) {
                minDistance = diffZ;
                direction = 3;
            }
            if (freeZPlus && 1.0D - diffZ < minDistance) {
                direction = 4;
            }

            float ejectForce = 0.3F;

            if (direction == 0) this.motionX = -ejectForce;
            else if (direction == 1) this.motionX = ejectForce;
            else if (direction == 2) this.motionY = ejectForce;
            else if (direction == 3) this.motionZ = -ejectForce;
            else if (direction == 4) this.motionZ = ejectForce;
            else {
                this.motionY = ejectForce * 1.5F;
            }
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }

    @Overwrite
    public ItemStack getEquipmentInSlot(int slot) {
        if (slot < 0 || slot >= this.Zw.length) return null;
        return this.Zw[slot];
    }

    @Overwrite
    public void setCurrentItemOrArmor(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.Zw.length) this.Zw[slot] = stack;
    }

    @Overwrite
    public ItemStack g(ItemStack stack) {
        if (stack == null || stack.getItem() instanceof deci.av.c) return null;

        ItemStack hardCopy = stack.copy();

        for (int i = 5; i < this.Zw.length; ++i) {
            if (this.Zw[i] == null) {
                this.Zw[i] = hardCopy;
                return null;
            }
        }

        if (this.Zx.size() < 54) {
            this.Zx.addLast(hardCopy);
            return null;
        }

        return hardCopy;
    }
}
package net.zimi.revisited.mixin.mixins.Common;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import deci.Q.b;
import deci.ay.i;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.zimi.revisited.Addon;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = b.class, remap = false)
public abstract class DeciPlayerData {
    @Shadow
    @Final
    private EntityPlayer player;

    @Shadow
    public int Vo;

    @Shadow
    private int Wu;

    @Shadow
    private int WP;

    @Shadow
    private int VC;

    @Shadow
    private EntityTracker WM;

    @Shadow
    @Final
    private static SimpleNetworkWrapper Vm;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void dataConstructor(EntityPlayer par1, CallbackInfo ci) {
        this.Vo = 500000;
    }

    /**
     * @author zim
     * @reason Fix null checks and stance transitions
     */
    @Overwrite(remap = false)
    public void u(int stance) {
        Block blockAtFeet = null;
        boolean canChangeStance = true;

        if (!this.player.worldObj.isRemote) {
            int requiredHeight = 0;
            switch (stance) {
                case 0:
                case 1:
                    requiredHeight = 1;
                    break;
                case 2:
                    break;
            }

            for (byte b1 = 0; b1 < 8; b1++) {
                float offsetX = ((b1 % 2) - 0.5F) * this.player.width * 0.8F;
                float offsetY = (((b1 >> 1) % 2) - 0.5F) * 0.1F;
                float offsetZ = (((b1 >> 2) % 2) - 0.5F) * this.player.width * 0.8F;

                int x = MathHelper.floor_double(this.player.posX + offsetX);
                int y = MathHelper.floor_double(this.player.posY + this.player.getEyeHeight() + offsetY);
                int z = MathHelper.floor_double(this.player.posZ + offsetZ);

                for (int yOff = 0; yOff <= requiredHeight; yOff++) {
                    Block checkBlock = this.player.worldObj.getBlock(x, y + yOff, z);
                    if (!this.a(checkBlock)) {
                        canChangeStance = false;
                        break;
                    }
                    if (yOff == 0) blockAtFeet = checkBlock;
                }
                if (!canChangeStance) break;
            }
        }

        switch (stance) {
            case 0:
                this.WP = 10;
                break;
            case 1:
                this.WP = 5;
                break;
            case 2:
                this.WP = 20;
                break;
        }

        if (this.Wu != stance) {
            if (this.Wu == 0) {
                this.VC = this.Wu;
                this.Wu = stance;
            } else if (this.Wu == 1) {
                if (stance == 0 || stance == 2) {
                    this.VC = this.Wu;
                    this.Wu = stance;
                }
            } else if (this.Wu == 2) {
                if (this.player.worldObj.isRemote || (canChangeStance && this.a(blockAtFeet))) {
                    this.VC = this.Wu;
                    this.Wu = stance;
                }
            }
        }

        if (!this.player.worldObj.isRemote) {
            this.syncStanceImmediately(stance);
        }
    }

    @Unique
    public void syncStanceImmediately(int stance) {
        if (!this.player.worldObj.isRemote) {
            Addon.Network.PACKET.sendToAll(new Addon.Message_SyncStance(this.player.getEntityId(), stance));
        }
    }

    /**
     * @author zim
     * @reason fixes block checks for stances.
     */
    @Overwrite
    public boolean a(Block givenBlock) {
        if (givenBlock == null || givenBlock instanceof net.minecraft.block.BlockAir) return true;

        if (givenBlock instanceof net.minecraft.block.BlockSlab || givenBlock instanceof net.minecraft.block.BlockBush || givenBlock instanceof net.minecraft.block.BlockFlower || givenBlock instanceof net.minecraft.block.BlockTallGrass || givenBlock instanceof net.minecraft.block.BlockLadder) {
            return true;
        }

        Material mat = givenBlock.getMaterial();
        if (mat == Material.plants || mat == Material.vine || mat == Material.water || mat == Material.web) {
            return true;
        }

        try {
            int x = MathHelper.floor_double(this.player.posX);
            int y = MathHelper.floor_double(this.player.posY);
            int z = MathHelper.floor_double(this.player.posZ);
            return givenBlock.getCollisionBoundingBoxFromPool(this.player.worldObj, x, y, z) == null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @author zimip
     * @reason solves desync with helditem - Fixed EntityTracker and Null check
     */
    @Overwrite
    public void cS() {
        if (this.player == null || this.player.worldObj == null || this.player.worldObj.isRemote) {
            return;
        }

        if (this.WM == null && this.player.worldObj instanceof WorldServer) {
            this.WM = ((WorldServer) this.player.worldObj).getEntityTracker();
        }

        if (this.WM == null) return;

        int currentSlot = this.player.inventory.currentItem;
        ItemStack currentStack = this.player.inventory.getStackInSlot(currentSlot);

        boolean hasGun = (currentStack != null && currentStack.getItem() instanceof i);
        deci.aE.a.aa msg = new deci.aE.a.aa(hasGun ? currentStack : null, this.player.getEntityId(), (byte) currentSlot, hasGun);

        try {
            Set<EntityPlayer> trackingPlayers = this.WM.getTrackingPlayers(this.player);
            for (EntityPlayer target : trackingPlayers) {
                if (target instanceof EntityPlayerMP) {
                    Vm.sendTo(msg, (EntityPlayerMP) target);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


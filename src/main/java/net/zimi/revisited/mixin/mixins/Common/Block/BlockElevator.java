package net.zimi.revisited.mixin.mixins.Common.Block;

import cpw.mods.fml.common.network.NetworkRegistry;
import deci.aF.a;
import deci.ac.c;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = c.class, remap = false)
public abstract class BlockElevator extends TileEntity {
    @Shadow
    public abstract c du();

    /**
     * @author zim
     * @reason fix MISSING (worldObj.isRemote ? client : server)
     */
    @Overwrite
    public void dt() {
        if (this.worldObj.isRemote) return;

        c theElevator = this.du();
        if (theElevator != null) {
            NetworkRegistry.TargetPoint target = new NetworkRegistry.TargetPoint(
                    this.worldObj.provider.dimensionId,
                    theElevator.xCoord + 0.5D,
                    theElevator.yCoord + 0.5D,
                    theElevator.zCoord + 0.5D,
                    20.0D
            );

            a.a.a.gB().sendToAllAround(new deci.aE.a.X("deci:block.elevator.stop", theElevator.xCoord + 0.5F, theElevator.yCoord + 0.5F, theElevator.zCoord + 0.5F), target);

            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 2, this.yCoord + 3, this.zCoord + 2);

            @SuppressWarnings("unchecked")
            List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);

            for (EntityPlayer entity : players) {
                double offsetX = entity.posX - this.xCoord;
                double offsetY = entity.posY - this.yCoord;
                double offsetZ = entity.posZ - this.zCoord;
                this.setPositionUpdates(theElevator, entity, offsetX, offsetY, offsetZ);
            }
        }
    }

    @Unique
    private void setPositionUpdates(c theElevator, EntityPlayer entity, double x, double y, double z) {
        entity.setPositionAndUpdate(theElevator.xCoord + x, theElevator.yCoord + y + 0.2D, theElevator.zCoord + z);
        entity.fallDistance = 0.0F;
    }
}
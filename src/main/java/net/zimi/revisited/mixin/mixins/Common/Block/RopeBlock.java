package net.zimi.revisited.mixin.mixins.Common.Block;

import deci.U.h;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({h.class})
public abstract class RopeBlock extends Block {
    protected RopeBlock(Material p_i45394_1_) {
        super(p_i45394_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isSneaking()) {
                player.motionY = 0.2D;
                player.fallDistance = 0.0F;
            } else if (player.motionY < 0.0D) {
                player.motionY *= 0.75D;
                player.fallDistance = 0.0F;
            } else {
                player.motionY = 0.0D;
            }

            if (Math.abs(player.motionY) > 0.01D && Math.random() < 0.01D) {
                world.playSoundAtEntity(player, "deci:block.rope.climb", 1.0F, 1.0F);
            }
        }
    }
}
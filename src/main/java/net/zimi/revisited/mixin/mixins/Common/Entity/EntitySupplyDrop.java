package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.ac.a;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin({a.class})
public abstract class EntitySupplyDrop extends EntityFallingBlock {
    public EntitySupplyDrop(World p_i1706_1_) {
        super(p_i1706_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onUpdate() {
        super.onUpdate();
        if (this.motionY < -0.04F) {
            this.motionY = -0.04F;
        }

        this.field_145812_b = 4;
        this.renderDistanceWeight = 12.0F;

        Random rand = new Random();

        if(!this.worldObj.isRemote && rand.nextInt(7) == 0) {
            deci.aF.a.a.a.gB().sendToAll(new deci.aE.a.W("SignalSmoke_GREEN", this.posX, this.posY + 1.75D, this.posZ));
        }
    }
}

package net.zimi.revisited.mixin.mixins.Common.Entity;

import deci.aE.a;
import deci.ag.k;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({k.class})
public abstract class EntityMech extends EntityMob {
    public EntityMech(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void func_145780_a(int var1, int var2, int var3, Block var4) {
        if (this.worldObj.isRemote) deci.aF.a.a.a.gB().sendToServer(new a.ag(this.posX, this.posY, this.posZ, 1.8F));

        deci.aF.a.a.a.gB().sendToAll(new a.ag(this.posX, this.posY, this.posZ, 1.8F));
        playSound("deci:mob.mech.step", 2.0F, 1.0F);
    }
}

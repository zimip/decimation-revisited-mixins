package net.zimi.revisited.Common.Entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class AITargetSelector implements IEntitySelector {
    private final deci.ag.a shooter;

    public AITargetSelector(deci.ag.a shooter) {
        this.shooter = shooter;
    }

    @Override
    public boolean isEntityApplicable(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return false;
        EntityLivingBase target = (EntityLivingBase) entity;

        if (!shooter.canEntityBeSeen(target) || target == shooter) return false;

        return shooter.c(target);
    }
}
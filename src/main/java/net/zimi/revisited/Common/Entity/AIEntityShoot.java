package net.zimi.revisited.Common.Entity;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLivingBase;

public class AIEntityShoot extends EntityAIBase {
    private final deci.ag.a bandit;
    private EntityLivingBase target;
    private int pathingTimer;

    public AIEntityShoot(deci.ag.a bandit) {
        this.bandit = bandit;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase t = this.bandit.getAttackTarget();
        if (t == null || !t.isEntityAlive()) return false;
        this.target = t;
        return true;
    }

    @Override
    public void startExecuting() {
        this.pathingTimer = 0;
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.bandit.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        this.bandit.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);

        double distSq = this.bandit.getDistanceSq(this.target.posX, this.target.boundingBox.minY, this.target.posZ);
        boolean canSee = this.bandit.getEntitySenses().canSee(this.target);

        if (--this.pathingTimer <= 0) {
            if (!canSee || distSq > 256.0D) {
                this.bandit.getNavigator().tryMoveToEntityLiving(this.target, 1.0D);
            } else {
                this.bandit.getNavigator().clearPathEntity();
            }
            this.pathingTimer = 10;
        }

        if (canSee && distSq <= 1024.0D) {
            this.bandit.e(this.target);
        }
    }
}
package net.zimi.revisited.Common.Entity;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.Vec3;
import deci.ag.d;
import java.util.WeakHashMap;

public class AIInvestigateSound extends EntityAIBase {

    public static final WeakHashMap<d, Vec3> SOUND_TARGETS = new WeakHashMap<>();

    private final d zombie;
    private double targetX, targetY, targetZ;
    private boolean hasTarget = false;
    private int timer = 0;

    public AIInvestigateSound(d zombie) {
        this.zombie = zombie;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (SOUND_TARGETS.containsKey(this.zombie)) {
            Vec3 target = SOUND_TARGETS.remove(this.zombie);
            if (target != null) {
                this.targetX = target.xCoord;
                this.targetY = target.yCoord;
                this.targetZ = target.zCoord;
                this.hasTarget = true;
                this.timer = 200;
            }
        }

        return this.hasTarget && this.zombie.getAttackTarget() == null;
    }

    @Override
    public void startExecuting() {
        this.zombie.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, 1.2D);
    }

    @Override
    public boolean continueExecuting() {
        return this.hasTarget && this.timer > 0 && this.zombie.getAttackTarget() == null && !this.zombie.getNavigator().noPath();
    }

    @Override
    public void updateTask() {
        this.timer--;
        if (this.timer <= 0) {
            this.hasTarget = false;
        }
    }

    @Override
    public void resetTask() {
        this.hasTarget = false;
        this.zombie.getNavigator().clearPathEntity();
    }
}
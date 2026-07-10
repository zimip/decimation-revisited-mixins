//package net.zimi.revisited.Common.Entity;
//
//import net.minecraft.entity.Entity;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.MovingObjectPosition;
//import net.minecraft.util.Vec3;
//import net.minecraft.world.World;
//import net.zimi.revisited.Common.Handler.NukeTransitManager;
//
//public class EntityNuke extends Entity {
//
//    private double targetX, targetY, targetZ;
//    private double launchY;
//    private boolean isReentry = false;
//    private double gravity = 0.02D;
//
//    public EntityNuke(World world) {
//        super(world);
//        this.setSize(1.0F, 1.0F);
//    }
//
//    public EntityNuke(World world, double startX, double startY, double startZ, double targetX, double targetY, double targetZ, boolean isReentry) {
//        super(world);
//        this.setSize(1.0F, 1.0F);
//        this.setPosition(startX, startY, startZ);
//
//        this.launchY = startY;
//        this.targetX = targetX;
//        this.targetY = targetY;
//        this.targetZ = targetZ;
//        this.isReentry = isReentry;
//
//        if (!this.isReentry) {
//            this.motionX = 0.0D;
//            this.motionY = 0.05D;
//            this.motionZ = 0.0D;
//            this.rotationPitch = -90.0F;
//            this.prevRotationPitch = -90.0F;
//        } else {
//            this.motionX = 0.0D;
//            this.motionY = -0.5D;
//            this.motionZ = 0.0D;
//            this.rotationPitch = 90.0F;
//            this.prevRotationPitch = 90.0F;
//        }
//
//        this.rotationYaw = 0.0F;
//        this.prevRotationYaw = 0.0F;
//
//        if (!world.isRemote) {
//            System.out.println("[NUKE SYSTEM] EntityNuke spawned at X: " + startX + " Y: " + startY + " Z: " + startZ + " | Reentry Mode: " + isReentry);
//        }
//    }
//
//    @Override
//    protected void entityInit() {
//    }
//
//    @Override
//    public void onUpdate() {
//        super.onUpdate();
//
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//        this.prevRotationYaw = this.rotationYaw;
//        this.prevRotationPitch = this.rotationPitch;
//
//        Vec3 currentPos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
//        Vec3 nextPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
//        MovingObjectPosition hit = this.worldObj.rayTraceBlocks(currentPos, nextPos);
//
//        if (hit != null) {
//            this.setPosition(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
//            this.explode();
//            return;
//        }
//
//        if (!this.isReentry) {
//            if (this.motionY < 0.6D) {
//                this.motionY += 0.005D;
//            }
//
//            if (this.posY >= 250.0D && !this.worldObj.isRemote) {
//                double dx = this.targetX - this.posX;
//                double dz = this.targetZ - this.posZ;
//                double distance = Math.sqrt(dx * dx + dz * dz);
//
//                int flightTicks = (int) (distance / 0.7D);
//                flightTicks = Math.max(100, flightTicks);
//
//                System.out.println("[NUKE SYSTEM] Nuke reached orbit (Y=250). Entering transit phase. ETA: " + flightTicks + " ticks.");
//                NukeTransitManager.flyingNukes.add(new NukeTransitManager.TransitData(this.worldObj, this.targetX, this.targetY, this.targetZ, flightTicks));
//
//                this.setDead();
//                return;
//            }
//        } else {
//            this.motionY -= this.gravity;
//            this.rotationPitch = 90.0F;
//        }
//
//        this.posX += this.motionX;
//        this.posY += this.motionY;
//        this.posZ += this.motionZ;
//        this.setPosition(this.posX, this.posY, this.posZ);
//
//        if (this.worldObj.isRemote) {
//            for (int i = 0; i < 3; i++) {
//                double smokeYDir = this.isReentry ? 0.2D : -0.2D;
//                this.worldObj.spawnParticle("flame", this.posX, this.posY + (this.isReentry ? 1.0D : -1.0D), this.posZ, (Math.random() - 0.5) * 0.1, smokeYDir, (Math.random() - 0.5) * 0.1);
//                this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + (this.isReentry ? 1.0D : -1.0D), this.posZ, (Math.random() - 0.5) * 0.2, smokeYDir / 2, (Math.random() - 0.5) * 0.2);
//            }
//        }
//    }
//
//    private void explode() {
//        if (!this.worldObj.isRemote) {
//            System.out.println("[NUKE SYSTEM] Detonation confirmed at X: " + this.posX + " Y: " + this.posY + " Z: " + this.posZ);
//
//            deci.aF.a.a.a.gB().sendToAll(new deci.aE.a.W("ExplosionSmoke", this.posX, this.posY, this.posZ));
//            deci.aF.a.a.a.gB().sendToAll(new deci.aE.a.W("ExplosionFlash", this.posX, this.posY, this.posZ));
//
//            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 50.0F, false);
//            this.setDead();
//            this.spawnInWorld((int) this.targetX, (int) this.targetY, (int) this.targetZ);
//        }
//    }
//
//    private void spawnInWorld(int x, int y, int z) {
//        deci.ag.b demon = new deci.ag.b(this.worldObj, x, y + 40, z);
//        this.worldObj.spawnEntityInWorld(demon);
//    }
//
//    @Override
//    protected void writeEntityToNBT(NBTTagCompound nbt) {
//        nbt.setDouble("TargetX", this.targetX);
//        nbt.setDouble("TargetY", this.targetY);
//        nbt.setDouble("TargetZ", this.targetZ);
//        nbt.setDouble("LaunchY", this.launchY);
//        nbt.setBoolean("IsReentry", this.isReentry);
//    }
//
//    @Override
//    protected void readEntityFromNBT(NBTTagCompound nbt) {
//        this.targetX = nbt.getDouble("TargetX");
//        this.targetY = nbt.getDouble("TargetY");
//        this.targetZ = nbt.getDouble("TargetZ");
//        this.launchY = nbt.getDouble("LaunchY");
//        this.isReentry = nbt.getBoolean("IsReentry");
//    }
//}
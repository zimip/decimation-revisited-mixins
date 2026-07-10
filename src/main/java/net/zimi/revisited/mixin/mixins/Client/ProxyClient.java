package net.zimi.revisited.mixin.mixins.Client;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.zimi.revisited.Client.Handler.ShaderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = deci.a.c.class, remap = false)
public abstract class ProxyClient implements deci.a.d{
    @Inject(method = "preInit", at = @At("TAIL"))
    public void preInit(FMLPreInitializationEvent par1, CallbackInfo ci) {
        ShaderHelper.initShaders();
    }

    @Inject(method = "postInit", at =@At("HEAD"))
    public void postInit(CallbackInfo ci) {
    }

    @Mixin(value = deci.a.c.a.class, remap = false)
    public static class RaytraceDoDo {
        @Shadow
        public static final Minecraft mc = Minecraft.getMinecraft();

        @SideOnly(Side.CLIENT)
        @Overwrite
        public static boolean a(EntityPlayer param1EntityPlayer, TileEntity param1TileEntity) {
            AxisAlignedBB bb = param1TileEntity.getRenderBoundingBox();
            World world = param1EntityPlayer.worldObj;

            double pX = param1EntityPlayer.posX;
            double pY = param1EntityPlayer.posY + param1EntityPlayer.getEyeHeight();
            double pZ = param1EntityPlayer.posZ;

            return isClear(world, pX, pY, pZ, bb.maxX, bb.maxY, bb.maxZ) ||
                    isClear(world, pX, pY, pZ, bb.maxX, bb.maxY, bb.minZ) ||
                    isClear(world, pX, pY, pZ, bb.maxX, bb.minY, bb.maxZ) ||
                    isClear(world, pX, pY, pZ, bb.minX, bb.maxY, bb.maxZ) ||
                    isClear(world, pX, pY, pZ, bb.minX, bb.minY, bb.minZ) ||
                    isClear(world, pX, pY, pZ, bb.minX, bb.minY, bb.maxZ) ||
                    isClear(world, pX, pY, pZ, bb.minX, bb.maxY, bb.minZ) ||
                    isClear(world, pX, pY, pZ, bb.maxX, bb.minY, bb.minZ);
        }


        @SideOnly(Side.CLIENT)
        @Overwrite
        public static boolean a(EntityPlayer param1EntityPlayer, Entity param1Entity) {

            if (mc.theWorld == null || param1Entity instanceof deci.e.a) return true;

            World world = param1EntityPlayer.worldObj;

            double pX = param1EntityPlayer.posX;
            double pY = param1EntityPlayer.posY + param1EntityPlayer.getEyeHeight();
            double pZ = param1EntityPlayer.posZ;

            double eX = param1Entity.posX;
            double eY = param1Entity.posY + param1Entity.getEyeHeight();
            double eZ = param1Entity.posZ;
            double eYBase = param1Entity.posY;

            return isClear(world, pX, pY, pZ, eX, eY, eZ) ||
                    isClear(world, pX, pY, pZ, eX + 0.5D, eY, eZ + 0.5D) ||
                    isClear(world, pX, pY, pZ, eX - 0.5D, eY, eZ - 0.5D) ||
                    isClear(world, pX, pY, pZ, eX - 0.5D, eY, eZ + 0.5D) ||
                    isClear(world, pX, pY, pZ, eX + 0.5D, eY, eZ - 0.5D) ||
                    isClear(world, pX, pY, pZ, eX, eYBase, eZ);
        }

        @Unique
        @SideOnly(Side.CLIENT)
        private static boolean isClear(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
            return rayTrace(world, x1, y1, z1, x2, y2, z2, false, false, false) == null;
        }


        @SideOnly(Side.CLIENT)
        @Overwrite
        public static MovingObjectPosition a(World param1World, Vec3 param1Vec31, Vec3 param1Vec32, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3) {
            return rayTrace(param1World, param1Vec31.xCoord, param1Vec31.yCoord, param1Vec31.zCoord, param1Vec32.xCoord, param1Vec32.yCoord, param1Vec32.zCoord, param1Boolean1, param1Boolean2, param1Boolean3);
        }

        @SideOnly(Side.CLIENT)
        @Unique
        private static MovingObjectPosition rayTrace(World param1World, double startX, double startY, double startZ, double endX, double endY, double endZ, boolean stopOnLiquid, boolean ignoreBlockWithoutBB, boolean returnLastUncollidable) {
            if (!Double.isNaN(startX) && !Double.isNaN(startY) && !Double.isNaN(startZ) && !Double.isNaN(endX) && !Double.isNaN(endY) && !Double.isNaN(endZ)) {

                int endXInt = MathHelper.floor_double(endX);
                int endYInt = MathHelper.floor_double(endY);
                int endZInt = MathHelper.floor_double(endZ);
                int startXInt = MathHelper.floor_double(startX);
                int startYInt = MathHelper.floor_double(startY);
                int startZInt = MathHelper.floor_double(startZ);

                Block block = param1World.getBlock(startXInt, startYInt, startZInt);
                int meta = param1World.getBlockMetadata(startXInt, startYInt, startZInt);

                if (!block.renderAsNormalBlock()) return null;

                if ((!ignoreBlockWithoutBB || block.getCollisionBoundingBoxFromPool(param1World, startXInt, startYInt, startZInt) != null) && block.canCollideCheck(meta, stopOnLiquid)) {
                    MovingObjectPosition mop = block.collisionRayTrace(param1World, startXInt, startYInt, startZInt, Vec3.createVectorHelper(startX, startY, startZ), Vec3.createVectorHelper(endX, endY, endZ));
                    if (mop != null) return mop;
                }

                MovingObjectPosition lastUncollidable = null;
                int steps = 200;

                while (steps-- >= 0) {
                    if (Double.isNaN(startX) || Double.isNaN(startY) || Double.isNaN(startZ)) return null;

                    if (startXInt == endXInt && startYInt == endYInt && startZInt == endZInt) {
                        return returnLastUncollidable ? lastUncollidable : null;
                    }

                    boolean stepX = true, stepY = true, stepZ = true;
                    double nextX = 999.0D, nextY = 999.0D, nextZ = 999.0D;

                    if (endXInt > startXInt) { nextX = startXInt + 1.0D; }
                    else if (endXInt < startXInt) { nextX = startXInt + 0.0D; }
                    else { stepX = false; }

                    if (endYInt > startYInt) { nextY = startYInt + 1.0D; }
                    else if (endYInt < startYInt) { nextY = startYInt + 0.0D; }
                    else { stepY = false; }

                    if (endZInt > startZInt) { nextZ = startZInt + 1.0D; }
                    else if (endZInt < startZInt) { nextZ = startZInt + 0.0D; }
                    else { stepZ = false; }

                    double tX = 999.0D, tY = 999.0D, tZ = 999.0D;
                    double diffX = endX - startX;
                    double diffY = endY - startY;
                    double diffZ = endZ - startZ;

                    if (stepX) tX = (nextX - startX) / diffX;
                    if (stepY) tY = (nextY - startY) / diffY;
                    if (stepZ) tZ = (nextZ - startZ) / diffZ;

                    byte side;
                    if (tX < tY && tX < tZ) {
                        side = (byte)(endXInt > startXInt ? 4 : 5);
                        startX = nextX;
                        startY += diffY * tX;
                        startZ += diffZ * tX;
                    } else if (tY < tZ) {
                        side = (byte)(endYInt > startYInt ? 0 : 1);
                        startX += diffX * tY;
                        startY = nextY;
                        startZ += diffZ * tY;
                    } else {
                        side = (byte)(endZInt > startZInt ? 2 : 3);
                        startX += diffX * tZ;
                        startY += diffY * tZ;
                        startZ = nextZ;
                    }

                    startXInt = MathHelper.floor_double(startX);
                    if (side == 5) startXInt--;

                    startYInt = MathHelper.floor_double(startY);
                    if (side == 1) startYInt--;

                    startZInt = MathHelper.floor_double(startZ);
                    if (side == 3) startZInt--;

                    Block currentBlock = param1World.getBlock(startXInt, startYInt, startZInt);
                    int currentMeta = param1World.getBlockMetadata(startXInt, startYInt, startZInt);

                    if (!currentBlock.renderAsNormalBlock()) return null;

                    if (!ignoreBlockWithoutBB || currentBlock.getCollisionBoundingBoxFromPool(param1World, startXInt, startYInt, startZInt) != null) {
                        if (currentBlock.canCollideCheck(currentMeta, stopOnLiquid)) {
                            MovingObjectPosition mop = currentBlock.collisionRayTrace(param1World, startXInt, startYInt, startZInt, Vec3.createVectorHelper(startX, startY, startZ), Vec3.createVectorHelper(endX, endY, endZ));
                            if (mop != null) return mop;
                            continue;
                        }
                        lastUncollidable = new MovingObjectPosition(startXInt, startYInt, startZInt, side, Vec3.createVectorHelper(startX, startY, startZ), false);
                    }
                }
                return returnLastUncollidable ? lastUncollidable : null;
            }
            return null;
        }
    }
}

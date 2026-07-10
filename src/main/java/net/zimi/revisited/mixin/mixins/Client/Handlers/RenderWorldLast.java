package net.zimi.revisited.mixin.mixins.Client.Handlers;

import deci.M.d;
import deci.aD.k;
import deci.ay.i;
import deci.b.c;
import deci.j.a;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.zimi.revisited.Addon;
import net.zimi.revisited.Client.Handler.LaserDotRenderer;
import net.zimi.revisited.Client.Handler.ShaderHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = {d.class}, remap = false)
public abstract class RenderWorldLast {

    @Shadow
    protected abstract MovingObjectPosition a(EntityPlayer entityPlayer, double v, float v1);

    @Unique
    private int depthTexId = -1;

    @Unique
    private float ticker = 5;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void c(RenderWorldLastEvent var1) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        EntityLivingBase viewEntity = mc.renderViewEntity;

        if (player == null || mc.theWorld == null || viewEntity == null) return;

        ItemStack var2 = player.getEquipmentInSlot(0);
        float pTicks = var1.partialTicks;
        boolean isFirstPerson = (mc.gameSettings.thirdPersonView == 0);

        if (isFirstPerson && !LaserDotRenderer.dots.isEmpty()) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            for (LaserDotRenderer.DotData dot : LaserDotRenderer.dots) {
                a.a(c.aq, dot.x, dot.y, dot.z, dot.pt, dot.width, dot.height, 1.0F);
            }
        }

        if (var2 == null || !(var2.getItem() instanceof i)) return;

        i currentItem = (i) var2.getItem();
        Object itemType = currentItem.J(var2);

        if (itemType == k.awg) {
            double maxDistance = 256.0D;
            MovingObjectPosition mop = this.a(player, maxDistance, pTicks);

            Vec3 posVec = viewEntity.getPosition(pTicks);
            Vec3 lookVec = viewEntity.getLook(pTicks);

            double lookX = lookVec.xCoord * maxDistance;
            double lookY = lookVec.yCoord * maxDistance;
            double lookZ = lookVec.zCoord * maxDistance;

            Vec3 endVec = posVec.addVector(lookX, lookY, lookZ);

            double distToBlock = maxDistance;
            if (mop != null) {
                distToBlock = mop.hitVec.distanceTo(posVec);
            }

            Entity hitEntity = null;
            Vec3 finalVec = null;
            double closestEntityDist = distToBlock;

            AxisAlignedBB searchBox = viewEntity.boundingBox.addCoord(lookX, lookY, lookZ).expand(1.0D, 1.0D, 1.0D);

            @SuppressWarnings("unchecked")
            List<Entity> entities = viewEntity.worldObj.getEntitiesWithinAABBExcludingEntity(viewEntity, searchBox);

            for (Entity entity : entities) {
                if (!entity.canBeCollidedWith()) continue;

                float border = entity.getCollisionBorderSize();
                AxisAlignedBB aabb = entity.boundingBox.expand(border, border, border);
                MovingObjectPosition intercept = aabb.calculateIntercept(posVec, endVec);

                if (aabb.isVecInside(posVec)) {
                    if (closestEntityDist >= 0.0D) {
                        hitEntity = entity;
                        finalVec = intercept == null ? posVec : intercept.hitVec;
                        closestEntityDist = 0.0D;
                    }
                } else if (intercept != null) {
                    double dist = posVec.distanceTo(intercept.hitVec);
                    if (dist < closestEntityDist || closestEntityDist == 0.0D) {
                        if (entity == viewEntity.ridingEntity && !entity.canRiderInteract()) {
                            if (closestEntityDist == 0.0D) {
                                hitEntity = entity;
                                finalVec = intercept.hitVec;
                            }
                        } else {
                            hitEntity = entity;
                            finalVec = intercept.hitVec;
                            closestEntityDist = dist;
                        }
                    }
                }
            }

            if (hitEntity != null && (closestEntityDist < distToBlock || mop == null)) {
                mop = new MovingObjectPosition(hitEntity, finalVec);
            }

            if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
                Vec3 hitVec = mop.hitVec;
                if (hitVec != null) {
                    double distanceTo = hitVec.distanceTo(Vec3.createVectorHelper(viewEntity.posX, viewEntity.posY, viewEntity.posZ));
                    a.jl = isFirstPerson;

                    GL11.glPushMatrix();
                    GL11.glEnable(32826);
                    GL11.glDisable(3008);

                    float size = 2.0F + (float) (distanceTo / 7.0D);
                    a.a(c.aq, hitVec.xCoord, hitVec.yCoord, hitVec.zCoord, pTicks, size, size, 1.0F);

                    GL11.glDisable(32826);
                    GL11.glEnable(3008);
                    GL11.glEnable(2896);

                    if (mop.entityHit instanceof EntityPlayer) {
                        if (ticker > 0) {
                            ticker--;
                        } else {
                            ticker = 5;
                            Addon.Network.PACKET.sendToServer(new Addon.Network.Message_LaserDotC2S(
                                    mop.entityHit.getEntityId(), hitVec.xCoord, hitVec.yCoord, hitVec.zCoord, pTicks, size, size
                            ));
                        }
                    }

                    GL11.glPopMatrix();
                    a.jl = true;
                }
            }
        }
        else if (itemType == k.awh && isFirstPerson) {
            if (depthTexId == -1) depthTexId = GL11.glGenTextures();
            ShaderHelper.renderShader(ShaderHelper.spotlightProgram, depthTexId, pTicks);
        }
    }
}
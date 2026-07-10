//package net.zimi.revisited.Client.Render;
//
//import net.minecraft.client.renderer.entity.Render;
//import net.minecraft.entity.Entity;
//import net.minecraft.util.ResourceLocation;
//import org.lwjgl.opengl.GL11;
//import net.zimi.revisited.Common.Entity.EntityNuke;
//
//public class RenderNuke extends Render {
//    private final ResourceLocation texture = new ResourceLocation("deci", "textures/model/entities/projectile/rocket.png");
//
//    private deci.n.f model;
//
//    @Override
//    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
//
//        if (entity instanceof EntityNuke) {
//            GL11.glPushMatrix();
//
//            GL11.glTranslatef((float) x, (float) y + 0.2F, (float) z);
//
//            float interpolatedYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
//            float interpolatedPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
//
//            GL11.glRotatef(interpolatedYaw + 90.0F, 0.0F, 1.0F, 0.0F);
//            GL11.glRotatef(interpolatedPitch, 0.0F, 0.0F, 1.0F);
//
//            float f4 = 2F;
//            GL11.glScalef(f4, f4, f4);
//
//            this.bindTexture(this.texture);
//
//            GL11.glScalef(2, 2, 2);
//
//            if (this.model == null) {
//                this.model = deci.n.g.a(new ResourceLocation("deci:models/entities/projectile/rocket.bmodel"));
//            }
//
//            if (this.model != null) {
//                this.model.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
//            }
//
//            GL11.glPopMatrix();
//        }
//    }
//
//    @Override
//    protected ResourceLocation getEntityTexture(Entity entity) {
//        return this.texture;
//    }
//}
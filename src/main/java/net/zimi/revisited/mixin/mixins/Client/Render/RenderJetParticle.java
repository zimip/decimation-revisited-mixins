package net.zimi.revisited.mixin.mixins.Client.Render;

import deci.B.m;
import deci.b.g;
import deci.b.i;
import deci.n.f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = m.class, remap = false)
public abstract class RenderJetParticle extends EntityFX {

    @Shadow private float Qu;

    public RenderJetParticle(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Unique private static final ResourceLocation JET_TEXTURE = new ResourceLocation("deci", "textures/model/misc/jet.png");
    @Unique private static final ResourceLocation JET_MODEL_LOC = new ResourceLocation("deci", "models/misc/jet.bmodel");

    @Unique private static f cachedJetModel = null;

    /**
     * @author zim
     * @reason Fix gravissimo CPU/Memory leak. Il modello veniva letto dal disco ad ogni frame. Rimosso anche dead code.
     */
    @Overwrite
    public void renderParticle(Tessellator paramTessellator, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!minecraft.gameSettings.fancyGraphics) return;

        GL11.glPushMatrix();

        g.D();

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);

        minecraft.renderEngine.bindTexture(JET_TEXTURE);

        if (cachedJetModel == null) {
            cachedJetModel = deci.n.g.a(JET_MODEL_LOC);
        }

        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(this.Qu, (float)minecraft.thePlayer.posY - this.worldObj.getHeight() - 30.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

        float f1 = i.bz * 4.0F;
        float f2 = (float)(Math.sin((f1 / 50.0F)) * 30.0D);
        float f3 = (float)(Math.sin((f1 / 25.0F)) / 2.0D);

        GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);

        cachedJetModel.render(minecraft.thePlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, 1.0F);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
}
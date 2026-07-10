package net.zimi.revisited.mixin.mixins.Client.Render;

import deci.B.r;
import deci.ak.q;
import deci.j.a;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;

@Mixin(value = r.class, remap = false)
public abstract class RenderSignalParticle extends EntityFX {

    @Final
    @Shadow private q Qv;
    @Shadow private int index;
    @Shadow private float aa;
    @Shadow private double Qm;
    @Shadow private double Qn;

    @Unique private ResourceLocation cachedTexture = null;

    public RenderSignalParticle(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    /**
     * @author zim
     * @reason Fix texture cache, fix pushAttrib pesantissimo e FIX bug "oscuramento" (Lighting e Lightmap)
     */
    @Overwrite
    public void renderParticle(Tessellator paramTessellator, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {
        if (this.aa <= 0.0F) return;

        if (this.cachedTexture == null) {
            this.cachedTexture = new ResourceLocation("deci", "textures/particle/signalsmoke/signalsmoke" + this.Qv.color + this.index + ".png");
        }

        GL11.glPushMatrix();
        GL11.glDepthMask(false);

        GL11.glDisable(GL11.GL_LIGHTING);
        RenderHelper.disableStandardItemLighting();

        float lastBrightX = OpenGlHelper.lastBrightnessX;
        float lastBrightY = OpenGlHelper.lastBrightnessY;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        a.b(this.cachedTexture, this.posX, this.posY + 0.2D, this.posZ, 1.0F, 150.0F, 150.0F, this.aa);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightX, lastBrightY);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    /**
     * @author zim
     * @reason Rimozione variabili non utilizzate, pulizia e ottimizzazione Early Exit
     */
    @Overwrite
    public void onUpdate() {
        if (this.particleAge < 5) {
            this.aa += 0.5F;
        } else {
            this.aa -= 0.01F;
        }

        if (this.worldObj != null) {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.particleAge++ >= this.particleMaxAge || this.aa <= 0.0F) {
                this.setDead();
                return;
            }

            this.motionX *= 0.999D;
            this.motionY *= 0.999D;
            this.motionZ *= 0.999D;

            this.Qm += 0.02D;
            this.motionX = this.Qm;

            this.Qn += 0.02D;
            this.motionZ = this.Qn;

            this.motionY = 0.1D;

            this.moveEntity(this.motionX / 40.0D, this.motionY, this.motionZ / 40.0D);

            if (this.onGround) {
                this.motionX *= 0.7D;
                this.motionZ *= 0.7D;
            }
        }
    }
}
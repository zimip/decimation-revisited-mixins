package net.zimi.revisited.mixin.mixins.Client.Model;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.zimi.revisited.Client.Handler.IOptimizedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "deci.n.c", remap = false)
public abstract class BeardieTexturedQuad extends TexturedQuad implements IOptimizedQuad {

    @Shadow private PositionTextureVertex[] kb;
    @Shadow private boolean kc;
    @Shadow public boolean jE;

    public BeardieTexturedQuad(PositionTextureVertex[] p_i1152_1_) {
        super(p_i1152_1_);
    }

    @Shadow private int p(int paramInt1, int paramInt2) { return 0; }

    @Unique private float optiNx;
    @Unique private float optiNy;
    @Unique private float optiNz;

    @Override
    public PositionTextureVertex[] getVertices() { return this.kb; }

    @Override
    public boolean isInvertNormal() { return this.kc; }

    @Unique
    private void calcNormalsZeroAlloc() {
        float x0 = (float)kb[0].vector3D.xCoord; float y0 = (float)kb[0].vector3D.yCoord; float z0 = (float)kb[0].vector3D.zCoord;
        float x1 = (float)kb[1].vector3D.xCoord; float y1 = (float)kb[1].vector3D.yCoord; float z1 = (float)kb[1].vector3D.zCoord;
        float x2 = (float)kb[2].vector3D.xCoord; float y2 = (float)kb[2].vector3D.yCoord; float z2 = (float)kb[2].vector3D.zCoord;

        float ax = x1 - x0; float ay = y1 - y0; float az = z1 - z0;
        float bx = x1 - x2; float by = y1 - y2; float bz = z1 - z2;

        float cx = ay * bz - az * by;
        float cy = az * bx - ax * bz;
        float cz = ax * by - ay * bx;

        float len = (float)Math.sqrt(cx * cx + cy * cy + cz * cz);
        if (len < 1.0E-4F) len = 1.0F;

        if (this.kc) {
            this.optiNx = -(cx / len); this.optiNy = -(cy / len); this.optiNz = -(cz / len);
        } else {
            this.optiNx = (cx / len); this.optiNy = (cy / len); this.optiNz = (cz / len);
        }
    }

    @Inject(method = "<init>([Lnet/minecraft/client/model/PositionTextureVertex;)V", at = @At("RETURN"))
    private void onInitPrivate(PositionTextureVertex[] paramArray, CallbackInfo ci) {
        this.calcNormalsZeroAlloc();
    }

    @Inject(method = "<init>([Lnet/minecraft/client/model/PositionTextureVertex;FFFFFF)V", at = @At("RETURN"))
    private void onInitPublic(PositionTextureVertex[] paramArray, float f1, float f2, float f3, float f4, float f5, float f6, CallbackInfo ci) {
        this.calcNormalsZeroAlloc();
    }

    @Inject(method = "flipFace", at = @At("RETURN"), remap = true)
    private void onFlipFace(CallbackInfo ci) {
        this.calcNormalsZeroAlloc();
    }

    @Overwrite
    public void draw(Tessellator tessellator, float scale) {
        if (this.jE) tessellator.setBrightness(224);
        tessellator.setNormal(this.optiNx, this.optiNy, this.optiNz);

        PositionTextureVertex[] v = this.kb;
        tessellator.addVertexWithUV((float)v[0].vector3D.xCoord * scale, (float)v[0].vector3D.yCoord * scale, (float)v[0].vector3D.zCoord * scale, v[0].texturePositionX, v[0].texturePositionY);
        tessellator.addVertexWithUV((float)v[1].vector3D.xCoord * scale, (float)v[1].vector3D.yCoord * scale, (float)v[1].vector3D.zCoord * scale, v[1].texturePositionX, v[1].texturePositionY);
        tessellator.addVertexWithUV((float)v[2].vector3D.xCoord * scale, (float)v[2].vector3D.yCoord * scale, (float)v[2].vector3D.zCoord * scale, v[2].texturePositionX, v[2].texturePositionY);
        tessellator.addVertexWithUV((float)v[3].vector3D.xCoord * scale, (float)v[3].vector3D.yCoord * scale, (float)v[3].vector3D.zCoord * scale, v[3].texturePositionX, v[3].texturePositionY);
    }

    @Overwrite
    public void b(Tessellator paramTessellator, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
        if (this.jE) paramTessellator.setBrightness(224);
        paramTessellator.setNormal(this.optiNx, this.optiNy, this.optiNz);

        PositionTextureVertex p1 = this.kb[1];
        paramTessellator.addVertexWithUV((float)p1.vector3D.xCoord * paramFloat1, (float)p1.vector3D.yCoord * paramFloat1, (float)p1.vector3D.zCoord * paramFloat1, paramFloat2, paramFloat3);
        PositionTextureVertex p2 = this.kb[2];
        paramTessellator.addVertexWithUV((float)p2.vector3D.xCoord * paramFloat1, (float)p2.vector3D.yCoord * paramFloat1, (float)p2.vector3D.zCoord * paramFloat1, paramFloat2, paramFloat5);
        PositionTextureVertex p3 = this.kb[3];
        paramTessellator.addVertexWithUV((float)p3.vector3D.xCoord * paramFloat1, (float)p3.vector3D.yCoord * paramFloat1, (float)p3.vector3D.zCoord * paramFloat1, paramFloat4, paramFloat5);
        PositionTextureVertex p4 = this.kb[0];
        paramTessellator.addVertexWithUV((float)p4.vector3D.xCoord * paramFloat1, (float)p4.vector3D.yCoord * paramFloat1, (float)p4.vector3D.zCoord * paramFloat1, paramFloat4, paramFloat3);
    }

    @Overwrite
    public void a(Tessellator paramTessellator, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, float[][] paramArrayOffloat) {
        if (this.jE) paramTessellator.setBrightness(224);
        paramTessellator.setNormal(this.optiNx, this.optiNy, this.optiNz);

        PositionTextureVertex p1 = this.kb[1];
        paramTessellator.addVertexWithUV((float)p1.vector3D.xCoord * paramFloat1, (float)p1.vector3D.yCoord * paramFloat1, (float)p1.vector3D.zCoord * paramFloat1, paramArrayOffloat[p(paramInt, 1)][0], paramArrayOffloat[p(paramInt, 1)][1]);
        PositionTextureVertex p2 = this.kb[2];
        paramTessellator.addVertexWithUV((float)p2.vector3D.xCoord * paramFloat1, (float)p2.vector3D.yCoord * paramFloat1, (float)p2.vector3D.zCoord * paramFloat1, paramArrayOffloat[p(paramInt, 2)][0], paramArrayOffloat[p(paramInt, 2)][1]);
        PositionTextureVertex p3 = this.kb[3];
        paramTessellator.addVertexWithUV((float)p3.vector3D.xCoord * paramFloat1, (float)p3.vector3D.yCoord * paramFloat1, (float)p3.vector3D.zCoord * paramFloat1, paramArrayOffloat[p(paramInt, 3)][0], paramArrayOffloat[p(paramInt, 3)][1]);
        PositionTextureVertex p4 = this.kb[0];
        paramTessellator.addVertexWithUV((float)p4.vector3D.xCoord * paramFloat1, (float)p4.vector3D.yCoord * paramFloat1, (float)p4.vector3D.zCoord * paramFloat1, paramArrayOffloat[p(paramInt, 0)][0], paramArrayOffloat[p(paramInt, 0)][1]);
    }

    @Overwrite
    public void b(Tessellator paramTessellator, float paramFloat) {
        paramTessellator.setNormal(this.optiNx, this.optiNy, this.optiNz);
        for (PositionTextureVertex positionTextureVertex : this.kb) {
            paramTessellator.addVertexWithUV(
                    (float)positionTextureVertex.vector3D.xCoord * paramFloat,
                    (float)positionTextureVertex.vector3D.yCoord * paramFloat,
                    (float)positionTextureVertex.vector3D.zCoord * paramFloat,
                    positionTextureVertex.texturePositionX,
                    positionTextureVertex.texturePositionY
            );
        }
    }
}
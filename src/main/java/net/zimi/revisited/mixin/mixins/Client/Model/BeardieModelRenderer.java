package net.zimi.revisited.mixin.mixins.Client.Model;

import deci.n.a;
import deci.n.b;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.zimi.revisited.Client.Handler.IOptimizedQuad;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = b.class, remap = false)
public abstract class BeardieModelRenderer extends net.minecraft.client.model.ModelRenderer {

    @Final @Shadow private boolean jG;
    @Shadow private boolean jX;
    @Shadow public float fW, offsetX, offsetY, offsetZ, jO, jN;
    @Shadow private int jY, jZ, jJ, jK;
    @Shadow private List<a> jQ;
    @Shadow public boolean jE;
    @Final @Shadow private Tessellator jF;
    @Final @Shadow private float[][] jI;
    @Shadow public ResourceLocation jP;
    @Final @Shadow private Minecraft mc;
    @Shadow public List<b> childModels;

    @Unique private boolean useVbo = false;
    @Unique private boolean useVao = false;
    @Unique private boolean vboCompiled = false;
    @Unique private int vboId = -1, vaoId = -1, vertexCount = 0;
    @Unique private float compiledScale = 0.0f;

    @Unique private static ResourceLocation lastBoundTexture = null;

    @Unique private static Field jDField;
    @Unique private static boolean reflectionReady = false;

    static {
        try {
            jDField = a.class.getDeclaredField("jD");
            jDField.setAccessible(true);
            reflectionReady = true;
        } catch (Exception e) {}
    }

    public BeardieModelRenderer(ModelBase p_i1172_1_, String p_i1172_2_) { super(p_i1172_1_, p_i1172_2_); }

    @Shadow private void f(float paramFloat) {}

    @Inject(method = "render", at = @At("HEAD"), remap = true)
    private void onRenderStart(float scale, CallbackInfo ci) {
        lastBoundTexture = null;
    }

    @Unique
    private void cacheTex(ResourceLocation texture) {
        if (texture != null && texture != lastBoundTexture) {
            this.mc.renderEngine.bindTexture(texture);
            lastBoundTexture = texture;
        }
    }

    @Unique
    private void compileVBO(float scale) {
        if (!reflectionReady || this.jO != 0.0F || this.jN != 0.0F || (this.jI != null && this.jI.length >= 8)) return;

        this.useVbo = GLContext.getCapabilities().OpenGL15;
        this.useVao = GLContext.getCapabilities().OpenGL30;
        if (!this.useVbo) return;

        try {
            this.vertexCount = 0;
            for (a box : this.jQ) {
                Object[] quads = (Object[]) jDField.get(box);
                for (Object quadObj : quads) {
                    if (quadObj != null) {
                        this.vertexCount += ((IOptimizedQuad) quadObj).getVertices().length;
                    }
                }
            }
            if (this.vertexCount == 0) return;

            FloatBuffer buffer = BufferUtils.createFloatBuffer(this.vertexCount * 8);

            for (a box : this.jQ) {
                Object[] quads = (Object[]) jDField.get(box);
                for (Object quadObj : quads) {
                    if (quadObj == null) continue;

                    IOptimizedQuad optiQuad = (IOptimizedQuad) quadObj;
                    PositionTextureVertex[] v = optiQuad.getVertices();
                    boolean invertNormal = optiQuad.isInvertNormal();

                    float x0 = (float)v[0].vector3D.xCoord; float y0 = (float)v[0].vector3D.yCoord; float z0 = (float)v[0].vector3D.zCoord;
                    float x1 = (float)v[1].vector3D.xCoord; float y1 = (float)v[1].vector3D.yCoord; float z1 = (float)v[1].vector3D.zCoord;
                    float x2 = (float)v[2].vector3D.xCoord; float y2 = (float)v[2].vector3D.yCoord; float z2 = (float)v[2].vector3D.zCoord;
                    float cx = (y1-y0)*(z1-z2) - (z1-z0)*(y1-y2);
                    float cy = (z1-z0)*(x1-x2) - (x1-x0)*(z1-z2);
                    float cz = (x1-x0)*(y1-y2) - (y1-y0)*(x1-x2);
                    float len = (float)Math.sqrt(cx*cx + cy*cy + cz*cz);
                    if (len < 1.0E-4F) len = 1.0F;

                    float nx = invertNormal ? -(cx/len) : (cx/len);
                    float ny = invertNormal ? -(cy/len) : (cy/len);
                    float nz = invertNormal ? -(cz/len) : (cz/len);

                    for (PositionTextureVertex vert : v) {
                        buffer.put((float)vert.vector3D.xCoord * scale).put((float)vert.vector3D.yCoord * scale).put((float)vert.vector3D.zCoord * scale);
                        buffer.put(vert.texturePositionX).put(vert.texturePositionY);
                        buffer.put(nx).put(ny).put(nz);
                    }
                }
            }
            buffer.flip();

            if (this.vboId != -1) GL15.glDeleteBuffers(this.vboId);
            this.vboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

            if (this.useVao) {
                if (this.vaoId != -1) GL30.glDeleteVertexArrays(this.vaoId);
                this.vaoId = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(this.vaoId);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboId);
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY); GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY); GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0); GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 12); GL11.glNormalPointer(GL11.GL_FLOAT, 32, 20);
                GL30.glBindVertexArray(0);
            }
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            this.vboCompiled = true; this.compiledScale = scale;
        } catch (Exception e) { this.vboCompiled = false; }
    }

    @Unique
    private void renderVBO() {
        if (this.useVao) {
            GL30.glBindVertexArray(this.vaoId);
            GL11.glDrawArrays(GL11.GL_QUADS, 0, this.vertexCount);
            GL30.glBindVertexArray(0);
        } else {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboId);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY); GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY); GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0); GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 12); GL11.glNormalPointer(GL11.GL_FLOAT, 32, 20);
            GL11.glDrawArrays(GL11.GL_QUADS, 0, this.vertexCount);
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY); GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY); GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
    }

    @Overwrite
    private void b(float paramFloat, boolean paramBoolean) {
        if (paramBoolean) {
            GL11.glCallList(this.jZ);
        } else {
            this.cacheTex(this.jP);
            if (this.childModels != null) {
                for (b b1 : this.childModels) {
                    b1.jE = this.jE;
                    b1.a(paramFloat, false);
                }
            }
        }
    }

    @Overwrite
    public void a(float paramFloat, boolean paramBoolean) {
        if (this.isHidden || !this.jG) return;

        GL11.glPushMatrix();

        if ((!this.jX || this.compiledScale != paramFloat) && paramBoolean) {
            this.f(paramFloat);
            this.compileVBO(paramFloat);
        }

        GL11.glShadeModel(GL11.GL_FLAT);

        boolean hasRot = (this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F);
        boolean hasTrans = (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F);

        if (hasTrans || hasRot) {
            GL11.glTranslatef(this.rotationPointX * paramFloat, this.rotationPointY * paramFloat, this.rotationPointZ * paramFloat);

            if (hasRot) {
                GL11.glTranslatef(this.offsetX * paramFloat, this.offsetY * paramFloat, this.offsetZ * paramFloat);
                if (this.rotateAngleZ != 0.0F) GL11.glRotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
                if (this.rotateAngleY != 0.0F) GL11.glRotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
                if (this.rotateAngleX != 0.0F) GL11.glRotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(-this.offsetX * paramFloat, -this.offsetY * paramFloat, -this.offsetZ * paramFloat);
            }
        }

        if (this.fW != 1.0F) {
            GL11.glScalef(this.fW, this.fW, this.fW);
        }

        if (paramBoolean) {
            if (this.useVbo && this.vboCompiled) {
                this.renderVBO();
            } else {
                GL11.glCallList(this.jY);
            }
        } else if (this.jO != 0.0F || this.jN != 0.0F) {
            for (a a : this.jQ) { a.jE = this.jE; a.a(this.jF, paramFloat, this.jJ, this.jK, this.jO, this.jN); }
        } else if (this.jI != null && this.jI.length >= 8) {
            for (a a : this.jQ) { a.jE = this.jE; a.a(this.jF, paramFloat, this.jJ, this.jK, this.jI); }
        } else {
            for (a a : this.jQ) { a.jE = this.jE; a.a(this.jF, paramFloat); }
        }

        this.b(paramFloat, paramBoolean);

        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glPopMatrix();
    }
}
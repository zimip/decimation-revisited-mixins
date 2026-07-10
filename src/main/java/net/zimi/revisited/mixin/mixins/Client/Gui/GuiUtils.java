package net.zimi.revisited.mixin.mixins.Client.Gui;

import deci.b.i;
import deci.j.a;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.*;

import java.awt.Color;
import java.util.ArrayList;

@Mixin(value = a.class, remap = false)
public abstract class GuiUtils {

    @Final
    @Shadow private static Tessellator jj;
    @Final
    @Shadow private static Minecraft mc;
    @Shadow private static deci.J.e.a jo;
    @Shadow private static deci.e.c jp;
    @Shadow public static boolean jl;
    @Final
    @Shadow public static ResourceLocation jh;

    @Shadow public static void a(double var0, double var2, ResourceLocation var4, float var5, float var6, float var7, double var8) {}
    @Shadow public static void l(int var0, int var1) {}
    @Shadow public static void b(double var0, double var2, double var4, double var6, String var8, float var9) {}
    @Shadow public static void c(double var0, double var2, ResourceLocation var4, double var5, double var7) {}
    @Shadow public static void drawGradientRect(int var0, int var1, int var2, int var3, int var4, int var5) {}
    @Shadow public static void a(EntityPlayer var0, float var1, AxisAlignedBB var2, Color var3) {}

    // --- CACHE (Memory Leak Fixes) ---
    @Unique private static final ResourceLocation TEX_BLACKBLOCK1 = new ResourceLocation("deci", "textures/gui/blackblock1.png");
    @Unique private static final ResourceLocation TEX_BLACKBLOCK2 = new ResourceLocation("deci", "textures/gui/blackblock2.png");
    @Unique private static final ResourceLocation TEX_SUN = new ResourceLocation("deci", "textures/model/gui/sun.png");
    @Unique private static final ResourceLocation TEX_EARTH = new ResourceLocation("deci", "textures/model/gui/earth.png");
    @Unique private static final ResourceLocation TEX_SUN_PARTICLE = new ResourceLocation("deci", "textures/particle/sun.png");
    @Unique private static final ResourceLocation TEX_SIDESHADOW = new ResourceLocation("deci", "textures/gui/menu/sideshadow.png");
    @Unique private static ModelBase cachedSunEarthModel = null;

    /**
     * @author zim
     * @reason Ottimizzazione colori GC Thrash Fix. Usa bitwise shift invece di creare istanze java.awt.Color.
     */
    @Overwrite
    public static void o(int var0) {
        float var2 = (float)(var0 >> 16 & 255) / 255.0F;
        float var3 = (float)(var0 >> 8 & 255) / 255.0F;
        float var4 = (float)(var0 & 255) / 255.0F;
        GL11.glColor3f(var2, var3, var4);
    }

    /**
     * @author zim
     * @reason Ottimizzazione string -> color. Passa l'intero esadecimale direttamente.
     */
    @Overwrite
    public static void v(String var0) {
        try {
            int color = Integer.parseInt(var0.replace("#", ""), 16);
            o(color);
        } catch (NumberFormatException e) {
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * @author zim
     * @reason Evita allocazione di Color quando è possibile calcolare il colore tramite bitwise shift per il font renderer.
     */
    @Overwrite
    public static void a(String var0, double var1, double var3, double var5, float var7, float var8) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;
        GL11.glPushMatrix();
        GL11.glTranslated(var1, var3, var5);
        GL11.glTranslated(-RenderManager.instance.viewerPosX, -RenderManager.instance.viewerPosY, -RenderManager.instance.viewerPosZ);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int stringWidth = mc.fontRenderer.getStringWidth(var0);
        GL11.glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(player.rotationPitch, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-0.01F, -0.01F, 0.01F);
        if (jl) GL11.glDepthMask(false);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int alpha = (int)(var8 * 255.0F);
        int colorRGB = (alpha << 24) | 16777215;

        mc.fontRenderer.drawString(var0, -(stringWidth / 2), 0, colorRGB);

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    /**
     * @author zim
     * @reason Traduzione hex-to-rgb senza la classe AWT Color.
     */
    @Overwrite
    public static void a(double var0, double var2, double var4, double var6, String var8, float var9) {
        int hex;
        try {
            hex = Integer.decode(var8);
        } catch (NumberFormatException e) {
            hex = 16777215;
        }
        float var11 = (float)(hex >> 16 & 255) / 255.0F;
        float var12 = (float)(hex >> 8 & 255) / 255.0F;
        float var13 = (float)(hex & 255) / 255.0F;

        a(var0, var2, var0 + var4, var2 + var6, var11, var12, var13, var9);
    }

//    @Shadow
//    public static void a(double var0, double var2, double v, double v1, float var11, float var12, float var13, float var9) {
//    }

    /**
     * @author zim
     * @reason Fix GC Memory leak causato da replaceAll ed elaborazione stringhe inefficiente.
     */
    @Overwrite
    public static void a(ArrayList<String> var0, Color var1, float var2, EntityPlayer var3) {
        for (String var5 : var0) {
            int eqIdx = var5.indexOf('=');
            if (eqIdx == -1) continue;

            String var7 = var5.substring(0, eqIdx).trim();
            String var8 = var5.substring(eqIdx + 1).trim();
            String[] var9 = var7.split("/");
            String[] var10 = var8.split("/");

            if (var9.length < 3 || var10.length < 3) continue;

            try {
                int var11 = Integer.parseInt(var9[0]);
                int var12 = Integer.parseInt(var9[1]);
                int var13 = Integer.parseInt(var9[2]);
                int var14 = Integer.parseInt(var10[0]);
                int var15 = Integer.parseInt(var10[1]);
                int var16 = Integer.parseInt(var10[2]);
                AxisAlignedBB var17 = AxisAlignedBB.getBoundingBox(var11, var12, var13, var14, var15, var16);
                a(var3, var2, var17, var1);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    /**
     * @author zim
     * @reason Utilizzo di texture cacheate per non sovraccaricare la heap.
     */
    @Overwrite
    public static void a(GuiScreen var0) {
        GL11.glPushMatrix();
        int var1 = (int)(Math.sin(i.bz / 60.0F) * 15.0D);
        drawGradientRect(0, var1, var0.width, 50, -43691, 0);
        drawGradientRect(0, var0.height - 55 - var1, var0.width, 55, 0, -43691);
        c(0.0D, 0.0D, TEX_BLACKBLOCK1, var0.width, 50.0D);
        c(0.0D, (double)(var0.height - 55), TEX_BLACKBLOCK2, var0.width, 55.0D);
        GL11.glPopMatrix();
    }

    /**
     * @author zim
     * @reason Rimozione di allocazioni continue dal render loop e custom rescale Terra.
     */
    @Overwrite
    public static void b(GuiScreen var0) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();

        int scaleFactor = 1;
        while (mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            scaleFactor++;
        }
        if (scaleFactor == 0) scaleFactor = 1;

        float var2 = (float)(Math.sin((double)(i.bz / 100.0F)) / 30.0D);
        float var3 = (float)Math.sin((double)(i.bz / 10.0F));
        float var4 = (float)Math.sin((double)(i.bz / 20.0F));

        GL11.glPushMatrix();
        GL11.glTranslatef(var3, var4, 0.0F);
        GL11.glTranslatef(i.bA / 4.0F, i.bB / 10.0F, 0.0F);

        a((double)(var0.width / 2), (double)(var0.height / 2), jh, (float)var0.width, (float)var0.height, 1.3F + var2, 1.0D);
        GL11.glPopMatrix();

        l(var0.width, var0.height);
        GL11.glPopMatrix();

        b(0.0D, 0.0D, (double)var0.width, (double)var0.height, "0x000000", 1.0F);

        if(cachedSunEarthModel == null) cachedSunEarthModel = new deci.v.a();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        float var5 = (float)(Math.sin((double)(i.bz / 20.0F)) / 5.0D);
        float var6 = (float)(Math.sin((double)(i.bz / 20.0F)) / 5.0D);
        GL11.glTranslatef(var5, var6, 0.0F);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)(var0.width / 5 * 4), (float)(var0.height / 2), 0.0F);
        GL11.glRotatef(i.bz / 30.0F, 1.0F, 1.0F, 0.0F);
        deci.j.a.a(0, 0, 0, cachedSunEarthModel, TEX_SUN, (float)(15 * scaleFactor));
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef((float)(var0.width / 5), (float)(var0.height / 2), 0.0F);
        GL11.glRotatef(i.bz / 25.0F, 1.0F, 1.0F, 0.0F);
        // FIX APPLICATO QUI: La terra usa il tuo (80 * scaleFactor) al posto di (130 * scaleFactor)
        deci.j.a.a(0, 0, 0, cachedSunEarthModel, TEX_EARTH, (float)(80 * scaleFactor));
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();

        float var7 = (float)(Math.sin((double)(i.bz / (float)(Math.random() < 0.25D ? 7 : 8))) / 35.0D) + 1.0F;

        a((double)(var0.width / 5 * 4), (double)(var0.height / 2), TEX_SUN_PARTICLE, 600.0F, 600.0F, 1.0F, (double)var7);
        for(int x = 0; x < 5; x++) {
            a((double)(var0.width / 5 * 4), (double)(var0.height / 2), TEX_SUN_PARTICLE, 150.0F, 150.0F, 1.0F, 1.0D);
        }

        c(0.0D, 0.0D, TEX_SIDESHADOW, (double)var0.width, (double)var0.height);
        drawGradientRect(0, 0, var0.width, var0.height / 2, -16777216, 0);
        drawGradientRect(0, var0.height / 2, var0.width, var0.height / 2, 0, -16777216);
    }

    /**
     * @author zim
     * @reason Fix gravissimo leak di GL_BLEND e fix barre bianche (Texture2D disattivato da metodi precedenti)
     */
    @Overwrite
    public static void a(double var0, double var2, ResourceLocation var4, double var5, double var7, double var9) {
        if (var9 <= 0.0D) return;

        GL11.glPushMatrix();

        // FIX: Forziamo l'accensione delle texture e dell'alpha test prima di disegnare!
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        mc.renderEngine.bindTexture(var4);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, var9);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        jj.startDrawingQuads();
        jj.setColorRGBA_F(1.0F, 1.0F, 1.0F, (float)var9);
        addVertexWithUV(var0, var2, var5, var7);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    /**
     * @author zim
     * @reason Fix leak GL stati + pulizia e fix rendering texture bianco
     */
    @Overwrite
    public static void a(double var0, double var2, ResourceLocation var4, double var5, double var7, double var9, Color var11) {
        // FIX: Forziamo l'accensione delle texture
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        mc.renderEngine.bindTexture(var4);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        jj.startDrawingQuads();
        jj.setColorRGBA(var11.getRed(), var11.getGreen(), var11.getBlue(), (int)((double)var11.getAlpha() * var9));
        addVertexWithUV(var0, var2, var5, var7);

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Unique
    private static void addVertexWithUV(double var0, double var2, double var5, double var7) {
        jj.addVertexWithUV(var0, var2 + var7, 0.0D, 0.0D, 1.0D);
        jj.addVertexWithUV(var0 + var5, var2 + var7, 0.0D, 1.0D, 1.0D);
        jj.addVertexWithUV(var0 + var5, var2, 0.0D, 1.0D, 0.0D);
        jj.addVertexWithUV(var0, var2, 0.0D, 0.0D, 0.0D);
        jj.draw();
    }

    /**
     * @author zim
     * @reason Fix chiamate doppie inutili a GL11 ed evidenti leak di texture 2D
     */
    @Overwrite
    public static void a(double var0, double var2, double var4, double var6, float var8, float var9, float var10, float var11) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(var8, var9, var10, var11);

        jj.startDrawingQuads();
        jj.addVertex(var0, var6, 0.0D);
        jj.addVertex(var4, var6, 0.0D);
        jj.addVertex(var4, var2, 0.0D);
        jj.addVertex(var0, var2, 0.0D);
        jj.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    /**
     * @author zim
     * @reason Fix gravissimo leak di GL_ALPHA_TEST che rendeva le texture nere/invisibili
     */
    @Overwrite
    public static void a(int var0, int var1, int var2, int var3, int var4, int var5, float var6) {
        var2 = var0 + var2;
        var3 = var1 + var3;
        float var7 = (float)(var4 >> 16 & 255) / 255.0F;
        float var8 = (float)(var4 >> 8 & 255) / 255.0F;
        float var9 = (float)(var4 & 255) / 255.0F;
        float var10 = (float)(var5 >> 16 & 255) / 255.0F;
        float var11 = (float)(var5 >> 8 & 255) / 255.0F;
        float var12 = (float)(var5 & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        jj.startDrawingQuads();
        jj.setColorRGBA_F(var7, var8, var9, var6);
        jj.addVertex((double)var2, (double)var1, 0.0D);
        jj.addVertex((double)var0, (double)var1, 0.0D);
        jj.setColorRGBA_F(var10, var11, var12, var6);
        jj.addVertex((double)var0, (double)var3, 0.0D);
        jj.addVertex((double)var2, (double)var3, 0.0D);
        jj.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * @author zim
     * @reason Fix leak GL_COLOR_MATERIAL e state reset corretto per la Lightmap
     */
    @Overwrite
    public static void a(Minecraft var0, EntityPlayer var1, int var2, int var3, float var4, float var5) {
        RenderManager.instance.field_147941_i = var1;
        RenderManager.instance.renderEngine = var0.getTextureManager();

        if (jo == null) {
            jo = new deci.J.e.a();
            jo.setRenderManager(RenderManager.instance);
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glTranslatef((float)var2, (float)var3, 80.0F);
        GL11.glScalef(-var5, var5, var5);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

        float var6 = var1.renderYawOffset;
        float var7 = var1.rotationYaw;
        float var8 = var1.rotationPitch;
        float varPrevYawHead = var1.rotationYawHead;

        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-110.0F + var4, 0.0F, 1.0F, 0.0F);

        var1.renderYawOffset = 0.0F;
        var1.rotationYaw = 0.0F;
        var1.rotationPitch = 0.0F;
        var1.rotationYawHead = var1.renderYawOffset + deci.b.i.bA;
        var1.rotationPitch = 10.0F + -deci.b.i.bB;

        GL11.glTranslatef(0.0F, var1.yOffset, 0.0F);

        float var9 = (float)(Math.sin((double)(deci.b.i.by / 30.0F)) * 5.0D);
        float var10 = (float)(Math.sin((double)(deci.b.i.by / 20.0F)) * 5.0D);
        RenderManager.instance.playerViewX = 180.0F;
        var1.rotationYawHead += var10;
        var1.rotationPitch += var9;

        jo.doRender(var1, 0.0D, 0.0D, 0.0D, 0.0F, 0.625F);

        var1.renderYawOffset = var6;
        var1.rotationYaw = var7;
        var1.rotationPitch = var8;
        var1.rotationYawHead = varPrevYawHead;

        // --- THE CRITICAL FIX IS HERE ---
        // 1. Spegniamo gli stati 3D dell'entità
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // 2. Spegniamo brutalmente le luci 3D che scoloriscono l'UI facendola diventare bianca
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);

        // 3. Resettiamo la Lightmap e forziamo l'accensione di GL_TEXTURE_2D (fondamentale)
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // 4. Per sicurezza riaccendiamo l'Alpha Test che potrebbe essersi spento
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        GL11.glPopMatrix();
    }

    /**
     * @author zim
     * @reason Ripristino pulito degli stati di rendering e lightmap + Fix GL_RESCALE_NORMAL
     */
    @Overwrite
    public static void a(EntityPlayer var0, double var1, double var3, float var5, float var6) {
        RenderManager.instance.field_147941_i = var0;
        RenderManager.instance.renderEngine = mc.getTextureManager();
        deci.Q.b var7 = deci.Q.b.e(var0);
        if (var7 != null) var7.h(true);

        if (jp == null) {
            jp = new deci.e.c();
            jp.setRenderManager(RenderManager.instance);
        }

        GL11.glPushMatrix();
        GL11.glTranslated(var1, var3, 50.0D);
        GL11.glScalef(-var5, var5, var5);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

        float var8 = var0.renderYawOffset;
        float var9 = var0.rotationYaw;
        float var10 = var0.rotationPitch;
        float varPrevYawHead = var0.rotationYawHead;

        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-110.0F + var6, 0.0F, 1.0F, 0.0F);

        var0.renderYawOffset = 0.0F;
        var0.rotationYaw = 0.0F;
        var0.rotationPitch = mc.thePlayer != null ? mc.thePlayer.rotationPitch : -(deci.b.i.bB * 2.0F) + 15.0F;
        var0.rotationYawHead = var0.renderYawOffset;

        GL11.glTranslatef(0.0F, var0.yOffset, 0.0F);
        RenderManager.instance.playerViewX = 180.0F;

        jp.doRender(var0, 0.0D, 0.0D, 0.0D, 0.0F, 0.625F);

        var0.renderYawOffset = var8;
        var0.rotationYaw = var9;
        var0.rotationPitch = var10;
        var0.rotationYawHead = varPrevYawHead;

        GL11.glPopMatrix();

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }


    /**
     * @author zim
     * @reason Rendering smoke grenades "Volumetric Soft Edition" - Più piccolo, bordi morbidi, no clipping visibile.
     */
    @Overwrite
    public static void a(ResourceLocation texture, double x, double y, double z, float partialTicks, int width, int height, String color, double alpha) {
        EntityClientPlayerMP player = mc.thePlayer;

        double realAlpha = Math.max(0.0D, Math.min(1.0D, alpha / 150.0D));

        if (player == null || realAlpha <= 0.0D) return;

        GL11.glPushMatrix();

        float interpX = (float)(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks);
        float interpY = (float)(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks);
        float interpZ = (float)(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks);

        GL11.glTranslatef((float)x - interpX, (float)y - interpY, (float)z - interpZ);

        float scale = 0.016F;
        GL11.glScalef(-scale, -scale, scale);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.004F);

        float time = deci.b.i.by + partialTicks;

        GL11.glRotatef(time * 0.05F, 0.0F, 1.0F, 0.0F);

        int planes = 24;
        double renderAlpha = realAlpha * 0.35D;

        for (int i = 0; i < planes; i++) {
            GL11.glPushMatrix();

            float yaw = i * 137.5F;
            float pitch = (i * 90.0F / planes) - 45.0F;

            GL11.glRotatef(yaw + (time * 0.15F), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);

            float offset = width * (0.05F + (i % 4) * 0.04F);
            GL11.glTranslatef(0.0F, 0.0F, offset);

            float quadScale = 0.65F + (i % 5) * 0.2F;
            GL11.glScalef(quadScale, quadScale, quadScale);

            GL11.glRotatef(time * 0.2F + (i * 43.0F), 0.0F, 0.0F, 1.0F);

            a((double) -width / 2, (double) -height / 2, texture, (double) width, (double) height, renderAlpha);

            GL11.glPopMatrix();
        }

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glPopMatrix();
    }

}
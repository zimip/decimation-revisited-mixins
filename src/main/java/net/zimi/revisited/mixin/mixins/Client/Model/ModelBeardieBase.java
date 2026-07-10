package net.zimi.revisited.mixin.mixins.Client.Model;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import deci.ay.i;
import deci.n.b;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.zimi.revisited.Addon;
import net.zimi.revisited.Client.Handler.ShaderHelper;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = deci.n.f.class, remap = false)
public abstract class ModelBeardieBase extends ModelBase {
    @Shadow
    private Field lh;

    @Unique
    private int depthTex = -1;

    @ModifyVariable(method = "a(Ldeci/ay/i;[Ljava/lang/Object;)V", at = @At("STORE"), ordinal = 0)
    public int setEjectSpeed(int value) {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Inject(method = {"a(Lnet/minecraft/client/Minecraft;Ldeci/ay/i;Lnet/minecraft/item/ItemStack;F)V"}, at = {@At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0, shift = At.Shift.AFTER)})
    public void renderScope(Minecraft par1, i par2, ItemStack par3, float par4, CallbackInfo ci) {
        deci.ay.h scope = par2.H(par3);
        if (scope != null && scope == deci.aD.k.awi) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        }
    }

    /**
     * @author
     * @reason
     */

    @SideOnly(Side.CLIENT)
    @Overwrite
    private void b(b var1) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE); // 2884
        Tessellator var2 = Tessellator.instance;

        if (this.lh == null) {
            try {
                this.lh = ReflectionHelper.findField(Tessellator.class, "brightness", "field_78401_l");
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

        int var3 = 0;
        try {
            var3 = (Integer)this.lh.get(var2);
        } catch (IllegalAccessException | IllegalArgumentException var5) {
            var5.printStackTrace();
        }

        var1.jO = 1.0F;
        var1.jN = deci.c.b.dB < 0.0F ? 1.0F : -1.0F;

        Minecraft mc = Minecraft.getMinecraft();
        boolean isThermalScope = isThermalScope(mc);

        GL11.glEnable(GL11.GL_TEXTURE_2D); // 3553

        if (isThermalScope) {
            if (ShaderHelper.thermalProgram != 0) {
                if(depthTex == -1) depthTex = GL11.glGenTextures();

                ARBShaderObjects.glUseProgramObjectARB(ShaderHelper.thermalProgram);

                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, deci.c.b.dC);

                int diffLoc = ARBShaderObjects.glGetUniformLocationARB(ShaderHelper.thermalProgram, "diffuseTex");
                if(diffLoc != -1) ARBShaderObjects.glUniform1iARB(diffLoc, 0);

                OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTex);
                GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, mc.displayWidth, mc.displayHeight, 0);

                int depthLoc = ARBShaderObjects.glGetUniformLocationARB(ShaderHelper.thermalProgram, "depthTex");
                if(depthLoc != -1) ARBShaderObjects.glUniform1iARB(depthLoc, 1);

                int resLoc = ARBShaderObjects.glGetUniformLocationARB(ShaderHelper.thermalProgram, "resolution");
                if(resLoc != -1) ARBShaderObjects.glUniform2fARB(resLoc, (float)mc.displayWidth, (float)mc.displayHeight);

                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            } else {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, deci.c.b.dC);
            }
        } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, deci.c.b.dC);
        }

        var1.jE = true;
        var1.a(0.0625F, true);

        if (isThermalScope && ShaderHelper.thermalProgram != 0) {
            ARBShaderObjects.glUseProgramObjectARB(0);

            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        var2.setBrightness(var3);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    @Unique
    private static boolean isThermalScope(Minecraft mc) {
        boolean isThermalScope = false;

        if (mc.thePlayer != null && mc.thePlayer.getCurrentEquippedItem() != null) {
            if(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof i) {
                ItemStack t = mc.thePlayer.getCurrentEquippedItem();
                i theGun = (i) t.getItem();
                if(theGun != null && theGun.H(t) == Addon.thermalScope && mc.gameSettings.thirdPersonView == 0) {
                    isThermalScope = true;
                }
            }
        }
        return isThermalScope;
    }

    @SideOnly(Side.CLIENT)
    @Inject(method = "a(Lnet/minecraft/client/Minecraft;Ldeci/ay/i;Lnet/minecraft/item/ItemStack;F)V", at = @At("RETURN"), remap = false)
    private void fixScopeStateLeak(Minecraft mc, i gun, ItemStack stack, float var4, CallbackInfo ci) {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        deci.b.g.E();

        if (mc.thePlayer != null && mc.thePlayer.worldObj != null) {
            deci.b.g.a(mc.thePlayer.worldObj, mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        }
    }
}
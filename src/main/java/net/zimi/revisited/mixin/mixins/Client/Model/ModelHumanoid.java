package net.zimi.revisited.mixin.mixins.Client.Model;

import deci.af.d;
import deci.b.g;
import deci.q.h;
import net.minecraft.client.model.ModelBiped;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({h.class})
public abstract class ModelHumanoid extends ModelBiped {
    @Inject(method = {"a(Ldeci/af/d;FFFFFF)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V", ordinal = 1)})
    private void preEyesBind(d par1, float par2, float par3, float par4, float par5, float par6, float par7, CallbackInfo ci) {
        if (par1 instanceof deci.ag.d) {
            GL11.glPushMatrix();
            g.a(240.0F, 240.0F);
            GL11.glPopMatrix();
        }
    }

    @Inject(method = {"a(Ldeci/af/d;FFFFFF)V"}, at = {@At(value = "INVOKE", target = "Ldeci/q/h;a(FLdeci/q/h;)V", shift = At.Shift.AFTER, ordinal = 1)}, remap = false)
    private void postEyesBind(d par1, float par2, float par3, float par4, float par5, float par6, float par7, CallbackInfo ci) {
        if (par1 instanceof deci.ag.d) {
            GL11.glPushMatrix();
            g.a(par1.worldObj, par1.posX, par1.posY, par1.posZ);
            GL11.glPopMatrix();
        }
    }
}
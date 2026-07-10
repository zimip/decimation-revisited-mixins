package net.zimi.revisited.mixin.mixins.Client.Render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = deci.q.c.class, remap = false)
public class ModelDeciPlayer extends ModelBiped {

    @Inject(method = "a(Lnet/minecraft/entity/player/EntityPlayer;FFFFFF)V", at = @At("RETURN"), remap = false)
    private void fixOpenGLStateLeak(EntityPlayer player, float var2, float var3, float var4, float var5, float var6, float var7, CallbackInfo ci) {
        GL11.glEnable(2896);

        GL11.glEnable(3008);

        deci.b.g.E();

        if (player != null && player.worldObj != null) {
            deci.b.g.a(player.worldObj, player.posX, player.posY, player.posZ);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
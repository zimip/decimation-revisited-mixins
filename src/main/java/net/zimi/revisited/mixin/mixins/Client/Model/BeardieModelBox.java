package net.zimi.revisited.mixin.mixins.Client.Model;

import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = deci.n.a.class, remap = false)
public class BeardieModelBox {
    @Inject(method = "a(Lnet/minecraft/client/renderer/Tessellator;F)V", at = @At("HEAD"))
    private void onDrawStart1(Tessellator tessellator, float f, CallbackInfo ci) {
        tessellator.startDrawingQuads();
    }

    @Inject(method = "a(Lnet/minecraft/client/renderer/Tessellator;F)V", at = @At("RETURN"))
    private void onDrawEnd1(Tessellator tessellator, float f, CallbackInfo ci) {
        tessellator.draw();
    }

    @Inject(method = "a(Lnet/minecraft/client/renderer/Tessellator;FFFFF)V", at = @At("HEAD"))
    private void onDrawStart2(Tessellator tessellator, float f1, float f2, float f3, float f4, float f5, CallbackInfo ci) {
        tessellator.startDrawingQuads();
    }

    @Inject(method = "a(Lnet/minecraft/client/renderer/Tessellator;FFFFF)V", at = @At("RETURN"))
    private void onDrawEnd2(Tessellator tessellator, float f1, float f2, float f3, float f4, float f5, CallbackInfo ci) {
        tessellator.draw();
    }

    @Inject(method = "a(Lnet/minecraft/client/renderer/Tessellator;FFF[[F)V", at = @At("HEAD"))
    private void onDrawStart3(Tessellator tessellator, float f1, float f2, float f3, float[][] arr, CallbackInfo ci) {
        tessellator.startDrawingQuads();
    }

    @Inject(method = "a(Lnet/minecraft/client/renderer/Tessellator;FFF[[F)V", at = @At("RETURN"))
    private void onDrawEnd3(Tessellator tessellator, float f1, float f2, float f3, float[][] arr, CallbackInfo ci) {
        tessellator.draw();
    }
}
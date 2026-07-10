package net.zimi.revisited.mixin.mixins.Client.Render;

import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = deci.J.w.class)
public abstract class RenderSignalGrenade extends Render {
    @ModifyArg(method = "getEntityTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ResourceLocation;<init>(Ljava/lang/String;Ljava/lang/String;)V", ordinal = 0), index = 1)
    public String fixTexture(String texture) {
        return texture.toLowerCase();
    }
}

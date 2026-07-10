package net.zimi.revisited.mixin.mixins.Common.Handler;

import deci.aD.g;
import net.decimation.mod.common.block.props.BlockProp;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = g.class, remap = false)
public abstract class PropRegistry implements deci.aD.a{
    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        ((BlockProp)g.ait).setSafezoneLootable(true);
        ((BlockProp)g.ais).setSafezoneLootable(true);

        ((BlockProp)g.ajW).setAsLightEmitter(1F).setLightLevel(1F);
        ((BlockProp)g.aiu).setAmbientSound(new ResourceLocation("deci", "block.light.idle"), 0.1F);
    }
}

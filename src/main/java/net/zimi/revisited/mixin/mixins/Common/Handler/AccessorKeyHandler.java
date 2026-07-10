package net.zimi.revisited.mixin.mixins.Common.Handler;

import deci.aE.a;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = deci.aE.a.P.class, remap = false)
public interface AccessorKeyHandler {

    @Accessor("ayv")
    a.P.b getAction();

}
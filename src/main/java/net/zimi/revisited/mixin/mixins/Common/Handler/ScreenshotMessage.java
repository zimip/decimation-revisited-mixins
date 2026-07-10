package net.zimi.revisited.mixin.mixins.Common.Handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = deci.aE.a.ae.a.class, remap = false)
public class ScreenshotMessage {
    @Overwrite(remap = false)
    public IMessage a(deci.aE.a.ae var1, MessageContext var2) {
        if (!cpw.mods.fml.common.FMLCommonHandler.instance().getSide().isServer()) {
            deci.aE.a.ae.ayU = 2;
        }
        return null;
    }
}
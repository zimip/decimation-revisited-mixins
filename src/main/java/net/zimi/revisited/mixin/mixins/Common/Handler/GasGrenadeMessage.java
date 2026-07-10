package net.zimi.revisited.mixin.mixins.Common.Handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import deci.aE.a;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = a.ak.a.class, remap = false)
public abstract class GasGrenadeMessage implements IMessageHandler<a.ak, IMessage> {
    @Overwrite
    public IMessage a(a.ak param2ak, MessageContext param2MessageContext) {
        return null;
    }
}
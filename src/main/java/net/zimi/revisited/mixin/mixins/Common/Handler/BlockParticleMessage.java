package net.zimi.revisited.mixin.mixins.Common.Handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import deci.aE.a;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = a.C.a.class, remap = false)
public abstract class BlockParticleMessage implements IMessageHandler<deci.aE.a.C, IMessage> {
    @Overwrite
    public IMessage a(deci.aE.a.C var1, MessageContext var2) {
        return null;
    }
}

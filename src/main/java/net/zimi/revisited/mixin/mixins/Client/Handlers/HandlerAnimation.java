package net.zimi.revisited.mixin.mixins.Client.Handlers;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import deci.F.a;
import deci.ay.i;
import java.util.HashMap;
import net.minecraft.client.model.ModelBase;
import net.zimi.revisited.Addon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {a.class}, remap = false)
public class HandlerAnimation {
    @Shadow
    public HashMap<Integer, String> Rm = new HashMap<>();

    @Shadow
    public int QQ = 0;

    @Inject(method = {"a"}, at = {@At(value = "INVOKE", target = "Ljava/util/HashMap;containsKey(Ljava/lang/Object;)Z", ordinal = 0)})
    @SideOnly(Side.CLIENT)
    private void onAnimationReload(ModelBase par1, i par2, CallbackInfo ci) {
        int id = (FMLClientHandler.instance().getClient()).thePlayer.getEntityId();
        String sound = par2.aer + this.Rm.get(this.QQ);
        if (!sound.contains("null"))
            Addon.Network.PACKET.sendToServer(new Addon.Network.Message_PlaySoundRequest(id, sound, 0.7F));
    }
}
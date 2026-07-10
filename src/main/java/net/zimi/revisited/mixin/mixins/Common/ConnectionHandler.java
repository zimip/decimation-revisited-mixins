package net.zimi.revisited.mixin.mixins.Common;

import com.boehmod.lib.utils.BoehModLogger;
import com.esotericsoftware.kryonet.Client;
import net.decimation.mod.utilities.net.client_network.api.objects.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.InetAddress;

@Mixin(value = deci.aP.a.class, remap = false)
public class ConnectionHandler {
    @Shadow
    public Client aBX = new Client(32768, 8192);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void e(String var1, int var2, int var3) throws IOException {
        BoehModLogger.printLine(BoehModLogger.EnumLogType.NETWORK, String.format("Registering new client network connection TCP (%s) UDP (%s)", var2, var3));
        ObjectRegistry.registerObjects(this.aBX.getKryo());
        (new Thread(this.aBX)).start();
        this.aBX.connect(5000, "network.zimipiri.net", 28108, 28109);
        BoehModLogger.printLine(BoehModLogger.EnumLogType.NETWORK, String.format("Connecting to network TCP (%s) UDP (%s)", 28108, 28109));
    }
}

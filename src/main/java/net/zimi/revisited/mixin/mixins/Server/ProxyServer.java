package net.zimi.revisited.mixin.mixins.Server;

import com.boehmod.lib.utils.BoehModLogger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import deci.aJ.a;
import deci.aK.*;
import net.minecraftforge.common.MinecraftForge;
import net.zimi.revisited.Addon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.zimi.revisited.Addon.*;

@Mixin(value = deci.a.e.class, remap = false)
public abstract class ProxyServer implements deci.a.d {
    @Shadow
    @Final
    private a N;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void preInit(FMLPreInitializationEvent var1) {
        BoehModLogger.printLine(BoehModLogger.EnumLogType.CLIENT, "Pre-Initializing Fixed Deci-Forge Server Proxy...");

        this.N.init();
    }
}

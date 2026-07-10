package net.zimi.revisited.mixin.mixins.Common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import deci.b.i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.io.File;

@Mixin(value = net.decimation.mod.common.utils.h.class, remap = false)
public class Updater {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void gH() {
    }

    @Overwrite
    public static boolean gI() throws OutOfMemoryError {
        return false;
    }

    @Mixin(targets = "net.decimation.mod.common.utils.h$4", remap = false)
    public static class resourcePackDownloader {
        @SideOnly(Side.CLIENT)
        @ModifyArgs(method = "run", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/utils/h$4;f(Ljava/lang/String;Ljava/lang/String;)V"))
        public void stringParameters(Args args) {
            args.set(0, "https://github.com/zimip/deciresourcepack/releases/download/resourcepack/DeciResourcePack.zip");
        }
    }
}

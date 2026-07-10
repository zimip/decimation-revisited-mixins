package net.zimi.revisited.mixin.mixins.Common.Handler;

import deci.aD.k;
import deci.ay.b;
import deci.ay.c;
import deci.ay.e;
import deci.ay.h;
import net.zimi.revisited.Addon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {k.class}, remap = false)
public abstract class ItemRegistry {
    @ModifyArg(method = {"gg"}, at = @At(value = "INVOKE", target = "Ldeci/ay/i;am(I)Ldeci/ay/i;", ordinal = 65), index = 0)
    private static int setDamage(int i) {
        return 80;
    }

    @ModifyArg(method = {"gg"}, at = @At(value = "INVOKE", target = "Ldeci/ay/e;<init>([F[Ldeci/ay/e$a;[I[[FFF)V", ordinal = 88), index = 1)
    private static e.a[] fireModes(e.a[] as) {
        return new e.a[]{e.a.SINGLE, e.a.AUTO};
    }

    @ModifyArg(method = {"gg"}, at = @At(value = "INVOKE", target = "Ldeci/ay/h;<init>(Ldeci/ay/b;Ldeci/ay/c;FFLjava/lang/String;)V", ordinal = 4), index = 2)
    private static float psoZoom(float v) {
        return 48F;
    }

    @Inject(method = "gg", at = @At("TAIL"))
    private static void thermalScope(CallbackInfo ci) {
        Addon.thermalScope = new h(b.sight, c.all, 10F, 0.2F, "thermal");
    }

    @ModifyArg(method = {"init"}, at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemMaskDeci;<init>(Ljava/lang/String;F)V", ordinal = 0), index = 1)
    private float balaclavaProt(float value) {
        return 0.98F;
    }

    @ModifyArg(method = {"init"}, at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemGasMaskDeci;<init>(Ljava/lang/String;F)V", ordinal = 0), index = 1)
    private float mask1Prot(float value) {
        return 0.98F;
    }

    @ModifyArg(method = {"init"}, at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemGasMaskDeci;<init>(Ljava/lang/String;F)V", ordinal = 1), index = 1)
    private float mask2Prot(float value) {
        return 0.95F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 0), index = 2)
    private float spetsHelm(float value) {
        return 0.9F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 4), index = 2)
    private float sasHelm(float value) {
        return 0.9F;
    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 13), index = 2)
    private float banditHelm(float value) {
        return 0.98F;
    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 17), index = 2)
    private float militiaHelm(float value) {
        return 0.98F;
    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 23), index = 2)
    private float marineDesertHelm(float value) {
        return 0.9F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 28), index = 2)
    private float marineDesertHat(float value) {
        return 0.95F;
    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 29), index = 2)
    private float marineForestHelm(float value) {
        return 0.9F;
    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 34), index = 2)
    private float marineForestHat(float value) {
        return 0.95F;
    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 35), index = 2)
    private float marineUrbanHelm(float value) {
        return 0.9F;
    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 40), index = 2)
    private float marineUrbanHat(float value) {
        return 0.95F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 41), index = 2)
    private float marineSnowHelm(float value) {
        return 0.9F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 46), index = 2)
    private float marineSnowHat(float value) {
        return 0.95F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 47), index = 2)
    private float marineBlackHelm(float value) {
        return 0.9F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 51), index = 2)
    private float marineBlackHat(float value) {
        return 0.95F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 52), index = 2)
    private float marineBlueHelm(float value) {
        return 0.9F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorDeci;<init>(IIFLjava/lang/String;)V", ordinal = 56), index = 2)
    private float marineFuryHelm(float value) {
        return 0.9F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorJuggernaut;<init>(IIFLjava/lang/String;)V", ordinal = 0), index = 2)
    private float juggGreenHelm(float value) {
        return 0.8F;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/decimation/mod/common/item/armor/ItemArmorJuggernaut;<init>(IIFLjava/lang/String;)V", ordinal = 4), index = 2)
    private float juggBlackHelm(float value) {
        return 0.8F;
    }
}

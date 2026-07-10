package net.zimi.revisited.mixin.mixins.Client.Gui.Menu;

import deci.d.m;
import deci.d.p;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = {p.class}, remap = false)
public abstract class RenderGuiServerSlot extends m {
    @ModifyArg(method = {"c"}, at = @At(value = "INVOKE", target = "Ldeci/j/a;d(Ljava/lang/String;II)V", ordinal = 1), index = 1)
    private int alignPingCorrectly(int original) {
        return this.posX + 145;
    }

    @ModifyArg(method = {"c"}, at = @At(value = "INVOKE", target = "Ldeci/j/a;d(Ljava/lang/String;II)V", ordinal = 2), index = 1)
    private int alignUnreachableCorrectly(int original) {
        return this.posX + 130;
    }

    @ModifyArg(method = {"c"}, at = @At(value = "INVOKE", target = "Ldeci/j/a;d(Ljava/lang/String;II)V", ordinal = 4), index = 0)
    private String removeQuestionMark(String original) {
        return "";
    }
}

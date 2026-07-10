package net.zimi.revisited.mixin.mixins.Common.Handler.Loot;

import deci.aB.e;
import deci.aJ.b;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.TimeUnit;

@Mixin(value = {e.class}, remap = false)
public abstract class LootTimeTracker {

    @Final
    @Shadow
    private TLongLongMap afm;

    @Unique
    private long decimixin$lastCleanupTime = System.currentTimeMillis();

    @Inject(method = "k", at = @At("HEAD"))
    public void onIsLootable(int var1, int var2, int var3, CallbackInfoReturnable<Boolean> cir) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - decimixin$lastCleanupTime > 300000L) {
            decimixin$lastCleanupTime = currentTime;

            long cooldownMs = TimeUnit.SECONDS.toMillis(b.aAy);

            TLongLongIterator iterator = this.afm.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                if (currentTime - iterator.value() >= cooldownMs) {
                    iterator.remove();
                }
            }
        }
    }
}
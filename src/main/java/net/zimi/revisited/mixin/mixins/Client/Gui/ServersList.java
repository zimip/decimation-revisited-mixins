package net.zimi.revisited.mixin.mixins.Client.Gui;

import deci.k.a;
import deci.k.b;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = {b.class}, remap = false)
public class ServersList {
    /**
     * @author
     * @reason
     */
    @Overwrite
    private static List<a> bg() {
        ArrayList<a> var0 = new ArrayList<>();
        var0.add((new a("EU-BUKOVKA", "Bukovka - §9EU", "play.zimipiri.net", 25565, "§2Bukovka", "bukovka")).y("bukovka"));
        return var0;
    }
}

package net.zimi.revisited.mixin.mixins.Client.Handlers;

import deci.d.c;
import deci.d.i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = c.class, remap = false)
public abstract class HandlerArticles extends deci.d.b {
    public HandlerArticles(int i, int i1, int i2, int i3, int i4, i i5) {
        super(i, i1, i2, i3, i4, i5);
    }

    @Overwrite
    public c j(String paramString) {
        if (paramString == null || paramString.trim().isEmpty()) {
            return (c) (Object) this;
        }

        try {
            String[] parts = paramString.split("<>", -1);

            if (parts.length > 0 && !parts[0].isEmpty()) k(parts[0]);
            if (parts.length > 1 && !parts[1].isEmpty()) l(parts[1]);
            if (parts.length > 2 && !parts[2].isEmpty()) m(parts[2]);
            if (parts.length > 3 && !parts[3].isEmpty()) n(parts[3]);

        } catch (Exception e) {
            System.err.println("Error parsing article string '" + paramString + "': " + e.getMessage());
        }

        return (c) (Object) this;
    }

    @Shadow public abstract c k(String var1);
    @Shadow public abstract c l(String var1);
    @Shadow public abstract c m(String var1);
    @Shadow public abstract c n(String var1);
}
package net.zimi.revisited.mixin.mixins.Client;

import net.minecraft.world.World;
import net.zimi.revisited.Client.Handler.ClientVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = deci.b.i.class, remap = false)
public class VariablesClient {

    /**
     * @author ZIM
     * @reason uhhh
     */
    @Overwrite
    public static float a(World theWorld) {
        return ambience_worldFog();
    }

    @Unique
    private static float ambience_worldFog() {
        return ClientVariables.fogDensity;
    }
}


package net.zimi.revisited.Client.Handler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientVariables {
    public static float fogDensity = 0.008F;

    public static void setFogDensity(float fogDensity) {
        ClientVariables.fogDensity = fogDensity;
    }
}

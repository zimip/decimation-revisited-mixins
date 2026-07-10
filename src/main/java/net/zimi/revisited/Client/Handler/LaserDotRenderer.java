package net.zimi.revisited.Client.Handler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LaserDotRenderer {
    public static class DotData {
        public double x, y, z;
        public float pt, width, height;
        public int ticksLeft;

        public DotData(double x, double y, double z, float pt, float width, float height) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.pt = pt;
            this.width = width;
            this.height = height;
            this.ticksLeft = 3;
        }
    }

    public static final List<DotData> dots = new ArrayList<>();

    public static void addDot(double x, double y, double z, float pt, float w, float h) {
        dots.add(new DotData(x, y, z, pt, w, h));
    }
}
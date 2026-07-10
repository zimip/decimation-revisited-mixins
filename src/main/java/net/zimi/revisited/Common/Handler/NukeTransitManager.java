//package net.zimi.revisited.Common.Handler;
//
//import net.minecraft.world.World;
//import net.zimi.revisited.Common.Entity.EntityNuke;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class NukeTransitManager {
//
//    public static class TransitData {
//        public double targetX, targetY, targetZ;
//        public int ticksRemaining;
//        public World world;
//
//        public TransitData(World w, double tx, double ty, double tz, int ticks) {
//            this.world = w;
//            this.targetX = tx;
//            this.targetY = ty;
//            this.targetZ = tz;
//            this.ticksRemaining = ticks;
//        }
//    }
//
//    public static List<TransitData> flyingNukes = new ArrayList<>();
//
//    public static void tick() {
//        if (flyingNukes.isEmpty()) return;
//
//        Iterator<TransitData> it = flyingNukes.iterator();
//        while(it.hasNext()) {
//            TransitData data = it.next();
//            data.ticksRemaining--;
//
//            System.out.println("Time left: " + data.ticksRemaining);
//
//            if (data.ticksRemaining <= 0) {
//                System.out.println("[NUKE SYSTEM] Transit complete. Spawning reentry nuke at X: " + data.targetX + " Z: " + data.targetZ);
//                EntityNuke fallingNuke = new EntityNuke(data.world, data.targetX, 250.0D, data.targetZ, data.targetX, data.targetY, data.targetZ, true);
//                data.world.spawnEntityInWorld(fallingNuke);
//                it.remove();
//            }
//        }
//    }
//}
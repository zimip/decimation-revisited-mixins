//package net.zimi.revisited.Common.Handler;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SiloFileManager {
//    private static final File SILO_FILE = new File("silos.json");
//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//
//    public static class SiloData {
//        public int x, y, z;
//        public long lastUsedTime;
//
//        public SiloData(int x, int y, int z) {
//            this.x = x; this.y = y; this.z = z;
//            this.lastUsedTime = 0L;
//        }
//    }
//
//    public static List<SiloData> getSilos() {
//        if (!SILO_FILE.exists()) {
//            return new ArrayList<>();
//        }
//        try (FileReader reader = new FileReader(SILO_FILE)) {
//            return GSON.fromJson(reader, new TypeToken<List<SiloData>>(){}.getType());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }
//
//    public static void addSilo(int x, int y, int z) {
//        List<SiloData> silos = getSilos();
//        silos.add(new SiloData(x, y, z));
//        saveSilos(silos);
//    }
//
//    public static void removeSilo(int x, int y, int z) {
//        List<SiloData> silos = getSilos();
//        silos.removeIf(silo -> silo.x == x && silo.y == y && silo.z == z);
//        saveSilos(silos);
//    }
//
//    public static void saveSilos(List<SiloData> silos) {
//        try (FileWriter writer = new FileWriter(SILO_FILE)) {
//            GSON.toJson(silos, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
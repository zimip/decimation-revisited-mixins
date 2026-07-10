package net.zimi.revisited.mixin;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.zimi.revisited.Addon;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class CorePlugin implements IFMLLoadingPlugin {
    private static final Path MODS_DIR = (new File(Launch.minecraftHome, "mods/")).toPath();

    public static File findFileOfName() throws IOException {
        if (!MODS_DIR.toFile().exists()) return null;

        return Files.walk(MODS_DIR, new java.nio.file.FileVisitOption[0])
                .filter(path -> path.toString().toLowerCase().endsWith(".jar"))
                .map(Path::toFile)
                .filter(file -> {
                    try (java.util.jar.JarFile jar = new java.util.jar.JarFile(file)) {

                        return jar.getEntry("deci/a/b.class") != null;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (!(Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            try {
                File decimationJar = findFileOfName();
                assert decimationJar != null;
                if (decimationJar.exists()) try {
                    LaunchClassLoader loader = Launch.classLoader;
                    loader.addURL(decimationJar.toURI().toURL());
                    loader.getSources().remove(loader.getSources().size() - 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.revisited.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

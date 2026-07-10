package net.zimi.revisited.Client.Handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class CorpseSkinHelper {
    private static final Set<String> DOWNLOADING = new HashSet<>();

    public static ResourceLocation getSkin(String username) {
        if (username == null || username.isEmpty()) return AbstractClientPlayer.locationStevePng;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null) {
            EntityPlayer player = mc.theWorld.getPlayerEntityByName(username);
            if (player instanceof AbstractClientPlayer) {
                ResourceLocation loc = ((AbstractClientPlayer) player).getLocationSkin();
                if (loc != null) return loc;
            }
        }

        ResourceLocation skinLoc = new ResourceLocation("decimation_corpses", "skins/" + username);
        TextureManager tm = mc.getTextureManager();

        if (tm.getTexture(skinLoc) == null) {
            if (!DOWNLOADING.contains(username)) {
                DOWNLOADING.add(username);
                String url = "https://minotar.net/skin/" + username + ".png";
                ThreadDownloadImageData downloader = new ThreadDownloadImageData(
                        null, url, AbstractClientPlayer.locationStevePng, new ImageBufferDownload()
                );
                tm.loadTexture(skinLoc, downloader);
            }
            return AbstractClientPlayer.locationStevePng;
        }

        return skinLoc;
    }
}
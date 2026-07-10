package net.zimi.revisited.mixin.mixins.Client.Gui.Menu;

import deci.a.b;
import net.decimation.mod.utilities.net.client_network.api.messages.Request_FromClient_Articles;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(deci.i.e.class)
public abstract class RenderGuiHome extends  deci.i.a{
    public RenderGuiHome(b b) {
        super(b);
    }

    @Overwrite
    public void initGui() {
        super.initGui();
        setTitle("Main Menu");

        b b1 = b.a();
        Request_FromClient_Articles request = new Request_FromClient_Articles();
        (b1.e()).aBX.sendTCP(request);

        int i = this.width / 2 - 173;

        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            deci.Q.b b2 = deci.Q.b.e(this.mc.thePlayer);
            if (b2.cf() > 0L) {
                setTitle(EnumChatFormatting.DARK_RED + "Combat Timer: " + EnumChatFormatting.GRAY + (b2.cf() / 20L) + EnumChatFormatting.DARK_RED + " Seconds");
            } else {
                setTitle(EnumChatFormatting.GREEN + "Not in combat! Safe to disconnect!");
            }

        } else if ((b1.e()).aBX.isConnected()) {
            int baseX = i + 104;
            int baseY = 47;

            String[] articleStrings = new String[] { deci.b.i.ct, deci.b.i.cu, deci.b.i.cv, deci.b.i.cw };

            int validCount = 0;

            for (String article : articleStrings) {
                if (article != null && !article.trim().isEmpty() && !article.contains("Nothing here yet")) {

                    int offsetX = (validCount % 2) * 123;
                    int offsetY = (validCount / 2) * 73;

                    deci.d.c newArticleGui = new deci.d.c(1, baseX + offsetX, baseY + offsetY, this);
                    newArticleGui.j(article);

                    this.eX.add(newArticleGui);
                    validCount++;
                }
            }
        }
    }
}

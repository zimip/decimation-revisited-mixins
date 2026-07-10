package net.zimi.revisited.mixin.mixins.Client.Gui.Menu;

import deci.a.b;
import deci.d.l;
import deci.d.p;
import deci.k.a;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(value = deci.i.i.class)
public class RenderMenuServers extends deci.i.a {
    @Shadow
    @Final
    @Mutable
    private final List<a> servers;
    @Shadow
    private final l iW = new l(3, 0, 73, 350, 150, this);
    @Shadow
    private boolean iX;

    public RenderMenuServers(b b, List<a> servers) {
        super(b);
        this.servers = servers;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void aV() {
        if (this.servers != null) {
            this.iW.a(this.servers.stream().map((var1) -> new p(0.5F, EnumChatFormatting.GRAY + "Official " + EnumChatFormatting.DARK_GREEN + "Addon" + EnumChatFormatting.GRAY + " Server " + (var1.bb().equals("1.21.10f") ? EnumChatFormatting.GREEN + "(" + var1.bb() + EnumChatFormatting.GREEN + ")" : EnumChatFormatting.WHITE + "(" + var1.bb() + EnumChatFormatting.WHITE + ")"), var1, this.iX)).iterator());
        }
    }
}

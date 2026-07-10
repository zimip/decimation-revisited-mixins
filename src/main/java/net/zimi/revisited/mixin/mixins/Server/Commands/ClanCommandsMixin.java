package net.zimi.revisited.mixin.mixins.Server.Commands;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.decimation.mod.common.common_commands.CommandBaseDeci;
import net.decimation.mod.server.clans.ClanCommands;
import net.decimation.mod.server.clans.EnumClanRank;
import net.decimation.mod.server.clans.a;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ClanCommands.class})
public abstract class ClanCommandsMixin extends CommandBaseDeci {
    @Redirect(method = {"processCommand"}, at = @At(value = "INVOKE", target = "Lnet/decimation/mod/server/clans/a;w(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/decimation/mod/server/clans/EnumClanRank;", ordinal = 4))
    @SideOnly(Side.SERVER)
    public EnumClanRank clanClaim(a instance, EntityPlayer player) {
        return EnumClanRank.LEADER;
    }
}

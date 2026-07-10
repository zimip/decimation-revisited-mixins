//package net.zimi.revisited.Common.Handler;
//
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.ChatComponentText;
//import net.minecraft.util.EnumChatFormatting;
//
//public class CommandRegisterSilo extends CommandBase {
//    @Override
//    public String getCommandName() { return "registerSilo"; }
//
//    @Override
//    public String getCommandUsage(ICommandSender sender) { return "/registerSilo"; }
//
//    @Override
//    public int getRequiredPermissionLevel() { return 2; }
//
//    @Override
//    public void processCommand(ICommandSender sender, String[] args) {
//        if (sender instanceof EntityPlayer) {
//            EntityPlayer player = (EntityPlayer) sender;
//            int x = (int) Math.floor(player.posX);
//            int y = (int) Math.floor(player.posY);
//            int z = (int) Math.floor(player.posZ);
//
//            SiloFileManager.addSilo(x, y, z);
//            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Silo registered at X:" + x + " Y:" + y + " Z:" + z));
//        }
//    }
//}
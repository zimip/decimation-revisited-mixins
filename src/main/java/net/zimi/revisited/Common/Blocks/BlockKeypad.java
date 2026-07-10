//package net.zimi.revisited.Common.Blocks;
//
//import net.decimation.mod.common.block.props.BlockProp;
//import net.minecraft.block.material.Material;
//import net.minecraft.client.model.ModelBase;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.util.ChatComponentText;
//import net.minecraft.util.EnumChatFormatting;
//import net.minecraft.world.World;
//import net.zimi.revisited.Addon;
//
//import java.util.HashMap;
//import java.util.UUID;
//
//public class BlockKeypad extends BlockProp {
//    private static final HashMap<UUID, Long> clickCooldowns = new HashMap<>();
//
//    public BlockKeypad(String s, String s1, Material material, ModelBase modelBase) {
//        super(s, s1, material, modelBase);
//    }
//
//    @Override
//    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
//        if (player.isSneaking()) {
//            return false;
//        }
//
//        // Tutta la logica e l'invio del pacchetto avvengono SOLO lato server
//        if (!world.isRemote) {
//            UUID playerId = player.getUniqueID();
//            long currentTime = System.currentTimeMillis();
//
//            if (clickCooldowns.containsKey(playerId)) {
//                long lastClickTime = clickCooldowns.get(playerId);
//                long timePassed = currentTime - lastClickTime;
//
//                if (timePassed < 5000L) {
//                    long secondsLeft = (5000L - timePassed) / 1000L;
//                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You must wait " + (secondsLeft + 1) + " seconds."));
//                    return true; // Cooldown attivo, non mandiamo il pacchetto
//                }
//            }
//
//            // Aggiorna il cooldown
//            clickCooldowns.put(playerId, currentTime);
//
//            // Invia il pacchetto S2C al giocatore specifico per aprirgli la GUI!
//            if (player instanceof EntityPlayerMP) {
//                Addon.Network.PACKET.sendTo(new Addon.Network.Message_OpenKeypadS2C(x, y, z), (EntityPlayerMP) player);
//            }
//        }
//
//        return true;
//    }
//}
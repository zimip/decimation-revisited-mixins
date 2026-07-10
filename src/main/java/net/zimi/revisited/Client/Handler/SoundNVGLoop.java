//package net.zimi.revisited.Client.Handler; // Cambialo con il tuo percorso
//
//import cpw.mods.fml.relauncher.Side;
//import cpw.mods.fml.relauncher.SideOnly;
//import net.decimation.mod.common.item.armor.ItemNVGoggles;
//import net.minecraft.client.audio.MovingSound;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.ResourceLocation;
//
//@SideOnly(Side.CLIENT)
//public class SoundNVGLoop extends MovingSound {
//
//    private final EntityPlayer player;
//
//    public SoundNVGLoop(EntityPlayer player) {
//        super(new ResourceLocation("deci", "item.nvgoggles.static"));
//        this.player = player;
//        this.repeat = true;
//        this.volume = 0.1F;
//        this.field_147663_c = 0.7F;
//    }
//
//    @Override
//    public void update() {
//        if (this.player == null || this.player.isDead) {
//            this.volume = 0.0F;
//            this.donePlaying = true;
//            return;
//        }
//
//        deci.Q.b playerData = deci.Q.b.e(this.player);
//        boolean hasGoggles = playerData != null &&
//                playerData.cx() != null &&
//                playerData.cx().getItem() instanceof ItemNVGoggles;
//
//        if (!hasGoggles) {
//            this.volume = 0.0F;
//            this.repeat = false;
//            this.donePlaying = true;
//        } else {
//            this.xPosF = (float) this.player.posX;
//            this.yPosF = (float) this.player.posY;
//            this.zPosF = (float) this.player.posZ;
//        }
//    }
//}
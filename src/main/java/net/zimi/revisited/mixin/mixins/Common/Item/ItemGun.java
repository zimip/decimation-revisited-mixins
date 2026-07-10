package net.zimi.revisited.mixin.mixins.Common.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import deci.K.b;
import deci.ay.e;
import deci.ay.i;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.zimi.revisited.Addon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = i.class, remap = false)
public abstract class ItemGun extends Item {

    @Shadow public abstract int v(ItemStack itemStack);
    @Shadow public e aeq;
    @Shadow @SideOnly(Side.CLIENT) public abstract void a(ItemStack itemStack, EntityLivingBase entityLivingBase);
    @Shadow protected abstract void c(ItemStack itemStack, EntityPlayer entityPlayer);
    @Shadow public String aer;

    @Unique
    private void shoot(ItemStack stack, EntityPlayer player) {
        if (player.worldObj.isRemote) {
            deci.Q.b data = deci.Q.b.e(player);
            i theGun = (i) stack.getItem();

            if (theGun.aes != deci.ay.c.rocket && theGun.aes != deci.ay.c.crossbow) {
                data.h(false);
                Addon.Network.PACKET.sendToServer(new Addon.Message_PlayerShootC2S());
            } else {
                deci.aF.a.a.a.gB().sendToServer(new deci.aE.a.Q());
            }
        }
    }

    @Overwrite
    @SideOnly(Side.CLIENT)
    public void a(ItemStack stack, EntityPlayer player) {
        deci.Q.b data = deci.Q.b.e(player);
        if (data == null) return;

        int mode = this.v(stack);

        if (!data.cz() && (this.aeq.adp[mode] == e.a.AUTO || this.aeq.adp[mode] == e.a.BURST)) {
            data.k(true);
        }

        this.shoot(stack, player);
        data.Vs = System.currentTimeMillis();
        this.a(stack, (EntityLivingBase) player);

        if (b.TI != null) {
            b.TI.TM = deci.F.a.b(new ResourceLocation("deci", "animations/" + this.aer + "/" + this.aer + "Fire.anib"));
            b.TI.TM.active = true;
            deci.b.i.ch = 2;
        }

        data.Q(data.cA() + 5);
        this.c(stack, player);
    }
}
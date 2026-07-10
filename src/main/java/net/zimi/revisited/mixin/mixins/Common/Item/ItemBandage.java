package net.zimi.revisited.mixin.mixins.Common.Item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(deci.au.a.class)
public class ItemBandage extends Item {
    @ModifyArg(method = "onItemRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSoundAtEntity(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V", ordinal = 0), index = 1)
    public String bandageOpen(String p_72956_2_) {
        return "deci:item.bandage.use";
    }

    @Inject(method = "onEaten", at = @At(value = "INVOKE", target = "Ldeci/Q/b;cn()Z", ordinal = 0, shift = At.Shift.AFTER))
    public void playSoundBandage(ItemStack par1, World par2, EntityPlayer par3, CallbackInfoReturnable<ItemStack> cir) {
        par2.playSoundAtEntity(par3, "deci:item.bandage.use", 1.0F, 1.0F);
    }

    @Inject(method = "onEaten", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addChatComponentMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 1))
    public void onEaten(ItemStack par1, World par2, EntityPlayer par3, CallbackInfoReturnable<ItemStack> cir) {
        if(par3 != null && par3.getHealth() < 20F) {
            float healAmount = Math.min(par3.getMaxHealth() * 0.1F, par3.getMaxHealth() - par3.getHealth());
            par3.heal(healAmount);
        }
    }
}

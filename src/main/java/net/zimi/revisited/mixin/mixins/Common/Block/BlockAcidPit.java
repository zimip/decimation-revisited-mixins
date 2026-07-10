package net.zimi.revisited.mixin.mixins.Common.Block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = net.decimation.mod.common.block.props.BlockAcidPit.class, remap = false)
public abstract class BlockAcidPit extends BlockContainer {

    @Shadow
    public abstract boolean playerHasAcid(EntityPlayer entityPlayer);

    @Shadow
    public abstract ItemStack getAcidContainer(EntityPlayer entityPlayer);

    protected BlockAcidPit(Material p_i45386_1_) {
        super(p_i45386_1_);
    }

    @Overwrite
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return false;

        ItemStack heldItem = player.getHeldItem();

        if (heldItem == null) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "I think I can use stuff with this..."));
            return false;
        }

        if (!this.playerHasAcid(player)) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "I think I need acid to use this..."));
            return false;
        }

        if (heldItem.getItem() == deci.aD.k.alD) {
            heldItem.stackSize--;
            player.inventory.addItemStackToInventory(new ItemStack(deci.aD.k.alE, 1));

            if (!player.capabilities.isCreativeMode) {
                ItemStack acidContainer = this.getAcidContainer(player);
                if (acidContainer != null && acidContainer.getItem() instanceof deci.ao.b) {
                    ((deci.ao.b) acidContainer.getItem()).a(acidContainer, 20);
                }
            }

            player.inventoryContainer.detectAndSendChanges();

            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "deci:block.acidpit.use", 1.0F, 1.0F);
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "You used the Acid Pit to create cocaine produce..."));

            if (world.rand.nextFloat() < 0.06F) {
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.explode", 2.0F, 1.5F);

                if (!isWearingFullHazmat(player)) {
                    player.setFire(15);
                }

                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "The chemical reaction has exploded in your face!"));
            }
        }

        return false;
    }

    @Unique
    private boolean isWearingFullHazmat(EntityPlayer player) {
        ItemStack[] armor = player.inventory.armorInventory;

        if (armor[0] == null || armor[1] == null || armor[2] == null || armor[3] == null) {
            return false;
        }

        return armor[3].getItem() == deci.aD.k.aoY && armor[2].getItem() == deci.aD.k.aoZ && armor[1].getItem() == deci.aD.k.apa && armor[0].getItem() == deci.aD.k.apb;
    }
}
package net.zimi.revisited.mixin.mixins.Common.Block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.decimation.mod.common.entity.blockentities.props.TileEntityProp;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

@Mixin(value = net.decimation.mod.common.block.props.BlockProp.class)
public abstract class BlockProp extends Block {

    @Final
    @Shadow public static Map<ChunkCoordinates, ISound> mapSoundPositions;

    @Shadow private boolean hasAmbientSound;
    @Shadow private ResourceLocation ambientSound;
    @Shadow private float ambientSoundVolume;

    @Final
    @Shadow private net.decimation.mod.common.block.props.BlockProp.EnumModelType modelType;
    @Shadow protected String propName;
    @Shadow protected ModelBase propModel;
    @Shadow private String propBModel;
    @Shadow protected boolean isBright;
    @Shadow protected net.decimation.mod.common.block.props.BlockProp.RenderPositions renderPositions;
    @Shadow protected ArrayList<deci.C.a> propFlares;
    @Shadow private float extraRotX;
    @Shadow private float extraRotY;
    @Shadow private float extraRotZ;

    @Shadow private boolean canSitOn;

    protected BlockProp(Material material) {
        super(material);
    }

    /**
     * Facciamo Override del metodo base di Minecraft (breakBlock).
     * Questo viene chiamato SEMPRE (esplosioni, setblock, zombie), garantendo
     * che il suono non rimanga appeso nella RAM del client all'infinito.
     */
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (world.isRemote && this.hasAmbientSound) {
            stopAmbientSound(x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Unique
    @SideOnly(Side.CLIENT)
    private void stopAmbientSound(int x, int y, int z) {
        ChunkCoordinates coords = new ChunkCoordinates(x, y, z);
        ISound sound = mapSoundPositions.remove(coords);
        if (sound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
        }
    }

    /**
     * @author Zimi
     * @reason Rimosso per delegare la pulizia dei suoni al breakBlock, molto più sicuro.
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void onBlockDestroyedByPlayer(World var1, int var2, int var3, int var4, int var5) {

    }

    /**
     * @author Zimi
     * @reason Ottimizzazione dell'uso della HashMap (containsKey invece di controllare il Null)
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
        if (this.hasAmbientSound) {
            ChunkCoordinates var6 = new ChunkCoordinates(var2, var3, var4);
            if (!mapSoundPositions.containsKey(var6)) {
                deci.N.b var7 = new deci.N.b(this.ambientSound, var2, var3, var4, this.ambientSoundVolume);
                Minecraft.getMinecraft().getSoundHandler().playSound(var7);
                mapSoundPositions.put(var6, var7);
            }
        }
    }

    /**
     * @author Zimi
     * @reason Ottimizzazione doppia chiamata al TileEntity, rimozione salvataggio Chunk inutile, e fix Items '.equals()'
     */
    @Overwrite
    public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
        TileEntity te = var1.getTileEntity(var2, var3, var4);

        if (te instanceof TileEntityProp) {
            TileEntityProp var10 = (TileEntityProp) te;
            ItemStack heldStack = var5.getHeldItem();

            if (var5.capabilities.isCreativeMode && heldStack != null && heldStack.getItem() instanceof deci.aw.a) {
                deci.aw.a var12 = (deci.aw.a) heldStack.getItem();
                Item heldItem = heldStack.getItem();

                if (heldItem == deci.aD.k.alv) {
                    var10.rotationX += var12.r(heldStack) ? 5 : -5;
                    if (var10.rotationX < 0L) var10.rotationX = 360L;
                    else if (var10.rotationX > 360L) var10.rotationX = 0L;
                } else if (heldItem == deci.aD.k.alw) {
                    var10.rotationY += var12.r(heldStack) ? 5 : -5;
                    if (var10.rotationY < 0L) var10.rotationY = 360L;
                    else if (var10.rotationY > 360L) var10.rotationY = 0L;
                } else if (heldItem == deci.aD.k.alx) {
                    var10.rotationZ += var12.r(heldStack) ? 5 : -5;
                    if (var10.rotationZ < 0L) var10.rotationZ = 360L;
                    else if (var10.rotationZ > 360L) var10.rotationZ = 0L;
                } else if (heldItem == deci.aD.k.aly) {
                    var10.rotationX = 0L;
                    var10.rotationY = 0L;
                    var10.rotationZ = 0L;
                    var5.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Reset prop rotation!"));
                }

                var1.playSound(var2, var3, var4, "random.pop", 1.0F, 2.0F, false);

                var10.markDirty();
                if(var1.isRemote) {
                    deci.aF.a.a.a.gB().sendToServer(new deci.aE.a.ar(var10.xCoord, var10.yCoord, var10.zCoord, var10.rotationX, var10.rotationY, var10.rotationZ));
                }
                return true;
            }

            if (var5.ridingEntity == null && this.canSitOn) {
                deci.al.a var11 = new deci.al.a(var1, (float)var10.xCoord + 0.5F, (float)var10.yCoord + 0.4F, (float)var10.zCoord + 0.5F);
                var1.spawnEntityInWorld(var11);
                var5.mountEntity(var11);
                var11.riddenByEntity = var5;
                return true;
            }
        }

        return false;
    }

    /**
     * @author Zimi
     * @reason Corretta l'uguaglianza tra Enum usando '==' al posto di '.equals()'
     */
    @Overwrite(remap = false)
    public TileEntity createTileEntity(World var1, int var2) {
        if (this.modelType == net.decimation.mod.common.block.props.BlockProp.EnumModelType.JAVA) {
            return (new TileEntityProp(this.propName, this.propModel, this.isBright, this.renderPositions)).setFlares(this.propFlares).setExtraRotation(this.extraRotX, this.extraRotY, this.extraRotZ);
        } else {
            return this.modelType == net.decimation.mod.common.block.props.BlockProp.EnumModelType.BMODEL
                    ? (new TileEntityProp(this.propName, this.propBModel, this.isBright, this.renderPositions)).setFlares(this.propFlares).setExtraRotation(this.extraRotX, this.extraRotY, this.extraRotZ)
                    : (new TileEntityProp(this.propName, this.propModel, this.isBright, this.renderPositions)).setFlares(this.propFlares).setExtraRotation(this.extraRotX, this.extraRotY, this.extraRotZ);
        }
    }
}
package net.zimi.revisited.mixin.mixins;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import deci.aD.r;
import deci.p.aJ;
import net.decimation.mod.common.block.props.BlockProp;
import net.decimation.mod.common.item.armor.ItemArmorDeci;
import net.minecraft.block.material.Material;
import net.zimi.revisited.Addon;
import net.zimi.revisited.Common.Handler.Decimation.Deci_CommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import static net.zimi.revisited.Addon.*;

@Mixin(value = deci.a.b.class, remap = false)
public class Decimation {
    @Overwrite
    public void a(FMLCommonHandler commonHandler) {
        Deci_CommonHandler deciCommonHandler = new Deci_CommonHandler();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(deciCommonHandler);
        commonHandler.bus().register(deciCommonHandler);
        modItems();
        modBlocks();
        Addon.Network.init();
    }

    @Unique
    private static void modItems() {
        marineBlackMedical = (new ItemArmorDeci(1, 3000, 0.8F, "marineblackmedical")).setUnlocalizedName("marineblackVestMedical").setTextureName("deci:armor/marineblackcoatmedical").setCreativeTab(r.axg);
        GameRegistry.registerItem(marineBlackMedical, marineBlackMedical.getUnlocalizedName().substring(5));
        ghillieUrbanHelm = (new ItemArmorDeci(0, 3000, 1F, "ghillieurban")).setUnlocalizedName("ghillieHelmUrban").setTextureName("deci:armor/ghillieurbanhelm").setCreativeTab(r.axg);
        GameRegistry.registerItem(ghillieUrbanHelm, ghillieUrbanHelm.getUnlocalizedName().substring(5));
        ghillieUrbanVest = (new ItemArmorDeci(1, 3000, 1F, "ghillieurban")).setUnlocalizedName("ghillieVestUrban").setTextureName("deci:armor/ghillieurbancoat").setCreativeTab(r.axg);
        GameRegistry.registerItem(ghillieUrbanVest, ghillieUrbanVest.getUnlocalizedName().substring(5));
        ghillieUrbanPants = (new ItemArmorDeci(2, 3000, 1F, "ghillieurban")).setUnlocalizedName("ghilliePantsUrban").setTextureName("deci:armor/ghillieurbanpants").setCreativeTab(r.axg);
        GameRegistry.registerItem(ghillieUrbanPants, ghillieUrbanPants.getUnlocalizedName().substring(5));
        ghillieUrbanBoots = (new ItemArmorDeci(3, 3000, 1F, "ghillieurban")).setUnlocalizedName("ghillieBootsUrban").setTextureName("deci:armor/ghillieurbanboots").setCreativeTab(r.axg);
        GameRegistry.registerItem(ghillieUrbanBoots, ghillieUrbanBoots.getUnlocalizedName().substring(5));
    }

    @Unique
    private static void modBlocks() {
        trashcan_Burnt = (new BlockProp("trashcan2", "tile.BlockTrashcan2", Material.iron, new aJ())).setPropSize(0.0F, 0.0F, 0.0F, 1.0F, 1.2F, 1.0F).setPropRenderSize(0.0F, 0.0F, 0.0F, 1.0F, 1.5F, 1.0F).setBlockTextureName("deci:prop_trashcan2").setCreativeTab(r.axj);
        GameRegistry.registerBlock(trashcan_Burnt, trashcan_Burnt.getUnlocalizedName().substring(5));
        traschan_Graffiti = (new BlockProp("trashcan3", "tile.BlockTrashcan3", Material.iron, new aJ())).setPropSize(0.0F, 0.0F, 0.0F, 1.0F, 1.2F, 1.0F).setPropRenderSize(0.0F, 0.0F, 0.0F, 1.0F, 1.5F, 1.0F).setBlockTextureName("deci:prop_trashcan3").setCreativeTab(r.axj);
        GameRegistry.registerBlock(traschan_Graffiti, traschan_Graffiti.getUnlocalizedName().substring(5));
    }
}

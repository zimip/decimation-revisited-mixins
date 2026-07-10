package net.zimi.revisited.mixin.mixins.Common.Handler.Loot;

import deci.aB.a;
import deci.aB.c;
import deci.aB.d;
import deci.aB.f;
import net.decimation.mod.common.item.armor.ItemArmorDeci;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(value = {c.class}, remap = false)
public abstract class LootCollection {

    @Final
    @Shadow
    private a aeZ;

    @Final
    @Shadow
    private d[] afa;

    @Unique
    private static final Random decimixin$decimationSharedRand = new Random();

    @Overwrite
    public void a(IInventory var1) {
        int invSize = var1.getSizeInventory();

        List<Integer> availableSlots = new ArrayList<>(invSize);
        for (int i = 0; i < invSize; i++) {
            availableSlots.add(i);
        }

        int maxStacks = this.aeZ.getUpperBound();

        int minStacks = Math.max(1, (int) (maxStacks * 0.6));

        int numStacks;
        if (maxStacks <= minStacks) {
            numStacks = maxStacks;
        } else {
            numStacks = minStacks + decimixin$decimationSharedRand.nextInt((maxStacks - minStacks) + 1);
        }

        f<d> var9 = new f<>(decimixin$decimationSharedRand);

        for (d var13 : this.afa) {
            if (var13.stack != null) {
                Item item = var13.stack.getItem();
                if (item instanceof deci.ao.d) {
                    var9.a(((deci.ao.d) item).getLootChance(), var13);
                } else if (item instanceof deci.ao.c) {
                    var9.a(((deci.ao.c) item).getLootChance(), var13);
                } else if (item instanceof ItemArmorDeci) {
                    var9.a(((ItemArmorDeci) item).getLootChance(), var13);
                } else {
                    var9.a(0.9D, var13);
                }
            }
        }

        for (int var7 = 0; var7 < numStacks && !availableSlots.isEmpty(); ++var7) {
            d var8 = var9.next();
            ItemStack var16 = var8.c(decimixin$decimationSharedRand);

            int randomListIndex = decimixin$decimationSharedRand.nextInt(availableSlots.size());
            int actualSlot = availableSlots.remove(randomListIndex);

            var1.setInventorySlotContents(actualSlot, var16);
        }

        for (int emptySlot : availableSlots) {
            var1.setInventorySlotContents(emptySlot, null);
        }
    }
}
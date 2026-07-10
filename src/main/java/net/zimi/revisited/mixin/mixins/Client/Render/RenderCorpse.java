package net.zimi.revisited.mixin.mixins.Client.Render;

import com.mojang.authlib.GameProfile;
import deci.q.c;
import deci.q.l;
import net.decimation.mod.common.item.armor.ItemArmorDeci;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.zimi.revisited.Client.Handler.CorpseSkinHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(value = deci.J.x.class)
public abstract class RenderCorpse extends RenderBiped {
    @Unique private EntityOtherPlayerMP dummyEntity;
    @Unique private c dummyModel;

    public RenderCorpse() {
        super(new l(), 0.5F);
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float pitch, float yaw, float partialTicks) {
        super.rotateCorpse(entity, pitch, yaw, partialTicks);
        GL11.glTranslatef(0.0F, 0.15F, 0.0F);
    }

    @Overwrite
    protected void renderModel(EntityLivingBase entity, float f1, float f2, float f3, float f4, float f5, float f6) {
        f1 = 0.0F; f2 = 0.0F; f3 = 0.0F; f4 = 0.0F; f5 = 0.0F;

        this.mainModel.isRiding = false;
        if (this.mainModel instanceof ModelBiped) {
            ModelBiped bipedModel = (ModelBiped) this.mainModel;
            bipedModel.isSneak = false;
            bipedModel.aimedBow = false;
        }

        if (entity instanceof deci.ae.a) {
            String ownerName = ((deci.ae.a) entity).ef();
            ResourceLocation skin = CorpseSkinHelper.getSkin(ownerName);
            this.bindTexture(skin);
        } else {
            this.bindTexture(this.getEntityTexture(entity));
        }

        // ==========================================
        // 1. RENDER CORPO NUDO
        // ==========================================
        if (!entity.isInvisible()) {
            this.mainModel.render(entity, f1, f2, f3, f4, f5, f6);
        } else {
            this.mainModel.setRotationAngles(f1, f2, f3, f4, f5, f6, entity);
            return;
        }

        // Inizializziamo le nostre variabili di supporto (Lazy Load per non pesare sulla RAM)
        if (dummyEntity == null && entity.worldObj != null) {
            dummyEntity = new EntityOtherPlayerMP(entity.worldObj, new GameProfile(UUID.randomUUID(), "CorpseDummy"));
        }
        if (dummyModel == null) {
            dummyModel = new c();
        }

        // ==========================================
        // 2. RENDER ARMATURA
        // ==========================================
        for (int armorType = 0; armorType <= 3; armorType++) {
            ItemStack armorStack = getArmorByType(entity, armorType);

            if (armorStack != null && armorStack.getItem() instanceof ItemArmorDeci) {
                ItemArmorDeci itemArmorDeci = (ItemArmorDeci) armorStack.getItem();
                loadArmorModel(itemArmorDeci);

                if (itemArmorDeci.armor != null) {
                    deci.n.e ma = itemArmorDeci.armor;
                    ma.type = 3 - armorType;

                    ma.bipedHead.showModel = (armorType == 0);
                    ma.bipedHeadwear.showModel = (armorType == 0);
                    ma.bipedBody.showModel = (armorType == 1 || armorType == 2);
                    ma.bipedRightArm.showModel = (armorType == 1);
                    ma.bipedLeftArm.showModel = (armorType == 1);
                    ma.bipedRightLeg.showModel = (armorType == 2 || armorType == 3);
                    ma.bipedLeftLeg.showModel = (armorType == 2 || armorType == 3);

                    ma.isSneak = false;
                    ma.isRiding = false;

                    this.bindTexture(new ResourceLocation("deci", "textures/model/armor/" + itemArmorDeci.modelName + ".png"));

                    // ==========================================
                    // IL PRESTITO (Sincronizzazione Angoli Perfetta)
                    syncModelRot((ModelBiped) this.mainModel, dummyModel);

                    ModelBiped originalDeciModel = deci.J.e.SS;
                    deci.J.e.SS = dummyModel;

                    GL11.glPushMatrix();

                    // FIX Z-FIGHTING ARMATURA
                    float scale = 1.01F;
                    GL11.glScalef(scale, scale, scale);

                    // Eseguiamo il render passando il nostro fakePlayer Vanilla. 
                    // Passerà i controlli "instanceof EntityPlayer" senza crashare e senza usare thePlayer!
                    if (dummyEntity != null) {
                        ma.render(dummyEntity, f1, f2, f3, f4, f5, f6);
                    }

                    GL11.glPopMatrix();

                    deci.J.e.SS = originalDeciModel;
                    // ==========================================
                }
            }
        }

        // ==========================================
        // 3. RENDER BACKPACK (SLOT 7)
        // ==========================================
        if (entity instanceof deci.ae.a) {
            ItemStack backpackStack = entity.getEquipmentInSlot(7);

            if (backpackStack != null && backpackStack.getItem() instanceof deci.av.a) {
                deci.av.a backpack = (deci.av.a) backpackStack.getItem();

                if (backpack.model == null) {
                    backpack.model = deci.n.g.a(new ResourceLocation("deci", "models/backpacks/" + backpack.modelName + ".bmodel"));
                }

                if (backpack.model != null && dummyEntity != null) {
                    String texName = backpack.getUnlocalizedName().substring(5);
                    this.bindTexture(new ResourceLocation("deci", "textures/model/backpacks/" + texName + ".png"));

                    GL11.glPushMatrix();

                    if (texName.contains("HugeMilitaryPack")) {
                        GL11.glScalef(0.4F, 0.4F, 0.4F);
                        GL11.glTranslatef(0.0F, 0.2F, -0.05F);
                    }

                    // Scaliamo per evitare Z-Fighting contro l'armatura
                    float bpScale = 1.02F;
                    GL11.glScalef(bpScale, bpScale, bpScale);

                    // 7 argomenti: 1.0F finale per la scala dello zaino
                    backpack.model.render(dummyEntity, f1, f2, f3, f4, f5, 1.0F);

                    GL11.glPopMatrix();
                }
            }
        }
    }

    @Overwrite
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks) {
        ItemStack armorStack = getArmorByType(entity, pass);
        if (armorStack != null && armorStack.getItem() instanceof ItemArmorDeci) {
            return -1;
        }
        if (armorStack != null && armorStack.getItem() instanceof ItemArmor) {
            this.bindTexture(RenderBiped.getArmorResource(entity, armorStack, pass, null));
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            return 1;
        }
        return -1;
    }

    @Unique
    private void syncModelRot(ModelBiped source, c target) {
        target.bipedHead.rotateAngleX = source.bipedHead.rotateAngleX;
        target.bipedHead.rotateAngleY = source.bipedHead.rotateAngleY;
        target.bipedHead.rotateAngleZ = source.bipedHead.rotateAngleZ;
        target.bipedBody.rotateAngleX = source.bipedBody.rotateAngleX;
        target.bipedBody.rotateAngleY = source.bipedBody.rotateAngleY;
        target.bipedBody.rotateAngleZ = source.bipedBody.rotateAngleZ;
        target.bipedRightArm.rotateAngleX = source.bipedRightArm.rotateAngleX;
        target.bipedRightArm.rotateAngleY = source.bipedRightArm.rotateAngleY;
        target.bipedRightArm.rotateAngleZ = source.bipedRightArm.rotateAngleZ;
        target.bipedLeftArm.rotateAngleX = source.bipedLeftArm.rotateAngleX;
        target.bipedLeftArm.rotateAngleY = source.bipedLeftArm.rotateAngleY;
        target.bipedLeftArm.rotateAngleZ = source.bipedLeftArm.rotateAngleZ;
        target.bipedRightLeg.rotateAngleX = source.bipedRightLeg.rotateAngleX;
        target.bipedRightLeg.rotateAngleY = source.bipedRightLeg.rotateAngleY;
        target.bipedRightLeg.rotateAngleZ = source.bipedRightLeg.rotateAngleZ;
        target.bipedLeftLeg.rotateAngleX = source.bipedLeftLeg.rotateAngleX;
        target.bipedLeftLeg.rotateAngleY = source.bipedLeftLeg.rotateAngleY;
        target.bipedLeftLeg.rotateAngleZ = source.bipedLeftLeg.rotateAngleZ;

        // Azzeriamo forzatamente le ginocchia del modello Decimation per evitare sfasamenti
        target.EP.rotateAngleX = 0.0F;
        target.EQ.rotateAngleX = 0.0F;
    }

    @Unique
    private void loadArmorModel(ItemArmorDeci iad) {
        if (iad.armor == null || iad.armor.kw == null || !iad.armor.kw.path.contains(iad.modelName)) {
            iad.armor = new deci.n.e();
            iad.armor.kw = deci.n.g.a(new ResourceLocation("deci", "models/armor/full/" + iad.modelName + ".bmodel"));
        }
    }

    @Unique
    private ItemStack getArmorByType(EntityLivingBase entity, int slotType) {
        if (entity instanceof deci.ae.a) {
            if (slotType >= 0 && slotType <= 3) {
                ItemStack stack = entity.getEquipmentInSlot(4 - slotType);
                if (stack != null && stack.getItem() instanceof ItemArmor) {
                    if (((ItemArmor) stack.getItem()).armorType == slotType) {
                        return stack;
                    }
                }
                return null;
            }
        }

        if (entity instanceof IInventory) {
            IInventory inv = (IInventory) entity;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack temp = inv.getStackInSlot(i);
                if (temp != null && temp.getItem() instanceof ItemArmor) {
                    if (slotType <= 3 && ((ItemArmor) temp.getItem()).armorType == slotType) return temp;
                }
            }
        }
        return null;
    }
}
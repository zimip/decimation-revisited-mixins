package net.zimi.revisited.mixin.mixins.Client.Render;

import deci.ay.i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = deci.K.b.class, remap = false)
public abstract class RenderGun implements IItemRenderer {

    @ModifyArg(method = "a(Ldeci/ay/i;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 7), index = 1)
    public float renderReddot(float x) {
        return 0.0105F;
    }

    @Inject(method = "a(Ldeci/ay/i;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void fixOverlayAttachmentLeak(i var1, ItemStack var2, CallbackInfo ci) {
        if (GL11.glGetBoolean(GL11.GL_BLEND)) {
            GL11.glPopMatrix();
            ci.cancel();
        } else {
            deci.b.g.E();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Redirect(method = "a(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V", at = @At(value = "INVOKE", target = "Ldeci/ay/i;C(Lnet/minecraft/item/ItemStack;)I"), remap = false)
    private int cancelMudRender(i gun, ItemStack stack) {
        return 0;
    }

    @Redirect(method = "a(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V", at = @At(value = "INVOKE", target = "Ldeci/ay/i;D(Lnet/minecraft/item/ItemStack;)I"), remap = false)
    private int cancelBloodRender(i gun, ItemStack stack) {
        return 0;
    }

    @ModifyArg(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V", ordinal = 0), index = 0, remap = true)
    private ResourceLocation fixArmSkin1(ResourceLocation originalTexture) {
        if (originalTexture == deci.K.b.TJ) {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            if (player != null) {

                return player.getLocationSkin();
            }
        }
        return originalTexture;
    }

    @ModifyArg(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V", ordinal = 1), index = 0, remap = true)
    private ResourceLocation fixArmSkin2(ResourceLocation originalTexture) {
        if (originalTexture == deci.K.b.TJ) {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            if (player != null) {

                return player.getLocationSkin();
            }
        }
        return originalTexture;
    }
}
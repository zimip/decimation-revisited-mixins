package net.zimi.revisited.mixin.mixins.Client.Render;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(value = net.decimation.mod.client.cape.b.class, remap = false)
public class RenderCape {
    @Shadow public static ResourceLocation dg;
    @Shadow public static ArrayList<String> dh;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addCustomDevUUID(CallbackInfo ci) {
        dh.add("ad7f22b7-c679-4428-8d59-9e4caacc9f4f");
        dh.add("1547e9fb-f387-4d58-9bb7-1fb8ebcbd0b2");
    }

    /**
     * @author zim
     * @reason Refactored cape rendering logic to prioritize dev capes
     */
    @SubscribeEvent
    @Overwrite()
    public void a(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.entityPlayer;
        Minecraft mc = FMLClientHandler.instance().getClient();

        if (player.getGameProfile() == null || player.isInvisible()) return;
        if (player.equals(mc.thePlayer) && mc.gameSettings.thirdPersonView == 0) return;

        deci.Q.b data = deci.Q.b.e(player);
        if (data == null || !data.cT()) return;

        String uuidStr = player.getGameProfile().getId().toString();
        boolean isDev = dh.contains(uuidStr);

        if (isDev) {
            mc.getTextureManager().bindTexture(dg);
            renderCape(event, player, event.partialRenderTick);
        } else if (data.cR()) {
            mc.getTextureManager().bindTexture(
                    new ResourceLocation("deci:textures/model/cape/cape_supporter_" + data.cU() + ".png")
            );
            renderCape(event, player, event.partialRenderTick);
        }
    }

    @Unique
    private void renderCape(RenderPlayerEvent.Specials.Post event, EntityPlayer player, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 0.125F);

        double d3 = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * partialTicks - (player.prevPosX + (player.posX - player.prevPosX) * partialTicks);
        double d4 = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * partialTicks - (player.prevPosY + (player.posY - player.prevPosY) * partialTicks);
        double d0 = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * partialTicks - (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks);

        float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        double d1 = MathHelper.sin(yawOffset * (float) Math.PI / 180.0F);
        double d2 = -MathHelper.cos(yawOffset * (float) Math.PI / 180.0F);

        float f5 = MathHelper.clamp_float((float) d4 * 10.0F, -6.0F, 32.0F);

        float f6 = Math.max(0.0F, (float) (d3 * d1 + d0 * d2) * 100.0F);
        float f7 = (float) (d3 * d2 - d0 * d1) * 100.0F;

        float cameraYaw = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
        f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * cameraYaw;

        if (player.isSneaking()) f5 += 25.0F;

        GL11.glRotatef(6.0F + f6 / 2.0F + f5, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(f7 / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-f7 / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

        event.renderer.modelBipedMain.renderCloak(0.0625F);
        GL11.glPopMatrix();
    }
}
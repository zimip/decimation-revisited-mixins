package net.zimi.revisited.mixin.mixins.Client.Handlers;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.zimi.revisited.Client.Handler.LaserDotRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = deci.c.c.class, remap = false)
public class ClientHandlers {

    @Overwrite
    private void N() {
    }

    @Inject(method = "b", at = @At("HEAD"))
    public void tickEvent(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        this.tick();
    }

    @Redirect(method = "b", at = @At(value = "INVOKE", target = "Ldeci/b/i;a(Lnet/minecraft/world/World;)F", ordinal = 0))
    public float redirectFogCall_o0(World world) {
        float target = deci.b.i.a(world);
        float current = deci.b.i.bL;
        return (current - target < 4.0E-4F && current > target) ? current : target;
    }

    @Redirect(method = "b", at = @At(value = "INVOKE", target = "Ldeci/b/i;a(Lnet/minecraft/world/World;)F", ordinal = 1))
    public float redirectFogCall_o1(World world) {
        float target = deci.b.i.a(world);
        float current = deci.b.i.bL;
        return (target - current < 4.0E-4F && current < target) ? current : target;
    }

    @Unique
    private void tick() {
        List<LaserDotRenderer.DotData> dotList = LaserDotRenderer.dots;
        for (int i = dotList.size() - 1; i >= 0; i--) {
            LaserDotRenderer.DotData dot = dotList.get(i);
            if (--dot.ticksLeft <= 0) {
                dotList.remove(i);
            }
        }
    }

    @ModifyArg(method = "a(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$Post;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawRect(IIIII)V", ordinal = 2), index = 4, remap = true)
    private int darkenOnLowHp(int originalColor) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null || player.capabilities.isCreativeMode) return originalColor;

        float hp = player.getHealth();
        if (hp >= 5.0F) return originalColor;

        float t = MathHelper.clamp_float(1.0F - (hp / 5.0F), 0.0F, 1.0F);
        return ((int) (t * 230.0F)) << 24;
    }

    @Inject(method = "a(Lnet/minecraftforge/client/event/sound/PlaySoundEvent17;)V", at = @At("HEAD"))
    public void onPlaySound(PlaySoundEvent17 event, CallbackInfo ci) {
        if (event == null || event.sound == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;

        float hp = player.getHealth();
        if (hp >= 5.0F) return;

        float t = MathHelper.clamp_float(1.0F - (hp / 5.0F), 0.0F, 1.0F);

        ISound original = event.sound;
        float newPitch = original.getPitch() * (1.0F - t * 0.5F);
        float newVolume = original.getVolume() * (1.0F + t * 0.25F);

        event.result = new PositionedSoundRecord(original.getPositionedSoundLocation(), newVolume, newPitch, original.getXPosF(), original.getYPosF(), original.getZPosF());
    }
}
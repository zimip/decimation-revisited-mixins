package net.zimi.revisited.mixin.mixins.Server.Screenshot;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.decimation.mod.common.common_commands.CommandBaseDeci;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Mixin(value = net.decimation.mod.server.screenshot.ScreenshotCommand.class, remap = false)
public abstract class ScreenshotCommand extends CommandBaseDeci {
    @Overwrite
    private static void handleResponse(EntityPlayerMP admin, EntityPlayerMP target, BufferedImage image, Throwable error) {
        if (error == null && image != null) {
            try {
                ByteBuf byteBuf = Unpooled.buffer(image.getWidth() * image.getHeight() * 2);

                ImageIO.write(image, "JPEG", new ByteBufOutputStream(byteBuf));

                int maxChunk = 2097047;
                int totalChunks = MathHelper.ceiling_float_int((float) byteBuf.readableBytes() / maxChunk);

                for (byte b = 0; b < totalChunks; b++) {
                    int startIndex = b * maxChunk;
                    int length = Math.min(byteBuf.readableBytes() - startIndex, maxChunk);
                    ByteBuf slice = byteBuf.slice(startIndex, length);

                    deci.aF.a.a.a.gB().sendTo(new deci.aE.a.ad(b, totalChunks, slice), admin);
                }
            } catch (Throwable t) {
                error = t;
            }
        }

        if (error != null) {
            ChatComponentText msg = new ChatComponentText("Error during screenshot: " + error.getMessage());
            msg.getChatStyle().setColor(EnumChatFormatting.RED);
            admin.addChatMessage(msg);
            error.printStackTrace();
        }
    }
}
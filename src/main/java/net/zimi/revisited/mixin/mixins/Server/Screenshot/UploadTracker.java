package net.zimi.revisited.mixin.mixins.Server.Screenshot;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import net.minecraftforge.common.IExtendedEntityProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Mixin(value = net.decimation.mod.server.screenshot.a.class, remap = false)
public abstract class UploadTracker implements IExtendedEntityProperties {

    @Shadow private net.decimation.mod.server.screenshot.a.a aBj;
    @Shadow private ByteBuf[] ayT;
    @Shadow private CompletableFuture<BufferedImage> aBl;
    @Shadow private long aBk;

    @Overwrite
    public boolean a(int totalChunks, int chunkIndex, ByteBuf chunkData) {
        if (chunkIndex >= totalChunks) {
            if (chunkData != null) chunkData.release();
            return false;
        }
        if (this.aBj != net.decimation.mod.server.screenshot.a.a.REQUESTED && this.aBj != net.decimation.mod.server.screenshot.a.a.UPLOADING) {
            if (chunkData != null) chunkData.release();
            return false;
        }

        if (this.aBj == net.decimation.mod.server.screenshot.a.a.REQUESTED) {
            this.ayT = new ByteBuf[totalChunks];
            this.aBj = net.decimation.mod.server.screenshot.a.a.UPLOADING;
        }

        this.ayT[chunkIndex] = chunkData;

        for (ByteBuf buf : this.ayT) {
            if (buf == null) return true;
        }

        final ByteBuf fullBuffer = Unpooled.wrappedBuffer(this.ayT);
        this.ayT = null;
        this.aBj = net.decimation.mod.server.screenshot.a.a.IDLE;

        final CompletableFuture<BufferedImage> future = this.aBl;
        this.aBl = null;

        ForkJoinPool.commonPool().execute(() -> {
            try (ByteBufInputStream bbis = new ByteBufInputStream(fullBuffer)) {
                BufferedImage image = ImageIO.read(bbis);
                future.complete(image);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            } finally {
                fullBuffer.release();
            }
        });

        return true;
    }

    @Overwrite
    public void s(long currentTime) {
        if (this.aBj != net.decimation.mod.server.screenshot.a.a.IDLE && (this.aBk - currentTime < 0L)) {

            if (this.ayT != null) {
                for (ByteBuf buf : this.ayT) {
                    if (buf != null && buf.refCnt() > 0) {
                        buf.release();
                    }
                }
                this.ayT = null;
            }

            this.aBj = net.decimation.mod.server.screenshot.a.a.IDLE;
            if (this.aBl != null) {
                this.aBl.completeExceptionally(new RuntimeException("Screenshot timeout"));
                this.aBl = null;
            }
        }
    }
}
package net.zimi.revisited.Client.Handler;

import net.minecraft.client.model.PositionTextureVertex;

public interface IOptimizedQuad {
    PositionTextureVertex[] getVertices();
    boolean isInvertNormal();
}
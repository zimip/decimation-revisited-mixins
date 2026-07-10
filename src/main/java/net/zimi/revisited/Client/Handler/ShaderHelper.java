package net.zimi.revisited.Client.Handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ShaderHelper {
    private static final Logger LOGGER = LogManager.getLogger("revisited_shading");
    public static int spotlightProgram = 0;
    public static int thermalProgram = 0;

    public static void initShaders() {
        spotlightProgram = loadProgram("spotlight");
        thermalProgram = loadProgram("thermal");
    }

    private static int loadProgram(String name) {
        ResourceLocation vertLoc = new ResourceLocation("deci", "shaders/generic_vertex.vsh");
        ResourceLocation fragLoc = new ResourceLocation("deci", "shaders/" + name + ".fsh");

        int vertShader = createShader(vertLoc, ARBVertexShader.GL_VERTEX_SHADER_ARB);
        int fragShader = createShader(fragLoc, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

        if (vertShader == 0 || fragShader == 0) return 0;

        int program = ARBShaderObjects.glCreateProgramObjectARB();
        ARBShaderObjects.glAttachObjectARB(program, vertShader);
        ARBShaderObjects.glAttachObjectARB(program, fragShader);
        ARBShaderObjects.glLinkProgramARB(program);
        ARBShaderObjects.glValidateProgramARB(program);

        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            LOGGER.error("Error with shader linking [{}]: {}", name, getLogInfo(program));
            return 0;
        }
        return program;
    }

    public static void renderShader(int programId, int depthTexId, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        int width = mc.displayWidth;
        int height = mc.displayHeight;

        if (width <= 0 || height <= 0 || programId == 0) return; // FIX 1281

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, width, height, 0);

        GL11.glPushMatrix();
        setupOrtho(width, height);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        ARBShaderObjects.glUseProgramObjectARB(programId);

        int worldTimeLoc = ARBShaderObjects.glGetUniformLocationARB(programId, "worldTime");
        float totalTicks = (float) mc.thePlayer.ticksExisted + partialTicks;
        if (worldTimeLoc != -1) ARBShaderObjects.glUniform1fARB(worldTimeLoc, totalTicks * 0.05F);

        int resLoc = ARBShaderObjects.glGetUniformLocationARB(programId, "resolution");
        if (resLoc != -1) ARBShaderObjects.glUniform2fARB(resLoc, (float) width, (float) height);

        drawFullScreenQuad(width, height);

        ARBShaderObjects.glUseProgramObjectARB(0);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        restoreProjection();
        GL11.glPopMatrix();
    }

    public static void drawFullScreenQuad(int width, int height) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, height, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(width, height, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(width, 0.0D, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
        tessellator.draw();
    }

    public static void setupOrtho(int width, int height) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, width, height, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public static void restoreProjection() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private static int createShader(ResourceLocation location, int shaderType) {
        int shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
        try {
            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
            String shaderCode = IOUtils.toString(stream, StandardCharsets.UTF_8);
            ARBShaderObjects.glShaderSourceARB(shader, shaderCode);
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                return 0;
        } catch (Exception e) {
            return 0;
        }
        return shader;
    }

    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
}
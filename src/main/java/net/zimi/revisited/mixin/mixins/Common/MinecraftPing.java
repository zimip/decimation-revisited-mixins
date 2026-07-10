//package net.zimi.revisited.mixin.mixins.Common;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import net.decimation.mod.utilities.ping.MinecraftPingOptions;
//import net.decimation.mod.utilities.ping.MinecraftPingReply;
//import net.decimation.mod.utilities.ping.MinecraftPingUtil;
//import net.minecraft.util.IChatComponent;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Overwrite;
//import org.spongepowered.asm.mixin.Shadow;
//
//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//
//@Mixin(value = net.decimation.mod.utilities.ping.MinecraftPing.class, remap = false)
//public class MinecraftPing {
//    @Shadow
//    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(IChatComponent.class, new IChatComponent.Serializer()).create();
//
//    @Overwrite
//    public MinecraftPingReply getPing(MinecraftPingOptions options) throws IOException {
//        MinecraftPingUtil.validate(options.getHostname(), "Hostname cannot be null!");
//
//        long pingStart;
//        long pingEnd;
//        String json;
//
//        try (Socket socket = new Socket()) {
//            socket.connect(new InetSocketAddress(options.getHostname(), options.getPort()), options.getTimeout());
//
//            try (DataInputStream in = new DataInputStream(socket.getInputStream());
//                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//                 ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();
//                 DataOutputStream handshake = new DataOutputStream(handshakeBytes)) {
//
//                handshake.writeByte(MinecraftPingUtil.PACKET_HANDSHAKE);
//                MinecraftPingUtil.writeVarInt(handshake, MinecraftPingUtil.PROTOCOL_VERSION);
//                MinecraftPingUtil.writeVarInt(handshake, options.getHostname().length());
//                handshake.writeBytes(options.getHostname());
//                handshake.writeShort(options.getPort());
//                MinecraftPingUtil.writeVarInt(handshake, MinecraftPingUtil.STATUS_HANDSHAKE);
//
//                MinecraftPingUtil.writeVarInt(out, handshakeBytes.size());
//                out.write(handshakeBytes.toByteArray());
//
//                out.writeByte(1);
//                out.writeByte(MinecraftPingUtil.PACKET_STATUSREQUEST);
//
//                pingStart = System.currentTimeMillis();
//
//                MinecraftPingUtil.readVarInt(in);
//                int jsonLength = MinecraftPingUtil.readVarInt(in);
//                byte[] jsonData = new byte[jsonLength];
//                in.readFully(jsonData);
//                json = new String(jsonData, options.getCharset());
//
//                out.writeByte(9);
//                out.writeByte(MinecraftPingUtil.PACKET_PING);
//                out.writeLong(System.currentTimeMillis());
//
//                pingEnd = System.currentTimeMillis();
//                MinecraftPingUtil.readVarInt(in);
//            }
//        }
//
//        try {
//            MinecraftPingReply reply = GSON.fromJson(json, MinecraftPingReply.class);
//            reply.setPing((pingEnd - pingStart) / 4L);
//            return reply;
//        } catch (Exception e) {
//            throw new IOException("Failed to parse server response: ", e);
//        }
//    }
//}
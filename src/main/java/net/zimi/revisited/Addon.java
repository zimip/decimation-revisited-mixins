package net.zimi.revisited;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import deci.K.b;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.zimi.revisited.Client.Handler.ClientVariables;
import net.zimi.revisited.Client.Handler.LaserDotRenderer;
import net.zimi.revisited.Common.Entity.AIInvestigateSound;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Mod(modid = Addon.MOD_ID, version = Addon.MOD_VERSION, dependencies = "required-after:deci@[1.21.10,);")
public class Addon {
    public static final String MOD_ID = "revisited";
    public static final String MOD_VERSION = "0.0.1";

    public static Item marineBlackMedical;
    public static Item ghillieUrbanHelm;
    public static Item ghillieUrbanVest;
    public static Item ghillieUrbanPants;
    public static Item ghillieUrbanBoots;
    public static Item thermalScope;

    public static Block traschan_Graffiti, trashcan_Burnt;

    // ==============================
    //          NETWORK
    // ==============================
    public static final class Network {

        public static SimpleNetworkWrapper PACKET = new SimpleNetworkWrapper(Addon.MOD_ID);

        public static void init() {
            int discriminator = 0;

            PACKET.registerMessage(Message_WorldProperties.Handler_WorldProperties.class, Message_WorldProperties.class, discriminator++, Side.CLIENT);
            PACKET.registerMessage(Message_LaserDot.Handler.class, Message_LaserDot.class, discriminator++, Side.CLIENT);
            PACKET.registerMessage(Message_PlaySoundRequest.Handler_PlaySoundRequest.class, Message_PlaySoundRequest.class, discriminator++, Side.SERVER);
            PACKET.registerMessage(Message_PlaySoundS2C.Handler_PlaySoundS2C.class, Message_PlaySoundS2C.class, discriminator++, Side.CLIENT);
            PACKET.registerMessage(Message_LaserDotC2S.Handler_LaserDot.class, Message_LaserDotC2S.class, discriminator++, Side.SERVER);
            PACKET.registerMessage(Message_StopReloadC2S.Handler_StopReloadC2S.class, Message_StopReloadC2S.class, discriminator++, Side.SERVER);
            PACKET.registerMessage(Message_StopReloadS2C.Handler_StopReloadS2C.class, Message_StopReloadS2C.class, discriminator++, Side.CLIENT);
            PACKET.registerMessage(Message_PlayerShootC2S.Handler.class, Message_PlayerShootC2S.class, discriminator++, Side.SERVER);
            PACKET.registerMessage(Message_RenderTracer.Handler.class, Message_RenderTracer.class, discriminator++, Side.CLIENT);
            PACKET.registerMessage(Message_InfectionSpasm.Handler.class, Message_InfectionSpasm.class, discriminator++, Side.CLIENT);
            PACKET.registerMessage(Message_SyncStance.Handler.class, Message_SyncStance.class, discriminator++, Side.CLIENT);

        }

        public static class Message_WorldProperties implements IMessage {
            private float density;

            public Message_WorldProperties(float setDensity) {
                density = setDensity;
            }

            public Message_WorldProperties() {
            }

            @Override
            public void toBytes(ByteBuf buf) {
                buf.writeFloat(density);
            }

            @Override
            public void fromBytes(ByteBuf buf) {
                this.density = buf.readFloat();
            }

            public static class Handler_WorldProperties implements IMessageHandler<Message_WorldProperties, IMessage> {
                @Override
                @SideOnly(Side.CLIENT)
                public IMessage onMessage(Message_WorldProperties message, MessageContext ctx) {
                    ClientVariables.setFogDensity(message.density);
                    return null;
                }
            }
        }

        public static class Message_LaserDotC2S implements IMessage {
            private int targetEntityId;
            private double x, y, z;
            private float pt, width, height;

            public Message_LaserDotC2S(int targetEntityId, double x, double y, double z, float pt, float width, float height) {
                this.targetEntityId = targetEntityId;
                this.x = x;
                this.y = y;
                this.z = z;
                this.pt = pt;
                this.width = width;
                this.height = height;
            }

            public Message_LaserDotC2S() {
            }

            @Override
            public void toBytes(ByteBuf buf) {
                buf.writeInt(targetEntityId);
                buf.writeDouble(x);
                buf.writeDouble(y);
                buf.writeDouble(z);
                buf.writeFloat(pt);
                buf.writeFloat(width);
                buf.writeFloat(height);
            }

            @Override
            public void fromBytes(ByteBuf buf) {
                this.targetEntityId = buf.readInt();
                this.x = buf.readDouble();
                this.y = buf.readDouble();
                this.z = buf.readDouble();
                this.pt = buf.readFloat();
                this.width = buf.readFloat();
                this.height = buf.readFloat();
            }

            public static class Handler_LaserDot implements IMessageHandler<Message_LaserDotC2S, IMessage> {
                @Override
                public IMessage onMessage(Message_LaserDotC2S message, MessageContext ctx) {
                    Entity target = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.targetEntityId);

                    if (target instanceof EntityPlayerMP) {
                        EntityPlayerMP targetPlayer = (EntityPlayerMP) target;
                        Network.PACKET.sendTo(new Message_LaserDot(message.x, message.y, message.z, message.pt, message.width, message.height), targetPlayer);
                    }

                    return null;
                }
            }
        }

        public static class Message_LaserDot implements IMessage {
            private double x, y, z;
            private float pt, width, height;

            public Message_LaserDot(double x, double y, double z, float pt, float width, float height) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.pt = pt;
                this.width = width;
                this.height = height;
            }

            public Message_LaserDot() {

            }

            @Override
            public void toBytes(ByteBuf buf) {
                buf.writeDouble(x);
                buf.writeDouble(y);
                buf.writeDouble(z);
                buf.writeFloat(pt);
                buf.writeFloat(width);
                buf.writeFloat(height);
            }

            @Override
            public void fromBytes(ByteBuf buf) {
                this.x = buf.readDouble();
                this.y = buf.readDouble();
                this.z = buf.readDouble();
                this.pt = buf.readFloat();
                this.width = buf.readFloat();
                this.height = buf.readFloat();
            }

            public static class Handler implements IMessageHandler<Message_LaserDot, IMessage> {
                @Override
                @SideOnly(Side.CLIENT)
                public IMessage onMessage(Message_LaserDot message, MessageContext ctx) {
                    LaserDotRenderer.addDot(message.x, message.y, message.z, message.pt, 1F, 1F);

                    return null;
                }
            }
        }

        public static class Message_PlaySoundRequest implements IMessage {
            private String sound;
            private float vol;
            private int x, y, z, entityID, mode;

            public Message_PlaySoundRequest() {
            }

            public Message_PlaySoundRequest(int entityID, String sound, float volume) {
                this.mode = 0;
                this.entityID = entityID;
                this.sound = sound;
                this.vol = volume;
            }

            public Message_PlaySoundRequest(int x, int y, int z, String sound, float volume) {
                this.mode = 1;
                this.x = x;
                this.y = y;
                this.z = z;
                this.sound = sound;
                this.vol = volume;
            }

            public void fromBytes(ByteBuf buf) {
                this.mode = buf.readByte();
                this.sound = ByteBufUtils.readUTF8String(buf);
                this.vol = buf.readFloat();
                if (this.mode == 0) {
                    this.entityID = buf.readInt();
                } else {
                    this.x = buf.readInt();
                    this.y = buf.readInt();
                    this.z = buf.readInt();
                }
            }

            public void toBytes(ByteBuf buf) {
                buf.writeByte(this.mode);
                ByteBufUtils.writeUTF8String(buf, this.sound);
                buf.writeFloat(this.vol);
                if (this.mode == 0) {
                    buf.writeInt(this.entityID);
                } else {
                    buf.writeInt(this.x);
                    buf.writeInt(this.y);
                    buf.writeInt(this.z);
                }
            }

            public static class Handler_PlaySoundRequest implements IMessageHandler<Message_PlaySoundRequest, IMessage> {
                public IMessage onMessage(Message_PlaySoundRequest message, MessageContext ctx) {
                    if (message.mode == 0) {
                        Network.PACKET.sendToAll(new Message_PlaySoundS2C(message.entityID, message.sound, message.vol));
                    } else {
                        Network.PACKET.sendToAll(new Message_PlaySoundS2C(message.x, message.y, message.z, message.sound, message.vol));
                    }
                    return null;
                }
            }
        }

        public static class Message_PlaySoundS2C implements IMessage {
            private boolean useCoords;

            private int entityID;

            private int x;

            private int y;

            private int z;

            private String sound;

            private float vol;

            public Message_PlaySoundS2C() {
            }

            public Message_PlaySoundS2C(int entityID, String sound, float volume) {
                this.useCoords = false;
                this.entityID = entityID;
                this.sound = sound;
                this.vol = volume;
            }

            public Message_PlaySoundS2C(int x, int y, int z, String sound, float volume) {
                this.useCoords = true;
                this.x = x;
                this.y = y;
                this.z = z;
                this.sound = sound;
                this.vol = volume;
            }

            public void fromBytes(ByteBuf buf) {
                this.useCoords = buf.readBoolean();
                this.sound = ByteBufUtils.readUTF8String(buf);
                this.vol = buf.readFloat();
                if (this.useCoords) {
                    this.x = buf.readInt();
                    this.y = buf.readInt();
                    this.z = buf.readInt();
                } else {
                    this.entityID = buf.readInt();
                }
            }

            public void toBytes(ByteBuf buf) {
                buf.writeBoolean(this.useCoords);
                ByteBufUtils.writeUTF8String(buf, this.sound);
                buf.writeFloat(this.vol);
                if (this.useCoords) {
                    buf.writeInt(this.x);
                    buf.writeInt(this.y);
                    buf.writeInt(this.z);
                } else {
                    buf.writeInt(this.entityID);
                }
            }

            public static class Handler_PlaySoundS2C implements IMessageHandler<Message_PlaySoundS2C, IMessage> {
                @SideOnly(Side.CLIENT)
                public IMessage onMessage(Message_PlaySoundS2C message, MessageContext ctx) {
                    if (FMLClientHandler.instance().getClient() == null) return null;
                    Minecraft mc = FMLClientHandler.instance().getClient();
                    WorldClient worldClient = mc.theWorld;
                    EntityClientPlayerMP entityClientPlayerMP = mc.thePlayer;
                    if (message.useCoords) {
                        mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("deci:" + message.sound), message.vol, 1.0F, message.x + 0.5F, message.y + 0.5F, message.z + 0.5F));
                    } else {
                        EntityPlayer theirPlayer = (EntityPlayer) worldClient.getEntityByID(message.entityID);
                        if (worldClient.getEntityByID(entityClientPlayerMP.getEntityId()) != null && theirPlayer != null && entityClientPlayerMP.getEntityId() != theirPlayer.getEntityId() && entityClientPlayerMP.getDistanceToEntity(theirPlayer) < 64.0F)
                            mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("deci:" + message.sound), message.vol, 1.0F, (float) theirPlayer.posX, (float) theirPlayer.posY, (float) theirPlayer.posZ));
                    }
                    return null;
                }
            }
        }

        public static class Message_StopReloadC2S implements IMessage {
            public Message_StopReloadC2S() {
            }

            public void fromBytes(ByteBuf buf) {
            }

            public void toBytes(ByteBuf buf) {
            }

            public static class Handler_StopReloadC2S implements IMessageHandler<Message_StopReloadC2S, IMessage> {
                public IMessage onMessage(Message_StopReloadC2S message, MessageContext ctx) {
                    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                    if (player != null) {
                        Network.PACKET.sendTo(new Message_StopReloadS2C(), player);
                    }
                    return null;
                }
            }
        }

        public static class Message_StopReloadS2C implements IMessage {
            public Message_StopReloadS2C() {
            }

            public void fromBytes(ByteBuf buf) {
            }

            public void toBytes(ByteBuf buf) {
            }

            public static class Handler_StopReloadS2C implements IMessageHandler<Message_StopReloadS2C, IMessage> {
                @SideOnly(Side.CLIENT)
                public IMessage onMessage(Message_StopReloadS2C message, MessageContext ctx) {
                    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                    if (player != null && player.worldObj.isRemote) {
                        if (b.TI != null && b.TI.TM != null) {
                            b.TI.TM.Rn = false;
                            b.TI.TM.active = false;
                        }
                    }

                    return null;
                }
            }
        }
    }

    public static class Message_RenderTracer implements IMessage {
        private float startX, startY, startZ;
        private float endX, endY, endZ;

        private int colorID;

        public Message_RenderTracer() {
        }

        public Message_RenderTracer(double startX, double startY, double startZ, double endX, double endY, double endZ, deci.ay.i.a color) {
            this.startX = (float) startX;
            this.startY = (float) startY;
            this.startZ = (float) startZ;
            this.endX = (float) endX;
            this.endY = (float) endY;
            this.endZ = (float) endZ;
            this.colorID = color != null ? color.ordinal() : 0;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.startX = buf.readFloat();
            this.startY = buf.readFloat();
            this.startZ = buf.readFloat();
            this.endX = buf.readFloat();
            this.endY = buf.readFloat();
            this.endZ = buf.readFloat();
            this.colorID = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeFloat(this.startX);
            buf.writeFloat(this.startY);
            buf.writeFloat(this.startZ);
            buf.writeFloat(this.endX);
            buf.writeFloat(this.endY);
            buf.writeFloat(this.endZ);
            buf.writeInt(this.colorID);
        }

        public static class Handler implements IMessageHandler<Message_RenderTracer, IMessage> {
            @SideOnly(Side.CLIENT)
            @Override
            public IMessage onMessage(Message_RenderTracer message, MessageContext ctx) {
                deci.ay.i.a[] colors = deci.ay.i.a.values();
                deci.ay.i.a tracerColor = (message.colorID >= 0 && message.colorID < colors.length) ? colors[message.colorID] : deci.ay.i.a.values()[0];

                deci.n.d.a(new deci.n.d.a(new Vector3f(message.startX, message.startY, message.startZ), new Vector3f(message.endX, message.endY, message.endZ), tracerColor));

                return null;
            }
        }
    }

    public static class Message_PlayerShootC2S implements IMessage {
        public Message_PlayerShootC2S() {
        }

        @Override
        public void fromBytes(ByteBuf buf) {
        }

        @Override
        public void toBytes(ByteBuf buf) {
        }

        public static class Handler implements IMessageHandler<Message_PlayerShootC2S, IMessage> {
            @Override
            public IMessage onMessage(Message_PlayerShootC2S message, MessageContext ctx) {
                EntityPlayerMP shooter = ctx.getServerHandler().playerEntity;

                if (shooter == null || shooter.isDead || shooter.getHeldItem() == null || !(shooter.getHeldItem().getItem() instanceof deci.ay.i))
                    return null;

                deci.ay.i gun = (deci.ay.i) shooter.getHeldItem().getItem();

                if (gun.t(shooter.getHeldItem()) <= 0) {
                    return null;
                }

                boolean hasSilencer = gun.F(shooter.getHeldItem());

                double noiseRadius = hasSilencer ? 32.0D : 128.0D;
                this.alertZombies(shooter, noiseRadius);

                this.handleAmmoCount(gun, shooter);

                int pelletCount = 1;
                float spreadAmount = 0.0008F;

                if (gun.aes == deci.ay.c.shotgun) {
                    spreadAmount = gun == deci.aD.k.asO ? 0.01F : 0.04F;
                    if (gun == deci.aD.k.asL) {
                        pelletCount = gun.aep[0].adI.adN;
                    } else {
                        pelletCount = gun.aep[0].adN;
                    }
                }

                double range = gun == deci.aD.k.asO ? 84 : gun == deci.aD.k.asn ? 256 : getMaxRangeFromType(gun.aes);
                if (range <= 0) return null;

                float damagePerPellet = (float) gun.aew / (pelletCount > 1 ? (float) pelletCount / 3 : 1);

                Vec3 eyePos = Vec3.createVectorHelper(shooter.posX, shooter.posY + (double) shooter.getEyeHeight(), shooter.posZ);
                Vec3 baseLookVec = shooter.getLookVec();
                Random rand = shooter.worldObj.rand;

                AxisAlignedBB traceBox = shooter.boundingBox.addCoord(baseLookVec.xCoord * range, baseLookVec.yCoord * range, baseLookVec.zCoord * range).expand(range * spreadAmount + 1.0, range * spreadAmount + 1.0, range * spreadAmount + 1.0);
                List<Entity> possibleEntities = shooter.worldObj.getEntitiesWithinAABBExcludingEntity(shooter, traceBox);

                for (int p = 0; p < pelletCount; ++p) {

                    Vec3 currentEyePos = Vec3.createVectorHelper(eyePos.xCoord, eyePos.yCoord, eyePos.zCoord);

                    double dirX = baseLookVec.xCoord + (rand.nextGaussian() * spreadAmount);
                    double dirY = baseLookVec.yCoord + (rand.nextGaussian() * spreadAmount);
                    double dirZ = baseLookVec.zCoord + (rand.nextGaussian() * spreadAmount);

                    double length = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                    dirX /= length;
                    dirY /= length;
                    dirZ /= length;

                    Vec3 endPos = currentEyePos.addVector(dirX * range, dirY * range, dirZ * range);

                    boolean isItric = gun.aep[0].adI == deci.aD.k.avc;
                    float penetrationMultiplier = 1.0F;

                    Vec3 traceStart = Vec3.createVectorHelper(currentEyePos.xCoord, currentEyePos.yCoord, currentEyePos.zCoord);
                    Vec3 traceEnd = Vec3.createVectorHelper(endPos.xCoord, endPos.yCoord, endPos.zCoord);
                    MovingObjectPosition blockHit;

                    while (true) {
                        blockHit = shooter.worldObj.func_147447_a(Vec3.createVectorHelper(traceStart.xCoord, traceStart.yCoord, traceStart.zCoord), Vec3.createVectorHelper(traceEnd.xCoord, traceEnd.yCoord, traceEnd.zCoord), false, true, false);

                        if (blockHit != null && blockHit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                            net.minecraft.block.Block hitB = shooter.worldObj.getBlock(blockHit.blockX, blockHit.blockY, blockHit.blockZ);

                            if (isItric && (hitB instanceof deci.T.b || hitB instanceof deci.T.d || hitB instanceof deci.T.e || hitB instanceof deci.T.f)) {

                                penetrationMultiplier *= 0.8F;

                                if (penetrationMultiplier < 0.2F) {
                                    break;
                                }

                                traceStart = Vec3.createVectorHelper(blockHit.hitVec.xCoord + (dirX * 0.05D), blockHit.hitVec.yCoord + (dirY * 0.05D), blockHit.hitVec.zCoord + (dirZ * 0.05D));

                                if (currentEyePos.distanceTo(traceStart) >= range) {
                                    blockHit = null;
                                    break;
                                }

                                continue;
                            }
                        }
                        break;
                    }

                    double maxDistance = range;
                    if (blockHit != null && blockHit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        maxDistance = currentEyePos.distanceTo(blockHit.hitVec);
                        endPos = Vec3.createVectorHelper(blockHit.hitVec.xCoord, blockHit.hitVec.yCoord, blockHit.hitVec.zCoord);
                    }

                    Entity hitEntity = null;
                    Vec3 hitVec = null;
                    double closestEntityDist = maxDistance;

                    for (Entity entity : possibleEntities) {
                        if (entity.canBeCollidedWith() && !entity.isDead) {
                            if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).deathTime > 0)
                                continue;

                            float border = entity.getCollisionBorderSize();
                            AxisAlignedBB aabb = entity.boundingBox.expand(border, border, border);
                            MovingObjectPosition mop = aabb.calculateIntercept(currentEyePos, endPos);

                            if (mop != null) {
                                double dist = currentEyePos.distanceTo(mop.hitVec);
                                if (dist < closestEntityDist || dist == 0.0D) {
                                    closestEntityDist = dist;
                                    hitEntity = entity;
                                    hitVec = mop.hitVec;
                                }
                            }
                        }
                    }

                    if (hitEntity != null) {

                        if (hitEntity instanceof EntityPlayer) {
                            EntityPlayer target = (EntityPlayer) hitEntity;
                            deci.Q.b data_target = deci.Q.b.e(target);
                            deci.Q.b data_source = deci.Q.b.e(shooter);

                            boolean targetHasCombatTimer = data_target.cf() > 0L;
                            boolean targetInSafezone = net.decimation.mod.server.zones.b.a(target, net.decimation.mod.server.zones.a.SAFEZONE);
                            boolean sourceHasCombatTimer = data_source.cf() > 0L;
                            boolean sourceInSafezone = net.decimation.mod.server.zones.b.a(shooter, net.decimation.mod.server.zones.a.SAFEZONE);

                            if (data_target.dn() > 0L && !targetHasCombatTimer) {
                                if (p == 0)
                                    shooter.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Can't attack player! They've left a safezone too recently!"));
                                continue;
                            }

                            boolean targetProtectedBySZ = targetInSafezone && !targetHasCombatTimer;
                            boolean sourceProtectedBySZ = sourceInSafezone && !sourceHasCombatTimer;

                            if (targetProtectedBySZ || sourceProtectedBySZ) {
                                if (p == 0)
                                    shooter.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Can't attack players from in or out of safezone!"));
                                continue;
                            }

                            if (target instanceof EntityPlayerMP) {
                                EntityPlayerMP targetMP = (EntityPlayerMP) target;
                                net.decimation.mod.server.clans.a clanManager = deci.a.b.a().d().v();

                                try {
                                    if (clanManager.isPlayerInClan(targetMP) && clanManager.isPlayerInClan(shooter)) {
                                        String targetClan = clanManager.v(targetMP);
                                        String sourceClan = clanManager.v(shooter);

                                        if (targetClan.equals(sourceClan) && !targetClan.equalsIgnoreCase(net.decimation.mod.server.clans.a.UNKNOWN_CLAN)) {
                                            continue;
                                        }
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else if (hitEntity instanceof deci.ah.c) {
                            if (net.decimation.mod.server.zones.b.a(hitEntity, net.decimation.mod.server.zones.a.SAFEZONE)) {
                                if (((deci.ah.c) hitEntity).getOwner() != null) continue;
                            }
                        }


                        float damageToApply = damagePerPellet * penetrationMultiplier;

                        if (hitEntity instanceof EntityPlayer) {
                            EntityPlayer target = (EntityPlayer) hitEntity;
                            deci.Q.b targetData = deci.Q.b.e(target);

                            if (target.getHeldItem() != null && target.getHeldItem().getItem() instanceof deci.az.c) {
                                Vec3 targetLook = target.getLookVec();
                                Vec3 shooterLook = shooter.getLookVec();

                                double dot = targetLook.xCoord * shooterLook.xCoord + targetLook.yCoord * shooterLook.yCoord + targetLook.zCoord * shooterLook.zCoord;

                                if (dot < -0.9D && ((deci.az.c) target.getHeldItem().getItem()).O(target.getHeldItem())) {
                                    if (p == 0)
                                        target.worldObj.playSoundAtEntity(target, "deci:bullet.bounce.metal", 1.0F, 1.0F);

                                    target.getHeldItem().damageItem((int) damageToApply / 2, target);

                                    if (target.getHeldItem().stackSize <= 0) {
                                        target.destroyCurrentEquippedItem();
                                    }

                                    shooter.inventoryContainer.detectAndSendChanges();
                                    continue;
                                }
                            }

                            if (!target.capabilities.isCreativeMode || !targetData.cl()) {
                                if (hitVec.yCoord < target.posY + (target.height / 2) && rand.nextInt(4) == 0) {
                                    breakLegs(target, targetData);
                                } else if (targetData.bM() == 2 && rand.nextInt(4) == 0) {
                                    breakLegs(target, targetData);
                                }
                            }

                            boolean isHeadshot = hitVec != null && hitVec.yCoord >= (target.posY + target.height * 0.8D);

                            if (isHeadshot && targetData.bM() != 2) {
                                if (target.getCurrentArmor(3) != null && target.getCurrentArmor(3).getItem() instanceof net.decimation.mod.common.item.armor.ItemArmorDeci) {
                                    if (p == 0)
                                        target.worldObj.playSoundEffect(target.posX, target.posY + 1.0D, target.posZ, "deci:mob.human.headshot.helmet", 4.0F, 1.0F);
                                    net.decimation.mod.common.item.armor.ItemArmorDeci helmet = (net.decimation.mod.common.item.armor.ItemArmorDeci) target.getCurrentArmor(3).getItem();
                                    damageToApply *= helmet.getDamageMultiplier();
                                }
                            } else {
                                for (int i = 0; i < 3; ++i) {
                                    if (target.getCurrentArmor(i) != null && target.getCurrentArmor(i).getItem() instanceof net.decimation.mod.common.item.armor.ItemArmorDeci) {
                                        net.decimation.mod.common.item.armor.ItemArmorDeci armor = (net.decimation.mod.common.item.armor.ItemArmorDeci) target.getCurrentArmor(i).getItem();
                                        damageToApply *= armor.getDamageMultiplier();
                                    }
                                }
                            }

                            if (targetData.cx() != null && targetData.cx().getItem() instanceof net.decimation.mod.common.item.armor.ItemMaskDeci) {
                                damageToApply *= ((net.decimation.mod.common.item.armor.ItemMaskDeci) targetData.cx().getItem()).getDamageMultiplier();
                            }

                            if (targetData.cw() != null && targetData.cw().getItem() == deci.aD.k.ani) {
                                if (p == 0)
                                    ((net.decimation.mod.common.item.armor.ItemVestDeci) targetData.cw().getItem()).explodeVest(target);
                                continue;
                            }

                            if (targetData.cw() != null && targetData.cw().getItem() instanceof net.decimation.mod.common.item.armor.ItemVestDeci) {
                                damageToApply *= ((net.decimation.mod.common.item.armor.ItemVestDeci) targetData.cw().getItem()).getDamageMultiplier();
                            }

                            target.hurtResistantTime = 0;
                            applyHit(target, shooter, damageToApply);

                        } else if (hitEntity instanceof EntityLivingBase) {
                            hitEntity.hurtResistantTime = 0;
                            applyHit((EntityLivingBase) hitEntity, shooter, damageToApply);
                        } else if (hitEntity instanceof deci.aj.c) {
                            ((deci.aj.c) hitEntity).fe();
                        }

                    } else if (blockHit != null) {
                        net.minecraft.block.Block block = shooter.worldObj.getBlock(blockHit.blockX, blockHit.blockY, blockHit.blockZ);
                        String soundName = getString(block);

                        if (soundName != null && p == 0) {
                            shooter.worldObj.playSoundEffect((double) blockHit.blockX + 0.5, (double) blockHit.blockY + 0.5, (double) blockHit.blockZ + 0.5, soundName, 0.5F, 1.0F);
                        }

                        if (shooter.worldObj instanceof net.minecraft.world.WorldServer && block != net.minecraft.init.Blocks.air) {
                            int meta = shooter.worldObj.getBlockMetadata(blockHit.blockX, blockHit.blockY, blockHit.blockZ);
                            String particleName = "blockcrack_" + net.minecraft.block.Block.getIdFromBlock(block) + "_" + meta;

                            ((net.minecraft.world.WorldServer) shooter.worldObj).func_147487_a(particleName, blockHit.hitVec.xCoord, blockHit.hitVec.yCoord, blockHit.hitVec.zCoord, 10, 0.1D, 0.1D, 0.1D, 0.05D);
                        }
                    }

                    double tracerEndX = (hitEntity != null && hitVec != null) ? hitVec.xCoord : endPos.xCoord;
                    double tracerEndY = (hitEntity != null && hitVec != null) ? hitVec.yCoord : endPos.yCoord;
                    double tracerEndZ = (hitEntity != null && hitVec != null) ? hitVec.zCoord : endPos.zCoord;

                    double visualStartX = shooter.posX;
                    double visualStartY = shooter.posY + shooter.getEyeHeight() - 0.1D;
                    double visualStartZ = shooter.posZ;

                    deci.ay.i.a color = gun.z(shooter.getHeldItem());

                    Addon.Network.PACKET.sendToAllAround(new Addon.Message_RenderTracer(visualStartX, visualStartY, visualStartZ, tracerEndX, tracerEndY, tracerEndZ, color), new cpw.mods.fml.common.network.NetworkRegistry.TargetPoint(shooter.dimension, shooter.posX, shooter.posY, shooter.posZ, range));
                }

                shooter.inventoryContainer.detectAndSendChanges();
                return null;
            }

            private void alertZombies(EntityPlayerMP player, double radius) {
                AxisAlignedBB alertArea = AxisAlignedBB.getBoundingBox(player.posX - radius, player.posY - radius, player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius);

                List<deci.ag.d> nearbyZombies = player.worldObj.getEntitiesWithinAABB(deci.ag.d.class, alertArea);

                double radiusSq = radius * radius;

                for (deci.ag.d zombie : nearbyZombies) {
                    if (zombie.getDistanceSqToEntity(player) <= radiusSq) {
                        AIInvestigateSound.SOUND_TARGETS.put(zombie, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));
                    }
                }
            }

            private void breakLegs(EntityPlayer target, deci.Q.b targetData) {
                target.worldObj.playSoundAtEntity(target, "deci:mob.human.legbreak", 0.1F, 0.5F);
                targetData.e(true);
                target.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Your legs have been broken!"));
            }

            private void handleAmmoCount(deci.ay.i theGun, EntityPlayer shooter) {
                theGun.e(shooter.getHeldItem(), theGun.t(shooter.getHeldItem()) - 1);
            }

            private double getMaxRangeFromType(deci.ay.c type) {
                switch (type) {
                    case mg:
                        return 218D;
                    case rifle:
                        return 192D;
                    case smg:
                        return 144D;
                    case revolver:
                    case pistol:
                        return 112D;
                    case shotgun:
                        return 64D;
                    default:
                        return -1D;
                }
            }

            private static String getString(net.minecraft.block.Block block) {
                net.minecraft.block.material.Material mat = block.getMaterial();
                String soundName = null;

                if (mat == net.minecraft.block.material.Material.water) soundName = "deci:bullet.bounce.water";
                else if (mat == net.minecraft.block.material.Material.glass) soundName = "deci:bullet.bounce.glass";
                else if (mat == net.minecraft.block.material.Material.iron) soundName = "deci:bullet.bounce.metal";
                else if (mat == net.minecraft.block.material.Material.ground) soundName = "deci:bullet.bounce.dirt";
                else if (mat == net.minecraft.block.material.Material.rock) soundName = "deci:bullet.bounce.stone";
                else if (mat == net.minecraft.block.material.Material.wood) soundName = "deci:bullet.bounce.wood";
                return soundName;
            }

            private void applyHit(EntityLivingBase target, EntityPlayer shooter, float damage) {
                target.attackEntityFrom(net.minecraft.util.DamageSource.causePlayerDamage(shooter), damage);
                target.arrowHitTimer++;
                target.hurtResistantTime = target.maxHurtResistantTime / 2;

                target.motionX = 0.0D;
                target.motionY = 0.0D;
                target.motionZ = 0.0D;
            }
        }
    }

    public static class Message_InfectionSpasm implements IMessage {
        private int entityId;

        public Message_InfectionSpasm() {
        }

        public Message_InfectionSpasm(int entityId) {
            this.entityId = entityId;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.entityId = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.entityId);
        }

        public static class Handler implements IMessageHandler<Message_InfectionSpasm, IMessage> {
            @SideOnly(Side.CLIENT)
            @Override
            public IMessage onMessage(Message_InfectionSpasm message, MessageContext ctx) {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.theWorld == null) return null;

                Entity targetEntity = mc.theWorld.getEntityByID(message.entityId);

                if (targetEntity instanceof EntityPlayer) {
                    EntityPlayer spasmingPlayer = (EntityPlayer) targetEntity;
                    java.util.Random rand = spasmingPlayer.getRNG();

                    float spasmYaw = (rand.nextFloat() - 0.5F) * 30.0F;
                    float spasmPitch = (rand.nextFloat() - 0.5F) * 20.0F;

                    spasmingPlayer.rotationYawHead += spasmYaw;

                    if (spasmingPlayer == mc.thePlayer) {
                        spasmingPlayer.rotationYaw += spasmYaw;
                        spasmingPlayer.rotationPitch += spasmPitch;

                        spasmingPlayer.cameraYaw = 0.15F;
                    }
                }
                return null;
            }
        }
    }

    public static class Message_SyncStance implements IMessage {
        private int entityId;
        private int stance;

        public Message_SyncStance() {
        }

        public Message_SyncStance(int entityId, int stance) {
            this.entityId = entityId;
            this.stance = stance;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.entityId = buf.readInt();
            this.stance = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.entityId);
            buf.writeInt(this.stance);
        }

        public static class Handler implements IMessageHandler<Message_SyncStance, IMessage> {
            @SideOnly(Side.CLIENT)
            @Override
            public IMessage onMessage(Message_SyncStance message, MessageContext ctx) {

                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();

                if (mc.theWorld == null) return null;
                net.minecraft.entity.Entity targetEntity = mc.theWorld.getEntityByID(message.entityId);

                if (targetEntity instanceof net.minecraft.entity.player.EntityPlayer) {
                    deci.Q.b data = deci.Q.b.e((net.minecraft.entity.player.EntityPlayer) targetEntity);

                    if (data != null) {
                        data.u(message.stance);
                    }
                }

                return null;
            }
        }
    }
}
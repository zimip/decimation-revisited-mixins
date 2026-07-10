package net.zimi.revisited.mixin.mixins.Common.Handler;

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import deci.aE.a;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(value = a.P.a.class)
public abstract class KeyHandlerMessage implements IMessageHandler<a.P, IMessage> {
    @Overwrite(remap = false)
    public IMessage a(deci.aE.a.P message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        deci.Q.b data = deci.Q.b.e(player);

        if (data == null) return null;

        data.P(data.bM());

        a.P.b actionEnum = ((AccessorKeyHandler)(Object) message).getAction();
        if (actionEnum == null) return null;

        switch (actionEnum.name()) {
            case "WHISTLE":
                if (data.cj()) {
                    if (player.ridingEntity instanceof deci.ad.c && ((deci.ad.c) player.ridingEntity).dG()) {
                        player.swingItem();
                        deci.ad.c vehicle = (deci.ad.c) player.ridingEntity;
                        if (vehicle.Yo != null) {
                            player.worldObj.playSoundAtEntity(player, "deci:vehicle." + vehicle.Yo.dY() + ".horn", 5.0F, 1.0F);
                        }
                    } else {
                        player.worldObj.playSoundAtEntity(player, "deci:mob.human.whistle", 2.0F, 1.0F);

                        List<?> entities = player.worldObj.loadedEntityList;
                        if (entities != null) {
                            for (Object obj : entities) {
                                if (obj instanceof deci.ah.c) {
                                    deci.ah.c dog = (deci.ah.c) obj;
                                    if (dog.getOwner() != null && dog.getOwner().getEntityId() == player.getEntityId()) {
                                        dog.func_70907_r().setSitting(false);
                                    }
                                }
                            }
                        }
                    }
                } else if (player.ridingEntity instanceof deci.ad.c && ((deci.ad.c) player.ridingEntity).dG()) {
                    player.addChatComponentMessage(new ChatComponentText("You're too exhausted to Beep."));
                } else {
                    player.addChatComponentMessage(new ChatComponentText("You're too hoarse to Whistle."));
                }
                data.cq();
                break;

            case "UNHARNESS":
                if (player.riddenByEntity instanceof deci.ah.c) {
                    Entity dog = player.riddenByEntity;
                    dog.ridingEntity = null;
                    ((deci.ah.c) dog).dismountEntity(player);
                    player.addChatComponentMessage(new ChatComponentText("You've unharnessed your dog..."));
                    player.worldObj.playSoundAtEntity(player, "deci:misc.armor.equip", 0.3F, 1.0F);
                }
                break;

            case "STANCE_PRONE":
                if (player.ridingEntity == null && !data.bJ() && !data.bK() && data.di() <= 0) {
                    data.u(data.bM() == 2 ? 0 : 2);
                    player.setSneaking(false);
                    player.worldObj.playSoundAtEntity(player, "deci:mob.human.movement", 0.3F, 1.0F);
                }
                break;

            case "STANCE_CROUCH":
                if (player.ridingEntity == null && !data.bJ() && !data.bK() && data.di() <= 0) {
                    data.u(data.bM() == 1 ? 0 : 1);
                    player.setSneaking(false);
                    player.worldObj.playSoundAtEntity(player, "deci:mob.human.movement", 0.3F, 1.0F);
                }
                break;

            case "STANCE_RESET":
                if (player.ridingEntity == null) {
                    if (data.bM() == 2) {
                        int blockX = MathHelper.floor_double(player.posX);
                        int blockY = MathHelper.floor_double(player.posY + 1.0D);
                        int blockZ = MathHelper.floor_double(player.posZ);

                        if (player.worldObj.getBlock(blockX, blockY, blockZ) == Blocks.air) {
                            player.worldObj.playSoundAtEntity(player, "deci:mob.human.movement", 0.3F, 1.0F);
                            data.u(0);
                        }
                    } else if (data.bM() != 0) {
                        player.worldObj.playSoundAtEntity(player, "deci:mob.human.movement", 0.3F, 1.0F);
                        data.u(0);
                    }
                }
                break;

            case "RELOAD":
                if (data.ci() != 0) {
                    data.M(0);
                }
                break;

            case "FIREMODE":
                if (player.getEquipmentInSlot(0) != null && player.getEquipmentInSlot(0).getItem() instanceof deci.ay.i) {
                    deci.ay.i gun = (deci.ay.i) player.getEquipmentInSlot(0).getItem();
                    int currentMode = gun.v(player.getEquipmentInSlot(0));
                    int nextMode = currentMode < gun.aeq.adp.length - 1 ? currentMode + 1 : 0;

                    if (!player.getEquipmentInSlot(0).hasTagCompound()) {
                        player.getEquipmentInSlot(0).setTagCompound(new NBTTagCompound());
                    }
                    player.getEquipmentInSlot(0).getTagCompound().setInteger("mode", nextMode);
                }
                break;

            case "GESTURE_SURRENDER":
                data.G(data.bX() != 1 ? 1 : 0);
                break;

            case "GESTURE_WAVE":
                data.G(data.bX() != 2 ? 2 : 0);
                break;

            case "GESTURE_CLAP":
                data.G(data.bX() != 3 ? 3 : 0);
                break;

            case "GESTURE_DAB":
                data.G(data.bX() != 5 ? 5 : 0);
                break;

            case "ATTACH":
                player.openGui(deci.a.b.f, deci.Q.c.Xc, player.worldObj, 0, 0, 0);
                break;

            case "CRAFT":
                player.openGui(deci.a.b.f, deci.Q.c.Xd, player.worldObj, 0, 0, 0);
                break;

            case "HOLSTER":
                data.h(!data.cg());
                break;

            case "VEHICLE_LIGHTS":
                if (player.ridingEntity instanceof deci.ad.c) {
                    deci.ad.c vehicle = (deci.ad.c) player.ridingEntity;
                    if (vehicle.Yo != null && vehicle.Yo.YO != null && vehicle.Yo.YO.riddenByEntity != null && vehicle.Yo.YO.riddenByEntity.getEntityId() == player.getEntityId()) {
                        vehicle.Yo.ec();
                    }
                }
                break;
        }

        return null;
    }
}
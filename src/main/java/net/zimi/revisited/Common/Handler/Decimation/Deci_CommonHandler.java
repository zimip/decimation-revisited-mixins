package net.zimi.revisited.Common.Handler.Decimation;

import com.boehmod.lib.utils.BoehModLogger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import deci.T.f;
import deci.W.d;
import deci.aB.c;
import deci.aB.e;
import net.decimation.mod.common.block.props.BlockProp;
import net.decimation.mod.common.item.armor.ItemNVGoggles;
import net.decimation.mod.server.zones.a;
import net.decimation.mod.server.zones.b;
import net.decimation.mod.utilities.net.client_network.api.messages.Request_FromClient_AddDeath;
import net.decimation.mod.utilities.net.client_network.api.messages.Request_FromClient_AddKill;
import net.decimation.mod.utilities.net.client_network.api.messages.Request_FromClient_AddZombieKill;
import net.decimation.mod.utilities.net.client_network.api.messages.Request_FromClient_Supporter_Check;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.zimi.revisited.Addon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Deci_CommonHandler {
    private static final Random RANDOM = new Random();

    private static int ticks = 0;
    private static float fogDensity_current;
    private static int DEMON_COUNT = 0;
    public static String[] spawnPoints;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private long lastReconnectAttempt = 0;
    private boolean isReconnecting = false;

    private final WeakHashMap<EntityPlayer, Integer> growlTimers = new WeakHashMap<>();

    private deci.aP.a getNetConnectionSafe() {

            try {
                return deci.a.b.a().e();
            } catch (Throwable ignored) {
            }

        return null;
    }

    private net.decimation.mod.server.clans.a getClanManagerSafe() {
        try {
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null && server.isDedicatedServer()) {
                return deci.a.b.a().d().v();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private net.decimation.mod.server.turf.a getTurfManagerSafe() {
        try {
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null && server.isDedicatedServer()) {
                return deci.a.b.a().d().x();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private void tick_Weather() {
        if (ticks > 0) {
            --ticks;
        } else {
            try {
                BoehModLogger.printLine(BoehModLogger.EnumLogType.SERVER, "Adjusting weather.");
                final float min = 0.008F, max = 0.015F;

                float randomValue = min + RANDOM.nextFloat() * (max - min);
                fogDensity_current = Math.round(randomValue * 10000.0F) / 10000.0F;

                BoehModLogger.printLine(BoehModLogger.EnumLogType.SERVER, "Setting to -> " + fogDensity_current);

                Addon.Network.PACKET.sendToAll(new Addon.Network.Message_WorldProperties(fogDensity_current));

                ticks = 25000;
            } catch (NullPointerException e) {
                System.err.println("NPE in tick_Weather!");
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) throws IOException {
        if (event.world.isRemote) return;

        deci.a.b decimationMain = deci.a.b.a();

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && !event.entityPlayer.capabilities.isCreativeMode) {

            if (FMLCommonHandler.instance().getEffectiveSide().isServer() && deci.aJ.b.aAu) {
                return;
            }

            e lootManager = e.d(event.world);
            int x = event.x;
            int y = event.y;
            int z = event.z;
            Block targetBlock = event.world.getBlock(x, y, z);

            if ((targetBlock == Blocks.hopper || targetBlock == Blocks.furnace || targetBlock == Blocks.dropper || targetBlock == Blocks.crafting_table || targetBlock == Blocks.jukebox || targetBlock == Blocks.ender_chest || targetBlock == Blocks.anvil || targetBlock == Blocks.brewing_stand || targetBlock == Blocks.dispenser)) {
                event.setCanceled(true);
            }

            if (targetBlock instanceof BlockChest && !(targetBlock instanceof f) && !deci.aJ.b.aAu) {
                event.setCanceled(true);
            }

            if (targetBlock instanceof f) {
                net.decimation.mod.server.turf.a turfManager = getTurfManagerSafe();
                boolean canOpen = true;

                if (turfManager != null) {
                    canOpen = turfManager.c(event.entityPlayer, event.x, event.y, event.z);
                }

                if (!canOpen && !net.decimation.mod.server.zones.b.a(event.entityPlayer, net.decimation.mod.server.zones.a.SAFEZONE)) {
                    event.setCanceled(true);
                    event.entityPlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Can't open that in another clans turf!"));
                }
            }

            if (targetBlock instanceof deci.W.b) {
                TileEntity te = event.world.getTileEntity(x, y, z);
                if (te == null) return;

                d multiBlockTile = (d) te;
                x = multiBlockTile.getOriginX();
                y = multiBlockTile.getOriginY();
                z = multiBlockTile.getOriginZ();
                Block actualBlock = event.world.getBlock(x, y, z);
                if (actualBlock != targetBlock) return;
            }

            if (lootManager != null && lootManager.k(x, y, z)) {
                boolean isOutsideSafezone = !net.decimation.mod.server.zones.b.b(event.entityPlayer, net.decimation.mod.server.zones.a.SAFEZONE);
                boolean isInSafezoneLootable = net.decimation.mod.server.zones.b.a(event.entityPlayer, net.decimation.mod.server.zones.a.SAFEZONE) && (targetBlock instanceof deci.V.a || (targetBlock instanceof BlockProp && ((BlockProp) targetBlock).getSafezoneLootable()));

                if (isOutsideSafezone || isInSafezoneLootable) {
                    c generatedLoot = decimationMain.f().c(targetBlock);
                    if (generatedLoot != null) {
                        lootManager.l(x, y, z);
                        event.setCanceled(true);
                        byte guiOpenDelay = 5;
                        deci.Q.b playerData = deci.Q.b.e(event.entityPlayer);
                        InventoryBasic lootInv = new InventoryBasic("Loot", true, 18);
                        generatedLoot.a(lootInv);

                        playerData.a(lootInv);
                        if (event.entityPlayer instanceof EntityPlayerMP) {
                            deci.aF.a.a.a.gB().sendTo(new deci.aE.a.R(lootInv), (EntityPlayerMP) event.entityPlayer);
                        }

                        net.decimation.mod.common.utils.b.gD().a(() -> event.entityPlayer.openGui(deci.a.b.f, deci.Q.c.Xa, event.entityPlayer.worldObj, -1, 2, 0), guiOpenDelay / 2);

                        if (targetBlock instanceof deci.aB.b) {
                            int finalX = x;
                            int finalY = y;
                            int finalZ = z;
                            net.decimation.mod.common.utils.b.gD().b(() -> {
                                if (!event.entityPlayer.isDead && event.entityPlayer.openContainer != event.entityPlayer.inventoryContainer) {
                                    return true;
                                } else {
                                    if (event.world.getBlock(finalX, finalY, finalZ) == targetBlock) {
                                        ((deci.aB.b) targetBlock).a(event.entityPlayer, event.world, finalX, finalY, finalZ);
                                    }
                                    return false;
                                }
                            });
                        }
                    }
                }
            } else {
                event.entityPlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "This " + targetBlock.getLocalizedName() + " is empty..."));
            }
        }

        if (event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().getItem() == deci.aD.k.alX) {
            Block targetBlock = event.world.getBlock(event.x, event.y, event.z);
            if (targetBlock == deci.aD.c.afJ) {
                event.world.func_147480_a(event.x, event.y, event.z, false);
                event.entityPlayer.getHeldItem().damageItem(1, event.entityPlayer);
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) throws IOException {
        if (e.phase != TickEvent.Phase.START) return;

        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return;

        tick_Weather();

        final deci.a.b decimationMain = deci.a.b.a();
        net.decimation.mod.common.utils.b.gD().tick();

        if (decimationMain.e() != null && decimationMain.e().aBX != null && !decimationMain.e().aBX.isConnected()) {

            long currentTime = System.currentTimeMillis();

            if (!isReconnecting && (currentTime - lastReconnectAttempt > 10000)) {

                isReconnecting = true;
                lastReconnectAttempt = currentTime;

                Thread reconnectThread = new Thread(() -> {
                    BoehModLogger.printLine(BoehModLogger.EnumLogType.NETWORK, "Attempting reconnection to Network...");
                    try {
                        decimationMain.e().aBX.reconnect();
                        BoehModLogger.printLine(BoehModLogger.EnumLogType.NETWORK, "Successfully reconnected to Network!");
                    } catch (Exception ex) {
                        BoehModLogger.printError(BoehModLogger.EnumLogType.NETWORK, "Failed to Connect to Network!");
                    } finally {
                        isReconnecting = false;
                    }
                });

                reconnectThread.setDaemon(true);
                reconnectThread.start();
            }
        }

        net.decimation.mod.server.clans.a clanManager = getClanManagerSafe();
        if (clanManager != null) {
            clanManager.a(server);
        }

        if (deci.aJ.b.aAS > 0) {
            --deci.aJ.b.aAS;
        } else {
            deci.aJ.b.aAS = 3000;
            net.decimation.mod.server.traders.a.f(server);
        }

        if (deci.aJ.b.aAg == 18000) {
            server.getConfigurationManager().sendChatMsg(new ChatComponentText(EnumChatFormatting.GRAY + "A supply crate will drop in " + EnumChatFormatting.GREEN + "15" + EnumChatFormatting.GRAY + " minutes!"));
        } else if (deci.aJ.b.aAg == 12000) {
            server.getConfigurationManager().sendChatMsg(new ChatComponentText(EnumChatFormatting.GRAY + "A supply crate will drop in " + EnumChatFormatting.GREEN + "10" + EnumChatFormatting.GRAY + " minutes!"));
        } else if (deci.aJ.b.aAg == 6000) {
            server.getConfigurationManager().sendChatMsg(new ChatComponentText(EnumChatFormatting.GRAY + "A supply crate will drop in " + EnumChatFormatting.GREEN + "5" + EnumChatFormatting.GRAY + " minutes!"));
        }

        if (deci.aJ.b.aAg <= 0) {
            deci.aM.c.ha();
            deci.aJ.b.aAg = 24000;
        } else {
            --deci.aJ.b.aAg;
        }

        try {
            if (decimationMain.e() != null && decimationMain.e().aBX != null && decimationMain.e().aBX.isConnected() != deci.aJ.b.bp) {
                if (decimationMain.e().aBX.isConnected()) {
                    System.out.println("WARNING: Connection to the Decimation Network was Successfully Made!");
                    server.getConfigurationManager().sendChatMsg(new ChatComponentText("Server has " + EnumChatFormatting.GREEN + "Connected " + EnumChatFormatting.WHITE + "to the Network!"));
                } else {
                    System.out.println("WARNING: Connection to the Decimation Network was Interrupted!");
                    server.getConfigurationManager().sendChatMsg(new ChatComponentText("Server has " + EnumChatFormatting.DARK_RED + "Disconnected " + EnumChatFormatting.WHITE + "from the Network! Attempting to Re-connect..."));
                }
                deci.aJ.b.bp = decimationMain.e().aBX.isConnected();
            }
        } catch (Throwable ignored) {
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        if (event.entityLiving instanceof deci.ai.a) {
            event.setCanceled(true);
            return;
        }

        if (event.entityLiving instanceof EntityPlayer && net.decimation.mod.server.zones.b.a((EntityPlayer) event.entityLiving, net.decimation.mod.server.zones.a.SAFEZONE)) {
            deci.Q.b playerData = deci.Q.b.e((EntityPlayer) event.entityLiving);
            if (playerData != null && playerData.dn() > 0L) {
                event.setCanceled(true);
                return;
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent e) {
        if (e.entity.ridingEntity instanceof deci.ad.c) {
            if (((deci.ad.c) e.entity.ridingEntity).dI()) {
                e.setCanceled(true);
                return;
            }
        }

        if (e.entityLiving instanceof deci.ai.a) {
            e.setCanceled(true);
            return;
        }

        if (!e.entity.worldObj.isRemote) {
            double x = e.entity.posX;
            double y = e.entity.posY;
            double z = e.entity.posZ;
            cpw.mods.fml.common.network.NetworkRegistry.TargetPoint tp = new cpw.mods.fml.common.network.NetworkRegistry.TargetPoint(e.entity.dimension, x, y, z, 32.0D);

            if (e.source.equals(DamageSource.onFire)) {
                if (e.entity instanceof deci.ah.d || e.entity instanceof EntityPlayer) {
                    deci.aF.a.a.a.gB().sendToAllAround(new deci.aE.a.p(e.entity.getEntityId()), tp);
                }
                e.entity.worldObj.playSoundEffect(x, y, z, "fire.fire", 0.5F, 1.0F);
            } else {
                String type = (e.entity instanceof deci.ag.d) ? "BloodInfected" : "Blood";
                String groundType = (e.entity instanceof deci.ag.d) ? "GroundBloodInfected" : "GroundBlood";

                boolean inSafezone = (e.entity instanceof EntityPlayer) && net.decimation.mod.server.zones.b.a((EntityPlayer) e.entity, net.decimation.mod.server.zones.a.SAFEZONE);

                if (!inSafezone) {
                    deci.aF.a.a.a.gB().sendToAllAround(new deci.aE.a.W(type, x, y + 0.5, z), tp);
                    deci.aF.a.a.a.gB().sendToAllAround(new deci.aE.a.W(groundType, x, y, z), tp);
                }
            }
        }
        if (!e.entity.worldObj.isRemote) {

            if (e.entity instanceof EntityPlayer) {
                EntityPlayer victim = (EntityPlayer) e.entity;
                deci.Q.b victimData = deci.Q.b.e(victim);

                if (victimData != null) {
                    if (RANDOM.nextInt(6) == 0 && !Objects.equals(e.source.damageType, DamageSource.magic.damageType)) victimData.g(true);

                    if (e.source == DamageSource.fall) {
                        if (e.entity.fallDistance >= 10F || (e.entity.fallDistance >= 5.0F && RANDOM.nextInt(4) == 0)) {
                            victim.worldObj.playSoundAtEntity(victim, "deci:mob.human.legbreak", 0.8F, 0.5F);
                            victimData.e(true);
                            victim.addPotionEffect(new PotionEffect(Potion.blindness.id, 40, 1));
                        }
                    }
                }

                Entity attackerEntity = e.source.getEntity();

                if (attackerEntity instanceof deci.ag.d && victimData != null) {
                    victimData.l(300L);
                } else if (attackerEntity instanceof EntityPlayer) {
                    EntityPlayer attacker = (EntityPlayer) attackerEntity;
                    net.decimation.mod.server.clans.a clanManager = getClanManagerSafe();

                    if (clanManager != null) {
                        try {
                            if (clanManager.isPlayerInClan(attacker) && clanManager.isPlayerInClan(victim)) {
                                if (clanManager.v(attacker).equals(clanManager.v(victim))) {
                                    e.setCanceled(true);
                                }
                            }
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (e.getPlayer() == null || e.getPlayer().worldObj.isRemote) return;

        if (!e.getPlayer().capabilities.isCreativeMode) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        if (e.world.isRemote) return;

        Entity entity = e.entity;
        String clazz = entity.getClass().getName();

        if (clazz.startsWith("net.minecraft.entity.monster") || clazz.startsWith("net.minecraft.entity.passive") || clazz.startsWith("net.minecraft.entity.boss")) {
            e.setCanceled(true);
        }

        if (entity instanceof EntityPlayerMP) {
            executorService.submit(() -> {
                deci.Q.b playerData = deci.Q.b.e((EntityPlayerMP) entity);
                if (playerData != null) playerData.b(false);

                try {
                    deci.a.b decimationMain = deci.a.b.a();
                    if (decimationMain.e() != null && decimationMain.e().aBX != null) {
                        BoehModLogger.printLine(BoehModLogger.EnumLogType.NETWORK, String.format("Sent supporter check for %s", ((EntityPlayerMP) entity).getDisplayName()));
                        Request_FromClient_Supporter_Check check = new Request_FromClient_Supporter_Check();
                        check.playerUUID = ((EntityPlayerMP) entity).getGameProfile().getId().toString();
                        decimationMain.e().aBX.sendTCP(check);
                    }
                } catch (Throwable ignored) {
                }
            });
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            deci.Q.b playerData = deci.Q.b.e(player);
            if (playerData != null) {
                deci.aF.a.a.a.gB().sendToAll(new deci.aE.a.a(playerData));
            }
            player.inventoryContainer.detectAndSendChanges();
        }

        int x = (int) entity.posX;
        int y = (int) entity.posY;
        int z = (int) entity.posZ;

        if (entity instanceof deci.ag.d) {
            deci.ag.d infected = (deci.ag.d) entity;

            if (net.decimation.mod.server.zones.b.a(infected, net.decimation.mod.server.zones.a.SAFEZONE)) {
                e.setCanceled(true);
                return;
            }

            if (!(entity instanceof deci.ag.e) && RANDOM.nextDouble() < 0.1D) {
                Entity type = (RANDOM.nextDouble() < 0.3D) ? new deci.ag.e(e.world, entity.posX, entity.posY, entity.posZ) : new deci.ag.j(e.world, entity.posX, entity.posY, entity.posZ);
                e.world.spawnEntityInWorld(type);
                e.setCanceled(true);
                entity.setDead();
                return;
            }

            if (!(entity instanceof deci.ag.h) && e.world.canLightningStrikeAt(x, y, z) && RANDOM.nextDouble() < 0.15D) {
                deci.ag.h frail = new deci.ag.h(e.world, entity.posX, entity.posY, entity.posZ);
                e.world.spawnEntityInWorld(frail);
                e.setCanceled(true);
                entity.setDead();
                return;
            }

            double randomValue = RANDOM.nextDouble();
            if (net.decimation.mod.server.zones.b.a(infected, net.decimation.mod.server.zones.a.MILITARY)) {
                if (randomValue < 0.4D) infected.af(deci.am.b.MILITARY.id);
            } else if (net.decimation.mod.server.zones.b.a(infected, net.decimation.mod.server.zones.a.POLICE)) {
                if (randomValue < 0.4D) infected.af(deci.am.b.POLICE.id);
            }
            return;
        }

        if (entity instanceof deci.ag.l && RANDOM.nextDouble() < 0.1D) {
            deci.ag.k mech = new deci.ag.k(e.world, entity.posX, entity.posY, entity.posZ);
            e.world.spawnEntityInWorld(mech);
            e.setCanceled(true);
            entity.setDead();
            return;
        }

        if (entity instanceof deci.aj.c || entity instanceof deci.aj.b || entity instanceof deci.aj.e || entity instanceof deci.aj.a) {
            if (net.decimation.mod.server.zones.b.b(entity, net.decimation.mod.server.zones.a.SAFEZONE)) {
                e.setCanceled(true);
            }
            return;
        }

        if (entity instanceof deci.ak.b || entity instanceof deci.ak.f || entity instanceof deci.ak.c || entity instanceof deci.ak.e) {
            if (net.decimation.mod.server.zones.b.b(entity, net.decimation.mod.server.zones.a.SAFEZONE)) {
                e.setCanceled(true);
            }
            return;
        }

        if (entity instanceof deci.ag.b) {
            DEMON_COUNT++;
            if (DEMON_COUNT > deci.aJ.b.aAI) {
                DEMON_COUNT--;
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null) return;

        deci.Q.b playerData = deci.Q.b.e(player);
        if (playerData == null) return;

        if (event.item.getEntityItem().stackSize > 0) {
            int stackSize = event.item.getEntityItem().stackSize;
            if (event.item.getEntityItem().getItem() == deci.aD.k.aln) {
                if (playerData.cb() < playerData.Vo) {
                    playerData.g(stackSize);
                    ((EntityPlayer)event.entity).addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "+" + stackSize + EnumChatFormatting.GRAY + " bottlecap(s)"));
                    (event.item.getEntityItem()).stackSize = 0;
                    event.entity.worldObj.playSoundAtEntity(event.entity, "deci:misc.bottlecap.pickup", 2.0F, 1.0F);
                }
            } else if (event.item.getEntityItem().getItem() == deci.aD.k.alo) {
                if (playerData.ca() < playerData.Vo) {
                    playerData.d(stackSize);
                    ((EntityPlayer)event.entity).addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "+" + stackSize + EnumChatFormatting.GOLD + " gold" + EnumChatFormatting.GRAY + " bottlecap(s)"));
                    (event.item.getEntityItem()).stackSize = 0;
                    event.entity.worldObj.playSoundAtEntity(event.entity, "deci:misc.bottlecap.pickupgold", 2.0F, 1.0F);
                }
            }
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && deci.Q.b.e((EntityPlayer) event.entity) == null) {
            deci.Q.b.d((EntityPlayer) event.entity);
        }

        if (event.entity instanceof EntityPlayerMP && event.entity.worldObj != null && !event.entity.worldObj.isRemote) {
            net.decimation.mod.server.screenshot.a.b((EntityPlayerMP) event.entity);
        }
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.target instanceof EntityItem && event.entityPlayer != null && !event.entityPlayer.worldObj.isRemote) {
            EntityItem groundItem = (EntityItem) event.target;
            EntityPlayer player = event.entityPlayer;
            player.inventory.addItemStackToInventory(groundItem.getEntityItem());
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entity.worldObj.isRemote) return;

        deci.aP.a netConn = getNetConnectionSafe();

        if (event.entity instanceof EntityPlayer) {
            EntityPlayer deadPlayer = (EntityPlayer) event.entity;
            deci.Q.b deadPlayerData = deci.Q.b.e(deadPlayer);

            if (deadPlayerData != null) {
                deadPlayerData.n(0L);
                deadPlayerData.bF();
            }

            spawnDeadPlayerBody(deadPlayer);

            if (netConn != null && netConn.aBX != null) {
                if (event.source.getEntity() instanceof EntityPlayer) {
                    Request_FromClient_AddKill request_FromClient_AddKill = new Request_FromClient_AddKill();
                    request_FromClient_AddKill.setAuthCode(deci.aJ.b.aAv);
                    request_FromClient_AddKill.setPlayerUUID(((EntityPlayer) event.source.getEntity()).getGameProfile().getId().toString());
                    request_FromClient_AddKill.setAmount(1);
                    netConn.aBX.sendTCP(request_FromClient_AddKill);
                }

                Request_FromClient_AddDeath request_FromClient_AddDeath = new Request_FromClient_AddDeath();
                request_FromClient_AddDeath.setAuthCode(deci.aJ.b.aAv);
                request_FromClient_AddDeath.setPlayerUUID(deadPlayer.getGameProfile().getId().toString());
                request_FromClient_AddDeath.setAmount(1);
                netConn.aBX.sendTCP(request_FromClient_AddDeath);
            }
        } else if (event.entity instanceof deci.ag.d) {
            if(event.source.getEntity() instanceof EntityPlayer) {
                if (netConn != null && netConn.aBX != null) {
                    Request_FromClient_AddZombieKill request_FromClient_AddZombieKill = new Request_FromClient_AddZombieKill();
                    request_FromClient_AddZombieKill.setAuthCode(deci.aJ.b.aAv);
                    request_FromClient_AddZombieKill.setAmount(1);
                    request_FromClient_AddZombieKill.setPlayerUUID(((EntityPlayer) event.source.getEntity()).getGameProfile().getId().toString());
                    netConn.aBX.sendTCP(request_FromClient_AddZombieKill);
                }
            } else {
                if (netConn != null && netConn.aBX != null) {
                    Request_FromClient_AddZombieKill request_FromClient_AddZombieKill = new Request_FromClient_AddZombieKill();
                    request_FromClient_AddZombieKill.setAuthCode(deci.aJ.b.aAv);
                    request_FromClient_AddZombieKill.setAmount(1);
                    request_FromClient_AddZombieKill.setPlayerUUID("Generic");
                    netConn.aBX.sendTCP(request_FromClient_AddZombieKill);
                }
            }
        }

        if ((event.entity instanceof deci.ah.d) && event.source.getEntity() instanceof deci.ag.d) {
            spawnHumanoidInfected(event);
        }

        if (event.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer killer = (EntityPlayer) event.source.getEntity();
            deci.Q.b killerData = deci.Q.b.e(killer);
            net.decimation.mod.server.clans.a clanManager = getClanManagerSafe();

            if (!(event.entityLiving instanceof EntityPlayer)) {
                if (clanManager != null) {
                    try {
                        clanManager.V(clanManager.v(killer));
                    } catch (IOException ignored) {
                    }
                } else if (killerData != null) {
                    killerData.I(1);
                }
            } else {
                deci.Q.b victimData = deci.Q.b.e((EntityPlayer) event.entityLiving);
                if (victimData != null && victimData.cc() <= 0L && victimData.cd() >= 50) {
                    if (clanManager != null) {
                        try {
                            clanManager.W(clanManager.v(killer));
                        } catch (IOException ignored) {
                        }
                    } else if (killerData != null) {
                        killerData.J(10);
                    }
                } else {
                    if (clanManager != null) {
                        try {
                            clanManager.V(clanManager.v(killer));
                        } catch (IOException ignored) {
                        }
                    } else if (killerData != null) {
                        killerData.I(1);
                    }
                }
            }
        }
    }

    private void spawnHumanoidInfected(LivingDeathEvent event) {
        deci.af.d humanoidZombie = new deci.af.d(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ);
        for (int slot = 1; slot < 4; slot++) {
            if (event.entityLiving.getEquipmentInSlot(slot) != null) {
                humanoidZombie.setCurrentItemOrArmor(slot, event.entityLiving.getEquipmentInSlot(slot).copy());
            }
        }

        if (event.entity instanceof deci.ah.d) {
            humanoidZombie.a((deci.ah.d) event.entity);
        }

        event.entity.worldObj.spawnEntityInWorld(humanoidZombie);
    }

    private void spawnDeadPlayerBody(EntityPlayer player) {
        deci.Q.b playerData = deci.Q.b.e(player);
        World world = player.worldObj;

        if (!world.isRemote && playerData != null) {
            deci.aF.a.a.a.gB().sendToAll(new deci.aE.a.p(player.getEntityId()));
            if (FMLCommonHandler.instance().getEffectiveSide().isServer() && !deci.aJ.b.aAP) return;

            deci.ae.a corpseEntity = new deci.ae.a(world);
            corpseEntity.setPositionAndRotation(player.posX, player.posY, player.posZ, player.getRotationYawHead(), 0.0F);
            corpseEntity.r(world.getTotalWorldTime());
            corpseEntity.E(player.getCommandSenderName());

            if (playerData.cb() != 0L) {
                int capMultiplier = 1 + RANDOM.nextInt(80);
                long capsDropped = playerData.cb() / 100L * capMultiplier;
                player.addChatComponentMessage(new net.minecraft.util.ChatComponentText(net.minecraft.util.EnumChatFormatting.DARK_RED + "-" + capsDropped + net.minecraft.util.EnumChatFormatting.GRAY + " bottlecaps"));
                corpseEntity.g(capsDropped);
                playerData.g(playerData.cb() - capsDropped);
            }

            if (!world.getGameRules().getGameRuleBooleanValue("keepInventory")) {
                ItemStack heldItem = player.getHeldItem();
                if (heldItem != null) {
                    corpseEntity.setCurrentItemOrArmor(0, heldItem.copy());
                    player.inventory.mainInventory[player.inventory.currentItem] = null;
                }

                for (int armorIndex = 0; armorIndex < 4; armorIndex++) {
                    ItemStack armor = player.getCurrentArmor(armorIndex);
                    if (armor != null) {
                        corpseEntity.setCurrentItemOrArmor(armorIndex + 1, armor.copy());
                        player.inventory.armorInventory[armorIndex] = null;
                    }
                }

                if (playerData.cv() != null) corpseEntity.setCurrentItemOrArmor(5, playerData.cv().copy());
                if (playerData.cw() != null) corpseEntity.setCurrentItemOrArmor(6, playerData.cw().copy());
                if (playerData.cx() != null) corpseEntity.setCurrentItemOrArmor(7, playerData.cx().copy());

                for (byte invIndex = 0; invIndex < player.inventory.mainInventory.length; invIndex++) {
                    ItemStack stack = player.inventory.mainInventory[invIndex];
                    if (stack != null) {
                        corpseEntity.g(stack.copy());
                        player.inventory.mainInventory[invIndex] = null;
                    }
                }

                player.inventory.clearInventory(null, -1);
            }

            world.spawnEntityInWorld(corpseEntity);
            corpseEntity.q(player.getRotationYawHead());
        }
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
        if (event.entityItem != null && event.entityItem.getEntityItem() != null) {
            Item droppedItem = event.entityItem.getEntityItem().getItem();

            if (droppedItem == Item.getItemFromBlock(Blocks.bedrock) && !event.player.capabilities.isCreativeMode) {
                event.setCanceled(true);
                return;
            }
            if (droppedItem == deci.aD.k.alo && !event.player.capabilities.isCreativeMode) {
                if (!event.player.worldObj.isRemote) {
                    event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You cannot drop Gold Bottle Caps there!"));
                }
                event.entityItem.setDead();
                return;
            }
            if (droppedItem instanceof deci.ay.i) {
                deci.Q.b playerData = deci.Q.b.e(event.player);
                if (playerData != null && playerData.bE()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (!event.entityPlayer.worldObj.isRemote) {
            if (deci.Q.b.e(event.entityPlayer) != null && deci.Q.b.e(event.original) != null) {
                deci.Q.b.e(event.entityPlayer).a(deci.Q.b.e(event.original));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) throws IOException {
        if (event.player.worldObj.isRemote) return;

        event.player.inventory.clearInventory(null, -1);
        event.player.inventoryContainer.detectAndSendChanges();

        deci.aP.a netConn = getNetConnectionSafe();
        if (netConn != null && netConn.aBX != null) {
            Request_FromClient_Supporter_Check request = new Request_FromClient_Supporter_Check();
            request.playerUUID = event.player.getGameProfile().getId().toString();
            netConn.aBX.sendTCP(request);
        }

        Addon.Network.PACKET.sendTo(new Addon.Network.Message_StopReloadS2C(), (EntityPlayerMP) event.player);

        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            File spawnProperties = new File("decimation_spawnpoints.properties");
            if (deci.aJ.b.aAj && spawnProperties.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(spawnProperties));
                ArrayList<String> pointsList = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) pointsList.add(line);

                spawnPoints = pointsList.toArray(new String[0]);
                reader.close();

                int randomIndex = RANDOM.nextInt(spawnPoints.length);
                String selectedPoint = spawnPoints[randomIndex];

                int pointX = Integer.parseInt(selectedPoint.split("/")[0]);
                int pointY = Integer.parseInt(selectedPoint.split("/")[1]);
                int pointZ = Integer.parseInt(selectedPoint.split("/")[2]);

                event.player.setPositionAndUpdate(pointX, (pointY + 1), pointZ);
                event.player.addPotionEffect(new PotionEffect(Potion.blindness.id, 40, 1, true));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player == null) return;
        deci.Q.b playerData = deci.Q.b.e(player);
        if (playerData == null) return;

        if (event.phase == TickEvent.Phase.START) {
            playerData.tick();

            if (playerData.cm()) {
                Integer currentGrowlTimerObj = growlTimers.get(player);
                int currentGrowlTimer = (currentGrowlTimerObj == null) ? 160 : currentGrowlTimerObj;

                if (currentGrowlTimer > 0) {
                    growlTimers.put(player, currentGrowlTimer - 1);
                } else {
                    if (RANDOM.nextInt(3) == 0) {
                        if (!player.worldObj.isRemote) {
                            player.worldObj.playSoundAtEntity(player, "deci:mob.infected.idle", 0.5F, 0.7F + RANDOM.nextFloat() * 0.4F);
                            if (player.isSprinting()) player.setSprinting(false);

                            cpw.mods.fml.common.network.NetworkRegistry.TargetPoint visualRange = new cpw.mods.fml.common.network.NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64.0D);

                            Addon.Network.PACKET.sendToAllAround(new Addon.Message_InfectionSpasm(player.getEntityId()), visualRange);
                        }
                    }
                    growlTimers.put(player, 160);
                }
            } else {
                growlTimers.remove(player);
            }

            if (player.ridingEntity instanceof deci.ad.e) {
                player.moveStrafing = 0.0F;
            }

            if (!player.worldObj.isRemote) {
                if (net.decimation.mod.server.zones.b.a(player, net.decimation.mod.server.zones.a.SAFEZONE)) {
                    playerData.v(20);
                } else if (net.decimation.mod.server.zones.b.a(player, a.BORDER) && !player.capabilities.isCreativeMode) {
                    player.attackEntityFrom(DamageSource.outOfWorld, 4F);
                } else {
                    if ((b.a(player, a.RADIATION) && !player.capabilities.isCreativeMode)) {
                        if (!playerData.co()) {
                            playerData.i(true);
                        }
                    } else {
                        playerData.i(false);
                    }
                }
            }

            if (b.b(player, a.RADIATION) && !player.capabilities.isCreativeMode) {
                if (Math.random() < 0.05) {
                    player.playSound("deci:misc.effect.irradiated", 1.0F, 1.0F);
                }
            }
        } else if (event.phase == TickEvent.Phase.END) {
            playerData.bx();

            if (!player.worldObj.isRemote) {
                if (net.decimation.mod.server.zones.b.a(player, a.SAFEZONE)) {
                    FoodStats stats = player.getFoodStats();
                    ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, stats, 0.0F, "foodExhaustionLevel", "field_75126_c");
                }

                net.decimation.mod.server.screenshot.a tracker = net.decimation.mod.server.screenshot.a.z(player);
                if (tracker != null) {
                    tracker.s(System.currentTimeMillis());
                }

                if (player.isSneaking() && playerData.bM() == 2) {
                    player.setSneaking(false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onStartTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (!event.entityPlayer.worldObj.isRemote && event.target instanceof EntityPlayer) {
            deci.Q.b trackedPlayerData = deci.Q.b.e((EntityPlayer) event.target);
            if (trackedPlayerData != null) {
                deci.aF.a.a.a.gB().sendTo(new deci.aE.a.Z(trackedPlayerData), (EntityPlayerMP) event.entityPlayer);
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.world.isRemote) {
            if (event.world.getWorldTime() > 24000L) {
                event.world.setWorldTime(0L);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.worldObj.isRemote) return;

        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            deci.Q.b playerData = deci.Q.b.e(player);

            if (playerData != null) {
                if (FMLCommonHandler.instance().getEffectiveSide().isServer() && deci.aJ.b.aAC) {
                    playerData.cS();
                } else if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                    playerData.cS();
                }
            }

            deci.aF.a.a.a.gB().sendTo(new deci.aE.a.i(), player);
            Addon.Network.PACKET.sendTo(new Addon.Network.Message_WorldProperties(fogDensity_current), player);

            System.out.println("Sent fog: " + fogDensity_current + " to " + player.getDisplayName());
        }
    }
}
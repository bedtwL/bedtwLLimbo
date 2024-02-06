package me.bedtwl.bedtwllimbo;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;

public final class BedtwLLimbo extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Loading bedtwL Limbo by bedtwL");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginCommand("setlobby").setExecutor(new SetLobby(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    sendTitle(p);
                }
            }
        }.runTaskTimer(this, 0, 20);

        spawnlocation=stringToLocation(getConfig().getString("lobby-location"));

    }
    public static void sendTitle(Player player) {

        CraftPlayer craftPlayer = (CraftPlayer) player;

        IChatBaseComponent titleComponent= IChatBaseComponent.ChatSerializer.a("{\"text\":\"You are AFK\",\"color\":\"red\"}");
        IChatBaseComponent subtitleComponent=IChatBaseComponent.ChatSerializer.a("{\"text\":\"Move around to return Lobby\",\"color\":\"yellow\"}");


        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
                titleComponent, 0, 60, 0);
        PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                subtitleComponent);

        craftPlayer.getHandle().playerConnection.sendPacket(packetTitle);
        craftPlayer.getHandle().playerConnection.sendPacket(packetSubtitle);
    }


    public static String locationToString(final Location loc) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ()+", "+loc.getYaw()+", "+loc.getPitch();
    }

    public static Location stringToLocation(final String string) {
        final String[] split = string.split(",");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]),Float.valueOf(split[4]),Float.valueOf(split[5]));
    }
    Location spawnlocation;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        if (spawnlocation!=null)
           e.getPlayer().teleport(spawnlocation);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.hidePlayer(e.getPlayer());
            e.getPlayer().hidePlayer(p);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        e.setCancelled(true);
    }
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e)
    {
        e.setCancelled(true);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e)
    {
        e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerChat(PlayerChatEvent e)
    {
        SendPlayerToLobby(e.getPlayer());
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent e)
    {
        Sending.remove(e.getPlayer());e.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        SendPlayerToLobby(e.getPlayer());
    }

    ArrayList<Player> Sending=new ArrayList<>();
    public void SendPlayerToLobby(Player p)
    {
        if (Sending.contains(p))
            return;
        Sending.add(p);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getConfig().getString("lobby-server"));

        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

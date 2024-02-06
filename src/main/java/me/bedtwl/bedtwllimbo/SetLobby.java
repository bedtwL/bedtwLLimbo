package me.bedtwl.bedtwllimbo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static me.bedtwl.bedtwllimbo.BedtwLLimbo.locationToString;

public class SetLobby implements CommandExecutor {
    BedtwLLimbo plugin;
    public SetLobby(BedtwLLimbo limbo)
    {
        plugin=limbo;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p= (Player)sender;
        if (p.hasPermission("bedtwl.limbo.setlobby")| p.isOp())
        {
            plugin.spawnlocation=p.getLocation();
            plugin.getConfig().set("lobby-location",locationToString(p.getLocation()));
            plugin.saveConfig();
            p.sendMessage(ChatColor.GOLD+"[bedtwL Limbo]"+ChatColor.GREEN+" Setted Lobby Location");
        }
        else{
            p.sendMessage(ChatColor.RED+"You dont have permission!");
        }
        return true;
    }
}

package com.github.games647.fastlogin.bukkit.commands;

import com.github.games647.fastlogin.bukkit.FastLoginBukkit;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Let users activate fast login by command. This only be accessible if
 * the user has access to it's account. So we can make sure that not another
 * person with a paid account and the same username can steal his account.
 */
public class PremiumCommand implements CommandExecutor {

    private final FastLoginBukkit plugin;

    public PremiumCommand(FastLoginBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                //console or command block
                sender.sendMessage(ChatColor.DARK_RED + "Only players can add themselves as premium");
                return true;
            }

            String playerName = sender.getName();
            boolean exist = plugin.getEnabledPremium().add(playerName);
            if (exist) {
                sender.sendMessage(ChatColor.DARK_RED + "You are already on the premium list");
            } else {
                sender.sendMessage(ChatColor.DARK_GREEN + "Added to the list of premium players");
            }

            notifiyBungeeCord((Player) sender);
            return true;
        } else {
            String playerName = args[0];
            plugin.getEnabledPremium().add(playerName);
            sender.sendMessage(ChatColor.DARK_GREEN + "Added player to the list of premium players");
//            notifiyBungeeCord();
        }

        return true;
    }

    private void notifiyBungeeCord(Player target) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("ON");

        target.sendPluginMessage(plugin, plugin.getName(), dataOutput.toByteArray());
    }
}

package me.zinno.zinnospigotcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public interface SubCommand extends TabCompleter {
	
	void runCommand(CommandSender sender, Command cmd, String label, String[] args);
	
	String permissionLabel();
}

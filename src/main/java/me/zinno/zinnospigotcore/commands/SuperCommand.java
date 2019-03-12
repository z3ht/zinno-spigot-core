package me.zinno.zinnospigotcore.commands;

import me.zinno.zinnospigotcore.utils.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public abstract class SuperCommand implements CommandExecutor, TabCompleter {
	
	private Map<List<String>, SubCommand> subCommandHashMap = new HashMap<>();
	private final String permissionLabel;
	private final Preconditions preconditions;
	
	public SuperCommand(String permissionLabel) {
		this(
				new Preconditions(
					ChatColor.RED.toString() + ChatColor.BOLD + "No permission!",
					ChatColor.RED.toString() + ChatColor.BOLD + "Command not found."
				),
				permissionLabel
		);
	}
	
	public SuperCommand(Preconditions preconditions, String permissionLabel) {
		this.preconditions = preconditions;
		this.permissionLabel = permissionLabel;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!preconditions.senderHasPerms(sender, true, permissionLabel))
			return true;
		
		if(args.length == 0) {
			executeBasicCommand(sender, cmd, label, args);
			return true;
		}
		
		executeSubCommand(sender, cmd, label, args);
		
		return true;
	}
	
	abstract public void executeBasicCommand(CommandSender sender, Command cmd, String label, String[] args);
	
	public void executeSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
		SubCommand subCommand = findAvailableSubCommandFromString(args[0], sender,true);
		
		if(subCommand == null) {
			sender.sendMessage(preconditions.getNoCommandMessage());
			return;
		}
		
		subCommand.runCommand(sender, cmd, label, args);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		Preconditions cmdHelper = preconditions;
		if(args.length == 0)
			return getAvailableSubCommandAliasList(sender);
		
		if(args.length > 1) {
			SubCommand possibleCommand = findAvailableSubCommandFromString(args[0], sender, false);
			if(possibleCommand == null || possibleCommand.permissionLabel() == null
					|| !cmdHelper.senderHasPerms(sender, possibleCommand.permissionLabel()))
				return null;
			return possibleCommand.onTabComplete(sender, cmd, label, args);
		}
		
		List<String> refinedList = new ArrayList<String>();
		for(String item : getAvailableSubCommandAliasList(sender)) {
			if(item.startsWith(args[0].toLowerCase()))
				refinedList.add(item);
		}
		return refinedList;
	}
	
	public void addCommand(List<String> commandNames, SubCommand subCommand) {
		subCommandHashMap.put(commandNames, subCommand);
	}
	
	private SubCommand findAvailableSubCommandFromString(String arg, CommandSender sender, boolean shouldFindIntendedSubCommand) {
		SubCommand possibleSubCommand = null;
		for(List<String> subCommandNames : subCommandHashMap.keySet()) {
			if(subCommandHashMap.get(subCommandNames).permissionLabel() != null &&
					!(preconditions.senderHasPerms(sender, false,
							subCommandHashMap.get(subCommandNames).permissionLabel())))
				continue;
			for(String subCommandName : subCommandNames) {
				if (subCommandName.toLowerCase().equals(arg.toLowerCase())) {
					return subCommandHashMap.get(subCommandNames);
				}
			}
		}
		
		if(shouldFindIntendedSubCommand)
			return findIntendedSubCommandFromString(arg, sender);
		
		return null;
	}
	
	private SubCommand findIntendedSubCommandFromString(String arg, CommandSender sender) {
		String intendedString = null;
		for (String possibleAlias : getAvailableSubCommandAliasList(sender)) {
			if (possibleAlias.toLowerCase().startsWith(arg.toLowerCase())) {
				if (intendedString != null)
					return null;
				intendedString = possibleAlias;
			}
		}
		if (intendedString == null)
			return null;
		return findAvailableSubCommandFromString(intendedString, sender, false);
	}
	
	private List<String> getAvailableSubCommandAliasList(CommandSender sender) {
		List<String> availableAliasList = new ArrayList<>();
		for(List<String> subCommandNames : subCommandHashMap.keySet()) {
			if(subCommandHashMap.get(subCommandNames).permissionLabel() != null) {
				if(!(preconditions.senderHasPerms(sender, subCommandHashMap.get(subCommandNames).permissionLabel()))) {
					continue;
				}
			}
			for(String item : subCommandNames)
				availableAliasList.add(item.toLowerCase());
		}
		Collections.sort(availableAliasList);
		return availableAliasList;
	}
	
	private List<String> getSubCommandAliasList() {
		List<String> subCommands = new ArrayList<>();
		for(List<String> subCommandKeys : subCommandHashMap.keySet())
			for(String item : subCommandKeys)
				subCommands.add(item.toLowerCase());
		Collections.sort(subCommands);
		return subCommands;
	}

}

package me.zinno.zinnospigotcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigFile {
	
	// Warning! ConfigFile is VERY fragile!
	//     -    Do not have multiple instances of the yaml config!
	//     -    Always set and change the yaml config with the same reference
	//     -    This will NOT work with any more than 1 thread
	
	private Plugin plugin;
	private String name;
	private String path;
	private FileConfiguration configYaml;
	private File configFile;
	
	public ConfigFile(Plugin plugin, String path, String name) {
		
		this.plugin = plugin;
		this.name = name;
		this.path = path;
		
		initialize();
	}
	
	private void initialize() {
		if(!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdir();
		
		String fileLoc = null;
		if(path == null)
			fileLoc = plugin.getDataFolder().toString();
		else
			fileLoc = plugin.getDataFolder().toString() + path.replace("/", File.pathSeparator);
		
		this.configFile = new File(fileLoc, name);
		
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (Exception ex) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create " + name + " file.");
				ex.printStackTrace();
			}
		}
		
		configYaml = YamlConfiguration.loadConfiguration(configFile);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The " + name + " file has been created");
		
	}
	
	public FileConfiguration getConfigYaml() {
		if(configYaml == null)
			initialize();
		if(configYaml == null) {
			try {
				throw new Exception();
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The config file could not be found!");
			}
		}
		return configYaml;
	}
	
	public void saveConfig() {
		try {
			configYaml.save(configFile);
		} catch (Exception ex) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not save config file!");
		}
	}
	
	public FileConfiguration reloadConfig() {
		configYaml = YamlConfiguration.loadConfiguration(configFile);
		return configYaml;
	}
}

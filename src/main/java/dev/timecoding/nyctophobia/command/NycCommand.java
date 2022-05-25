package dev.timecoding.nyctophobia.command;

import dev.timecoding.nyctophobia.Nyctophobia;
import dev.timecoding.nyctophobia.file.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NycCommand implements CommandExecutor {

    ConfigManager config = Nyctophobia.config;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String helpperm = config.getString("Permission.HelpCommand");
        String rlperm = config.getString("Permission.ReloadCommand");
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                if(sender.hasPermission(helpperm)){
                    sender.sendMessage("§aCommands: §e/nyc help, /nyc reload");
                }else{
                    sender.sendMessage("§cYou don't have the permission to do this!");
                }
            }else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")){
                if(sender.hasPermission(rlperm)){
                    sender.sendMessage("§aYou have successfully reloaded the config of the plugin!");
                    //Reload Config
                    config.reloadConfig();
                }else{
                    sender.sendMessage("§cYou don't have the permission to do this!");
                }
            }
        }else{
            if(sender.hasPermission(helpperm)){
                sender.sendMessage("§cType /nyc help for help!");
            }else{
                sender.sendMessage("§cYou don't have the permission to do this!");
            }
        }
        return false;
    }
}

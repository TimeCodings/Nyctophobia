package dev.timecoding.nyctophobia.command;

import dev.timecoding.nyctophobia.Nyctophobia;
import dev.timecoding.nyctophobia.file.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NycCommand implements CommandExecutor {

    //Get Configmanager Instance
    private ConfigManager config = Nyctophobia.plugin.getPluginConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String helpperm = config.getString("Permission.HelpCommand");
        String rlperm = config.getString("Permission.ReloadCommand");
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                if(sender.hasPermission(helpperm)){
                    sender.sendMessage(ChatColor.GREEN+"Commands: "+ChatColor.YELLOW+"/nyc help, /nyc reload");
                }else{
                    sender.sendMessage(ChatColor.RED+"You don't have the permission to do this!");
                }
            }else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")){
                if(sender.hasPermission(rlperm)){
                    sender.sendMessage(ChatColor.GREEN+"You have successfully reloaded the config of the plugin!");
                    //Reload Config
                    config.reloadConfig();
                }else{
                    sender.sendMessage(ChatColor.RED+"You don't have the permission to do this!");
                }
            }
        }else{
            if(sender.hasPermission(helpperm)){
                sender.sendMessage(ChatColor.RED+"Type /nyc help for help!");
            }else{
                sender.sendMessage(ChatColor.RED+"You don't have the permission to do this!");
            }
        }
        return false;
    }
}

package dev.timecoding.nyctophobia.command;

import dev.timecoding.nyctophobia.Nyctophobia;
import dev.timecoding.nyctophobia.file.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NycTabComplete implements TabCompleter {

    ConfigManager config = Nyctophobia.config;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("nyc") || command.getName().equalsIgnoreCase("nyctophobia")){
            Player p = (Player) sender;
            String helpperm = config.getString("Permission.HelpCommand");
            String rlperm = config.getString("Permission.ReloadCommand");
            List<String> completer = new ArrayList<>();
            if(p.hasPermission(helpperm)){
                completer.add("help");
            }
            if(p.hasPermission(rlperm)){
                completer.add("reload");
            }
            return completer;
        }
        return null;
    }
}

package dev.timecoding.nyctophobia.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private Plugin plugin;
    private File file;
    private YamlConfiguration cfg;

    public ConfigManager(Plugin plugin, String filename){
        this.plugin = plugin;
        this.file = new File("plugins//Nyctophobia", filename+".yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
    }

    public void init(){
        cfg.options().header("Hello, thanks for installing my plugin! This plugin was programmed in a short amount of time, which means the quality of the plugin is not that good! Of course there will be many more updates! (You can help me with this). Now the most important functions: With MaxLighting and MaxBlockLighting you can set how much the block has to light up for the events to be activated. At MustBeNightInWorld you can set whether it must be night in the world for the event! With MonsterRequirement you can specify how many monsters must be nearby! For events you can set what happens when all actions occur. (Placeholders: %player% for the playername and %uuid% for the UUID; Add a \"-\" to Titles to add a subtitle; Add - console to the end of a command to run the command in the console!) Attached Cooldown you can set the cooldown for the action! Have fun with the plugin! If you have any questions or suggestions for improvement, just join my discord: https://discord.gg/8QWmU4ebCC");
        cfg.options().copyDefaults(true);
        save();
    }

    public void save(){
        try {
            cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDefaultString(String key, String value){
        cfg.addDefault(key, value);
        save();
    }

    public void addDefaultInt(String key, Integer value){
        cfg.addDefault(key, value);
        save();
    }

    public void addDefaultBoolean(String key, boolean value){
        cfg.addDefault(key, value);
        save();
    }

    public void addDefaultList(String key, String previewvalue){
        List<String> list = new ArrayList<>();
        //Add Preview
        list.add(previewvalue);
        //Add + Save
        cfg.addDefault(key, list);
        save();
    }

    public String getString(String key){
        if(keyExists(key)){
            return cfg.getString(key).replace("&", "§");
        }
        return "NO VALUE";
    }

    public Integer getInt(String key){
        if(keyExists(key)){
            return cfg.getInt(key);
        }
        return 0;
    }

    public Boolean getBoolean(String key){
        if(keyExists(key)){
            return cfg.getBoolean(key);
        }
        return false;
    }

    public List<String> getList(String key){
        if(keyExists(key)){
            List<String> list = cfg.getStringList(key);
            List<String> newlist = new ArrayList<>();
            for(String s : list){
                newlist.add(s.replace("&","§"));
            }
            return newlist;
        }
        return new ArrayList<String>();
    }

    public boolean keyExists(String key){
        if(cfg.get(key) != null){
            return true;
        }
        return false;
    }

}

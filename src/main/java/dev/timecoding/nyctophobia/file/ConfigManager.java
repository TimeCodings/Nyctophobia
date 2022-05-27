package dev.timecoding.nyctophobia.file;

import dev.timecoding.nyctophobia.Nyctophobia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private Plugin plugin;
    private File file;
    private YamlConfiguration cfg;

    public ConfigManager(Plugin plugin, String filename) {
        this.plugin = plugin;
        this.file = new File("plugins//Nyctophobia", filename + ".yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
    }

    public void init() {
        //If NoteblockAPI exists, create musicfolder
        createMusicFolder();
        //Delete Old Backup Folder
        deleteOldBackupFolder();
    }

    public void deleteOldBackupFolder(){
        File folder = new File("plugins//Nyctophobia//backup");
        if(folder.exists()){
            //Clean directory
            for(File f : folder.listFiles()){
                if(!f.isDirectory()){
                    f.delete();
                }
            }
            //Delete Directory
            try {
                Files.delete(folder.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Sucessfully deleted the old backup folder (AUTOUPDATER v1.3.2 UPDATE)!");
        }
    }

    public void reloadConfig(){
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
    }

    public void createMusicFolder(){
        File folder = new File("plugins//Nyctophobia//music");
        if(!folder.exists() && Nyctophobia.plugin.nbapienabled){
            folder.mkdir();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Sucessfully created a new music folder!");
        }
    }

    public void save() {
        try {
            cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDefaultString(String key, String value) {
        cfg.addDefault(key, value);
        save();
    }

    public void addDefaultInt(String key, Integer value) {
        cfg.addDefault(key, value);
        save();
    }

    public void addDefaultBoolean(String key, boolean value) {
        cfg.addDefault(key, value);
        save();
    }

    public void copyDefaults(boolean b){
        cfg.options().copyDefaults(b);
    }

    public void addDefaultList(String key, String previewvalue) {
        List<String> list = new ArrayList<>();
        //Add Preview
        list.add(previewvalue);
        //Add + Save
        cfg.addDefault(key, list);
        save();
    }

    public void set(String key, Object object){
        cfg.set(key, object);
        save();
    }

    public String getString(String key) {
        if (keyExists(key)) {
            return cfg.getString(key).replace("&", "§");
        }
        return "NO VALUE";
    }

    public Map<String, Object> getValues(boolean deep) {
        return cfg.getValues(deep);
    }

    public boolean keyExists(String key) {
        if (cfg.get(key) != null) {
            return true;
        }
        return false;
    }

    public Integer getInt(String key) {
        if (keyExists(key)) {
            return cfg.getInt(key);
        }
        return 0;
    }

    public Boolean getBoolean(String key) {
        if (keyExists(key)) {
            return cfg.getBoolean(key);
        }else if(key.equalsIgnoreCase("AutoUpdater")){
            return true;
        }
        return false;
    }

    public List<String> getList(String key){
        if(keyExists(key)){
            List<String> s = cfg.getStringList(key);
            List<String> ns = new ArrayList<>();
            for(String sa : s){
                ns.add(sa.replace("&", "§"));
            }
            return ns;
        }
        return new ArrayList<String>();
    }

}
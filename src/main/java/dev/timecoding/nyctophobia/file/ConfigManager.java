package dev.timecoding.nyctophobia.file;

import dev.timecoding.nyctophobia.Nyctophobia;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private Plugin plugin;
    private File file;
    public YamlConfiguration cfg;

    public ConfigManager(Plugin plugin, String filename) {
        this.plugin = plugin;
        this.file = new File("plugins//Nyctophobia", filename + ".yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
        cfg.options().copyDefaults(true);
        pluginversion = plugin.getDescription().getVersion();
    }

    public void init() {
        if (file.exists()) {
            //ConfigUpdate
            configUpdate(file);
        } else {
            //Load Default
            plugin.saveResource("config.yml", false);
            Bukkit.getConsoleSender().sendMessage("No config found! Creating a new config...");
        }
        //Checking for updates
        Nyctophobia.plugin.updateavailable = updateAvailable();
        if (Nyctophobia.plugin.updateavailable) {
            Bukkit.getConsoleSender().sendMessage("§cA new update is available! To guarantee the best gaming experience, please download the new update from this link: §ehttps://www.spigotmc.org/resources/nyctophobia.102177/");
        }
    }

    private String resourceid = "102177";
    private String baseurl = "https://api.spigotmc.org/legacy/update.php?resource=";

    public String getUpdateVersion() {
        String aversion = plugin.getDescription().getVersion();
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) new URL(baseurl + resourceid).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        String raw = null;
        try {
            raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return raw;
    }

    public boolean updateAvailable() {
        Bukkit.getConsoleSender().sendMessage("Checking for updates...");
        try {
            String aversion = plugin.getDescription().getVersion();
            HttpsURLConnection connection = (HttpsURLConnection) new URL(baseurl + resourceid).openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            String remoteVersion;
            if (raw.contains("-")) {
                remoteVersion = raw.split("-")[0].trim();
            } else {
                remoteVersion = raw;
            }
            if (!aversion.equalsIgnoreCase(remoteVersion))
                return true;
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private String pluginversion;

    public void configUpdate(File f) {
        if ((f.exists()) && (!this.getString("config-version").equalsIgnoreCase(pluginversion)) && (this.getString("config-version") != null)) {
            Bukkit.getConsoleSender().sendMessage("§cConfig version doesn't match, deleting and recreating...");
            f.delete();
            plugin.saveResource("config.yml", false);
            Bukkit.getConsoleSender().sendMessage("§aSuccessfully deleted and recreated the config.");
        } else if ((f.exists()) && (!this.getString("config-version").equalsIgnoreCase(pluginversion))) {
            Bukkit.getConsoleSender().sendMessage("§cConfig version doesn't match, deleting and recreating...");
            f.delete();
            plugin.saveResource("config.yml", false);
            Bukkit.getConsoleSender().sendMessage("§aSuccessfully deleted and recreated the config.");
        } else if ((f.exists()) && (this.getString("config-version").equalsIgnoreCase(pluginversion))) {
            Bukkit.getConsoleSender().sendMessage("§aConfig version matched, processing load... Success");
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

    public void addDefaultList(String key, String previewvalue) {
        List<String> list = new ArrayList<>();
        //Add Preview
        list.add(previewvalue);
        //Add + Save
        cfg.addDefault(key, list);
        save();
    }

    public String getString(String key) {
        if (keyExists(key)) {
            return cfg.getString(key).replace("&", "§");
        }
        return "NO VALUE";
    }

    private boolean keyExists(String key) {
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
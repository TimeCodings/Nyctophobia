package dev.timecoding.nyctophobia;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import dev.timecoding.nyctophobia.api.AutoUpdater;
import dev.timecoding.nyctophobia.api.Metrics;
import dev.timecoding.nyctophobia.command.NycCommand;
import dev.timecoding.nyctophobia.command.NycTabComplete;
import dev.timecoding.nyctophobia.event.DarknessLeaveEvent;
import dev.timecoding.nyctophobia.file.ConfigManager;
import dev.timecoding.nyctophobia.listener.MoveListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Nyctophobia extends JavaPlugin implements Listener {

    public static Nyctophobia plugin;

    private ArrayList<Player> cooldown = new ArrayList<>();
    private ConfigManager config;

    public HashMap<Player, RadioSongPlayer> songs = new HashMap<>();
    public boolean updateavailable = false;
    public boolean nbapienabled = false;

    @Override
    public void onEnable() {
        //Init
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+"Nyctophobia "+ChatColor.GREEN+"up and running "+ChatColor.RED+"v"+getDescription().getVersion());
        //Register Instance
        plugin = this;
        config = new ConfigManager(plugin, "config");
        //Check for NoteBlockAPI
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+"Searching for "+ChatColor.GREEN+"NoteBlockAPI...");
        if(getServer().getPluginManager().isPluginEnabled("NoteBlockAPI")){
            nbapienabled = true;
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+"NoteBlockAPI "+ChatColor.GREEN+"was found! Registering MusicModule...");
        }else{
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+"NoteBlockAPI "+ChatColor.RED+"was not found! If you want to use custom NBS music please install NoteBlockAPI ("+ChatColor.YELLOW+"https://www.spigotmc.org/resources/noteblockapi.19287/)");
        }
        //Init Config
        config.init();
        //Register Listeners
        getServer().getPluginManager().registerEvents(new MoveListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
        //Register Commands and TabCompleter
        getCommand("nyc").setExecutor(new NycCommand());
        getCommand("nyc").setTabCompleter(new NycTabComplete());
        //Checking for updates with the AutoUpdater
        AutoUpdater updater = new AutoUpdater(this);
        updateavailable = updater.pluginUpdateAvailable();
        updater.checkForConfigUpdate();
        updater.checkForPluginUpdate();
        //Enable/Disable bStats
        if(config.getBoolean("bStats")){
            int pluginId = 15276;
            Metrics metrics = new Metrics(this, pluginId);
        }
    }

    public ConfigManager getPluginConfig(){
        return this.config;
    }

    public void onDisable(){
        //Disable Plugin
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+"Nytophobia "+ChatColor.RED+"got disabled!");
        //If NoteBlockAPI enabled remove all songs
        if(nbapienabled){
            for(Player player : songs.keySet()){
                RadioSongPlayer rsp = songs.get(player);
                rsp.destroy();
            }
            songs.clear();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(songs.containsKey(p)){
            songs.get(p).destroy();
            songs.remove(p);
        }
        if (p.isOp() && updateavailable) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
            p.sendMessage(ChatColor.WHITE+"Nyctophobia | "+ChatColor.RED+"A new update is available! To guarantee the best gaming experience, please download the new update from this link: "+ChatColor.YELLOW+"https://www.spigotmc.org/resources/nyctophobia.102177/");
        }
    }

    public boolean cooldownEnabled(Player p){
        if(cooldown.contains(p)){
            return true;
        }
        return false;
    }

    public void enableCooldown(Player p){
        if(!cooldownEnabled(p) && config.getBoolean("Cooldown.Enabled")){
            cooldown.add(p);
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    cooldown.remove(p);
                }
            }, 20*config.getInt("Cooldown.InSeconds"));
        }
    }

    public boolean isDay(Player player) {
        Server server = getServer();
        long time = server.getWorld(player.getWorld().getName()).getTime();
        boolean b = time < 12300 || time > 23850;
        return b;
    }

}

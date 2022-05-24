package dev.timecoding.nyctophobia;

import dev.timecoding.nyctophobia.api.Metrics;
import dev.timecoding.nyctophobia.command.NycCommand;
import dev.timecoding.nyctophobia.command.NycTabComplete;
import dev.timecoding.nyctophobia.file.ConfigManager;
import dev.timecoding.nyctophobia.listener.MoveListener;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Nyctophobia extends JavaPlugin implements Listener {

    public static Nyctophobia plugin;
    public static ConfigManager config;
    private ArrayList<Player> cooldown = new ArrayList<>();
    public boolean updateavailable = false;

    @Override
    public void onEnable() {
        //Init
        Bukkit.getConsoleSender().sendMessage("§eNyctophobia §aup and running §cv"+getDescription().getVersion());
        //Register Instance
        plugin = this;
        config = new ConfigManager(plugin, "config");
        //Init Config
        config.init();
        //Register Listeners
        getServer().getPluginManager().registerEvents(new MoveListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
        //Register Commands and TabCompleter
        getCommand("nyc").setExecutor(new NycCommand());
        getCommand("nyc").setTabCompleter(new NycTabComplete());
        //bStats
        if(config.getBoolean("bStats")){
            int pluginId = 15276;
            Metrics metrics = new Metrics(this, pluginId);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(p.isOp()){
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
            p.sendMessage("§fNyctophobia | §cA new update is available! To guarantee the best gaming experience, please download the new update from this link: §ehttps://www.spigotmc.org/resources/nyctophobia.102177/");
        }
    }

    public boolean cooldownEnabled(Player p){
        if(cooldown.contains(p)){
            return true;
        }
        return false;
    }

    public void activateCooldown(Player p){
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

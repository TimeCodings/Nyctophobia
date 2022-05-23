package dev.timecoding.nyctophobia;

import dev.timecoding.nyctophobia.bstats.Metrics;
import dev.timecoding.nyctophobia.file.ConfigManager;
import dev.timecoding.nyctophobia.listener.MoveListener;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;

public final class Nyctophobia extends JavaPlugin {

    public static Nyctophobia plugin;
    public static ConfigManager config;
    private ArrayList<Player> cooldown = new ArrayList<>();

    @Override
    public void onEnable() {
        //Init
        Bukkit.getConsoleSender().sendMessage("§aNyctophobia §bby TimeCode §e(Created in short amount of time) §cv"+getDescription().getVersion());
        //Register Instance
        plugin = this;
        config = new ConfigManager(plugin, "config");
        //Add Defaults (Config)
        addDefaults();
        //Register Listener
        getServer().getPluginManager().registerEvents(new MoveListener(), this);
        //bStats
        if(config.getBoolean("bStats")){
            int pluginId = 15276;
            Metrics metrics = new Metrics(this, pluginId);
        }
    }

    public void addDefaults(){
        //Init
        config.init();
        //Enabled
        config.addDefaultBoolean("Enabled", true);
        config.addDefaultBoolean("bStats", true);
        //Lighting
        config.addDefaultInt("MaxLighting", 0);
        config.addDefaultInt("MaxBlockLighting", 1);
        //NightValue
        config.addDefaultBoolean("MustBeNightInWorld", false);
        //MonsterRequirement
        config.addDefaultBoolean("MonsterRequirement.Enabled", true);
        config.addDefaultInt("MonsterRequirement.MinMonsters", 2);
        config.addDefaultInt("MonsterRequirement.RadiusInBlocks", 10);
        //Events
        config.addDefaultBoolean("Events.RandomTitle", false);
        config.addDefaultBoolean("Events.RandomMessage", false);
        config.addDefaultBoolean("Events.RandomPotion", false);
        config.addDefaultBoolean("Events.RandomSound", false);
        config.addDefaultBoolean("Events.RandomCommand", false);
        config.addDefaultList("Events.Titles", "&8&lIt is so dark! - &0Dark...");
        config.addDefaultList("Events.Messages", "&8Why it's so dark here? &0Help me! &eGet a torch to light up!");
        config.addDefaultList("Events.Potions", "BLINDNESS");
        config.addDefaultList("Events.Sounds", "BLOCK_ANVIL_BREAK");
        config.addDefaultList("Events.Commands", "give %player% apple 1 - console");
        //Cooldown
        config.addDefaultBoolean("Cooldown.Enabled", true);
        config.addDefaultInt("Cooldown.InSeconds", 5);
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

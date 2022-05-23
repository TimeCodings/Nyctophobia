package dev.timecoding.nyctophobia.listener;

import dev.timecoding.nyctophobia.Nyctophobia;
import dev.timecoding.nyctophobia.event.DarknessEnterEvent;
import dev.timecoding.nyctophobia.event.DarknessLeaveEvent;
import dev.timecoding.nyctophobia.file.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MoveListener implements Listener {

    private ConfigManager config = Nyctophobia.config;
    private Nyctophobia plugin = Nyctophobia.plugin;
    private List<Player> playerindarkness = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Location loc = player.getLocation();
        Block b = loc.getBlock();

        //Get ConfigValues
        boolean enabled = config.getBoolean("Enabled");
        int maxlighting = config.getInt("MaxLighting");
        int maxblocklighting = config.getInt("MaxBlockLighting");
        boolean night = config.getBoolean("MustBeNightInWorld");

        if(enabled) {
            if(night && !plugin.isDay(player) || !night){
                //Get Lightlevels
                String lightlevel = String.valueOf(b.getLightLevel());
                String skylightlevel = String.valueOf(b.getLightFromSky());
                //Test
                if(Integer.valueOf(lightlevel) <= maxlighting && Integer.valueOf(skylightlevel) <= maxblocklighting){
                    if(!playerindarkness.contains(player)) {
                        //Call Events
                        DarknessEnterEvent darkevent = new DarknessEnterEvent(player);
                        Bukkit.getPluginManager().callEvent(darkevent);
                        //Check for Monsters
                        boolean monsterr = config.getBoolean("MonsterRequirement.Enabled");
                        int blocks = config.getInt("MonsterRequirement.RadiusInBlocks");
                        int monsters = config.getInt("MonsterRequirement.MinMonsters");
                        boolean monster = true;
                        if(monsterr){
                            List<Entity> nearby = player.getNearbyEntities(blocks, 10, blocks);
                            int count = 0;
                            for(Entity entity : nearby){
                                if(entity instanceof Monster){
                                    count++;
                                }
                            }
                            if(count < monsters){
                                monster = false;
                            }
                        }
                        //Actions
                        if (!darkevent.isCancelled() && !plugin.cooldownEnabled(player) && monster) {
                            //Add To List
                            playerindarkness.add(player);
                            //Start Cooldown, if activated
                            plugin.activateCooldown(player);
                            //Random Functions
                            boolean rtitle = config.getBoolean("Events.RandomTitle");
                            boolean rmsg = config.getBoolean("Events.RandomMessage");
                            boolean rpotion = config.getBoolean("Events.RandomPotion");
                            boolean rsound = config.getBoolean("Events.RandomSound");
                            boolean rcmd = config.getBoolean("Events.RandomCommand");
                            //Listes
                            List<String> titlel = config.getList("Events.Titles");
                            List<String> msgl = config.getList("Events.Messages");
                            List<String> potl = config.getList("Events.Potions");
                            List<String> soundl = config.getList("Events.Sounds");
                            List<String> cmdl = config.getList("Events.Commands");
                            //Check and apply
                            Random r = new Random();
                            List<String> list = null;
                            String s;
                            list = titlel;
                            if (rtitle) {
                                int size = list.size();
                                int random = r.nextInt(size);
                                s = list.get(random);
                                if (s.contains(" - ")) {
                                    ArrayList<String> split = new ArrayList<String>(Arrays.asList(s.split(" - ")));
                                    player.sendTitle(split.get(0), split.get(1));
                                } else {
                                    player.sendTitle(s, "");
                                }
                            } else {
                                for (String st : list) {
                                    if (st.contains(" - ")) {
                                        ArrayList<String> split = new ArrayList<String>(Arrays.asList(st.split(" - ")));
                                        player.sendTitle(split.get(0), split.get(1));
                                    } else {
                                        player.sendTitle(st, "");
                                    }
                                }
                            }
                            list = msgl;
                            if (rmsg) {
                                int size = list.size();
                                int random = r.nextInt(size);
                                s = list.get(random);
                                player.sendMessage(s);
                            } else {
                                for (String st : list) {
                                    player.sendMessage(st);
                                }
                            }
                            list = potl;
                            if (rpotion) {
                                int size = list.size();
                                int random = r.nextInt(size);
                                s = list.get(random);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(s), 999999999, 999999999));
                            } else {
                                for (String st : list) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(st), 999999999, 999999999));
                                }
                            }
                            list = soundl;
                            if (rsound) {
                                int size = list.size();
                                int random = r.nextInt(size);
                                s = list.get(random);
                                player.playSound(player.getLocation(), Sound.valueOf(s), 2, 2);
                            } else {
                                for (String st : list) {
                                    player.playSound(player.getLocation(), Sound.valueOf(st), 2, 2);
                                }
                            }
                            list = cmdl;
                            if (rcmd) {
                                int size = list.size();
                                int random = r.nextInt(size);
                                s = list.get(random);
                                s = s.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
                                if (s.contains(" - console") || s.contains(" - CONSOLE")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace(" - console","").replace(" - CONSOLE", ""));
                                } else {
                                    player.performCommand(s);
                                }
                            } else {
                                for (String st : list) {
                                    st = st.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
                                    if (st.contains(" - console") || st.contains(" - CONSOLE")) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), st.replace(" - console","").replace(" - CONSOLE", ""));
                                    } else {
                                        player.performCommand(st);
                                    }
                                }
                            }
                        }
                    }
                }else if(playerindarkness.contains(player)){
                    //Remove from List
                    playerindarkness.remove(player);
                    //Remove all effects
                    List<String> potl = config.getList("Events.Potions");
                    for(String s : potl) {
                        player.removePotionEffect(PotionEffectType.getByName(s));
                    }
                    //Trigger Event
                    Bukkit.getPluginManager().callEvent(new DarknessLeaveEvent(player));
                }
            }
        }
    }


}

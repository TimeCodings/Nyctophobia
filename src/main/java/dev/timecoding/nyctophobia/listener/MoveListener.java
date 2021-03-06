package dev.timecoding.nyctophobia.listener;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.timecoding.nyctophobia.Nyctophobia;
import dev.timecoding.nyctophobia.event.DarknessEnterEvent;
import dev.timecoding.nyctophobia.event.DarknessLeaveEvent;
import dev.timecoding.nyctophobia.file.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public class MoveListener implements Listener {

    private ConfigManager config = Nyctophobia.plugin.getPluginConfig();
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
        int chancenumber = config.getInt("Chance.ChanceNumber");
        boolean night = config.getBoolean("MustBeNightInWorld");
        boolean blacklistenabled = config.getBoolean("Blacklist.Enabled");
        boolean intowhitelist = config.getBoolean("Blacklist.TurnIntoWhitelist");
        boolean chanceenabled = config.getBoolean("Chance.Enabled");
        boolean musicenabled = config.getBoolean("Events.Music.Enabled");
        boolean restartmusicifrunning = config.getBoolean("Events.Music.RestartIfRunning");
        boolean randommusicenabled = config.getBoolean("Events.Music.RandomMusic");
        boolean stopmusiconleave = config.getBoolean("LeaveEvents.Music.Stop");
        boolean bypassenabled = config.getBoolean("Permission.Bypass.Enabled");

        String bypassperm = config.getString("Permission.Bypass.Permission");

        if(chancenumber <= 0){
            chancenumber = 1;
        }

        Random ran = new Random();
        int ra = ran.nextInt((chancenumber+1));


        List<String> blacklist = config.getList("Blacklist.Worlds");
        List<String> musicfiles = config.getList("Events.Music.Files");

        HashMap<Player, RadioSongPlayer> mlist = Nyctophobia.plugin.songs;

        if(maxlighting >= 16){
            maxlighting = 0;
        }
        if(maxblocklighting >= 16){
            maxblocklighting = 0;
        }

        if(enabled && !bypassenabled || enabled && bypassenabled && !player.hasPermission(bypassperm)) {
            if(!blacklistenabled || blacklistenabled && !intowhitelist && !blacklist.contains(player.getWorld().getName()) || blacklistenabled && intowhitelist && blacklist.contains(player.getWorld().getName()))
            if(night && !plugin.isDay(player) || !night) {
                //Get Lightlevels
                String lightlevel = String.valueOf(b.getLightLevel());
                String skylightlevel = String.valueOf(b.getLightFromSky());
                //Test
                if (Integer.valueOf(lightlevel) <= maxlighting && Integer.valueOf(skylightlevel) <= maxblocklighting) {
                    if(!chanceenabled || chanceenabled && ra == (chancenumber-1)){
                    if (!playerindarkness.contains(player)) {
                        //Call Events
                        DarknessEnterEvent darkevent = new DarknessEnterEvent(player);
                        Bukkit.getPluginManager().callEvent(darkevent);
                        //Check for Monsters
                        boolean monsterr = config.getBoolean("MonsterRequirement.Enabled");
                        int blocks = config.getInt("MonsterRequirement.RadiusInBlocks");
                        int monsters = config.getInt("MonsterRequirement.MinMonsters");
                        boolean monster = true;
                        if (monsterr) {
                            List<Entity> nearby = player.getNearbyEntities(blocks, 10, blocks);
                            int count = 0;
                            for (Entity entity : nearby) {
                                if (entity instanceof Monster) {
                                    count++;
                                }
                            }
                            if (count < monsters) {
                                monster = false;
                            }
                        }
                        //Actions
                        if (!darkevent.isCancelled() && !plugin.cooldownEnabled(player) && monster) {
                            //Remove all effects
                            List<String> potll = config.getList("LeaveEvents.Potions");
                            for (String s : potll) {
                                if(!s.contains(" _ ") && !s.contains(" - ")){
                                    player.removePotionEffect(PotionEffectType.getByName(s));
                                }
                                if(s.contains(" _ ") && !s.startsWith(" _ ") && !s.contains(" - ")){
                                    ArrayList<String> split2 = new ArrayList<String>(Arrays.asList(s.split(" _ ")));
                                    int i = 0;
                                    for(String s2 : split2){
                                        if(i == 1){
                                            player.removePotionEffect(PotionEffectType.getByName(s.replace(" _ "+s2, "")));
                                        }else{
                                            i++;
                                        }
                                    }
                                }
                                if (s.contains(" - ") && !s.startsWith(" - ")) {
                                    ArrayList<String> split = new ArrayList<String>(Arrays.asList(s.split(" - ")));
                                    if(s.contains(" _ ") && !s.startsWith(" _ ")){
                                            ArrayList<String> split2 = new ArrayList<String>(Arrays.asList(s.split(" _ ")));
                                            int i = 0;
                                            for(String s2 : split2){
                                                if(i == 1){
                                                    player.removePotionEffect(PotionEffectType.getByName(split.get(0).replace(" _ "+s2, "")));
                                                }else{
                                                    i++;
                                                }
                                        }
                                    }else{
                                        player.removePotionEffect(PotionEffectType.getByName(split.get(0)));
                                    }
                                }
                            }
                            //Add To List
                            playerindarkness.add(player);
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
                            //NOTEBLOCKAPI: Play Music if enabled
                            if(Nyctophobia.plugin.nbapienabled && musicenabled && !randommusicenabled && musicfiles.size() != 0 && musicFolderExists()){
                                File music = new File("plugins//Nyctophobia/music", musicfiles.get(0));
                                if(restartmusicifrunning && mlist.containsKey(player)){
                                    mlist.get(player).destroy();
                                    mlist.remove(player);
                                    Nyctophobia.plugin.songs = mlist;
                                }
                                if(music.exists() && !mlist.containsKey(player)) {
                                    Song song = NBSDecoder.parse(music);
                                    RadioSongPlayer rsp = new RadioSongPlayer(song);
                                    rsp.addPlayer(player);
                                    //Play song
                                    rsp.setPlaying(true);

                                    //Add player to list
                                    mlist.put(player, rsp);
                                }else{
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Cannot play "+musicfiles.get(0)+", because the file does not exists!");
                                }
                            }else if(Nyctophobia.plugin.nbapienabled && musicenabled && randommusicenabled && musicfiles.size() != 0 && musicFolderExists()){
                                List<RadioSongPlayer> rsps = new ArrayList<>();
                                for(String ms : musicfiles){
                                    File music = new File("plugins//Nyctophobia/music", ms);
                                    if(music.exists() && !mlist.containsKey(player)) {
                                        Song song = NBSDecoder.parse(music);
                                        RadioSongPlayer rsp = new RadioSongPlayer(song);
                                        rsps.add(rsp);
                                    }else{
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Cannot play "+musicfiles.get(0)+", because the file does not exists!");
                                    }
                                }
                                if(restartmusicifrunning && mlist.containsKey(player)){
                                    mlist.get(player).destroy();
                                    mlist.remove(player);
                                    Nyctophobia.plugin.songs = mlist;
                                }
                                if(rsps.size() != 0 && !mlist.containsKey(player)){
                                    Random r = new Random();
                                    int rn = r.nextInt(rsps.size());
                                    RadioSongPlayer finalrsp = rsps.get(rn);
                                    finalrsp.addPlayer(player);
                                    //Play song
                                    finalrsp.setPlaying(true);

                                    //Add player to list
                                    mlist.put(player, finalrsp);
                                }
                            }
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
                                boolean block = false;
                                for (String st : list) {
                                    if (!block) {
                                        if (st.contains(" - ")) {
                                            ArrayList<String> split = new ArrayList<String>(Arrays.asList(st.split(" - ")));
                                            player.sendTitle(split.get(0), split.get(1));
                                        } else {
                                            player.sendTitle(st, "");
                                        }
                                        block = true;
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
                                if (s.contains(" - ") && !s.startsWith(" - ")) {
                                    ArrayList<String> split = new ArrayList<String>(Arrays.asList(s.split(" - ")));
                                    int i;
                                    try {
                                        i = Integer.parseInt(split.get(1));
                                    } catch (NumberFormatException en) {
                                        i = 999999999;
                                    }
                                    if (i <= 999999999) {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(split.get(0)), 999999999, i));
                                    }
                                } else {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(s), 999999999, 999999999));
                                }
                            } else {
                                for (String st : list) {
                                    if (st.contains(" - ") && !st.startsWith(" - ")) {
                                        ArrayList<String> split = new ArrayList<String>(Arrays.asList(st.split(" - ")));
                                        int i;
                                        try {
                                            i = Integer.parseInt(split.get(1));
                                        } catch (NumberFormatException en) {
                                            i = 999999999;
                                        }
                                        if (i <= 999999999) {
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(split.get(0)), 999999999, i));
                                        }
                                    } else {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(st), 999999999, 999999999));
                                    }
                                    ;
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
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace(" - console", "").replace(" - CONSOLE", ""));
                                } else {
                                    player.performCommand(s);
                                }
                            } else {
                                for (String st : list) {
                                    st = st.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
                                    if (st.contains(" - console") || st.contains(" - CONSOLE")) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), st.replace(" - console", "").replace(" - CONSOLE", ""));
                                    } else {
                                        player.performCommand(st);
                                    }
                                }
                            }

                        }
                    }
                    }else{
                        if(!playerindarkness.contains(player)) {
                            playerindarkness.add(player);
                        }
                    }
                } else if (playerindarkness.contains(player)) {
                    //Trigger Event
                    DarknessLeaveEvent leave = new DarknessLeaveEvent(player);
                    Bukkit.getPluginManager().callEvent(leave);
                    if (!leave.isCancelled()) {
                        //Start Cooldown, if activated
                        plugin.enableCooldown(player);
                        //Remove from List
                        playerindarkness.remove(player);
                        //Remove all effects
                        List<String> potll = config.getList("Events.Potions");
                        for (String s : potll) {
                            if (s.contains(" - ") && !s.startsWith(" - ")) {
                                ArrayList<String> split = new ArrayList<String>(Arrays.asList(s.split(" - ")));
                                player.removePotionEffect(PotionEffectType.getByName(split.get(0)));
                            } else {
                                player.removePotionEffect(PotionEffectType.getByName(s));
                            }
                        }
                        //Trigger actions from config (Same as the EnterEvent)
                        //Random Functions
                        boolean rtitle = config.getBoolean("LeaveEvents.RandomTitle");
                        boolean rmsg = config.getBoolean("LeaveEvents.RandomMessage");
                        boolean rpotion = config.getBoolean("LeaveEvents.RandomPotion");
                        boolean rsound = config.getBoolean("LeaveEvents.RandomSound");
                        boolean rcmd = config.getBoolean("LeaveEvents.RandomCommand");
                        //Listes
                        List<String> titlel = config.getList("LeaveEvents.Titles");
                        List<String> msgl = config.getList("LeaveEvents.Messages");
                        List<String> potl = config.getList("LeaveEvents.Potions");
                        List<String> soundl = config.getList("LeaveEvents.Sounds");
                        List<String> cmdl = config.getList("LeaveEvents.Commands");
                        //Check and apply
                        Random r = new Random();
                        List<String> list = null;
                        String s = null;
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
                            boolean block = false;
                            for (String st : list) {
                                if (!block) {
                                    if (st.contains(" - ")) {
                                        ArrayList<String> split = new ArrayList<String>(Arrays.asList(st.split(" - ")));
                                        player.sendTitle(split.get(0), split.get(1));
                                    } else {
                                        player.sendTitle(st, "");
                                    }
                                    block = true;
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
                            int time = 999999999;
                            boolean c = false;
                            String replace = "";
                            if(s.contains(" _ ") && !s.startsWith(" _ ")){
                                c = true;
                                ArrayList<String> split2 = new ArrayList<String>(Arrays.asList(s.split(" _ ")));
                                int i = 0;
                                for(String s2 : split2){
                                    if(i == 1){
                                        replace = s2;
                                        time = Integer.valueOf(s2)*20;
                                    }else{
                                        i++;
                                    }
                                }
                            }

                            if (s.contains(" - ") && !s.startsWith(" - ")) {
                                ArrayList<String> split = new ArrayList<String>(Arrays.asList(s.split(" - ")));
                                int i;
                                try {
                                    if(c){
                                        i = Integer.parseInt(split.get(1).replace(" _ "+replace, ""));
                                    }else{
                                        i = Integer.parseInt(split.get(1));
                                    }
                                } catch (NumberFormatException en) {
                                    i = 999999999;
                                }
                                if (i <= 999999999) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(split.get(0)), time, i));
                                }
                            } else {
                                if(c){
                                    s = s.replace(" _ "+replace, "");
                                }
                                player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(s), time, 999999999));
                            }
                        } else {
                            for (String st : list) {
                                int time = 999999999;
                                boolean c = false;
                                String replace = "";
                                if(st.contains(" _ ") && !st.startsWith(" _ ")){
                                    c = true;
                                    ArrayList<String> split2 = new ArrayList<String>(Arrays.asList(st.split(" _ ")));
                                    int i = 0;
                                    for(String s2 : split2){
                                        if(i == 1){
                                            replace = s2;
                                            time = Integer.valueOf(s2)*20;
                                        }else{
                                            i++;
                                        }
                                    }
                                }

                                if (st.contains(" - ") && !st.startsWith(" - ")) {
                                    ArrayList<String> split = new ArrayList<String>(Arrays.asList(st.split(" - ")));
                                    int i;
                                    try {
                                        if(c){
                                            i = Integer.parseInt(split.get(1).replace(" _ "+replace, ""));
                                        }else{
                                            i = Integer.parseInt(split.get(1));
                                        }
                                    } catch (NumberFormatException en) {
                                        i = 999999999;
                                    }
                                    if (i <= 999999999) {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(split.get(0)), time, i));
                                    }
                                } else {
                                    if(c){
                                        st = st.replace(" _ "+replace, "");
                                    }
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(st), time, 999999999));
                                }
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
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace(" - console", "").replace(" - CONSOLE", ""));
                            } else {
                                player.performCommand(s);
                            }
                        } else {
                            for (String st : list) {
                                st = st.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
                                if (st.contains(" - console") || st.contains(" - CONSOLE")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), st.replace(" - console", "").replace(" - CONSOLE", ""));
                                } else {
                                    player.performCommand(st);
                                }
                            }
                        }
                    }
                    if(stopmusiconleave && mlist.containsKey(player)){
                        mlist.get(player).destroy();
                        mlist.remove(player);
                        Nyctophobia.plugin.songs = mlist;
                    }
                }
            }

        }
    }

    public boolean musicFolderExists(){
        File folder = new File("plugins//Nyctophobia//music");
        if(folder.exists()){
            return true;
        }
        return false;
    }


}

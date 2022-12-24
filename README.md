![Unbenannt-4](https://user-images.githubusercontent.com/94994775/209411253-7f738c9c-ed14-4d2c-8786-0a2d1953d822.png)

***Nyctophobia is a plugin which I made for fun! If you want to have new features please open a conversation or join my Discord:*** https://discord.gg/8QWmU4ebCC

***Spigot-Page:*** https://www.spigotmc.org/resources/nyctophobia.102177

**Whats Nyctophobia?**
With Nyctophobia you can scare players with e.g. effects, messages, etc. when they're is in the dark! The plugin is 99% adjustable and offers many small features that can be activated and configured in the config! It also offers a small API for developers.

**Features:**
• Allow certain actions to be performed when the player is in darkness (Title, Messages, Potions, Sounds, Commmands)
• Change Largest BlockLightning
• Send Random Titles, Messages, ...
• World Blacklist
• Enable Cooldowns, Chances, ...
• There's also a really big config
• 1.19 Support
• Challange System (NyctoChallenge | SpigotMC - High Performance Minecraft)

**TODO:**
• Fix some bugs
• Your Ideas

**How to use custom music with NoteBlockAPI?**
**1.** To play custom music just follow the tutorial "How to get .nbs" on the following spigot site and put the plugin into the plugins folder of your Minecraft-Server: https://www.spigotmc.org/resources/noteblockapi.19287/
**2.** If you followed the tutorial on the spigot site and got a YOURMUSIC.nbs file put this file in the music folder, which got created (if you've installed the NoteBlockAPI plugin and reloaded the Nyctophobia plugin) in the music folder path (plugins/Nyctophobia/music). Now you're ready!

**Commands:**
/nyc reload | Reloads the Config (Permission you can change in the config)
/nyc help | Displays the help site (Permission you can change in the config)

**API (for developers):**
There are 2 Events included in the API (just put the JAR in your buildpath of your project), the DarknessEnterEvent and the DarknessLeaveEvent! Here's a example how to use it:

    @EventHandler
    public void onDarknessEnter(DarknessEnterEvent event){
        //Cancel the Event if you don't want to trigger the events in the config
        event.setCancelled(true);
       
        //Get the player which ran into the darkness
        Player player = event.getPlayer();
       
        //And for example send him a message
        player.sendMessage("WOOOOOAH! It's dark here...");
    }
   
    @EventHandler
    public void onDarknessLeave(DarknessLeaveEvent event){
        //Like the DarknessEnterEvent, cancel the Event if you don't want to remove for example the
        //effects (which you configurated in the config)
        event.setCancelled(true);

        //Get the player which ran out of the darkness
        Player player = event.getPlayer();

        //And for example give him a potioneffect
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5, 5));
    }

package dev.timecoding.nyctophobia.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DarknessLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private boolean cancelled;

    public DarknessLeaveEvent(Player p) {
        this.player = p;
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

package org.lonelysail.qqbot.server;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.lonelysail.qqbot.websocket.WsSender;

public class EventListener implements Listener {
    private final WsSender sender;

    public EventListener(WsSender sender) {
        this.sender = sender;
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        sender.sendPlayerLeft(event.getPlayer().getName());
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        sender.sendPlayerJoined(event.getPlayer().getName());
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        sender.sendPlayerChat(event.getPlayer().getName(), event.getMessage());
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        sender.sendPlayerDeath(player.getName(), event.getDeathMessage());
    }
}

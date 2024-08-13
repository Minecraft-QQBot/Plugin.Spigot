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

    // 当玩家退出游戏时触发
    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        // 发送玩家离开游戏的消息
        this.sender.sendPlayerLeft(event.getPlayer().getName());
    }

    // 当玩家加入游戏时触发
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        // 发送玩家加入游戏的消息
        this.sender.sendPlayerJoined(event.getPlayer().getName());
    }

    // 当玩家聊天时触发
    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        // 发送玩家聊天的消息
        this.sender.sendPlayerChat(event.getPlayer().getName(), event.getMessage());
    }

    // 当玩家死亡时触发
    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        // 获取死亡的玩家对象
        Player player = event.getEntity();
        // 发送玩家死亡的消息
        this.sender.sendPlayerDeath(player.getName(), event.getDeathMessage());
    }
}

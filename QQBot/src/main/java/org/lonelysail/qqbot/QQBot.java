package org.lonelysail.qqbot;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.lonelysail.qqbot.server.EventListener;
import org.lonelysail.qqbot.server.QQCommand;
import org.lonelysail.qqbot.websocket.WsListener;
import org.lonelysail.qqbot.websocket.WsSender;

import java.util.Objects;

public final class QQBot extends JavaPlugin {
    public Configuration config;
    private WsListener websocketListener;
    private WsSender websocketSender;

    @Override
    public void onLoad() {
        this.initConfig();
    }

    @Override
    public void onEnable() {
        String name = this.config.getString("name");
        QQCommand command = new QQCommand(this.websocketSender, name);
        EventListener eventListener = new EventListener(this.websocketSender);
        Objects.requireNonNull(this.getCommand("qq")).setExecutor(command);
        this.getServer().getPluginManager().registerEvents(eventListener, this);
        this.websocketSender = new WsSender(this, this.config);
        this.websocketSender.connect();
        this.websocketListener = new WsListener(this, this.config);
        this.websocketListener.connect();
    }

    @Override
    public void onDisable() {
        this.websocketSender.close();
        this.websocketListener.close();
        this.websocketListener.serverRunning = false;
    }

    public void initConfig() {
        this.config = this.getConfig();
        this.config.addDefault("token", "YourToken");
        this.config.addDefault("name", "YourServerName");
        this.config.addDefault("uri", "ws://127.0.0.1:8080/");
    }
}

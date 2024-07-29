package org.lonelysail.qqbot;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.lonelysail.qqbot.server.EventListener;
import org.lonelysail.qqbot.server.QQCommand;
import org.lonelysail.qqbot.websocket.WsListener;
import org.lonelysail.qqbot.websocket.WsSender;

// QQBot类继承自JavaPlugin，是插件的主类
public final class QQBot extends JavaPlugin {
    public Configuration config;
    private WsListener websocketListener;
    private WsSender websocketSender;

    // 插件加载时调用的方法，初始化配置文件
    @Override
    public void onLoad() {
        this.config = this.getConfig();
        this.config.addDefault("token", "YourToken");
        this.config.addDefault("name", "YourServerName");
        this.config.addDefault("uri", "ws://127.0.0.1:8080/");
    }

    // 插件启用时调用的方法，初始化并启动各种服务
    @Override
    public void onEnable() {
        EventListener eventListener = new EventListener(this.websocketSender);
        QQCommand command = new QQCommand(this.websocketSender, this.config.getString("name"));
        this.getCommand("qq").setExecutor(command);
        this.getServer().getPluginManager().registerEvents(eventListener, this);
        this.websocketSender = new WsSender(this, this.config);
        this.websocketSender.connect();
        this.websocketSender.sendServerStartup();
        this.websocketListener = new WsListener(this, this.config);
        this.websocketListener.connect();
    }

    // 插件禁用时调用的方法，关闭各种服务
    @Override
    public void onDisable() {
        this.websocketSender.sendServerShutdown();
        this.websocketSender.close();
        this.websocketListener.close();
        this.websocketListener.serverRunning = false;
    }

}
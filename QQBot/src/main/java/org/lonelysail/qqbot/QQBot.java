package org.lonelysail.qqbot;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.lonelysail.qqbot.server.EventListener;
import org.lonelysail.qqbot.server.commands.QQCommand;
import org.lonelysail.qqbot.websocket.WsListener;
import org.lonelysail.qqbot.websocket.WsSender;

import java.util.Objects;

// QQBot类继承自JavaPlugin，是插件的主类
public final class QQBot extends JavaPlugin {
    public Configuration config;

    private WsListener websocketListener;
    private WsSender websocketSender;

    // 插件加载时调用的方法，初始化配置文件
    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        this.config = this.getConfig();
    }

    // 插件启用时调用的方法，初始化并启动各种服务
    @Override
    public void onEnable() {
        this.getLogger().info("正在初始化与机器人的连接……");
        this.websocketSender = new WsSender(this, this.config);
        this.websocketSender.connect();
        this.websocketListener = new WsListener(this, this.config);
        this.websocketListener.connect();
        EventListener eventListener = new EventListener(this.websocketSender);
        QQCommand command = new QQCommand(this.websocketSender, this.config.getString("name"));
        Objects.requireNonNull(this.getCommand("qq")).setExecutor(command);
        this.getServer().getPluginManager().registerEvents(eventListener, this);
        Bukkit.getScheduler().runTaskLater(this, this.websocketSender::sendServerStartup, 20);
    }

    // 插件禁用时调用的方法，关闭各种服务
    @Override
    public void onDisable() {
        this.websocketSender.sendServerShutdown();
        this.websocketSender.close();
        this.websocketListener.serverRunning = false;
        this.websocketListener.close();
    }

}
package org.lonelysail.qqbot.websocket;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.lonelysail.qqbot.Utils;
import org.lonelysail.qqbot.server.CustomCommandSender;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class WsListener extends WebSocketClient {
    private final Utils utils;
    private final Logger logger;
    private final Server server;
    public boolean serverRunning = true;

    public WsListener(JavaPlugin plugin, Configuration config) {
        super(URI.create(Objects.requireNonNull(config.getString("uri"))).resolve("websocket/minecraft"));
        this.utils = new Utils();
        this.logger = plugin.getLogger();
        this.server = plugin.getServer();

        // 添加请求头信息
        HashMap<String, String> headers = new HashMap<>();
        headers.put("name", config.getString("name"));
        headers.put("token", config.getString("token"));
        this.setDaemon(true);
        this.addHeader("type", "Spigot");
        this.addHeader("info", this.utils.encode(headers));
    }

    // 处理命令请求
    private HashMap<String, ?> command(HashMap<?, ?> data) {
        CustomCommandSender sender = new CustomCommandSender();
        Bukkit.dispatchCommand(sender, (String) data.get("command"));
        HashMap<String, Object> response = new HashMap<>();
        response.put("response", sender.messages);
        return response;
    }

    // 获取在线玩家列表
    private HashMap<String, ?> playerList(HashMap<?, ?> data) {
        List<String> players = new ArrayList<>();
        for (Player player : this.server.getOnlinePlayers()) players.add(player.getName());
        HashMap<String, List<String>> response = new HashMap<>();
        response.put("players", players);
        return response;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.logger.info("The listening connection to bot was opened.");
        this.send("Ok");
    }

    @Override
    public void onMessage(String message) {
        HashMap<String, ?> map = this.utils.decode(message);
        String event_type = (String) map.get("type");
        HashMap<?, ?> data = (HashMap<?, ?>) map.get("data");

        HashMap<String, ?> response;
        HashMap<String, Object> responseMessage = new HashMap<>();

        if (Objects.equals(event_type, "command")) {
            // 如果事件类型是"command"，则调用command方法处理
            response = this.command(data);
        } else if (Objects.equals(event_type, "player_list")) {
            // 如果事件类型是"player_list"，则调用playerList方法处理
            response = this.playerList(data);
        } else {
            // 如果事件类型未知，则记录警告信息并返回失败响应
            this.logger.warning("Unknown event type: " + event_type);
            responseMessage.put("success", false);
            this.send(this.utils.encode(responseMessage));
            return;
        }

        // 构造成功响应并发送
        responseMessage.put("success", true);
        responseMessage.put("data", response);
        this.send(this.utils.encode(responseMessage));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        this.logger.info("The listening connection to bot was closed.");
        if (this.serverRunning) {
            this.logger.info("Trying to reconnecting...");
            try {
//                隔 5s 重连
                this.wait(5000);
            } catch (InterruptedException ignored) {
            }
            this.reconnect();
        }
    }

    @Override
    public void onError(Exception ex) {
    }
}

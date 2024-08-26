package org.lonelysail.qqbot.websocket;

import com.sun.management.OperatingSystemMXBean;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.lonelysail.qqbot.Utils;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class WsListener extends WebSocketClient {
    public boolean serverRunning = true;
    private final Logger logger;
    private final Server server;
    private final JavaPlugin plugin;

    private final Utils utils = new Utils();
    private final OperatingSystemMXBean bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);


    public WsListener(JavaPlugin plugin, Configuration config) {
        super(URI.create(Objects.requireNonNull(config.getString("uri"))).resolve("websocket/minecraft"));
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.server = plugin.getServer();

        // 添加请求头信息
        HashMap<String, String> headers = new HashMap<>();
        headers.put("name", config.getString("name"));
        headers.put("token", config.getString("token"));
        this.addHeader("type", "Spigot");
        this.addHeader("info", this.utils.encode(headers));
    }

    // 处理命令请求
    private String command(String data) {
    //    CustomCommandSender customSender = new CustomCommandSender(this.server.getConsoleSender());
        Bukkit.getScheduler().runTask(this.plugin, () -> this.server.dispatchCommand(this.server.getConsoleSender(), data));
        return "命令已发送到服务器！当前插件不支持获取命令返回值。";
    }

    // 获取在线玩家列表
    private List<String> playerList(String data) {
        List<String> players = new ArrayList<>();
        for (Player player : this.server.getOnlinePlayers()) players.add(player.getName());
        return players;
    }

    private List<Double> serverOccupation(String data) {
        Runtime runtime = Runtime.getRuntime();
        List<Double> serverOccupations = new ArrayList<>();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        serverOccupations.add(this.bean.getProcessCpuLoad() * 100);
        serverOccupations.add(((double) ((totalMemory - freeMemory)) / totalMemory) * 100);
        return serverOccupations;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.logger.info("[Listener] 与机器人成功建立连接！");
    }

    @Override
    public void onMessage(String message) {
        HashMap<String, ?> map = this.utils.decode(message);
        Object data = map.get("data");
        String event_type = (String) map.get("type");
        this.logger.fine("收到消息机器人消息 " + map);
        Object response;
        HashMap<String, Object> responseMessage = new HashMap<>();

        if (Objects.equals(event_type, "message")) {
            String broadcastMessage = this.utils.toStringMessage((List) data);
            this.server.broadcastMessage(broadcastMessage);
            this.logger.fine("[Listener] 收到广播消息 " + broadcastMessage);
            return;
        } else if (Objects.equals(event_type, "command")) {
            // 如果事件类型是"command"，则调用command方法处理
            response = this.command((String) data);
        } else if (Objects.equals(event_type, "player_list")) {
            // 如果事件类型是"player_list"，则调用playerList方法处理
            response = this.playerList((String) data);
        } else if (Objects.equals(event_type, "server_occupation")) {
            // 如果事件类型是"server_occupation"，则调用serverOccupation方法处理
            response = this.serverOccupation((String) data);
        } else {
            // 如果事件类型未知，则记录警告信息并返回失败响应
            this.logger.warning("[Listener] 未知的事件类型: " + event_type);
            responseMessage.put("success", false);
            this.send(this.utils.encode(responseMessage));
            return;
        }
        responseMessage.put("success", true);
        responseMessage.put("data", response);
        this.logger.fine("发送响应消息 " + responseMessage);
        // 构造成功响应并发送
        this.send(this.utils.encode(responseMessage));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        this.logger.warning("[Listener] 与机器人的链接已关闭！");
        if (this.serverRunning) {
            this.logger.info("[Listener] 正在尝试重新链接……");
            Bukkit.getScheduler().runTaskLater(this.plugin, this::reconnect, 100);
        }
    }

    @Override
    public void onError(Exception ex) {
        this.logger.warning("[Listener] 机器人连接发生 " + ex.getMessage() + " 错误！");
        ex.printStackTrace();
    }
}

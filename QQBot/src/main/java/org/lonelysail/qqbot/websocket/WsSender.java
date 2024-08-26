package org.lonelysail.qqbot.websocket;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.lonelysail.qqbot.Utils;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class WsSender extends WebSocketClient {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(WsSender.class);
    private String message;

    private final Logger logger;
    private final Utils utils = new Utils();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();


    public WsSender(JavaPlugin plugin, Configuration config) {
        super(URI.create(Objects.requireNonNull(config.getString("uri"))).resolve("websocket/bot"));
        this.logger = plugin.getLogger();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("name", config.getString("name"));
        headers.put("token", config.getString("token"));
        this.addHeader("info", this.utils.encode(headers));
    }

    public boolean isConnected() {
        return this.isOpen() && !this.isClosed() && !this.isClosing();
    }

    public boolean tryReconnect() {
//            尝试重连三次
        for (int count = 0; count < 3; count ++) {
            logger.warning("[Sender] 检测到与机器人的连接已断开！正在尝试重连……");
            this.reconnect();
            try {
//                等待重连
                Thread.sleep(1000);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
            }
//                重连成功后跳出重连尝试循环
            if (this.isConnected()) {
                this.logger.info("[Sender] 与机器人连接成功！");
                return true;
            }
        }
        return false;
    }

    //    发送事件基本函数
    public boolean sendData(String event_type, Object data, Boolean waitResponse) {
//        重连模块
        if (!this.isConnected()) {
            if (!this.tryReconnect()) {
                return false;
            }
        }
        boolean responseReceived = false;
        HashMap<String, Object> messageData = new HashMap<>();
        messageData.put("data", data);
        messageData.put("type", event_type);
        try {
            this.send(this.utils.encode(messageData));
        } catch (WebsocketNotConnectedException error) {
            logger.warning("[Sender] 发送数据失败！与机器人的连接已断开。");
            return false;
        }
        if (!waitResponse) return true;
//        等待响应
        this.lock.lock();
        try {
            responseReceived = this.condition.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        } finally {
            this.lock.unlock();
        }
        if (!responseReceived) {
            this.logger.warning("[Sender] 等待响应超时。");
            return false;
        }
//        返回是否成功
        return (boolean) this.utils.decode(this.message).get("success");
    }

    public void sendServerStartup() {
        HashMap<String, Object> data = new HashMap<>();
        if (this.sendData("server_startup", data, true)) this.logger.fine("发送服务器启动消息成功！");
        else this.logger.warning("发送服务器启动消息失败！请检查机器人是否启动后再次尝试。");
    }

    public void sendServerShutdown() {
        HashMap<String, Object> data = new HashMap<>();
        if (this.sendData("server_shutdown", data, true)) this.logger.fine("发送服务器关闭消息成功！");
        else this.logger.warning("发送服务器关闭消息失败！请检查机器人是否启动后再次尝试。");
    }

    public void sendPlayerLeft(String name) {
        if (this.sendData("player_left", name, true)) this.logger.fine("发送玩家离开消息成功！");
        else this.logger.warning("发送玩家离开消息失败！请检查机器人是否启动后再次尝试。");
    }

    public void sendPlayerJoined(String name) {
        if (this.sendData("player_joined", name, true)) this.logger.fine("发送玩家进入消息成功！");
        else this.logger.warning("发送玩家进入消息失败！请检查机器人是否启动后再次尝试。");
    }

    public void sendPlayerChat(String name, String message) {
        List<String> data = new ArrayList<>();
        data.add(name);
        data.add(message);
        this.sendData("player_chat", data, false);
        this.logger.fine("发送玩家消息成功！");
    }

    public void sendPlayerDeath(String name, String message) {
        List<String> data = new ArrayList<>();
        data.add(name);
        data.add(message);
        if (this.sendData("player_death", data, true)) this.logger.fine("发送玩家死亡消息成功！");
        else this.logger.warning("发送玩家死亡消息失败！请检查机器人是否启动后再次尝试。");
    }

    public boolean sendSynchronousMessage(String message) {
        return this.sendData("message", message, true);
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.logger.fine("[Sender] 与机器人成功建立链接！");
    }

    @Override
    public void onMessage(String message) {
//        收到消息时设置 message 为收到的消息
        this.lock.lock();
        try {
            this.message = message;
            this.condition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        this.logger.info("[Sender] 与机器人的连接已断开！");
    }

    @Override
    public void onError(Exception ex) {
        this.logger.warning("[Sender] 机器人连接发生 " + ex.getMessage() + " 错误！");
        ex.printStackTrace();
    }
}

package org.lonelysail.qqbot.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.lonelysail.qqbot.websocket.WsSender;

public class QQCommand implements CommandExecutor {
    private final String name;
    private final WsSender sender;

    public QQCommand(WsSender sender, String name) {
        this.name = name;
        this.sender = sender;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }
        String message = String.format("[%s] <%s> %s", this.name, sender.getName(), args[0]);
        if (this.sender.sendSynchronousMessage(message)) sender.sendMessage("§a发送消息成功！");
        else sender.sendMessage("§c发送消息失败！");
        return true;
    }
}

package org.lonelysail.qqbot.server;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class CustomCommandSender implements ConsoleCommandSender {
    private final ConsoleCommandSender delegate;
    public List<String> messages = new ArrayList<>();

    public CustomCommandSender(ConsoleCommandSender delegate) {
        this.delegate = delegate;
    }

    @Override
    public void sendMessage(String message) {
        this.messages.add(message);
        this.delegate.sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        this.delegate.sendMessage(messages);
        this.messages.addAll(Arrays.asList(messages));
    }

    @Override
    public Server getServer() {
        return this.delegate.getServer();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public Spigot spigot() {
        return this.delegate.spigot();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.delegate.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return this.delegate.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.delegate.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return this.delegate.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return this.delegate.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.delegate.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return this.delegate.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return this.delegate.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.delegate.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.delegate.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.delegate.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return this.delegate.isOp();
    }

    @Override
    public void setOp(boolean value) {
        this.delegate.setOp(value);
    }

    @Override
    public boolean isConversing() {
        return this.delegate.isConversing();
    }

    @Override
    public void acceptConversationInput(String input) {
        this.delegate.acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return this.delegate.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        this.delegate.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        this.delegate.abandonConversation(conversation, details);
    }

    @Override
    public void sendRawMessage(String message) {
        this.messages.add(message);
        this.delegate.sendRawMessage(message);
    }
}

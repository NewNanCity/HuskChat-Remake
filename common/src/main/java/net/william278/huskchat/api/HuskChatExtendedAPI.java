/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.api;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.event.*;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.message.PrivateMessage;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * HuskChat扩展API - 提供更多高级功能和事件处理
 * HuskChat Extended API - provides more advanced features and event handling
 */
@SuppressWarnings("unused")
public class HuskChatExtendedAPI extends HuskChatAPI {

    protected HuskChatExtendedAPI(@NotNull HuskChat plugin) {
        super(plugin);
    }

    /**
     * 获取扩展API实例
     * Get the extended API instance
     *
     * @return 扩展API实例 / extended API instance
     */
    @NotNull
    public static HuskChatExtendedAPI getInstance() {
        if (!(instance instanceof HuskChatExtendedAPI)) {
            throw new IllegalStateException("Extended API not initialized. Make sure you're using HuskChat Remake.");
        }
        return (HuskChatExtendedAPI) instance;
    }

    // ========== 频道管理 API / Channel Management API ==========

    /**
     * 切换玩家频道
     * Switch player channel
     *
     * @param player 玩家 / player
     * @param channelId 目标频道ID / target channel ID
     * @param reason 切换原因 / switch reason
     * @return 是否成功切换 / whether successfully switched
     */
    public CompletableFuture<Boolean> switchPlayerChannel(@NotNull OnlineUser player, @NotNull String channelId, 
                                                         @NotNull ChannelSwitchEvent.SwitchReason reason) {
        final Optional<Channel> targetChannel = plugin.getChannels().getChannel(channelId);
        if (targetChannel.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        final String previousChannelId = plugin.getUserCache().getPlayerChannel(player.getUuid()).orElse(null);
        
        return plugin.fireChannelSwitchEvent(player, previousChannelId, channelId, reason)
                .thenApply(event -> {
                    if (event.isCancelled()) {
                        return false;
                    }
                    
                    // 触发离开事件
                    if (previousChannelId != null) {
                        plugin.firePlayerLeaveChannelEvent(player, previousChannelId, 
                            PlayerLeaveChannelEvent.LeaveReason.CHANNEL_SWITCH);
                    }
                    
                    // 执行频道切换
                    plugin.editUserCache(cache -> cache.switchPlayerChannel(player, event.getNewChannelId(), plugin));
                    
                    // 触发加入事件
                    plugin.firePlayerJoinChannelEvent(player, event.getNewChannelId(), 
                        PlayerJoinChannelEvent.JoinReason.MANUAL_SWITCH);
                    
                    return true;
                });
    }

    /**
     * 获取频道中的所有玩家
     * Get all players in a channel
     *
     * @param channelId 频道ID / channel ID
     * @return 玩家列表 / player list
     */
    @NotNull
    public List<OnlineUser> getPlayersInChannel(@NotNull String channelId) {
        return plugin.getOnlinePlayers().stream()
                .filter(player -> plugin.getUserCache().getPlayerChannel(player.getUuid())
                        .map(channel -> channel.equals(channelId))
                        .orElse(false))
                .toList();
    }

    /**
     * 检查玩家是否在指定频道
     * Check if player is in specified channel
     *
     * @param player 玩家 / player
     * @param channelId 频道ID / channel ID
     * @return 是否在频道中 / whether in channel
     */
    public boolean isPlayerInChannel(@NotNull OnlineUser player, @NotNull String channelId) {
        return plugin.getUserCache().getPlayerChannel(player.getUuid())
                .map(channel -> channel.equals(channelId))
                .orElse(false);
    }

    // ========== 消息发送 API / Message Sending API ==========

    /**
     * 发送私聊消息
     * Send private message
     *
     * @param sender 发送者 / sender
     * @param recipients 接收者列表 / recipient list
     * @param message 消息内容 / message content
     * @return 发送结果 / send result
     */
    public CompletableFuture<Boolean> sendPrivateMessage(@NotNull OnlineUser sender, @NotNull List<String> recipients, 
                                                        @NotNull String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                new PrivateMessage(sender, recipients, message, plugin).dispatch();
                return true;
            } catch (Exception e) {
                plugin.log(java.util.logging.Level.WARNING, "Failed to send private message", e);
                return false;
            }
        });
    }

    /**
     * 向频道发送系统消息
     * Send system message to channel
     *
     * @param channelId 频道ID / channel ID
     * @param message 消息内容 / message content
     * @param sender 发送者（可为null表示系统消息） / sender (null for system message)
     * @return 发送结果 / send result
     */
    public CompletableFuture<Boolean> sendChannelMessage(@NotNull String channelId, @NotNull String message, 
                                                        @Nullable OnlineUser sender) {
        final Optional<Channel> channel = plugin.getChannels().getChannel(channelId);
        if (channel.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (sender != null) {
                    new ChatMessage(channel.get(), sender, message, plugin).dispatch();
                } else {
                    // 系统消息处理
                    // TODO: 实现系统消息发送逻辑
                }
                return true;
            } catch (Exception e) {
                plugin.log(java.util.logging.Level.WARNING, "Failed to send channel message", e);
                return false;
            }
        });
    }

    // ========== 事件监听器注册 API / Event Listener Registration API ==========

    /**
     * 注册聊天消息事件监听器
     * Register chat message event listener
     *
     * @param listener 监听器 / listener
     */
    public void registerChatMessageListener(@NotNull ChatMessageEventListener listener) {
        // 这个方法需要在各平台具体实现中重写
        // This method needs to be overridden in platform-specific implementations
        throw new UnsupportedOperationException("Platform-specific implementation required");
    }

    /**
     * 注册频道切换事件监听器
     * Register channel switch event listener
     *
     * @param listener 监听器 / listener
     */
    public void registerChannelSwitchListener(@NotNull ChannelSwitchEventListener listener) {
        throw new UnsupportedOperationException("Platform-specific implementation required");
    }

    // ========== 事件监听器接口 / Event Listener Interfaces ==========

    /**
     * 聊天消息事件监听器接口
     * Chat message event listener interface
     */
    @FunctionalInterface
    public interface ChatMessageEventListener {
        void onChatMessage(@NotNull ChatMessageEvent event);
    }

    /**
     * 频道切换事件监听器接口
     * Channel switch event listener interface
     */
    @FunctionalInterface
    public interface ChannelSwitchEventListener {
        void onChannelSwitch(@NotNull ChannelSwitchEvent event);
    }

    /**
     * 私聊消息事件监听器接口
     * Private message event listener interface
     */
    @FunctionalInterface
    public interface PrivateMessageEventListener {
        void onPrivateMessage(@NotNull PrivateMessageEvent event);
    }

    /**
     * 消息过滤事件监听器接口
     * Message filter event listener interface
     */
    @FunctionalInterface
    public interface MessageFilterEventListener {
        void onMessageFilter(@NotNull MessageFilterEvent event);
    }
}

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

    // ========== 命令执行 API / Command Execution API ==========

    /**
     * 代表玩家执行聊天命令
     * Execute chat command on behalf of player
     *
     * @param player 玩家 / player
     * @param command 命令 / command
     * @param args 参数 / arguments
     * @return 执行结果 / execution result
     */
    public CompletableFuture<Boolean> executeChatCommand(@NotNull OnlineUser player, @NotNull String command, @NotNull String... args) {
        ChatCommandEvent.CommandType commandType = ChatCommandEvent.CommandType.fromCommand(command);

        // 触发PRE事件
        return plugin.fireChatCommandEvent(player, command, args, commandType, ChatCommandEvent.ExecutionPhase.PRE)
                .thenCompose(preEvent -> {
                    if (preEvent.isCancelled()) {
                        return CompletableFuture.completedFuture(false);
                    }

                    // 执行命令逻辑
                    return executeCommandLogic(player, preEvent.getCommand(), preEvent.getArgs(), commandType)
                            .thenCompose(success -> {
                                // 触发POST事件
                                return plugin.fireChatCommandEvent(player, command, args, commandType, ChatCommandEvent.ExecutionPhase.POST)
                                        .thenApply(postEvent -> {
                                            postEvent.setSuccessful(success);
                                            if (!success) {
                                                postEvent.setFailureReason("Command execution failed");
                                            }
                                            return success;
                                        });
                            });
                });
    }

    /**
     * 验证命令权限
     * Validate command permissions
     *
     * @param player 玩家 / player
     * @param command 命令 / command
     * @return 是否有权限 / whether has permission
     */
    public boolean validateCommandPermission(@NotNull OnlineUser player, @NotNull String command) {
        ChatCommandEvent.CommandType commandType = ChatCommandEvent.CommandType.fromCommand(command);

        return switch (commandType) {
            case CHANNEL_SWITCH -> player.hasPermission("huskchat.command.channel", true);
            case PRIVATE_MESSAGE -> player.hasPermission("huskchat.command.msg", true);
            case REPLY -> player.hasPermission("huskchat.command.reply", true);
            case BROADCAST -> player.hasPermission("huskchat.command.broadcast", false);
            case SOCIAL_SPY -> player.hasPermission("huskchat.command.socialspy", false);
            case LOCAL_SPY -> player.hasPermission("huskchat.command.localspy", false);
            case OPT_OUT_MESSAGE -> player.hasPermission("huskchat.command.optout", true);
            default -> true;
        };
    }

    private CompletableFuture<Boolean> executeCommandLogic(@NotNull OnlineUser player, @NotNull String command,
                                                          @NotNull String[] args, @NotNull ChatCommandEvent.CommandType commandType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return switch (commandType) {
                    case CHANNEL_SWITCH -> {
                        if (args.length > 0) {
                            yield switchPlayerChannel(player, args[0], ChannelSwitchEvent.SwitchReason.API_CALL).join();
                        }
                        yield false;
                    }
                    case PRIVATE_MESSAGE -> {
                        if (args.length >= 2) {
                            String recipient = args[0];
                            String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                            yield sendPrivateMessage(player, List.of(recipient), message).join();
                        }
                        yield false;
                    }
                    default -> {
                        // 其他命令的默认处理
                        plugin.log(java.util.logging.Level.INFO, "Executed command: " + command + " by " + player.getUsername());
                        yield true;
                    }
                };
            } catch (Exception e) {
                plugin.log(java.util.logging.Level.WARNING, "Failed to execute command: " + command, e);
                return false;
            }
        });
    }

    // ========== 玩家状态管理 API / Player Status Management API ==========

    /**
     * 获取玩家信息
     * Get player information
     *
     * @param player 玩家 / player
     * @return 玩家信息 / player information
     */
    @NotNull
    public PlayerInfo getPlayerInfo(@NotNull OnlineUser player) {
        return player; // OnlineUser implements PlayerInfo
    }

    /**
     * 更新玩家状态
     * Update player status
     *
     * @param player 玩家 / player
     * @param statusType 状态类型 / status type
     * @param newValue 新值 / new value
     * @param reason 原因 / reason
     * @param duration 持续时间（毫秒，-1为永久） / duration (milliseconds, -1 for permanent)
     * @return 更新结果 / update result
     */
    public CompletableFuture<Boolean> updatePlayerStatus(@NotNull OnlineUser player,
                                                        @NotNull PlayerStatusChangeEvent.StatusType statusType,
                                                        @NotNull Object newValue,
                                                        @NotNull String reason,
                                                        long duration) {
        Object previousValue = player.getStatus(statusType).orElse(null);

        return plugin.firePlayerStatusChangeEvent(player, statusType, previousValue, newValue, reason, duration)
                .thenApply(event -> {
                    if (event.isCancelled()) {
                        return false;
                    }

                    // 更新状态（需要在具体平台实现中处理）
                    updatePlayerStatusInternal(player, statusType, newValue, duration);
                    return true;
                });
    }

    /**
     * 检查玩家是否满足聊天条件
     * Check if player meets chat conditions
     *
     * @param player 玩家 / player
     * @param channelId 频道ID / channel ID
     * @return 检查结果 / check result
     */
    @NotNull
    public ChatConditionResult checkChatConditions(@NotNull OnlineUser player, @NotNull String channelId) {
        // 检查基本权限
        if (!player.hasPermission("huskchat.channel." + channelId + ".send", true)) {
            return new ChatConditionResult(false, "No permission to send messages in this channel");
        }

        // 检查禁言状态
        if (player.isMuted()) {
            return new ChatConditionResult(false, "Player is muted");
        }

        // 检查生命值限制（如果配置了）
        if (player.isCriticalHealth() && shouldRestrictChatOnLowHealth(channelId)) {
            return new ChatConditionResult(false, "Cannot chat while in critical health condition");
        }

        // 检查战斗状态限制
        if (player.isInCombat() && shouldRestrictChatInCombat(channelId)) {
            return new ChatConditionResult(false, "Cannot chat while in combat");
        }

        return new ChatConditionResult(true, null);
    }

    protected void updatePlayerStatusInternal(@NotNull OnlineUser player, @NotNull PlayerStatusChangeEvent.StatusType statusType, @NotNull Object newValue, long duration) {
        // 默认实现，子类可以重写
        throw new UnsupportedOperationException("Platform-specific implementation required");
    }

    protected boolean shouldRestrictChatOnLowHealth(@NotNull String channelId) {
        // 默认不限制，可以通过配置或子类重写
        return false;
    }

    protected boolean shouldRestrictChatInCombat(@NotNull String channelId) {
        // 默认不限制，可以通过配置或子类重写
        return false;
    }

    /**
     * 聊天条件检查结果
     * Chat condition check result
     */
    public static class ChatConditionResult {
        private final boolean allowed;
        private final String reason;

        public ChatConditionResult(boolean allowed, @Nullable String reason) {
            this.allowed = allowed;
            this.reason = reason;
        }

        public boolean isAllowed() {
            return allowed;
        }

        @Nullable
        public String getReason() {
            return reason;
        }
    }
}

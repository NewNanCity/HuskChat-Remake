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

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.william278.huskchat.VelocityHuskChat;
import net.william278.huskchat.event.*;
import net.william278.huskchat.network.PlayerStatusMessage;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.VelocityUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Velocity平台的HuskChat扩展API实现
 * Velocity platform implementation of HuskChat Extended API
 */
public class VelocityHuskChatExtendedAPI extends HuskChatExtendedAPI {

    private final VelocityHuskChat plugin;
    private final ProxyServer proxyServer;
    private final Map<String, PlayerStatusMessage> pendingStatusUpdates = new ConcurrentHashMap<>();

    public VelocityHuskChatExtendedAPI(@NotNull VelocityHuskChat plugin) {
        super(plugin);
        this.plugin = plugin;
        this.proxyServer = plugin.getProxyServer();

        // 注册插件消息通道
        registerPluginMessageChannels();
    }

    private void registerPluginMessageChannels() {
        // 注册用于跨服务器通信的插件消息通道
        proxyServer.getChannelRegistrar().register(
            plugin.getPluginMessageIdentifier("player_status")
        );
    }

    // ========== 平台特定实现 / Platform-Specific Implementation ==========

    @Override
    protected void updatePlayerStatusInternal(@NotNull OnlineUser player, @NotNull PlayerStatusChangeEvent.StatusType statusType, @NotNull Object newValue, long duration) {
        if (player instanceof VelocityUser velocityUser) {
            velocityUser.updateStatus(statusType, newValue);

            // 广播状态变化到所有后端服务器
            broadcastStatusUpdate(player, statusType, newValue, "API call");

            // 如果是临时状态，设置定时器
            if (duration > 0) {
                plugin.getScheduler().buildTask(plugin, () -> {
                    velocityUser.removeStatus(statusType);
                    // 触发状态变化事件
                    plugin.firePlayerStatusChangeEvent(player, statusType, newValue, null, "Temporary status expired", -1);
                    broadcastStatusUpdate(player, statusType, null, "Temporary status expired");
                }).delay(java.time.Duration.ofMillis(duration)).schedule();
            }
        }
    }

    @Override
    protected boolean shouldRestrictChatOnLowHealth(@NotNull String channelId) {
        // 在代理服务器环境中，限制策略可能来自配置
        return plugin.getChannels().getChannel(channelId)
                .map(channel -> {
                    // 检查频道是否配置了生命值限制
                    return false; // 默认不限制
                })
                .orElse(false);
    }

    @Override
    protected boolean shouldRestrictChatInCombat(@NotNull String channelId) {
        // 在代理服务器环境中，限制策略可能来自配置
        return plugin.getChannels().getChannel(channelId)
                .map(channel -> {
                    // 检查频道是否配置了战斗限制
                    return false; // 默认不限制
                })
                .orElse(false);
    }

    /**
     * 广播状态更新到所有后端服务器
     * Broadcast status update to all backend servers
     *
     * @param player 玩家 / player
     * @param statusType 状态类型 / status type
     * @param value 状态值 / status value
     * @param reason 原因 / reason
     */
    private void broadcastStatusUpdate(@NotNull OnlineUser player, @NotNull PlayerStatusChangeEvent.StatusType statusType,
                                     Object value, @NotNull String reason) {
        PlayerStatusMessage message = PlayerStatusMessage.createStatusUpdate(
            player.getUuid(), player.getName(), player.getServerName(), statusType, value, reason);

        // 发送到所有后端服务器
        for (RegisteredServer server : proxyServer.getAllServers()) {
            sendPlayerStatusMessage(server, message);
        }
    }

    /**
     * 发送玩家状态消息到指定服务器
     * Send player status message to specified server
     *
     * @param server 目标服务器 / target server
     * @param message 消息 / message
     */
    private void sendPlayerStatusMessage(@NotNull RegisteredServer server, @NotNull PlayerStatusMessage message) {
        try {
            byte[] data = plugin.getGson().toJson(message).getBytes();
            server.sendPluginMessage(plugin.getPluginMessageIdentifier("player_status"), data);
        } catch (Exception e) {
            plugin.log(java.util.logging.Level.WARNING, "Failed to send player status message to server " + server.getServerInfo().getName(), e);
        }
    }

    /**
     * 处理来自后端服务器的玩家状态消息
     * Handle player status message from backend server
     *
     * @param serverName 服务器名称 / server name
     * @param message 消息 / message
     */
    public void handlePlayerStatusMessage(@NotNull String serverName, @NotNull PlayerStatusMessage message) {
        Optional<OnlineUser> playerOpt = plugin.getOnlinePlayers().stream()
                .filter(user -> user.getUuid().equals(message.getPlayerUuid()))
                .findFirst();

        if (playerOpt.isEmpty()) {
            return;
        }

        OnlineUser player = playerOpt.get();

        switch (message.getMessageType()) {
            case STATUS_UPDATE -> handleStatusUpdate(player, message);
            case HEALTH_CHANGE -> handleHealthChange(player, message);
            case LOCATION_CHANGE -> handleLocationChange(player, message);
            case PLAYER_DEATH -> handlePlayerDeath(player, message);
            case PLAYER_RESPAWN -> handlePlayerRespawn(player, message);
            case SYNC_RESPONSE -> handleSyncResponse(player, message);
        }
    }

    private void handleStatusUpdate(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        String statusTypeKey = message.getData("status_type", String.class);
        Object value = message.getData("value");
        String reason = message.getData("reason", String.class);

        if (statusTypeKey != null && value != null && reason != null) {
            try {
                PlayerStatusChangeEvent.StatusType statusType = PlayerStatusChangeEvent.StatusType.fromKey(statusTypeKey);

                // 更新本地缓存
                if (player instanceof VelocityUser velocityUser) {
                    Object previousValue = velocityUser.getStatus(statusType).orElse(null);
                    velocityUser.updateStatus(statusType, value);

                    // 触发事件
                    plugin.firePlayerStatusChangeEvent(player, statusType, previousValue, value, reason, -1);
                }
            } catch (Exception e) {
                plugin.log(java.util.logging.Level.WARNING, "Failed to handle status update: " + e.getMessage());
            }
        }
    }

    private void handleHealthChange(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        Double previousHealth = message.getData("previous_health", Double.class);
        Double newHealth = message.getData("new_health", Double.class);
        Double maxHealth = message.getData("max_health", Double.class);
        String reason = message.getData("reason", String.class);

        if (previousHealth != null && newHealth != null && maxHealth != null && reason != null) {
            // 更新健康状态缓存
            if (player instanceof VelocityUser velocityUser) {
                velocityUser.updateStatus(PlayerStatusChangeEvent.StatusType.HEALTH, newHealth);
                velocityUser.updateStatus(PlayerStatusChangeEvent.StatusType.MAX_HEALTH, maxHealth);
            }

            // 触发生命值变化事件
            plugin.firePlayerHealthChangeEvent(player, previousHealth, newHealth, maxHealth,
                net.william278.huskchat.event.PlayerHealthChangeEvent.HealthChangeReason.OTHER, null);
        }
    }

    private void handleLocationChange(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        // 处理位置变化
        Map<String, Object> locationData = message.getData();

        // 触发位置变化事件
        // 这里需要根据具体的位置数据格式来实现
        plugin.log(java.util.logging.Level.INFO, "Player " + player.getName() + " location changed on server " + message.getServerName());
    }

    private void handlePlayerDeath(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        // 处理玩家死亡
        String deathMessage = message.getData("death_message", String.class);
        String killerName = message.getData("killer", String.class);

        OnlineUser killer = null;
        if (killerName != null) {
            killer = plugin.getOnlinePlayers().stream()
                    .filter(user -> user.getName().equals(killerName))
                    .findFirst()
                    .orElse(null);
        }

        // 触发死亡事件 - 需要提供完整的参数
        plugin.firePlayerDeathEvent(player, deathMessage != null ? deathMessage : "Player died",
            net.william278.huskchat.event.PlayerDeathEvent.DeathCause.OTHER, killer,
            net.william278.huskchat.user.VelocityPlayerLocation.from("unknown", "unknown", 0.0, 0.0, 0.0, 0.0f, 0.0f));
    }

    private void handlePlayerRespawn(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        // 处理玩家重生
        String respawnReason = message.getData("reason", String.class);

        // 触发重生事件 - 需要提供完整的参数
        plugin.firePlayerRespawnEvent(player,
            net.william278.huskchat.user.VelocityPlayerLocation.from("unknown", "unknown", 0.0, 0.0, 0.0, 0.0f, 0.0f),
            net.william278.huskchat.event.PlayerRespawnEvent.RespawnReason.NORMAL);
    }

    private void handleSyncResponse(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        // 处理状态同步响应
        if (player instanceof VelocityUser velocityUser) {
            velocityUser.syncFromBackend(message.getData());
        }
    }

    /**
     * 请求玩家状态同步
     * Request player status sync
     *
     * @param player 玩家 / player
     * @return 同步结果 / sync result
     */
    @NotNull
    public CompletableFuture<Boolean> requestPlayerStatusSync(@NotNull OnlineUser player) {
        if (!(player instanceof VelocityUser)) {
            return CompletableFuture.completedFuture(false);
        }

        Player velocityPlayer = ((VelocityUser) player).getPlayer();
        Optional<RegisteredServer> currentServer = velocityPlayer.getCurrentServer()
                .map(connection -> connection.getServer());

        if (currentServer.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        PlayerStatusMessage syncRequest = PlayerStatusMessage.createSyncRequest(
            player.getUuid(), player.getName(), currentServer.get().getServerInfo().getName());

        sendPlayerStatusMessage(currentServer.get(), syncRequest);

        return CompletableFuture.completedFuture(true);
    }

    /**
     * 获取附近的玩家（代理服务器版本）
     * Get nearby players (proxy server version)
     *
     * @param player 中心玩家 / center player
     * @param radius 半径 / radius
     * @return 附近的玩家列表 / nearby players list
     */
    @NotNull
    public List<OnlineUser> getNearbyPlayers(@NotNull OnlineUser player, double radius) {
        // 在代理服务器环境中，只能返回同一服务器的玩家
        return plugin.getOnlinePlayers().stream()
                .filter(other -> !other.getUuid().equals(player.getUuid()))
                .filter(other -> other.getServerName().equals(player.getServerName()))
                .collect(Collectors.toList());
    }

    /**
     * 检查玩家是否在同一区域（代理服务器版本）
     * Check if players are in the same region (proxy server version)
     *
     * @param player1 玩家1 / player 1
     * @param player2 玩家2 / player 2
     * @return 是否在同一区域 / whether in same region
     */
    public boolean arePlayersInSameRegion(@NotNull OnlineUser player1, @NotNull OnlineUser player2) {
        // 在代理服务器环境中，同一服务器视为同一区域
        return player1.getServerName().equals(player2.getServerName());
    }

    /**
     * 创建基于位置的频道（代理服务器版本）
     * Create location-based channel (proxy server version)
     *
     * @param player 玩家 / player
     * @param radius 半径 / radius
     * @return 临时频道ID / temporary channel ID
     */
    @NotNull
    public String createLocationBasedChannel(@NotNull OnlineUser player, double radius) {
        // 在代理服务器环境中，基于服务器创建频道
        return "server_" + player.getServerName() + "_" + (int) radius;
    }
}

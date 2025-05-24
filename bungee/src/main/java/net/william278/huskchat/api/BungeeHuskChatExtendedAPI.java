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

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.william278.huskchat.BungeeHuskChat;
import net.william278.huskchat.event.*;
import net.william278.huskchat.network.PlayerStatusMessage;
import net.william278.huskchat.user.BungeeUser;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * BungeeCord平台的HuskChat扩展API实现
 * BungeeCord platform implementation of HuskChat Extended API
 */
public class BungeeHuskChatExtendedAPI extends HuskChatExtendedAPI {

    private final BungeeHuskChat plugin;
    private final ProxyServer proxyServer;
    private final Map<String, PlayerStatusMessage> pendingStatusUpdates = new ConcurrentHashMap<>();

    public BungeeHuskChatExtendedAPI(@NotNull BungeeHuskChat plugin) {
        super(plugin);
        this.plugin = plugin;
        this.proxyServer = plugin.getProxy();
        
        // 注册插件消息通道
        registerPluginMessageChannels();
    }

    private void registerPluginMessageChannels() {
        // 注册用于跨服务器通信的插件消息通道
        proxyServer.registerChannel("huskchat:player_status");
    }

    // ========== 平台特定实现 / Platform-Specific Implementation ==========

    @Override
    protected void updatePlayerStatusInternal(@NotNull OnlineUser player, @NotNull PlayerStatusChangeEvent.StatusType statusType, @NotNull Object newValue, long duration) {
        if (player instanceof BungeeUser bungeeUser) {
            bungeeUser.updateStatus(statusType, newValue);
            
            // 广播状态变化到所有后端服务器
            broadcastStatusUpdate(player, statusType, newValue, "API call");
            
            // 如果是临时状态，设置定时器
            if (duration > 0) {
                proxyServer.getScheduler().schedule(plugin, () -> {
                    bungeeUser.removeStatus(statusType);
                    // 触发状态变化事件
                    plugin.firePlayerStatusChangeEvent(player, statusType, newValue, null, "Temporary status expired", -1);
                    broadcastStatusUpdate(player, statusType, null, "Temporary status expired");
                }, duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    protected boolean shouldRestrictChatOnLowHealth(@NotNull String channelId) {
        // 在代理服务器环境中，限制策略可能来自配置
        return plugin.getSettings().getChannels().getChannel(channelId)
                .map(channel -> {
                    // 检查频道是否配置了生命值限制
                    return false; // 默认不限制
                })
                .orElse(false);
    }

    @Override
    protected boolean shouldRestrictChatInCombat(@NotNull String channelId) {
        // 在代理服务器环境中，限制策略可能来自配置
        return plugin.getSettings().getChannels().getChannel(channelId)
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
            player.getUuid(), player.getUsername(), player.getServerName(), statusType, value, reason);
        
        // 发送到所有后端服务器
        for (ServerInfo server : proxyServer.getServers().values()) {
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
    private void sendPlayerStatusMessage(@NotNull ServerInfo server, @NotNull PlayerStatusMessage message) {
        try {
            byte[] data = plugin.getGson().toJson(message).getBytes();
            server.sendData("huskchat:player_status", data);
        } catch (Exception e) {
            plugin.log(java.util.logging.Level.WARNING, "Failed to send player status message to server " + server.getName(), e);
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
        Optional<OnlineUser> playerOpt = getOnlineUsers().stream()
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
                if (player instanceof BungeeUser bungeeUser) {
                    Object previousValue = bungeeUser.getStatus(statusType).orElse(null);
                    bungeeUser.updateStatus(statusType, value);
                    
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
            if (player instanceof BungeeUser bungeeUser) {
                bungeeUser.updateStatus(PlayerStatusChangeEvent.StatusType.HEALTH, newHealth);
                bungeeUser.updateStatus(PlayerStatusChangeEvent.StatusType.MAX_HEALTH, maxHealth);
            }
            
            // 触发生命值变化事件
            plugin.firePlayerHealthChangeEvent(player, previousHealth, newHealth, maxHealth, reason, null);
        }
    }

    private void handleLocationChange(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        // 处理位置变化
        Map<String, Object> locationData = message.getData();
        
        // 触发位置变化事件
        plugin.log(java.util.logging.Level.INFO, "Player " + player.getUsername() + " location changed on server " + message.getServerName());
    }

    private void handlePlayerDeath(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        // 处理玩家死亡
        String deathMessage = message.getData("death_message", String.class);
        String killerName = message.getData("killer", String.class);
        
        OnlineUser killer = null;
        if (killerName != null) {
            killer = getOnlineUsers().stream()
                    .filter(user -> user.getUsername().equals(killerName))
                    .findFirst()
                    .orElse(null);
        }
        
        // 触发死亡事件
        plugin.firePlayerDeathEvent(player, killer, deathMessage != null ? deathMessage : "Player died");
    }

    private void handlePlayerRespawn(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        // 处理玩家重生
        String respawnReason = message.getData("reason", String.class);
        
        // 触发重生事件
        plugin.firePlayerRespawnEvent(player, respawnReason != null ? respawnReason : "Player respawned");
    }

    private void handleSyncResponse(@NotNull OnlineUser player, @NotNull PlayerStatusMessage message) {
        // 处理状态同步响应
        if (player instanceof BungeeUser bungeeUser) {
            bungeeUser.syncFromBackend(message.getData());
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
        if (!(player instanceof BungeeUser)) {
            return CompletableFuture.completedFuture(false);
        }
        
        ProxiedPlayer bungeePlayer = ((BungeeUser) player).getPlayer();
        ServerInfo currentServer = bungeePlayer.getServer().getInfo();
        
        PlayerStatusMessage syncRequest = PlayerStatusMessage.createSyncRequest(
            player.getUuid(), player.getUsername(), currentServer.getName());
        
        sendPlayerStatusMessage(currentServer, syncRequest);
        
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
    @Override
    public List<OnlineUser> getNearbyPlayers(@NotNull OnlineUser player, double radius) {
        // 在代理服务器环境中，只能返回同一服务器的玩家
        return getOnlineUsers().stream()
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
    @Override
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
    @Override
    public String createLocationBasedChannel(@NotNull OnlineUser player, double radius) {
        // 在代理服务器环境中，基于服务器创建频道
        return "server_" + player.getServerName() + "_" + (int) radius;
    }
}

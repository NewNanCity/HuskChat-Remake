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

package net.william278.huskchat.listener;

import net.william278.huskchat.BukkitHuskChat;
import net.william278.huskchat.event.PlayerLocationChangeEvent;
import net.william278.huskchat.event.PlayerStatusChangeEvent;
import net.william278.huskchat.network.PlayerStatusMessage;
import net.william278.huskchat.user.BukkitPlayerLocation;
import net.william278.huskchat.user.BukkitUser;
import net.william278.huskchat.user.OnlineUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bukkit平台的玩家状态监听器
 * Bukkit platform player status listener
 */
public class BukkitPlayerStatusListener implements Listener, PluginMessageListener {

    private final BukkitHuskChat plugin;
    private final Map<UUID, PlayerLocationChangeEvent.PlayerLocation> lastLocations = new ConcurrentHashMap<>();
    private final Map<UUID, Double> lastHealthValues = new ConcurrentHashMap<>();

    public BukkitPlayerStatusListener(@NotNull BukkitHuskChat plugin) {
        this.plugin = plugin;
    }

    // ========== Bukkit事件监听 / Bukkit Event Listeners ==========

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return; // 只有位置变化才触发
        }

        Player player = event.getPlayer();
        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        PlayerLocationChangeEvent.PlayerLocation previousLocation = lastLocations.get(player.getUniqueId());
        PlayerLocationChangeEvent.PlayerLocation newLocation = BukkitPlayerLocation.from(plugin.getServerName(), event.getTo());
        
        lastLocations.put(player.getUniqueId(), newLocation);
        
        if (previousLocation != null) {
            // 触发位置变化事件
            plugin.firePlayerLocationChangeEvent(huskPlayer, previousLocation, newLocation, 
                PlayerLocationChangeEvent.MovementReason.PLAYER_MOVEMENT);
            
            // 发送到代理服务器
            sendLocationChangeToProxy(huskPlayer, previousLocation, newLocation);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        // 初始化位置和生命值
        PlayerLocationChangeEvent.PlayerLocation location = BukkitPlayerLocation.from(plugin.getServerName(), player.getLocation());
        lastLocations.put(player.getUniqueId(), location);
        lastHealthValues.put(player.getUniqueId(), player.getHealth());
        
        // 请求状态同步
        requestStatusSyncFromProxy(huskPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        lastLocations.remove(playerId);
        lastHealthValues.remove(playerId);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        double previousHealth = lastHealthValues.getOrDefault(player.getUniqueId(), player.getHealth());
        double newHealth = Math.max(0, player.getHealth() - event.getFinalDamage());
        double maxHealth = player.getMaxHealth();
        
        lastHealthValues.put(player.getUniqueId(), newHealth);
        
        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        // 触发生命值变化事件
        plugin.firePlayerHealthChangeEvent(huskPlayer, previousHealth, newHealth, maxHealth, 
            event.getCause().name(), null);
        
        // 发送到代理服务器
        sendHealthChangeToProxy(huskPlayer, previousHealth, newHealth, maxHealth, event.getCause().name());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        Player killer = player.getKiller();
        OnlineUser huskKiller = killer != null ? BukkitUser.adapt(killer, plugin) : null;
        
        String deathMessage = event.getDeathMessage();
        
        // 触发死亡事件
        plugin.firePlayerDeathEvent(huskPlayer, huskKiller, deathMessage);
        
        // 发送到代理服务器
        sendPlayerDeathToProxy(huskPlayer, huskKiller, deathMessage);
        
        // 重置生命值记录
        lastHealthValues.put(player.getUniqueId(), player.getMaxHealth());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        // 触发重生事件
        plugin.firePlayerRespawnEvent(huskPlayer, "Player respawned");
        
        // 发送到代理服务器
        sendPlayerRespawnToProxy(huskPlayer, "Player respawned");
        
        // 更新位置记录
        PlayerLocationChangeEvent.PlayerLocation respawnLocation = BukkitPlayerLocation.from(plugin.getServerName(), event.getRespawnLocation());
        lastLocations.put(player.getUniqueId(), respawnLocation);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        // 触发状态变化事件
        plugin.firePlayerStatusChangeEvent(huskPlayer, PlayerStatusChangeEvent.StatusType.GAME_MODE, 
            player.getGameMode().name(), event.getNewGameMode().name(), "Game mode changed", -1);
        
        // 发送到代理服务器
        sendStatusUpdateToProxy(huskPlayer, PlayerStatusChangeEvent.StatusType.GAME_MODE, 
            event.getNewGameMode().name(), "Game mode changed");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        // 触发状态变化事件
        plugin.firePlayerStatusChangeEvent(huskPlayer, PlayerStatusChangeEvent.StatusType.SNEAKING, 
            !event.isSneaking(), event.isSneaking(), "Sneak toggled", -1);
        
        // 发送到代理服务器
        sendStatusUpdateToProxy(huskPlayer, PlayerStatusChangeEvent.StatusType.SNEAKING, 
            event.isSneaking(), "Sneak toggled");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        // 触发状态变化事件
        plugin.firePlayerStatusChangeEvent(huskPlayer, PlayerStatusChangeEvent.StatusType.FLYING, 
            !event.isFlying(), event.isFlying(), "Flight toggled", -1);
        
        // 发送到代理服务器
        sendStatusUpdateToProxy(huskPlayer, PlayerStatusChangeEvent.StatusType.FLYING, 
            event.isFlying(), "Flight toggled");
    }

    // ========== 插件消息处理 / Plugin Message Handling ==========

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("huskchat:player_status")) {
            return;
        }

        try {
            String json = new String(message);
            PlayerStatusMessage statusMessage = plugin.getGson().fromJson(json, PlayerStatusMessage.class);
            
            handlePlayerStatusMessage(statusMessage);
        } catch (Exception e) {
            plugin.log(java.util.logging.Level.WARNING, "Failed to handle plugin message: " + e.getMessage());
        }
    }

    private void handlePlayerStatusMessage(@NotNull PlayerStatusMessage message) {
        switch (message.getMessageType()) {
            case SYNC_REQUEST -> handleSyncRequest(message);
            case STATUS_UPDATE -> handleStatusUpdateFromProxy(message);
        }
    }

    private void handleSyncRequest(@NotNull PlayerStatusMessage message) {
        Player player = plugin.getServer().getPlayer(message.getPlayerUuid());
        if (player == null) {
            return;
        }

        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        // 收集所有状态信息
        Map<String, Object> allStatuses = new HashMap<>();
        allStatuses.put("health", player.getHealth());
        allStatuses.put("max_health", player.getMaxHealth());
        allStatuses.put("food_level", player.getFoodLevel());
        allStatuses.put("experience_level", player.getLevel());
        allStatuses.put("game_mode", player.getGameMode().name());
        allStatuses.put("sneaking", player.isSneaking());
        allStatuses.put("flying", player.isFlying());
        allStatuses.put("world", player.getWorld().getName());
        
        // 发送同步响应
        PlayerStatusMessage response = PlayerStatusMessage.createSyncResponse(
            huskPlayer.getUuid(), huskPlayer.getUsername(), plugin.getServerName(), allStatuses);
        
        sendMessageToProxy(player, response);
    }

    private void handleStatusUpdateFromProxy(@NotNull PlayerStatusMessage message) {
        // 处理来自代理服务器的状态更新
        Player player = plugin.getServer().getPlayer(message.getPlayerUuid());
        if (player == null) {
            return;
        }

        OnlineUser huskPlayer = BukkitUser.adapt(player, plugin);
        
        // 更新本地状态（如果需要）
        String statusTypeKey = message.getData("status_type", String.class);
        Object value = message.getData("value");
        
        if (statusTypeKey != null && value != null) {
            try {
                PlayerStatusChangeEvent.StatusType statusType = PlayerStatusChangeEvent.StatusType.fromKey(statusTypeKey);
                
                if (huskPlayer instanceof BukkitUser bukkitUser) {
                    bukkitUser.updateStatus(statusType, value);
                }
            } catch (Exception e) {
                plugin.log(java.util.logging.Level.WARNING, "Failed to update status from proxy: " + e.getMessage());
            }
        }
    }

    // ========== 消息发送方法 / Message Sending Methods ==========

    private void sendLocationChangeToProxy(@NotNull OnlineUser player, @NotNull PlayerLocationChangeEvent.PlayerLocation from, 
                                         @NotNull PlayerLocationChangeEvent.PlayerLocation to) {
        Map<String, Object> locationData = Map.of(
            "from_world", from.getWorld(),
            "from_x", from.getX(),
            "from_y", from.getY(),
            "from_z", from.getZ(),
            "to_world", to.getWorld(),
            "to_x", to.getX(),
            "to_y", to.getY(),
            "to_z", to.getZ()
        );
        
        PlayerStatusMessage message = PlayerStatusMessage.createLocationChange(
            player.getUuid(), player.getUsername(), plugin.getServerName(), locationData);
        
        sendMessageToProxy(((BukkitUser) player).getPlayer(), message);
    }

    private void sendHealthChangeToProxy(@NotNull OnlineUser player, double previousHealth, double newHealth, 
                                       double maxHealth, @NotNull String reason) {
        PlayerStatusMessage message = PlayerStatusMessage.createHealthChange(
            player.getUuid(), player.getUsername(), plugin.getServerName(), 
            previousHealth, newHealth, maxHealth, reason);
        
        sendMessageToProxy(((BukkitUser) player).getPlayer(), message);
    }

    private void sendPlayerDeathToProxy(@NotNull OnlineUser player, OnlineUser killer, String deathMessage) {
        Map<String, Object> data = new HashMap<>();
        data.put("death_message", deathMessage);
        if (killer != null) {
            data.put("killer", killer.getUsername());
        }
        
        PlayerStatusMessage message = new PlayerStatusMessage(
            PlayerStatusMessage.MessageType.PLAYER_DEATH, 
            player.getUuid(), player.getUsername(), plugin.getServerName(), data);
        
        sendMessageToProxy(((BukkitUser) player).getPlayer(), message);
    }

    private void sendPlayerRespawnToProxy(@NotNull OnlineUser player, @NotNull String reason) {
        Map<String, Object> data = Map.of("reason", reason);
        
        PlayerStatusMessage message = new PlayerStatusMessage(
            PlayerStatusMessage.MessageType.PLAYER_RESPAWN, 
            player.getUuid(), player.getUsername(), plugin.getServerName(), data);
        
        sendMessageToProxy(((BukkitUser) player).getPlayer(), message);
    }

    private void sendStatusUpdateToProxy(@NotNull OnlineUser player, @NotNull PlayerStatusChangeEvent.StatusType statusType, 
                                       @NotNull Object value, @NotNull String reason) {
        PlayerStatusMessage message = PlayerStatusMessage.createStatusUpdate(
            player.getUuid(), player.getUsername(), plugin.getServerName(), statusType, value, reason);
        
        sendMessageToProxy(((BukkitUser) player).getPlayer(), message);
    }

    private void requestStatusSyncFromProxy(@NotNull OnlineUser player) {
        PlayerStatusMessage message = PlayerStatusMessage.createSyncRequest(
            player.getUuid(), player.getUsername(), plugin.getServerName());
        
        sendMessageToProxy(((BukkitUser) player).getPlayer(), message);
    }

    private void sendMessageToProxy(@NotNull Player player, @NotNull PlayerStatusMessage message) {
        try {
            byte[] data = plugin.getGson().toJson(message).getBytes();
            player.sendPluginMessage(plugin, "huskchat:player_status", data);
        } catch (Exception e) {
            plugin.log(java.util.logging.Level.WARNING, "Failed to send message to proxy: " + e.getMessage());
        }
    }
}

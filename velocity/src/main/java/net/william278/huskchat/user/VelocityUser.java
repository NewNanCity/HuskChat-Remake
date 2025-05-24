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

package net.william278.huskchat.user;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.util.TriState;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.event.PlayerLocationChangeEvent;
import net.william278.huskchat.event.PlayerStatusChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Velocity implementation of a cross-platform {@link OnlineUser}
 */
public class VelocityUser extends OnlineUser {

    private final com.velocitypowered.api.proxy.Player player;
    private final Map<PlayerStatusChangeEvent.StatusType, Object> statusCache = new ConcurrentHashMap<>();
    private final long joinTime;

    private VelocityUser(@NotNull Player player, @NotNull HuskChat plugin) {
        super(player.getUsername(), player.getUniqueId(), plugin);
        this.player = player;
        this.joinTime = System.currentTimeMillis();
        initializeStatusCache();
    }

    private void initializeStatusCache() {
        // Initialize with default values for proxy environment
        statusCache.put(PlayerStatusChangeEvent.StatusType.AWAY, false);
        statusCache.put(PlayerStatusChangeEvent.StatusType.COMBAT, false);
        statusCache.put(PlayerStatusChangeEvent.StatusType.MUTED, false);
        statusCache.put(PlayerStatusChangeEvent.StatusType.BUSY, false);
        statusCache.put(PlayerStatusChangeEvent.StatusType.VANISHED, false);
    }

    @NotNull
    public static VelocityUser adapt(@NotNull Player player, @NotNull HuskChat plugin) {
        return new VelocityUser(player, plugin);
    }

    @Override
    public int getPing() {
        return (int) player.getPing();
    }

    @Override
    @NotNull
    public String getServerName() {
        final Optional<ServerConnection> connection = player.getCurrentServer();
        if (connection.isPresent()) {
            return connection.get().getServerInfo().getName();
        }
        return "";
    }

    @Override
    public int getPlayersOnServer() {
        return player.getCurrentServer().map(conn -> conn.getServer().getPlayersConnected().size()).orElse(0);
    }

    @Override
    public boolean hasPermission(@Nullable String permission, boolean allowByDefault) {
        if (permission == null) {
            return allowByDefault;
        }
        final TriState state = player.getPermissionValue(permission).toAdventureTriState();
        if (state == TriState.NOT_SET) {
            return allowByDefault;
        }
        return state == TriState.TRUE;
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    // ========== PlayerInfo Implementation ==========
    // Note: In proxy environment, most player data comes from backend servers
    // These methods provide proxy-level information and cached data

    @Override
    public double getHealth() {
        // In proxy environment, health data comes from backend server
        // Return cached value or request from backend
        return (Double) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.HEALTH, 20.0);
    }

    @Override
    public double getMaxHealth() {
        // Standard max health, can be overridden by backend server data
        return (Double) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.MAX_HEALTH, 20.0);
    }

    @Override
    public int getFoodLevel() {
        // Food level from backend server
        return (Integer) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.FOOD_LEVEL, 20);
    }

    @Override
    public int getExperienceLevel() {
        // Experience level from backend server
        return (Integer) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.EXPERIENCE_LEVEL, 0);
    }

    @NotNull
    @Override
    public PlayerLocationChangeEvent.PlayerLocation getLocation() {
        // Create proxy location - actual coordinates come from backend
        return VelocityPlayerLocation.from(getServerName(),
            (String) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.WORLD, "unknown"));
    }

    @NotNull
    @Override
    public GameMode getGameMode() {
        String gameMode = (String) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.GAME_MODE, "SURVIVAL");
        return GameMode.fromName(gameMode);
    }

    @Override
    public boolean isOnline() {
        return player.isActive();
    }

    @Override
    public boolean isSneaking() {
        return (Boolean) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.SNEAKING, false);
    }

    @Override
    public boolean isFlying() {
        return (Boolean) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.FLYING, false);
    }

    @Override
    public boolean isVanished() {
        return (Boolean) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.VANISHED, false);
    }

    @Override
    public boolean isInCombat() {
        return (Boolean) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.COMBAT, false);
    }

    @Override
    public boolean isAway() {
        return (Boolean) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.AWAY, false);
    }

    @Override
    public boolean isMuted() {
        return (Boolean) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.MUTED, false);
    }

    @NotNull
    @Override
    public Optional<Object> getStatus(@NotNull PlayerStatusChangeEvent.StatusType statusType) {
        return Optional.ofNullable(statusCache.get(statusType));
    }

    @NotNull
    @Override
    public Map<PlayerStatusChangeEvent.StatusType, Object> getAllStatuses() {
        Map<PlayerStatusChangeEvent.StatusType, Object> allStatuses = new HashMap<>(statusCache);

        // Add proxy-specific status
        allStatuses.put(PlayerStatusChangeEvent.StatusType.SERVER, getServerName());
        allStatuses.put(PlayerStatusChangeEvent.StatusType.PING, getPing());

        return allStatuses;
    }

    @Nullable
    @Override
    public String getIpAddress() {
        return player.getRemoteAddress().getAddress().getHostAddress();
    }

    @Nullable
    @Override
    public String getClientBrand() {
        // Client brand information may not be available in proxy
        return (String) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.CLIENT_BRAND, null);
    }

    @Override
    public int getProtocolVersion() {
        return player.getProtocolVersion().getProtocol();
    }

    @Nullable
    @Override
    public String getLocale() {
        return player.getPlayerSettings().getLocale().toString();
    }

    @Override
    public long getFirstJoinTime() {
        // First join time needs to be tracked separately in proxy environment
        return (Long) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.FIRST_JOIN_TIME, joinTime);
    }

    @Override
    public long getLastLoginTime() {
        // Last login time in proxy environment
        return (Long) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.LAST_LOGIN_TIME, joinTime);
    }

    @Override
    public long getSessionTime() {
        return System.currentTimeMillis() - joinTime;
    }

    @Override
    public long getTotalOnlineTime() {
        // Total online time needs to be tracked across sessions
        return (Long) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.TOTAL_ONLINE_TIME, getSessionTime());
    }

    /**
     * 更新玩家状态（代理服务器版本）
     * Update player status (proxy server version)
     *
     * @param statusType 状态类型 / status type
     * @param value 状态值 / status value
     */
    public void updateStatus(@NotNull PlayerStatusChangeEvent.StatusType statusType, @NotNull Object value) {
        if (statusType.isValidValue(value)) {
            statusCache.put(statusType, value);
        }
    }

    /**
     * 移除玩家状态
     * Remove player status
     *
     * @param statusType 状态类型 / status type
     */
    public void removeStatus(@NotNull PlayerStatusChangeEvent.StatusType statusType) {
        statusCache.remove(statusType);
    }

    /**
     * 从后端服务器同步状态
     * Sync status from backend server
     *
     * @param serverData 服务器数据 / server data
     */
    public void syncFromBackend(@NotNull Map<String, Object> serverData) {
        // Update cache with data from backend server
        serverData.forEach((key, value) -> {
            try {
                PlayerStatusChangeEvent.StatusType statusType = PlayerStatusChangeEvent.StatusType.fromKey(key);
                updateStatus(statusType, value);
            } catch (Exception e) {
                // Ignore unknown status types
            }
        });
    }

}

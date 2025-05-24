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

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.event.PlayerLocationChangeEvent;
import net.william278.huskchat.event.PlayerStatusChangeEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitUser extends OnlineUser {
    private final Player player;
    private final Map<PlayerStatusChangeEvent.StatusType, Object> statusCache = new ConcurrentHashMap<>();
    private final long joinTime;

    private BukkitUser(@NotNull Player player, @NotNull HuskChat plugin) {
        super(player.getName(), player.getUniqueId(), plugin);
        this.player = player;
        this.joinTime = System.currentTimeMillis();
        initializeStatusCache();
    }

    private void initializeStatusCache() {
        // Initialize with default values
        statusCache.put(PlayerStatusChangeEvent.StatusType.AWAY, false);
        statusCache.put(PlayerStatusChangeEvent.StatusType.COMBAT, false);
        statusCache.put(PlayerStatusChangeEvent.StatusType.MUTED, false);
        statusCache.put(PlayerStatusChangeEvent.StatusType.BUSY, false);
        statusCache.put(PlayerStatusChangeEvent.StatusType.VANISHED, false);
    }

    @NotNull
    public static BukkitUser adapt(@NotNull Player player, @NotNull HuskChat plugin) {
        return new BukkitUser(player, plugin);
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @NotNull
    @Override
    public String getServerName() {
        return "server";
    }

    @Override
    public int getPlayersOnServer() {
        return player.getServer().getOnlinePlayers().size();
    }

    @Override
    public boolean hasPermission(@Nullable String node, boolean allowByDefault) {
        if (node != null && player.isPermissionSet(node)) {
            return player.hasPermission(node);
        } else {
            return allowByDefault || player.isOp();
        }
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    // ========== PlayerInfo Implementation ==========

    @Override
    public double getHealth() {
        return player.getHealth();
    }

    @Override
    public double getMaxHealth() {
        return player.getMaxHealth();
    }

    @Override
    public int getFoodLevel() {
        return player.getFoodLevel();
    }

    @Override
    public int getExperienceLevel() {
        return player.getLevel();
    }

    @NotNull
    @Override
    public PlayerLocationChangeEvent.PlayerLocation getLocation() {
        return BukkitPlayerLocation.from(getServerName(), player.getLocation());
    }

    @NotNull
    @Override
    public GameMode getGameMode() {
        return switch (player.getGameMode()) {
            case SURVIVAL -> GameMode.SURVIVAL;
            case CREATIVE -> GameMode.CREATIVE;
            case ADVENTURE -> GameMode.ADVENTURE;
            case SPECTATOR -> GameMode.SPECTATOR;
        };
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }

    @Override
    public boolean isFlying() {
        return player.isFlying();
    }

    @Override
    public boolean isVanished() {
        // Check for common vanish plugins
        return (Boolean) statusCache.getOrDefault(PlayerStatusChangeEvent.StatusType.VANISHED, false) ||
               player.hasMetadata("vanished") ||
               !player.canSee(player); // Basic vanish check
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

        // Add real-time status updates
        allStatuses.put(PlayerStatusChangeEvent.StatusType.SNEAKING, isSneaking());
        allStatuses.put(PlayerStatusChangeEvent.StatusType.FLYING, isFlying());
        allStatuses.put(PlayerStatusChangeEvent.StatusType.GAME_MODE, getGameMode().getName());
        allStatuses.put(PlayerStatusChangeEvent.StatusType.FOOD_LEVEL, getFoodLevel());
        allStatuses.put(PlayerStatusChangeEvent.StatusType.EXPERIENCE_LEVEL, getExperienceLevel());

        return allStatuses;
    }

    @Nullable
    @Override
    public String getIpAddress() {
        return player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : null;
    }

    @Nullable
    @Override
    public String getClientBrand() {
        // Try to get client brand if available using reflection for compatibility
        try {
            // Try Paper/Spigot method first
            java.lang.reflect.Method method = player.getClass().getMethod("getClientBrandName");
            return (String) method.invoke(player);
        } catch (Exception e) {
            // Fallback for older versions
            try {
                java.lang.reflect.Method method = player.getClass().getMethod("getClientBrand");
                return (String) method.invoke(player);
            } catch (Exception ex) {
                return "Unknown";
            }
        }
    }

    @Override
    public int getProtocolVersion() {
        // Try to get protocol version if available using reflection for compatibility
        try {
            java.lang.reflect.Method method = player.getClass().getMethod("getProtocolVersion");
            return (Integer) method.invoke(player);
        } catch (Exception e) {
            return -1; // Unknown
        }
    }

    @Nullable
    @Override
    public String getLocale() {
        return player.getLocale();
    }

    @Override
    public long getFirstJoinTime() {
        return player.getFirstPlayed();
    }

    @Override
    public long getLastLoginTime() {
        return player.getLastPlayed();
    }

    @Override
    public long getSessionTime() {
        return System.currentTimeMillis() - joinTime;
    }

    @Override
    public long getTotalOnlineTime() {
        return player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
    }

    /**
     * 更新玩家状态
     * Update player status
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

}

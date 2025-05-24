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

import net.kyori.adventure.audience.Audience;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.event.PlayerLocationChangeEvent;
import net.william278.huskchat.event.PlayerStatusChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ConsoleUser extends OnlineUser {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private static final String CONSOLE_USERNAME = "[CONSOLE]";

    private ConsoleUser(@NotNull HuskChat plugin) {
        super(CONSOLE_USERNAME, CONSOLE_UUID, plugin);
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    @NotNull
    public String getServerName() {
        return plugin.getPlatform();
    }

    @Override
    public int getPlayersOnServer() {
        return plugin.getOnlinePlayers().size();
    }

    @Override
    public boolean hasPermission(@Nullable String node, boolean allowByDefault) {
        return true;
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return plugin.getConsole();
    }

    /**
     * Adapt the proxy console player into a cross-platform one
     *
     * @param plugin The implementing HuskChat plugin
     * @return The ConsolePlayer
     */
    @NotNull
    public static ConsoleUser wrap(@NotNull HuskChat plugin) {
        return new ConsoleUser(plugin);
    }

    /**
     * Returns true if the UUID is that of the console player
     *
     * @param uuid UUID to check
     * @return {@code true} if the UUID is the console
     */
    public static boolean isConsolePlayer(@NotNull UUID uuid) {
        return uuid.equals(CONSOLE_UUID);
    }

    /**
     * Returns true if the username is that of the console player
     *
     * @param username username to check
     * @return {@code true} if the username is the console
     */
    public static boolean isConsolePlayer(@NotNull String username) {
        return username.equalsIgnoreCase(CONSOLE_USERNAME);
    }

    // ========== PlayerInfo 接口实现 / PlayerInfo interface implementation ==========

    @Override
    public double getHealth() {
        return 20.0; // 控制台用户总是满血
    }

    @Override
    public double getMaxHealth() {
        return 20.0;
    }

    @Override
    public int getFoodLevel() {
        return 20; // 控制台用户总是满饱食度
    }

    @Override
    public int getExperienceLevel() {
        return 0;
    }

    @Override
    @NotNull
    public PlayerLocationChangeEvent.PlayerLocation getLocation() {
        return new PlayerLocationChangeEvent.PlayerLocation() {
            @Override
            @NotNull
            public String getServer() {
                return "console";
            }

            @Override
            @NotNull
            public String getWorld() {
                return "console";
            }

            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public double getZ() {
                return 0;
            }

            @Override
            public float getYaw() {
                return 0;
            }

            @Override
            public float getPitch() {
                return 0;
            }
        };
    }

    @Override
    @NotNull
    public GameMode getGameMode() {
        return GameMode.CREATIVE; // 控制台用户默认创造模式
    }

    @Override
    public boolean isOnline() {
        return true; // 控制台总是在线
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public boolean isVanished() {
        return false;
    }

    @Override
    public boolean isMuted() {
        return false; // 控制台不能被禁言
    }

    @Override
    public boolean isInCombat() {
        return false;
    }

    @Override
    public boolean isAway() {
        return false; // 控制台不会离开
    }

    @Override
    @NotNull
    public Optional<Object> getStatus(@NotNull PlayerStatusChangeEvent.StatusType statusType) {
        return Optional.empty(); // 控制台用户没有特殊状态
    }

    @Override
    @NotNull
    public Map<PlayerStatusChangeEvent.StatusType, Object> getAllStatuses() {
        return Map.of(); // 控制台用户没有状态
    }

    @Override
    @Nullable
    public String getIpAddress() {
        return "127.0.0.1"; // 本地地址
    }

    @Override
    @Nullable
    public String getClientBrand() {
        return "Console";
    }

    @Override
    public int getProtocolVersion() {
        return 0;
    }

    @Override
    @Nullable
    public String getLocale() {
        return "en_US";
    }

    @Override
    public long getFirstJoinTime() {
        return 0; // 控制台没有首次加入时间
    }

    @Override
    public long getLastLoginTime() {
        return System.currentTimeMillis();
    }

    @Override
    public long getSessionTime() {
        return 0; // 控制台没有会话时间概念
    }

    @Override
    public long getTotalOnlineTime() {
        return 0; // 控制台没有在线时间概念
    }

}

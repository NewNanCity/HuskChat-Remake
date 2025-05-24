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
import net.william278.huskchat.event.PlayerLocationChangeEvent;
import net.william278.huskchat.event.PlayerStatusChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TestOnlineUser extends OnlineUser {

    private final static int TEST_PLAYER_PING = 5;
    private final static String TEST_PLAYER_SERVER = "test";
    private final static int TEST_PLAYER_SERVER_PLAYER_COUNT = 1;

    public TestOnlineUser() {
        super(UUID.randomUUID().toString().split("-")[0], UUID.randomUUID());
    }

    @Override
    public int getPing() {
        return TEST_PLAYER_PING;
    }

    @Override
    @NotNull
    public String getServerName() {
        return TEST_PLAYER_SERVER;
    }

    @Override
    public int getPlayersOnServer() {
        return TEST_PLAYER_SERVER_PLAYER_COUNT;
    }

    @Override
    public boolean hasPermission(@Nullable String permission, boolean allowByDefault) {
        return true;
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return Audience.empty();
    }

    // ========== PlayerInfo 接口实现 / PlayerInfo interface implementation ==========

    @Override
    public double getHealth() {
        return 20.0;
    }

    @Override
    public double getMaxHealth() {
        return 20.0;
    }

    @Override
    public int getFoodLevel() {
        return 20;
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
                return TEST_PLAYER_SERVER;
            }

            @Override
            @NotNull
            public String getWorld() {
                return "test_world";
            }

            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 64;
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
        return GameMode.SURVIVAL;
    }

    @Override
    public boolean isOnline() {
        return true;
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
        return false;
    }

    @Override
    public boolean isInCombat() {
        return false;
    }

    @Override
    public boolean isAway() {
        return false;
    }

    @Override
    @NotNull
    public Optional<Object> getStatus(@NotNull PlayerStatusChangeEvent.StatusType statusType) {
        return Optional.empty();
    }

    @Override
    @NotNull
    public Map<PlayerStatusChangeEvent.StatusType, Object> getAllStatuses() {
        return Map.of();
    }

    @Override
    @Nullable
    public String getIpAddress() {
        return "127.0.0.1";
    }

    @Override
    @Nullable
    public String getClientBrand() {
        return "test";
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
        return 0;
    }

    @Override
    public long getLastLoginTime() {
        return System.currentTimeMillis();
    }

    @Override
    public long getSessionTime() {
        return 0;
    }

    @Override
    public long getTotalOnlineTime() {
        return 0;
    }
}

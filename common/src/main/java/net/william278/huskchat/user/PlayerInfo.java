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

import net.william278.huskchat.event.PlayerLocationChangeEvent;
import net.william278.huskchat.event.PlayerStatusChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * 玩家信息接口 - 提供Minecraft特定的玩家数据
 * Player info interface - provides Minecraft-specific player data
 */
public interface PlayerInfo {

    /**
     * 获取玩家生命值
     * Get player health
     *
     * @return 当前生命值 / current health
     */
    double getHealth();

    /**
     * 获取玩家最大生命值
     * Get player maximum health
     *
     * @return 最大生命值 / maximum health
     */
    double getMaxHealth();

    /**
     * 获取玩家饥饿值
     * Get player food level
     *
     * @return 饥饿值 / food level
     */
    int getFoodLevel();

    /**
     * 获取玩家经验等级
     * Get player experience level
     *
     * @return 经验等级 / experience level
     */
    int getExperienceLevel();

    /**
     * 获取玩家当前位置
     * Get player current location
     *
     * @return 当前位置 / current location
     */
    @NotNull
    PlayerLocationChangeEvent.PlayerLocation getLocation();

    /**
     * 获取玩家游戏模式
     * Get player game mode
     *
     * @return 游戏模式 / game mode
     */
    @NotNull
    GameMode getGameMode();

    /**
     * 检查玩家是否在线
     * Check if player is online
     *
     * @return 是否在线 / whether online
     */
    boolean isOnline();

    /**
     * 检查玩家是否潜行
     * Check if player is sneaking
     *
     * @return 是否潜行 / whether sneaking
     */
    boolean isSneaking();

    /**
     * 检查玩家是否在飞行
     * Check if player is flying
     *
     * @return 是否飞行 / whether flying
     */
    boolean isFlying();

    /**
     * 检查玩家是否隐身
     * Check if player is vanished
     *
     * @return 是否隐身 / whether vanished
     */
    boolean isVanished();

    /**
     * 检查玩家是否处于战斗状态
     * Check if player is in combat
     *
     * @return 是否战斗状态 / whether in combat
     */
    boolean isInCombat();

    /**
     * 检查玩家是否离开状态
     * Check if player is away
     *
     * @return 是否离开 / whether away
     */
    boolean isAway();

    /**
     * 检查玩家是否被禁言
     * Check if player is muted
     *
     * @return 是否被禁言 / whether muted
     */
    boolean isMuted();

    /**
     * 获取玩家状态
     * Get player status
     *
     * @param statusType 状态类型 / status type
     * @return 状态值 / status value
     */
    @NotNull
    Optional<Object> getStatus(@NotNull PlayerStatusChangeEvent.StatusType statusType);

    /**
     * 获取所有玩家状态
     * Get all player statuses
     *
     * @return 状态映射 / status map
     */
    @NotNull
    Map<PlayerStatusChangeEvent.StatusType, Object> getAllStatuses();

    /**
     * 获取玩家IP地址
     * Get player IP address
     *
     * @return IP地址 / IP address
     */
    @Nullable
    String getIpAddress();

    /**
     * 获取玩家客户端品牌
     * Get player client brand
     *
     * @return 客户端品牌 / client brand
     */
    @Nullable
    String getClientBrand();

    /**
     * 获取玩家协议版本
     * Get player protocol version
     *
     * @return 协议版本 / protocol version
     */
    int getProtocolVersion();

    /**
     * 获取玩家区域设置
     * Get player locale
     *
     * @return 区域设置 / locale
     */
    @Nullable
    String getLocale();

    /**
     * 获取玩家首次加入时间
     * Get player first join time
     *
     * @return 首次加入时间戳 / first join timestamp
     */
    long getFirstJoinTime();

    /**
     * 获取玩家最后登录时间
     * Get player last login time
     *
     * @return 最后登录时间戳 / last login timestamp
     */
    long getLastLoginTime();

    /**
     * 获取玩家在线时长（当前会话）
     * Get player online time (current session)
     *
     * @return 在线时长（毫秒） / online time (milliseconds)
     */
    long getSessionTime();

    /**
     * 获取玩家总在线时长
     * Get player total online time
     *
     * @return 总在线时长（毫秒） / total online time (milliseconds)
     */
    long getTotalOnlineTime();

    /**
     * 检查玩家是否为新玩家
     * Check if player is a new player
     *
     * @return 是否新玩家 / whether new player
     */
    default boolean isNewPlayer() {
        return getSessionTime() < 300000; // 5 minutes
    }

    /**
     * 检查玩家是否处于低血量状态
     * Check if player is in low health state
     *
     * @return 是否低血量 / whether low health
     */
    default boolean isLowHealth() {
        return getHealth() <= getMaxHealth() * 0.2;
    }

    /**
     * 检查玩家是否处于危险状态
     * Check if player is in critical health state
     *
     * @return 是否危险状态 / whether critical health
     */
    default boolean isCriticalHealth() {
        return getHealth() <= getMaxHealth() * 0.1;
    }

    /**
     * 检查玩家是否饥饿
     * Check if player is hungry
     *
     * @return 是否饥饿 / whether hungry
     */
    default boolean isHungry() {
        return getFoodLevel() <= 6; // 3 hunger bars or less
    }

    /**
     * 获取生命值百分比
     * Get health percentage
     *
     * @return 生命值百分比 / health percentage
     */
    default double getHealthPercentage() {
        return (getHealth() / getMaxHealth()) * 100;
    }

    /**
     * 获取饥饿值百分比
     * Get food level percentage
     *
     * @return 饥饿值百分比 / food level percentage
     */
    default double getFoodPercentage() {
        return (getFoodLevel() / 20.0) * 100;
    }

    /**
     * 游戏模式枚举
     * Game mode enum
     */
    enum GameMode {
        /**
         * 生存模式 / Survival mode
         */
        SURVIVAL("survival", "S"),
        
        /**
         * 创造模式 / Creative mode
         */
        CREATIVE("creative", "C"),
        
        /**
         * 冒险模式 / Adventure mode
         */
        ADVENTURE("adventure", "A"),
        
        /**
         * 旁观模式 / Spectator mode
         */
        SPECTATOR("spectator", "SP"),
        
        /**
         * 未知模式 / Unknown mode
         */
        UNKNOWN("unknown", "?");

        private final String name;
        private final String shortName;

        GameMode(String name, String shortName) {
            this.name = name;
            this.shortName = shortName;
        }

        /**
         * 获取游戏模式名称
         * Get game mode name
         *
         * @return 游戏模式名称 / game mode name
         */
        @NotNull
        public String getName() {
            return name;
        }

        /**
         * 获取游戏模式简称
         * Get game mode short name
         *
         * @return 游戏模式简称 / game mode short name
         */
        @NotNull
        public String getShortName() {
            return shortName;
        }

        /**
         * 根据名称获取游戏模式
         * Get game mode by name
         *
         * @param name 游戏模式名称 / game mode name
         * @return 游戏模式 / game mode
         */
        @NotNull
        public static GameMode fromName(@NotNull String name) {
            String lowerName = name.toLowerCase();
            for (GameMode mode : values()) {
                if (mode.name.equals(lowerName) || mode.shortName.toLowerCase().equals(lowerName)) {
                    return mode;
                }
            }
            return UNKNOWN;
        }
    }
}

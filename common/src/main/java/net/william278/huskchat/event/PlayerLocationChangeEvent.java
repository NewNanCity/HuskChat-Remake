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

package net.william278.huskchat.event;

import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 玩家位置变化事件 - 当玩家位置发生变化时触发
 * Player location change event - triggered when a player's location changes
 */
public interface PlayerLocationChangeEvent extends EventBase {

    /**
     * 获取位置变化的玩家
     * Get the player whose location changed
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getPlayer();

    /**
     * 获取之前的位置
     * Get the previous location
     *
     * @return 之前的位置 / previous location
     */
    @Nullable
    PlayerLocation getPreviousLocation();

    /**
     * 获取新的位置
     * Get the new location
     *
     * @return 新的位置 / new location
     */
    @NotNull
    PlayerLocation getNewLocation();

    /**
     * 获取移动原因
     * Get the movement reason
     *
     * @return 移动原因 / movement reason
     */
    @NotNull
    MovementReason getReason();

    /**
     * 检查是否跨世界移动
     * Check if this is a cross-world movement
     *
     * @return 是否跨世界 / whether cross-world
     */
    default boolean isCrossWorld() {
        return getPreviousLocation() != null && 
               !getPreviousLocation().getWorld().equals(getNewLocation().getWorld());
    }

    /**
     * 检查是否跨服务器移动
     * Check if this is a cross-server movement
     *
     * @return 是否跨服务器 / whether cross-server
     */
    default boolean isCrossServer() {
        return getPreviousLocation() != null && 
               !getPreviousLocation().getServer().equals(getNewLocation().getServer());
    }

    /**
     * 计算移动距离
     * Calculate movement distance
     *
     * @return 移动距离，如果跨世界则返回-1 / movement distance, -1 if cross-world
     */
    default double getDistance() {
        if (getPreviousLocation() == null || isCrossWorld()) {
            return -1;
        }
        return getPreviousLocation().distance(getNewLocation());
    }

    /**
     * 玩家位置信息
     * Player location information
     */
    interface PlayerLocation {
        /**
         * 获取服务器名称
         * Get server name
         */
        @NotNull
        String getServer();

        /**
         * 获取世界名称
         * Get world name
         */
        @NotNull
        String getWorld();

        /**
         * 获取X坐标
         * Get X coordinate
         */
        double getX();

        /**
         * 获取Y坐标
         * Get Y coordinate
         */
        double getY();

        /**
         * 获取Z坐标
         * Get Z coordinate
         */
        double getZ();

        /**
         * 获取偏航角
         * Get yaw
         */
        float getYaw();

        /**
         * 获取俯仰角
         * Get pitch
         */
        float getPitch();

        /**
         * 计算与另一个位置的距离
         * Calculate distance to another location
         *
         * @param other 另一个位置 / other location
         * @return 距离 / distance
         */
        default double distance(@NotNull PlayerLocation other) {
            if (!getWorld().equals(other.getWorld()) || !getServer().equals(other.getServer())) {
                return -1;
            }
            double dx = getX() - other.getX();
            double dy = getY() - other.getY();
            double dz = getZ() - other.getZ();
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }

        /**
         * 获取区块坐标
         * Get chunk coordinates
         *
         * @return 区块坐标字符串 / chunk coordinates string
         */
        @NotNull
        default String getChunkCoordinates() {
            return String.format("%d,%d", (int) getX() >> 4, (int) getZ() >> 4);
        }

        /**
         * 获取区域标识符（用于区域频道）
         * Get region identifier (for region channels)
         *
         * @return 区域标识符 / region identifier
         */
        @NotNull
        default String getRegionId() {
            return String.format("%s:%s:%s", getServer(), getWorld(), getChunkCoordinates());
        }
    }

    /**
     * 移动原因枚举
     * Movement reason enum
     */
    enum MovementReason {
        /**
         * 玩家自主移动 / Player self movement
         */
        PLAYER_MOVEMENT,
        
        /**
         * 传送 / Teleportation
         */
        TELEPORT,
        
        /**
         * 服务器切换 / Server switch
         */
        SERVER_SWITCH,
        
        /**
         * 世界切换 / World change
         */
        WORLD_CHANGE,
        
        /**
         * 重生 / Respawn
         */
        RESPAWN,
        
        /**
         * 插件传送 / Plugin teleport
         */
        PLUGIN,
        
        /**
         * 其他原因 / Other reason
         */
        OTHER
    }
}

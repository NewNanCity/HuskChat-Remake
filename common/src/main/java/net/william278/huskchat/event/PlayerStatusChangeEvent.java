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
 * 玩家状态变化事件 - 当玩家状态发生变化时触发
 * Player status change event - triggered when a player's status changes
 */
public interface PlayerStatusChangeEvent extends EventBase {

    /**
     * 获取状态变化的玩家
     * Get the player whose status changed
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getPlayer();

    /**
     * 获取状态类型
     * Get the status type
     *
     * @return 状态类型 / status type
     */
    @NotNull
    StatusType getStatusType();

    /**
     * 获取之前的状态值
     * Get the previous status value
     *
     * @return 之前的状态值 / previous status value
     */
    @Nullable
    Object getPreviousValue();

    /**
     * 获取新的状态值
     * Get the new status value
     *
     * @return 新的状态值 / new status value
     */
    @NotNull
    Object getNewValue();

    /**
     * 获取状态变化原因
     * Get the status change reason
     *
     * @return 变化原因 / change reason
     */
    @NotNull
    String getReason();

    /**
     * 获取状态变化的持续时间（毫秒，-1表示永久）
     * Get the duration of the status change (milliseconds, -1 for permanent)
     *
     * @return 持续时间 / duration
     */
    long getDuration();

    /**
     * 检查状态变化是否为临时的
     * Check if the status change is temporary
     *
     * @return 是否临时 / whether temporary
     */
    default boolean isTemporary() {
        return getDuration() > 0;
    }

    /**
     * 玩家状态类型枚举
     * Player status type enum
     */
    enum StatusType {
        /**
         * 离开状态 / Away status
         */
        AWAY("away", Boolean.class),
        
        /**
         * 战斗模式 / Combat mode
         */
        COMBAT("combat", Boolean.class),
        
        /**
         * 潜行状态 / Sneaking status
         */
        SNEAKING("sneaking", Boolean.class),
        
        /**
         * 飞行状态 / Flying status
         */
        FLYING("flying", Boolean.class),
        
        /**
         * 游戏模式 / Game mode
         */
        GAME_MODE("gamemode", String.class),
        
        /**
         * 饥饿值 / Food level
         */
        FOOD_LEVEL("food", Integer.class),
        
        /**
         * 经验等级 / Experience level
         */
        EXPERIENCE_LEVEL("exp_level", Integer.class),
        
        /**
         * 禁言状态 / Muted status
         */
        MUTED("muted", Boolean.class),
        
        /**
         * 忙碌状态 / Busy status
         */
        BUSY("busy", Boolean.class),
        
        /**
         * 隐身状态 / Vanished status
         */
        VANISHED("vanished", Boolean.class),
        
        /**
         * 自定义状态 / Custom status
         */
        CUSTOM("custom", Object.class);

        private final String key;
        private final Class<?> valueType;

        StatusType(String key, Class<?> valueType) {
            this.key = key;
            this.valueType = valueType;
        }

        /**
         * 获取状态键
         * Get status key
         *
         * @return 状态键 / status key
         */
        @NotNull
        public String getKey() {
            return key;
        }

        /**
         * 获取值类型
         * Get value type
         *
         * @return 值类型 / value type
         */
        @NotNull
        public Class<?> getValueType() {
            return valueType;
        }

        /**
         * 根据键获取状态类型
         * Get status type by key
         *
         * @param key 状态键 / status key
         * @return 状态类型 / status type
         */
        @NotNull
        public static StatusType fromKey(@NotNull String key) {
            for (StatusType type : values()) {
                if (type.key.equals(key)) {
                    return type;
                }
            }
            return CUSTOM;
        }

        /**
         * 验证值类型
         * Validate value type
         *
         * @param value 要验证的值 / value to validate
         * @return 是否有效 / whether valid
         */
        public boolean isValidValue(@Nullable Object value) {
            if (value == null) {
                return true;
            }
            return valueType.isAssignableFrom(value.getClass()) || valueType == Object.class;
        }
    }
}

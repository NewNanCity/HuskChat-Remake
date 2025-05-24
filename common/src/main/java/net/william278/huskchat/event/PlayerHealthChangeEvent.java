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
 * 玩家生命值变化事件 - 当玩家生命值发生变化时触发
 * Player health change event - triggered when a player's health changes
 */
public interface PlayerHealthChangeEvent extends EventBase {

    /**
     * 获取生命值变化的玩家
     * Get the player whose health changed
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getPlayer();

    /**
     * 获取之前的生命值
     * Get the previous health value
     *
     * @return 之前的生命值 / previous health value
     */
    double getPreviousHealth();

    /**
     * 获取新的生命值
     * Get the new health value
     *
     * @return 新的生命值 / new health value
     */
    double getNewHealth();

    /**
     * 获取最大生命值
     * Get the maximum health value
     *
     * @return 最大生命值 / maximum health value
     */
    double getMaxHealth();

    /**
     * 获取生命值变化原因
     * Get the health change reason
     *
     * @return 变化原因 / change reason
     */
    @NotNull
    HealthChangeReason getReason();

    /**
     * 获取造成伤害的实体（如果适用）
     * Get the damaging entity (if applicable)
     *
     * @return 造成伤害的实体，如果不适用则为null / damaging entity, null if not applicable
     */
    @Nullable
    String getDamager();

    /**
     * 检查玩家是否即将死亡
     * Check if the player is about to die
     *
     * @return 是否即将死亡 / whether about to die
     */
    default boolean isAboutToDie() {
        return getNewHealth() <= 0;
    }

    /**
     * 检查玩家是否处于低血量状态
     * Check if the player is in low health state
     *
     * @return 是否低血量 / whether in low health
     */
    default boolean isLowHealth() {
        return getNewHealth() <= getMaxHealth() * 0.2; // 20% or less
    }

    /**
     * 检查玩家是否处于危险状态
     * Check if the player is in critical health state
     *
     * @return 是否危险状态 / whether in critical state
     */
    default boolean isCriticalHealth() {
        return getNewHealth() <= getMaxHealth() * 0.1; // 10% or less
    }

    /**
     * 生命值变化原因枚举
     * Health change reason enum
     */
    enum HealthChangeReason {
        /**
         * 实体攻击 / Entity attack
         */
        ENTITY_ATTACK,
        
        /**
         * 玩家攻击 / Player attack
         */
        PLAYER_ATTACK,
        
        /**
         * 环境伤害（掉落、熔岩等） / Environmental damage (fall, lava, etc.)
         */
        ENVIRONMENTAL,
        
        /**
         * 饥饿 / Starvation
         */
        STARVATION,
        
        /**
         * 中毒 / Poison
         */
        POISON,
        
        /**
         * 魔法伤害 / Magic damage
         */
        MAGIC,
        
        /**
         * 治疗 / Healing
         */
        HEALING,
        
        /**
         * 再生 / Regeneration
         */
        REGENERATION,
        
        /**
         * 食物恢复 / Food restoration
         */
        FOOD,
        
        /**
         * 药水效果 / Potion effect
         */
        POTION,
        
        /**
         * 插件或命令 / Plugin or command
         */
        PLUGIN,
        
        /**
         * 其他原因 / Other reason
         */
        OTHER
    }
}

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
 * 玩家死亡事件 - 当玩家死亡时触发
 * Player death event - triggered when a player dies
 */
public interface PlayerDeathEvent extends EventBase {

    /**
     * 获取死亡的玩家
     * Get the player who died
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getPlayer();

    /**
     * 获取死亡消息
     * Get the death message
     *
     * @return 死亡消息 / death message
     */
    @NotNull
    String getDeathMessage();

    /**
     * 设置死亡消息
     * Set the death message
     *
     * @param message 新的死亡消息 / new death message
     */
    void setDeathMessage(@NotNull String message);

    /**
     * 获取死亡原因
     * Get the death cause
     *
     * @return 死亡原因 / death cause
     */
    @NotNull
    DeathCause getDeathCause();

    /**
     * 获取杀手（如果有）
     * Get the killer (if any)
     *
     * @return 杀手玩家，如果没有则为null / killer player, null if none
     */
    @Nullable
    OnlineUser getKiller();

    /**
     * 获取死亡位置
     * Get the death location
     *
     * @return 死亡位置 / death location
     */
    @NotNull
    PlayerLocationChangeEvent.PlayerLocation getDeathLocation();

    /**
     * 检查是否应该发送死亡消息到聊天
     * Check if death message should be sent to chat
     *
     * @return 是否发送死亡消息 / whether to send death message
     */
    boolean shouldSendDeathMessage();

    /**
     * 设置是否发送死亡消息到聊天
     * Set whether to send death message to chat
     *
     * @param send 是否发送 / whether to send
     */
    void setSendDeathMessage(boolean send);

    /**
     * 获取死亡消息应该发送到的频道
     * Get the channel where death message should be sent
     *
     * @return 频道ID，null表示使用默认频道 / channel ID, null for default channel
     */
    @Nullable
    String getDeathMessageChannel();

    /**
     * 设置死亡消息应该发送到的频道
     * Set the channel where death message should be sent
     *
     * @param channelId 频道ID / channel ID
     */
    void setDeathMessageChannel(@Nullable String channelId);

    /**
     * 死亡原因枚举
     * Death cause enum
     */
    enum DeathCause {
        /**
         * 玩家击杀 / Player kill
         */
        PLAYER_KILL,

        /**
         * 实体击杀 / Entity kill
         */
        ENTITY_KILL,

        /**
         * 掉落伤害 / Fall damage
         */
        FALL_DAMAGE,

        /**
         * 火焰伤害 / Fire damage
         */
        FIRE_DAMAGE,

        /**
         * 熔岩伤害 / Lava damage
         */
        LAVA_DAMAGE,

        /**
         * 溺水 / Drowning
         */
        DROWNING,

        /**
         * 窒息 / Suffocation
         */
        SUFFOCATION,

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
        MAGIC_DAMAGE,

        /**
         * 爆炸 / Explosion
         */
        EXPLOSION,

        /**
         * 虚空伤害 / Void damage
         */
        VOID_DAMAGE,

        /**
         * 闪电 / Lightning
         */
        LIGHTNING,

        /**
         * 自杀 / Suicide
         */
        SUICIDE,

        /**
         * 插件或命令 / Plugin or command
         */
        PLUGIN,

        /**
         * 其他原因 / Other cause
         */
        OTHER
    }
}

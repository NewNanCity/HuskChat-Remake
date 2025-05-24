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
 * 玩家重生事件 - 当玩家重生时触发
 * Player respawn event - triggered when a player respawns
 */
public interface PlayerRespawnEvent extends EventBase {

    /**
     * 获取重生的玩家
     * Get the player who respawned
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getPlayer();

    /**
     * 获取重生位置
     * Get the respawn location
     *
     * @return 重生位置 / respawn location
     */
    @NotNull
    PlayerLocationChangeEvent.PlayerLocation getRespawnLocation();

    /**
     * 设置重生位置
     * Set the respawn location
     *
     * @param location 新的重生位置 / new respawn location
     */
    void setRespawnLocation(@NotNull PlayerLocationChangeEvent.PlayerLocation location);

    /**
     * 获取重生原因
     * Get the respawn reason
     *
     * @return 重生原因 / respawn reason
     */
    @NotNull
    RespawnReason getReason();

    /**
     * 检查是否应该发送重生消息到聊天
     * Check if respawn message should be sent to chat
     *
     * @return 是否发送重生消息 / whether to send respawn message
     */
    boolean shouldSendRespawnMessage();

    /**
     * 设置是否发送重生消息到聊天
     * Set whether to send respawn message to chat
     *
     * @param send 是否发送 / whether to send
     */
    void setSendRespawnMessage(boolean send);

    /**
     * 获取重生消息应该发送到的频道
     * Get the channel where respawn message should be sent
     *
     * @return 频道ID，null表示使用默认频道 / channel ID, null for default channel
     */
    @Nullable
    String getRespawnMessageChannel();

    /**
     * 设置重生消息应该发送到的频道
     * Set the channel where respawn message should be sent
     *
     * @param channelId 频道ID / channel ID
     */
    void setRespawnMessageChannel(@Nullable String channelId);

    /**
     * 重生原因枚举
     * Respawn reason enum
     */
    enum RespawnReason {
        /**
         * 正常重生 / Normal respawn
         */
        NORMAL,
        
        /**
         * 床重生 / Bed respawn
         */
        BED,
        
        /**
         * 重生锚重生 / Respawn anchor
         */
        RESPAWN_ANCHOR,
        
        /**
         * 插件重生 / Plugin respawn
         */
        PLUGIN,
        
        /**
         * 其他原因 / Other reason
         */
        OTHER
    }
}

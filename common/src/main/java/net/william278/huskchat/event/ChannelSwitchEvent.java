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
 * 频道切换事件 - 当玩家切换聊天频道时触发
 * Channel switch event - triggered when a player switches chat channels
 */
public interface ChannelSwitchEvent extends EventBase {

    /**
     * 获取切换频道的玩家
     * Get the player switching channels
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getPlayer();

    /**
     * 获取原频道ID
     * Get the previous channel ID
     *
     * @return 原频道ID，如果是首次加入则为null / previous channel ID, null if first join
     */
    @Nullable
    String getPreviousChannelId();

    /**
     * 获取新频道ID
     * Get the new channel ID
     *
     * @return 新频道ID / new channel ID
     */
    @NotNull
    String getNewChannelId();

    /**
     * 设置新频道ID
     * Set the new channel ID
     *
     * @param channelId 新频道ID / new channel ID
     */
    void setNewChannelId(@NotNull String channelId);

    /**
     * 获取切换原因
     * Get the switch reason
     *
     * @return 切换原因 / switch reason
     */
    @NotNull
    SwitchReason getReason();

    /**
     * 频道切换原因枚举
     * Channel switch reason enum
     */
    enum SwitchReason {
        /**
         * 玩家主动切换 / Player manually switched
         */
        PLAYER_COMMAND,
        
        /**
         * 服务器切换导致的自动切换 / Auto switch due to server change
         */
        SERVER_SWITCH,
        
        /**
         * 玩家加入服务器 / Player joined server
         */
        PLAYER_JOIN,
        
        /**
         * 管理员强制切换 / Admin forced switch
         */
        ADMIN_FORCE,
        
        /**
         * 插件API调用 / Plugin API call
         */
        API_CALL,
        
        /**
         * 其他原因 / Other reason
         */
        OTHER
    }
}

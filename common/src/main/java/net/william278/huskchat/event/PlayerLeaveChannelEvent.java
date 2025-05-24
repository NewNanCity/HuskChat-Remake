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

/**
 * 玩家离开频道事件 - 当玩家离开聊天频道时触发
 * Player leave channel event - triggered when a player leaves a chat channel
 */
public interface PlayerLeaveChannelEvent extends EventBase {

    /**
     * 获取离开频道的玩家
     * Get the player leaving the channel
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getPlayer();

    /**
     * 获取频道ID
     * Get the channel ID
     *
     * @return 频道ID / channel ID
     */
    @NotNull
    String getChannelId();

    /**
     * 获取离开原因
     * Get the leave reason
     *
     * @return 离开原因 / leave reason
     */
    @NotNull
    LeaveReason getReason();

    /**
     * 频道离开原因枚举
     * Channel leave reason enum
     */
    enum LeaveReason {
        /**
         * 玩家断开连接 / Player disconnect
         */
        DISCONNECT,
        
        /**
         * 切换到其他频道 / Switch to another channel
         */
        CHANNEL_SWITCH,
        
        /**
         * 服务器切换 / Server switch
         */
        SERVER_SWITCH,
        
        /**
         * 管理员操作 / Admin action
         */
        ADMIN_ACTION,
        
        /**
         * 插件API调用 / Plugin API call
         */
        API_CALL,
        
        /**
         * 权限不足被踢出 / Kicked due to insufficient permissions
         */
        PERMISSION_REVOKED
    }
}

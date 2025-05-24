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
 * 玩家加入频道事件 - 当玩家加入聊天频道时触发
 * Player join channel event - triggered when a player joins a chat channel
 */
public interface PlayerJoinChannelEvent extends EventBase {

    /**
     * 获取加入频道的玩家
     * Get the player joining the channel
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
     * 获取加入原因
     * Get the join reason
     *
     * @return 加入原因 / join reason
     */
    @NotNull
    JoinReason getReason();

    /**
     * 频道加入原因枚举
     * Channel join reason enum
     */
    enum JoinReason {
        /**
         * 玩家首次登录 / Player first login
         */
        FIRST_LOGIN,
        
        /**
         * 玩家重新登录 / Player reconnect
         */
        RECONNECT,
        
        /**
         * 服务器切换 / Server switch
         */
        SERVER_SWITCH,
        
        /**
         * 手动切换 / Manual switch
         */
        MANUAL_SWITCH,
        
        /**
         * 管理员操作 / Admin action
         */
        ADMIN_ACTION,
        
        /**
         * 插件API调用 / Plugin API call
         */
        API_CALL
    }
}

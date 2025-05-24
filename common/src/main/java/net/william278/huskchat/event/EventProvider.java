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

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 事件提供者接口 - 定义所有HuskChat事件的触发方法
 * Event provider interface - defines methods for triggering all HuskChat events
 */
public interface EventProvider {

    /**
     * 触发聊天消息事件
     * Fire chat message event
     */
    CompletableFuture<ChatMessageEvent> fireChatMessageEvent(@NotNull OnlineUser player, @NotNull String message, @NotNull String channelId);

    /**
     * 触发私聊消息事件
     * Fire private message event
     */
    CompletableFuture<PrivateMessageEvent> firePrivateMessageEvent(@NotNull OnlineUser sender, @NotNull List<OnlineUser> receivers, @NotNull String message);

    /**
     * 触发广播消息事件
     * Fire broadcast message event
     */
    CompletableFuture<BroadcastMessageEvent> fireBroadcastMessageEvent(@NotNull OnlineUser sender, @NotNull String message);

    /**
     * 触发频道切换事件
     * Fire channel switch event
     */
    CompletableFuture<ChannelSwitchEvent> fireChannelSwitchEvent(@NotNull OnlineUser player, @Nullable String previousChannelId, @NotNull String newChannelId, @NotNull ChannelSwitchEvent.SwitchReason reason);

    /**
     * 触发玩家加入频道事件
     * Fire player join channel event
     */
    CompletableFuture<PlayerJoinChannelEvent> firePlayerJoinChannelEvent(@NotNull OnlineUser player, @NotNull String channelId, @NotNull PlayerJoinChannelEvent.JoinReason reason);

    /**
     * 触发玩家离开频道事件
     * Fire player leave channel event
     */
    CompletableFuture<PlayerLeaveChannelEvent> firePlayerLeaveChannelEvent(@NotNull OnlineUser player, @NotNull String channelId, @NotNull PlayerLeaveChannelEvent.LeaveReason reason);

    /**
     * 触发消息过滤事件
     * Fire message filter event
     */
    CompletableFuture<MessageFilterEvent> fireMessageFilterEvent(@NotNull OnlineUser sender, @NotNull String originalMessage, @NotNull String filteredMessage, @NotNull MessageFilterEvent.FilterType filterType, @NotNull String filterName);

    /**
     * 触发聊天命令事件
     * Fire chat command event
     */
    CompletableFuture<ChatCommandEvent> fireChatCommandEvent(@NotNull OnlineUser player, @NotNull String command, @NotNull String[] args, @NotNull ChatCommandEvent.CommandType commandType, @NotNull ChatCommandEvent.ExecutionPhase phase);

    /**
     * 触发玩家生命值变化事件
     * Fire player health change event
     */
    CompletableFuture<PlayerHealthChangeEvent> firePlayerHealthChangeEvent(@NotNull OnlineUser player, double previousHealth, double newHealth, double maxHealth, @NotNull PlayerHealthChangeEvent.HealthChangeReason reason, @Nullable String damager);

    /**
     * 触发玩家位置变化事件
     * Fire player location change event
     */
    CompletableFuture<PlayerLocationChangeEvent> firePlayerLocationChangeEvent(@NotNull OnlineUser player, @Nullable PlayerLocationChangeEvent.PlayerLocation previousLocation, @NotNull PlayerLocationChangeEvent.PlayerLocation newLocation, @NotNull PlayerLocationChangeEvent.MovementReason reason);

    /**
     * 触发玩家状态变化事件
     * Fire player status change event
     */
    CompletableFuture<PlayerStatusChangeEvent> firePlayerStatusChangeEvent(@NotNull OnlineUser player, @NotNull PlayerStatusChangeEvent.StatusType statusType, @Nullable Object previousValue, @NotNull Object newValue, @NotNull String reason, long duration);

    /**
     * 触发玩家死亡事件
     * Fire player death event
     */
    CompletableFuture<PlayerDeathEvent> firePlayerDeathEvent(@NotNull OnlineUser player, @NotNull String deathMessage, @NotNull PlayerDeathEvent.DeathCause deathCause, @Nullable OnlineUser killer, @NotNull PlayerLocationChangeEvent.PlayerLocation deathLocation);

    /**
     * 触发玩家重生事件
     * Fire player respawn event
     */
    CompletableFuture<PlayerRespawnEvent> firePlayerRespawnEvent(@NotNull OnlineUser player, @NotNull PlayerLocationChangeEvent.PlayerLocation respawnLocation, @NotNull PlayerRespawnEvent.RespawnReason reason);

}

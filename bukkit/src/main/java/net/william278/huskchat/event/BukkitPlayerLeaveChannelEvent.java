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
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit玩家离开频道事件实现
 * Bukkit player leave channel event implementation
 */
public class BukkitPlayerLeaveChannelEvent extends BukkitEvent implements PlayerLeaveChannelEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String channelId;
    private final LeaveReason reason;

    public BukkitPlayerLeaveChannelEvent(@NotNull OnlineUser player, @NotNull String channelId, @NotNull LeaveReason reason) {
        super(player);
        this.channelId = channelId;
        this.reason = reason;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    @NotNull
    @Override
    public OnlineUser getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public String getChannelId() {
        return channelId;
    }

    @NotNull
    @Override
    public LeaveReason getReason() {
        return reason;
    }
}

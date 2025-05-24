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
import org.jetbrains.annotations.Nullable;

/**
 * Bukkit频道切换事件实现
 * Bukkit channel switch event implementation
 */
public class BukkitChannelSwitchEvent extends BukkitEvent implements ChannelSwitchEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String previousChannelId;
    private String newChannelId;
    private final SwitchReason reason;

    public BukkitChannelSwitchEvent(@NotNull OnlineUser player, @Nullable String previousChannelId, 
                                   @NotNull String newChannelId, @NotNull SwitchReason reason) {
        super(player);
        this.previousChannelId = previousChannelId;
        this.newChannelId = newChannelId;
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

    @Nullable
    @Override
    public String getPreviousChannelId() {
        return previousChannelId;
    }

    @NotNull
    @Override
    public String getNewChannelId() {
        return newChannelId;
    }

    @Override
    public void setNewChannelId(@NotNull String channelId) {
        this.newChannelId = channelId;
    }

    @NotNull
    @Override
    public SwitchReason getReason() {
        return reason;
    }
}

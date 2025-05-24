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
 * Bukkit消息过滤事件实现
 * Bukkit message filter event implementation
 */
public class BukkitMessageFilterEvent extends BukkitEvent implements MessageFilterEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String originalMessage;
    private String filteredMessage;
    private final FilterType filterType;
    private final String filterName;
    private String filterReason;
    private boolean blocked;

    public BukkitMessageFilterEvent(@NotNull OnlineUser sender, @NotNull String originalMessage, 
                                   @NotNull String filteredMessage, @NotNull FilterType filterType, 
                                   @NotNull String filterName) {
        super(sender);
        this.originalMessage = originalMessage;
        this.filteredMessage = filteredMessage;
        this.filterType = filterType;
        this.filterName = filterName;
        this.blocked = false;
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
    public OnlineUser getSender() {
        return player;
    }

    @NotNull
    @Override
    public String getOriginalMessage() {
        return originalMessage;
    }

    @NotNull
    @Override
    public String getFilteredMessage() {
        return filteredMessage;
    }

    @Override
    public void setFilteredMessage(@NotNull String message) {
        this.filteredMessage = message;
    }

    @NotNull
    @Override
    public FilterType getFilterType() {
        return filterType;
    }

    @NotNull
    @Override
    public String getFilterName() {
        return filterName;
    }

    @Nullable
    @Override
    public String getFilterReason() {
        return filterReason;
    }

    @Override
    public void setFilterReason(@Nullable String reason) {
        this.filterReason = reason;
    }

    @Override
    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}

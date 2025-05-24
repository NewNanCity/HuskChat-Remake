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
 * Bukkit玩家位置变化事件实现
 * Bukkit player location change event implementation
 */
public class BukkitPlayerLocationChangeEvent extends BukkitEvent implements PlayerLocationChangeEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final PlayerLocation previousLocation;
    private final PlayerLocation newLocation;
    private final MovementReason reason;

    public BukkitPlayerLocationChangeEvent(@NotNull OnlineUser player, 
                                          @Nullable PlayerLocation previousLocation,
                                          @NotNull PlayerLocation newLocation, 
                                          @NotNull MovementReason reason) {
        super(player);
        this.previousLocation = previousLocation;
        this.newLocation = newLocation;
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
    public PlayerLocation getPreviousLocation() {
        return previousLocation;
    }

    @NotNull
    @Override
    public PlayerLocation getNewLocation() {
        return newLocation;
    }

    @NotNull
    @Override
    public MovementReason getReason() {
        return reason;
    }
}

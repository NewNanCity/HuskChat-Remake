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
 * Bukkit玩家死亡事件实现
 * Bukkit player death event implementation
 */
public class BukkitPlayerDeathEvent extends BukkitEvent implements PlayerDeathEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private String deathMessage;
    private final DeathCause deathCause;
    private final OnlineUser killer;
    private final PlayerLocationChangeEvent.PlayerLocation deathLocation;
    private boolean sendDeathMessage = true;
    private String deathMessageChannel;

    public BukkitPlayerDeathEvent(@NotNull OnlineUser player, @NotNull String deathMessage,
                                 @NotNull DeathCause deathCause, @Nullable OnlineUser killer,
                                 @NotNull PlayerLocationChangeEvent.PlayerLocation deathLocation) {
        super(player);
        this.deathMessage = deathMessage;
        this.deathCause = deathCause;
        this.killer = killer;
        this.deathLocation = deathLocation;
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
    public String getDeathMessage() {
        return deathMessage;
    }

    @Override
    public void setDeathMessage(@NotNull String message) {
        this.deathMessage = message;
    }

    @NotNull
    @Override
    public DeathCause getDeathCause() {
        return deathCause;
    }

    @Nullable
    @Override
    public OnlineUser getKiller() {
        return killer;
    }

    @NotNull
    @Override
    public PlayerLocationChangeEvent.PlayerLocation getDeathLocation() {
        return deathLocation;
    }

    @Override
    public boolean shouldSendDeathMessage() {
        return sendDeathMessage;
    }

    @Override
    public void setSendDeathMessage(boolean send) {
        this.sendDeathMessage = send;
    }

    @Nullable
    @Override
    public String getDeathMessageChannel() {
        return deathMessageChannel;
    }

    @Override
    public void setDeathMessageChannel(@Nullable String channelId) {
        this.deathMessageChannel = channelId;
    }
}

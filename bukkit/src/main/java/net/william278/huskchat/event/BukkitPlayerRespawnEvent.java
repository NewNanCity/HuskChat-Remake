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
 * Bukkit玩家重生事件实现
 * Bukkit player respawn event implementation
 */
public class BukkitPlayerRespawnEvent extends BukkitEvent implements PlayerRespawnEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private PlayerLocationChangeEvent.PlayerLocation respawnLocation;
    private final RespawnReason reason;
    private boolean sendRespawnMessage = false;
    private String respawnMessageChannel;

    public BukkitPlayerRespawnEvent(@NotNull OnlineUser player,
                                   @NotNull PlayerLocationChangeEvent.PlayerLocation respawnLocation,
                                   @NotNull RespawnReason reason) {
        super(player);
        this.respawnLocation = respawnLocation;
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
    public PlayerLocationChangeEvent.PlayerLocation getRespawnLocation() {
        return respawnLocation;
    }

    @Override
    public void setRespawnLocation(@NotNull PlayerLocationChangeEvent.PlayerLocation location) {
        this.respawnLocation = location;
    }

    @NotNull
    @Override
    public RespawnReason getReason() {
        return reason;
    }

    @Override
    public boolean shouldSendRespawnMessage() {
        return sendRespawnMessage;
    }

    @Override
    public void setSendRespawnMessage(boolean send) {
        this.sendRespawnMessage = send;
    }

    @Nullable
    @Override
    public String getRespawnMessageChannel() {
        return respawnMessageChannel;
    }

    @Override
    public void setRespawnMessageChannel(@Nullable String channelId) {
        this.respawnMessageChannel = channelId;
    }
}

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
 * Bukkit玩家生命值变化事件实现
 * Bukkit player health change event implementation
 */
public class BukkitPlayerHealthChangeEvent extends BukkitEvent implements PlayerHealthChangeEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final double previousHealth;
    private final double newHealth;
    private final double maxHealth;
    private final HealthChangeReason reason;
    private final String damager;

    public BukkitPlayerHealthChangeEvent(@NotNull OnlineUser player, double previousHealth, 
                                        double newHealth, double maxHealth, 
                                        @NotNull HealthChangeReason reason, @Nullable String damager) {
        super(player);
        this.previousHealth = previousHealth;
        this.newHealth = newHealth;
        this.maxHealth = maxHealth;
        this.reason = reason;
        this.damager = damager;
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

    @Override
    public double getPreviousHealth() {
        return previousHealth;
    }

    @Override
    public double getNewHealth() {
        return newHealth;
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
    }

    @NotNull
    @Override
    public HealthChangeReason getReason() {
        return reason;
    }

    @Nullable
    @Override
    public String getDamager() {
        return damager;
    }
}

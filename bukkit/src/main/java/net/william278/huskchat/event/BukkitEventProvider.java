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

import net.william278.huskchat.BukkitHuskChat;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Bukkit事件提供者接口 - 为Bukkit平台实现事件触发
 * Bukkit event provider interface - implements event triggering for Bukkit platform
 */
public interface BukkitEventProvider extends EventProvider {

    @Override
    default CompletableFuture<ChatMessageEvent> fireChatMessageEvent(@NotNull OnlineUser player,
                                                                    @NotNull String message,
                                                                    @NotNull String channelId) {
        final CompletableFuture<ChatMessageEvent> completableFuture = new CompletableFuture<>();
        final BukkitChatMessageEvent event = new BukkitChatMessageEvent(player, message, channelId);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PrivateMessageEvent> firePrivateMessageEvent(@NotNull OnlineUser sender,
                                                                          @NotNull List<OnlineUser> receivers,
                                                                          @NotNull String message) {
        final CompletableFuture<PrivateMessageEvent> completableFuture = new CompletableFuture<>();
        final BukkitPrivateMessageEvent event = new BukkitPrivateMessageEvent(sender, receivers, message);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<BroadcastMessageEvent> fireBroadcastMessageEvent(@NotNull OnlineUser sender,
                                                                              @NotNull String message) {
        final CompletableFuture<BroadcastMessageEvent> completableFuture = new CompletableFuture<>();
        final BukkitBroadcastMessageEvent event = new BukkitBroadcastMessageEvent(sender, message);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<ChannelSwitchEvent> fireChannelSwitchEvent(@NotNull OnlineUser player,
                                                                         @Nullable String previousChannelId,
                                                                         @NotNull String newChannelId,
                                                                         @NotNull ChannelSwitchEvent.SwitchReason reason) {
        final CompletableFuture<ChannelSwitchEvent> completableFuture = new CompletableFuture<>();
        final BukkitChannelSwitchEvent event = new BukkitChannelSwitchEvent(player, previousChannelId, newChannelId, reason);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PlayerJoinChannelEvent> firePlayerJoinChannelEvent(@NotNull OnlineUser player,
                                                                                 @NotNull String channelId,
                                                                                 @NotNull PlayerJoinChannelEvent.JoinReason reason) {
        final CompletableFuture<PlayerJoinChannelEvent> completableFuture = new CompletableFuture<>();
        final BukkitPlayerJoinChannelEvent event = new BukkitPlayerJoinChannelEvent(player, channelId, reason);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PlayerLeaveChannelEvent> firePlayerLeaveChannelEvent(@NotNull OnlineUser player,
                                                                                   @NotNull String channelId,
                                                                                   @NotNull PlayerLeaveChannelEvent.LeaveReason reason) {
        final CompletableFuture<PlayerLeaveChannelEvent> completableFuture = new CompletableFuture<>();
        final BukkitPlayerLeaveChannelEvent event = new BukkitPlayerLeaveChannelEvent(player, channelId, reason);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<MessageFilterEvent> fireMessageFilterEvent(@NotNull OnlineUser sender,
                                                                         @NotNull String originalMessage,
                                                                         @NotNull String filteredMessage,
                                                                         @NotNull MessageFilterEvent.FilterType filterType,
                                                                         @NotNull String filterName) {
        final CompletableFuture<MessageFilterEvent> completableFuture = new CompletableFuture<>();
        final BukkitMessageFilterEvent event = new BukkitMessageFilterEvent(sender, originalMessage, filteredMessage, filterType, filterName);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<ChatCommandEvent> fireChatCommandEvent(@NotNull OnlineUser player,
                                                                     @NotNull String command,
                                                                     @NotNull String[] args,
                                                                     @NotNull ChatCommandEvent.CommandType commandType,
                                                                     @NotNull ChatCommandEvent.ExecutionPhase phase) {
        final CompletableFuture<ChatCommandEvent> completableFuture = new CompletableFuture<>();
        final BukkitChatCommandEvent event = new BukkitChatCommandEvent(player, command, args, commandType, phase);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PlayerHealthChangeEvent> firePlayerHealthChangeEvent(@NotNull OnlineUser player,
                                                                                  double previousHealth,
                                                                                  double newHealth,
                                                                                  double maxHealth,
                                                                                  @NotNull PlayerHealthChangeEvent.HealthChangeReason reason,
                                                                                  @Nullable String damager) {
        final CompletableFuture<PlayerHealthChangeEvent> completableFuture = new CompletableFuture<>();
        final BukkitPlayerHealthChangeEvent event = new BukkitPlayerHealthChangeEvent(player, previousHealth, newHealth, maxHealth, reason, damager);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PlayerLocationChangeEvent> firePlayerLocationChangeEvent(@NotNull OnlineUser player,
                                                                                       @Nullable PlayerLocationChangeEvent.PlayerLocation previousLocation,
                                                                                       @NotNull PlayerLocationChangeEvent.PlayerLocation newLocation,
                                                                                       @NotNull PlayerLocationChangeEvent.MovementReason reason) {
        final CompletableFuture<PlayerLocationChangeEvent> completableFuture = new CompletableFuture<>();
        final BukkitPlayerLocationChangeEvent event = new BukkitPlayerLocationChangeEvent(player, previousLocation, newLocation, reason);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PlayerStatusChangeEvent> firePlayerStatusChangeEvent(@NotNull OnlineUser player,
                                                                                   @NotNull PlayerStatusChangeEvent.StatusType statusType,
                                                                                   @Nullable Object previousValue,
                                                                                   @NotNull Object newValue,
                                                                                   @NotNull String reason,
                                                                                   long duration) {
        final CompletableFuture<PlayerStatusChangeEvent> completableFuture = new CompletableFuture<>();
        final BukkitPlayerStatusChangeEvent event = new BukkitPlayerStatusChangeEvent(player, statusType, previousValue, newValue, reason, duration);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PlayerDeathEvent> firePlayerDeathEvent(@NotNull OnlineUser player,
                                                                     @NotNull String deathMessage,
                                                                     @NotNull PlayerDeathEvent.DeathCause deathCause,
                                                                     @Nullable OnlineUser killer,
                                                                     @NotNull PlayerLocationChangeEvent.PlayerLocation deathLocation) {
        final CompletableFuture<PlayerDeathEvent> completableFuture = new CompletableFuture<>();
        final BukkitPlayerDeathEvent event = new BukkitPlayerDeathEvent(player, deathMessage, deathCause, killer, deathLocation);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PlayerRespawnEvent> firePlayerRespawnEvent(@NotNull OnlineUser player,
                                                                         @NotNull PlayerLocationChangeEvent.PlayerLocation respawnLocation,
                                                                         @NotNull PlayerRespawnEvent.RespawnReason reason) {
        final CompletableFuture<PlayerRespawnEvent> completableFuture = new CompletableFuture<>();
        final BukkitPlayerRespawnEvent event = new BukkitPlayerRespawnEvent(player, respawnLocation, reason);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    BukkitHuskChat getPlugin();

}

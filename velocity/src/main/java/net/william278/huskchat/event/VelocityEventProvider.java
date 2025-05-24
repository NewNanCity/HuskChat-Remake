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

import com.velocitypowered.api.proxy.ProxyServer;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VelocityEventProvider extends EventProvider {

    @Override
    default CompletableFuture<ChatMessageEvent> fireChatMessageEvent(@NotNull OnlineUser player,
                                                                     @NotNull String message,
                                                                     @NotNull String channelId) {
        return getProxyServer().getEventManager().fire(new VelocityChatMessageEvent(player, message, channelId));
    }

    @Override
    default CompletableFuture<PrivateMessageEvent> firePrivateMessageEvent(@NotNull OnlineUser sender,
                                                                           @NotNull List<OnlineUser> receivers,
                                                                           @NotNull String message) {
        return getProxyServer().getEventManager().fire(new VelocityPrivateMessageEvent(sender, receivers, message));
    }

    @Override
    default CompletableFuture<BroadcastMessageEvent> fireBroadcastMessageEvent(@NotNull OnlineUser sender,
                                                                               @NotNull String message) {
        return getProxyServer().getEventManager().fire(new VelocityBroadcastMessageEvent(sender, message));
    }

    // 暂时使用简化实现，返回已完成的 CompletableFuture
    // 在代理服务器环境中，这些事件主要用于跨服务器通信

    @Override
    default CompletableFuture<ChannelSwitchEvent> fireChannelSwitchEvent(@NotNull OnlineUser player,
                                                                         @org.jetbrains.annotations.Nullable String previousChannelId,
                                                                         @NotNull String newChannelId,
                                                                         @NotNull ChannelSwitchEvent.SwitchReason reason) {
        // 简化实现：直接返回一个基本的事件对象
        return CompletableFuture.completedFuture(new ChannelSwitchEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public @org.jetbrains.annotations.Nullable String getPreviousChannelId() { return previousChannelId; }
            @Override public @NotNull String getNewChannelId() { return newChannelId; }
            @Override public void setNewChannelId(@NotNull String channelId) {}
            @Override public @NotNull SwitchReason getReason() { return reason; }
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<PlayerJoinChannelEvent> firePlayerJoinChannelEvent(@NotNull OnlineUser player,
                                                                                 @NotNull String channelId,
                                                                                 @NotNull PlayerJoinChannelEvent.JoinReason reason) {
        return CompletableFuture.completedFuture(new PlayerJoinChannelEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public @NotNull String getChannelId() { return channelId; }
            @Override public @NotNull JoinReason getReason() { return reason; }
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<PlayerLeaveChannelEvent> firePlayerLeaveChannelEvent(@NotNull OnlineUser player,
                                                                                   @NotNull String channelId,
                                                                                   @NotNull PlayerLeaveChannelEvent.LeaveReason reason) {
        return CompletableFuture.completedFuture(new PlayerLeaveChannelEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public @NotNull String getChannelId() { return channelId; }
            @Override public @NotNull LeaveReason getReason() { return reason; }
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<MessageFilterEvent> fireMessageFilterEvent(@NotNull OnlineUser sender,
                                                                         @NotNull String originalMessage,
                                                                         @NotNull String filteredMessage,
                                                                         @NotNull MessageFilterEvent.FilterType filterType,
                                                                         @NotNull String filterName) {
        return CompletableFuture.completedFuture(new MessageFilterEvent() {
            @Override public @NotNull OnlineUser getSender() { return sender; }
            @Override public @NotNull String getOriginalMessage() { return originalMessage; }
            @Override public @NotNull String getFilteredMessage() { return filteredMessage; }
            @Override public void setFilteredMessage(@NotNull String message) {}
            @Override public @NotNull FilterType getFilterType() { return filterType; }
            @Override public @NotNull String getFilterName() { return filterName; }
            @Override public boolean isBlocked() { return false; }
            @Override public void setBlocked(boolean blocked) {}
            @Override public @org.jetbrains.annotations.Nullable String getFilterReason() { return null; }
            @Override public void setFilterReason(@org.jetbrains.annotations.Nullable String reason) {}
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<ChatCommandEvent> fireChatCommandEvent(@NotNull OnlineUser player,
                                                                    @NotNull String command,
                                                                    @NotNull String[] args,
                                                                    @NotNull ChatCommandEvent.CommandType commandType,
                                                                    @NotNull ChatCommandEvent.ExecutionPhase phase) {
        return CompletableFuture.completedFuture(new ChatCommandEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public @NotNull String getCommand() { return command; }
            @Override public @NotNull String[] getArgs() { return args; }
            @Override public void setArgs(@NotNull String[] newArgs) {}
            @Override public @NotNull CommandType getCommandType() { return commandType; }
            @Override public @NotNull ExecutionPhase getPhase() { return phase; }
            @Override public boolean isSuccessful() { return true; }
            @Override public void setSuccessful(boolean successful) {}
            @Override public @org.jetbrains.annotations.Nullable String getFailureReason() { return null; }
            @Override public void setFailureReason(@org.jetbrains.annotations.Nullable String reason) {}
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<PlayerHealthChangeEvent> firePlayerHealthChangeEvent(@NotNull OnlineUser player,
                                                                                   double previousHealth,
                                                                                   double newHealth,
                                                                                   double maxHealth,
                                                                                   @NotNull PlayerHealthChangeEvent.HealthChangeReason reason,
                                                                                   @org.jetbrains.annotations.Nullable String damager) {
        return CompletableFuture.completedFuture(new PlayerHealthChangeEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public double getPreviousHealth() { return previousHealth; }
            @Override public double getNewHealth() { return newHealth; }
            @Override public double getMaxHealth() { return maxHealth; }
            @Override public @NotNull HealthChangeReason getReason() { return reason; }
            @Override public @org.jetbrains.annotations.Nullable String getDamager() { return damager; }
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<PlayerLocationChangeEvent> firePlayerLocationChangeEvent(@NotNull OnlineUser player,
                                                                                       @org.jetbrains.annotations.Nullable PlayerLocationChangeEvent.PlayerLocation previousLocation,
                                                                                       @NotNull PlayerLocationChangeEvent.PlayerLocation newLocation,
                                                                                       @NotNull PlayerLocationChangeEvent.MovementReason reason) {
        return CompletableFuture.completedFuture(new PlayerLocationChangeEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public @org.jetbrains.annotations.Nullable PlayerLocation getPreviousLocation() { return previousLocation; }
            @Override public @NotNull PlayerLocation getNewLocation() { return newLocation; }
            @Override public @NotNull MovementReason getReason() { return reason; }
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<PlayerStatusChangeEvent> firePlayerStatusChangeEvent(@NotNull OnlineUser player,
                                                                                   @NotNull PlayerStatusChangeEvent.StatusType statusType,
                                                                                   @org.jetbrains.annotations.Nullable Object previousValue,
                                                                                   @NotNull Object newValue,
                                                                                   @NotNull String reason,
                                                                                   long duration) {
        return CompletableFuture.completedFuture(new PlayerStatusChangeEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public @NotNull StatusType getStatusType() { return statusType; }
            @Override public @org.jetbrains.annotations.Nullable Object getPreviousValue() { return previousValue; }
            @Override public @NotNull Object getNewValue() { return newValue; }
            @Override public @NotNull String getReason() { return reason; }
            @Override public long getDuration() { return duration; }
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<PlayerDeathEvent> firePlayerDeathEvent(@NotNull OnlineUser player,
                                                                    @NotNull String deathMessage,
                                                                    @NotNull PlayerDeathEvent.DeathCause deathCause,
                                                                    @org.jetbrains.annotations.Nullable OnlineUser killer,
                                                                    @NotNull PlayerLocationChangeEvent.PlayerLocation deathLocation) {
        return CompletableFuture.completedFuture(new PlayerDeathEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public @NotNull String getDeathMessage() { return deathMessage; }
            @Override public void setDeathMessage(@NotNull String message) {}
            @Override public @NotNull DeathCause getDeathCause() { return deathCause; }
            @Override public @org.jetbrains.annotations.Nullable OnlineUser getKiller() { return killer; }
            @Override public @NotNull PlayerLocationChangeEvent.PlayerLocation getDeathLocation() { return deathLocation; }
            @Override public boolean shouldSendDeathMessage() { return true; }
            @Override public void setSendDeathMessage(boolean send) {}
            @Override public @org.jetbrains.annotations.Nullable String getDeathMessageChannel() { return null; }
            @Override public void setDeathMessageChannel(@org.jetbrains.annotations.Nullable String channelId) {}
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @Override
    default CompletableFuture<PlayerRespawnEvent> firePlayerRespawnEvent(@NotNull OnlineUser player,
                                                                         @NotNull PlayerLocationChangeEvent.PlayerLocation respawnLocation,
                                                                         @NotNull PlayerRespawnEvent.RespawnReason reason) {
        return CompletableFuture.completedFuture(new PlayerRespawnEvent() {
            @Override public @NotNull OnlineUser getPlayer() { return player; }
            @Override public @NotNull PlayerLocationChangeEvent.PlayerLocation getRespawnLocation() { return respawnLocation; }
            @Override public void setRespawnLocation(@NotNull PlayerLocationChangeEvent.PlayerLocation location) {}
            @Override public @NotNull RespawnReason getReason() { return reason; }
            @Override public boolean shouldSendRespawnMessage() { return false; }
            @Override public void setSendRespawnMessage(boolean send) {}
            @Override public @org.jetbrains.annotations.Nullable String getRespawnMessageChannel() { return null; }
            @Override public void setRespawnMessageChannel(@org.jetbrains.annotations.Nullable String channelId) {}
            @Override public boolean isCancelled() { return false; }
            @Override public void setCancelled(boolean cancelled) {}
        });
    }

    @NotNull
    ProxyServer getProxyServer();

}

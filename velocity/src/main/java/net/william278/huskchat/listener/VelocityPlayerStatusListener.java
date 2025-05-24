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

package net.william278.huskchat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.william278.huskchat.VelocityHuskChat;
import net.william278.huskchat.api.VelocityHuskChatExtendedAPI;
import net.william278.huskchat.network.PlayerStatusMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Velocity平台的玩家状态监听器
 * Velocity platform player status listener
 */
public class VelocityPlayerStatusListener {

    private final VelocityHuskChat plugin;
    private final VelocityHuskChatExtendedAPI extendedAPI;

    public VelocityPlayerStatusListener(@NotNull VelocityHuskChat plugin, @NotNull VelocityHuskChatExtendedAPI extendedAPI) {
        this.plugin = plugin;
        this.extendedAPI = extendedAPI;
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().getId().equals("huskchat:player_status")) {
            return;
        }

        // 确保消息来自后端服务器
        if (!(event.getSource() instanceof ServerConnection serverConnection)) {
            return;
        }

        try {
            String json = new String(event.getData());
            PlayerStatusMessage message = plugin.getGson().fromJson(json, PlayerStatusMessage.class);
            
            // 处理来自后端服务器的消息
            extendedAPI.handlePlayerStatusMessage(serverConnection.getServerInfo().getName(), message);
            
        } catch (Exception e) {
            plugin.log(java.util.logging.Level.WARNING, "Failed to handle plugin message from " + 
                serverConnection.getServerInfo().getName() + ": " + e.getMessage());
        }
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        
        // 玩家连接到新服务器时，请求状态同步
        plugin.getScheduler().buildTask(plugin, () -> {
            try {
                net.william278.huskchat.user.OnlineUser huskPlayer = 
                    net.william278.huskchat.user.VelocityUser.adapt(player, plugin);
                extendedAPI.requestPlayerStatusSync(huskPlayer);
            } catch (Exception e) {
                plugin.log(java.util.logging.Level.WARNING, "Failed to request status sync for " + 
                    player.getUsername() + ": " + e.getMessage());
            }
        }).delay(java.time.Duration.ofSeconds(1)).schedule(); // 延迟1秒确保连接稳定
    }
}

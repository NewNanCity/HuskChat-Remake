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

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.william278.huskchat.BungeeHuskChat;
import net.william278.huskchat.api.BungeeHuskChatExtendedAPI;
import net.william278.huskchat.network.PlayerStatusMessage;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * BungeeCord平台的玩家状态监听器
 * BungeeCord platform player status listener
 */
public class BungeePlayerStatusListener implements Listener {

    private final BungeeHuskChat plugin;
    private final BungeeHuskChatExtendedAPI extendedAPI;

    public BungeePlayerStatusListener(@NotNull BungeeHuskChat plugin, @NotNull BungeeHuskChatExtendedAPI extendedAPI) {
        this.plugin = plugin;
        this.extendedAPI = extendedAPI;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("huskchat:player_status")) {
            return;
        }

        // 确保消息来自后端服务器
        if (!(event.getSender() instanceof Server server)) {
            return;
        }

        try {
            String json = new String(event.getData());
            PlayerStatusMessage message = plugin.getGson().fromJson(json, PlayerStatusMessage.class);
            
            // 处理来自后端服务器的消息
            extendedAPI.handlePlayerStatusMessage(server.getInfo().getName(), message);
            
        } catch (Exception e) {
            plugin.log(java.util.logging.Level.WARNING, "Failed to handle plugin message from " + 
                server.getInfo().getName() + ": " + e.getMessage());
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        
        // 玩家连接到新服务器时，请求状态同步
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            try {
                net.william278.huskchat.user.OnlineUser huskPlayer = 
                    net.william278.huskchat.user.BungeeUser.adapt(player, plugin);
                extendedAPI.requestPlayerStatusSync(huskPlayer);
            } catch (Exception e) {
                plugin.log(java.util.logging.Level.WARNING, "Failed to request status sync for " + 
                    player.getName() + ": " + e.getMessage());
            }
        }, 1, TimeUnit.SECONDS); // 延迟1秒确保连接稳定
    }
}

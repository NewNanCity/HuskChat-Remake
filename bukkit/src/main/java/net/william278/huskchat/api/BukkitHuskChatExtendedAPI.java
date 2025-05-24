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

package net.william278.huskchat.api;

import net.william278.huskchat.BukkitHuskChat;
import net.william278.huskchat.event.*;
import net.william278.huskchat.user.BukkitUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Bukkit平台的HuskChat扩展API实现
 * Bukkit platform implementation of HuskChat Extended API
 */
@SuppressWarnings("unused")
public class BukkitHuskChatExtendedAPI extends HuskChatExtendedAPI {

    private final List<ChatMessageEventListener> chatMessageListeners = new ArrayList<>();
    private final List<ChannelSwitchEventListener> channelSwitchListeners = new ArrayList<>();
    private final List<PrivateMessageEventListener> privateMessageListeners = new ArrayList<>();
    private final List<MessageFilterEventListener> messageFilterListeners = new ArrayList<>();

    private BukkitHuskChatExtendedAPI(@NotNull BukkitHuskChat plugin) {
        super(plugin);
        // 注册内部事件监听器
        plugin.getServer().getPluginManager().registerEvents(new InternalEventListener(), plugin);
    }

    /**
     * 获取Bukkit扩展API实例
     * Get the Bukkit extended API instance
     *
     * @return Bukkit扩展API实例 / Bukkit extended API instance
     */
    @NotNull
    public static BukkitHuskChatExtendedAPI getInstance() {
        return (BukkitHuskChatExtendedAPI) HuskChatExtendedAPI.getInstance();
    }

    /**
     * 注册扩展API
     * Register the extended API
     *
     * @param plugin 插件实例 / plugin instance
     */
    public static void register(@NotNull BukkitHuskChat plugin) {
        HuskChatAPI.instance = new BukkitHuskChatExtendedAPI(plugin);
    }

    /**
     * 适配Bukkit玩家对象
     * Adapt Bukkit player object
     *
     * @param player Bukkit玩家对象 / Bukkit player object
     * @return 跨平台玩家对象 / cross-platform player object
     */
    @NotNull
    public BukkitUser adaptPlayer(@NotNull Player player) {
        return BukkitUser.adapt(player, (BukkitHuskChat) plugin);
    }

    // ========== 事件监听器注册实现 / Event Listener Registration Implementation ==========

    @Override
    public void registerChatMessageListener(@NotNull ChatMessageEventListener listener) {
        chatMessageListeners.add(listener);
    }

    @Override
    public void registerChannelSwitchListener(@NotNull ChannelSwitchEventListener listener) {
        channelSwitchListeners.add(listener);
    }

    /**
     * 注册私聊消息事件监听器
     * Register private message event listener
     *
     * @param listener 监听器 / listener
     */
    public void registerPrivateMessageListener(@NotNull PrivateMessageEventListener listener) {
        privateMessageListeners.add(listener);
    }

    /**
     * 注册消息过滤事件监听器
     * Register message filter event listener
     *
     * @param listener 监听器 / listener
     */
    public void registerMessageFilterListener(@NotNull MessageFilterEventListener listener) {
        messageFilterListeners.add(listener);
    }

    /**
     * 移除聊天消息事件监听器
     * Remove chat message event listener
     *
     * @param listener 监听器 / listener
     */
    public void unregisterChatMessageListener(@NotNull ChatMessageEventListener listener) {
        chatMessageListeners.remove(listener);
    }

    /**
     * 移除频道切换事件监听器
     * Remove channel switch event listener
     *
     * @param listener 监听器 / listener
     */
    public void unregisterChannelSwitchListener(@NotNull ChannelSwitchEventListener listener) {
        channelSwitchListeners.remove(listener);
    }

    /**
     * 移除私聊消息事件监听器
     * Remove private message event listener
     *
     * @param listener 监听器 / listener
     */
    public void unregisterPrivateMessageListener(@NotNull PrivateMessageEventListener listener) {
        privateMessageListeners.remove(listener);
    }

    /**
     * 移除消息过滤事件监听器
     * Remove message filter event listener
     *
     * @param listener 监听器 / listener
     */
    public void unregisterMessageFilterListener(@NotNull MessageFilterEventListener listener) {
        messageFilterListeners.remove(listener);
    }

    // ========== 内部事件监听器 / Internal Event Listener ==========

    /**
     * 内部事件监听器 - 将Bukkit事件转发给注册的监听器
     * Internal event listener - forwards Bukkit events to registered listeners
     */
    private class InternalEventListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChatMessage(@NotNull BukkitChatMessageEvent event) {
            chatMessageListeners.forEach(listener -> {
                try {
                    listener.onChatMessage(event);
                } catch (Exception e) {
                    plugin.log(java.util.logging.Level.WARNING, "Error in chat message listener", e);
                }
            });
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChannelSwitch(@NotNull BukkitChannelSwitchEvent event) {
            channelSwitchListeners.forEach(listener -> {
                try {
                    listener.onChannelSwitch(event);
                } catch (Exception e) {
                    plugin.log(java.util.logging.Level.WARNING, "Error in channel switch listener", e);
                }
            });
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPrivateMessage(@NotNull BukkitPrivateMessageEvent event) {
            privateMessageListeners.forEach(listener -> {
                try {
                    listener.onPrivateMessage(event);
                } catch (Exception e) {
                    plugin.log(java.util.logging.Level.WARNING, "Error in private message listener", e);
                }
            });
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onMessageFilter(@NotNull BukkitMessageFilterEvent event) {
            messageFilterListeners.forEach(listener -> {
                try {
                    listener.onMessageFilter(event);
                } catch (Exception e) {
                    plugin.log(java.util.logging.Level.WARNING, "Error in message filter listener", e);
                }
            });
        }
    }
}

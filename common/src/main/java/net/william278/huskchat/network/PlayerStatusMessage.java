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

package net.william278.huskchat.network;

import com.google.gson.annotations.SerializedName;
import net.william278.huskchat.event.PlayerStatusChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * 玩家状态消息 - 用于跨服务器传播玩家状态变化
 * Player status message - for cross-server player status change propagation
 */
public class PlayerStatusMessage {

    @SerializedName("message_type")
    private final MessageType messageType;

    @SerializedName("player_uuid")
    private final UUID playerUuid;

    @SerializedName("player_name")
    private final String playerName;

    @SerializedName("server_name")
    private final String serverName;

    @SerializedName("timestamp")
    private final long timestamp;

    @SerializedName("data")
    private final Map<String, Object> data;

    public PlayerStatusMessage(@NotNull MessageType messageType, @NotNull UUID playerUuid, 
                              @NotNull String playerName, @NotNull String serverName, 
                              @NotNull Map<String, Object> data) {
        this.messageType = messageType;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.serverName = serverName;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    @NotNull
    public MessageType getMessageType() {
        return messageType;
    }

    @NotNull
    public UUID getPlayerUuid() {
        return playerUuid;
    }

    @NotNull
    public String getPlayerName() {
        return playerName;
    }

    @NotNull
    public String getServerName() {
        return serverName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @NotNull
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 获取特定数据字段
     * Get specific data field
     *
     * @param key 键 / key
     * @return 值 / value
     */
    @Nullable
    public Object getData(@NotNull String key) {
        return data.get(key);
    }

    /**
     * 获取特定数据字段（带类型转换）
     * Get specific data field with type casting
     *
     * @param key 键 / key
     * @param type 类型 / type
     * @param <T> 泛型类型 / generic type
     * @return 值 / value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getData(@NotNull String key, @NotNull Class<T> type) {
        Object value = data.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }

    /**
     * 消息类型枚举
     * Message type enumeration
     */
    public enum MessageType {
        /**
         * 玩家状态更新
         * Player status update
         */
        @SerializedName("status_update")
        STATUS_UPDATE("status_update"),

        /**
         * 玩家生命值变化
         * Player health change
         */
        @SerializedName("health_change")
        HEALTH_CHANGE("health_change"),

        /**
         * 玩家位置变化
         * Player location change
         */
        @SerializedName("location_change")
        LOCATION_CHANGE("location_change"),

        /**
         * 玩家死亡
         * Player death
         */
        @SerializedName("player_death")
        PLAYER_DEATH("player_death"),

        /**
         * 玩家重生
         * Player respawn
         */
        @SerializedName("player_respawn")
        PLAYER_RESPAWN("player_respawn"),

        /**
         * 命令执行
         * Command execution
         */
        @SerializedName("command_execution")
        COMMAND_EXECUTION("command_execution"),

        /**
         * 状态同步请求
         * Status sync request
         */
        @SerializedName("sync_request")
        SYNC_REQUEST("sync_request"),

        /**
         * 状态同步响应
         * Status sync response
         */
        @SerializedName("sync_response")
        SYNC_RESPONSE("sync_response");

        private final String key;

        MessageType(@NotNull String key) {
            this.key = key;
        }

        @NotNull
        public String getKey() {
            return key;
        }

        @NotNull
        public static MessageType fromKey(@NotNull String key) {
            for (MessageType type : values()) {
                if (type.key.equals(key)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown message type: " + key);
        }
    }

    /**
     * 创建状态更新消息
     * Create status update message
     */
    @NotNull
    public static PlayerStatusMessage createStatusUpdate(@NotNull UUID playerUuid, @NotNull String playerName, 
                                                        @NotNull String serverName, @NotNull PlayerStatusChangeEvent.StatusType statusType, 
                                                        @NotNull Object value, @NotNull String reason) {
        Map<String, Object> data = Map.of(
            "status_type", statusType.getKey(),
            "value", value,
            "reason", reason
        );
        return new PlayerStatusMessage(MessageType.STATUS_UPDATE, playerUuid, playerName, serverName, data);
    }

    /**
     * 创建生命值变化消息
     * Create health change message
     */
    @NotNull
    public static PlayerStatusMessage createHealthChange(@NotNull UUID playerUuid, @NotNull String playerName, 
                                                        @NotNull String serverName, double previousHealth, 
                                                        double newHealth, double maxHealth, @NotNull String reason) {
        Map<String, Object> data = Map.of(
            "previous_health", previousHealth,
            "new_health", newHealth,
            "max_health", maxHealth,
            "reason", reason
        );
        return new PlayerStatusMessage(MessageType.HEALTH_CHANGE, playerUuid, playerName, serverName, data);
    }

    /**
     * 创建位置变化消息
     * Create location change message
     */
    @NotNull
    public static PlayerStatusMessage createLocationChange(@NotNull UUID playerUuid, @NotNull String playerName, 
                                                          @NotNull String serverName, @NotNull Map<String, Object> locationData) {
        return new PlayerStatusMessage(MessageType.LOCATION_CHANGE, playerUuid, playerName, serverName, locationData);
    }

    /**
     * 创建同步请求消息
     * Create sync request message
     */
    @NotNull
    public static PlayerStatusMessage createSyncRequest(@NotNull UUID playerUuid, @NotNull String playerName, 
                                                       @NotNull String serverName) {
        return new PlayerStatusMessage(MessageType.SYNC_REQUEST, playerUuid, playerName, serverName, Map.of());
    }

    /**
     * 创建同步响应消息
     * Create sync response message
     */
    @NotNull
    public static PlayerStatusMessage createSyncResponse(@NotNull UUID playerUuid, @NotNull String playerName, 
                                                        @NotNull String serverName, @NotNull Map<String, Object> allStatuses) {
        return new PlayerStatusMessage(MessageType.SYNC_RESPONSE, playerUuid, playerName, serverName, allStatuses);
    }

    @Override
    public String toString() {
        return String.format("PlayerStatusMessage{type=%s, player=%s(%s), server=%s, timestamp=%d, data=%s}",
                messageType, playerName, playerUuid, serverName, timestamp, data);
    }
}

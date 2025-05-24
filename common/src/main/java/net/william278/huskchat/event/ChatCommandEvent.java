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
import org.jetbrains.annotations.NotNull;

/**
 * 聊天命令事件 - 当玩家执行聊天相关命令时触发
 * Chat command event - triggered when a player executes chat-related commands
 */
public interface ChatCommandEvent extends EventBase {

    /**
     * 获取执行命令的玩家
     * Get the player executing the command
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getPlayer();

    /**
     * 获取命令名称
     * Get the command name
     *
     * @return 命令名称 / command name
     */
    @NotNull
    String getCommand();

    /**
     * 获取命令参数
     * Get the command arguments
     *
     * @return 命令参数数组 / command arguments array
     */
    @NotNull
    String[] getArgs();

    /**
     * 设置命令参数
     * Set the command arguments
     *
     * @param args 新的命令参数 / new command arguments
     */
    void setArgs(@NotNull String[] args);

    /**
     * 获取命令类型
     * Get the command type
     *
     * @return 命令类型 / command type
     */
    @NotNull
    CommandType getCommandType();

    /**
     * 获取执行阶段
     * Get the execution phase
     *
     * @return 执行阶段 / execution phase
     */
    @NotNull
    ExecutionPhase getPhase();

    /**
     * 获取失败原因（仅在POST阶段且执行失败时有值）
     * Get the failure reason (only available in POST phase when execution failed)
     *
     * @return 失败原因，成功时为null / failure reason, null if successful
     */
    String getFailureReason();

    /**
     * 设置失败原因
     * Set the failure reason
     *
     * @param reason 失败原因 / failure reason
     */
    void setFailureReason(String reason);

    /**
     * 检查命令是否执行成功（仅在POST阶段有意义）
     * Check if command executed successfully (only meaningful in POST phase)
     *
     * @return 是否成功 / whether successful
     */
    boolean isSuccessful();

    /**
     * 设置命令执行结果
     * Set the command execution result
     *
     * @param successful 是否成功 / whether successful
     */
    void setSuccessful(boolean successful);

    /**
     * 命令类型枚举
     * Command type enum
     */
    enum CommandType {
        /**
         * 频道切换命令 / Channel switch command
         */
        CHANNEL_SWITCH("/channel", "/c"),
        
        /**
         * 私聊命令 / Private message command
         */
        PRIVATE_MESSAGE("/msg", "/tell", "/whisper", "/w", "/m", "/pm"),
        
        /**
         * 回复命令 / Reply command
         */
        REPLY("/reply", "/r"),
        
        /**
         * 广播命令 / Broadcast command
         */
        BROADCAST("/broadcast", "/alert"),
        
        /**
         * 社交间谍命令 / Social spy command
         */
        SOCIAL_SPY("/socialspy", "/ss"),
        
        /**
         * 本地间谍命令 / Local spy command
         */
        LOCAL_SPY("/localspy", "/ls"),
        
        /**
         * 退出群聊命令 / Opt out message command
         */
        OPT_OUT_MESSAGE("/optoutmsg"),
        
        /**
         * 频道快捷命令 / Channel shortcut command
         */
        CHANNEL_SHORTCUT("shortcut"),
        
        /**
         * 其他聊天相关命令 / Other chat-related command
         */
        OTHER("other");

        private final String[] aliases;

        CommandType(String... aliases) {
            this.aliases = aliases;
        }

        /**
         * 获取命令别名
         * Get command aliases
         *
         * @return 命令别名数组 / command aliases array
         */
        @NotNull
        public String[] getAliases() {
            return aliases.clone();
        }

        /**
         * 根据命令名称获取命令类型
         * Get command type by command name
         *
         * @param command 命令名称 / command name
         * @return 命令类型 / command type
         */
        @NotNull
        public static CommandType fromCommand(@NotNull String command) {
            String normalizedCommand = command.toLowerCase();
            if (!normalizedCommand.startsWith("/")) {
                normalizedCommand = "/" + normalizedCommand;
            }
            
            for (CommandType type : values()) {
                for (String alias : type.aliases) {
                    if (alias.equals(normalizedCommand)) {
                        return type;
                    }
                }
            }
            return OTHER;
        }
    }

    /**
     * 命令执行阶段枚举
     * Command execution phase enum
     */
    enum ExecutionPhase {
        /**
         * 命令执行前 / Before command execution
         */
        PRE,
        
        /**
         * 命令执行后 / After command execution
         */
        POST
    }
}

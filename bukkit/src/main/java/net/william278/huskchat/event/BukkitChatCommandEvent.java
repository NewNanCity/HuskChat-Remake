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

/**
 * Bukkit聊天命令事件实现
 * Bukkit chat command event implementation
 */
public class BukkitChatCommandEvent extends BukkitEvent implements ChatCommandEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String command;
    private String[] args;
    private final CommandType commandType;
    private final ExecutionPhase phase;
    private String failureReason;
    private boolean successful = true;

    public BukkitChatCommandEvent(@NotNull OnlineUser player, @NotNull String command, 
                                 @NotNull String[] args, @NotNull CommandType commandType, 
                                 @NotNull ExecutionPhase phase) {
        super(player);
        this.command = command;
        this.args = args.clone();
        this.commandType = commandType;
        this.phase = phase;
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
    public String getCommand() {
        return command;
    }

    @NotNull
    @Override
    public String[] getArgs() {
        return args.clone();
    }

    @Override
    public void setArgs(@NotNull String[] args) {
        this.args = args.clone();
    }

    @NotNull
    @Override
    public CommandType getCommandType() {
        return commandType;
    }

    @NotNull
    @Override
    public ExecutionPhase getPhase() {
        return phase;
    }

    @Override
    public String getFailureReason() {
        return failureReason;
    }

    @Override
    public void setFailureReason(String reason) {
        this.failureReason = reason;
    }

    @Override
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}

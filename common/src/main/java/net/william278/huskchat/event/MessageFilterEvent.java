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
import org.jetbrains.annotations.Nullable;

/**
 * 消息过滤事件 - 在消息被过滤器处理时触发
 * Message filter event - triggered when a message is processed by filters
 */
public interface MessageFilterEvent extends EventBase {

    /**
     * 获取发送消息的玩家
     * Get the player sending the message
     *
     * @return 玩家对象 / player object
     */
    @NotNull
    OnlineUser getSender();

    /**
     * 获取原始消息内容
     * Get the original message content
     *
     * @return 原始消息 / original message
     */
    @NotNull
    String getOriginalMessage();

    /**
     * 获取过滤后的消息内容
     * Get the filtered message content
     *
     * @return 过滤后的消息 / filtered message
     */
    @NotNull
    String getFilteredMessage();

    /**
     * 设置过滤后的消息内容
     * Set the filtered message content
     *
     * @param message 过滤后的消息 / filtered message
     */
    void setFilteredMessage(@NotNull String message);

    /**
     * 获取过滤器类型
     * Get the filter type
     *
     * @return 过滤器类型 / filter type
     */
    @NotNull
    FilterType getFilterType();

    /**
     * 获取过滤器名称
     * Get the filter name
     *
     * @return 过滤器名称 / filter name
     */
    @NotNull
    String getFilterName();

    /**
     * 获取过滤原因
     * Get the filter reason
     *
     * @return 过滤原因，如果没有被过滤则为null / filter reason, null if not filtered
     */
    @Nullable
    String getFilterReason();

    /**
     * 设置过滤原因
     * Set the filter reason
     *
     * @param reason 过滤原因 / filter reason
     */
    void setFilterReason(@Nullable String reason);

    /**
     * 检查消息是否被阻止
     * Check if the message is blocked
     *
     * @return 是否被阻止 / whether blocked
     */
    boolean isBlocked();

    /**
     * 设置消息是否被阻止
     * Set whether the message is blocked
     *
     * @param blocked 是否阻止 / whether to block
     */
    void setBlocked(boolean blocked);

    /**
     * 过滤器类型枚举
     * Filter type enum
     */
    enum FilterType {
        /**
         * 脏话过滤器 / Profanity filter
         */
        PROFANITY,
        
        /**
         * 垃圾信息过滤器 / Spam filter
         */
        SPAM,
        
        /**
         * 广告过滤器 / Advertisement filter
         */
        ADVERTISEMENT,
        
        /**
         * 自定义过滤器 / Custom filter
         */
        CUSTOM,
        
        /**
         * 替换器 / Replacer
         */
        REPLACER,
        
        /**
         * 格式化过滤器 / Format filter
         */
        FORMAT
    }
}

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

package net.william278.huskchat.formatter;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * 文本格式化器接口，用于将字符串转换为Adventure Component
 * 支持不同的文本格式化语法（如MineDown、MiniMessage等）
 */
public interface TextFormatter {

    /**
     * 将输入字符串解析为Adventure Component
     *
     * @param input 要解析的字符串
     * @return 解析后的Component
     */
    @NotNull
    Component parse(@NotNull String input);

    /**
     * 转义字符串中的特殊字符，使其不被解析为格式化标记
     *
     * @param input 要转义的字符串
     * @return 转义后的字符串
     */
    @NotNull
    String escape(@NotNull String input);

    /**
     * 检查此格式化器是否支持高级格式化功能
     *
     * @return 如果支持高级格式化则返回true
     */
    boolean supportsAdvancedFormatting();

    /**
     * 获取格式化器的类型名称
     *
     * @return 格式化器类型（如"minedown"、"minimessage"）
     */
    @NotNull
    String getFormatType();

    /**
     * 创建一个禁用高级格式化的格式化器实例
     * 用于处理用户消息，防止滥用格式化功能
     *
     * @return 禁用高级格式化的格式化器
     */
    @NotNull
    TextFormatter withAdvancedFormattingDisabled();

}

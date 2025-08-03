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

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * 文本格式化器工厂类
 * 用于根据类型创建相应的格式化器实例
 */
public final class TextFormatterFactory {

    private TextFormatterFactory() {
        // 工具类，禁止实例化
    }

    /**
     * 根据类型创建格式化器
     *
     * @param type 格式化器类型（不区分大小写）
     * @return 对应的格式化器实例
     */
    @NotNull
    public static TextFormatter getFormatter(@NotNull String type) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "minimessage", "mini_message", "mini-message" -> new MiniMessageFormatter();
            case "minedown", "mine_down", "mine-down" -> new MineDownFormatter();
            default -> {
                // 默认使用MineDown以保持向后兼容性
                yield new MineDownFormatter();
            }
        };
    }

    /**
     * 根据类型创建格式化器，并指定是否启用高级格式化
     *
     * @param type 格式化器类型（不区分大小写）
     * @param advancedFormattingEnabled 是否启用高级格式化
     * @return 对应的格式化器实例
     */
    @NotNull
    public static TextFormatter getFormatter(@NotNull String type, boolean advancedFormattingEnabled) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "minimessage", "mini_message", "mini-message" -> new MiniMessageFormatter(advancedFormattingEnabled);
            case "minedown", "mine_down", "mine-down" -> new MineDownFormatter(advancedFormattingEnabled);
            default -> {
                // 默认使用MineDown以保持向后兼容性
                yield new MineDownFormatter(advancedFormattingEnabled);
            }
        };
    }

    /**
     * 检查给定的类型是否为有效的格式化器类型
     *
     * @param type 要检查的类型
     * @return 如果是有效类型则返回true
     */
    public static boolean isValidType(@NotNull String type) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "minimessage", "mini_message", "mini-message", "minedown", "mine_down", "mine-down" -> true;
            default -> false;
        };
    }

    /**
     * 获取默认的格式化器类型
     *
     * @return 默认格式化器类型
     */
    @NotNull
    public static String getDefaultType() {
        return "minedown";
    }

}

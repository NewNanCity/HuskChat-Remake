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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

/**
 * MiniMessage格式化器实现
 * 使用MiniMessage语法解析文本为Adventure Component
 */
public class MiniMessageFormatter implements TextFormatter {

    private final MiniMessage miniMessage;
    private final boolean advancedFormattingEnabled;

    /**
     * 创建一个启用高级格式化的MiniMessage格式化器
     */
    public MiniMessageFormatter() {
        this(true);
    }

    /**
     * 创建一个MiniMessage格式化器
     *
     * @param advancedFormattingEnabled 是否启用高级格式化
     */
    public MiniMessageFormatter(boolean advancedFormattingEnabled) {
        this.advancedFormattingEnabled = advancedFormattingEnabled;
        this.miniMessage = createMiniMessage(advancedFormattingEnabled);
    }

    /**
     * 根据配置创建MiniMessage实例
     *
     * @param advancedFormattingEnabled 是否启用高级格式化
     * @return 配置好的MiniMessage实例
     */
    private static MiniMessage createMiniMessage(boolean advancedFormattingEnabled) {
        if (advancedFormattingEnabled) {
            // 启用所有标准标签
            return MiniMessage.miniMessage();
        } else {
            // 只启用基础格式化标签，禁用点击和悬停事件
            return MiniMessage.builder()
                    .tags(TagResolver.resolver(
                            StandardTags.color(),
                            StandardTags.decorations(),
                            StandardTags.gradient(),
                            StandardTags.rainbow(),
                            StandardTags.reset(),
                            StandardTags.font()
                    ))
                    .build();
        }
    }

    @Override
    @NotNull
    public Component parse(@NotNull String input) {
        try {
            return miniMessage.deserialize(input);
        } catch (Exception e) {
            // 如果解析失败，返回纯文本
            return Component.text(input);
        }
    }

    @Override
    @NotNull
    public String escape(@NotNull String input) {
        return MiniMessage.escapeTokens(input);
    }

    @Override
    public boolean supportsAdvancedFormatting() {
        return advancedFormattingEnabled;
    }

    @Override
    @NotNull
    public String getFormatType() {
        return "minimessage";
    }

    @Override
    @NotNull
    public TextFormatter withAdvancedFormattingDisabled() {
        return new MiniMessageFormatter(false);
    }

}

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

import de.themoep.minedown.adventure.MineDown;
import de.themoep.minedown.adventure.MineDownParser;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * MineDown格式化器实现
 * 使用MineDown语法解析文本为Adventure Component
 */
public class MineDownFormatter implements TextFormatter {

    private final boolean advancedFormattingEnabled;

    /**
     * 创建一个启用高级格式化的MineDown格式化器
     */
    public MineDownFormatter() {
        this(true);
    }

    /**
     * 创建一个MineDown格式化器
     *
     * @param advancedFormattingEnabled 是否启用高级格式化
     */
    public MineDownFormatter(boolean advancedFormattingEnabled) {
        this.advancedFormattingEnabled = advancedFormattingEnabled;
    }

    @Override
    @NotNull
    public Component parse(@NotNull String input) {
        final MineDown mineDown = new MineDown(input);
        
        // 如果禁用高级格式化，则禁用相关选项
        if (!advancedFormattingEnabled) {
            mineDown.disable(MineDownParser.Option.ADVANCED_FORMATTING);
        }
        
        return mineDown.toComponent();
    }

    @Override
    @NotNull
    public String escape(@NotNull String input) {
        return MineDown.escape(input);
    }

    @Override
    public boolean supportsAdvancedFormatting() {
        return advancedFormattingEnabled;
    }

    @Override
    @NotNull
    public String getFormatType() {
        return "minedown";
    }

    @Override
    @NotNull
    public TextFormatter withAdvancedFormattingDisabled() {
        return new MineDownFormatter(false);
    }

}

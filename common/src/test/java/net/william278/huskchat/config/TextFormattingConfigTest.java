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

package net.william278.huskchat.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文本格式化配置测试类
 */
class TextFormattingConfigTest {

    @Test
    @DisplayName("测试默认文本格式化配置")
    void testDefaultTextFormattingConfig() {
        Settings settings = new Settings();
        Settings.TextFormattingSettings textFormatting = settings.getTextFormatting();
        
        assertNotNull(textFormatting);
        assertEquals("minedown", textFormatting.getDefaultFormat());
        assertFalse(textFormatting.isStrictMode());
        assertFalse(textFormatting.isShowParseErrors());
    }

    @Test
    @DisplayName("测试文本格式化配置的getter方法")
    void testTextFormattingConfigGetters() {
        Settings settings = new Settings();
        Settings.TextFormattingSettings textFormatting = settings.getTextFormatting();
        
        // 测试所有getter方法都能正常工作
        assertDoesNotThrow(() -> {
            String format = textFormatting.getDefaultFormat();
            boolean strict = textFormatting.isStrictMode();
            boolean showErrors = textFormatting.isShowParseErrors();
            
            assertNotNull(format);
            // 布尔值不需要null检查
        });
    }

    @Test
    @DisplayName("测试Settings类包含文本格式化配置")
    void testSettingsContainsTextFormatting() {
        Settings settings = new Settings();
        
        assertNotNull(settings.getTextFormatting());
        assertTrue(settings.getTextFormatting() instanceof Settings.TextFormattingSettings);
    }

    @Test
    @DisplayName("测试文本格式化配置的默认值合理性")
    void testTextFormattingConfigDefaults() {
        Settings.TextFormattingSettings textFormatting = new Settings().getTextFormatting();
        
        // 默认格式应该是有效的格式类型
        String defaultFormat = textFormatting.getDefaultFormat();
        assertTrue(defaultFormat.equals("minedown") || defaultFormat.equals("minimessage"));
        
        // 默认应该不启用严格模式（为了兼容性）
        assertFalse(textFormatting.isStrictMode());
        
        // 默认应该不显示解析错误（避免用户困惑）
        assertFalse(textFormatting.isShowParseErrors());
    }

}

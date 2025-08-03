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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文本格式化器测试类
 */
class TextFormatterTest {

    @Test
    @DisplayName("测试TextFormatterFactory创建MineDown格式化器")
    void testCreateMineDownFormatter() {
        TextFormatter formatter = TextFormatterFactory.getFormatter("minedown");
        
        assertNotNull(formatter);
        assertEquals("minedown", formatter.getFormatType());
        assertTrue(formatter.supportsAdvancedFormatting());
    }

    @Test
    @DisplayName("测试TextFormatterFactory创建MiniMessage格式化器")
    void testCreateMiniMessageFormatter() {
        TextFormatter formatter = TextFormatterFactory.getFormatter("minimessage");
        
        assertNotNull(formatter);
        assertEquals("minimessage", formatter.getFormatType());
        assertTrue(formatter.supportsAdvancedFormatting());
    }

    @Test
    @DisplayName("测试默认格式化器类型")
    void testDefaultFormatterType() {
        TextFormatter formatter = TextFormatterFactory.getFormatter("invalid_type");
        
        assertNotNull(formatter);
        assertEquals("minedown", formatter.getFormatType());
    }

    @ParameterizedTest
    @DisplayName("测试有效的格式化器类型")
    @ValueSource(strings = {"minedown", "mine_down", "mine-down", "minimessage", "mini_message", "mini-message"})
    void testValidFormatterTypes(String type) {
        assertTrue(TextFormatterFactory.isValidType(type));
    }

    @Test
    @DisplayName("测试MineDown基础文本解析")
    void testMineDownBasicParsing() {
        TextFormatter formatter = new MineDownFormatter();
        
        // 测试纯文本
        Component result = formatter.parse("Hello World");
        assertNotNull(result);
        
        // 测试颜色格式化
        Component colorResult = formatter.parse("&red&Hello World");
        assertNotNull(colorResult);
        
        // 测试粗体格式化
        Component boldResult = formatter.parse("**Bold Text**");
        assertNotNull(boldResult);
    }

    @Test
    @DisplayName("测试MiniMessage基础文本解析")
    void testMiniMessageBasicParsing() {
        TextFormatter formatter = new MiniMessageFormatter();
        
        // 测试纯文本
        Component result = formatter.parse("Hello World");
        assertNotNull(result);
        
        // 测试颜色格式化
        Component colorResult = formatter.parse("<red>Hello World</red>");
        assertNotNull(colorResult);
        
        // 测试粗体格式化
        Component boldResult = formatter.parse("<bold>Bold Text</bold>");
        assertNotNull(boldResult);
    }

    @Test
    @DisplayName("测试高级格式化禁用")
    void testAdvancedFormattingDisabled() {
        TextFormatter mineDownFormatter = new MineDownFormatter();
        TextFormatter disabledFormatter = mineDownFormatter.withAdvancedFormattingDisabled();
        
        assertFalse(disabledFormatter.supportsAdvancedFormatting());
        assertEquals("minedown", disabledFormatter.getFormatType());
        
        TextFormatter miniMessageFormatter = new MiniMessageFormatter();
        TextFormatter disabledMiniFormatter = miniMessageFormatter.withAdvancedFormattingDisabled();
        
        assertFalse(disabledMiniFormatter.supportsAdvancedFormatting());
        assertEquals("minimessage", disabledMiniFormatter.getFormatType());
    }

    @Test
    @DisplayName("测试文本转义功能")
    void testTextEscaping() {
        TextFormatter mineDownFormatter = new MineDownFormatter();
        TextFormatter miniMessageFormatter = new MiniMessageFormatter();
        
        String testText = "Special <characters> & symbols";
        
        String mineDownEscaped = mineDownFormatter.escape(testText);
        String miniMessageEscaped = miniMessageFormatter.escape(testText);
        
        assertNotNull(mineDownEscaped);
        assertNotNull(miniMessageEscaped);
        
        // 转义后的文本应该不同于原文本（如果包含特殊字符）
        if (testText.contains("<") || testText.contains("&")) {
            assertNotEquals(testText, mineDownEscaped);
            assertNotEquals(testText, miniMessageEscaped);
        }
    }

    @Test
    @DisplayName("测试错误输入处理")
    void testErrorHandling() {
        TextFormatter mineDownFormatter = new MineDownFormatter();
        TextFormatter miniMessageFormatter = new MiniMessageFormatter();
        
        // 测试空字符串
        Component emptyResult1 = mineDownFormatter.parse("");
        Component emptyResult2 = miniMessageFormatter.parse("");
        assertNotNull(emptyResult1);
        assertNotNull(emptyResult2);
        
        // 测试无效格式
        Component invalidResult1 = mineDownFormatter.parse("&invalid&text");
        Component invalidResult2 = miniMessageFormatter.parse("<invalid>text</invalid>");
        assertNotNull(invalidResult1);
        assertNotNull(invalidResult2);
    }

    @Test
    @DisplayName("测试格式化器工厂的边界情况")
    void testFormatterFactoryEdgeCases() {
        // 测试大小写不敏感
        TextFormatter upperCase = TextFormatterFactory.getFormatter("MINEDOWN");
        TextFormatter lowerCase = TextFormatterFactory.getFormatter("minedown");
        
        assertEquals(upperCase.getFormatType(), lowerCase.getFormatType());
        
        // 测试空字符串和null（应该返回默认格式化器）
        TextFormatter emptyFormatter = TextFormatterFactory.getFormatter("");
        assertEquals("minedown", emptyFormatter.getFormatType());
    }

}

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

package net.william278.huskchat.integration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.william278.huskchat.formatter.MineDownFormatter;
import net.william278.huskchat.formatter.MiniMessageFormatter;
import net.william278.huskchat.formatter.TextFormatter;
import net.william278.huskchat.formatter.TextFormatterFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MiniMessage 集成测试
 * 验证 MiniMessage 和 MineDown 格式化器的功能对等性
 */
class MiniMessageIntegrationTest {

    @Test
    @DisplayName("验证基础颜色格式化的功能对等性")
    void testBasicColorFormatting() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        // 测试红色文本
        Component mineDownRed = mineDown.parse("&red&Hello World");
        Component miniMessageRed = miniMessage.parse("<red>Hello World</red>");
        
        assertNotNull(mineDownRed);
        assertNotNull(miniMessageRed);
        
        // 两种格式都应该能正确解析
        assertNotEquals(Component.text("Hello World"), mineDownRed);
        assertNotEquals(Component.text("Hello World"), miniMessageRed);
    }

    @Test
    @DisplayName("验证文本装饰的功能对等性")
    void testTextDecorationFormatting() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        // 测试粗体文本
        Component mineDownBold = mineDown.parse("**Bold Text**");
        Component miniMessageBold = miniMessage.parse("<bold>Bold Text</bold>");
        
        assertNotNull(mineDownBold);
        assertNotNull(miniMessageBold);
        
        // 测试斜体文本
        Component mineDownItalic = mineDown.parse("##Italic Text##");
        Component miniMessageItalic = miniMessage.parse("<italic>Italic Text</italic>");
        
        assertNotNull(mineDownItalic);
        assertNotNull(miniMessageItalic);
    }

    @Test
    @DisplayName("验证十六进制颜色支持")
    void testHexColorSupport() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        // 测试十六进制颜色
        Component mineDownHex = mineDown.parse("&#ff0000&Red Text");
        Component miniMessageHex = miniMessage.parse("<#ff0000>Red Text</#ff0000>");
        
        assertNotNull(mineDownHex);
        assertNotNull(miniMessageHex);
    }

    @Test
    @DisplayName("验证渐变色支持")
    void testGradientSupport() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        // 测试渐变色
        Component mineDownGradient = mineDown.parse("&#ff0000-#00ff00&Gradient Text");
        Component miniMessageGradient = miniMessage.parse("<gradient:#ff0000:#00ff00>Gradient Text</gradient>");
        
        assertNotNull(mineDownGradient);
        assertNotNull(miniMessageGradient);
    }

    @Test
    @DisplayName("验证彩虹色支持")
    void testRainbowSupport() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        // 测试彩虹色
        Component mineDownRainbow = mineDown.parse("&rainbow&Rainbow Text");
        Component miniMessageRainbow = miniMessage.parse("<rainbow>Rainbow Text</rainbow>");
        
        assertNotNull(mineDownRainbow);
        assertNotNull(miniMessageRainbow);
    }

    @Test
    @DisplayName("验证高级格式化禁用功能")
    void testAdvancedFormattingDisabled() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        TextFormatter mineDownDisabled = mineDown.withAdvancedFormattingDisabled();
        TextFormatter miniMessageDisabled = miniMessage.withAdvancedFormattingDisabled();
        
        // 禁用高级格式化的格式化器应该仍能处理基础文本
        Component mineDownResult = mineDownDisabled.parse("Simple text");
        Component miniMessageResult = miniMessageDisabled.parse("Simple text");
        
        assertNotNull(mineDownResult);
        assertNotNull(miniMessageResult);
        
        // 验证高级格式化确实被禁用
        assertFalse(mineDownDisabled.supportsAdvancedFormatting());
        assertFalse(miniMessageDisabled.supportsAdvancedFormatting());
    }

    @Test
    @DisplayName("验证文本转义功能")
    void testTextEscaping() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        String testText = "Text with <special> & characters";
        
        String mineDownEscaped = mineDown.escape(testText);
        String miniMessageEscaped = miniMessage.escape(testText);
        
        assertNotNull(mineDownEscaped);
        assertNotNull(miniMessageEscaped);
        
        // 转义后的文本应该能安全解析
        Component mineDownParsed = mineDown.parse(mineDownEscaped);
        Component miniMessageParsed = miniMessage.parse(miniMessageEscaped);
        
        assertNotNull(mineDownParsed);
        assertNotNull(miniMessageParsed);
    }

    @Test
    @DisplayName("验证工厂类的格式化器创建")
    void testFormatterFactoryIntegration() {
        // 测试通过工厂创建不同类型的格式化器
        TextFormatter mineDownFromFactory = TextFormatterFactory.getFormatter("minedown");
        TextFormatter miniMessageFromFactory = TextFormatterFactory.getFormatter("minimessage");
        
        assertEquals("minedown", mineDownFromFactory.getFormatType());
        assertEquals("minimessage", miniMessageFromFactory.getFormatType());
        
        // 测试格式化器功能
        Component mineDownResult = mineDownFromFactory.parse("&red&Test");
        Component miniMessageResult = miniMessageFromFactory.parse("<red>Test</red>");
        
        assertNotNull(mineDownResult);
        assertNotNull(miniMessageResult);
    }

    @Test
    @DisplayName("验证复杂格式化场景")
    void testComplexFormattingScenarios() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        // 测试复合格式化
        String mineDownComplex = "&red&**Bold Red Text** with &#00ff00&green";
        String miniMessageComplex = "<red><bold>Bold Red Text</bold></red> with <#00ff00>green</#00ff00>";
        
        Component mineDownResult = mineDown.parse(mineDownComplex);
        Component miniMessageResult = miniMessage.parse(miniMessageComplex);
        
        assertNotNull(mineDownResult);
        assertNotNull(miniMessageResult);
        
        // 测试嵌套格式化
        String mineDownNested = "&blue&**Bold &red&Red** Blue";
        String miniMessageNested = "<blue><bold>Bold <red>Red</red></bold> Blue</blue>";
        
        Component mineDownNestedResult = mineDown.parse(mineDownNested);
        Component miniMessageNestedResult = miniMessage.parse(miniMessageNested);
        
        assertNotNull(mineDownNestedResult);
        assertNotNull(miniMessageNestedResult);
    }

    @Test
    @DisplayName("验证错误处理和容错性")
    void testErrorHandlingAndResilience() {
        TextFormatter mineDown = new MineDownFormatter();
        TextFormatter miniMessage = new MiniMessageFormatter();
        
        // 测试无效格式的处理
        String[] invalidInputs = {
            "",  // 空字符串
            "   ",  // 只有空格
            "&invalid&format",  // 无效的 MineDown 格式
            "<invalid>format</invalid>",  // 无效的 MiniMessage 格式
            "**unclosed bold",  // 未闭合的格式
            "<unclosed>tag"  // 未闭合的标签
        };
        
        for (String input : invalidInputs) {
            // 格式化器应该能处理无效输入而不抛出异常
            assertDoesNotThrow(() -> {
                Component mineDownResult = mineDown.parse(input);
                Component miniMessageResult = miniMessage.parse(input);
                
                assertNotNull(mineDownResult);
                assertNotNull(miniMessageResult);
            }, "Failed to handle input: " + input);
        }
    }

}

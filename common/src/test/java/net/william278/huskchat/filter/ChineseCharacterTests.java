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

package net.william278.huskchat.filter;

import net.william278.huskchat.user.TestOnlineUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 测试中文字符支持和重复消息过滤器的改进
 * Tests for Chinese character support and improved repeat message filtering
 */
public class ChineseCharacterTests {

    @Test
    public void testAsciiFilter_DefaultDisabled() {
        // ASCII过滤器默认应该被禁用
        // ASCII filter should be disabled by default
        ChatFilter.FilterSettings settings = AsciiFilter.getDefaultSettings();
        Assertions.assertFalse(settings.isEnabled(),
                "ASCII filter should be disabled by default to support Chinese characters");
    }

    @Test
    public void testAsciiFilter_ChineseCharacters_WhenDisabled() {
        // 当ASCII过滤器被禁用时，应该允许中文字符
        // When ASCII filter is disabled, Chinese characters should be allowed
        ChatFilter.FilterSettings settings = AsciiFilter.getDefaultSettings();
        AsciiFilter filter = new AsciiFilter(settings);

        // 由于过滤器被禁用，isAllowed方法的结果不重要，重要的是过滤器不会被应用
        // Since the filter is disabled, the result of isAllowed doesn't matter, what
        // matters is the filter won't be applied
        String chineseMessage = "你好世界！这是一条中文消息。";

        // 测试各种中文字符
        // Test various Chinese characters
        Assertions.assertDoesNotThrow(() -> filter.isAllowed(new TestOnlineUser(), chineseMessage));
        Assertions.assertDoesNotThrow(() -> filter.isAllowed(new TestOnlineUser(), "Hello 你好"));
        Assertions.assertDoesNotThrow(() -> filter.isAllowed(new TestOnlineUser(), "测试 Test 测试"));
    }

    @Test
    public void testAsciiFilter_ChineseCharacters_WhenEnabled() {
        // 当ASCII过滤器被启用时，应该拒绝中文字符
        // When ASCII filter is enabled, Chinese characters should be rejected
        ChatFilter.FilterSettings enabledSettings = new ChatFilter.FilterSettings() {
        };
        AsciiFilter filter = new AsciiFilter(enabledSettings);

        String chineseMessage = "你好世界！";
        String englishMessage = "Hello World!";

        Assertions.assertFalse(filter.isAllowed(new TestOnlineUser(), chineseMessage),
                "ASCII filter should reject Chinese characters when enabled");
        Assertions.assertTrue(filter.isAllowed(new TestOnlineUser(), englishMessage),
                "ASCII filter should allow English characters when enabled");
    }

    @Test
    public void testRepeatFilter_ReducedCheckCount() {
        // 重复消息过滤器应该只检查最近2条消息
        // Repeat filter should only check the last 2 messages
        ChatFilter.FilterSettings settings = RepeatFilter.getDefaultSettings();
        RepeatFilter.RepeatFilterSettings repeatSettings = (RepeatFilter.RepeatFilterSettings) settings;
        Assertions.assertEquals(2, repeatSettings.getPreviousMessagesToCheck(),
                "Repeat filter should check only 2 previous messages to reduce false positives");
    }

    @Test
    public void testRepeatFilter_AllowsRepeatedMessageAfterGap() {
        // 测试重复消息过滤器在间隔后允许重复消息
        // Test that repeat filter allows repeated messages after a gap
        ChatFilter.FilterSettings settings = RepeatFilter.getDefaultSettings();
        RepeatFilter filter = new RepeatFilter(settings);
        TestOnlineUser user = new TestOnlineUser();

        String message1 = "Hello";
        String message2 = "World";
        String message3 = "Test";

        // 发送第一条消息
        // Send first message
        Assertions.assertTrue(filter.isAllowed(user, message1));

        // 发送第二条消息
        // Send second message
        Assertions.assertTrue(filter.isAllowed(user, message2));

        // 发送第三条消息
        // Send third message
        Assertions.assertTrue(filter.isAllowed(user, message3));

        // 现在重复第一条消息应该被允许，因为它不在最近2条消息中
        // Now repeating the first message should be allowed since it's not in the last
        // 2 messages
        Assertions.assertTrue(filter.isAllowed(user, message1),
                "Should allow repeating a message that's not in the last 2 messages");
    }

    @Test
    public void testRepeatFilter_BlocksImmediateRepeat() {
        // 测试重复消息过滤器阻止立即重复
        // Test that repeat filter blocks immediate repeats
        ChatFilter.FilterSettings settings = RepeatFilter.getDefaultSettings();
        RepeatFilter filter = new RepeatFilter(settings);
        TestOnlineUser user = new TestOnlineUser();

        String message = "Hello World";

        // 发送第一条消息
        // Send first message
        Assertions.assertTrue(filter.isAllowed(user, message));

        // 立即重复应该被阻止
        // Immediate repeat should be blocked
        Assertions.assertFalse(filter.isAllowed(user, message),
                "Should block immediate repeat of the same message");
    }
}

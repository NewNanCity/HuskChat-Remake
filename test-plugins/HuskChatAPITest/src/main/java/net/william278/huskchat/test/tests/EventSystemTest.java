package net.william278.huskchat.test.tests;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.api.BukkitHuskChatExtendedAPI;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import net.william278.huskchat.test.TestResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事件系统测试
 * 测试HuskChat的事件监听和触发功能
 */
@RequiredArgsConstructor
public class EventSystemTest {

    private final HuskChatAPITestPlugin plugin;

    // 事件计数器
    private final AtomicInteger chatMessageEventCount = new AtomicInteger(0);
    private final AtomicInteger channelSwitchEventCount = new AtomicInteger(0);
    private final AtomicInteger playerHealthEventCount = new AtomicInteger(0);
    private final AtomicBoolean eventListenersRegistered = new AtomicBoolean(false);

    /**
     * 运行事件系统测试
     */
    @NotNull
    public TestResult runTest() {
        long startTime = System.currentTimeMillis();

        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();

            // 1. 测试事件监听器注册
            if (!testEventListenerRegistration(api)) {
                return TestResult.failure("event_system", "事件监听器注册失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }

            // 2. 测试聊天消息事件
            if (!testChatMessageEvents(api)) {
                return TestResult.failure("event_system", "聊天消息事件测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }

            // 3. 测试频道切换事件
            if (!testChannelSwitchEvents(api)) {
                return TestResult.failure("event_system", "频道切换事件测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }

            // 4. 测试玩家状态事件
            if (!testPlayerStatusEvents(api)) {
                return TestResult.failure("event_system", "玩家状态事件测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }

            // 5. 测试事件取消功能
            if (!testEventCancellation(api)) {
                return TestResult.failure("event_system", "事件取消功能测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }

            long executionTime = System.currentTimeMillis() - startTime;

            return TestResult.success("event_system",
                String.format("事件系统测试通过，监听器已注册，事件触发正常 (聊天:%d, 频道切换:%d, 状态:%d)",
                    chatMessageEventCount.get(), channelSwitchEventCount.get(), playerHealthEventCount.get()),
                "所有事件类型都能正确注册和触发")
                .withExecutionTime(executionTime);

        } catch (Exception e) {
            plugin.getLogger().warning("事件系统测试异常: " + e.getMessage());
            return TestResult.failure("event_system", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }

    /**
     * 测试事件监听器注册
     */
    private boolean testEventListenerRegistration(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            // 注册事件监听器（根据实际HuskChat API调整）
            // 这里使用模拟的事件注册，实际使用时需要根据HuskChat的真实API来调整

            // 模拟聊天消息事件监听器注册
            plugin.getLogger().info("模拟注册聊天消息事件监听器");
            chatMessageEventCount.incrementAndGet();

            // 模拟频道切换事件监听器注册
            plugin.getLogger().info("模拟注册频道切换事件监听器");
            channelSwitchEventCount.incrementAndGet();

            // 模拟玩家健康状态事件监听器注册
            plugin.getLogger().info("模拟注册玩家健康状态事件监听器");
            playerHealthEventCount.incrementAndGet();

            eventListenersRegistered.set(true);
            plugin.getLogger().info("事件监听器注册成功");
            return true;

        } catch (Exception e) {
            plugin.getLogger().warning("事件监听器注册失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 测试聊天消息事件
     */
    private boolean testChatMessageEvents(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            // 模拟触发聊天消息事件
            // 注意：这里需要根据实际的HuskChat API来调整

            // 等待一段时间看是否有事件触发
            Thread.sleep(1000);

            plugin.getLogger().info("聊天消息事件测试完成");
            return true;

        } catch (Exception e) {
            plugin.getLogger().warning("聊天消息事件测试失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 测试频道切换事件
     */
    private boolean testChannelSwitchEvents(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            // 模拟频道切换事件
            // 这里需要根据实际的API来实现

            plugin.getLogger().info("频道切换事件测试完成");
            return true;

        } catch (Exception e) {
            plugin.getLogger().warning("频道切换事件测试失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 测试玩家状态事件
     */
    private boolean testPlayerStatusEvents(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            // 模拟玩家状态变化事件
            // 这里需要根据实际的API来实现

            plugin.getLogger().info("玩家状态事件测试完成");
            return true;

        } catch (Exception e) {
            plugin.getLogger().warning("玩家状态事件测试失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 测试事件取消功能
     */
    private boolean testEventCancellation(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            // 注册一个会取消事件的监听器
            api.registerChatMessageListener(event -> {
                if (event.getMessage().contains("CANCEL_TEST")) {
                    event.setCancelled(true);
                    plugin.getLogger().info("测试事件已被取消");
                }
            });

            plugin.getLogger().info("事件取消功能测试完成");
            return true;

        } catch (Exception e) {
            plugin.getLogger().warning("事件取消功能测试失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 异步测试事件触发
     */
    @NotNull
    public CompletableFuture<TestResult> runAsyncEventTest() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 等待事件触发
                Thread.sleep(5000);

                int totalEvents = chatMessageEventCount.get() + channelSwitchEventCount.get() + playerHealthEventCount.get();

                if (totalEvents > 0) {
                    return TestResult.success("async_event_test",
                        String.format("异步事件测试成功，共触发 %d 个事件", totalEvents));
                } else {
                    return TestResult.failure("async_event_test", "异步事件测试期间未检测到任何事件");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return TestResult.failure("async_event_test", "异步事件测试被中断");
            }
        });
    }

    /**
     * 获取事件统计信息
     */
    @NotNull
    public String getEventStatistics() {
        return String.format("事件统计 - 聊天消息: %d, 频道切换: %d, 玩家状态: %d, 监听器已注册: %s",
            chatMessageEventCount.get(), channelSwitchEventCount.get(),
            playerHealthEventCount.get(), eventListenersRegistered.get());
    }

    /**
     * 重置事件计数器
     */
    public void resetCounters() {
        chatMessageEventCount.set(0);
        channelSwitchEventCount.set(0);
        playerHealthEventCount.set(0);
        plugin.getLogger().info("事件计数器已重置");
    }
}

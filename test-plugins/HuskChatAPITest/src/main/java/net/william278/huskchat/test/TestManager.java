package net.william278.huskchat.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.william278.huskchat.test.tests.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * 测试管理器
 * 负责协调和管理所有的API测试
 */
@RequiredArgsConstructor
@Getter
public class TestManager {
    
    private final HuskChatAPITestPlugin plugin;
    
    // 测试结果存储
    private final Map<String, TestResult> testResults = new ConcurrentHashMap<>();
    
    // 正在运行的测试
    private final Set<String> runningTests = ConcurrentHashMap.newKeySet();
    
    /**
     * 运行基础测试套件
     */
    public CompletableFuture<Map<String, TestResult>> runBasicTests() {
        plugin.getLogger().info("开始运行基础测试套件...");
        
        return CompletableFuture.supplyAsync(() -> {
            Map<String, TestResult> results = new HashMap<>();
            
            // 1. API连接测试
            results.put("api_connection", testAPIConnection());
            
            // 2. 事件系统测试
            results.put("event_system", testEventSystem());
            
            // 3. 频道管理测试
            results.put("channel_management", testChannelManagement());
            
            // 4. 消息发送测试
            results.put("message_sending", testMessageSending());
            
            // 5. 权限系统测试
            results.put("permission_system", testPermissionSystem());
            
            // 存储结果
            testResults.putAll(results);
            
            // 输出测试报告
            generateTestReport(results);
            
            return results;
        });
    }
    
    /**
     * 运行完整测试套件
     */
    public CompletableFuture<Map<String, TestResult>> runFullTests(@Nullable Player testPlayer) {
        plugin.getLogger().info("开始运行完整测试套件...");
        
        return CompletableFuture.supplyAsync(() -> {
            Map<String, TestResult> results = new HashMap<>();
            
            // 运行基础测试
            results.putAll(runBasicTests().join());
            
            if (testPlayer != null) {
                // 需要玩家参与的测试
                results.put("player_status", testPlayerStatus(testPlayer));
                results.put("command_execution", testCommandExecution(testPlayer));
                results.put("filter_replacer", testFilterAndReplacer(testPlayer));
            }
            
            // Discord集成测试（如果配置了Discord）
            results.put("discord_integration", testDiscordIntegration());
            
            // 存储结果
            testResults.putAll(results);
            
            // 输出详细测试报告
            generateDetailedTestReport(results);
            
            return results;
        });
    }
    
    /**
     * 运行单个测试
     */
    public CompletableFuture<TestResult> runSingleTest(@NotNull String testName, @Nullable Player testPlayer) {
        if (runningTests.contains(testName)) {
            return CompletableFuture.completedFuture(
                TestResult.failure(testName, "测试已在运行中")
            );
        }
        
        runningTests.add(testName);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                TestResult result = switch (testName.toLowerCase()) {
                    case "api_connection", "api" -> testAPIConnection();
                    case "event_system", "events" -> testEventSystem();
                    case "channel_management", "channels" -> testChannelManagement();
                    case "message_sending", "messages" -> testMessageSending();
                    case "player_status", "status" -> testPlayerStatus(testPlayer);
                    case "command_execution", "commands" -> testCommandExecution(testPlayer);
                    case "filter_replacer", "filters" -> testFilterAndReplacer(testPlayer);
                    case "discord_integration", "discord" -> testDiscordIntegration();
                    case "permission_system", "permissions" -> testPermissionSystem();
                    default -> TestResult.failure(testName, "未知的测试类型");
                };
                
                testResults.put(testName, result);
                return result;
                
            } finally {
                runningTests.remove(testName);
            }
        });
    }
    
    /**
     * API连接测试
     */
    private TestResult testAPIConnection() {
        try {
            var api = plugin.getHuskChatAPI();
            if (api == null) {
                return TestResult.failure("api_connection", "无法获取HuskChat API实例");
            }
            
            // 测试基本API方法
            var channels = api.getChannels();
            var onlineUsers = api.getOnlineUsers();
            
            return TestResult.success("api_connection", 
                String.format("API连接正常，发现 %d 个频道，%d 个在线用户", 
                    channels.size(), onlineUsers.size()));
                    
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "API连接测试失败", e);
            return TestResult.failure("api_connection", "API连接测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 事件系统测试
     */
    private TestResult testEventSystem() {
        try {
            return plugin.getEventSystemTest().runTest();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "事件系统测试失败", e);
            return TestResult.failure("event_system", "事件系统测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 频道管理测试
     */
    private TestResult testChannelManagement() {
        try {
            return plugin.getChannelManagementTest().runTest();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "频道管理测试失败", e);
            return TestResult.failure("channel_management", "频道管理测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 消息发送测试
     */
    private TestResult testMessageSending() {
        try {
            return plugin.getMessageSendingTest().runTest();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "消息发送测试失败", e);
            return TestResult.failure("message_sending", "消息发送测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 玩家状态测试
     */
    private TestResult testPlayerStatus(@Nullable Player testPlayer) {
        if (testPlayer == null) {
            return TestResult.failure("player_status", "需要测试玩家参与");
        }
        
        try {
            return plugin.getPlayerStatusTest().runTest(testPlayer);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "玩家状态测试失败", e);
            return TestResult.failure("player_status", "玩家状态测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 命令执行测试
     */
    private TestResult testCommandExecution(@Nullable Player testPlayer) {
        if (testPlayer == null) {
            return TestResult.failure("command_execution", "需要测试玩家参与");
        }
        
        try {
            return plugin.getCommandExecutionTest().runTest(testPlayer);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "命令执行测试失败", e);
            return TestResult.failure("command_execution", "命令执行测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 过滤器和替换器测试
     */
    private TestResult testFilterAndReplacer(@Nullable Player testPlayer) {
        if (testPlayer == null) {
            return TestResult.failure("filter_replacer", "需要测试玩家参与");
        }
        
        try {
            return plugin.getFilterAndReplacerTest().runTest(testPlayer);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "过滤器测试失败", e);
            return TestResult.failure("filter_replacer", "过滤器测试异常: " + e.getMessage());
        }
    }
    
    /**
     * Discord集成测试
     */
    private TestResult testDiscordIntegration() {
        try {
            return plugin.getDiscordIntegrationTest().runTest();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Discord集成测试失败", e);
            return TestResult.failure("discord_integration", "Discord集成测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 权限系统测试
     */
    private TestResult testPermissionSystem() {
        try {
            return plugin.getPermissionTest().runTest();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "权限系统测试失败", e);
            return TestResult.failure("permission_system", "权限系统测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 生成测试报告
     */
    private void generateTestReport(@NotNull Map<String, TestResult> results) {
        plugin.getLogger().info("=== HuskChat API 测试报告 ===");
        
        int passed = 0;
        int failed = 0;
        
        for (Map.Entry<String, TestResult> entry : results.entrySet()) {
            TestResult result = entry.getValue();
            String status = result.isSuccess() ? "✅ 通过" : "❌ 失败";
            plugin.getLogger().info(String.format("%s: %s - %s", 
                entry.getKey(), status, result.getMessage()));
            
            if (result.isSuccess()) {
                passed++;
            } else {
                failed++;
            }
        }
        
        plugin.getLogger().info(String.format("测试完成: %d 通过, %d 失败", passed, failed));
        plugin.getLogger().info("========================");
    }
    
    /**
     * 生成详细测试报告
     */
    private void generateDetailedTestReport(@NotNull Map<String, TestResult> results) {
        generateTestReport(results);
        
        // 输出失败测试的详细信息
        plugin.getLogger().info("=== 详细测试信息 ===");
        for (Map.Entry<String, TestResult> entry : results.entrySet()) {
            TestResult result = entry.getValue();
            if (!result.isSuccess()) {
                plugin.getLogger().warning(String.format("失败测试 [%s]: %s", 
                    entry.getKey(), result.getDetails()));
            }
        }
        plugin.getLogger().info("==================");
    }
    
    /**
     * 获取测试结果
     */
    @NotNull
    public Map<String, TestResult> getTestResults() {
        return new HashMap<>(testResults);
    }
    
    /**
     * 清理测试资源
     */
    public void cleanup() {
        testResults.clear();
        runningTests.clear();
        plugin.getLogger().info("测试管理器已清理");
    }
    
    /**
     * 获取可用的测试列表
     */
    @NotNull
    public List<String> getAvailableTests() {
        return Arrays.asList(
            "api_connection", "event_system", "channel_management", 
            "message_sending", "player_status", "command_execution",
            "filter_replacer", "discord_integration", "permission_system"
        );
    }
}

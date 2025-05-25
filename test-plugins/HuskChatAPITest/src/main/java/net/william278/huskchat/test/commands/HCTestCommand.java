package net.william278.huskchat.test.commands;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import net.william278.huskchat.test.TestResult;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * HuskChat测试命令处理器
 * 处理 /hctest 命令的执行和Tab补全
 */
@RequiredArgsConstructor
public class HCTestCommand implements CommandExecutor, TabCompleter {
    
    private final HuskChatAPITestPlugin plugin;
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("huskchat.test.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令！");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help", "?" -> sendHelpMessage(sender);
            case "basic" -> runBasicTests(sender);
            case "full" -> runFullTests(sender);
            case "single" -> runSingleTest(sender, args);
            case "status" -> showTestStatus(sender);
            case "report" -> showTestReport(sender);
            case "reset" -> resetTests(sender);
            case "list" -> listAvailableTests(sender);
            case "player" -> runPlayerTests(sender, args);
            default -> {
                sender.sendMessage(ChatColor.RED + "未知的子命令: " + subCommand);
                sendHelpMessage(sender);
            }
        }
        
        return true;
    }
    
    /**
     * 发送帮助信息
     */
    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== HuskChat API 测试命令 ===");
        sender.sendMessage(ChatColor.YELLOW + "/hctest help" + ChatColor.WHITE + " - 显示此帮助信息");
        sender.sendMessage(ChatColor.YELLOW + "/hctest basic" + ChatColor.WHITE + " - 运行基础测试套件");
        sender.sendMessage(ChatColor.YELLOW + "/hctest full" + ChatColor.WHITE + " - 运行完整测试套件");
        sender.sendMessage(ChatColor.YELLOW + "/hctest single <测试名>" + ChatColor.WHITE + " - 运行单个测试");
        sender.sendMessage(ChatColor.YELLOW + "/hctest player <玩家名>" + ChatColor.WHITE + " - 运行玩家相关测试");
        sender.sendMessage(ChatColor.YELLOW + "/hctest status" + ChatColor.WHITE + " - 显示测试状态");
        sender.sendMessage(ChatColor.YELLOW + "/hctest report" + ChatColor.WHITE + " - 显示测试报告");
        sender.sendMessage(ChatColor.YELLOW + "/hctest list" + ChatColor.WHITE + " - 列出可用测试");
        sender.sendMessage(ChatColor.YELLOW + "/hctest reset" + ChatColor.WHITE + " - 重置测试结果");
        sender.sendMessage(ChatColor.GOLD + "========================");
    }
    
    /**
     * 运行基础测试
     */
    private void runBasicTests(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "开始运行基础测试套件...");
        
        CompletableFuture<Void> future = plugin.getTestManager().runBasicTests()
            .thenAccept(results -> {
                sender.sendMessage(ChatColor.GREEN + "基础测试完成！");
                
                int passed = 0;
                int failed = 0;
                
                for (TestResult result : results.values()) {
                    if (result.isSuccess()) {
                        passed++;
                        sender.sendMessage(ChatColor.GREEN + "✅ " + result.getTestName() + ": " + result.getMessage());
                    } else {
                        failed++;
                        sender.sendMessage(ChatColor.RED + "❌ " + result.getTestName() + ": " + result.getMessage());
                    }
                }
                
                sender.sendMessage(String.format("%s测试结果: %s%d 通过%s, %s%d 失败",
                    ChatColor.GOLD, ChatColor.GREEN, passed, ChatColor.GOLD, 
                    ChatColor.RED, failed));
            })
            .exceptionally(throwable -> {
                sender.sendMessage(ChatColor.RED + "测试过程中发生错误: " + throwable.getMessage());
                plugin.getLogger().warning("基础测试异常", throwable);
                return null;
            });
    }
    
    /**
     * 运行完整测试
     */
    private void runFullTests(@NotNull CommandSender sender) {
        Player testPlayer = null;
        if (sender instanceof Player) {
            testPlayer = (Player) sender;
        }
        
        sender.sendMessage(ChatColor.GREEN + "开始运行完整测试套件...");
        
        CompletableFuture<Void> future = plugin.getTestManager().runFullTests(testPlayer)
            .thenAccept(results -> {
                sender.sendMessage(ChatColor.GREEN + "完整测试完成！");
                
                int passed = 0;
                int failed = 0;
                
                for (TestResult result : results.values()) {
                    if (result.isSuccess()) {
                        passed++;
                        sender.sendMessage(ChatColor.GREEN + "✅ " + result.getTestName() + ": " + result.getMessage());
                    } else {
                        failed++;
                        sender.sendMessage(ChatColor.RED + "❌ " + result.getTestName() + ": " + result.getMessage());
                        if (result.getDetails() != null) {
                            sender.sendMessage(ChatColor.GRAY + "   详情: " + result.getDetails());
                        }
                    }
                }
                
                sender.sendMessage(String.format("%s完整测试结果: %s%d 通过%s, %s%d 失败",
                    ChatColor.GOLD, ChatColor.GREEN, passed, ChatColor.GOLD, 
                    ChatColor.RED, failed));
            })
            .exceptionally(throwable -> {
                sender.sendMessage(ChatColor.RED + "测试过程中发生错误: " + throwable.getMessage());
                plugin.getLogger().warning("完整测试异常", throwable);
                return null;
            });
    }
    
    /**
     * 运行单个测试
     */
    private void runSingleTest(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "请指定要运行的测试名称！");
            sender.sendMessage(ChatColor.YELLOW + "使用 /hctest list 查看可用测试");
            return;
        }
        
        String testName = args[1];
        Player testPlayer = null;
        if (sender instanceof Player) {
            testPlayer = (Player) sender;
        }
        
        sender.sendMessage(ChatColor.GREEN + "开始运行测试: " + testName);
        
        CompletableFuture<Void> future = plugin.getTestManager().runSingleTest(testName, testPlayer)
            .thenAccept(result -> {
                if (result.isSuccess()) {
                    sender.sendMessage(ChatColor.GREEN + "✅ 测试 " + testName + " 通过: " + result.getMessage());
                } else {
                    sender.sendMessage(ChatColor.RED + "❌ 测试 " + testName + " 失败: " + result.getMessage());
                    if (result.getDetails() != null) {
                        sender.sendMessage(ChatColor.GRAY + "详情: " + result.getDetails());
                    }
                }
                
                if (result.getExecutionTimeMs() > 0) {
                    sender.sendMessage(ChatColor.GRAY + "执行时间: " + result.getExecutionTimeMs() + " ms");
                }
            })
            .exceptionally(throwable -> {
                sender.sendMessage(ChatColor.RED + "测试 " + testName + " 执行异常: " + throwable.getMessage());
                plugin.getLogger().warning("单个测试异常", throwable);
                return null;
            });
    }
    
    /**
     * 运行玩家相关测试
     */
    private void runPlayerTests(@NotNull CommandSender sender, @NotNull String[] args) {
        Player targetPlayer = null;
        
        if (args.length >= 2) {
            targetPlayer = plugin.getServer().getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "找不到玩家: " + args[1]);
                return;
            }
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "请指定要测试的玩家名称！");
            return;
        }
        
        sender.sendMessage(ChatColor.GREEN + "开始运行玩家 " + targetPlayer.getName() + " 的相关测试...");
        
        // 运行玩家状态测试
        plugin.getTestManager().runSingleTest("player_status", targetPlayer)
            .thenCompose(result -> {
                sender.sendMessage(result.isSuccess() ? 
                    ChatColor.GREEN + "✅ 玩家状态测试: " + result.getMessage() :
                    ChatColor.RED + "❌ 玩家状态测试: " + result.getMessage());
                
                // 运行频道切换测试
                return plugin.getChannelManagementTest().testPlayerChannelSwitch(targetPlayer);
            })
            .thenCompose(result -> {
                sender.sendMessage(result.isSuccess() ? 
                    ChatColor.GREEN + "✅ 频道切换测试: " + result.getMessage() :
                    ChatColor.RED + "❌ 频道切换测试: " + result.getMessage());
                
                // 运行消息发送测试
                return plugin.getMessageSendingTest().testPlayerMessageSending(targetPlayer);
            })
            .thenAccept(result -> {
                sender.sendMessage(result.isSuccess() ? 
                    ChatColor.GREEN + "✅ 消息发送测试: " + result.getMessage() :
                    ChatColor.RED + "❌ 消息发送测试: " + result.getMessage());
                
                sender.sendMessage(ChatColor.GOLD + "玩家 " + targetPlayer.getName() + " 的测试完成！");
            })
            .exceptionally(throwable -> {
                sender.sendMessage(ChatColor.RED + "玩家测试过程中发生错误: " + throwable.getMessage());
                plugin.getLogger().warning("玩家测试异常", throwable);
                return null;
            });
    }
    
    /**
     * 显示测试状态
     */
    private void showTestStatus(@NotNull CommandSender sender) {
        var testResults = plugin.getTestManager().getTestResults();
        
        if (testResults.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "还没有运行任何测试");
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== 测试状态 ===");
        
        int passed = 0;
        int failed = 0;
        
        for (TestResult result : testResults.values()) {
            if (result.isSuccess()) {
                passed++;
            } else {
                failed++;
            }
        }
        
        sender.sendMessage(String.format("%s总计: %d 个测试", ChatColor.WHITE, testResults.size()));
        sender.sendMessage(String.format("%s通过: %s%d", ChatColor.WHITE, ChatColor.GREEN, passed));
        sender.sendMessage(String.format("%s失败: %s%d", ChatColor.WHITE, ChatColor.RED, failed));
        sender.sendMessage(ChatColor.GOLD + "===============");
    }
    
    /**
     * 显示测试报告
     */
    private void showTestReport(@NotNull CommandSender sender) {
        var testResults = plugin.getTestManager().getTestResults();
        
        if (testResults.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "还没有运行任何测试");
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== 详细测试报告 ===");
        
        for (TestResult result : testResults.values()) {
            String status = result.isSuccess() ? ChatColor.GREEN + "✅" : ChatColor.RED + "❌";
            sender.sendMessage(String.format("%s %s: %s", 
                status, result.getTestName(), result.getMessage()));
            
            if (result.getDetails() != null && !result.getDetails().trim().isEmpty()) {
                sender.sendMessage(ChatColor.GRAY + "  详情: " + result.getDetails());
            }
            
            sender.sendMessage(ChatColor.GRAY + "  时间: " + result.getFormattedTimestamp() + 
                (result.getExecutionTimeMs() > 0 ? " (耗时: " + result.getExecutionTimeMs() + "ms)" : ""));
        }
        
        sender.sendMessage(ChatColor.GOLD + "==================");
    }
    
    /**
     * 重置测试结果
     */
    private void resetTests(@NotNull CommandSender sender) {
        plugin.getTestManager().getTestResults().clear();
        plugin.getEventSystemTest().resetCounters();
        sender.sendMessage(ChatColor.GREEN + "测试结果已重置");
    }
    
    /**
     * 列出可用测试
     */
    private void listAvailableTests(@NotNull CommandSender sender) {
        var availableTests = plugin.getTestManager().getAvailableTests();
        
        sender.sendMessage(ChatColor.GOLD + "=== 可用测试列表 ===");
        for (String testName : availableTests) {
            sender.sendMessage(ChatColor.YELLOW + "- " + testName);
        }
        sender.sendMessage(ChatColor.GOLD + "==================");
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                               @NotNull String alias, @NotNull String[] args) {
        
        if (!sender.hasPermission("huskchat.test.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("help", "basic", "full", "single", "player", 
                               "status", "report", "reset", "list");
        }
        
        if (args.length == 2) {
            if ("single".equals(args[0])) {
                return plugin.getTestManager().getAvailableTests();
            }
            
            if ("player".equals(args[0])) {
                return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
            }
        }
        
        return new ArrayList<>();
    }
}

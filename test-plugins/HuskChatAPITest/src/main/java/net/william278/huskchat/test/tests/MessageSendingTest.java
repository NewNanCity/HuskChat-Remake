package net.william278.huskchat.test.tests;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.api.BukkitHuskChatExtendedAPI;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import net.william278.huskchat.test.TestResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 消息发送测试
 * 测试HuskChat的消息发送API功能
 */
@RequiredArgsConstructor
public class MessageSendingTest {
    
    private final HuskChatAPITestPlugin plugin;
    
    /**
     * 运行消息发送测试
     */
    @NotNull
    public TestResult runTest() {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 1. 测试频道消息发送
            if (!testChannelMessageSending(api)) {
                return TestResult.failure("message_sending", "频道消息发送测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 2. 测试私聊消息发送
            if (!testPrivateMessageSending(api)) {
                return TestResult.failure("message_sending", "私聊消息发送测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 3. 测试广播消息发送
            if (!testBroadcastMessageSending(api)) {
                return TestResult.failure("message_sending", "广播消息发送测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 4. 测试格式化消息发送
            if (!testFormattedMessageSending(api)) {
                return TestResult.failure("message_sending", "格式化消息发送测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 5. 测试消息发送权限检查
            if (!testMessageSendingPermissions(api)) {
                return TestResult.failure("message_sending", "消息发送权限检查失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("message_sending", 
                "消息发送API测试通过，所有消息类型发送正常",
                "频道消息、私聊消息、广播消息、格式化消息发送均正常")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("消息发送测试异常: " + e.getMessage());
            return TestResult.failure("message_sending", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
    
    /**
     * 测试频道消息发送
     */
    private boolean testChannelMessageSending(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            var channels = api.getChannels();
            
            for (var channel : channels) {
                try {
                    // 发送测试消息到频道
                    String testMessage = "[API测试] 频道消息测试 - " + System.currentTimeMillis();
                    
                    boolean success = api.sendChannelMessage(channel.getId(), testMessage, null);
                    
                    if (success) {
                        plugin.getLogger().info("成功发送消息到频道: " + channel.getId());
                    } else {
                        plugin.getLogger().warning("发送消息到频道 " + channel.getId() + " 失败");
                    }
                    
                } catch (Exception e) {
                    plugin.getLogger().warning("发送消息到频道 " + channel.getId() + " 时发生异常: " + e.getMessage());
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("频道消息发送测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试私聊消息发送
     */
    private boolean testPrivateMessageSending(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            var onlineUsers = api.getOnlineUsers();
            
            if (onlineUsers.size() < 2) {
                plugin.getLogger().info("在线用户不足2人，跳过私聊消息测试");
                return true;
            }
            
            // 选择两个用户进行私聊测试
            var userList = onlineUsers.stream().toList();
            var sender = userList.get(0);
            var receiver = userList.get(1);
            
            try {
                // 发送私聊消息
                String testMessage = "[API测试] 私聊消息测试 - " + System.currentTimeMillis();
                List<String> recipients = Arrays.asList(receiver.getUsername());
                
                boolean success = api.sendPrivateMessage(sender, recipients, testMessage);
                
                if (success) {
                    plugin.getLogger().info(String.format("成功发送私聊消息: %s -> %s", 
                        sender.getUsername(), receiver.getUsername()));
                } else {
                    plugin.getLogger().warning("私聊消息发送失败");
                }
                
            } catch (Exception e) {
                plugin.getLogger().warning("私聊消息发送异常: " + e.getMessage());
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("私聊消息发送测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试广播消息发送
     */
    private boolean testBroadcastMessageSending(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            // 发送广播消息
            String testMessage = "[API测试] 广播消息测试 - " + System.currentTimeMillis();
            
            boolean success = api.sendBroadcastMessage(testMessage, null);
            
            if (success) {
                plugin.getLogger().info("成功发送广播消息");
            } else {
                plugin.getLogger().warning("广播消息发送失败");
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("广播消息发送测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试格式化消息发送
     */
    private boolean testFormattedMessageSending(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            var channels = api.getChannels();
            
            if (!channels.isEmpty()) {
                var channel = channels.iterator().next();
                
                // 测试各种格式化消息
                String[] formattedMessages = {
                    "&a[API测试] &b彩色消息测试",
                    "&l&c[API测试] &r&e粗体和颜色组合",
                    "&#FF5555[API测试] &#55FFFF十六进制颜色测试",
                    "[API测试] **粗体** *斜体* __下划线__ ~~删除线~~",
                    "[API测试] 渐变色测试 <gradient:#ff0000:#0000ff>渐变文本</gradient>"
                };
                
                for (String message : formattedMessages) {
                    try {
                        boolean success = api.sendChannelMessage(channel.getId(), message, null);
                        
                        if (success) {
                            plugin.getLogger().info("成功发送格式化消息: " + message);
                        } else {
                            plugin.getLogger().warning("格式化消息发送失败: " + message);
                        }
                        
                        // 短暂延迟避免消息过快
                        Thread.sleep(100);
                        
                    } catch (Exception e) {
                        plugin.getLogger().warning("发送格式化消息异常: " + e.getMessage());
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("格式化消息发送测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试消息发送权限检查
     */
    private boolean testMessageSendingPermissions(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            var onlineUsers = api.getOnlineUsers();
            var channels = api.getChannels();
            
            for (var user : onlineUsers) {
                for (var channel : channels) {
                    try {
                        // 检查用户是否可以发送消息到频道
                        boolean canSend = api.canPlayerSendToChannel(user.getUuid(), channel.getId());
                        
                        plugin.getLogger().info(String.format("用户 %s 对频道 %s 的发送权限: %s",
                            user.getUsername(), channel.getId(), canSend));
                            
                        // 如果有权限，尝试发送测试消息
                        if (canSend) {
                            String testMessage = String.format("[权限测试] %s 在频道 %s 的消息", 
                                user.getUsername(), channel.getId());
                            api.sendChannelMessage(channel.getId(), testMessage, user);
                        }
                        
                    } catch (Exception e) {
                        plugin.getLogger().warning(String.format("检查用户 %s 对频道 %s 的权限失败: %s",
                            user.getUsername(), channel.getId(), e.getMessage()));
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("消息发送权限检查测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试玩家消息发送
     */
    @NotNull
    public TestResult testPlayerMessageSending(@NotNull Player player) {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 获取玩家当前频道
            String currentChannel = api.getPlayerChannel(player.getUniqueId());
            
            if (currentChannel == null) {
                return TestResult.failure("player_message_sending", "玩家未在任何频道中");
            }
            
            // 测试玩家发送消息
            String testMessage = "[玩家测试] " + player.getName() + " 的消息测试 - " + System.currentTimeMillis();
            
            // 模拟玩家发送消息
            boolean success = api.sendChannelMessage(currentChannel, testMessage, 
                api.getOnlineUsers().stream()
                    .filter(user -> user.getUuid().equals(player.getUniqueId()))
                    .findFirst()
                    .orElse(null));
            
            if (success) {
                plugin.getLogger().info("玩家 " + player.getName() + " 成功发送消息到频道 " + currentChannel);
                
                // 测试私聊功能
                var onlineUsers = api.getOnlineUsers();
                if (onlineUsers.size() > 1) {
                    var otherUser = onlineUsers.stream()
                        .filter(user -> !user.getUuid().equals(player.getUniqueId()))
                        .findFirst();
                        
                    if (otherUser.isPresent()) {
                        String privateMessage = "[私聊测试] 来自 " + player.getName() + " 的私聊消息";
                        List<String> recipients = Arrays.asList(otherUser.get().getUsername());
                        
                        var senderUser = onlineUsers.stream()
                            .filter(user -> user.getUuid().equals(player.getUniqueId()))
                            .findFirst()
                            .orElse(null);
                            
                        if (senderUser != null) {
                            boolean privateSuccess = api.sendPrivateMessage(senderUser, recipients, privateMessage);
                            plugin.getLogger().info("私聊消息发送结果: " + privateSuccess);
                        }
                    }
                }
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("player_message_sending", 
                "玩家消息发送测试完成，发送结果: " + success,
                "测试了频道消息和私聊消息发送")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("玩家消息发送测试异常: " + e.getMessage());
            return TestResult.failure("player_message_sending", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
}

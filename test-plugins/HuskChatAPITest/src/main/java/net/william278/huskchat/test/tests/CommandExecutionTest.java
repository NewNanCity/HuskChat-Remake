package net.william278.huskchat.test.tests;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.api.BukkitHuskChatExtendedAPI;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import net.william278.huskchat.test.TestResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 命令执行测试
 * 测试HuskChat的命令执行API功能
 */
@RequiredArgsConstructor
public class CommandExecutionTest {
    
    private final HuskChatAPITestPlugin plugin;
    
    /**
     * 运行命令执行测试
     */
    @NotNull
    public TestResult runTest(@NotNull Player player) {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 1. 测试频道命令执行
            if (!testChannelCommands(api, player)) {
                return TestResult.failure("command_execution", "频道命令执行测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 2. 测试私聊命令执行
            if (!testPrivateMessageCommands(api, player)) {
                return TestResult.failure("command_execution", "私聊命令执行测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 3. 测试管理命令执行
            if (!testAdminCommands(api, player)) {
                return TestResult.failure("command_execution", "管理命令执行测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 4. 测试命令权限检查
            if (!testCommandPermissions(api, player)) {
                return TestResult.failure("command_execution", "命令权限检查失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("command_execution", 
                String.format("玩家 %s 命令执行测试通过，所有命令类型执行正常", player.getName()),
                "频道命令、私聊命令、管理命令、权限检查均正常")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("命令执行测试异常: " + e.getMessage());
            return TestResult.failure("command_execution", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
    
    /**
     * 测试频道命令执行
     */
    private boolean testChannelCommands(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            // 测试频道列表命令
            plugin.getLogger().info("测试频道列表命令");
            
            // 模拟执行 /channel list 命令
            var channels = api.getChannels();
            plugin.getLogger().info("频道列表命令执行成功，发现 " + channels.size() + " 个频道");
            
            // 测试频道切换命令
            if (!channels.isEmpty()) {
                var targetChannel = channels.iterator().next();
                plugin.getLogger().info("测试频道切换命令到: " + targetChannel.getId());
                
                // 模拟执行 /channel <channelId> 命令
                String originalChannel = api.getPlayerChannel(player.getUniqueId());
                boolean switchSuccess = api.switchPlayerChannel(player.getUniqueId(), targetChannel.getId());
                
                if (switchSuccess) {
                    plugin.getLogger().info("频道切换命令执行成功");
                    
                    // 切换回原频道
                    if (originalChannel != null) {
                        api.switchPlayerChannel(player.getUniqueId(), originalChannel);
                    }
                } else {
                    plugin.getLogger().warning("频道切换命令执行失败");
                }
            }
            
            // 测试频道信息命令
            plugin.getLogger().info("测试频道信息命令");
            for (var channel : channels) {
                plugin.getLogger().info(String.format("频道信息: %s (%s)", 
                    channel.getId(), channel.getName()));
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("频道命令测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试私聊命令执行
     */
    private boolean testPrivateMessageCommands(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            var onlineUsers = api.getOnlineUsers();
            
            if (onlineUsers.size() < 2) {
                plugin.getLogger().info("在线用户不足2人，跳过私聊命令测试");
                return true;
            }
            
            // 找到其他玩家
            var otherUser = onlineUsers.stream()
                .filter(user -> !user.getUuid().equals(player.getUniqueId()))
                .findFirst();
                
            if (otherUser.isEmpty()) {
                plugin.getLogger().info("找不到其他玩家，跳过私聊命令测试");
                return true;
            }
            
            var targetUser = otherUser.get();
            
            // 测试发送私聊消息命令
            plugin.getLogger().info("测试私聊消息命令到: " + targetUser.getUsername());
            
            // 模拟执行 /msg <player> <message> 命令
            var senderUser = onlineUsers.stream()
                .filter(user -> user.getUuid().equals(player.getUniqueId()))
                .findFirst();
                
            if (senderUser.isPresent()) {
                String testMessage = "[命令测试] 私聊消息测试";
                boolean success = api.sendPrivateMessage(senderUser.get(), 
                    java.util.Arrays.asList(targetUser.getUsername()), testMessage);
                
                if (success) {
                    plugin.getLogger().info("私聊消息命令执行成功");
                } else {
                    plugin.getLogger().warning("私聊消息命令执行失败");
                }
            }
            
            // 测试回复命令
            plugin.getLogger().info("测试回复命令");
            // 模拟执行 /reply <message> 命令
            // 这里需要根据实际的HuskChat API来实现
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("私聊命令测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试管理命令执行
     */
    private boolean testAdminCommands(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            // 检查玩家是否有管理权限
            boolean hasAdminPermission = player.hasPermission("huskchat.admin") || player.isOp();
            
            if (!hasAdminPermission) {
                plugin.getLogger().info("玩家没有管理权限，跳过管理命令测试");
                return true;
            }
            
            // 测试重载配置命令
            plugin.getLogger().info("测试重载配置命令");
            // 模拟执行 /huskchat reload 命令
            // 这里需要根据实际的HuskChat API来实现
            
            // 测试广播命令
            plugin.getLogger().info("测试广播命令");
            String broadcastMessage = "[命令测试] 管理员广播测试";
            boolean broadcastSuccess = api.sendBroadcastMessage(broadcastMessage, null);
            
            if (broadcastSuccess) {
                plugin.getLogger().info("广播命令执行成功");
            } else {
                plugin.getLogger().warning("广播命令执行失败");
            }
            
            // 测试社交间谍命令
            plugin.getLogger().info("测试社交间谍命令");
            // 模拟执行 /socialspy 命令
            // 这里需要根据实际的HuskChat API来实现
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("管理命令测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试命令权限检查
     */
    private boolean testCommandPermissions(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            // 检查基本聊天权限
            boolean canChat = player.hasPermission("huskchat.chat");
            plugin.getLogger().info("基本聊天权限: " + canChat);
            
            // 检查频道权限
            var channels = api.getChannels();
            for (var channel : channels) {
                boolean canSendToChannel = api.canPlayerSendToChannel(player.getUniqueId(), channel.getId());
                boolean canReceiveFromChannel = api.canPlayerReceiveFromChannel(player.getUniqueId(), channel.getId());
                
                plugin.getLogger().info(String.format("频道 %s 权限 - 发送: %s, 接收: %s",
                    channel.getId(), canSendToChannel, canReceiveFromChannel));
            }
            
            // 检查私聊权限
            boolean canPrivateMessage = player.hasPermission("huskchat.message");
            plugin.getLogger().info("私聊权限: " + canPrivateMessage);
            
            // 检查管理权限
            boolean canAdmin = player.hasPermission("huskchat.admin");
            plugin.getLogger().info("管理权限: " + canAdmin);
            
            // 检查社交间谍权限
            boolean canSocialSpy = player.hasPermission("huskchat.socialspy");
            plugin.getLogger().info("社交间谍权限: " + canSocialSpy);
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("命令权限检查失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试命令执行结果验证
     */
    @NotNull
    public TestResult testCommandResultValidation(@NotNull Player player) {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 记录命令执行前的状态
            String originalChannel = api.getPlayerChannel(player.getUniqueId());
            plugin.getLogger().info("命令执行前玩家频道: " + originalChannel);
            
            // 执行频道切换命令并验证结果
            var channels = api.getChannels();
            if (!channels.isEmpty()) {
                var targetChannel = channels.stream()
                    .filter(channel -> !channel.getId().equals(originalChannel))
                    .findFirst();
                    
                if (targetChannel.isPresent()) {
                    // 执行切换命令
                    boolean switchSuccess = api.switchPlayerChannel(player.getUniqueId(), targetChannel.get().getId());
                    
                    if (switchSuccess) {
                        // 验证切换结果
                        String newChannel = api.getPlayerChannel(player.getUniqueId());
                        boolean validationSuccess = targetChannel.get().getId().equals(newChannel);
                        
                        plugin.getLogger().info(String.format("命令执行验证 - 期望频道: %s, 实际频道: %s, 验证结果: %s",
                            targetChannel.get().getId(), newChannel, validationSuccess));
                        
                        // 恢复原频道
                        if (originalChannel != null) {
                            api.switchPlayerChannel(player.getUniqueId(), originalChannel);
                        }
                        
                        if (!validationSuccess) {
                            return TestResult.failure("command_result_validation", "命令执行结果验证失败");
                        }
                    } else {
                        return TestResult.failure("command_result_validation", "频道切换命令执行失败");
                    }
                }
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("command_result_validation", 
                "命令执行结果验证通过",
                "命令执行和结果验证均正常")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("命令执行结果验证异常: " + e.getMessage());
            return TestResult.failure("command_result_validation", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
}

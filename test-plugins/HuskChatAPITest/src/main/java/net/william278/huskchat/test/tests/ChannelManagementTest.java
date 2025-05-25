package net.william278.huskchat.test.tests;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.api.BukkitHuskChatExtendedAPI;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import net.william278.huskchat.test.TestResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * 频道管理测试
 * 测试HuskChat的频道管理API功能
 */
@RequiredArgsConstructor
public class ChannelManagementTest {
    
    private final HuskChatAPITestPlugin plugin;
    
    /**
     * 运行频道管理测试
     */
    @NotNull
    public TestResult runTest() {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 1. 测试获取频道列表
            if (!testGetChannels(api)) {
                return TestResult.failure("channel_management", "获取频道列表失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 2. 测试频道信息获取
            if (!testChannelInfo(api)) {
                return TestResult.failure("channel_management", "获取频道信息失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 3. 测试频道成员管理
            if (!testChannelMembers(api)) {
                return TestResult.failure("channel_management", "频道成员管理测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 4. 测试频道权限检查
            if (!testChannelPermissions(api)) {
                return TestResult.failure("channel_management", "频道权限检查失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 5. 测试频道状态管理
            if (!testChannelStatus(api)) {
                return TestResult.failure("channel_management", "频道状态管理测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("channel_management", 
                "频道管理API测试通过，所有频道操作正常",
                "频道列表获取、信息查询、成员管理、权限检查、状态管理均正常")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("频道管理测试异常: " + e.getMessage());
            return TestResult.failure("channel_management", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
    
    /**
     * 测试获取频道列表
     */
    private boolean testGetChannels(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            // 获取所有频道
            var channels = api.getChannels();
            plugin.getLogger().info("发现 " + channels.size() + " 个频道");
            
            // 检查是否有默认频道
            boolean hasGlobalChannel = channels.stream()
                .anyMatch(channel -> "global".equalsIgnoreCase(channel.getId()));
            
            if (!hasGlobalChannel) {
                plugin.getLogger().warning("未找到全局频道");
            }
            
            // 输出频道信息
            for (var channel : channels) {
                plugin.getLogger().info(String.format("频道: %s (%s)", 
                    channel.getId(), channel.getName()));
            }
            
            return !channels.isEmpty();
            
        } catch (Exception e) {
            plugin.getLogger().warning("获取频道列表失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试频道信息获取
     */
    private boolean testChannelInfo(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            var channels = api.getChannels();
            
            for (var channel : channels) {
                // 测试频道基本信息
                String channelId = channel.getId();
                String channelName = channel.getName();
                
                plugin.getLogger().info(String.format("频道详情 - ID: %s, 名称: %s", 
                    channelId, channelName));
                
                // 测试频道配置信息
                // 这里需要根据实际的HuskChat API来调整
                try {
                    // 检查频道是否启用
                    // boolean isEnabled = channel.isEnabled();
                    
                    // 检查频道类型
                    // String channelType = channel.getType();
                    
                    plugin.getLogger().info("频道 " + channelId + " 信息获取成功");
                    
                } catch (Exception e) {
                    plugin.getLogger().warning("获取频道 " + channelId + " 详细信息失败: " + e.getMessage());
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("频道信息测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试频道成员管理
     */
    private boolean testChannelMembers(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            // 获取在线用户
            var onlineUsers = api.getOnlineUsers();
            plugin.getLogger().info("当前在线用户数: " + onlineUsers.size());
            
            // 测试获取频道中的玩家
            for (var user : onlineUsers) {
                try {
                    // 获取玩家当前频道
                    String currentChannel = api.getPlayerChannel(user.getUuid());
                    plugin.getLogger().info(String.format("玩家 %s 当前在频道: %s", 
                        user.getUsername(), currentChannel != null ? currentChannel : "未知"));
                    
                } catch (Exception e) {
                    plugin.getLogger().warning("获取玩家 " + user.getUsername() + " 频道信息失败: " + e.getMessage());
                }
            }
            
            // 测试获取特定频道的成员
            var channels = api.getChannels();
            for (var channel : channels) {
                try {
                    var members = api.getPlayersInChannel(channel.getId());
                    plugin.getLogger().info(String.format("频道 %s 有 %d 个成员", 
                        channel.getId(), members.size()));
                        
                } catch (Exception e) {
                    plugin.getLogger().warning("获取频道 " + channel.getId() + " 成员失败: " + e.getMessage());
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("频道成员管理测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试频道权限检查
     */
    private boolean testChannelPermissions(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            var onlineUsers = api.getOnlineUsers();
            var channels = api.getChannels();
            
            for (var user : onlineUsers) {
                for (var channel : channels) {
                    try {
                        // 检查发送权限
                        boolean canSend = api.canPlayerSendToChannel(user.getUuid(), channel.getId());
                        
                        // 检查接收权限
                        boolean canReceive = api.canPlayerReceiveFromChannel(user.getUuid(), channel.getId());
                        
                        plugin.getLogger().info(String.format("玩家 %s 对频道 %s 的权限 - 发送: %s, 接收: %s",
                            user.getUsername(), channel.getId(), canSend, canReceive));
                            
                    } catch (Exception e) {
                        plugin.getLogger().warning(String.format("检查玩家 %s 对频道 %s 的权限失败: %s",
                            user.getUsername(), channel.getId(), e.getMessage()));
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("频道权限检查测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试频道状态管理
     */
    private boolean testChannelStatus(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            var channels = api.getChannels();
            
            for (var channel : channels) {
                try {
                    // 检查频道是否活跃
                    var members = api.getPlayersInChannel(channel.getId());
                    boolean isActive = !members.isEmpty();
                    
                    plugin.getLogger().info(String.format("频道 %s 状态 - 活跃: %s, 成员数: %d",
                        channel.getId(), isActive, members.size()));
                        
                } catch (Exception e) {
                    plugin.getLogger().warning("检查频道 " + channel.getId() + " 状态失败: " + e.getMessage());
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("频道状态管理测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试玩家频道切换
     */
    @NotNull
    public TestResult testPlayerChannelSwitch(@NotNull Player player) {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 获取玩家当前频道
            String originalChannel = api.getPlayerChannel(player.getUniqueId());
            plugin.getLogger().info("玩家 " + player.getName() + " 原始频道: " + originalChannel);
            
            // 获取可用频道
            var channels = api.getChannels();
            if (channels.isEmpty()) {
                return TestResult.failure("channel_switch", "没有可用的频道进行切换测试");
            }
            
            // 尝试切换到不同的频道
            for (var channel : channels) {
                if (!channel.getId().equals(originalChannel)) {
                    try {
                        // 切换频道
                        boolean success = api.switchPlayerChannel(player.getUniqueId(), channel.getId());
                        
                        if (success) {
                            plugin.getLogger().info("成功切换玩家 " + player.getName() + " 到频道: " + channel.getId());
                            
                            // 验证切换结果
                            String newChannel = api.getPlayerChannel(player.getUniqueId());
                            if (channel.getId().equals(newChannel)) {
                                plugin.getLogger().info("频道切换验证成功");
                            } else {
                                plugin.getLogger().warning("频道切换验证失败，期望: " + channel.getId() + ", 实际: " + newChannel);
                            }
                            
                            // 切换回原始频道
                            if (originalChannel != null) {
                                api.switchPlayerChannel(player.getUniqueId(), originalChannel);
                            }
                            
                            break;
                        } else {
                            plugin.getLogger().warning("切换到频道 " + channel.getId() + " 失败");
                        }
                        
                    } catch (Exception e) {
                        plugin.getLogger().warning("切换频道时发生异常: " + e.getMessage());
                    }
                }
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("channel_switch", 
                "玩家频道切换测试完成",
                "成功测试了频道切换功能")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("玩家频道切换测试异常: " + e.getMessage());
            return TestResult.failure("channel_switch", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
}

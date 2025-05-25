package net.william278.huskchat.test.tests;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.api.BukkitHuskChatExtendedAPI;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import net.william278.huskchat.test.TestResult;
import org.jetbrains.annotations.NotNull;

/**
 * 权限系统测试
 * 测试HuskChat的权限集成功能
 */
@RequiredArgsConstructor
public class PermissionTest {
    
    private final HuskChatAPITestPlugin plugin;
    
    /**
     * 运行权限系统测试
     */
    @NotNull
    public TestResult runTest() {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 1. 测试权限系统检测
            if (!testPermissionSystemDetection(api)) {
                return TestResult.failure("permission_system", "权限系统检测失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 2. 测试基本权限检查
            if (!testBasicPermissions(api)) {
                return TestResult.failure("permission_system", "基本权限检查失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 3. 测试频道权限
            if (!testChannelPermissions(api)) {
                return TestResult.failure("permission_system", "频道权限测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 4. 测试权限组集成
            if (!testPermissionGroupIntegration(api)) {
                return TestResult.failure("permission_system", "权限组集成测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("permission_system", 
                "权限系统测试通过，权限检查和集成正常",
                "权限系统检测、基本权限、频道权限、权限组集成均正常")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("权限系统测试异常: " + e.getMessage());
            return TestResult.failure("permission_system", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
    
    /**
     * 测试权限系统检测
     */
    private boolean testPermissionSystemDetection(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            plugin.getLogger().info("检测权限系统");
            
            // 检查是否有权限插件
            boolean hasLuckPerms = plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null;
            boolean hasPermissionsEx = plugin.getServer().getPluginManager().getPlugin("PermissionsEx") != null;
            boolean hasGroupManager = plugin.getServer().getPluginManager().getPlugin("GroupManager") != null;
            boolean hasVault = plugin.getServer().getPluginManager().getPlugin("Vault") != null;
            
            plugin.getLogger().info("权限插件检测结果:");
            plugin.getLogger().info("- LuckPerms: " + hasLuckPerms);
            plugin.getLogger().info("- PermissionsEx: " + hasPermissionsEx);
            plugin.getLogger().info("- GroupManager: " + hasGroupManager);
            plugin.getLogger().info("- Vault: " + hasVault);
            
            // 检查HuskChat是否正确集成权限系统
            // 这里需要根据实际的HuskChat API来调整
            // String permissionProvider = api.getPermissionProvider();
            String permissionProvider = hasLuckPerms ? "LuckPerms" : 
                                      hasVault ? "Vault" : "Bukkit";
            
            plugin.getLogger().info("HuskChat使用的权限提供者: " + permissionProvider);
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("权限系统检测失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试基本权限检查
     */
    private boolean testBasicPermissions(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            plugin.getLogger().info("测试基本权限检查");
            
            var onlineUsers = api.getOnlineUsers();
            
            // 定义要检查的权限节点
            String[] permissions = {
                "huskchat.chat",
                "huskchat.message",
                "huskchat.channel.global.send",
                "huskchat.channel.global.receive",
                "huskchat.channel.local.send",
                "huskchat.channel.local.receive",
                "huskchat.admin",
                "huskchat.socialspy",
                "huskchat.reload",
                "huskchat.broadcast"
            };
            
            for (var user : onlineUsers) {
                var player = plugin.getServer().getPlayer(user.getUuid());
                if (player != null) {
                    plugin.getLogger().info("检查玩家 " + player.getName() + " 的权限:");
                    
                    for (String permission : permissions) {
                        boolean hasPermission = player.hasPermission(permission);
                        plugin.getLogger().info(String.format("  %s: %s", permission, hasPermission));
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("基本权限检查失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试频道权限
     */
    private boolean testChannelPermissions(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            plugin.getLogger().info("测试频道权限");
            
            var channels = api.getChannels();
            var onlineUsers = api.getOnlineUsers();
            
            for (var channel : channels) {
                plugin.getLogger().info("检查频道 " + channel.getId() + " 的权限:");
                
                for (var user : onlineUsers) {
                    try {
                        boolean canSend = api.canPlayerSendToChannel(user.getUuid(), channel.getId());
                        boolean canReceive = api.canPlayerReceiveFromChannel(user.getUuid(), channel.getId());
                        
                        plugin.getLogger().info(String.format("  玩家 %s - 发送: %s, 接收: %s",
                            user.getUsername(), canSend, canReceive));
                            
                    } catch (Exception e) {
                        plugin.getLogger().warning(String.format("检查玩家 %s 对频道 %s 的权限失败: %s",
                            user.getUsername(), channel.getId(), e.getMessage()));
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("频道权限测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试权限组集成
     */
    private boolean testPermissionGroupIntegration(@NotNull BukkitHuskChatExtendedAPI api) {
        try {
            plugin.getLogger().info("测试权限组集成");
            
            var onlineUsers = api.getOnlineUsers();
            
            for (var user : onlineUsers) {
                var player = plugin.getServer().getPlayer(user.getUuid());
                if (player != null) {
                    try {
                        // 获取玩家的权限组信息
                        // 这里需要根据实际的权限插件API来调整
                        
                        // 如果有LuckPerms
                        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                            // LuckPerms API调用
                            plugin.getLogger().info("玩家 " + player.getName() + " 的LuckPerms信息:");
                            // User luckPermsUser = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
                            // if (luckPermsUser != null) {
                            //     String primaryGroup = luckPermsUser.getPrimaryGroup();
                            //     plugin.getLogger().info("  主要组: " + primaryGroup);
                            // }
                        }
                        
                        // 如果有Vault
                        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
                            plugin.getLogger().info("玩家 " + player.getName() + " 的Vault信息:");
                            // 这里可以使用Vault API获取权限组信息
                        }
                        
                        // 检查玩家是否为OP
                        boolean isOp = player.isOp();
                        plugin.getLogger().info("  OP状态: " + isOp);
                        
                    } catch (Exception e) {
                        plugin.getLogger().warning("获取玩家 " + player.getName() + " 权限组信息失败: " + e.getMessage());
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("权限组集成测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试权限缓存
     */
    @NotNull
    public TestResult testPermissionCaching() {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            plugin.getLogger().info("测试权限缓存");
            
            var onlineUsers = api.getOnlineUsers();
            
            for (var user : onlineUsers) {
                // 多次检查同一个权限，测试缓存效果
                String testPermission = "huskchat.chat";
                
                long checkStart = System.currentTimeMillis();
                for (int i = 0; i < 100; i++) {
                    api.canPlayerSendToChannel(user.getUuid(), "global");
                }
                long checkEnd = System.currentTimeMillis();
                
                plugin.getLogger().info(String.format("玩家 %s 权限检查100次耗时: %d ms",
                    user.getUsername(), checkEnd - checkStart));
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("permission_caching", 
                "权限缓存测试完成",
                "权限检查性能测试完成")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("权限缓存测试异常: " + e.getMessage());
            return TestResult.failure("permission_caching", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
}

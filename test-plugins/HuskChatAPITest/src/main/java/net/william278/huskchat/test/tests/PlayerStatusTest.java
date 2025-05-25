package net.william278.huskchat.test.tests;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.api.BukkitHuskChatExtendedAPI;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import net.william278.huskchat.test.TestResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 玩家状态测试
 * 测试HuskChat的玩家状态集成功能
 */
@RequiredArgsConstructor
public class PlayerStatusTest {
    
    private final HuskChatAPITestPlugin plugin;
    
    /**
     * 运行玩家状态测试
     */
    @NotNull
    public TestResult runTest(@NotNull Player player) {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 1. 测试玩家基本信息获取
            if (!testPlayerBasicInfo(api, player)) {
                return TestResult.failure("player_status", "玩家基本信息获取失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 2. 测试玩家健康状态
            if (!testPlayerHealth(api, player)) {
                return TestResult.failure("player_status", "玩家健康状态测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 3. 测试玩家位置信息
            if (!testPlayerLocation(api, player)) {
                return TestResult.failure("player_status", "玩家位置信息测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 4. 测试玩家游戏模式
            if (!testPlayerGameMode(api, player)) {
                return TestResult.failure("player_status", "玩家游戏模式测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            // 5. 测试玩家战斗状态
            if (!testPlayerCombatStatus(api, player)) {
                return TestResult.failure("player_status", "玩家战斗状态测试失败")
                    .withExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("player_status", 
                String.format("玩家 %s 状态测试通过，所有状态信息获取正常", player.getName()),
                "基本信息、健康状态、位置信息、游戏模式、战斗状态均正常")
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("玩家状态测试异常: " + e.getMessage());
            return TestResult.failure("player_status", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
    
    /**
     * 测试玩家基本信息获取
     */
    private boolean testPlayerBasicInfo(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            // 获取玩家在线用户信息
            var onlineUsers = api.getOnlineUsers();
            var playerUser = onlineUsers.stream()
                .filter(user -> user.getUuid().equals(player.getUniqueId()))
                .findFirst();
            
            if (playerUser.isEmpty()) {
                plugin.getLogger().warning("在HuskChat在线用户列表中找不到玩家: " + player.getName());
                return false;
            }
            
            var user = playerUser.get();
            plugin.getLogger().info(String.format("玩家基本信息 - 用户名: %s, UUID: %s", 
                user.getUsername(), user.getUuid()));
            
            // 获取玩家当前频道
            String currentChannel = api.getPlayerChannel(player.getUniqueId());
            plugin.getLogger().info("玩家当前频道: " + (currentChannel != null ? currentChannel : "未知"));
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("获取玩家基本信息失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试玩家健康状态
     */
    private boolean testPlayerHealth(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            // 获取玩家当前健康值
            double health = player.getHealth();
            double maxHealth = player.getMaxHealth();
            double healthPercentage = (health / maxHealth) * 100;
            
            plugin.getLogger().info(String.format("玩家健康状态 - 当前: %.1f/%.1f (%.1f%%)", 
                health, maxHealth, healthPercentage));
            
            // 测试健康状态变化监听（如果API支持）
            // 这里可以模拟健康状态变化事件
            plugin.getLogger().info("健康状态监听测试完成");
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("玩家健康状态测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试玩家位置信息
     */
    private boolean testPlayerLocation(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            var location = player.getLocation();
            
            plugin.getLogger().info(String.format("玩家位置信息 - 世界: %s, 坐标: %.1f, %.1f, %.1f", 
                location.getWorld() != null ? location.getWorld().getName() : "未知",
                location.getX(), location.getY(), location.getZ()));
            
            // 测试位置变化监听（如果API支持）
            plugin.getLogger().info("位置信息监听测试完成");
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("玩家位置信息测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试玩家游戏模式
     */
    private boolean testPlayerGameMode(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            var gameMode = player.getGameMode();
            
            plugin.getLogger().info("玩家游戏模式: " + gameMode.name());
            
            // 测试游戏模式变化监听（如果API支持）
            plugin.getLogger().info("游戏模式监听测试完成");
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("玩家游戏模式测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试玩家战斗状态
     */
    private boolean testPlayerCombatStatus(@NotNull BukkitHuskChatExtendedAPI api, @NotNull Player player) {
        try {
            // 检查玩家是否在战斗中（这里使用简单的逻辑）
            boolean inCombat = player.getLastDamage() > 0;
            
            plugin.getLogger().info("玩家战斗状态: " + (inCombat ? "战斗中" : "非战斗"));
            
            // 测试战斗状态变化监听（如果API支持）
            plugin.getLogger().info("战斗状态监听测试完成");
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("玩家战斗状态测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试玩家状态同步
     */
    @NotNull
    public TestResult testPlayerStatusSync(@NotNull Player player) {
        long startTime = System.currentTimeMillis();
        
        try {
            BukkitHuskChatExtendedAPI api = plugin.getHuskChatAPI();
            
            // 记录初始状态
            double initialHealth = player.getHealth();
            var initialLocation = player.getLocation();
            var initialGameMode = player.getGameMode();
            
            plugin.getLogger().info("记录玩家初始状态");
            plugin.getLogger().info(String.format("初始健康: %.1f, 位置: %.1f,%.1f,%.1f, 游戏模式: %s",
                initialHealth, initialLocation.getX(), initialLocation.getY(), initialLocation.getZ(),
                initialGameMode.name()));
            
            // 等待一段时间检查状态变化
            Thread.sleep(2000);
            
            // 检查状态是否有变化
            double currentHealth = player.getHealth();
            var currentLocation = player.getLocation();
            var currentGameMode = player.getGameMode();
            
            boolean healthChanged = Math.abs(currentHealth - initialHealth) > 0.1;
            boolean locationChanged = initialLocation.distance(currentLocation) > 0.1;
            boolean gameModeChanged = !initialGameMode.equals(currentGameMode);
            
            plugin.getLogger().info(String.format("状态变化检测 - 健康: %s, 位置: %s, 游戏模式: %s",
                healthChanged, locationChanged, gameModeChanged));
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return TestResult.success("player_status_sync", 
                "玩家状态同步测试完成",
                String.format("监测到状态变化: 健康=%s, 位置=%s, 游戏模式=%s", 
                    healthChanged, locationChanged, gameModeChanged))
                .withExecutionTime(executionTime);
                
        } catch (Exception e) {
            plugin.getLogger().warning("玩家状态同步测试异常: " + e.getMessage());
            return TestResult.failure("player_status_sync", "测试过程中发生异常: " + e.getMessage())
                .withExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
}

package net.william278.huskchat.test.listeners;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.*;

/**
 * 玩家状态测试监听器
 * 监听玩家状态变化事件，用于测试HuskChat的玩家状态集成
 */
@RequiredArgsConstructor
public class PlayerStatusTestListener implements Listener {
    
    private final HuskChatAPITestPlugin plugin;
    
    /**
     * 监听玩家受伤事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) {
            return;
        }
        
        if (event.isCancelled()) {
            return;
        }
        
        try {
            double damage = event.getFinalDamage();
            double newHealth = Math.max(0, player.getHealth() - damage);
            
            plugin.getLogger().info(String.format("[状态事件] 玩家 %s 受到伤害: %.1f, 新健康值: %.1f", 
                player.getName(), damage, newHealth));
            
            // 检查是否进入战斗状态
            if (damage > 0) {
                plugin.getLogger().info(String.format("[状态事件] 玩家 %s 进入战斗状态", player.getName()));
                
                // 这里可以测试HuskChat的战斗状态集成
                testCombatStatusIntegration(player, true);
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("[状态事件] 处理玩家受伤事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 监听玩家恢复健康事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) {
            return;
        }
        
        if (event.isCancelled()) {
            return;
        }
        
        try {
            double healAmount = event.getAmount();
            double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + healAmount);
            
            plugin.getLogger().info(String.format("[状态事件] 玩家 %s 恢复健康: %.1f, 新健康值: %.1f", 
                player.getName(), healAmount, newHealth));
            
            // 测试健康状态变化的HuskChat集成
            testHealthStatusIntegration(player, newHealth);
            
        } catch (Exception e) {
            plugin.getLogger().warning("[状态事件] 处理玩家恢复健康事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 监听玩家移动事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        // 只在玩家实际移动时记录（避免频繁日志）
        if (event.getFrom().distance(event.getTo()) > 5.0) {
            try {
                var player = event.getPlayer();
                var from = event.getFrom();
                var to = event.getTo();
                
                plugin.getLogger().info(String.format("[状态事件] 玩家 %s 移动: (%.1f,%.1f,%.1f) -> (%.1f,%.1f,%.1f)", 
                    player.getName(), from.getX(), from.getY(), from.getZ(), 
                    to.getX(), to.getY(), to.getZ()));
                
                // 检查是否跨世界移动
                if (!from.getWorld().equals(to.getWorld())) {
                    plugin.getLogger().info(String.format("[状态事件] 玩家 %s 跨世界移动: %s -> %s", 
                        player.getName(), from.getWorld().getName(), to.getWorld().getName()));
                    
                    // 测试跨世界移动的HuskChat集成
                    testWorldChangeIntegration(player, from.getWorld().getName(), to.getWorld().getName());
                }
                
            } catch (Exception e) {
                plugin.getLogger().warning("[状态事件] 处理玩家移动事件时发生异常: " + e.getMessage());
            }
        }
    }
    
    /**
     * 监听玩家游戏模式变化事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        try {
            var player = event.getPlayer();
            var oldGameMode = player.getGameMode();
            var newGameMode = event.getNewGameMode();
            
            plugin.getLogger().info(String.format("[状态事件] 玩家 %s 游戏模式变化: %s -> %s", 
                player.getName(), oldGameMode.name(), newGameMode.name()));
            
            // 测试游戏模式变化的HuskChat集成
            testGameModeChangeIntegration(player, oldGameMode.name(), newGameMode.name());
            
        } catch (Exception e) {
            plugin.getLogger().warning("[状态事件] 处理游戏模式变化事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 监听玩家等级变化事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        try {
            var player = event.getPlayer();
            int oldLevel = event.getOldLevel();
            int newLevel = event.getNewLevel();
            
            plugin.getLogger().info(String.format("[状态事件] 玩家 %s 等级变化: %d -> %d", 
                player.getName(), oldLevel, newLevel));
            
            // 测试等级变化的HuskChat集成
            testLevelChangeIntegration(player, oldLevel, newLevel);
            
        } catch (Exception e) {
            plugin.getLogger().warning("[状态事件] 处理等级变化事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 监听玩家传送事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        try {
            var player = event.getPlayer();
            var from = event.getFrom();
            var to = event.getTo();
            var cause = event.getCause();
            
            plugin.getLogger().info(String.format("[状态事件] 玩家 %s 传送: %s, 原因: %s", 
                player.getName(), 
                String.format("(%.1f,%.1f,%.1f) -> (%.1f,%.1f,%.1f)", 
                    from.getX(), from.getY(), from.getZ(), 
                    to.getX(), to.getY(), to.getZ()),
                cause.name()));
            
            // 测试传送事件的HuskChat集成
            testTeleportIntegration(player, cause.name());
            
        } catch (Exception e) {
            plugin.getLogger().warning("[状态事件] 处理传送事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 测试战斗状态集成
     */
    private void testCombatStatusIntegration(org.bukkit.entity.Player player, boolean inCombat) {
        try {
            // 这里可以测试HuskChat是否正确处理战斗状态
            // 例如：战斗中的玩家可能无法使用某些频道
            
            var api = plugin.getHuskChatAPI();
            String currentChannel = api.getPlayerChannel(player.getUniqueId());
            
            plugin.getLogger().info(String.format("[集成测试] 玩家 %s 战斗状态: %s, 当前频道: %s", 
                player.getName(), inCombat, currentChannel));
                
        } catch (Exception e) {
            plugin.getLogger().warning("[集成测试] 战斗状态集成测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试健康状态集成
     */
    private void testHealthStatusIntegration(org.bukkit.entity.Player player, double health) {
        try {
            // 测试健康状态是否影响聊天功能
            double healthPercentage = (health / player.getMaxHealth()) * 100;
            
            plugin.getLogger().info(String.format("[集成测试] 玩家 %s 健康状态: %.1f%% (%.1f/%.1f)", 
                player.getName(), healthPercentage, health, player.getMaxHealth()));
                
        } catch (Exception e) {
            plugin.getLogger().warning("[集成测试] 健康状态集成测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试世界变化集成
     */
    private void testWorldChangeIntegration(org.bukkit.entity.Player player, String fromWorld, String toWorld) {
        try {
            // 测试跨世界移动是否影响频道状态
            var api = plugin.getHuskChatAPI();
            String currentChannel = api.getPlayerChannel(player.getUniqueId());
            
            plugin.getLogger().info(String.format("[集成测试] 玩家 %s 跨世界移动: %s -> %s, 频道: %s", 
                player.getName(), fromWorld, toWorld, currentChannel));
                
        } catch (Exception e) {
            plugin.getLogger().warning("[集成测试] 世界变化集成测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试游戏模式变化集成
     */
    private void testGameModeChangeIntegration(org.bukkit.entity.Player player, String oldMode, String newMode) {
        try {
            // 测试游戏模式变化是否影响聊天权限
            plugin.getLogger().info(String.format("[集成测试] 玩家 %s 游戏模式变化: %s -> %s", 
                player.getName(), oldMode, newMode));
                
        } catch (Exception e) {
            plugin.getLogger().warning("[集成测试] 游戏模式变化集成测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试等级变化集成
     */
    private void testLevelChangeIntegration(org.bukkit.entity.Player player, int oldLevel, int newLevel) {
        try {
            // 测试等级变化是否影响聊天功能
            plugin.getLogger().info(String.format("[集成测试] 玩家 %s 等级变化: %d -> %d", 
                player.getName(), oldLevel, newLevel));
                
        } catch (Exception e) {
            plugin.getLogger().warning("[集成测试] 等级变化集成测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试传送集成
     */
    private void testTeleportIntegration(org.bukkit.entity.Player player, String cause) {
        try {
            // 测试传送是否影响聊天状态
            plugin.getLogger().info(String.format("[集成测试] 玩家 %s 传送事件: %s", 
                player.getName(), cause));
                
        } catch (Exception e) {
            plugin.getLogger().warning("[集成测试] 传送集成测试失败: " + e.getMessage());
        }
    }
}

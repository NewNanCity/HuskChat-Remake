package net.william278.huskchat.test.listeners;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 聊天事件测试监听器
 * 监听和记录各种聊天相关事件，用于测试HuskChat的事件集成
 */
@RequiredArgsConstructor
public class ChatEventTestListener implements Listener {
    
    private final HuskChatAPITestPlugin plugin;
    
    /**
     * 监听玩家聊天事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        try {
            String playerName = event.getPlayer().getName();
            String message = event.getMessage();
            
            plugin.getLogger().info(String.format("[聊天事件] 玩家 %s 发送消息: %s", playerName, message));
            
            // 检查是否为测试消息
            if (message.contains("[API测试]") || message.contains("[测试]")) {
                plugin.getLogger().info("[聊天事件] 检测到API测试消息");
                
                // 记录测试消息统计
                plugin.getEventSystemTest().resetCounters();
            }
            
            // 测试消息格式化
            if (message.contains("&") || message.contains("#")) {
                plugin.getLogger().info("[聊天事件] 检测到格式化代码");
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("[聊天事件] 处理聊天事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 监听玩家命令事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        try {
            String playerName = event.getPlayer().getName();
            String command = event.getMessage();
            
            // 只记录HuskChat相关命令
            if (isHuskChatCommand(command)) {
                plugin.getLogger().info(String.format("[命令事件] 玩家 %s 执行HuskChat命令: %s", 
                    playerName, command));
                
                // 分析命令类型
                if (command.startsWith("/channel") || command.startsWith("/ch")) {
                    plugin.getLogger().info("[命令事件] 检测到频道命令");
                } else if (command.startsWith("/msg") || command.startsWith("/tell") || command.startsWith("/whisper")) {
                    plugin.getLogger().info("[命令事件] 检测到私聊命令");
                } else if (command.startsWith("/reply") || command.startsWith("/r")) {
                    plugin.getLogger().info("[命令事件] 检测到回复命令");
                } else if (command.startsWith("/huskchat")) {
                    plugin.getLogger().info("[命令事件] 检测到HuskChat管理命令");
                }
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("[命令事件] 处理命令事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 监听玩家加入事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            String playerName = event.getPlayer().getName();
            plugin.getLogger().info(String.format("[加入事件] 玩家 %s 加入服务器", playerName));
            
            // 延迟检查玩家的HuskChat状态
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                try {
                    var api = plugin.getHuskChatAPI();
                    String playerChannel = api.getPlayerChannel(event.getPlayer().getUniqueId());
                    
                    plugin.getLogger().info(String.format("[加入事件] 玩家 %s 的默认频道: %s", 
                        playerName, playerChannel != null ? playerChannel : "未知"));
                        
                } catch (Exception e) {
                    plugin.getLogger().warning("[加入事件] 检查玩家频道时发生异常: " + e.getMessage());
                }
            }, 20L); // 1秒后检查
            
        } catch (Exception e) {
            plugin.getLogger().warning("[加入事件] 处理加入事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 监听玩家退出事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            String playerName = event.getPlayer().getName();
            plugin.getLogger().info(String.format("[退出事件] 玩家 %s 退出服务器", playerName));
            
            // 检查玩家退出前的状态
            try {
                var api = plugin.getHuskChatAPI();
                String playerChannel = api.getPlayerChannel(event.getPlayer().getUniqueId());
                
                plugin.getLogger().info(String.format("[退出事件] 玩家 %s 退出前的频道: %s", 
                    playerName, playerChannel != null ? playerChannel : "未知"));
                    
            } catch (Exception e) {
                plugin.getLogger().warning("[退出事件] 检查玩家状态时发生异常: " + e.getMessage());
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("[退出事件] 处理退出事件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 检查是否为HuskChat相关命令
     */
    private boolean isHuskChatCommand(String command) {
        String lowerCommand = command.toLowerCase();
        
        return lowerCommand.startsWith("/channel") ||
               lowerCommand.startsWith("/ch ") ||
               lowerCommand.startsWith("/msg ") ||
               lowerCommand.startsWith("/tell ") ||
               lowerCommand.startsWith("/whisper ") ||
               lowerCommand.startsWith("/reply") ||
               lowerCommand.startsWith("/r ") ||
               lowerCommand.startsWith("/huskchat") ||
               lowerCommand.startsWith("/socialspy") ||
               lowerCommand.startsWith("/localspy") ||
               lowerCommand.startsWith("/broadcast");
    }
}

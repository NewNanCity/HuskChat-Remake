# HuskChat Remake 增强示例插件

这是一个展示HuskChat Remake新增玩家状态集成功能的完整示例插件。

## 插件功能

- 基于生命值的聊天限制
- 基于位置的区域聊天
- 战斗状态聊天限制
- 玩家状态监控和管理
- 命令执行API演示
- 死亡/重生消息自定义

## 完整代码

### plugin.yml

```yaml
name: HuskChatEnhancedExample
version: 2.0.0
main: com.example.enhanced.HuskChatEnhancedPlugin
api-version: 1.19
depend: [HuskChat]

commands:
  playerstatus:
    description: 查看或设置玩家状态
    usage: /playerstatus [player] [status] [value]
    permission: huskchat.enhanced.status
  
  nearbychat:
    description: 向附近玩家发送消息
    usage: /nearbychat <radius> <message>
    permission: huskchat.enhanced.nearby
    
  combatmode:
    description: 切换战斗模式
    usage: /combatmode [on|off]
    permission: huskchat.enhanced.combat

permissions:
  huskchat.enhanced.status:
    description: 管理玩家状态
    default: op
  huskchat.enhanced.nearby:
    description: 使用附近聊天
    default: true
  huskchat.enhanced.combat:
    description: 切换战斗模式
    default: true
  huskchat.enhanced.lowhealth.bypass:
    description: 绕过低血量聊天限制
    default: false
```

### 主插件类

```java
package com.example.enhanced;

import net.william278.huskchat.api.HuskChatExtendedAPI;
import net.william278.huskchat.event.*;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HuskChatEnhancedPlugin extends JavaPlugin implements Listener {
    
    private HuskChatExtendedAPI huskChatAPI;
    private final Map<UUID, Long> combatTimers = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastRegions = new ConcurrentHashMap<>();
    
    @Override
    public void onEnable() {
        try {
            // 获取HuskChat API
            huskChatAPI = HuskChatExtendedAPI.getInstance();
            getLogger().info("成功连接到HuskChat扩展API");
            
            // 注册事件监听器
            registerEventListeners();
            registerBukkitEvents();
            
            // 启动定时任务
            startPeriodicTasks();
            
            getLogger().info("HuskChat增强示例插件已启用！");
        } catch (Exception e) {
            getLogger().severe("无法连接到HuskChat API: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    private void registerEventListeners() {
        // 监听聊天消息 - 添加生命值和位置限制
        huskChatAPI.registerChatMessageListener(this::onChatMessage);
        
        // 监听玩家生命值变化
        huskChatAPI.registerPlayerHealthChangeListener(this::onHealthChange);
        
        // 监听玩家位置变化
        huskChatAPI.registerPlayerLocationChangeListener(this::onLocationChange);
        
        // 监听玩家状态变化
        huskChatAPI.registerPlayerStatusChangeListener(this::onStatusChange);
        
        // 监听玩家死亡
        huskChatAPI.registerPlayerDeathListener(this::onPlayerDeath);
        
        // 监听玩家重生
        huskChatAPI.registerPlayerRespawnListener(this::onPlayerRespawn);
        
        // 监听命令执行
        huskChatAPI.registerChatCommandListener(this::onChatCommand);
    }
    
    private void registerBukkitEvents() {
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    private void startPeriodicTasks() {
        // 每秒检查战斗状态
        Bukkit.getScheduler().runTaskTimer(this, this::checkCombatStatus, 20L, 20L);
        
        // 每5秒检查玩家状态
        Bukkit.getScheduler().runTaskTimer(this, this::checkPlayerStatuses, 100L, 100L);
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        OnlineUser sender = event.getSender();
        String channelId = event.getChannelId();
        
        // 检查聊天条件
        HuskChatExtendedAPI.ChatConditionResult result = huskChatAPI.checkChatConditions(sender, channelId);
        if (!result.isAllowed()) {
            event.setCancelled(true);
            sender.sendMessage("§c无法发送消息: " + result.getReason());
            return;
        }
        
        // 添加状态前缀
        String statusPrefix = getStatusPrefix(sender);
        if (!statusPrefix.isEmpty()) {
            event.setMessage(statusPrefix + " " + event.getMessage());
        }
        
        // 基于位置的消息处理
        if (channelId.equals("local")) {
            handleLocalChat(event);
        }
    }
    
    private void onHealthChange(PlayerHealthChangeEvent event) {
        OnlineUser player = event.getPlayer();
        double newHealth = event.getNewHealth();
        double maxHealth = event.getMaxHealth();
        
        // 检查是否进入低血量状态
        if (newHealth <= maxHealth * 0.2 && event.getPreviousHealth() > maxHealth * 0.2) {
            player.sendMessage("§c⚠ 警告: 你的生命值过低，某些频道可能无法使用！");
            
            // 更新状态
            huskChatAPI.updatePlayerStatus(player, 
                PlayerStatusChangeEvent.StatusType.CUSTOM, 
                "low_health", 
                "Health dropped below 20%", 
                -1);
        }
        
        // 检查是否恢复正常血量
        if (newHealth > maxHealth * 0.2 && event.getPreviousHealth() <= maxHealth * 0.2) {
            player.sendMessage("§a生命值已恢复，聊天限制已解除。");
            
            // 移除低血量状态
            huskChatAPI.updatePlayerStatus(player, 
                PlayerStatusChangeEvent.StatusType.CUSTOM, 
                null, 
                "Health recovered", 
                -1);
        }
    }
    
    private void onLocationChange(PlayerLocationChangeEvent event) {
        OnlineUser player = event.getPlayer();
        String newRegion = event.getNewLocation().getRegionId();
        String previousRegion = lastRegions.get(player.getUuid());
        
        if (previousRegion != null && !previousRegion.equals(newRegion)) {
            // 玩家进入新区域
            player.sendMessage("§7你进入了新区域: §b" + newRegion);
            
            // 检查是否有区域专属频道
            String regionChannel = "region_" + newRegion.replace(":", "_");
            if (huskChatAPI.getChannels().contains(regionChannel)) {
                huskChatAPI.switchPlayerChannel(player, regionChannel, 
                    ChannelSwitchEvent.SwitchReason.API_CALL);
                player.sendMessage("§a已自动切换到区域频道: " + regionChannel);
            }
        }
        
        lastRegions.put(player.getUuid(), newRegion);
    }
    
    private void onStatusChange(PlayerStatusChangeEvent event) {
        OnlineUser player = event.getPlayer();
        PlayerStatusChangeEvent.StatusType statusType = event.getStatusType();
        Object newValue = event.getNewValue();
        
        getLogger().info(String.format("玩家 %s 的状态 %s 变更为: %s", 
            player.getUsername(), statusType.getKey(), newValue));
        
        // 根据状态变化执行特定逻辑
        switch (statusType) {
            case COMBAT:
                if ((Boolean) newValue) {
                    player.sendMessage("§c你进入了战斗状态！某些聊天功能可能受限。");
                } else {
                    player.sendMessage("§a你已脱离战斗状态。");
                }
                break;
            case AWAY:
                if ((Boolean) newValue) {
                    player.sendMessage("§7你现在处于离开状态。");
                } else {
                    player.sendMessage("§a欢迎回来！");
                }
                break;
        }
    }
    
    private void onPlayerDeath(net.william278.huskchat.event.PlayerDeathEvent event) {
        OnlineUser player = event.getPlayer();
        OnlineUser killer = event.getKiller();
        
        // 自定义死亡消息
        if (killer != null) {
            String customMessage = String.format("§c%s 被 %s 击败了！", 
                player.getUsername(), killer.getUsername());
            event.setDeathMessage(customMessage);
        } else {
            String customMessage = String.format("§7%s 不幸身亡", player.getUsername());
            event.setDeathMessage(customMessage);
        }
        
        // 设置死亡消息发送到特定频道
        event.setDeathMessageChannel("global");
        
        // 清除战斗状态
        huskChatAPI.updatePlayerStatus(player, 
            PlayerStatusChangeEvent.StatusType.COMBAT, 
            false, 
            "Player died", 
            -1);
    }
    
    private void onPlayerRespawn(net.william278.huskchat.event.PlayerRespawnEvent event) {
        OnlineUser player = event.getPlayer();
        
        // 发送重生消息
        event.setSendRespawnMessage(true);
        event.setRespawnMessageChannel("global");
        
        // 延迟发送欢迎回来消息
        Bukkit.getScheduler().runTaskLater(this, () -> {
            player.sendMessage("§a你已重生！生命值和状态已重置。");
        }, 20L);
    }
    
    private void onChatCommand(ChatCommandEvent event) {
        OnlineUser player = event.getPlayer();
        String command = event.getCommand();
        
        if (event.getPhase() == ChatCommandEvent.ExecutionPhase.PRE) {
            // 命令执行前检查
            if (!huskChatAPI.validateCommandPermission(player, command)) {
                event.setCancelled(true);
                player.sendMessage("§c你没有权限执行此命令！");
                return;
            }
            
            // 记录命令执行
            getLogger().info(String.format("玩家 %s 执行命令: %s %s", 
                player.getUsername(), command, String.join(" ", event.getArgs())));
        } else {
            // 命令执行后处理
            if (event.isSuccessful()) {
                getLogger().info(String.format("命令执行成功: %s", command));
            } else {
                getLogger().warning(String.format("命令执行失败: %s - %s", 
                    command, event.getFailureReason()));
            }
        }
    }
    
    // Bukkit事件监听
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player) {
            OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
            
            // 设置战斗状态
            combatTimers.put(player.getUniqueId(), System.currentTimeMillis());
            huskChatAPI.updatePlayerStatus(huskPlayer, 
                PlayerStatusChangeEvent.StatusType.COMBAT, 
                true, 
                "Entered combat", 
                15000); // 15秒战斗状态
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() || 
            event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            
            OnlineUser player = huskChatAPI.adaptPlayer(event.getPlayer());
            PlayerLocationChangeEvent.PlayerLocation from = 
                huskChatAPI.createLocation(event.getFrom());
            PlayerLocationChangeEvent.PlayerLocation to = 
                huskChatAPI.createLocation(event.getTo());
            
            // 触发位置变化事件
            huskChatAPI.firePlayerLocationChangeEvent(player, from, to, 
                PlayerLocationChangeEvent.MovementReason.PLAYER_MOVEMENT);
        }
    }
    
    private void checkCombatStatus() {
        long currentTime = System.currentTimeMillis();
        combatTimers.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > 15000) { // 15秒后脱离战斗
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
                    huskChatAPI.updatePlayerStatus(huskPlayer, 
                        PlayerStatusChangeEvent.StatusType.COMBAT, 
                        false, 
                        "Combat timeout", 
                        -1);
                }
                return true;
            }
            return false;
        });
    }
    
    private void checkPlayerStatuses() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
            PlayerInfo info = huskChatAPI.getPlayerInfo(huskPlayer);
            
            // 检查AFK状态（示例：5分钟无移动）
            // 这里可以添加更复杂的AFK检测逻辑
        }
    }
    
    private String getStatusPrefix(OnlineUser player) {
        StringBuilder prefix = new StringBuilder();
        
        if (player.isInCombat()) {
            prefix.append("§c[战斗]");
        }
        if (player.isLowHealth()) {
            prefix.append("§4[低血]");
        }
        if (player.isAway()) {
            prefix.append("§7[离开]");
        }
        
        return prefix.toString();
    }
    
    private void handleLocalChat(ChatMessageEvent event) {
        OnlineUser sender = event.getSender();
        
        // 获取附近的玩家
        List<OnlineUser> nearbyPlayers = huskChatAPI.getNearbyPlayers(sender, 50.0);
        
        // 这里可以实现基于距离的消息传递逻辑
        // 实际实现需要扩展频道系统
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "playerstatus":
                return handlePlayerStatusCommand(sender, args);
            case "nearbychat":
                return handleNearbyChatCommand(sender, args);
            case "combatmode":
                return handleCombatModeCommand(sender, args);
            default:
                return false;
        }
    }
    
    private boolean handlePlayerStatusCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("huskchat.enhanced.status")) {
            sender.sendMessage("§c你没有权限使用此命令！");
            return true;
        }
        
        if (args.length == 0) {
            // 显示自己的状态
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c控制台无法查看状态！");
                return true;
            }
            
            Player player = (Player) sender;
            OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
            showPlayerStatus(sender, huskPlayer);
        } else if (args.length == 1) {
            // 显示其他玩家状态
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§c玩家 " + args[0] + " 不在线！");
                return true;
            }
            
            OnlineUser huskTarget = huskChatAPI.adaptPlayer(target);
            showPlayerStatus(sender, huskTarget);
        } else if (args.length >= 3) {
            // 设置玩家状态
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§c玩家 " + args[0] + " 不在线！");
                return true;
            }
            
            OnlineUser huskTarget = huskChatAPI.adaptPlayer(target);
            String statusKey = args[1];
            String value = args[2];
            
            PlayerStatusChangeEvent.StatusType statusType = 
                PlayerStatusChangeEvent.StatusType.fromKey(statusKey);
            
            huskChatAPI.updatePlayerStatus(huskTarget, statusType, value, 
                "Admin command", -1)
                .thenAccept(success -> {
                    if (success) {
                        sender.sendMessage("§a成功设置玩家状态！");
                    } else {
                        sender.sendMessage("§c设置状态失败！");
                    }
                });
        }
        
        return true;
    }
    
    private boolean handleNearbyChatCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c此命令只能由玩家使用！");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§c用法: /nearbychat <半径> <消息>");
            return true;
        }
        
        try {
            double radius = Double.parseDouble(args[0]);
            String message = String.join(" ", 
                java.util.Arrays.copyOfRange(args, 1, args.length));
            
            OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
            List<OnlineUser> nearbyPlayers = huskChatAPI.getNearbyPlayers(huskPlayer, radius);
            
            String formattedMessage = String.format("§e[附近] %s: §f%s", 
                player.getName(), message);
            
            // 发送给附近的玩家
            for (OnlineUser nearbyPlayer : nearbyPlayers) {
                nearbyPlayer.sendMessage(formattedMessage);
            }
            
            sender.sendMessage(String.format("§a消息已发送给 %d 个附近的玩家", 
                nearbyPlayers.size()));
            
        } catch (NumberFormatException e) {
            sender.sendMessage("§c无效的半径值！");
        }
        
        return true;
    }
    
    private boolean handleCombatModeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c此命令只能由玩家使用！");
            return true;
        }
        
        OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
        boolean currentCombat = huskPlayer.isInCombat();
        
        if (args.length == 0) {
            // 切换状态
            boolean newCombat = !currentCombat;
            huskChatAPI.updatePlayerStatus(huskPlayer, 
                PlayerStatusChangeEvent.StatusType.COMBAT, 
                newCombat, 
                "Manual toggle", 
                -1);
            
            sender.sendMessage(newCombat ? "§c战斗模式已开启" : "§a战斗模式已关闭");
        } else {
            // 设置特定状态
            boolean newCombat = args[0].equalsIgnoreCase("on") || 
                               args[0].equalsIgnoreCase("true");
            
            huskChatAPI.updatePlayerStatus(huskPlayer, 
                PlayerStatusChangeEvent.StatusType.COMBAT, 
                newCombat, 
                "Manual set", 
                -1);
            
            sender.sendMessage(newCombat ? "§c战斗模式已开启" : "§a战斗模式已关闭");
        }
        
        return true;
    }
    
    private void showPlayerStatus(CommandSender sender, OnlineUser player) {
        PlayerInfo info = huskChatAPI.getPlayerInfo(player);
        
        sender.sendMessage("§6=== " + player.getUsername() + " 的状态 ===");
        sender.sendMessage("§7生命值: §f" + String.format("%.1f/%.1f (%.1f%%)", 
            info.getHealth(), info.getMaxHealth(), info.getHealthPercentage()));
        sender.sendMessage("§7饥饿值: §f" + info.getFoodLevel() + "/20 (" + 
            String.format("%.1f%%", info.getFoodPercentage()) + ")");
        sender.sendMessage("§7经验等级: §f" + info.getExperienceLevel());
        sender.sendMessage("§7游戏模式: §f" + info.getGameMode().getName());
        sender.sendMessage("§7位置: §f" + info.getLocation().getRegionId());
        sender.sendMessage("§7在线时长: §f" + formatTime(info.getSessionTime()));
        
        sender.sendMessage("§7状态:");
        sender.sendMessage("  §7战斗: " + (info.isInCombat() ? "§c是" : "§a否"));
        sender.sendMessage("  §7离开: " + (info.isAway() ? "§c是" : "§a否"));
        sender.sendMessage("  §7禁言: " + (info.isMuted() ? "§c是" : "§a否"));
        sender.sendMessage("  §7潜行: " + (info.isSneaking() ? "§c是" : "§a否"));
        sender.sendMessage("  §7飞行: " + (info.isFlying() ? "§c是" : "§a否"));
        sender.sendMessage("  §7隐身: " + (info.isVanished() ? "§c是" : "§a否"));
    }
    
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds % 60);
        } else {
            return String.format("%d秒", seconds);
        }
    }
    
    @Override
    public void onDisable() {
        // 清理资源
        if (huskChatAPI != null) {
            huskChatAPI.unregisterChatMessageListener(this::onChatMessage);
            huskChatAPI.unregisterPlayerHealthChangeListener(this::onHealthChange);
            huskChatAPI.unregisterPlayerLocationChangeListener(this::onLocationChange);
            huskChatAPI.unregisterPlayerStatusChangeListener(this::onStatusChange);
            huskChatAPI.unregisterPlayerDeathListener(this::onPlayerDeath);
            huskChatAPI.unregisterPlayerRespawnListener(this::onPlayerRespawn);
            huskChatAPI.unregisterChatCommandListener(this::onChatCommand);
        }
        
        getLogger().info("HuskChat增强示例插件已禁用！");
    }
}
```

## 功能演示

### 1. 生命值聊天限制
- 当玩家生命值低于20%时，某些频道会被限制
- 可以通过权限 `huskchat.enhanced.lowhealth.bypass` 绕过限制

### 2. 基于位置的聊天
- 玩家进入新区域时自动切换到区域频道
- `/nearbychat` 命令向附近玩家发送消息

### 3. 战斗状态管理
- 玩家受到攻击时自动进入战斗状态
- 战斗状态下某些聊天功能受限
- `/combatmode` 命令手动切换战斗状态

### 4. 玩家状态监控
- `/playerstatus` 命令查看详细的玩家状态
- 实时监控生命值、位置、游戏模式等信息

### 5. 自定义死亡/重生消息
- 根据死亡原因自定义死亡消息
- 重生时发送欢迎消息

## 配置建议

在HuskChat配置中添加以下频道配置：

```yaml
channels:
  global:
    # 全局频道配置
    restrictions:
      low_health: false  # 不限制低血量玩家
      combat: false      # 不限制战斗状态玩家
  
  staff:
    # 员工频道配置
    restrictions:
      low_health: true   # 限制低血量玩家
      combat: true       # 限制战斗状态玩家
```

这个增强示例展示了HuskChat Remake新增功能的强大能力，为开发者提供了丰富的玩家状态集成和聊天管理功能。

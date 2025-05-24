# HuskChat Remake API 示例插件

这是一个完整的示例插件，展示了如何使用HuskChat Remake的扩展API。

## 插件功能

- 为VIP玩家的消息添加特效
- 自动欢迎新玩家
- 频道切换通知
- 聊天统计功能
- 自定义命令

## 完整代码

### plugin.yml

```yaml
name: HuskChatExample
version: 1.0.0
main: com.example.huskchatexample.HuskChatExamplePlugin
api-version: 1.19
depend: [HuskChat]

commands:
  chatstats:
    description: 查看聊天统计
    usage: /chatstats [player]
    permission: huskchatexample.chatstats
  
  forcechannel:
    description: 强制玩家切换频道
    usage: /forcechannel <player> <channel>
    permission: huskchatexample.forcechannel

permissions:
  huskchatexample.vip:
    description: VIP聊天特效
    default: false
  huskchatexample.chatstats:
    description: 查看聊天统计
    default: op
  huskchatexample.forcechannel:
    description: 强制频道切换
    default: op
```

### 主插件类

```java
package com.example.huskchatexample;

import net.william278.huskchat.api.HuskChatExtendedAPI;
import net.william278.huskchat.event.*;
import net.william278.huskchat.user.OnlineUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HuskChatExamplePlugin extends JavaPlugin {
    
    private HuskChatExtendedAPI huskChatAPI;
    private final Map<UUID, ChatStats> playerStats = new ConcurrentHashMap<>();
    
    @Override
    public void onEnable() {
        try {
            // 获取HuskChat API
            huskChatAPI = HuskChatExtendedAPI.getInstance();
            getLogger().info("成功连接到HuskChat API");
            
            // 注册事件监听器
            registerEventListeners();
            
            getLogger().info("HuskChat示例插件已启用！");
        } catch (Exception e) {
            getLogger().severe("无法连接到HuskChat API: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    private void registerEventListeners() {
        // 监听聊天消息
        huskChatAPI.registerChatMessageListener(this::onChatMessage);
        
        // 监听频道切换
        huskChatAPI.registerChannelSwitchListener(this::onChannelSwitch);
        
        // 监听玩家加入频道
        huskChatAPI.registerPlayerJoinChannelListener(this::onPlayerJoinChannel);
        
        // 监听私聊消息
        huskChatAPI.registerPrivateMessageListener(this::onPrivateMessage);
        
        // 监听消息过滤
        huskChatAPI.registerMessageFilterListener(this::onMessageFilter);
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        OnlineUser sender = event.getSender();
        String message = event.getMessage();
        
        // 记录聊天统计
        updateChatStats(sender.getUuid(), ChatStats.MessageType.CHANNEL);
        
        // VIP特效
        if (sender.hasPermission("huskchatexample.vip")) {
            // 添加彩虹特效
            String rainbowMessage = addRainbowEffect(message);
            event.setMessage("✨ " + rainbowMessage + " ✨");
        }
        
        // 检测特殊关键词
        if (message.toLowerCase().contains("help")) {
            // 延迟发送帮助信息，避免干扰原消息
            Bukkit.getScheduler().runTaskLater(this, () -> {
                sender.sendMessage("§6需要帮助吗？输入 /help 查看可用命令！");
            }, 20L);
        }
    }
    
    private void onChannelSwitch(ChannelSwitchEvent event) {
        OnlineUser player = event.getPlayer();
        String newChannel = event.getNewChannelId();
        String previousChannel = event.getPreviousChannelId();
        
        // 记录频道切换
        getLogger().info(String.format("%s 从频道 %s 切换到 %s (原因: %s)", 
            player.getUsername(), previousChannel, newChannel, event.getReason()));
        
        // 特殊频道欢迎消息
        switch (newChannel) {
            case "staff":
                if (player.hasPermission("huskchat.channel.staff.receive")) {
                    player.sendMessage("§c欢迎来到员工频道！请遵守管理规定。");
                }
                break;
            case "global":
                player.sendMessage("§a欢迎来到全局频道！");
                break;
            case "local":
                player.sendMessage("§e欢迎来到本地频道！");
                break;
        }
    }
    
    private void onPlayerJoinChannel(PlayerJoinChannelEvent event) {
        OnlineUser player = event.getPlayer();
        String channelId = event.getChannelId();
        
        // 新玩家欢迎
        if (event.getReason() == PlayerJoinChannelEvent.JoinReason.FIRST_LOGIN) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.sendMessage("§6=== 欢迎来到服务器！===");
                player.sendMessage("§7你现在在 §b" + channelId + " §7频道");
                player.sendMessage("§7输入 §f/channel <频道名> §7来切换频道");
                player.sendMessage("§7输入 §f/msg <玩家> <消息> §7来私聊");
                player.sendMessage("§6========================");
            }, 60L); // 3秒后发送
        }
    }
    
    private void onPrivateMessage(PrivateMessageEvent event) {
        OnlineUser sender = event.getSender();
        
        // 记录私聊统计
        updateChatStats(sender.getUuid(), ChatStats.MessageType.PRIVATE);
        
        // 记录私聊日志（管理员可见）
        String recipients = event.getRecipients().stream()
            .map(OnlineUser::getUsername)
            .reduce((a, b) -> a + ", " + b)
            .orElse("无");
        
        getLogger().info(String.format("私聊: %s -> %s: %s", 
            sender.getUsername(), recipients, event.getMessage()));
    }
    
    private void onMessageFilter(MessageFilterEvent event) {
        if (event.getFilterType() == MessageFilterEvent.FilterType.PROFANITY) {
            OnlineUser sender = event.getSender();
            
            // 记录违规行为
            getLogger().warning(String.format("玩家 %s 尝试发送不当内容: %s", 
                sender.getUsername(), event.getOriginalMessage()));
            
            // 给玩家警告
            sender.sendMessage("§c⚠ 警告: 请注意你的言辞！");
            
            // 更新违规统计
            updateChatStats(sender.getUuid(), ChatStats.MessageType.VIOLATION);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "chatstats":
                return handleChatStatsCommand(sender, args);
            case "forcechannel":
                return handleForceChannelCommand(sender, args);
            default:
                return false;
        }
    }
    
    private boolean handleChatStatsCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("huskchatexample.chatstats")) {
            sender.sendMessage("§c你没有权限使用此命令！");
            return true;
        }
        
        if (args.length == 0) {
            // 显示自己的统计
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c控制台无法查看自己的统计！");
                return true;
            }
            
            Player player = (Player) sender;
            showChatStats(sender, player.getUniqueId(), player.getName());
        } else {
            // 显示其他玩家的统计
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§c玩家 " + args[0] + " 不在线！");
                return true;
            }
            
            showChatStats(sender, target.getUniqueId(), target.getName());
        }
        
        return true;
    }
    
    private boolean handleForceChannelCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("huskchatexample.forcechannel")) {
            sender.sendMessage("§c你没有权限使用此命令！");
            return true;
        }
        
        if (args.length != 2) {
            sender.sendMessage("§c用法: /forcechannel <玩家> <频道>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§c玩家 " + args[0] + " 不在线！");
            return true;
        }
        
        String channelId = args[1];
        OnlineUser targetUser = huskChatAPI.adaptPlayer(target);
        
        huskChatAPI.switchPlayerChannel(targetUser, channelId, ChannelSwitchEvent.SwitchReason.ADMIN_FORCE)
            .thenAccept(success -> {
                if (success) {
                    sender.sendMessage("§a成功将 " + target.getName() + " 切换到频道 " + channelId);
                    target.sendMessage("§6你被管理员切换到了频道 " + channelId);
                } else {
                    sender.sendMessage("§c切换失败！可能是频道不存在或玩家没有权限。");
                }
            });
        
        return true;
    }
    
    private void showChatStats(CommandSender sender, UUID playerId, String playerName) {
        ChatStats stats = playerStats.getOrDefault(playerId, new ChatStats());
        
        sender.sendMessage("§6=== " + playerName + " 的聊天统计 ===");
        sender.sendMessage("§7频道消息: §f" + stats.channelMessages);
        sender.sendMessage("§7私聊消息: §f" + stats.privateMessages);
        sender.sendMessage("§7违规次数: §f" + stats.violations);
        sender.sendMessage("§7总消息数: §f" + stats.getTotalMessages());
    }
    
    private void updateChatStats(UUID playerId, ChatStats.MessageType type) {
        ChatStats stats = playerStats.computeIfAbsent(playerId, k -> new ChatStats());
        stats.increment(type);
    }
    
    private String addRainbowEffect(String message) {
        // 简单的彩虹效果实现
        String[] colors = {"§c", "§6", "§e", "§a", "§b", "§d"};
        StringBuilder rainbow = new StringBuilder();
        
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c != ' ') {
                rainbow.append(colors[i % colors.length]);
            }
            rainbow.append(c);
        }
        
        return rainbow.toString();
    }
    
    @Override
    public void onDisable() {
        // 清理资源
        if (huskChatAPI != null) {
            huskChatAPI.unregisterChatMessageListener(this::onChatMessage);
            huskChatAPI.unregisterChannelSwitchListener(this::onChannelSwitch);
            huskChatAPI.unregisterPrivateMessageListener(this::onPrivateMessage);
            huskChatAPI.unregisterMessageFilterListener(this::onMessageFilter);
        }
        
        getLogger().info("HuskChat示例插件已禁用！");
    }
    
    // 聊天统计数据类
    private static class ChatStats {
        int channelMessages = 0;
        int privateMessages = 0;
        int violations = 0;
        
        enum MessageType {
            CHANNEL, PRIVATE, VIOLATION
        }
        
        void increment(MessageType type) {
            switch (type) {
                case CHANNEL:
                    channelMessages++;
                    break;
                case PRIVATE:
                    privateMessages++;
                    break;
                case VIOLATION:
                    violations++;
                    break;
            }
        }
        
        int getTotalMessages() {
            return channelMessages + privateMessages;
        }
    }
}
```

## 使用说明

1. 将此代码保存为一个独立的插件项目
2. 确保HuskChat Remake已安装并启用
3. 编译并安装此示例插件
4. 重启服务器

## 功能演示

### VIP特效
给玩家 `huskchatexample.vip` 权限，他们的消息将显示彩虹色彩和星星特效。

### 聊天统计
- `/chatstats` - 查看自己的聊天统计
- `/chatstats <玩家>` - 查看其他玩家的统计

### 强制频道切换
- `/forcechannel <玩家> <频道>` - 管理员强制切换玩家频道

### 自动欢迎
新玩家首次加入时会收到详细的欢迎信息和使用指南。

## 扩展建议

你可以基于这个示例添加更多功能：

- 聊天冷却系统
- 自定义表情符号
- 聊天记录保存
- 更复杂的统计分析
- 与其他插件的集成

这个示例展示了HuskChat Remake API的强大功能和灵活性，为开发者提供了丰富的扩展可能性。

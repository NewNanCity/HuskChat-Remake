package net.william278.huskchat.test.commands;

import lombok.RequiredArgsConstructor;
import net.william278.huskchat.test.HuskChatAPITestPlugin;
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

/**
 * HuskChat API演示命令处理器
 * 处理 /hcapi 命令的执行和Tab补全
 */
@RequiredArgsConstructor
public class APITestCommand implements CommandExecutor, TabCompleter {
    
    private final HuskChatAPITestPlugin plugin;
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("huskchat.test.api")) {
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
            case "info" -> showAPIInfo(sender);
            case "channels" -> showChannels(sender);
            case "users" -> showOnlineUsers(sender);
            case "send" -> sendMessage(sender, args);
            case "broadcast" -> sendBroadcast(sender, args);
            case "switch" -> switchChannel(sender, args);
            case "permissions" -> checkPermissions(sender, args);
            case "status" -> showPlayerStatus(sender, args);
            case "format" -> testFormatting(sender, args);
            case "filter" -> testFiltering(sender, args);
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
        sender.sendMessage(ChatColor.GOLD + "=== HuskChat API 演示命令 ===");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi help" + ChatColor.WHITE + " - 显示此帮助信息");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi info" + ChatColor.WHITE + " - 显示API信息");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi channels" + ChatColor.WHITE + " - 显示所有频道");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi users" + ChatColor.WHITE + " - 显示在线用户");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi send <频道> <消息>" + ChatColor.WHITE + " - 发送频道消息");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi broadcast <消息>" + ChatColor.WHITE + " - 发送广播消息");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi switch <频道>" + ChatColor.WHITE + " - 切换频道");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi permissions [玩家]" + ChatColor.WHITE + " - 检查权限");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi status [玩家]" + ChatColor.WHITE + " - 显示玩家状态");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi format <消息>" + ChatColor.WHITE + " - 测试消息格式化");
        sender.sendMessage(ChatColor.YELLOW + "/hcapi filter <消息>" + ChatColor.WHITE + " - 测试消息过滤");
        sender.sendMessage(ChatColor.GOLD + "========================");
    }
    
    /**
     * 显示API信息
     */
    private void showAPIInfo(@NotNull CommandSender sender) {
        try {
            var api = plugin.getHuskChatAPI();
            
            sender.sendMessage(ChatColor.GREEN + "=== HuskChat API 信息 ===");
            sender.sendMessage(ChatColor.YELLOW + "API类型: " + ChatColor.WHITE + api.getClass().getSimpleName());
            
            var channels = api.getChannels();
            var users = api.getOnlineUsers();
            
            sender.sendMessage(ChatColor.YELLOW + "频道数量: " + ChatColor.WHITE + channels.size());
            sender.sendMessage(ChatColor.YELLOW + "在线用户: " + ChatColor.WHITE + users.size());
            
            sender.sendMessage(ChatColor.GREEN + "=====================");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "获取API信息失败: " + e.getMessage());
            plugin.getLogger().warning("获取API信息失败", e);
        }
    }
    
    /**
     * 显示所有频道
     */
    private void showChannels(@NotNull CommandSender sender) {
        try {
            var api = plugin.getHuskChatAPI();
            var channels = api.getChannels();
            
            sender.sendMessage(ChatColor.GREEN + "=== 频道列表 ===");
            
            if (channels.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "没有找到任何频道");
                return;
            }
            
            for (var channel : channels) {
                var members = api.getPlayersInChannel(channel.getId());
                sender.sendMessage(String.format("%s%s %s(%s%s%s) - %s%d %s成员", 
                    ChatColor.YELLOW, channel.getId(), 
                    ChatColor.GRAY, ChatColor.WHITE, channel.getName(), ChatColor.GRAY,
                    ChatColor.GREEN, members.size(), ChatColor.GRAY));
            }
            
            sender.sendMessage(ChatColor.GREEN + "===============");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "获取频道列表失败: " + e.getMessage());
            plugin.getLogger().warning("获取频道列表失败", e);
        }
    }
    
    /**
     * 显示在线用户
     */
    private void showOnlineUsers(@NotNull CommandSender sender) {
        try {
            var api = plugin.getHuskChatAPI();
            var users = api.getOnlineUsers();
            
            sender.sendMessage(ChatColor.GREEN + "=== 在线用户 ===");
            
            if (users.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "没有在线用户");
                return;
            }
            
            for (var user : users) {
                String channel = api.getPlayerChannel(user.getUuid());
                sender.sendMessage(String.format("%s%s %s- %s%s", 
                    ChatColor.YELLOW, user.getUsername(),
                    ChatColor.GRAY, ChatColor.WHITE, channel != null ? channel : "未知频道"));
            }
            
            sender.sendMessage(ChatColor.GREEN + "===============");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "获取在线用户失败: " + e.getMessage());
            plugin.getLogger().warning("获取在线用户失败", e);
        }
    }
    
    /**
     * 发送频道消息
     */
    private void sendMessage(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "用法: /hcapi send <频道> <消息>");
            return;
        }
        
        String channelId = args[1];
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        
        try {
            var api = plugin.getHuskChatAPI();
            
            // 检查频道是否存在
            var channels = api.getChannels();
            boolean channelExists = channels.stream()
                .anyMatch(channel -> channel.getId().equalsIgnoreCase(channelId));
            
            if (!channelExists) {
                sender.sendMessage(ChatColor.RED + "频道不存在: " + channelId);
                return;
            }
            
            // 发送消息
            boolean success = api.sendChannelMessage(channelId, message, null);
            
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "消息发送成功到频道: " + channelId);
            } else {
                sender.sendMessage(ChatColor.RED + "消息发送失败");
            }
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "发送消息失败: " + e.getMessage());
            plugin.getLogger().warning("发送消息失败", e);
        }
    }
    
    /**
     * 发送广播消息
     */
    private void sendBroadcast(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /hcapi broadcast <消息>");
            return;
        }
        
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        try {
            var api = plugin.getHuskChatAPI();
            boolean success = api.sendBroadcastMessage(message, null);
            
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "广播消息发送成功");
            } else {
                sender.sendMessage(ChatColor.RED + "广播消息发送失败");
            }
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "发送广播失败: " + e.getMessage());
            plugin.getLogger().warning("发送广播失败", e);
        }
    }
    
    /**
     * 切换频道
     */
    private void switchChannel(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家执行");
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /hcapi switch <频道>");
            return;
        }
        
        String channelId = args[1];
        
        try {
            var api = plugin.getHuskChatAPI();
            
            // 检查频道是否存在
            var channels = api.getChannels();
            boolean channelExists = channels.stream()
                .anyMatch(channel -> channel.getId().equalsIgnoreCase(channelId));
            
            if (!channelExists) {
                sender.sendMessage(ChatColor.RED + "频道不存在: " + channelId);
                return;
            }
            
            // 切换频道
            boolean success = api.switchPlayerChannel(player.getUniqueId(), channelId);
            
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "成功切换到频道: " + channelId);
            } else {
                sender.sendMessage(ChatColor.RED + "频道切换失败");
            }
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "切换频道失败: " + e.getMessage());
            plugin.getLogger().warning("切换频道失败", e);
        }
    }
    
    /**
     * 检查权限
     */
    private void checkPermissions(@NotNull CommandSender sender, @NotNull String[] args) {
        Player targetPlayer;
        
        if (args.length >= 2) {
            targetPlayer = plugin.getServer().getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "找不到玩家: " + args[1]);
                return;
            }
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "请指定要检查的玩家");
            return;
        }
        
        try {
            var api = plugin.getHuskChatAPI();
            var channels = api.getChannels();
            
            sender.sendMessage(ChatColor.GREEN + "=== " + targetPlayer.getName() + " 的权限 ===");
            
            for (var channel : channels) {
                boolean canSend = api.canPlayerSendToChannel(targetPlayer.getUniqueId(), channel.getId());
                boolean canReceive = api.canPlayerReceiveFromChannel(targetPlayer.getUniqueId(), channel.getId());
                
                sender.sendMessage(String.format("%s%s: %s发送=%s%s %s接收=%s%s", 
                    ChatColor.YELLOW, channel.getId(),
                    ChatColor.GRAY, canSend ? ChatColor.GREEN + "✓" : ChatColor.RED + "✗", ChatColor.GRAY,
                    ChatColor.GRAY, canReceive ? ChatColor.GREEN + "✓" : ChatColor.RED + "✗"));
            }
            
            sender.sendMessage(ChatColor.GREEN + "==================");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "检查权限失败: " + e.getMessage());
            plugin.getLogger().warning("检查权限失败", e);
        }
    }
    
    /**
     * 显示玩家状态
     */
    private void showPlayerStatus(@NotNull CommandSender sender, @NotNull String[] args) {
        Player targetPlayer;
        
        if (args.length >= 2) {
            targetPlayer = plugin.getServer().getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "找不到玩家: " + args[1]);
                return;
            }
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "请指定要查看的玩家");
            return;
        }
        
        try {
            var api = plugin.getHuskChatAPI();
            String currentChannel = api.getPlayerChannel(targetPlayer.getUniqueId());
            
            sender.sendMessage(ChatColor.GREEN + "=== " + targetPlayer.getName() + " 的状态 ===");
            sender.sendMessage(ChatColor.YELLOW + "当前频道: " + ChatColor.WHITE + 
                (currentChannel != null ? currentChannel : "未知"));
            sender.sendMessage(ChatColor.YELLOW + "健康值: " + ChatColor.WHITE + 
                String.format("%.1f/%.1f", targetPlayer.getHealth(), targetPlayer.getMaxHealth()));
            sender.sendMessage(ChatColor.YELLOW + "等级: " + ChatColor.WHITE + targetPlayer.getLevel());
            sender.sendMessage(ChatColor.YELLOW + "游戏模式: " + ChatColor.WHITE + targetPlayer.getGameMode().name());
            sender.sendMessage(ChatColor.YELLOW + "世界: " + ChatColor.WHITE + targetPlayer.getWorld().getName());
            sender.sendMessage(ChatColor.GREEN + "==================");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "获取玩家状态失败: " + e.getMessage());
            plugin.getLogger().warning("获取玩家状态失败", e);
        }
    }
    
    /**
     * 测试消息格式化
     */
    private void testFormatting(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /hcapi format <消息>");
            return;
        }
        
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        try {
            // 这里应该使用HuskChat的格式化API，但目前使用简单的演示
            sender.sendMessage(ChatColor.GREEN + "原始消息: " + ChatColor.WHITE + message);
            sender.sendMessage(ChatColor.GREEN + "格式化后: " + ChatColor.WHITE + 
                ChatColor.translateAlternateColorCodes('&', message));
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "格式化测试失败: " + e.getMessage());
            plugin.getLogger().warning("格式化测试失败", e);
        }
    }
    
    /**
     * 测试消息过滤
     */
    private void testFiltering(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /hcapi filter <消息>");
            return;
        }
        
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        try {
            // 这里应该使用HuskChat的过滤API，但目前使用简单的演示
            String filteredMessage = message; // 实际应该调用API过滤
            
            sender.sendMessage(ChatColor.GREEN + "原始消息: " + ChatColor.WHITE + message);
            sender.sendMessage(ChatColor.GREEN + "过滤后: " + ChatColor.WHITE + filteredMessage);
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "过滤测试失败: " + e.getMessage());
            plugin.getLogger().warning("过滤测试失败", e);
        }
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                               @NotNull String alias, @NotNull String[] args) {
        
        if (!sender.hasPermission("huskchat.test.api")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("help", "info", "channels", "users", "send", 
                               "broadcast", "switch", "permissions", "status", "format", "filter");
        }
        
        if (args.length == 2) {
            if ("send".equals(args[0]) || "switch".equals(args[0])) {
                try {
                    var api = plugin.getHuskChatAPI();
                    return api.getChannels().stream()
                        .map(channel -> channel.getId())
                        .toList();
                } catch (Exception e) {
                    return new ArrayList<>();
                }
            }
            
            if ("permissions".equals(args[0]) || "status".equals(args[0])) {
                return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
            }
        }
        
        return new ArrayList<>();
    }
}

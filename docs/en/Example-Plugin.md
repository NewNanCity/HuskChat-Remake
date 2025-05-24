# HuskChat Remake API Example Plugin

This is a complete example plugin demonstrating how to use the HuskChat Remake Extended API.

## Plugin Features

- Add effects to VIP player messages
- Automatically welcome new players
- Channel switch notifications
- Chat statistics functionality
- Custom commands

## Complete Code

### plugin.yml

```yaml
name: HuskChatExample
version: 1.0.0
main: com.example.huskchatexample.HuskChatExamplePlugin
api-version: 1.19
depend: [HuskChat]

commands:
  chatstats:
    description: View chat statistics
    usage: /chatstats [player]
    permission: huskchatexample.chatstats
  
  forcechannel:
    description: Force player to switch channel
    usage: /forcechannel <player> <channel>
    permission: huskchatexample.forcechannel

permissions:
  huskchatexample.vip:
    description: VIP chat effects
    default: false
  huskchatexample.chatstats:
    description: View chat statistics
    default: op
  huskchatexample.forcechannel:
    description: Force channel switch
    default: op
```

### Main Plugin Class

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
            // Get HuskChat API
            huskChatAPI = HuskChatExtendedAPI.getInstance();
            getLogger().info("Successfully connected to HuskChat API");
            
            // Register event listeners
            registerEventListeners();
            
            getLogger().info("HuskChat Example Plugin enabled!");
        } catch (Exception e) {
            getLogger().severe("Failed to connect to HuskChat API: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    private void registerEventListeners() {
        // Listen to chat messages
        huskChatAPI.registerChatMessageListener(this::onChatMessage);
        
        // Listen to channel switches
        huskChatAPI.registerChannelSwitchListener(this::onChannelSwitch);
        
        // Listen to player join channel
        huskChatAPI.registerPlayerJoinChannelListener(this::onPlayerJoinChannel);
        
        // Listen to private messages
        huskChatAPI.registerPrivateMessageListener(this::onPrivateMessage);
        
        // Listen to message filtering
        huskChatAPI.registerMessageFilterListener(this::onMessageFilter);
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        OnlineUser sender = event.getSender();
        String message = event.getMessage();
        
        // Record chat statistics
        updateChatStats(sender.getUuid(), ChatStats.MessageType.CHANNEL);
        
        // VIP effects
        if (sender.hasPermission("huskchatexample.vip")) {
            // Add rainbow effect
            String rainbowMessage = addRainbowEffect(message);
            event.setMessage("✨ " + rainbowMessage + " ✨");
        }
        
        // Detect special keywords
        if (message.toLowerCase().contains("help")) {
            // Delay sending help info to avoid interfering with original message
            Bukkit.getScheduler().runTaskLater(this, () -> {
                sender.sendMessage("§6Need help? Type /help to see available commands!");
            }, 20L);
        }
    }
    
    private void onChannelSwitch(ChannelSwitchEvent event) {
        OnlineUser player = event.getPlayer();
        String newChannel = event.getNewChannelId();
        String previousChannel = event.getPreviousChannelId();
        
        // Log channel switch
        getLogger().info(String.format("%s switched from channel %s to %s (reason: %s)", 
            player.getUsername(), previousChannel, newChannel, event.getReason()));
        
        // Special channel welcome messages
        switch (newChannel) {
            case "staff":
                if (player.hasPermission("huskchat.channel.staff.receive")) {
                    player.sendMessage("§cWelcome to staff channel! Please follow admin guidelines.");
                }
                break;
            case "global":
                player.sendMessage("§aWelcome to global channel!");
                break;
            case "local":
                player.sendMessage("§eWelcome to local channel!");
                break;
        }
    }
    
    private void onPlayerJoinChannel(PlayerJoinChannelEvent event) {
        OnlineUser player = event.getPlayer();
        String channelId = event.getChannelId();
        
        // New player welcome
        if (event.getReason() == PlayerJoinChannelEvent.JoinReason.FIRST_LOGIN) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.sendMessage("§6=== Welcome to the server! ===");
                player.sendMessage("§7You are now in §b" + channelId + " §7channel");
                player.sendMessage("§7Type §f/channel <channel> §7to switch channels");
                player.sendMessage("§7Type §f/msg <player> <message> §7for private chat");
                player.sendMessage("§6============================");
            }, 60L); // 3 seconds later
        }
    }
    
    private void onPrivateMessage(PrivateMessageEvent event) {
        OnlineUser sender = event.getSender();
        
        // Record private message statistics
        updateChatStats(sender.getUuid(), ChatStats.MessageType.PRIVATE);
        
        // Log private messages (visible to admins)
        String recipients = event.getRecipients().stream()
            .map(OnlineUser::getUsername)
            .reduce((a, b) -> a + ", " + b)
            .orElse("none");
        
        getLogger().info(String.format("Private: %s -> %s: %s", 
            sender.getUsername(), recipients, event.getMessage()));
    }
    
    private void onMessageFilter(MessageFilterEvent event) {
        if (event.getFilterType() == MessageFilterEvent.FilterType.PROFANITY) {
            OnlineUser sender = event.getSender();
            
            // Log violation
            getLogger().warning(String.format("Player %s attempted inappropriate content: %s", 
                sender.getUsername(), event.getOriginalMessage()));
            
            // Warn player
            sender.sendMessage("§c⚠ Warning: Please watch your language!");
            
            // Update violation statistics
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
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            // Show own statistics
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cConsole cannot view own statistics!");
                return true;
            }
            
            Player player = (Player) sender;
            showChatStats(sender, player.getUniqueId(), player.getName());
        } else {
            // Show other player's statistics
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer " + args[0] + " is not online!");
                return true;
            }
            
            showChatStats(sender, target.getUniqueId(), target.getName());
        }
        
        return true;
    }
    
    private boolean handleForceChannelCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("huskchatexample.forcechannel")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /forcechannel <player> <channel>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer " + args[0] + " is not online!");
            return true;
        }
        
        String channelId = args[1];
        OnlineUser targetUser = huskChatAPI.adaptPlayer(target);
        
        huskChatAPI.switchPlayerChannel(targetUser, channelId, ChannelSwitchEvent.SwitchReason.ADMIN_FORCE)
            .thenAccept(success -> {
                if (success) {
                    sender.sendMessage("§aSuccessfully switched " + target.getName() + " to channel " + channelId);
                    target.sendMessage("§6You were switched to channel " + channelId + " by an admin");
                } else {
                    sender.sendMessage("§cSwitch failed! Channel may not exist or player lacks permission.");
                }
            });
        
        return true;
    }
    
    private void showChatStats(CommandSender sender, UUID playerId, String playerName) {
        ChatStats stats = playerStats.getOrDefault(playerId, new ChatStats());
        
        sender.sendMessage("§6=== " + playerName + "'s Chat Statistics ===");
        sender.sendMessage("§7Channel messages: §f" + stats.channelMessages);
        sender.sendMessage("§7Private messages: §f" + stats.privateMessages);
        sender.sendMessage("§7Violations: §f" + stats.violations);
        sender.sendMessage("§7Total messages: §f" + stats.getTotalMessages());
    }
    
    private void updateChatStats(UUID playerId, ChatStats.MessageType type) {
        ChatStats stats = playerStats.computeIfAbsent(playerId, k -> new ChatStats());
        stats.increment(type);
    }
    
    private String addRainbowEffect(String message) {
        // Simple rainbow effect implementation
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
        // Clean up resources
        if (huskChatAPI != null) {
            huskChatAPI.unregisterChatMessageListener(this::onChatMessage);
            huskChatAPI.unregisterChannelSwitchListener(this::onChannelSwitch);
            huskChatAPI.unregisterPrivateMessageListener(this::onPrivateMessage);
            huskChatAPI.unregisterMessageFilterListener(this::onMessageFilter);
        }
        
        getLogger().info("HuskChat Example Plugin disabled!");
    }
    
    // Chat statistics data class
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

## Usage Instructions

1. Save this code as a standalone plugin project
2. Ensure HuskChat Remake is installed and enabled
3. Compile and install this example plugin
4. Restart the server

## Feature Demonstration

### VIP Effects
Give players the `huskchatexample.vip` permission, and their messages will display rainbow colors and star effects.

### Chat Statistics
- `/chatstats` - View your own chat statistics
- `/chatstats <player>` - View other player's statistics

### Force Channel Switch
- `/forcechannel <player> <channel>` - Admin force switch player channel

### Auto Welcome
New players will receive detailed welcome information and usage guide on first join.

## Extension Suggestions

You can add more features based on this example:

- Chat cooldown system
- Custom emoji system
- Chat log saving
- More complex statistical analysis
- Integration with other plugins

This example demonstrates the powerful functionality and flexibility of the HuskChat Remake API, providing developers with rich extension possibilities.

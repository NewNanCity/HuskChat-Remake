# HuskChat Remake API Developer Guide

HuskChat Remake provides a powerful API that allows other plugins to interact with the chat system. This guide will show you how to use these APIs.

## Quick Start

### 1. Add Dependency

Add the dependency in your `plugin.yml`:

```yaml
depend: [HuskChat]
```

### 2. Get API Instance

```java
import net.william278.huskchat.api.HuskChatExtendedAPI;

// Get the extended API instance
HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
```

## Core Features

### Channel Management

#### Switch Player Channel

```java
import net.william278.huskchat.event.ChannelSwitchEvent;

// Switch player to specified channel
OnlineUser player = api.adaptPlayer(bukkitPlayer);
api.switchPlayerChannel(player, "staff", ChannelSwitchEvent.SwitchReason.API_CALL)
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("Channel switched successfully!");
        } else {
            player.sendMessage("Channel switch failed!");
        }
    });
```

#### Get Players in Channel

```java
// Get all players in specified channel
List<OnlineUser> playersInStaff = api.getPlayersInChannel("staff");
System.out.println("There are " + playersInStaff.size() + " players in staff channel");

// Check if player is in specified channel
boolean isInStaff = api.isPlayerInChannel(player, "staff");
```

### Message Sending

#### Send Private Messages

```java
// Send private message to single player
List<String> recipients = List.of("PlayerName");
api.sendPrivateMessage(sender, recipients, "Hello!")
    .thenAccept(success -> {
        if (success) {
            sender.sendMessage("Message sent successfully!");
        }
    });

// Send group message
List<String> groupRecipients = List.of("Player1", "Player2", "Player3");
api.sendPrivateMessage(sender, groupRecipients, "Group message");
```

#### Send Channel Messages

```java
// Send channel message as player
api.sendChannelMessage("global", "Hello everyone!", player);

// Send system message (no sender)
api.sendChannelMessage("global", "Server announcement", null);
```

## Event System

HuskChat Remake provides a rich event system that allows you to listen to and handle various chat-related events.

### Listen to Chat Messages

```java
api.registerChatMessageListener(event -> {
    OnlineUser sender = event.getSender();
    String message = event.getMessage();
    String channelId = event.getChannelId();
    
    // Check for specific keywords
    if (message.contains("forbidden")) {
        event.setCancelled(true);
        sender.sendMessage("Message contains forbidden content!");
        return;
    }
    
    // Modify message content
    if (sender.hasPermission("chat.rainbow")) {
        event.setMessage("&c" + message); // Add color
    }
});
```

### Listen to Channel Switches

```java
api.registerChannelSwitchListener(event -> {
    OnlineUser player = event.getPlayer();
    String previousChannel = event.getPreviousChannelId();
    String newChannel = event.getNewChannelId();
    
    // Log channel switches
    System.out.println(player.getUsername() + " switched from " + previousChannel + " to " + newChannel);
    
    // Prevent switching to certain channels
    if (newChannel.equals("admin") && !player.hasPermission("chat.admin")) {
        event.setCancelled(true);
        player.sendMessage("You don't have permission to enter admin channel!");
    }
});
```

### Listen to Private Messages

```java
api.registerPrivateMessageListener(event -> {
    OnlineUser sender = event.getSender();
    List<OnlineUser> recipients = event.getRecipients();
    String message = event.getMessage();
    
    // Log private messages
    System.out.println("Private: " + sender.getUsername() + " -> " + 
                      recipients.stream().map(OnlineUser::getUsername).collect(Collectors.joining(", ")) + 
                      ": " + message);
});
```

### Listen to Message Filtering

```java
api.registerMessageFilterListener(event -> {
    if (event.getFilterType() == MessageFilterEvent.FilterType.PROFANITY) {
        // Handle profanity filtering
        OnlineUser sender = event.getSender();
        sender.sendMessage("Please watch your language!");
        
        // Can modify filtered message
        event.setFilteredMessage("[Censored]");
    }
});
```

## Event Types

### ChatMessageEvent
- **Triggered**: When player sends chat message
- **Modifiable**: Sender, message content, target channel
- **Cancellable**: Yes

### ChannelSwitchEvent  
- **Triggered**: When player switches channels
- **Modifiable**: Target channel
- **Cancellable**: Yes

### PlayerJoinChannelEvent
- **Triggered**: When player joins channel
- **Modifiable**: None
- **Cancellable**: Yes

### PlayerLeaveChannelEvent
- **Triggered**: When player leaves channel
- **Modifiable**: None
- **Cancellable**: Yes

### PrivateMessageEvent
- **Triggered**: When player sends private message
- **Modifiable**: Sender, recipients, message content
- **Cancellable**: Yes

### MessageFilterEvent
- **Triggered**: When message is processed by filters
- **Modifiable**: Filtered message, filter reason, blocked status
- **Cancellable**: No (but can set blocked)

## Best Practices

### 1. Async Processing
```java
// Recommended: Use async processing to avoid blocking main thread
api.switchPlayerChannel(player, "global", ChannelSwitchEvent.SwitchReason.API_CALL)
    .thenAcceptAsync(success -> {
        // Handle result asynchronously
    });
```

### 2. Error Handling
```java
api.sendPrivateMessage(sender, recipients, message)
    .exceptionally(throwable -> {
        plugin.getLogger().warning("Failed to send private message: " + throwable.getMessage());
        return false;
    });
```

### 3. Permission Checks
```java
api.registerChannelSwitchListener(event -> {
    OnlineUser player = event.getPlayer();
    String targetChannel = event.getNewChannelId();
    
    // Check permissions
    if (!player.hasPermission("huskchat.channel." + targetChannel + ".join")) {
        event.setCancelled(true);
        player.sendMessage("You don't have permission to enter this channel!");
    }
});
```

### 4. Resource Cleanup
```java
@Override
public void onDisable() {
    // Remove event listeners
    HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
    api.unregisterChatMessageListener(myListener);
    api.unregisterChannelSwitchListener(myChannelListener);
}
```

## Example Plugin

Here's a complete example plugin showing how to use the HuskChat API:

```java
public class ExamplePlugin extends JavaPlugin implements Listener {
    
    private HuskChatExtendedAPI api;
    
    @Override
    public void onEnable() {
        // Get API instance
        api = HuskChatExtendedAPI.getInstance();
        
        // Register event listeners
        api.registerChatMessageListener(this::onChatMessage);
        api.registerChannelSwitchListener(this::onChannelSwitch);
        
        getLogger().info("Example plugin enabled!");
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        // Add effects for VIP players
        OnlineUser sender = event.getSender();
        if (sender.hasPermission("example.vip")) {
            String message = event.getMessage();
            event.setMessage("✨ " + message + " ✨");
        }
    }
    
    private void onChannelSwitch(ChannelSwitchEvent event) {
        // Welcome players to new channel
        OnlineUser player = event.getPlayer();
        String channelId = event.getNewChannelId();
        
        Bukkit.getScheduler().runTaskLater(this, () -> {
            player.sendMessage("Welcome to " + channelId + " channel!");
        }, 20L); // 1 second later
    }
    
    @Override
    public void onDisable() {
        // Clean up resources
        if (api != null) {
            api.unregisterChatMessageListener(this::onChatMessage);
            api.unregisterChannelSwitchListener(this::onChannelSwitch);
        }
    }
}
```

## More Information

- [Event System Documentation](Events.md)
- [Channel Configuration Guide](Channels.md)
- [Command Reference](Commands.md)

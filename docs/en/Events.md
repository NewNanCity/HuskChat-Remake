# HuskChat Remake Event System

HuskChat Remake provides a complete event system that allows developers to listen to and handle various chat-related events.

## Event Overview

### Core Events

| Event Name | Trigger | Cancellable | Platform Support |
|------------|---------|-------------|------------------|
| `ChatMessageEvent` | Player sends chat message | ✅ | Bukkit, Velocity, BungeeCord |
| `PrivateMessageEvent` | Player sends private message | ✅ | Bukkit, Velocity, BungeeCord |
| `BroadcastMessageEvent` | Broadcast message sent | ✅ | Bukkit, Velocity, BungeeCord |

### Extended Events (HuskChat Remake New)

| Event Name | Trigger | Cancellable | Platform Support |
|------------|---------|-------------|------------------|
| `ChannelSwitchEvent` | Player switches channel | ✅ | Bukkit, Velocity, BungeeCord |
| `PlayerJoinChannelEvent` | Player joins channel | ✅ | Bukkit, Velocity, BungeeCord |
| `PlayerLeaveChannelEvent` | Player leaves channel | ✅ | Bukkit, Velocity, BungeeCord |
| `MessageFilterEvent` | Message processed by filter | ❌* | Bukkit, Velocity, BungeeCord |

*MessageFilterEvent cannot be directly cancelled, but you can set `setBlocked(true)` to prevent message sending.

## Event Details

### ChatMessageEvent

**Trigger**: When a player sends a message in a channel

**Available Methods**:
- `getSender()` - Get the sender
- `getMessage()` - Get message content
- `getChannelId()` - Get channel ID
- `setSender(OnlineUser)` - Set sender
- `setMessage(String)` - Set message content
- `setChannelId(String)` - Set channel ID
- `isCancelled()` - Check if cancelled
- `setCancelled(boolean)` - Set cancellation

**Usage Example**:
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    OnlineUser sender = event.getSender();
    String message = event.getMessage();
    
    // Block messages containing "spam" in global channel
    if (event.getChannelId().equals("global") && message.contains("spam")) {
        event.setCancelled(true);
        sender.sendMessage("Spam messages are not allowed!");
    }
}
```

### ChannelSwitchEvent

**Trigger**: When a player switches chat channels

**Available Methods**:
- `getPlayer()` - Get the player switching channels
- `getPreviousChannelId()` - Get previous channel ID (may be null)
- `getNewChannelId()` - Get new channel ID
- `setNewChannelId(String)` - Set new channel ID
- `getReason()` - Get switch reason
- `isCancelled()` - Check if cancelled
- `setCancelled(boolean)` - Set cancellation

**Switch Reason Enum**:
- `PLAYER_COMMAND` - Player manually switched
- `SERVER_SWITCH` - Due to server switch
- `PLAYER_JOIN` - Player joined server
- `ADMIN_FORCE` - Admin forced switch
- `API_CALL` - API call
- `OTHER` - Other reason

**Usage Example**:
```java
@EventHandler
public void onChannelSwitch(BukkitChannelSwitchEvent event) {
    OnlineUser player = event.getPlayer();
    String newChannel = event.getNewChannelId();
    
    // Restrict non-VIP players from VIP channel
    if (newChannel.equals("vip") && !player.hasPermission("chat.vip")) {
        event.setCancelled(true);
        player.sendMessage("You need VIP permission to enter this channel!");
    }
    
    // Log channel switches
    if (!event.isCancelled()) {
        getLogger().info(player.getUsername() + " switched to channel: " + newChannel);
    }
}
```

### PlayerJoinChannelEvent

**Trigger**: When a player joins a channel (including first login, reconnect, switch, etc.)

**Available Methods**:
- `getPlayer()` - Get the player joining channel
- `getChannelId()` - Get channel ID
- `getReason()` - Get join reason
- `isCancelled()` - Check if cancelled
- `setCancelled(boolean)` - Set cancellation

**Join Reason Enum**:
- `FIRST_LOGIN` - First login
- `RECONNECT` - Reconnection
- `SERVER_SWITCH` - Server switch
- `MANUAL_SWITCH` - Manual switch
- `ADMIN_ACTION` - Admin action
- `API_CALL` - API call

**Usage Example**:
```java
@EventHandler
public void onPlayerJoinChannel(BukkitPlayerJoinChannelEvent event) {
    OnlineUser player = event.getPlayer();
    String channelId = event.getChannelId();
    
    // Welcome new players
    if (event.getReason() == PlayerJoinChannelEvent.JoinReason.FIRST_LOGIN) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage("Welcome to " + channelId + " channel! Type /help for assistance.");
        }, 40L); // 2 seconds later
    }
}
```

### PlayerLeaveChannelEvent

**Trigger**: When a player leaves a channel

**Available Methods**:
- `getPlayer()` - Get the player leaving channel
- `getChannelId()` - Get channel ID
- `getReason()` - Get leave reason
- `isCancelled()` - Check if cancelled
- `setCancelled(boolean)` - Set cancellation

**Leave Reason Enum**:
- `DISCONNECT` - Player disconnected
- `CHANNEL_SWITCH` - Switched to another channel
- `SERVER_SWITCH` - Server switch
- `ADMIN_ACTION` - Admin action
- `API_CALL` - API call
- `PERMISSION_REVOKED` - Permission revoked

### MessageFilterEvent

**Trigger**: When a message is processed by filters

**Available Methods**:
- `getSender()` - Get sender
- `getOriginalMessage()` - Get original message
- `getFilteredMessage()` - Get filtered message
- `setFilteredMessage(String)` - Set filtered message
- `getFilterType()` - Get filter type
- `getFilterName()` - Get filter name
- `getFilterReason()` - Get filter reason
- `setFilterReason(String)` - Set filter reason
- `isBlocked()` - Check if blocked
- `setBlocked(boolean)` - Set blocked status

**Filter Type Enum**:
- `PROFANITY` - Profanity filter
- `SPAM` - Spam filter
- `ADVERTISEMENT` - Advertisement filter
- `CUSTOM` - Custom filter
- `REPLACER` - Replacer
- `FORMAT` - Format filter

**Usage Example**:
```java
@EventHandler
public void onMessageFilter(BukkitMessageFilterEvent event) {
    if (event.getFilterType() == MessageFilterEvent.FilterType.PROFANITY) {
        OnlineUser sender = event.getSender();
        
        // Log violation
        getLogger().warning(sender.getUsername() + " attempted inappropriate content: " + event.getOriginalMessage());
        
        // Warn player
        sender.sendMessage("§cWarning: Please watch your language!");
        
        // Can choose to completely block message
        if (sender.hasPermission("chat.strict_filter")) {
            event.setBlocked(true);
            event.setFilterReason("Inappropriate content blocked");
        }
    }
}
```

## Event Priority

On Bukkit platform, you can set event listener priority:

```java
@EventHandler(priority = EventPriority.HIGH)
public void onChatMessage(BukkitChatMessageEvent event) {
    // High priority handling
}

@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
public void onChatMessageMonitor(BukkitChatMessageEvent event) {
    // Monitor level, only executes when event is not cancelled
}
```

**Priority Order** (low to high):
1. `LOWEST`
2. `LOW` 
3. `NORMAL` (default)
4. `HIGH`
5. `HIGHEST`
6. `MONITOR` (for monitoring only, should not modify event)

## Cross-Platform Compatibility

### Bukkit/Paper
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // Bukkit-specific handling
}
```

### Velocity
```java
@Subscribe
public void onChatMessage(VelocityChatMessageEvent event) {
    // Velocity-specific handling
}
```

### BungeeCord
```java
@EventHandler
public void onChatMessage(BungeeChatMessageEvent event) {
    // BungeeCord-specific handling
}
```

## Best Practices

### 1. Async Processing for Heavy Operations
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // Avoid heavy operations in event handling
    CompletableFuture.runAsync(() -> {
        // Heavy database operations
        saveMessageToDatabase(event.getSender(), event.getMessage());
    });
}
```

### 2. Properly Handle Cancelled Events
```java
@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
public void onChatMessageMonitor(BukkitChatMessageEvent event) {
    // Only log when event is not cancelled
    logChatMessage(event);
}
```

### 3. Avoid Infinite Loops
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // Avoid sending messages that might trigger the same event
    // Wrong: event.getSender().sendMessage("You said: " + event.getMessage());
    
    // Correct: Use delay or async
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
        event.getSender().sendMessage("You said: " + event.getMessage());
    }, 1L);
}
```

### 4. Resource Cleanup
```java
public class MyPlugin extends JavaPlugin {
    private final List<Listener> listeners = new ArrayList<>();
    
    @Override
    public void onEnable() {
        Listener chatListener = new ChatListener();
        listeners.add(chatListener);
        getServer().getPluginManager().registerEvents(chatListener, this);
    }
    
    @Override
    public void onDisable() {
        // Bukkit automatically unregisters event listeners, but API listeners need manual cleanup
        HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
        // Clean up API listeners...
    }
}
```

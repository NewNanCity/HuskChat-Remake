# HuskChat Remake Migration Guide

This guide will help developers migrate from original HuskChat or older versions of HuskChat Remake to the latest version and integrate new player status features.

## Migrating from Original HuskChat

### API Changes

#### 1. Getting API Instance

**Original HuskChat:**
```java
HuskChatAPI api = HuskChatAPI.getInstance();
```

**HuskChat Remake:**
```java
// Basic API (compatible with original)
HuskChatAPI api = HuskChatAPI.getInstance();

// Extended API (recommended)
HuskChatExtendedAPI extendedAPI = HuskChatExtendedAPI.getInstance();
```

#### 2. Event Listening

**Original HuskChat:**
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // Handle chat message
}
```

**HuskChat Remake:**
```java
// Method 1: Traditional Bukkit events (compatible)
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // Handle chat message
}

// Method 2: API listeners (recommended)
api.registerChatMessageListener(event -> {
    // Handle chat message
});
```

### New Feature Integration

#### 1. Player Status Monitoring

```java
// Listen to player health changes
api.registerPlayerHealthChangeListener(event -> {
    OnlineUser player = event.getPlayer();
    
    if (event.isLowHealth()) {
        // Handle low health logic
        player.sendMessage("§cWarning: Low health!");
    }
});

// Listen to player location changes
api.registerPlayerLocationChangeListener(event -> {
    if (event.isCrossWorld()) {
        // Handle cross-world movement logic
        OnlineUser player = event.getPlayer();
        player.sendMessage("Welcome to the new world!");
    }
});
```

#### 2. Command Execution API

```java
// Original: Manual command execution
player.performCommand("channel global");

// Remake: Use API to execute commands
api.executeChatCommand(player, "/channel", "global")
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("Channel switched successfully!");
        }
    });
```

#### 3. Chat Condition Checking

```java
// New: Check if player meets chat conditions
HuskChatExtendedAPI.ChatConditionResult result = api.checkChatConditions(player, "global");
if (!result.isAllowed()) {
    player.sendMessage("Cannot chat: " + result.getReason());
    return;
}
```

## Migrating from HuskChat Remake 1.x to 2.x

### Major Changes

1. **New player status integration events**
2. **Command execution API**
3. **Enhanced player information interface**
4. **Location-based chat features**

### Code Update Examples

#### 1. Update Dependencies

**plugin.yml:**
```yaml
# Ensure latest version dependency
depend: [HuskChat]
api-version: 1.19
```

#### 2. Update Event Listeners

**Old Version:**
```java
public class MyChatPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
        api.registerChatMessageListener(this::onChatMessage);
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        // Basic chat handling
    }
}
```

**New Version:**
```java
public class MyChatPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
        
        // Existing functionality
        api.registerChatMessageListener(this::onChatMessage);
        
        // New functionality
        api.registerPlayerHealthChangeListener(this::onHealthChange);
        api.registerPlayerLocationChangeListener(this::onLocationChange);
        api.registerPlayerStatusChangeListener(this::onStatusChange);
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        OnlineUser sender = event.getSender();
        
        // New: Check chat conditions
        HuskChatExtendedAPI.ChatConditionResult result = api.checkChatConditions(sender, event.getChannelId());
        if (!result.isAllowed()) {
            event.setCancelled(true);
            sender.sendMessage("§c" + result.getReason());
            return;
        }
        
        // New: Get detailed player information
        PlayerInfo info = api.getPlayerInfo(sender);
        if (info.isLowHealth() && event.getChannelId().equals("staff")) {
            event.setCancelled(true);
            sender.sendMessage("§cHealth too low to use staff channel!");
        }
    }
    
    private void onHealthChange(PlayerHealthChangeEvent event) {
        // New: Health change handling
        if (event.isAboutToDie()) {
            event.getPlayer().sendMessage("§cDanger! You are about to die!");
        }
    }
    
    private void onLocationChange(PlayerLocationChangeEvent event) {
        // New: Location change handling
        if (event.isCrossWorld()) {
            OnlineUser player = event.getPlayer();
            String worldChannel = "world_" + event.getNewLocation().getWorld();
            api.switchPlayerChannel(player, worldChannel, ChannelSwitchEvent.SwitchReason.API_CALL);
        }
    }
    
    private void onStatusChange(PlayerStatusChangeEvent event) {
        // New: Status change handling
        if (event.getStatusType() == PlayerStatusChangeEvent.StatusType.COMBAT) {
            boolean inCombat = (Boolean) event.getNewValue();
            if (inCombat) {
                event.getPlayer().sendMessage("§cEntered combat mode!");
            }
        }
    }
}
```

## Configuration Migration

### Enhanced Channel Configuration

**Old Version Configuration:**
```yaml
channels:
  global:
    format: "&7[Global] {username}: {message}"
    permission: "huskchat.channel.global"
```

**New Version Configuration (Recommended):**
```yaml
channels:
  global:
    format: "&7[Global] {username}: {message}"
    permission: "huskchat.channel.global"
    restrictions:
      low_health: false      # Whether to restrict low health players
      combat: false          # Whether to restrict combat players
      min_health: 0.0        # Minimum health requirement
      max_distance: -1       # Maximum distance limit (-1 for unlimited)
  
  staff:
    format: "&c[Staff] {username}: {message}"
    permission: "huskchat.channel.staff"
    restrictions:
      low_health: true       # Restrict low health players
      combat: true           # Restrict combat players
      min_health: 10.0       # Require at least 10 health points
```

## Common Issues and Solutions

### Q1: How to maintain backward compatibility?

A: HuskChat Remake is fully compatible with the original API. You can continue using existing code while gradually integrating new features.

```java
// This code still works in the new version
HuskChatAPI api = HuskChatAPI.getInstance();
api.getOnlineUsers();
api.getChannels();
```

### Q2: Will new event listeners affect performance?

A: The new event system is optimized and only triggers events when listeners are registered. If you don't use a specific event, it won't impact performance.

### Q3: How to handle cross-platform compatibility?

A: Use abstract interfaces and factory patterns:

```java
// Cross-platform compatible code
OnlineUser player = api.adaptPlayer(platformPlayer);
PlayerInfo info = api.getPlayerInfo(player);
```

### Q4: How to test new features?

A: Use the provided example plugin as reference:

```java
// Refer to the complete example in Enhanced-Example-Plugin.md
// Gradually integrate new features, ensuring each is tested
```

## Best Practices

### 1. Progressive Migration

Don't rewrite all code at once. Follow these steps:

1. Update to the latest version, ensure existing functionality works
2. Add basic player status monitoring
3. Integrate command execution API
4. Add advanced features (location-based chat, etc.)

### 2. Error Handling

```java
// Error handling when using async APIs
api.executeChatCommand(player, "/channel", "global")
    .exceptionally(throwable -> {
        getLogger().warning("Command execution failed: " + throwable.getMessage());
        return false;
    });
```

### 3. Resource Cleanup

```java
@Override
public void onDisable() {
    // Ensure all listeners are cleaned up
    if (api != null) {
        api.unregisterChatMessageListener(this::onChatMessage);
        api.unregisterPlayerHealthChangeListener(this::onHealthChange);
        // ... other listeners
    }
}
```

### 4. Configuration Validation

```java
@Override
public void onEnable() {
    // Validate configuration compatibility
    if (!validateConfig()) {
        getLogger().severe("Configuration file incompatible, please update config!");
        getServer().getPluginManager().disablePlugin(this);
        return;
    }
}
```

## Getting Help

If you encounter issues during migration:

1. Check the [API Guide](API-Guide.md) for detailed usage
2. Refer to [Enhanced Example Plugin](Enhanced-Example-Plugin.md) for complete examples
3. Report issues on [GitHub Issues](https://github.com/Gk0Wk/HuskChat-Remake/issues)
4. Join [Discord Discussions](https://github.com/Gk0Wk/HuskChat-Remake/discussions) for community support

Happy migrating! 🚀

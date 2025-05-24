# HuskChat Remake Developer Guide

This guide will help developers understand how to contribute to HuskChat Remake and how to extend its functionality.

## Project Structure

```
HuskChat-Remake/
├── bukkit/          # Bukkit/Paper platform implementation
├── bungee/          # BungeeCord platform implementation  
├── velocity/        # Velocity platform implementation
├── paper/           # Paper-specific features
├── common/          # Cross-platform common code
├── plugin/          # Plugin packaging configuration
└── docs/            # Documentation
```

### Module Description

- **common**: Contains core logic shared across all platforms
- **bukkit**: Bukkit/Spigot/Paper platform specific implementation
- **velocity**: Velocity proxy server implementation
- **bungee**: BungeeCord proxy server implementation
- **paper**: Paper server specific feature enhancements
- **plugin**: Final plugin build configuration

## Architecture Design

### Core Components

1. **Event System** (`common/src/main/java/net/william278/huskchat/event/`)
   - Defines interfaces for all chat-related events
   - Provides cross-platform event triggering mechanism

2. **API Layer** (`common/src/main/java/net/william278/huskchat/api/`)
   - Provides API interfaces for third-party plugins
   - Includes extended API and basic API

3. **Message System** (`common/src/main/java/net/william278/huskchat/message/`)
   - Handles various types of messages (chat, private, broadcast, etc.)
   - Message filtering and formatting

4. **Channel Management** (`common/src/main/java/net/william278/huskchat/channel/`)
   - Channel creation, management and configuration
   - Permission control and scope management

5. **User Management** (`common/src/main/java/net/william278/huskchat/user/`)
   - Cross-platform user abstraction
   - User caching and state management

## Development Environment Setup

### Prerequisites

- Java 17 or higher
- Gradle 7.0+
- Python 3.6+ (for running profanity filter tests)
- Git

### Clone Project

```bash
git clone https://github.com/Gk0Wk/HuskChat-Remake.git
cd HuskChat-Remake
```

### Install Python Dependencies

```bash
pip install jep alt-profanity-check
```

### Build Project

```bash
./gradlew clean build
```

### Run Tests

```bash
./gradlew test
```

## Adding New Features

### 1. Adding New Events

If you want to add new event types:

1. Create event interface in `common/src/main/java/net/william278/huskchat/event/`
2. Create concrete implementations for each platform
3. Add trigger method in `EventProvider` interface
4. Update `EventProvider` implementations for each platform

Example: Creating a new `PlayerMuteEvent`

```java
// 1. Create event interface
public interface PlayerMuteEvent extends EventBase {
    @NotNull OnlineUser getPlayer();
    @NotNull OnlineUser getModerator();
    @NotNull String getReason();
    long getDuration();
    void setDuration(long duration);
}

// 2. Create Bukkit implementation
public class BukkitPlayerMuteEvent extends BukkitEvent implements PlayerMuteEvent {
    // Implement all methods...
}

// 3. Add method in EventProvider
CompletableFuture<PlayerMuteEvent> firePlayerMuteEvent(
    @NotNull OnlineUser player, 
    @NotNull OnlineUser moderator, 
    @NotNull String reason, 
    long duration
);
```

### 2. Extending API Features

Add new API methods in `HuskChatExtendedAPI`:

```java
/**
 * Mute a player
 */
public CompletableFuture<Boolean> mutePlayer(@NotNull OnlineUser player, 
                                           @NotNull OnlineUser moderator,
                                           @NotNull String reason, 
                                           long duration) {
    return plugin.firePlayerMuteEvent(player, moderator, reason, duration)
            .thenApply(event -> {
                if (event.isCancelled()) {
                    return false;
                }
                // Execute mute logic
                return true;
            });
}
```

### 3. Adding New Message Types

1. Create new message class in `common/src/main/java/net/william278/huskchat/message/`
2. Implement message sending, filtering and formatting logic
3. Add corresponding event support

## Code Standards

### Naming Conventions

- **Class names**: PascalCase (e.g.: `ChatMessageEvent`)
- **Method names**: camelCase (e.g.: `sendChatMessage`)
- **Constants**: UPPER_SNAKE_CASE (e.g.: `DEFAULT_CHANNEL_ID`)
- **Package names**: lowercase, dot-separated (e.g.: `net.william278.huskchat.event`)

### Documentation Standards

Use JavaDoc for all public APIs:

```java
/**
 * Send a chat message to the specified channel
 *
 * @param channelId channel ID
 * @param sender sender
 * @param message message content
 * @return send result
 * @throws IllegalArgumentException if channel does not exist
 * @since 3.1.0
 */
public CompletableFuture<Boolean> sendChatMessage(@NotNull String channelId, 
                                                 @NotNull OnlineUser sender, 
                                                 @NotNull String message) {
    // Implementation...
}
```

### Exception Handling

- Use `CompletableFuture` for async operations
- Return `Optional` or `CompletableFuture<Boolean>` for operations that may fail
- Log important error information

```java
public CompletableFuture<Boolean> performOperation() {
    return CompletableFuture.supplyAsync(() -> {
        try {
            // Execute operation
            return true;
        } catch (Exception e) {
            plugin.log(Level.WARNING, "Operation failed", e);
            return false;
        }
    });
}
```

## Testing

### Unit Tests

Write unit tests for new features:

```java
@Test
public void testChannelSwitch() {
    // Prepare test data
    OnlineUser player = createMockPlayer();
    String targetChannel = "test-channel";
    
    // Execute operation
    CompletableFuture<Boolean> result = api.switchPlayerChannel(
        player, targetChannel, ChannelSwitchEvent.SwitchReason.API_CALL
    );
    
    // Verify result
    assertTrue(result.join());
    assertEquals(targetChannel, api.getPlayerChannel(player).orElse(null));
}
```

### Integration Tests

Test cross-platform compatibility and event system:

```java
@Test
public void testEventFiring() {
    AtomicBoolean eventFired = new AtomicBoolean(false);
    
    // Register event listener
    api.registerChatMessageListener(event -> {
        eventFired.set(true);
    });
    
    // Trigger event
    api.sendChatMessage("global", mockPlayer, "test message");
    
    // Verify event was fired
    assertTrue(eventFired.get());
}
```

## Contribution Guidelines

### Submitting Code

1. Fork the project to your GitHub account
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Create Pull Request

### Pull Request Requirements

- Include clear description and change notes
- Add corresponding test cases
- Ensure all tests pass
- Follow code standards
- Update relevant documentation

### Issue Reporting

When reporting issues using GitHub Issues, please include:

- Detailed problem description
- Reproduction steps
- Expected vs actual behavior
- Environment information (server version, plugin version, etc.)
- Relevant log information

## Release Process

### Version Numbering

Use Semantic Versioning (SemVer):
- `MAJOR.MINOR.PATCH`
- Example: `3.1.0`, `3.1.1`, `3.2.0`

### Release Checklist

- [ ] All tests pass
- [ ] Update version number
- [ ] Update CHANGELOG
- [ ] Update documentation
- [ ] Create GitHub Release
- [ ] Publish to Maven repository

## Common Questions

### Q: How to debug cross-platform compatibility issues?

A: Use abstract interfaces and factory patterns, add detailed logging in each platform's implementation.

### Q: How to handle async operations?

A: Use `CompletableFuture` and appropriate thread pools, avoid blocking the main thread.

### Q: How to ensure event system performance?

A: Use event priorities, avoid heavy operations in event handling, use async processing when necessary.

## Resource Links

- [Bukkit API Documentation](https://hub.spigotmc.org/javadocs/bukkit/)
- [Velocity API Documentation](https://jd.velocitypowered.com/)
- [BungeeCord API Documentation](https://ci.md-5.net/job/BungeeCord/ws/api/target/apidocs/)
- [Gradle Build Tool](https://gradle.org/docs/)
- [JUnit Testing Framework](https://junit.org/junit5/docs/current/user-guide/)

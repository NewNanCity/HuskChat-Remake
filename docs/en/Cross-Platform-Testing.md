# HuskChat Remake Cross-Platform Testing Guide

This document provides a comprehensive cross-platform testing guide to ensure player status integration features work correctly on all supported platforms.

## Testing Environment Requirements

### Single Server Testing Environment

**Bukkit/Paper Server**
- Minecraft 1.19+ server
- HuskChat Remake plugin
- Test example plugin

### Cluster Testing Environment

**Velocity Proxy Server**
- Velocity 3.2.0+
- HuskChat Remake Velocity version
- 2-3 backend Paper servers
- HuskChat Remake installed on each backend server

**BungeeCord Proxy Server**
- BungeeCord 1.19+
- HuskChat Remake BungeeCord version
- 2-3 backend Spigot/Paper servers
- HuskChat Remake installed on each backend server

## Test Cases

### 1. Basic Functionality Testing

#### 1.1 Single Server Environment Testing

**Test Steps:**
1. Start Bukkit/Paper server
2. Install HuskChat Remake and test plugin
3. Player joins server
4. Verify basic chat functionality

**Expected Results:**
- Chat messages sent and received normally
- Channel switching works correctly
- Player status initialized properly

#### 1.2 Proxy Server Environment Testing

**Test Steps:**
1. Start proxy server (Velocity/BungeeCord)
2. Start multiple backend servers
3. Player connects through proxy server
4. Verify cross-server chat functionality

**Expected Results:**
- Cross-server chat messages delivered correctly
- Player status maintained when switching servers
- Proxy server routes messages correctly

### 2. Player Status Integration Testing

#### 2.1 Health Change Testing

**Test Steps:**
1. Player joins server
2. Use commands or environmental damage to reduce player health
3. Observe health change event triggering
4. Verify proxy server receives status updates

**Verification Commands:**
```
/damage @p 10
/effect give @p minecraft:instant_damage 1 1
```

**Expected Results:**
- `PlayerHealthChangeEvent` triggers correctly
- Health data updates correctly on proxy server
- Low health status identified and handled correctly

#### 2.2 Location Change Testing

**Test Steps:**
1. Player moves within server
2. Player switches worlds
3. Player switches servers in proxy environment
4. Verify location change events

**Verification Commands:**
```
/tp @p ~ ~10 ~
/execute in minecraft:the_nether run tp @p 0 64 0
```

**Expected Results:**
- `PlayerLocationChangeEvent` triggers correctly
- Cross-world movement detected correctly
- Cross-server movement handled correctly on proxy server

#### 2.3 Game State Change Testing

**Test Steps:**
1. Switch game modes
2. Toggle flight status
3. Toggle sneak status
4. Verify status synchronization

**Verification Commands:**
```
/gamemode creative @p
/gamemode survival @p
```

**Expected Results:**
- Game mode changes detected correctly
- Flight and sneak status synchronized correctly
- Proxy server status cache updated correctly

### 3. Command Execution API Testing

#### 3.1 Channel Switch Command Testing

**Test Steps:**
1. Use API to execute channel switch commands
2. Verify command execution on different platforms
3. Check permission validation

**Test Code:**
```java
// In test plugin
HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
api.executeChatCommand(player, "/channel", "global")
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("Channel switched successfully");
        } else {
            player.sendMessage("Channel switch failed");
        }
    });
```

**Expected Results:**
- Commands execute correctly on all platforms
- Permission validation works normally
- Execution results returned correctly

#### 3.2 Private Message Command Testing

**Test Steps:**
1. Use API to send private messages
2. Verify cross-server private messaging
3. Check message delivery

**Test Code:**
```java
api.executeChatCommand(player, "/msg", "target_player", "Hello!")
    .thenAccept(success -> {
        // Verify execution result
    });
```

### 4. Cross-Server Event Propagation Testing

#### 4.1 Status Synchronization Testing

**Test Steps:**
1. Player modifies status on server A
2. Player switches to server B
3. Verify status synchronization

**Verification Methods:**
- Use `/playerstatus` command to check status
- Check proxy server logs
- Verify plugin message delivery

#### 4.2 Real-time Status Update Testing

**Test Steps:**
1. Player takes damage on server A
2. Check player status on proxy server
3. Verify real-time updates

**Expected Results:**
- Status changes propagate immediately to proxy server
- Players on other servers can see status updates
- Plugin messages delivered correctly

### 5. Performance and Stability Testing

#### 5.1 High Load Testing

**Test Steps:**
1. Simulate multiple players online simultaneously
2. Frequently trigger status change events
3. Monitor server performance

**Monitoring Metrics:**
- CPU usage
- Memory consumption
- Network bandwidth
- Event processing latency

#### 5.2 Long-term Running Testing

**Test Steps:**
1. Run server continuously for 24 hours
2. Periodically check functionality
3. Monitor memory leaks

## Testing Tools and Scripts

### Automated Test Scripts

```java
public class HuskChatTestSuite {
    
    @Test
    public void testPlayerHealthChange() {
        // Test health change
        Player player = getTestPlayer();
        double initialHealth = player.getHealth();
        
        // Simulate damage
        player.damage(5.0);
        
        // Verify event triggering
        verify(healthChangeListener, timeout(1000)).onHealthChange(any());
    }
    
    @Test
    public void testCrossServerStatusSync() {
        // Test cross-server status synchronization
        OnlineUser player = getTestPlayer();
        
        // Set status on server A
        api.updatePlayerStatus(player, StatusType.COMBAT, true, "Test", -1);
        
        // Switch to server B
        switchPlayerServer(player, "serverB");
        
        // Verify status sync
        assertTrue(player.isInCombat());
    }
}
```

### Monitoring Scripts

```bash
#!/bin/bash
# Monitoring script

echo "Starting HuskChat cross-platform testing monitoring..."

# Check server status
check_server_status() {
    local server=$1
    local port=$2
    
    if nc -z localhost $port; then
        echo "✓ $server server running normally"
    else
        echo "✗ $server server connection failed"
    fi
}

# Check each server
check_server_status "Velocity Proxy" 25577
check_server_status "Backend Server 1" 25565
check_server_status "Backend Server 2" 25566

# Check plugin message delivery
echo "Checking plugin message delivery..."
# Add specific checking logic here
```

## Troubleshooting

### Common Issues

#### 1. Plugin Messages Not Delivered

**Symptoms:**
- Proxy server not receiving status updates from backend servers
- Cross-server events not triggering

**Solutions:**
1. Check plugin message channel registration
2. Verify network connections
3. Review server logs

#### 2. Status Sync Delays

**Symptoms:**
- Noticeable delays in status updates
- Incorrect status after player switches servers

**Solutions:**
1. Check network latency
2. Optimize event processing logic
3. Adjust synchronization frequency

#### 3. High Memory Usage

**Symptoms:**
- Server memory continuously growing
- Memory leak warnings

**Solutions:**
1. Check event listener cleanup
2. Optimize caching strategy
3. Regularly clean expired data

### Debugging Tips

#### Enable Verbose Logging

```yaml
# config.yml
debug:
  enabled: true
  level: FINE
  log_player_status: true
  log_cross_server_events: true
```

#### Use Debug Commands

```
/huskchat debug status <player>
/huskchat debug sync <player>
/huskchat debug network
```

## Test Report Template

### Test Results Record

```
Test Date: 2024-XX-XX
Test Environment: Velocity + 3 Paper servers
Test Version: HuskChat Remake v2.0.0

Functionality Test Results:
✓ Basic chat functionality
✓ Player status integration
✓ Cross-server event propagation
✓ Command execution API
✗ High load performance (needs optimization)

Performance Metrics:
- Average latency: 50ms
- Memory usage: Stable
- CPU usage: Normal

Issues Recorded:
1. Occasional status sync delays under high load
2. Duplicate event triggering in some edge cases

Recommendations:
1. Optimize event processing performance
2. Add duplicate event detection
3. Improve error handling mechanisms
```

Through this comprehensive testing guide, you can ensure that HuskChat Remake's player status integration features work stably and reliably on all supported platforms.

# HuskChat Remake 事件系统

HuskChat Remake 提供了完整的事件系统，允许开发者监听和处理各种聊天相关的事件。

## 事件概览

### 核心事件

| 事件名称 | 触发时机 | 可取消 | 平台支持 |
|---------|---------|-------|---------|
| `ChatMessageEvent` | 玩家发送聊天消息 | ✅ | Bukkit, Velocity, BungeeCord |
| `PrivateMessageEvent` | 玩家发送私聊消息 | ✅ | Bukkit, Velocity, BungeeCord |
| `BroadcastMessageEvent` | 发送广播消息 | ✅ | Bukkit, Velocity, BungeeCord |

### 扩展事件 (HuskChat Remake 新增)

| 事件名称 | 触发时机 | 可取消 | 平台支持 |
|---------|---------|-------|---------|
| `ChannelSwitchEvent` | 玩家切换频道 | ✅ | Bukkit, Velocity, BungeeCord |
| `PlayerJoinChannelEvent` | 玩家加入频道 | ✅ | Bukkit, Velocity, BungeeCord |
| `PlayerLeaveChannelEvent` | 玩家离开频道 | ✅ | Bukkit, Velocity, BungeeCord |
| `MessageFilterEvent` | 消息被过滤器处理 | ❌* | Bukkit, Velocity, BungeeCord |

*MessageFilterEvent 不能直接取消，但可以通过设置 `setBlocked(true)` 来阻止消息发送。

## 事件详细说明

### ChatMessageEvent

**触发时机**: 当玩家在频道中发送消息时触发

**可用方法**:
- `getSender()` - 获取发送者
- `getMessage()` - 获取消息内容
- `getChannelId()` - 获取频道ID
- `setSender(OnlineUser)` - 设置发送者
- `setMessage(String)` - 设置消息内容
- `setChannelId(String)` - 设置频道ID
- `isCancelled()` - 检查是否被取消
- `setCancelled(boolean)` - 设置是否取消

**使用示例**:
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    OnlineUser sender = event.getSender();
    String message = event.getMessage();
    
    // 阻止在全局频道发送包含"spam"的消息
    if (event.getChannelId().equals("global") && message.contains("spam")) {
        event.setCancelled(true);
        sender.sendMessage("不允许发送垃圾信息！");
    }
}
```

### ChannelSwitchEvent

**触发时机**: 当玩家切换聊天频道时触发

**可用方法**:
- `getPlayer()` - 获取切换频道的玩家
- `getPreviousChannelId()` - 获取之前的频道ID（可能为null）
- `getNewChannelId()` - 获取新的频道ID
- `setNewChannelId(String)` - 设置新的频道ID
- `getReason()` - 获取切换原因
- `isCancelled()` - 检查是否被取消
- `setCancelled(boolean)` - 设置是否取消

**切换原因枚举**:
- `PLAYER_COMMAND` - 玩家主动切换
- `SERVER_SWITCH` - 服务器切换导致
- `PLAYER_JOIN` - 玩家加入服务器
- `ADMIN_FORCE` - 管理员强制切换
- `API_CALL` - API调用
- `OTHER` - 其他原因

**使用示例**:
```java
@EventHandler
public void onChannelSwitch(BukkitChannelSwitchEvent event) {
    OnlineUser player = event.getPlayer();
    String newChannel = event.getNewChannelId();
    
    // 限制非VIP玩家进入VIP频道
    if (newChannel.equals("vip") && !player.hasPermission("chat.vip")) {
        event.setCancelled(true);
        player.sendMessage("你需要VIP权限才能进入此频道！");
    }
    
    // 记录频道切换
    if (!event.isCancelled()) {
        getLogger().info(player.getUsername() + " 切换到频道: " + newChannel);
    }
}
```

### PlayerJoinChannelEvent

**触发时机**: 当玩家加入频道时触发（包括首次登录、重连、切换等）

**可用方法**:
- `getPlayer()` - 获取加入频道的玩家
- `getChannelId()` - 获取频道ID
- `getReason()` - 获取加入原因
- `isCancelled()` - 检查是否被取消
- `setCancelled(boolean)` - 设置是否取消

**加入原因枚举**:
- `FIRST_LOGIN` - 首次登录
- `RECONNECT` - 重新连接
- `SERVER_SWITCH` - 服务器切换
- `MANUAL_SWITCH` - 手动切换
- `ADMIN_ACTION` - 管理员操作
- `API_CALL` - API调用

**使用示例**:
```java
@EventHandler
public void onPlayerJoinChannel(BukkitPlayerJoinChannelEvent event) {
    OnlineUser player = event.getPlayer();
    String channelId = event.getChannelId();
    
    // 欢迎新玩家
    if (event.getReason() == PlayerJoinChannelEvent.JoinReason.FIRST_LOGIN) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage("欢迎来到 " + channelId + " 频道！输入 /help 获取帮助。");
        }, 40L); // 2秒后发送
    }
}
```

### PlayerLeaveChannelEvent

**触发时机**: 当玩家离开频道时触发

**可用方法**:
- `getPlayer()` - 获取离开频道的玩家
- `getChannelId()` - 获取频道ID
- `getReason()` - 获取离开原因
- `isCancelled()` - 检查是否被取消
- `setCancelled(boolean)` - 设置是否取消

**离开原因枚举**:
- `DISCONNECT` - 玩家断开连接
- `CHANNEL_SWITCH` - 切换到其他频道
- `SERVER_SWITCH` - 服务器切换
- `ADMIN_ACTION` - 管理员操作
- `API_CALL` - API调用
- `PERMISSION_REVOKED` - 权限被撤销

### MessageFilterEvent

**触发时机**: 当消息被过滤器处理时触发

**可用方法**:
- `getSender()` - 获取发送者
- `getOriginalMessage()` - 获取原始消息
- `getFilteredMessage()` - 获取过滤后的消息
- `setFilteredMessage(String)` - 设置过滤后的消息
- `getFilterType()` - 获取过滤器类型
- `getFilterName()` - 获取过滤器名称
- `getFilterReason()` - 获取过滤原因
- `setFilterReason(String)` - 设置过滤原因
- `isBlocked()` - 检查是否被阻止
- `setBlocked(boolean)` - 设置是否阻止

**过滤器类型枚举**:
- `PROFANITY` - 脏话过滤器
- `SPAM` - 垃圾信息过滤器
- `ADVERTISEMENT` - 广告过滤器
- `CUSTOM` - 自定义过滤器
- `REPLACER` - 替换器
- `FORMAT` - 格式化过滤器

**使用示例**:
```java
@EventHandler
public void onMessageFilter(BukkitMessageFilterEvent event) {
    if (event.getFilterType() == MessageFilterEvent.FilterType.PROFANITY) {
        OnlineUser sender = event.getSender();
        
        // 记录违规行为
        getLogger().warning(sender.getUsername() + " 尝试发送不当内容: " + event.getOriginalMessage());
        
        // 给玩家警告
        sender.sendMessage("§c警告: 请注意你的言辞！");
        
        // 可以选择完全阻止消息
        if (sender.hasPermission("chat.strict_filter")) {
            event.setBlocked(true);
            event.setFilterReason("内容不当，已被阻止");
        }
    }
}
```

## 事件优先级

在Bukkit平台上，你可以设置事件监听器的优先级：

```java
@EventHandler(priority = EventPriority.HIGH)
public void onChatMessage(BukkitChatMessageEvent event) {
    // 高优先级处理
}

@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
public void onChatMessageMonitor(BukkitChatMessageEvent event) {
    // 监控级别，只在事件未被取消时执行
}
```

**优先级顺序** (从低到高):
1. `LOWEST`
2. `LOW` 
3. `NORMAL` (默认)
4. `HIGH`
5. `HIGHEST`
6. `MONITOR` (仅用于监控，不应修改事件)

## 跨平台兼容性

### Bukkit/Paper
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // Bukkit特定处理
}
```

### Velocity
```java
@Subscribe
public void onChatMessage(VelocityChatMessageEvent event) {
    // Velocity特定处理
}
```

### BungeeCord
```java
@EventHandler
public void onChatMessage(BungeeChatMessageEvent event) {
    // BungeeCord特定处理
}
```

## 最佳实践

### 1. 异步处理耗时操作
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // 避免在事件处理中执行耗时操作
    CompletableFuture.runAsync(() -> {
        // 耗时的数据库操作
        saveMessageToDatabase(event.getSender(), event.getMessage());
    });
}
```

### 2. 正确处理取消的事件
```java
@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
public void onChatMessageMonitor(BukkitChatMessageEvent event) {
    // 只在事件未被取消时记录
    logChatMessage(event);
}
```

### 3. 避免无限循环
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // 避免在事件处理中发送可能触发相同事件的消息
    // 错误示例：event.getSender().sendMessage("你说了: " + event.getMessage());
    
    // 正确做法：使用延迟或异步
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
        event.getSender().sendMessage("你说了: " + event.getMessage());
    }, 1L);
}
```

### 4. 资源清理
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
        // Bukkit会自动注销事件监听器，但如果使用API监听器需要手动清理
        HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
        // 清理API监听器...
    }
}
```

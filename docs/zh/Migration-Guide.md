# HuskChat Remake 迁移指南

本指南将帮助开发者从原版HuskChat或旧版本的HuskChat Remake迁移到最新版本，并集成新的玩家状态功能。

## 从原版HuskChat迁移

### API变更

#### 1. 获取API实例

**原版HuskChat:**
```java
HuskChatAPI api = HuskChatAPI.getInstance();
```

**HuskChat Remake:**
```java
// 基础API（兼容原版）
HuskChatAPI api = HuskChatAPI.getInstance();

// 扩展API（推荐使用）
HuskChatExtendedAPI extendedAPI = HuskChatExtendedAPI.getInstance();
```

#### 2. 事件监听

**原版HuskChat:**
```java
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // 处理聊天消息
}
```

**HuskChat Remake:**
```java
// 方式1：传统Bukkit事件（兼容）
@EventHandler
public void onChatMessage(BukkitChatMessageEvent event) {
    // 处理聊天消息
}

// 方式2：API监听器（推荐）
api.registerChatMessageListener(event -> {
    // 处理聊天消息
});
```

### 新增功能集成

#### 1. 玩家状态监控

```java
// 监听玩家生命值变化
api.registerPlayerHealthChangeListener(event -> {
    OnlineUser player = event.getPlayer();
    
    if (event.isLowHealth()) {
        // 低血量时的处理逻辑
        player.sendMessage("§c警告：生命值过低！");
    }
});

// 监听玩家位置变化
api.registerPlayerLocationChangeListener(event -> {
    if (event.isCrossWorld()) {
        // 跨世界移动的处理逻辑
        OnlineUser player = event.getPlayer();
        player.sendMessage("欢迎来到新世界！");
    }
});
```

#### 2. 命令执行API

```java
// 原版：需要手动执行命令
player.performCommand("channel global");

// Remake：使用API执行命令
api.executeChatCommand(player, "/channel", "global")
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("频道切换成功！");
        }
    });
```

#### 3. 聊天条件检查

```java
// 新增：检查玩家是否满足聊天条件
HuskChatExtendedAPI.ChatConditionResult result = api.checkChatConditions(player, "global");
if (!result.isAllowed()) {
    player.sendMessage("无法聊天: " + result.getReason());
    return;
}
```

## 从HuskChat Remake 1.x迁移到2.x

### 主要变更

1. **新增玩家状态集成事件**
2. **命令执行API**
3. **增强的玩家信息接口**
4. **基于位置的聊天功能**

### 代码更新示例

#### 1. 更新依赖

**plugin.yml:**
```yaml
# 确保依赖最新版本
depend: [HuskChat]
api-version: 1.19
```

#### 2. 更新事件监听器

**旧版本:**
```java
public class MyChatPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
        api.registerChatMessageListener(this::onChatMessage);
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        // 基础聊天处理
    }
}
```

**新版本:**
```java
public class MyChatPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
        
        // 原有功能
        api.registerChatMessageListener(this::onChatMessage);
        
        // 新增功能
        api.registerPlayerHealthChangeListener(this::onHealthChange);
        api.registerPlayerLocationChangeListener(this::onLocationChange);
        api.registerPlayerStatusChangeListener(this::onStatusChange);
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        OnlineUser sender = event.getSender();
        
        // 新增：检查聊天条件
        HuskChatExtendedAPI.ChatConditionResult result = api.checkChatConditions(sender, event.getChannelId());
        if (!result.isAllowed()) {
            event.setCancelled(true);
            sender.sendMessage("§c" + result.getReason());
            return;
        }
        
        // 新增：获取玩家详细信息
        PlayerInfo info = api.getPlayerInfo(sender);
        if (info.isLowHealth() && event.getChannelId().equals("staff")) {
            event.setCancelled(true);
            sender.sendMessage("§c生命值过低，无法使用员工频道！");
        }
    }
    
    private void onHealthChange(PlayerHealthChangeEvent event) {
        // 新增：生命值变化处理
        if (event.isAboutToDie()) {
            event.getPlayer().sendMessage("§c危险！你即将死亡！");
        }
    }
    
    private void onLocationChange(PlayerLocationChangeEvent event) {
        // 新增：位置变化处理
        if (event.isCrossWorld()) {
            OnlineUser player = event.getPlayer();
            String worldChannel = "world_" + event.getNewLocation().getWorld();
            api.switchPlayerChannel(player, worldChannel, ChannelSwitchEvent.SwitchReason.API_CALL);
        }
    }
    
    private void onStatusChange(PlayerStatusChangeEvent event) {
        // 新增：状态变化处理
        if (event.getStatusType() == PlayerStatusChangeEvent.StatusType.COMBAT) {
            boolean inCombat = (Boolean) event.getNewValue();
            if (inCombat) {
                event.getPlayer().sendMessage("§c进入战斗模式！");
            }
        }
    }
}
```

## 配置迁移

### 频道配置增强

**旧版本配置:**
```yaml
channels:
  global:
    format: "&7[Global] {username}: {message}"
    permission: "huskchat.channel.global"
```

**新版本配置（建议）:**
```yaml
channels:
  global:
    format: "&7[Global] {username}: {message}"
    permission: "huskchat.channel.global"
    restrictions:
      low_health: false      # 是否限制低血量玩家
      combat: false          # 是否限制战斗状态玩家
      min_health: 0.0        # 最低生命值要求
      max_distance: -1       # 最大距离限制（-1为无限制）
  
  staff:
    format: "&c[Staff] {username}: {message}"
    permission: "huskchat.channel.staff"
    restrictions:
      low_health: true       # 限制低血量玩家
      combat: true           # 限制战斗状态玩家
      min_health: 10.0       # 至少需要10点生命值
```

## 常见问题和解决方案

### Q1: 如何保持向后兼容性？

A: HuskChat Remake完全兼容原版API。你可以继续使用原有的代码，同时逐步集成新功能。

```java
// 这些代码在新版本中仍然有效
HuskChatAPI api = HuskChatAPI.getInstance();
api.getOnlineUsers();
api.getChannels();
```

### Q2: 新的事件监听器会影响性能吗？

A: 新的事件系统经过优化，只有在有监听器注册时才会触发事件。如果你不使用某个事件，它不会对性能产生影响。

### Q3: 如何处理跨平台兼容性？

A: 使用抽象接口和工厂模式：

```java
// 跨平台兼容的代码
OnlineUser player = api.adaptPlayer(platformPlayer);
PlayerInfo info = api.getPlayerInfo(player);
```

### Q4: 如何测试新功能？

A: 使用提供的示例插件作为参考：

```java
// 参考 Enhanced-Example-Plugin.md 中的完整示例
// 逐步集成新功能，确保每个功能都经过测试
```

## 最佳实践

### 1. 渐进式迁移

不要一次性重写所有代码。建议按以下步骤进行：

1. 更新到最新版本，确保现有功能正常
2. 添加基础的玩家状态监控
3. 集成命令执行API
4. 添加高级功能（位置基础聊天等）

### 2. 错误处理

```java
// 使用异步API时的错误处理
api.executeChatCommand(player, "/channel", "global")
    .exceptionally(throwable -> {
        getLogger().warning("命令执行失败: " + throwable.getMessage());
        return false;
    });
```

### 3. 资源清理

```java
@Override
public void onDisable() {
    // 确保清理所有监听器
    if (api != null) {
        api.unregisterChatMessageListener(this::onChatMessage);
        api.unregisterPlayerHealthChangeListener(this::onHealthChange);
        // ... 其他监听器
    }
}
```

### 4. 配置验证

```java
@Override
public void onEnable() {
    // 验证配置兼容性
    if (!validateConfig()) {
        getLogger().severe("配置文件不兼容，请更新配置！");
        getServer().getPluginManager().disablePlugin(this);
        return;
    }
}
```

## 获取帮助

如果在迁移过程中遇到问题：

1. 查看 [API指南](API-Guide.md) 了解详细用法
2. 参考 [增强示例插件](Enhanced-Example-Plugin.md) 获取完整示例
3. 在 [GitHub Issues](https://github.com/Gk0Wk/HuskChat-Remake/issues) 报告问题
4. 加入 [Discord讨论](https://github.com/Gk0Wk/HuskChat-Remake/discussions) 获取社区支持

迁移愉快！🚀

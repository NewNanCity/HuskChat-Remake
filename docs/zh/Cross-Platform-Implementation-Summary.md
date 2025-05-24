# HuskChat Remake 跨平台实现总结

本文档总结了HuskChat Remake玩家状态集成功能的完整跨平台实现，确保在所有支持的平台上都能正常工作。

## 实现概述

### 支持的平台

✅ **Bukkit/Paper** (单服务器模式)
- 完整的玩家状态监控
- 实时事件触发
- 本地状态缓存

✅ **Velocity** (代理服务器模式)
- 跨服务器事件传播
- 状态同步机制
- 插件消息通信

✅ **BungeeCord** (代理服务器模式)
- 跨服务器事件传播
- 状态同步机制
- 插件消息通信

### 核心功能

#### 1. 玩家状态集成
- **生命值监控**: 实时监控玩家生命值变化
- **位置追踪**: 跨世界、跨服务器位置变化检测
- **游戏状态**: 游戏模式、飞行、潜行等状态同步
- **自定义状态**: 战斗、离开、禁言等自定义状态管理

#### 2. 跨服务器通信
- **插件消息**: 使用插件消息通道进行跨服务器通信
- **状态同步**: 玩家在服务器间切换时状态自动同步
- **实时更新**: 状态变化实时传播到代理服务器

#### 3. 命令执行API
- **程序化执行**: 通过API执行聊天相关命令
- **权限验证**: 自动验证命令执行权限
- **异步处理**: 所有命令执行都支持异步操作

## 技术架构

### 类层次结构

```
PlayerInfo (接口)
├── OnlineUser (抽象类)
│   ├── BukkitUser (Bukkit实现)
│   ├── VelocityUser (Velocity实现)
│   └── BungeeUser (BungeeCord实现)
```

### 事件系统

```
HuskChatExtendedAPI
├── ChatCommandEvent (命令执行事件)
├── PlayerHealthChangeEvent (生命值变化事件)
├── PlayerLocationChangeEvent (位置变化事件)
├── PlayerStatusChangeEvent (状态变化事件)
├── PlayerDeathEvent (玩家死亡事件)
└── PlayerRespawnEvent (玩家重生事件)
```

### 网络通信

```
PlayerStatusMessage (消息格式)
├── STATUS_UPDATE (状态更新)
├── HEALTH_CHANGE (生命值变化)
├── LOCATION_CHANGE (位置变化)
├── PLAYER_DEATH (玩家死亡)
├── PLAYER_RESPAWN (玩家重生)
├── COMMAND_EXECUTION (命令执行)
├── SYNC_REQUEST (同步请求)
└── SYNC_RESPONSE (同步响应)
```

## 平台特定实现

### Bukkit/Paper 实现

**核心类:**
- `BukkitUser`: 实现PlayerInfo接口，提供完整的玩家状态信息
- `BukkitPlayerStatusListener`: 监听Bukkit事件并转换为HuskChat事件
- `BukkitHuskChatExtendedAPI`: Bukkit平台的扩展API实现

**特性:**
- 直接访问Bukkit Player对象
- 实时监控所有玩家状态变化
- 通过插件消息向代理服务器发送状态更新

**事件监听:**
```java
@EventHandler
public void onPlayerMove(PlayerMoveEvent event) {
    // 触发位置变化事件并发送到代理服务器
}

@EventHandler
public void onEntityDamage(EntityDamageEvent event) {
    // 触发生命值变化事件并发送到代理服务器
}
```

### Velocity 实现

**核心类:**
- `VelocityUser`: 代理环境下的玩家状态实现
- `VelocityPlayerStatusListener`: 处理插件消息和玩家连接事件
- `VelocityHuskChatExtendedAPI`: Velocity平台的扩展API实现

**特性:**
- 状态缓存机制（因为代理服务器无法直接访问游戏状态）
- 跨服务器事件传播
- 自动状态同步

**消息处理:**
```java
@Subscribe
public void onPluginMessage(PluginMessageEvent event) {
    // 处理来自后端服务器的状态更新
    PlayerStatusMessage message = parseMessage(event.getData());
    extendedAPI.handlePlayerStatusMessage(serverName, message);
}
```

### BungeeCord 实现

**核心类:**
- `BungeeUser`: 代理环境下的玩家状态实现
- `BungeePlayerStatusListener`: 处理插件消息和玩家连接事件
- `BungeeHuskChatExtendedAPI`: BungeeCord平台的扩展API实现

**特性:**
- 与Velocity类似的状态缓存机制
- 兼容BungeeCord的事件系统
- 插件消息通道管理

## 数据流程

### 状态更新流程

1. **Bukkit服务器**: 玩家状态发生变化
2. **事件监听**: BukkitPlayerStatusListener捕获事件
3. **消息发送**: 通过插件消息发送到代理服务器
4. **代理处理**: 代理服务器更新状态缓存
5. **事件触发**: 在代理服务器触发相应的HuskChat事件

### 跨服务器同步流程

1. **玩家切换**: 玩家从服务器A切换到服务器B
2. **同步请求**: 代理服务器向服务器B发送同步请求
3. **状态收集**: 服务器B收集玩家当前状态
4. **同步响应**: 服务器B发送状态数据到代理服务器
5. **缓存更新**: 代理服务器更新状态缓存

## API使用示例

### 基础状态查询

```java
HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
PlayerInfo info = api.getPlayerInfo(player);

// 获取玩家状态
double health = info.getHealth();
boolean isInCombat = info.isInCombat();
PlayerLocation location = info.getLocation();
```

### 状态更新

```java
// 设置玩家战斗状态
api.updatePlayerStatus(player, 
    PlayerStatusChangeEvent.StatusType.COMBAT, 
    true, 
    "进入战斗", 
    15000) // 15秒后自动解除
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("战斗状态已设置");
        }
    });
```

### 命令执行

```java
// 执行频道切换命令
api.executeChatCommand(player, "/channel", "global")
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("频道切换成功");
        }
    });
```

### 事件监听

```java
// 监听生命值变化
api.registerPlayerHealthChangeListener(event -> {
    if (event.isLowHealth()) {
        event.getPlayer().sendMessage("§c警告：生命值过低！");
    }
});

// 监听位置变化
api.registerPlayerLocationChangeListener(event -> {
    if (event.isCrossWorld()) {
        OnlineUser player = event.getPlayer();
        player.sendMessage("欢迎来到新世界！");
    }
});
```

## 配置和部署

### 插件消息通道

所有平台都使用统一的插件消息通道：
- `huskchat:player_status` - 玩家状态消息

### 配置要求

**Bukkit/Paper服务器:**
```yaml
# 无需特殊配置，自动启用状态监控
```

**代理服务器:**
```yaml
# 启用跨服务器功能
cross_server:
  enabled: true
  status_sync: true
  event_propagation: true
```

### 部署步骤

1. **安装插件**: 在所有服务器上安装对应版本的HuskChat Remake
2. **配置网络**: 确保代理服务器和后端服务器网络连接正常
3. **测试功能**: 使用提供的测试指南验证功能
4. **监控运行**: 使用日志和调试命令监控运行状态

## 性能优化

### 缓存策略

- **本地缓存**: 每个平台维护本地状态缓存
- **智能更新**: 只在状态实际变化时触发事件
- **定期清理**: 自动清理过期的临时状态

### 网络优化

- **批量传输**: 合并多个状态更新为单个消息
- **压缩数据**: 使用JSON格式减少网络传输量
- **异步处理**: 所有网络操作都是异步的

### 内存管理

- **弱引用**: 使用弱引用避免内存泄漏
- **定时清理**: 定期清理不活跃的玩家数据
- **资源释放**: 插件卸载时正确清理所有资源

## 故障排除

### 常见问题

1. **状态同步失败**: 检查插件消息通道注册
2. **事件不触发**: 验证事件监听器注册
3. **性能问题**: 调整缓存策略和同步频率

### 调试工具

- **详细日志**: 启用调试模式查看详细日志
- **状态命令**: 使用 `/playerstatus` 查看玩家状态
- **网络监控**: 监控插件消息传输

## 未来扩展

### 计划功能

- **更多状态类型**: 支持更多自定义状态
- **性能优化**: 进一步优化网络传输和缓存
- **集成支持**: 与更多插件的集成支持

### 扩展接口

所有API都设计为可扩展的，开发者可以：
- 添加自定义状态类型
- 实现自定义事件监听器
- 扩展跨服务器通信协议

## 总结

HuskChat Remake的跨平台玩家状态集成功能提供了：

✅ **完整的平台支持** - Bukkit、Velocity、BungeeCord
✅ **实时状态同步** - 跨服务器状态实时同步
✅ **丰富的API** - 完整的开发者API
✅ **高性能设计** - 优化的缓存和网络传输
✅ **易于使用** - 简单的配置和部署
✅ **可扩展性** - 支持自定义扩展

这个实现为Minecraft服务器提供了强大的玩家状态管理能力，使开发者能够创建更加智能和交互性的聊天系统。

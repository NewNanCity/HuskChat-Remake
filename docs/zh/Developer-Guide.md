# HuskChat Remake 开发者指南

本指南将帮助开发者了解如何为HuskChat Remake做出贡献，以及如何扩展其功能。

## 项目结构

```
HuskChat-Remake/
├── bukkit/          # Bukkit/Paper平台实现
├── bungee/          # BungeeCord平台实现  
├── velocity/        # Velocity平台实现
├── paper/           # Paper特定功能
├── common/          # 跨平台通用代码
├── plugin/          # 插件打包配置
└── docs/            # 文档
```

### 模块说明

- **common**: 包含所有平台共享的核心逻辑
- **bukkit**: Bukkit/Spigot/Paper平台的具体实现
- **velocity**: Velocity代理服务器的实现
- **bungee**: BungeeCord代理服务器的实现
- **paper**: Paper服务器的特定功能增强
- **plugin**: 最终插件的构建配置

## 架构设计

### 核心组件

1. **事件系统** (`common/src/main/java/net/william278/huskchat/event/`)
   - 定义所有聊天相关事件的接口
   - 提供跨平台的事件触发机制

2. **API层** (`common/src/main/java/net/william278/huskchat/api/`)
   - 提供给第三方插件使用的API接口
   - 包含扩展API和基础API

3. **消息系统** (`common/src/main/java/net/william278/huskchat/message/`)
   - 处理各种类型的消息（聊天、私聊、广播等）
   - 消息过滤和格式化

4. **频道管理** (`common/src/main/java/net/william278/huskchat/channel/`)
   - 频道的创建、管理和配置
   - 权限控制和作用域管理

5. **用户管理** (`common/src/main/java/net/william278/huskchat/user/`)
   - 跨平台用户抽象
   - 用户缓存和状态管理

## 开发环境设置

### 前置要求

- Java 17 或更高版本
- Gradle 7.0+
- Python 3.6+ (用于运行脏话过滤器测试)
- Git

### 克隆项目

```bash
git clone https://github.com/Gk0Wk/HuskChat-Remake.git
cd HuskChat-Remake
```

### 安装Python依赖

```bash
pip install jep alt-profanity-check
```

### 构建项目

```bash
./gradlew clean build
```

### 运行测试

```bash
./gradlew test
```

## 添加新功能

### 1. 添加新事件

如果你想添加新的事件类型：

1. 在 `common/src/main/java/net/william278/huskchat/event/` 创建事件接口
2. 为每个平台创建具体实现
3. 在 `EventProvider` 接口中添加触发方法
4. 更新各平台的 `EventProvider` 实现

示例：创建一个新的 `PlayerMuteEvent`

```java
// 1. 创建事件接口
public interface PlayerMuteEvent extends EventBase {
    @NotNull OnlineUser getPlayer();
    @NotNull OnlineUser getModerator();
    @NotNull String getReason();
    long getDuration();
    void setDuration(long duration);
}

// 2. 创建Bukkit实现
public class BukkitPlayerMuteEvent extends BukkitEvent implements PlayerMuteEvent {
    // 实现所有方法...
}

// 3. 在EventProvider中添加方法
CompletableFuture<PlayerMuteEvent> firePlayerMuteEvent(
    @NotNull OnlineUser player, 
    @NotNull OnlineUser moderator, 
    @NotNull String reason, 
    long duration
);
```

### 2. 扩展API功能

在 `HuskChatExtendedAPI` 中添加新的API方法：

```java
/**
 * 禁言玩家
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
                // 执行禁言逻辑
                return true;
            });
}
```

### 3. 添加新的消息类型

1. 在 `common/src/main/java/net/william278/huskchat/message/` 创建新的消息类
2. 实现消息的发送、过滤和格式化逻辑
3. 添加相应的事件支持

## 代码规范

### 命名约定

- **类名**: PascalCase (例如: `ChatMessageEvent`)
- **方法名**: camelCase (例如: `sendChatMessage`)
- **常量**: UPPER_SNAKE_CASE (例如: `DEFAULT_CHANNEL_ID`)
- **包名**: 小写，用点分隔 (例如: `net.william278.huskchat.event`)

### 注释规范

使用JavaDoc为所有公共API添加注释：

```java
/**
 * 发送聊天消息到指定频道
 * Send a chat message to the specified channel
 *
 * @param channelId 频道ID / channel ID
 * @param sender 发送者 / sender
 * @param message 消息内容 / message content
 * @return 发送结果 / send result
 * @throws IllegalArgumentException 如果频道不存在 / if channel does not exist
 * @since 3.1.0
 */
public CompletableFuture<Boolean> sendChatMessage(@NotNull String channelId, 
                                                 @NotNull OnlineUser sender, 
                                                 @NotNull String message) {
    // 实现...
}
```

### 异常处理

- 使用 `CompletableFuture` 处理异步操作
- 对于可能失败的操作，返回 `Optional` 或 `CompletableFuture<Boolean>`
- 记录重要的错误信息

```java
public CompletableFuture<Boolean> performOperation() {
    return CompletableFuture.supplyAsync(() -> {
        try {
            // 执行操作
            return true;
        } catch (Exception e) {
            plugin.log(Level.WARNING, "操作失败", e);
            return false;
        }
    });
}
```

## 测试

### 单元测试

为新功能编写单元测试：

```java
@Test
public void testChannelSwitch() {
    // 准备测试数据
    OnlineUser player = createMockPlayer();
    String targetChannel = "test-channel";
    
    // 执行操作
    CompletableFuture<Boolean> result = api.switchPlayerChannel(
        player, targetChannel, ChannelSwitchEvent.SwitchReason.API_CALL
    );
    
    // 验证结果
    assertTrue(result.join());
    assertEquals(targetChannel, api.getPlayerChannel(player).orElse(null));
}
```

### 集成测试

测试跨平台兼容性和事件系统：

```java
@Test
public void testEventFiring() {
    AtomicBoolean eventFired = new AtomicBoolean(false);
    
    // 注册事件监听器
    api.registerChatMessageListener(event -> {
        eventFired.set(true);
    });
    
    // 触发事件
    api.sendChatMessage("global", mockPlayer, "test message");
    
    // 验证事件被触发
    assertTrue(eventFired.get());
}
```

## 贡献指南

### 提交代码

1. Fork 项目到你的GitHub账户
2. 创建功能分支: `git checkout -b feature/new-feature`
3. 提交更改: `git commit -am 'Add new feature'`
4. 推送到分支: `git push origin feature/new-feature`
5. 创建Pull Request

### Pull Request 要求

- 包含清晰的描述和变更说明
- 添加相应的测试用例
- 确保所有测试通过
- 遵循代码规范
- 更新相关文档

### 问题报告

使用GitHub Issues报告问题时，请包含：

- 详细的问题描述
- 重现步骤
- 预期行为和实际行为
- 环境信息（服务器版本、插件版本等）
- 相关的日志信息

## 发布流程

### 版本号规范

使用语义化版本控制 (SemVer):
- `MAJOR.MINOR.PATCH`
- 例如: `3.1.0`, `3.1.1`, `3.2.0`

### 发布检查清单

- [ ] 所有测试通过
- [ ] 更新版本号
- [ ] 更新CHANGELOG
- [ ] 更新文档
- [ ] 创建GitHub Release
- [ ] 发布到Maven仓库

## 常见问题

### Q: 如何调试跨平台兼容性问题？

A: 使用抽象接口和工厂模式，在每个平台的实现中添加详细的日志记录。

### Q: 如何处理异步操作？

A: 使用 `CompletableFuture` 和适当的线程池，避免阻塞主线程。

### Q: 如何确保事件系统的性能？

A: 使用事件优先级，避免在事件处理中执行耗时操作，必要时使用异步处理。

## 资源链接

- [Bukkit API文档](https://hub.spigotmc.org/javadocs/bukkit/)
- [Velocity API文档](https://jd.velocitypowered.com/)
- [BungeeCord API文档](https://ci.md-5.net/job/BungeeCord/ws/api/target/apidocs/)
- [Gradle构建工具](https://gradle.org/docs/)
- [JUnit测试框架](https://junit.org/junit5/docs/current/user-guide/)

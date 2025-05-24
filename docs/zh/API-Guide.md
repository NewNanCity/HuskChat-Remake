# HuskChat Remake API 开发指南

HuskChat Remake 提供了强大的API，允许其他插件与聊天系统进行交互。本指南将介绍如何使用这些API。

## 快速开始

### 1. 添加依赖

在你的 `plugin.yml` 中添加依赖：

```yaml
depend: [HuskChat]
```

### 2. 获取API实例

```java
import net.william278.huskchat.api.HuskChatExtendedAPI;

// 获取扩展API实例
HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
```

## 核心功能

### 频道管理

#### 切换玩家频道

```java
import net.william278.huskchat.event.ChannelSwitchEvent;

// 切换玩家到指定频道
OnlineUser player = api.adaptPlayer(bukkitPlayer);
api.switchPlayerChannel(player, "staff", ChannelSwitchEvent.SwitchReason.API_CALL)
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("频道切换成功！");
        } else {
            player.sendMessage("频道切换失败！");
        }
    });
```

#### 获取频道中的玩家

```java
// 获取指定频道中的所有玩家
List<OnlineUser> playersInStaff = api.getPlayersInChannel("staff");
System.out.println("员工频道中有 " + playersInStaff.size() + " 个玩家");

// 检查玩家是否在指定频道
boolean isInStaff = api.isPlayerInChannel(player, "staff");
```

### 消息发送

#### 发送私聊消息

```java
// 发送私聊消息给单个玩家
List<String> recipients = List.of("PlayerName");
api.sendPrivateMessage(sender, recipients, "Hello!")
    .thenAccept(success -> {
        if (success) {
            sender.sendMessage("消息发送成功！");
        }
    });

// 发送群聊消息
List<String> groupRecipients = List.of("Player1", "Player2", "Player3");
api.sendPrivateMessage(sender, groupRecipients, "群聊消息");
```

#### 发送频道消息

```java
// 以玩家身份发送频道消息
api.sendChannelMessage("global", "Hello everyone!", player);

// 发送系统消息（无发送者）
api.sendChannelMessage("global", "服务器公告", null);
```

## 事件系统

HuskChat Remake 提供了丰富的事件系统，允许你监听和处理各种聊天相关的事件。

### 监听聊天消息

```java
api.registerChatMessageListener(event -> {
    OnlineUser sender = event.getSender();
    String message = event.getMessage();
    String channelId = event.getChannelId();

    // 检查是否包含特定关键词
    if (message.contains("禁词")) {
        event.setCancelled(true);
        sender.sendMessage("消息包含禁止内容！");
        return;
    }

    // 修改消息内容
    if (sender.hasPermission("chat.rainbow")) {
        event.setMessage("&c" + message); // 添加颜色
    }
});
```

### 监听频道切换

```java
api.registerChannelSwitchListener(event -> {
    OnlineUser player = event.getPlayer();
    String previousChannel = event.getPreviousChannelId();
    String newChannel = event.getNewChannelId();

    // 记录频道切换
    System.out.println(player.getUsername() + " 从 " + previousChannel + " 切换到 " + newChannel);

    // 阻止切换到某些频道
    if (newChannel.equals("admin") && !player.hasPermission("chat.admin")) {
        event.setCancelled(true);
        player.sendMessage("你没有权限进入管理员频道！");
    }
});
```

### 监听私聊消息

```java
api.registerPrivateMessageListener(event -> {
    OnlineUser sender = event.getSender();
    List<OnlineUser> recipients = event.getRecipients();
    String message = event.getMessage();

    // 记录私聊消息
    System.out.println("私聊: " + sender.getUsername() + " -> " +
                      recipients.stream().map(OnlineUser::getUsername).collect(Collectors.joining(", ")) +
                      ": " + message);
});
```

### 监听消息过滤

```java
api.registerMessageFilterListener(event -> {
    if (event.getFilterType() == MessageFilterEvent.FilterType.PROFANITY) {
        // 处理脏话过滤
        OnlineUser sender = event.getSender();
        sender.sendMessage("请注意你的言辞！");

        // 可以修改过滤后的消息
        event.setFilteredMessage("[已屏蔽]");
    }
});
```

## 事件类型

### ChatMessageEvent
- **触发时机**: 玩家发送聊天消息时
- **可修改**: 发送者、消息内容、目标频道
- **可取消**: 是

### ChannelSwitchEvent
- **触发时机**: 玩家切换频道时
- **可修改**: 目标频道
- **可取消**: 是

### PlayerJoinChannelEvent
- **触发时机**: 玩家加入频道时
- **可修改**: 无
- **可取消**: 是

### PlayerLeaveChannelEvent
- **触发时机**: 玩家离开频道时
- **可修改**: 无
- **可取消**: 是

### PrivateMessageEvent
- **触发时机**: 玩家发送私聊消息时
- **可修改**: 发送者、接收者、消息内容
- **可取消**: 是

### MessageFilterEvent
- **触发时机**: 消息被过滤器处理时
- **可修改**: 过滤后的消息、过滤原因、是否阻止
- **可取消**: 否（但可以设置阻止）

## 最佳实践

### 1. 异步处理
```java
// 推荐：使用异步处理避免阻塞主线程
api.switchPlayerChannel(player, "global", ChannelSwitchEvent.SwitchReason.API_CALL)
    .thenAcceptAsync(success -> {
        // 异步处理结果
    });
```

### 2. 错误处理
```java
api.sendPrivateMessage(sender, recipients, message)
    .exceptionally(throwable -> {
        plugin.getLogger().warning("发送私聊消息失败: " + throwable.getMessage());
        return false;
    });
```

### 3. 权限检查
```java
api.registerChannelSwitchListener(event -> {
    OnlineUser player = event.getPlayer();
    String targetChannel = event.getNewChannelId();

    // 检查权限
    if (!player.hasPermission("huskchat.channel." + targetChannel + ".join")) {
        event.setCancelled(true);
        player.sendMessage("你没有权限进入此频道！");
    }
});
```

### 4. 资源清理
```java
@Override
public void onDisable() {
    // 移除事件监听器
    HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
    api.unregisterChatMessageListener(myListener);
    api.unregisterChannelSwitchListener(myChannelListener);
}
```

## 示例插件

这里是一个完整的示例插件，展示了如何使用HuskChat API：

```java
public class ExamplePlugin extends JavaPlugin implements Listener {

    private HuskChatExtendedAPI api;

    @Override
    public void onEnable() {
        // 获取API实例
        api = HuskChatExtendedAPI.getInstance();

        // 注册事件监听器
        api.registerChatMessageListener(this::onChatMessage);
        api.registerChannelSwitchListener(this::onChannelSwitch);

        getLogger().info("示例插件已启用！");
    }

    private void onChatMessage(ChatMessageEvent event) {
        // 为VIP玩家的消息添加特效
        OnlineUser sender = event.getSender();
        if (sender.hasPermission("example.vip")) {
            String message = event.getMessage();
            event.setMessage("✨ " + message + " ✨");
        }
    }

    private void onChannelSwitch(ChannelSwitchEvent event) {
        // 欢迎玩家进入新频道
        OnlineUser player = event.getPlayer();
        String channelId = event.getNewChannelId();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            player.sendMessage("欢迎来到 " + channelId + " 频道！");
        }, 20L); // 1秒后发送
    }

    @Override
    public void onDisable() {
        // 清理资源
        if (api != null) {
            api.unregisterChatMessageListener(this::onChatMessage);
            api.unregisterChannelSwitchListener(this::onChannelSwitch);
        }
    }
}
```

## 玩家状态集成 API

HuskChat Remake 新增了强大的玩家状态集成功能：

### 获取玩家信息

```java
// 获取玩家详细信息
PlayerInfo info = api.getPlayerInfo(player);

// 检查玩家状态
double health = info.getHealth();
boolean isLowHealth = info.isLowHealth();
boolean isInCombat = info.isInCombat();
PlayerLocationChangeEvent.PlayerLocation location = info.getLocation();
```

### 命令执行 API

```java
// 代表玩家执行聊天命令
api.executeChatCommand(player, "/channel", "global")
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("命令执行成功！");
        }
    });

// 验证命令权限
boolean hasPermission = api.validateCommandPermission(player, "/msg");
```

### 玩家状态管理

```java
// 更新玩家状态
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

// 检查聊天条件
HuskChatExtendedAPI.ChatConditionResult result = api.checkChatConditions(player, "global");
if (!result.isAllowed()) {
    player.sendMessage("无法聊天: " + result.getReason());
}
```

### 基于位置的功能

```java
// 获取附近的玩家
List<OnlineUser> nearbyPlayers = api.getNearbyPlayers(player, 50.0);

// 检查玩家是否在同一区域
boolean sameRegion = api.arePlayersInSameRegion(player1, player2);

// 创建基于位置的频道
String locationChannel = api.createLocationBasedChannel(player, 100.0);
```

## 事件监听器扩展

### 新增事件监听器

```java
// 监听玩家生命值变化
api.registerPlayerHealthChangeListener(event -> {
    if (event.isAboutToDie()) {
        event.getPlayer().sendMessage("§c危险！你即将死亡！");
    }
});

// 监听玩家位置变化
api.registerPlayerLocationChangeListener(event -> {
    if (event.isCrossWorld()) {
        OnlineUser player = event.getPlayer();
        player.sendMessage("欢迎来到 " + event.getNewLocation().getWorld());
    }
});

// 监听玩家状态变化
api.registerPlayerStatusChangeListener(event -> {
    if (event.getStatusType() == PlayerStatusChangeEvent.StatusType.COMBAT) {
        boolean inCombat = (Boolean) event.getNewValue();
        if (inCombat) {
            event.getPlayer().sendMessage("§c进入战斗模式！");
        }
    }
});

// 监听命令执行
api.registerChatCommandListener(event -> {
    if (event.getPhase() == ChatCommandEvent.ExecutionPhase.PRE) {
        // 命令执行前的处理
        getLogger().info("玩家执行命令: " + event.getCommand());
    }
});
```

## 高级用法示例

### 基于生命值的聊天限制

```java
api.registerChatMessageListener(event -> {
    OnlineUser sender = event.getSender();

    // 检查生命值限制
    if (sender.isLowHealth() && !sender.hasPermission("chat.lowhealth.bypass")) {
        if (event.getChannelId().equals("staff")) {
            event.setCancelled(true);
            sender.sendMessage("§c生命值过低，无法使用员工频道！");
        }
    }
});
```

### 战斗状态聊天限制

```java
api.registerChatMessageListener(event -> {
    OnlineUser sender = event.getSender();

    if (sender.isInCombat()) {
        // 战斗中只能使用本地频道
        if (!event.getChannelId().equals("local")) {
            event.setCancelled(true);
            sender.sendMessage("§c战斗中只能使用本地频道！");
        }
    }
});
```

### 自动频道切换

```java
api.registerPlayerLocationChangeListener(event -> {
    if (event.isCrossWorld()) {
        OnlineUser player = event.getPlayer();
        String worldName = event.getNewLocation().getWorld();

        // 根据世界自动切换频道
        String worldChannel = "world_" + worldName;
        if (api.getChannels().contains(worldChannel)) {
            api.switchPlayerChannel(player, worldChannel,
                ChannelSwitchEvent.SwitchReason.API_CALL);
        }
    }
});
```

## 更多信息

- [事件系统详细文档](Events.md)
- [增强示例插件](Enhanced-Example-Plugin.md)
- [开发者指南](Developer-Guide.md)
- [频道配置指南](Channels.md)
- [命令参考](Commands.md)

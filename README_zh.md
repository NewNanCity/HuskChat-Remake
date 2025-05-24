# HuskChat Remake

> 🎉 **HuskChat Remake** 🎉 - 这是对原版HuskChat插件的重制和改进版本，由新的维护团队继续开发和维护。我们致力于为Minecraft服务器提供更好的聊天体验。

**[English Documentation](README.md)**

## 致谢原作者

我们要特别感谢 [William278](https://william278.net/) 创建了原版的HuskChat插件。这个重制版本基于他的优秀工作，并在此基础上进行了现代化改进和功能扩展。

- 原版HuskChat: [GitHub](https://github.com/NewNanCity/HuskChat-Remake)
- 原作者: William278

<!--suppress ALL -->
<p align="center">
    <img src="images/banner.png" alt="HuskChat" />
    <a href="https://github.com/NewNanCity/HuskChat-Remake/actions/workflows/ci.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/NewNanCity/HuskChat-Remake/ci.yml?branch=master&logo=github"/>
    </a>
    <a href="https://repo.william278.net/#/releases/net/william278/huskchat/">
        <img src="https://repo.william278.net/api/badge/latest/releases/net/william278/huskchat?color=00fb9a&name=Maven&prefix=v" />
    </a>
    <a href="https://discord.gg/tVYhJfyDWG">
        <img src="https://img.shields.io/discord/818135932103557162.svg?label=&logo=discord&logoColor=fff&color=7389D8&labelColor=6A7EC2" />
    </a>
    <br/>
    <b>
        <a href="https://www.spigotmc.org/resources/huskchat.94496/">Spigot</a>
    </b> —
    <b>
        <a href="https://william278.net/docs/huskchat/setup">Setup</a>
    </b> —
    <b>
        <a href="https://william278.net/docs/huskchat/">Docs</a>
    </b> —
    <b>
        <a href="https://github.com/NewNanCity/HuskChat-Remake/issues">Issues</a>
    </b>
</p>
<br/>

**HuskChat Remake** 是一个简洁、可定制的Minecraft网络聊天系统。它支持在BungeeCord和Velocity前端服务器上运行以实现跨服务器聊天，也可以在单服务器Spigot设置上运行。

它设计为易于配置，具有优雅的开箱即用设置，同时也高度可配置，通过允许您定义频道并管理谁可以在其中发送和接收消息来适应各种用例。

## 新功能和改进

**🚀 扩展的事件系统** &mdash; 为所有聊天类型提供完整的事件API，支持第三方插件集成

**🚀 增强的API接口** &mdash; 提供统一的消息发送、频道管理和用户管理API

**🚀 玩家状态集成** &mdash; 生命值、位置、战斗状态等Minecraft特定数据集成

**🚀 命令执行API** &mdash; 程序化命令执行，支持权限验证和事件处理

**🚀 现代化架构** &mdash; 基于最新的Minecraft服务器平台进行优化

**🚀 更好的可扩展性** &mdash; 插件式设计，支持自定义扩展和集成

## Features
**⭐ 开箱即用** &mdash; 在您的Spigot服务器或Velocity/BungeeCord代理上安装并立即使用

**⭐ 占位符支持** &mdash; 与LuckPerms集成显示用户角色，通过[PAPIProxyBridge](https://william278.net/docs/huskchat/formatting)支持PAPI

**⭐ 私聊功能** &mdash; 私聊消息（包括群聊）和回复，管理员间谍功能

**⭐ 精细频道调节** &mdash; 为频道设置发送/接收权限，发送消息到Discord webhook，过滤器[等更多功能](https://william278.net/docs/huskchat/channels)！

**⭐ 快速易用** &mdash; 超级简单的[命令](https://william278.net/docs/huskchat/commands)集。也可以定义频道快捷命令！

**⭐ 高级脏话检查** &mdash; 机器学习驱动的脏话过滤器

**⭐ 过滤器和替换器** &mdash; 可定制的垃圾信息限制过滤器、反广告和特殊表情符号

**⭐ 现代格式化** &mdash; 通过[MineDown](https://github.com/Phoenix616/MineDown)利用现代格式化，支持RGB和渐变

## 支持的聊天类型

HuskChat Remake 支持多种聊天类型，每种都有对应的事件和API：

### 📢 频道聊天 (Channel Chat)
- **全局频道** - 跨服务器聊天
- **本地频道** - 同服务器玩家聊天
- **员工频道** - 管理员专用频道
- **自定义频道** - 完全可配置的频道系统

### 💬 私聊系统 (Private Messages)
- **单人私聊** - 一对一私密聊天
- **群组私聊** - 多人群聊功能
- **回复功能** - 快速回复最后收到的消息

### 📣 广播消息 (Broadcast Messages)
- **服务器广播** - 全服务器公告
- **管理员通知** - 管理员专用广播

### 🔗 特殊消息
- **加入/退出消息** - 玩家进出服务器提示
- **Discord集成** - 与Discord频道同步
- **社交间谍** - 管理员监控私聊功能

## 开发者API

HuskChat Remake 提供了完整的API供其他插件使用：

### 🎯 事件系统
```java
// 监听聊天消息
api.registerChatMessageListener(event -> {
    if (event.getMessage().contains("禁词")) {
        event.setCancelled(true);
    }
});

// 监听玩家生命值变化
api.registerPlayerHealthChangeListener(event -> {
    if (event.isLowHealth()) {
        event.getPlayer().sendMessage("警告：生命值过低！");
    }
});

// 监听频道切换
api.registerChannelSwitchListener(event -> {
    player.sendMessage("欢迎来到 " + event.getNewChannelId() + " 频道！");
});
```

### 🛠️ 频道管理API
```java
// 切换玩家频道
api.switchPlayerChannel(player, "staff", SwitchReason.API_CALL);

// 获取频道中的玩家
List<OnlineUser> players = api.getPlayersInChannel("global");
```

### 📨 消息发送API
```java
// 发送私聊消息
api.sendPrivateMessage(sender, List.of("PlayerName"), "Hello!");

// 发送频道消息
api.sendChannelMessage("global", "系统公告", null);
```

### 🎮 玩家状态集成
```java
// 获取玩家信息
PlayerInfo info = api.getPlayerInfo(player);
boolean isLowHealth = info.isLowHealth();
boolean isInCombat = info.isInCombat();

// 程序化执行命令
api.executeChatCommand(player, "/channel", "staff");

// 检查聊天条件
ChatConditionResult result = api.checkChatConditions(player, "global");
if (!result.isAllowed()) {
    player.sendMessage("无法聊天: " + result.getReason());
}
```

## 构建
要构建HuskChat，您需要安装python（>=`v3.6`）及相关包：`jep`和`alt-profanity-check`。
您可以使用`pip install jep`和`pip install alt-profanity-check`来安装这些包。这些是运行脏话过滤器测试所必需的。

然后，只需在仓库根目录运行以下命令：
```
./gradlew clean build
```

## License
HuskChat在Apache 2.0许可证下授权。

- [License](https://github.com/NewNanCity/HuskChat-Remake/blob/master/LICENSE)

## 翻译
欢迎插件本地化的翻译，以帮助使插件更易于访问。请提交包含您的翻译的`.yml`文件的拉取请求。

- [本地化目录](https://github.com/NewNanCity/HuskChat-Remake/tree/master/common/src/main/resources/locales)
- [英文本地化](https://github.com/NewNanCity/HuskChat-Remake/tree/master/common/src/main/resources/locales/en-gb.yml)

## 文档链接

### 📚 开发文档
- [API 开发指南](docs/zh/API-Guide.md) &mdash; 学习如何使用HuskChat API
- [事件系统文档](docs/zh/Events.md) &mdash; 详细的事件系统说明
- [开发者指南](docs/zh/Developer-Guide.md) &mdash; 贡献代码和扩展功能
- [示例插件](docs/zh/Example-Plugin.md) &mdash; 完整的API使用示例
- [频道配置](docs/zh/Channels.md) &mdash; 频道配置指南
- [命令参考](docs/zh/Commands.md) &mdash; 所有命令的详细说明

### 🔗 原版链接
- [原版文档](https://william278.net/docs/huskchat/) &mdash; 原版插件文档
- [原版Spigot页面](https://www.spigotmc.org/resources/huskchat.94496/) &mdash; 原版资源页面
- [原版GitHub](https://github.com/WiIIiam278/HuskChat) &mdash; 原版源代码

### 🆘 获取帮助
- [Issues](https://github.com/Gk0Wk/HuskChat-Remake/issues) &mdash; 报告问题或请求功能
- [Discussions](https://github.com/Gk0Wk/HuskChat-Remake/discussions) &mdash; 讨论和问答

---
&copy; [William278](https://william278.net/), 2024. Licensed under the Apache-2.0 License.

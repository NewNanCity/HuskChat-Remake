频道是玩家进行聊天的空间，可以通过 `/channel` 命令或专用频道快捷命令进行切换。默认情况下，HuskChat 已设置以下频道，适用于典型的服务器环境：

* `local` - 本地频道，使用 `/local`、`/l` 快捷命令，仅向同一服务器的玩家发送消息。
* `global`（默认频道）- 全局频道，使用 `/global`、`/g` 快捷命令，可在整个网络中发送消息。
* `staff` - 全局频道，使用 `/staff`、`/sc` 快捷命令，方便管理团队内部沟通。玩家需要 `huskchat.channel.staff.send` 和 `huskchat.channel.staff.receive` 权限才能分别发送和接收该频道的消息。
* `helpop` - 全局频道，使用 `/helpop` 快捷命令，方便玩家联系管理团队。玩家需要 `huskchat.channel.helpop.receive` 权限才能接收该频道的消息。

### 频道配置结构
要自定义频道，请在 [`channels.yml`](config-files) 文件中进行配置。以下是一个典型 `staff` 频道的配置示例：

```yaml
# 频道定义
channels:
  # ...
  - id: staff
    format: '&e[Staff] %name%: &7'
    broadcast_scope: GLOBAL
    log_to_console: true
    restricted_servers: []
    filtered: false
    permissions:
      send: huskchat.channel.staff.send
      receive: huskchat.channel.staff.receive
    shortcut_commands:
      - /staff
      - /sc
  # ...
```

### 频道范围（Scope）
频道范围定义了 HuskChat 广播和处理消息的范围。可用选项如下：

#### 代理端范围
当 HuskChat 运行在代理服务器（Velocity 或 BungeeCord/Waterfall）时可用：

* `GLOBAL` - 通过代理全局广播消息给有权限的玩家
* `LOCAL` - 通过代理仅向同一服务器且有权限的玩家广播消息
* `PASSTHROUGH` - 消息不由代理处理，而是直接传递给后端服务器
* `GLOBAL_PASSTHROUGH` - 通过代理全局广播消息给有权限的玩家，并同时传递给后端服务器
* `LOCAL_PASSTHROUGH` - 通过代理仅向同一服务器且有权限的玩家广播消息，并同时传递给后端服务器

#### 单服范围
当 HuskChat 运行在单服 Spigot 服务器时可用：

* `GLOBAL` - 向服务器内所有玩家广播消息
* `PASSTHROUGH` - HuskChat 不处理消息，聊天将交由其他/原版聊天处理器处理
* `GLOBAL_PASSTHROUGH` - 向服务器内所有玩家广播消息，并同时传递给其他/原版聊天处理器（事件不会被取消）

在单服环境下，`LOCAL` 和 `LOCAL_PASSTHROUGH` 的效果与 `GLOBAL` 和 `GLOBAL_PASSTHROUGH` 相同。

### 默认频道
> **注意：** 此功能仅在 Bungee/Velocity 服务器上使用。

你必须在 config.yml 中定义一个 `default_channel`，玩家加入时会自动进入该频道。

此外，你可以在 `server_default_channels` 部分为不同服务器定义专属默认频道。当玩家切换到设置了默认频道的服务器时，会自动切换到指定频道。

```yaml
server_default_channels:
  uhc: minigames
  bedwars: minigames
```

在单服环境下，此设置会被忽略。

### 受限频道
> **注意：** 此功能仅在 Bungee/Velocity 服务器上使用。

如果你希望阻止玩家在某些服务器使用特定频道，可以在每个频道的 `restricted_servers` 中进行设置（见上方频道定义示例）。当玩家连接到受限服务器时，将无法在该频道发送或接收任何消息。

此外，如果玩家切换到当前频道受限的服务器，将自动切换到 `default_channel`，除非有如上所述的服务器专属默认频道覆盖。

你还可以通过 [`config.yml`](config-files) 中 `message_command` 下的 `restricted_servers` 部分，限制 `/msg` 和 `/r` 命令在某些服务器的使用。

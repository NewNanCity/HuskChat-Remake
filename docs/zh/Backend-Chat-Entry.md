> **注意：** 本页面的信息不适用于在单服 Spigot 环境下运行 HuskChat 的用户。

本页面介绍了在代理端使用 HuskChat，并与依赖聊天进行数据输入的后端插件或模组配合的方法。

## 背景
依赖用户在聊天中输入内容（如设置名称）的插件，并没有按照聊天 API 的预期方式使用它。数据输入应通过指令完成，而不是通过聊天。尤其是随着 Minecraft 1.19.1 的到来，插件应当逐步避免依赖这种方式（即使它很方便）。

应优先使用铁砧菜单、指令输入、告示牌菜单等其他数据输入方式。一些插件，如 *QuickShopReremake*，正是出于这个原因，提供了指令替代方案（如 `/i <数量>`）。

## 需要了解的事项
* HuskChat 是一个代理插件。它运行在你的代理（Bungeecord、Waterfall、Velocity 等）上，而**不是**你的“后端”（Spigot、Fabric 等）服务器。这意味着当有人在聊天中输入内容时，HuskChat 会代表玩家所连接的服务器处理该消息；代理上的 HuskChat 不会让该消息传递到后端服务器。这就导致依赖聊天输入的插件无法正常工作。
* 不过，有一个变通方法。HuskChat 允许你设置频道的“广播范围”，这会影响玩家发送的聊天消息被谁看到。HuskChat 提供了三种特殊的范围，非常适合处理此类情况——`passthrough`、`local_passthrough` 和 `global_passthrough`。
  1. 创建一个 `passthrough` 范围的频道。
  2. 为该频道定义一个便捷的快捷指令（例如 `/i`）。
  3. 在你的后端服务器上安装 [CancelChat](https://github.com/WiIIiam278/CancelChat/releases)（Spigot）或 [CancelChat Fabric](https://modrinth.com/mod/cancelchat-fabric)——（它非常轻量，无需担心）。如果不安装，聊天消息会像往常一样被后端服务器广播给玩家。
  4. 保存配置并重启服务器。当需要输入数据时，使用 `/i` 切换到该频道并输入数据。玩家随后可以像往常一样切换回其他频道。
  5. 另见：[频道规范](https://william278.net/docs/huskchat/Channels)
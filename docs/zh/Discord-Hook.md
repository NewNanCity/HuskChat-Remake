HuskChat 支持通过 Discord Webhook 实现单向消息同步，或通过 Spicord 插件实现与 Discord 的双向消息同步（需要一些额外配置）。

要启用 Discord 支持，请在 [`config.yml`](config-files) 的 `discord:` 部分将 `enabled` 设置为 `true`。然后你可以配置机器人和频道 Webhook。

## Webhook
Webhook 是一种简单的方式，可以将消息发送到 Discord 频道。你可以在频道设置中进入“集成”->“Webhook”，创建一个 Webhook 并复制其 URL。然后在 HuskChat 配置的 `channel_webhooks` 部分添加该 URL 即可。

## Spicord
Spicord 是一个插件，可以实现 Discord 与 Minecraft 之间的双向通信。你可以在[这里](https://www.spigotmc.org/resources/spicord.64918/)了解更多关于 Spicord 的信息。Spicord 可安装在 BungeeCord、Velocity 或 Paper 上。

> **为什么不支持 DiscordSRV？** DiscordSRV 不支持 Velocity/Bungee，而 Spicord 支持 :-)

### 安装 Spicord
1. 下载 Spicord 插件，并将其与 HuskChat 一起放入服务器的 plugins 文件夹。
2. 启动服务器。
3. 用文本编辑器打开 `plugins/Spicord` 目录下的 config.toml 文件。
   * 在指定位置填写你的机器人 Token（见下方获取方法）
   * 将 enabled 选项设置为 true
   * 在 bot 的 addons 部分添加 `huskchat`
4. 重启服务器。

你的 Spicord `config.toml` 文件应包含如下 bot 配置：
```toml
  name = "Server Chat"
  enabled = true
  token = "[YOUR TOKEN]"
  command_support = true
  command_prefix = "-"
  addons = [
    "spicord::info",
    "spicord::plugins",
    "spicord::players",
    "huskchat"
  ]
```

### 创建机器人
以下是如何创建机器人并将其添加到你的 Discord 服务器（摘自 [Spicord 官方文档](https://github.com/Spicord/Spicord/blob/v5/tutorial/CREATE-A-BOT.md)）：

1. 登录 [Discord 开发者平台](https://discord.com/developers/applications)
2. 点击 **New Application** 并为你的机器人选择一个名称
3. 你会看到应用信息，复制 **Client ID** 下方的数字，后续邀请机器人时需要用到
4. 切换到页面左侧的 **Bot** 标签，然后点击 **Add Bot > Yes, do it!**
5. 你可以在此页面更改机器人的头像和名称
6. 点击 **Token** 区域下方的 **Copy** 按钮，将其用于 Spicord 配置
7. 邀请机器人时，访问 `https://discord.com/oauth2/authorize?scope=bot&permissions=8&client_id=YOUR_ID`，将 `YOUR_ID` 替换为第 3 步复制的 ID，这样生成的链接会让你的机器人拥有管理员权限

注意：你需要在开发者面板为机器人启用以下 Gateway Intents，否则机器人无法正常启动：
![Gateway intents](https://raw.githubusercontent.com/NewNanCity/HuskChat-Remake/master/images/spicord-bot-intents.png)

### 配置 HuskChat
创建并邀请机器人到服务器后，你可以在 HuskChat 配置中启用它。在 config 的 `discord:` 部分，将 `enabled` 和 `spicord.enabled` 都设置为 `true`。然后配置机器人和频道 ID。

要获取频道 ID，请确保在 Discord 设置中启用了开发者模式，然后右键频道选择“复制 ID”。将该 ID 粘贴到配置中并映射到对应的游戏内频道即可！

重启服务器，尽情享受吧！

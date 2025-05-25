# HuskChat Remake

> 🎉 **HuskChat Remake** 🎉 - This is a remake and improved version of the original HuskChat plugin, continued by a new maintenance team. We are committed to providing a better chat experience for Minecraft servers.

**[中文文档 / Chinese Documentation](README_zh.md)**

## Acknowledgments

We would like to give special thanks to [William278](https://william278.net/) for creating the original HuskChat plugin. This remake is based on his excellent work and has been modernized with improvements and feature extensions.

- Original HuskChat: [GitHub](https://github.com/WiIIiam278/HuskChat)
- Original Author: William278

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

**HuskChat Remake** is a clean, customizable chat system for Minecraft networks. It supports running on BungeeCord and Velocity frontend servers for cross-server chat, or on a single-server Spigot setup.

It's designed to be easy to configure with an elegant out-of-box setup, while also being highly configurable, suiting a variety of use cases by allowing you to define channels and manage who can send and receive messages within them.

## New Features and Improvements

**🚀 Extended Event System** &mdash; Complete event API for all chat types, supporting third-party plugin integration

**🚀 Enhanced API Interface** &mdash; Unified message sending, channel management, and user management API

**🚀 Player Status Integration** &mdash; Health, location, combat status, and other Minecraft-specific data integration

**🚀 Command Execution API** &mdash; Programmatic command execution with permission validation and event handling

**🚀 Modern Architecture** &mdash; Optimized for the latest Minecraft server platforms

**🚀 Better Extensibility** &mdash; Plugin-based design supporting custom extensions and integrations

**🚀 Chinese Character Support** &mdash; Default support for Chinese and other Unicode characters with optimized filter settings

## Features
**⭐ Works great out of the box** &mdash; Install on your Spigot server or Velocity/BungeeCord-based proxy and use right away

**⭐ Placeholder support** &mdash; Hooks with LuckPerms to display user roles, PAPI support via [PAPIProxyBridge](https://william278.net/docs/huskchat/formatting)

**⭐ Private messaging** &mdash; Private messages&mdash;including group DMs&mdash and replying, admin spy features

**⭐ Fine-tune channels** &mdash; Set send/receive permissions for channels, send messages to a discord webhook, filter [& more](https://william278.net/docs/huskchat/channels)!

**⭐ Quick and easy to use** &mdash; Super simple set of [commands](https://william278.net/docs/huskchat/commands). Define channel shortcut commands, too!

**⭐ Advanced profanity checking** &mdash; Machine learning powered profanity filter

**⭐ Filters & replacers** &mdash; Customisable spam limiting filter, anti-advertising & special emoji

**⭐ Modern formatting** &mdash;  Utilise modern formatting, with RGB and Gradient support via [MineDown](https://github.com/Phoenix616/MineDown)

## Supported Chat Types

HuskChat Remake supports multiple chat types, each with corresponding events and APIs:

### 📢 Channel Chat
- **Global Channels** - Cross-server chat
- **Local Channels** - Same-server player chat
- **Staff Channels** - Admin-only channels
- **Custom Channels** - Fully configurable channel system

### 💬 Private Messages
- **Direct Messages** - One-on-one private chat
- **Group Messages** - Multi-player group chat functionality
- **Reply Feature** - Quick reply to last received message

### 📣 Broadcast Messages
- **Server Broadcasts** - Server-wide announcements
- **Admin Notifications** - Admin-only broadcasts

### 🔗 Special Messages
- **Join/Quit Messages** - Player server entry/exit notifications
- **Discord Integration** - Sync with Discord channels
- **Social Spy** - Admin monitoring of private chats

## Developer API

HuskChat Remake provides a complete API for other plugins to use:

### 🎯 Event System
```java
// Listen to chat messages
api.registerChatMessageListener(event -> {
    if (event.getMessage().contains("spam")) {
        event.setCancelled(true);
    }
});

// Listen to player health changes
api.registerPlayerHealthChangeListener(event -> {
    if (event.isLowHealth()) {
        event.getPlayer().sendMessage("Warning: Low health!");
    }
});

// Listen to channel switches
api.registerChannelSwitchListener(event -> {
    player.sendMessage("Welcome to " + event.getNewChannelId() + " channel!");
});
```

### 🛠️ Channel Management API
```java
// Switch player channel
api.switchPlayerChannel(player, "staff", SwitchReason.API_CALL);

// Get players in channel
List<OnlineUser> players = api.getPlayersInChannel("global");
```

### 📨 Message Sending API
```java
// Send private message
api.sendPrivateMessage(sender, List.of("PlayerName"), "Hello!");

// Send channel message
api.sendChannelMessage("global", "System Announcement", null);
```

### 🎮 Player Status Integration
```java
// Get player information
PlayerInfo info = api.getPlayerInfo(player);
boolean isLowHealth = info.isLowHealth();
boolean isInCombat = info.isInCombat();

// Execute commands programmatically
api.executeChatCommand(player, "/channel", "staff");

// Check chat conditions
ChatConditionResult result = api.checkChatConditions(player, "global");
if (!result.isAllowed()) {
    player.sendMessage("Cannot chat: " + result.getReason());
}
```

## Building
To build HuskChat, you'll need python (>=`v3.6`) with associated packages installed; `jep` and `alt-profanity-check`.
You can install these with `pip install jep` and `pip install alt-profanity-check`. These are needed to run the profanity filter tests.

Then, simply run the following in the root of the repository:
```
./gradlew clean build
```

## License
HuskChat is licensed under the Apache 2.0 license.

- [License](https://github.com/NewNanCity/HuskChat-Remake/blob/master/LICENSE)

## Translations
Translations of the plugin locales are welcome to help make the plugin more accessible. Please submit a pull request with your translations as a `.yml` file.

- [Locales Directory](https://github.com/NewNanCity/HuskChat-Remake/tree/master/common/src/main/resources/locales)
- [English Locales](https://github.com/NewNanCity/HuskChat-Remake/tree/master/common/src/main/resources/locales/en-gb.yml)

## Documentation Links

### 📚 Developer Documentation
- [API Development Guide](docs/en/API-Guide.md) &mdash; Learn how to use HuskChat API
- [Event System Documentation](docs/en/Events.md) &mdash; Detailed event system explanation
- [Developer Guide](docs/en/Developer-Guide.md) &mdash; Contributing code and extending functionality
- [Example Plugin](docs/en/Example-Plugin.md) &mdash; Complete API usage examples
- [Channel Configuration](docs/en/Channels.md) &mdash; Channel configuration guide
- [Command Reference](docs/en/Commands.md) &mdash; Detailed command descriptions


### 🔗 Original Links
- [Original Documentation](https://william278.net/docs/huskchat/) &mdash; Original plugin documentation
- [Original Spigot Page](https://www.spigotmc.org/resources/huskchat.94496/) &mdash; Original resource page
- [Original GitHub](https://github.com/WiIIiam278/HuskChat) &mdash; Original source code

### 🆘 Getting Help
- [Issues](https://github.com/Gk0Wk/HuskChat-Remake/issues) &mdash; Report bugs or request features
- [Discussions](https://github.com/Gk0Wk/HuskChat-Remake/discussions) &mdash; Discussions and Q&A

---
&copy; [William278](https://william278.net/), 2024. Licensed under the Apache-2.0 License.

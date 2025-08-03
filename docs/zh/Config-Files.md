本页面包含 HuskChat 的配置结构说明。

## 配置结构
📁 `plugins/HuskChat/`
- 📄 `config.yml`：插件常规配置
- 📄 `channels.yml`：聊天[[频道]]配置
- 📄 `filters.yml`：聊天[[过滤器与替换器]]配置
- 📄 `user_cache.yml`：当前处于[[社交与本地监听]]模式下的用户缓存（此文件自动生成，无需手动编辑）
- 📄 `messages-xx-xx.yml`：插件语言文件，支持 MineDown 和 MiniMessage 格式（详见[[翻译]]）

## 新功能：MiniMessage 支持

HuskChat-Remake 现在支持两种文本格式化系统：
- **MineDown**：传统的 MarkDown 风格格式化（默认，向后兼容）
- **MiniMessage**：现代的标签风格格式化（Adventure 官方推荐）

详细使用说明请参考：[MiniMessage 支持示例配置](MiniMessage-Example-Config.md)

## 示例文件
<details>
<summary>config.yml</summary>

```yaml
# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
# ┃       HuskChat - Config      ┃
# ┃    Developed by William278   ┃
# ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
# ┣╸ Information: https://william278.net/project/huskchat/
# ┣╸ Config Help: https://william278.net/docs/huskchat/config-files/
# ┗╸ Documentation: https://william278.net/docs/huskchat/

# Locale of the default language file to use. Docs: https://william278.net/docs/huskclaims/translations
language: en-gb
# Whether to automatically check for plugin updates on startup
check_for_updates: true
# Whether to handle chat packets directly for better 1.19+ support (may cause rare compatibility issues)
use_packet_listening: true
# Placeholder settings
placeholder:
  # Use PlaceholderAPI. If you're on Bungee/Velocity, this requires PAPIProxyBridge installed
  use_papi: true
  # If using PAPIProxyBridge, how long to cache placeholders for (in milliseconds)
  cache_time: 3000
# Message comamnd settings
message_command:
  # Whether to enable the /msg command
  enabled: true
  # List of command aliases for /msg
  msg_aliases:
    - /msg
    - /m
    - /tell
    - /whisper
    - /w
    - /pm
  # List of command aliases for /reply
  reply_aliases:
    - /reply
    - /r
  # Whether to apply censorship filters on private messages
  censor: false
  # Whether to log private messages to the console
  log_to_console: true
  # Logging format for private messages
  log_format: '[MSG] [%sender% -> %receiver%]: '
  # Group private message settings
  group_messages:
    # Whether to enable group private messages (/msg Player1,Player2,...)
    enabled: true
    # Maximum amount of players in a group message
    max_size: 10
  # Formats for private messages (uses MineDown)
  format:
    inbound: '&e&l%name% &8→ &e&lYou&8: &f'
    outbound: '&e&lYou &8→ &e&l%name%&8: &f'
    group_inbound: '&e&l%name% &8→ &e&lYou[₍₊%group_amount_subscript%₎](gray show_text=&7%group_members%)&8:
      &f'
    group_outbound: '&e&lYou &8→ &e&l%name%[₍₊%group_amount_subscript%₎](gray show_text=&7%group_members%)&8:
      &f'
  # (Bungee/Velocity only) List of servers where private messages cannot be sent
  restricted_servers: []
# Social spy settings (see other users' private messages)
social_spy:
  enabled: true
  format: '&e[Spy] &7%name% &8→ &7%receiver_name%:%spy_color% '
  group_format: '&e[Spy] &7%name% &8→ &7%receiver_name% [₍₊%group_amount_subscript%₎](gray
    show_text=&7%group_members% suggest_command=/msg %group_members_comma_separated%
    ):%spy_color% '
  socialspy_aliases:
    - /socialspy
    - /ss
# (Bungee/Velocity only) Local spy settings (see local messages on other servers)
local_spy:
  enabled: true
  format: '&e[Spy] &7%name% &8→ &7%receiver_name%:%spy_color% '
  localspy_aliases:
    - /socialspy
    - /ss
  # List of channels to exclude from local spy
  excluded_local_channels: []
# Broadcast command settings
broadcast_command:
  enabled: true
  broadcast_aliases:
    - /broadcast
    - /alert
  format: '&6[Broadcast]&e '
  log_to_console: true
  log_format: '[BROADCAST]: '
# Join and quit message settings
join_and_quit_messages:
  # Use the "huskchat.join_message.[text]" permission to override this.
  # Use the "huskchat.silent_join" permission to silence for a user.
  join:
    enabled: true
    format: '&e%name% joined the network'
  # Use the "huskchat.quit_message.[text]" permission to override this.
  # Use the "huskchat.silent_quit" permission to silence for a user.
  quit:
    enabled: true
    format: '&e%name% left the network'
  # Note that on Velocity/Bungee, PASSTHROUGH modes won't cancel local join/quit messages
  broadcast_scope: GLOBAL
# Discord integration settings. Docs: https://william278.net/docs/huskchat/discord-hook
discord:
  # Enable hooking into Discord via Webhooks and/or Spicord
  enabled: false
  # Discord message format style (INLINE or EMBEDDED)
  format_style: INLINE
  # Send messages in channels to a webhook by mapped URL
  channel_webhooks: {}
  # Whether to hook into Spicord for two-way chat
  spicord:
    # Requires Spicord installed and "huskchat" added to the "addons" in config.toml
    enabled: true
    # Format of Discord users in-game. Note this doesn't support other placeholders
    username_format: '@%discord_handle%'
    # Send in-game messages on these channels to a specified Discord channel (by numeric ID)
    receive_channel_map:
      global: '123456789012345678'
    # Send Discord messages on these channels (by numeric ID) to a specified in-game channel
    send_channel_map:
      '123456789012345678': global
# Custom names to display wherever you use the "%server%" placeholder instead of their default name
server_name_replacement:
  very-long-server-name: VLSN
```
</details>

<details>
<summary>channels.yml</summary>

```yaml
# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
# ┃      HuskChat - Channels     ┃
# ┃    Developed by William278   ┃
# ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
# ┣╸ Information: https://william278.net/project/huskchat/
# ┗╸ Channels Help: https://william278.net/docs/huskchat/channels/

# The default chat channel players are placed in (can be overridden by server_default_channels)
default_channel: global
# Map of server names to a channel players will be automatically moved into when they join that server
server_default_channels:
  example: global
# The format of log messages (applies to channels with logging enabled)
channel_log_format: '[CHAT] [%channel%] %sender%: '
# Aliases for the /channel command
channel_command_aliases:
- channel
- c
# Channel definitions
channels:
- id: local
  format: '%fullname%&r&f: '
  broadcast_scope: LOCAL
  log_to_console: true
  restricted_servers: []
  filtered: true
  permissions: {}
  shortcut_commands:
  - /local
  - /l
- id: global
  format: '&#00fb9a&[G]&r&f %fullname%&r&f: '
  broadcast_scope: GLOBAL
  log_to_console: true
  restricted_servers: []
  filtered: true
  permissions: {}
  shortcut_commands:
  - /global
  - /g
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
- id: helpop
  format: '&#00fb9a&[HelpOp] %name%:&7'
  broadcast_scope: GLOBAL
  log_to_console: true
  restricted_servers: []
  filtered: false
  permissions:
    receive: huskchat.channel.helpop.receive
  shortcut_commands:
  - /helpop
  - /helpme
```
</details>

<details>
<summary>filters.yml</summary>

```yaml
# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
# ┃      HuskChat - Filters      ┃
# ┃    Developed by William278   ┃
# ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
# ┣╸ Information: https://william278.net/project/huskchat/
# ┗╸ Channels Help: https://william278.net/docs/huskchat/filters-and-replacers/

filters:
  CAPS:
    type: caps
    enabled: true
    channels:
      - global
      - local
    private_messages: true
    broadcast_messages: false
    max_caps_percentage: 0.4
  ADVERTISING:
    type: filter
    enabled: true
    channels:
      - global
      - local
    private_messages: true
    broadcast_messages: false
  ASCII:
    type: ascii
    enabled: false  # Disabled by default to support Unicode characters
    channels:
      - global
      - local
    private_messages: true
    broadcast_messages: false
  PROFANITY:
    type: profanity
    enabled: false
    channels:
      - global
      - local
    private_messages: true
    broadcast_messages: false
    library_path: ''
    mode: AUTOMATIC
    tolerance: 0.78
  REGEX:
    type: regex
    enabled: false
    channels:
      - global
      - local
    private_messages: true
    broadcast_messages: false
    patterns: []
  REPEAT:
    type: repeat
    enabled: true
    channels:
      - global
      - local
    private_messages: true
    broadcast_messages: false
    previous_messages_to_check: 2  # Reduced to minimize false positives
  SPAM:
    type: spam
    enabled: true
    channels:
      - global
      - local
    private_messages: true
    broadcast_messages: false
    period_seconds: 4
    messages_per_period: 3
replacers:
  EMOJI:
    type: emoji
    enabled: true
    channels:
      - global
      - local
    private_messages: true
    broadcast_messages: false
    case_insensitive: false
    emoji:
      ':heart:': ❤
      ':smile:': ☺
      :-(: ☹
      :-): ☺
      <3: ❤
      ':frown:': ☹
      ':star:': ⭐
      ':fire:': 🔥
      :(: ☹
      :): ☺
```
</details>
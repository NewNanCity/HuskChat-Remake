HuskChat 支持在玩家加入或离开你的网络（或单服环境下）时显示专属的**进服与离服消息**。

## 用法
要启用此功能，请在 `config.yml` 文件中将 `join_and_quit_messages.join.enabled` 和/或 `join_and_quit_messages.quit.enabled` 设置为 `true`。你可以自定义它们的 `format`，支持占位符和标准 MineDown 格式。

### 广播范围
你可以像为[[频道]]设置广播范围一样，为进服和离服消息设置 `broadcast_scope`。关于可用范围的详细说明，请参见[广播范围](channels#channel-scope)。

注意：全局、本地和常规 PASSTHROUGH 范围仅在插件运行于独立 Spigot/Paper 服务器时有效；当 HuskChat 运行在代理（Velocity/Bungee）服务器时，_常规进服/离服消息不会被取消_。这是因为进服/离服消息由后端服务器处理。

<details>
<summary>配置示例 config.yml</summary>

```yaml
# Options for customizing player join and quit messages
join_and_quit_messages:
  join:
    enabled: false
    # Use the huskchat.join_message.[text] permission to override this per-group if needed
    format: '&e%name% joined the network'
  quit:
    enabled: false
    # Use the huskchat.quit_message.[text] permission to override this per-group if needed
    format: '&e%name% left the network'
  broadcast_scope: GLOBAL # Note that on Velocity/Bungee, PASSTHROUGH modes won't cancel local join/quit messages
```
</details>

## 基于权限的格式
你可以通过 `huskchat.join_message.[文本]` 和 `huskchat.quit_message.[文本]` 权限为特定组设置专属进服/离服消息。例如，如果你想为 `vip` 组玩家设置特殊进服消息，可以给予他们 `huskchat.join_message.&a%name% has arrived with style!` 权限节点，以显示不同的进服消息。
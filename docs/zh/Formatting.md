频道的 `format` 属性定义了该频道消息的格式。玩家实际发送的消息内容会自动追加在格式内容之后。请注意，任何未清除的格式代码会持续作用于消息内容。私聊、群聊和广播的消息格式也可以自定义。

## MineDown
你可以使用 [MineDown 格式](https://github.com/Phoenix616/MineDown) 来实现现代（1.16+）十六进制颜色，并轻松使用渐变等高级文本效果。你也可以在 LuckPerms 组的前缀和后缀中嵌入这些格式。

## 占位符
在频道格式中，你可以使用以下占位符，这些占位符会被格式化后的文本替换。

### 常规占位符
* `%name%` - 用户名
* `%full_name%` - 角色前缀、玩家用户名和角色后缀
* `%prefix%` - 角色前缀
* `%suffix%` - 角色后缀
* `%role%` - 用户（主）组名
* `%role_display_name%` - 用户（主）组显示名
* `%ping%` - 用户延迟
* `%uuid%` - 用户 UUID
* `%server%` - 用户所在服务器（单服环境下始终为 `server`）
* `%local_players_online%` - 用户所在服务器的在线玩家数

### 时间占位符
这些占位符显示当前系统时间。
* `%timestamp%` - yyyy/MM/dd HH:mm:ss
* `%current_time%` - HH:mm:ss
* `%current_time_short%` - HH:mm
* `%current_date%` - yyyy/MM/dd
* `%current_date_uk%` - dd/MM/yyyy
* `%current_date_day%` - dd
* `%current_date_month%` - MM
* `%current_date_year%` - yyyy

### PlaceholderAPI 支持
在你的代理（Bungee 或 Velocity）和后端（Paper 或 Fabric）服务器上安装 [PAPIProxyBridge](https://modrinth.com/plugin/papiproxybridge) 后，可以在频道和消息格式中使用 PlaceholderAPI 占位符。

### 私聊消息占位符
在私聊消息中，入站消息的占位符应用于消息发送者，出站消息的占位符应用于接收者。你可以在上述所有占位符前加 `sender_` 或 `receiver_` 前缀使用（如 `%sender_(占位符)%` 和 `%receiver_(占位符)%`）。

群聊消息还支持以下额外占位符：
* `%group_amount%`（群聊成员数量）
* `%group_amount_subscript%`（群聊成员数量，下标字体）
* `%group_members_comma_separated%`（群聊成员逗号分隔列表）
* `%group_members%`（群聊成员换行分隔列表）

社交监听消息格式允许你同时格式化消息发送者和接收者，使用上述同样的占位符。发送者和接收者通过前缀区分，因此你可以同时使用 `%sender_(占位符)%` 和 `%receiver_(占位符)%`。
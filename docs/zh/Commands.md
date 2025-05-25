HuskChat 提供了多种命令，用于切换频道、发送广播以及私聊玩家或玩家组。频道还可以通过发送和接收权限节点进行限制。

## 命令列表
| 命令         | 用法                        | 别名                                               | 描述                           | 权限                                             |
| ------------ | --------------------------- | -------------------------------------------------- | ------------------------------ | ------------------------------------------------ |
| `/channel`   | `/channel <名称> [消息]`    | `/c`                                               | 发送消息或切换聊天频道         | `huskchat.command.channel`                       |
| `/huskchat`  | `/huskchat <about\|reload>` | 无                                                 | 查看插件信息和重载             | `huskchat.command.huskchat`                      |
| `/msg`       | `/msg <玩家(们)> <消息>`    | `/m`, `/tell`, `/w`, `/whisper`, `/message`, `/pm` | 向玩家发送私聊消息             | `huskchat.command.msg`                           |
| `/reply`     | `/reply <消息>`             | `/r`                                               | 快速回复一条私聊消息           | `huskchat.command.msg.reply`                     |
| `/socialspy` | `/socialspy [颜色]`         | `/ss`                                              | 查看其他用户的私聊消息         | `huskchat.command.socialspy`                     |
| `/localspy`  | `/localspy [颜色]`          | `/ls`                                              | 查看其他本地频道的消息&dagger; | `huskchat.command.localspy`                      |
| `/broadcast` | `/broadcast <消息>`         | `/alert`                                           | 在服务器上发送广播消息         | `huskchat.command.broadcast`                     |
| `/optoutmsg` | `/optoutmsg`                | 无                                                 | 退出你所在的群组私聊           | `huskchat.command.optoutmsg`                     |
| 快捷命令     | `/<命令> <消息>`            | 无                                                 | 快速在频道中发送消息或切换频道 | 频道发送权限，例如 `huskchat.channel.staff.send` |

&dagger; `/localspy` 在单服 Spigot 环境下不可用。

## 频道发送与接收权限
频道拥有各自的发送和接收权限。

你可以在频道配置文件中进行设置，默认权限为 `huskchat.channel.<channel>.receive`。未设置权限的频道无需权限节点即可发言。

## 聊天格式化权限
消息格式化也有专属权限，允许用户使用 minedown 格式。

你可以赋予 `huskchat.formatted_chat` 权限节点，让玩家可以用 Minedown 格式化他们的消息。没有该权限的用户尝试使用格式化时，消息会以普通文本发送，实际显示效果将采用配置文件中的格式。
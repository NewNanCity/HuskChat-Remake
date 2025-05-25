`filtered:` 属性用于指定频道中的消息是否应首先经过在专用 [`filters.yml`](config-files) 文件中 `chat_filters` 和 `message_replacers` 部分定义并启用的过滤器和消息替换器。要使用过滤器，请确保你希望过滤的频道已启用 `filtered`，并且聊天过滤器已正确启用和配置。

## 替换器
消息替换器会更改消息内容，例如将特定字符组合替换为表情符号。

* `emoji_replacer` - 将特定字符字符串替换为对应的 Unicode 表情符号。注意，即使启用了 `ascii_filter`，此功能仍可正常工作，并在聊天中显示 Unicode 表情。

## 过滤器
聊天过滤器会根据特定条件阻止用户发送消息。

* `advertising_filter` - 阻止玩家发送包含 IP 或网址的消息。
* `caps_filter` - 阻止玩家发送大写字母占比超过指定百分比（小数，0.0~1.0，代表0%~100%）的消息。
* `spam_filter` - 阻止玩家在聊天中过快发送消息（即限流）。可指定玩家在一定时间内可发送的消息数量。
* `profanity_filter` - 使用机器学习算法检测消息中是否包含英文脏话。详见下文设置方法（需要额外配置）。
* `repeat_filter` - 阻止玩家发送重复消息。会检查玩家之前指定数量的消息（默认：2 条）。
* `ascii_filter` - 阻止玩家在聊天中使用非 ASCII（即 Unicode/UTF-8）字符。**注意：默认禁用此过滤器**，以支持中文、日文、韩文等国际字符。仅在需要限制为纯 ASCII 聊天时启用。

### 绕过过滤器
你可以通过 `huskchat.bypass_filters` 权限让用户的消息不经过过滤器（但仍会经过替换器）。

此外，可以使用 `huskchat.ignore_filters.<filter_name>` 节点让用户绕过特定过滤器。该权限在所有频道均有效。
* `huskchat.ignore_filters.advertising` - 广告过滤器
* `huskchat.ignore_filters.caps` - 大写过滤器
* `huskchat.ignore_filters.spam` - 垃圾消息过滤器
* `huskchat.ignore_filters.profanity` - 脏话过滤器
* `huskchat.ignore_filters.repeat` - 重复消息过滤器
* `huskchat.ignore_filters.ascii` - ASCII 过滤器
* `huskchat.ignore_filters.regex` - 正则过滤器

你还可以通过以下权限禁用单独的替换器类型：
* `huskchat.ignore_filters.emoji_replacer` - 表情符号替换器

## 脏话过滤器
`profanity_filter` 使用 Python 机器学习算法（alt-profanity-check，基于 Scikit-learn）判断消息是否包含脏话。该算法并不完美，无法检测变形或拉长的脏话，但效果较好（毕竟如果有人执意要骂人，总能绕过任何脏话过滤器）。该过滤器仅对英文有效。

### 虚拟主机
如果你使用的是**虚拟主机**，很可能无法使用此功能，除非你的主机商非常配合。由于配置较为复杂，此功能仅推荐给**高级用户**，官方仅能提供文档指引，无法提供更多支持。

### 设置方法
要使用此功能，你需要在服务器上安装 Python 3.8+ 和 Jep，并确保 Jep 驱动已正确加入 Java classpath。可通过 `pip install jep` 安装 Jep。还需运行 `pip install alt-profanity-check` 安装脏话检测器及其依赖。

然后需要确保 HuskChat 能识别你的 Jep 驱动路径（如系统未自动识别）。Jep 驱动名称因平台而异：Linux 为 `libjep.so`，macOS 为 `libjep.jnilib`，Windows 为 `jep.dll`。

可通过以下方式之一指定路径：
* 将 Jep 的库路径加入 Java 库环境变量
    - Linux：`LD_LIBRARY_PATH`
    - macOS：`DYLD_LIBRARY_PATH`
    - Windows：`PATH`
* 启动命令添加 `-Djava.library.path=<路径>` 参数
* 在 HuskChat 配置的 `library_path` 选项中指定 Jep 驱动路径

路径应指向包含 jep 驱动的文件夹，而不是驱动本身。如果启动脏话过滤器时报错，可参考 [Jep 官方 FAQ](https://github.com/ninia/jep/wiki/FAQ#how-do-i-fix-unsatisfied-link-error-no-jep-in-javalibrarypath) 进行排查。

### 使用方法
当你完成上述依赖安装且服务器能正常启动脏话检测器后，可调整相关设置。默认情况下，检测器使用 `AUTOMATIC` 模式判断消息是否包含脏话。如果你想微调检测灵敏度，可将 `mode` 设置为 `TOLERANCE` 并调整下方的 `tolerance` 值。值越低，检测越严格。

## 配置建议

### 国际化服务器
对于国际用户较多的服务器，建议如下设置：
- 保持 ASCII 过滤器关闭（默认），以支持 Unicode 字符
- 适当调整重复消息检测数量（默认：2）
- 视需求启用其他过滤器

### 严格管理服务器
如需严格管理聊天，建议如下设置：
```yaml
filters:
  ASCII:
    enabled: true  # 启用 ASCII 过滤器，限制 Unicode 字符
  REPEAT:
    previous_messages_to_check: 5  # 增加重复消息检测数量
  CAPS:
    enabled: true  # 启用大写过滤器
```

### 常用过滤器权限
- `huskchat.ignore_filters.ascii` - 绕过 ASCII 字符过滤
- `huskchat.ignore_filters.repeat` - 绕过重复消息过滤
- `huskchat.ignore_filters.caps` - 绕过大写过滤
- `huskchat.ignore_filters.spam` - 绕过垃圾消息过滤
- `huskchat.ignore_filters.profanity` - 绕过脏话过滤

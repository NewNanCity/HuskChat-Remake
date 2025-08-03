# MiniMessage 支持示例配置

本文档展示了如何在 HuskChat-Remake 中使用新的 MiniMessage 支持功能。

## 配置文件示例

### config.yml 新增配置

```yaml
# 文本格式化设置
text_formatting:
  # 默认文本格式类型。选项: 'minedown' (默认), 'minimessage'
  default_format: "minedown"
  
  # 是否启用严格解析模式（更多错误检查）
  strict_mode: false
  
  # 当格式化失败时是否向用户显示解析错误
  show_parse_errors: false
```

### 使用 MiniMessage 的配置示例

```yaml
# 启用 MiniMessage 格式
text_formatting:
  default_format: "minimessage"
  strict_mode: true
  show_parse_errors: true
```

## 格式语法对比

### 基础颜色

| 功能 | MineDown | MiniMessage |
|------|----------|-------------|
| 红色文本 | `&red&Hello` | `<red>Hello</red>` |
| 十六进制颜色 | `&#ff0000&Hello` | `<#ff0000>Hello</#ff0000>` |

### 文本装饰

| 功能 | MineDown | MiniMessage |
|------|----------|-------------|
| 粗体 | `**Bold**` | `<bold>Bold</bold>` |
| 斜体 | `##Italic##` | `<italic>Italic</italic>` |
| 下划线 | `__Underline__` | `<underlined>Underline</underlined>` |
| 删除线 | `~~Strike~~` | `<strikethrough>Strike</strikethrough>` |
| 混淆 | `??Obfuscated??` | `<obfuscated>Obfuscated</obfuscated>` |

### 高级效果

| 功能 | MineDown | MiniMessage |
|------|----------|-------------|
| 渐变色 | `&#ff0000-#00ff00&Gradient` | `<gradient:#ff0000:#00ff00>Gradient</gradient>` |
| 彩虹色 | `&rainbow&Rainbow` | `<rainbow>Rainbow</rainbow>` |
| 彩虹相位 | `&rainbow:20&Rainbow` | `<rainbow:2>Rainbow</rainbow>` |

### 交互事件

| 功能 | MineDown | MiniMessage |
|------|----------|-------------|
| 点击运行命令 | `[Click](run_command=/help)` | `<click:run_command:/help>Click</click>` |
| 点击建议命令 | `[Click](suggest_command=/msg )` | `<click:suggest_command:/msg >Click</click>` |
| 悬停显示文本 | `[Hover](hover=Tooltip)` | `<hover:show_text:'Tooltip'>Hover</hover>` |

## 频道配置示例

### MineDown 格式的频道

```yaml
channels:
  global:
    id: "global"
    format: "&7[&bGlobal&7] &f%full_name%&8: &f"
    broadcast_scope: "GLOBAL"
```

### MiniMessage 格式的频道

```yaml
channels:
  global:
    id: "global"
    format: "<gray>[<aqua>Global</aqua>]</gray> <white>%full_name%</white><dark_gray>:</dark_gray> <white>"
    broadcast_scope: "GLOBAL"
```

## 私信格式示例

### MineDown 格式

```yaml
message_command:
  format:
    inbound: "&e&l%name% &8→ &e&lYou&8: &f"
    outbound: "&e&lYou &8→ &e&l%name%&8: &f"
```

### MiniMessage 格式

```yaml
message_command:
  format:
    inbound: "<yellow><bold>%name%</bold></yellow> <dark_gray>→</dark_gray> <yellow><bold>You</bold></yellow><dark_gray>:</dark_gray> <white>"
    outbound: "<yellow><bold>You</bold></yellow> <dark_gray>→</dark_gray> <yellow><bold>%name%</bold></yellow><dark_gray>:</dark_gray> <white>"
```

## 迁移指南

### 从 MineDown 迁移到 MiniMessage

1. **备份现有配置**
   ```bash
   cp config.yml config.yml.backup
   cp channels.yml channels.yml.backup
   ```

2. **更新配置文件**
   ```yaml
   text_formatting:
     default_format: "minimessage"
   ```

3. **转换格式语法**
   
   **颜色代码转换：**
   - `&red&` → `<red>`
   - `&#ff0000&` → `<#ff0000>`
   
   **装饰转换：**
   - `**text**` → `<bold>text</bold>`
   - `##text##` → `<italic>text</italic>`
   - `__text__` → `<underlined>text</underlined>`
   
   **事件转换：**
   - `[text](run_command=/cmd)` → `<click:run_command:/cmd>text</click>`
   - `[text](hover=tooltip)` → `<hover:show_text:'tooltip'>text</hover>`

4. **测试配置**
   - 重启服务器
   - 测试聊天消息格式化
   - 验证颜色和装饰效果

## 兼容性说明

### 向后兼容性

- 默认情况下，插件仍使用 MineDown 格式
- 现有配置文件无需修改即可正常工作
- 用户权限 `huskchat.formatted_chat` 同时支持两种格式

### 权限系统

- `huskchat.formatted_chat` - 允许用户使用格式化聊天（支持当前配置的格式类型）

### 性能考虑

- MiniMessage 通常比 MineDown 有更好的解析性能
- 两种格式可以并存，不会产生冲突
- 格式化器在插件启动时初始化，运行时开销最小

## 故障排除

### 常见问题

1. **格式化不生效**
   - 检查 `text_formatting.default_format` 配置
   - 确认用户有 `huskchat.formatted_chat` 权限
   - 验证语法是否正确

2. **解析错误**
   - 启用 `show_parse_errors: true` 查看详细错误
   - 检查标签是否正确闭合
   - 验证颜色代码格式

3. **性能问题**
   - 避免过度复杂的格式化
   - 考虑禁用 `strict_mode` 以提高性能
   - 监控服务器日志中的错误信息

### 调试技巧

1. **测试格式化**
   ```
   /huskchat reload  # 重载配置
   ```

2. **验证语法**
   - 使用在线 MiniMessage 预览工具
   - 逐步添加格式化元素进行测试

3. **日志检查**
   - 查看服务器启动日志
   - 检查是否有格式化相关错误

## 最佳实践

### 格式设计建议

1. **保持简洁**
   - 避免过度使用颜色和效果
   - 确保文本可读性

2. **一致性**
   - 在整个服务器中使用一致的颜色方案
   - 建立格式化标准

3. **性能优化**
   - 避免在高频消息中使用复杂格式
   - 合理使用渐变和动画效果

4. **用户体验**
   - 提供清晰的格式化指南
   - 考虑色盲用户的需求

这个新功能为 HuskChat-Remake 带来了现代化的文本格式化能力，同时保持了完全的向后兼容性。

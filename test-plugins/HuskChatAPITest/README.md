# HuskChat API 测试插件

![Java](https://img.shields.io/badge/Java-17-green) ![Bukkit](https://img.shields.io/badge/Bukkit-1.20+-green) ![License](https://img.shields.io/badge/License-MIT-green.svg)

**HuskChatAPITest** 是一个全面的测试插件，用于验证和演示 HuskChat Remake 的所有 API 功能。

---

## 🎯 功能特性

### ✅ 全面的API测试
- **事件系统测试**：验证聊天事件、频道切换事件、玩家状态事件
- **频道管理测试**：测试频道列表、信息获取、成员管理、权限检查
- **消息发送测试**：验证频道消息、私聊消息、广播消息、格式化消息
- **玩家状态测试**：测试健康状态、位置信息、游戏模式、战斗状态集成
- **命令执行测试**：验证频道命令、私聊命令、管理命令、权限检查
- **过滤器测试**：测试脏话过滤、占位符替换、自定义过滤器、消息格式化
- **Discord集成测试**：验证Discord连接、消息同步、命令处理
- **权限系统测试**：测试权限检测、基本权限、频道权限、权限组集成

### 🎮 交互式API演示
- **实时API调用**：通过命令直接调用HuskChat API
- **状态查看**：查看频道、用户、权限等实时状态
- **功能演示**：演示消息发送、频道切换、格式化等功能

### 📊 详细的测试报告
- **测试结果统计**：成功/失败统计和详细信息
- **性能监控**：测试执行时间和性能数据
- **错误诊断**：详细的错误信息和调试日志

---

## 🚀 快速开始

### 前置要求
- Minecraft 1.17+ 服务器（Bukkit/Spigot/Paper）
- Java 17+
- HuskChat Remake 3.0.4+

### 安装步骤

#### 1. 构建插件
```bash
# Windows
build.bat

# Linux/Mac
chmod +x build.sh
./build.sh
```

#### 2. 安装插件
1. 确保服务器已安装 HuskChat Remake
2. 将 `target/HuskChatAPITest-1.0.0.jar` 放入服务器的 `plugins/` 目录
3. 重启服务器

#### 3. 验证安装
```
/hctest help    # 查看测试命令
/hcapi help     # 查看API演示命令
```

---

## 📋 命令使用

### 测试命令 (`/hctest`)

#### 基础测试
```bash
/hctest basic           # 运行基础测试套件
/hctest full            # 运行完整测试套件
/hctest single <测试名>  # 运行单个测试
```

#### 玩家测试
```bash
/hctest player <玩家名>  # 运行玩家相关测试
```

#### 测试管理
```bash
/hctest status          # 显示测试状态
/hctest report          # 显示详细测试报告
/hctest list            # 列出可用测试
/hctest reset           # 重置测试结果
```

### API演示命令 (`/hcapi`)

#### 信息查看
```bash
/hcapi info             # 显示API信息
/hcapi channels         # 显示所有频道
/hcapi users            # 显示在线用户
/hcapi status [玩家]    # 显示玩家状态
```

#### 功能演示
```bash
/hcapi send <频道> <消息>     # 发送频道消息
/hcapi broadcast <消息>       # 发送广播消息
/hcapi switch <频道>          # 切换频道
/hcapi permissions [玩家]     # 检查权限
```

#### 测试功能
```bash
/hcapi format <消息>          # 测试消息格式化
/hcapi filter <消息>          # 测试消息过滤
```

---

## 🧪 测试类型

### 1. 事件系统测试 (`event_system`)
- 事件监听器注册
- 聊天消息事件
- 频道切换事件
- 玩家状态事件
- 事件取消功能

### 2. 频道管理测试 (`channel_management`)
- 频道列表获取
- 频道信息查询
- 频道成员管理
- 频道权限检查
- 频道状态管理

### 3. 消息发送测试 (`message_sending`)
- 频道消息发送
- 私聊消息发送
- 广播消息发送
- 格式化消息发送
- 消息发送权限检查

### 4. 玩家状态测试 (`player_status`)
- 玩家基本信息获取
- 健康状态监控
- 位置信息追踪
- 游戏模式检测
- 战斗状态判断

### 5. 命令执行测试 (`command_execution`)
- 频道命令执行
- 私聊命令执行
- 管理命令执行
- 命令权限检查
- 命令结果验证

### 6. 过滤器测试 (`filter_replacer`)
- 脏话过滤功能
- 占位符替换
- 自定义过滤器
- 消息格式化

### 7. Discord集成测试 (`discord_integration`)
- Discord连接状态
- 消息发送到Discord
- Discord消息接收
- 频道同步
- 命令处理

### 8. 权限系统测试 (`permission_system`)
- 权限系统检测
- 基本权限检查
- 频道权限验证
- 权限组集成
- 权限缓存测试

---

## 🔧 权限节点

```yaml
permissions:
  huskchat.test.*:
    description: 所有HuskChat测试权限
    default: op
    children:
      huskchat.test.admin: true
      huskchat.test.api: true
      huskchat.test.events: true
      
  huskchat.test.admin:
    description: HuskChat测试管理员权限
    default: op
    
  huskchat.test.api:
    description: HuskChat API使用权限
    default: op
    
  huskchat.test.events:
    description: HuskChat事件测试权限
    default: op
```

---

## 📊 测试报告示例

```
=== HuskChat API 测试报告 ===
✅ api_connection: API连接正常，发现 3 个频道，2 个在线用户
✅ event_system: 事件系统测试通过，监听器已注册，事件触发正常
✅ channel_management: 频道管理API测试通过，所有频道操作正常
✅ message_sending: 消息发送API测试通过，所有消息类型发送正常
❌ discord_integration: Discord集成测试失败 - Discord未配置
✅ permission_system: 权限系统测试通过，权限检查和集成正常

测试完成: 5 通过, 1 失败
========================
```

---

## 🐛 故障排除

### 常见问题

#### 1. 插件无法加载
- 检查 HuskChat Remake 是否已正确安装
- 确认 Java 版本为 17+
- 查看控制台错误日志

#### 2. API调用失败
- 确认 HuskChat 插件已完全加载
- 检查 HuskChat 配置是否正确
- 验证权限设置

#### 3. 测试失败
- 检查服务器环境是否满足要求
- 确认有足够的在线玩家进行测试
- 查看详细错误日志

### 调试技巧

#### 启用详细日志
插件会自动输出详细的测试日志，包括：
- API调用结果
- 事件触发情况
- 错误详细信息
- 性能统计数据

#### 查看测试状态
```bash
/hctest status    # 查看当前测试状态
/hctest report    # 查看详细测试报告
```

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个测试插件！

### 开发环境
- Java 17+
- Maven 3.6+
- IntelliJ IDEA 或 Eclipse

### 构建项目
```bash
git clone <repository>
cd HuskChatAPITest
mvn clean package
```

---

## 📄 许可证

本项目基于 MIT 许可证开源。

---

## 🔗 相关链接

- [HuskChat Remake](https://github.com/NewNanCity/HuskChat-Remake)
- [HuskChat 文档](https://william278.net/docs/huskchat/)
- [问题反馈](https://github.com/NewNanCity/HuskChat-Remake/issues)

---

**注意**：这是一个测试插件，主要用于开发和调试目的。在生产环境中使用时请谨慎。

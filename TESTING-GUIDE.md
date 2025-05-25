# HuskChat Remake 测试指南

## 🚀 快速开始

### 构建项目

#### 使用Maven构建
```bash
# Windows
build-maven.bat

# Linux/Mac
chmod +x build-maven.sh
./build-maven.sh
```

#### 使用Gradle构建（原版）
```bash
./gradlew clean build
```

## 📦 构建产物

构建完成后，您将获得以下插件文件：

### Maven构建产物
- `bukkit/target/HuskChat-Bukkit-3.0.4.jar` - Bukkit/Spigot服务器版本
- `velocity/target/HuskChat-Velocity-3.0.4.jar` - Velocity代理服务器版本
- `bungee/target/HuskChat-BungeeCord-3.0.4.jar` - BungeeCord代理服务器版本
- `paper/target/HuskChat-Paper-3.0.4.jar` - Paper服务器特定版本
- `plugin/target/HuskChat-3.0.4.jar` - 完整版本（包含所有平台）

### Gradle构建产物
- `target/HuskChat-Bukkit-3.0.4.jar`
- `target/HuskChat-Velocity-3.0.4.jar`
- `target/HuskChat-Bungee-3.0.4.jar`
- `target/HuskChat-Paper-3.0.4.jar`

## 🧪 测试环境搭建

### 1. 单服务器测试（Bukkit/Paper）

**环境要求：**
- Minecraft 1.17+ 服务器
- Java 17+
- Paper/Spigot/Bukkit

**安装步骤：**
1. 将 `HuskChat-Bukkit-3.0.4.jar` 放入 `plugins/` 目录
2. 启动服务器
3. 检查插件是否正常加载

**基础测试：**
```
/huskchat reload          # 重载配置
/channel global           # 切换到全局频道
Hello World!              # 发送聊天消息
/msg <player> Hello       # 发送私聊消息
/reply Hi there           # 回复私聊
```

### 2. 代理服务器测试（Velocity）

**环境要求：**
- Velocity 3.3.0+
- 2-3个后端Paper服务器
- Java 17+

**安装步骤：**
1. Velocity服务器：安装 `HuskChat-Velocity-3.0.4.jar`
2. 后端服务器：安装 `HuskChat-Bukkit-3.0.4.jar`
3. 配置Velocity的 `velocity.toml`
4. 启动所有服务器

**跨服测试：**
```
/server lobby             # 切换到lobby服务器
Hello from lobby!         # 发送消息
/server survival          # 切换到survival服务器
Hello from survival!      # 验证跨服聊天
```

### 3. BungeeCord测试

**环境要求：**
- BungeeCord 1.19+
- 2-3个后端Spigot/Paper服务器
- Java 17+

**安装步骤：**
1. BungeeCord服务器：安装 `HuskChat-BungeeCord-3.0.4.jar`
2. 后端服务器：安装 `HuskChat-Bukkit-3.0.4.jar`
3. 配置BungeeCord的 `config.yml`
4. 启动所有服务器

## 🎯 功能测试清单

### ✅ 基础聊天功能
- [ ] 频道聊天（全局、本地、管理员）
- [ ] 私聊系统（一对一、群聊）
- [ ] 消息回复功能
- [ ] 频道切换
- [ ] 聊天格式化（颜色、样式）

### ✅ 高级功能
- [ ] 脏话过滤
- [ ] 占位符支持（LuckPerms、PAPI）
- [ ] Discord集成
- [ ] 社交间谍功能
- [ ] 本地间谍功能
- [ ] 广播系统

### ✅ 跨平台功能
- [ ] 跨服务器聊天
- [ ] 玩家状态同步
- [ ] 频道状态保持
- [ ] 服务器切换时的状态维护

### ✅ API功能
- [ ] 事件系统
- [ ] 频道管理API
- [ ] 消息发送API
- [ ] 玩家状态API
- [ ] 命令执行API

## 🔧 配置测试

### 基础配置文件
检查以下配置文件是否正确生成：
- `plugins/HuskChat/config.yml`
- `plugins/HuskChat/channels.yml`
- `plugins/HuskChat/filters.yml`
- `plugins/HuskChat/locales/`

### 权限测试
测试以下权限节点：
```
huskchat.command.channel          # 频道命令
huskchat.command.message          # 私聊命令
huskchat.command.broadcast        # 广播命令
huskchat.command.socialspy        # 社交间谍
huskchat.channel.global.send      # 全局频道发送
huskchat.channel.global.receive   # 全局频道接收
```

## 🐛 常见问题排查

### 1. 插件无法加载
- 检查Java版本（需要17+）
- 检查服务器版本兼容性
- 查看控制台错误日志

### 2. 跨服聊天不工作
- 确认代理服务器和后端服务器都安装了插件
- 检查插件消息通道配置
- 验证服务器间网络连接

### 3. 权限问题
- 确认权限插件（如LuckPerms）正确安装
- 检查权限节点配置
- 验证玩家权限分配

### 4. 格式化问题
- 检查MineDown语法
- 验证占位符插件安装
- 确认配置文件格式正确

## 📊 性能测试

### 内存使用
- 监控插件内存占用
- 检查是否有内存泄漏
- 测试大量玩家同时在线的情况

### 网络性能
- 测试跨服消息延迟
- 监控网络带宽使用
- 验证消息传递的可靠性

### 数据库性能（如果使用）
- 测试数据库连接稳定性
- 监控查询性能
- 验证数据持久化

## 🔍 调试技巧

### 启用调试模式
在配置文件中设置：
```yaml
debug: true
```

### 查看详细日志
```bash
tail -f logs/latest.log | grep HuskChat
```

### 使用开发者工具
- 启用详细的错误报告
- 使用插件的调试命令
- 监控事件触发情况

## 📝 测试报告模板

```markdown
## 测试环境
- 服务器类型：[Bukkit/Paper/Velocity/BungeeCord]
- 服务器版本：[版本号]
- Java版本：[版本号]
- 插件版本：3.0.4

## 测试结果
- [ ] 基础聊天功能
- [ ] 跨服功能
- [ ] API功能
- [ ] 性能表现

## 发现的问题
1. [问题描述]
2. [问题描述]

## 建议改进
1. [改进建议]
2. [改进建议]
```

---

**注意：** 在生产环境部署前，请务必在测试环境中充分验证所有功能！

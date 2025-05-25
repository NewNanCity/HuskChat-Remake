# HuskChat API 测试插件构建和使用指南

## 📋 概述

本指南将帮助你构建和使用 HuskChat API 测试插件，全面测试 HuskChat Remake 的所有 API 功能。

---

## 🏗️ 构建步骤

### 1. 环境准备

#### 必需软件
- **Java 17+** - 编译和运行环境
- **Maven 3.6+** - 构建工具
- **Git** - 版本控制（可选）

#### 验证环境
```bash
java -version    # 应显示 17 或更高版本
mvn -version     # 应显示 3.6 或更高版本
```

### 2. 构建插件

#### 方法一：使用构建脚本（推荐）
```bash
# Windows
cd test-plugins/HuskChatAPITest
build.bat

# Linux/Mac
cd test-plugins/HuskChatAPITest
chmod +x build.sh
./build.sh
```

#### 方法二：手动构建
```bash
cd test-plugins/HuskChatAPITest
mvn clean compile package
```

### 3. 验证构建结果
构建成功后，应该在 `target/` 目录下看到：
- `HuskChatAPITest-1.0.0.jar` - 最终插件文件

---

## 🚀 安装和配置

### 1. 服务器准备

#### 前置要求
- Minecraft 1.17+ 服务器（Bukkit/Spigot/Paper）
- HuskChat Remake 3.0.4+ 已安装并正常运行

#### 验证 HuskChat 安装
```bash
# 在服务器控制台执行
/plugins
# 应该看到 HuskChat 在插件列表中
```

### 2. 安装测试插件

#### 步骤
1. 将 `HuskChatAPITest-1.0.0.jar` 复制到服务器的 `plugins/` 目录
2. 重启服务器或使用插件管理器重载
3. 检查插件是否正确加载

#### 验证安装
```bash
# 在游戏中或控制台执行
/plugins
# 应该看到 HuskChatAPITest 在插件列表中

# 测试基本命令
/hctest help
/hcapi help
```

---

## 🧪 测试使用

### 1. 基础测试流程

#### 自动测试（推荐新手）
```bash
# 运行基础测试套件
/hctest basic

# 运行完整测试套件（需要玩家参与）
/hctest full

# 查看测试结果
/hctest report
```

#### 单项测试（推荐开发者）
```bash
# 列出所有可用测试
/hctest list

# 运行特定测试
/hctest single api_connection
/hctest single event_system
/hctest single channel_management
/hctest single message_sending
```

### 2. 玩家相关测试

#### 需要玩家参与的测试
```bash
# 测试当前玩家
/hctest player

# 测试指定玩家
/hctest player <玩家名>

# 单独测试玩家功能
/hctest single player_status
/hctest single command_execution
/hctest single filter_replacer
```

### 3. API 功能演示

#### 查看系统状态
```bash
# 显示 API 基本信息
/hcapi info

# 显示所有频道
/hcapi channels

# 显示在线用户
/hcapi users

# 显示玩家状态
/hcapi status [玩家名]
```

#### 测试消息功能
```bash
# 发送频道消息
/hcapi send global 这是一条测试消息

# 发送广播消息
/hcapi broadcast 服务器广播测试

# 切换频道
/hcapi switch local

# 测试消息格式化
/hcapi format &a绿色文本 &c&l红色粗体
```

#### 测试权限系统
```bash
# 检查当前玩家权限
/hcapi permissions

# 检查指定玩家权限
/hcapi permissions <玩家名>
```

---

## 📊 测试结果解读

### 1. 测试状态

#### 成功标识
- ✅ **绿色对勾** - 测试通过
- 📊 **详细信息** - 测试执行的具体数据

#### 失败标识
- ❌ **红色叉号** - 测试失败
- 🔍 **错误详情** - 失败原因和调试信息

### 2. 常见测试结果

#### API 连接测试
```
✅ api_connection: API连接正常，发现 3 个频道，2 个在线用户
```
- 表示 HuskChat API 可以正常访问
- 显示当前频道数量和在线用户数

#### 事件系统测试
```
✅ event_system: 事件系统测试通过，监听器已注册，事件触发正常
```
- 表示事件监听器注册成功
- 事件可以正常触发和处理

#### 频道管理测试
```
✅ channel_management: 频道管理API测试通过，所有频道操作正常
```
- 频道列表获取正常
- 频道切换功能正常
- 权限检查正常

### 3. 性能数据

#### 执行时间
```
执行时间: 1250 ms
```
- 显示测试执行耗时
- 帮助识别性能问题

---

## 🔧 故障排除

### 1. 构建问题

#### 编译错误
```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version

# 清理并重新构建
mvn clean compile package
```

#### 依赖问题
- 确保网络连接正常，Maven 可以下载依赖
- 检查 `pom.xml` 中的仓库配置
- 尝试使用 `-U` 参数强制更新依赖

### 2. 运行时问题

#### 插件无法加载
1. 检查 HuskChat 是否已正确安装
2. 确认服务器版本兼容性
3. 查看服务器控制台错误日志

#### API 调用失败
1. 确认 HuskChat 插件已完全启动
2. 检查插件加载顺序（HuskChat 应该先于测试插件加载）
3. 验证权限配置

#### 测试失败
1. 检查是否有足够的在线玩家
2. 确认测试环境配置正确
3. 查看详细错误日志

### 3. 调试技巧

#### 启用详细日志
插件会自动输出详细日志，包括：
- API 调用过程
- 事件触发情况
- 错误堆栈信息

#### 逐步测试
```bash
# 先测试基础功能
/hctest single api_connection

# 再测试具体功能
/hctest single channel_management

# 最后测试复杂功能
/hctest single discord_integration
```

---

## 📈 高级使用

### 1. 自定义测试

#### 修改测试参数
可以通过修改源代码来自定义测试：
- 调整测试超时时间
- 添加新的测试用例
- 修改测试消息内容

#### 扩展测试功能
- 添加新的测试类
- 集成其他插件的测试
- 创建自动化测试脚本

### 2. 持续集成

#### 自动化测试
可以将测试插件集成到 CI/CD 流程中：
- 自动构建和部署
- 定期运行测试套件
- 生成测试报告

### 3. 性能监控

#### 监控指标
- API 调用响应时间
- 内存使用情况
- 事件处理效率

---

## 📝 最佳实践

### 1. 测试环境

#### 推荐配置
- 至少 2-3 个在线玩家
- 配置多个频道
- 启用权限插件（如 LuckPerms）

#### 测试数据
- 准备测试用的消息内容
- 配置不同权限级别的玩家
- 设置多种游戏场景

### 2. 测试流程

#### 建议顺序
1. 基础 API 连接测试
2. 频道和用户管理测试
3. 消息发送和接收测试
4. 权限和过滤器测试
5. 高级功能测试

#### 记录结果
- 保存测试报告
- 记录发现的问题
- 跟踪修复进度

---

## 🤝 贡献和反馈

### 报告问题
如果发现测试插件的问题，请提供：
- 详细的错误信息
- 服务器环境信息
- 重现步骤

### 改进建议
欢迎提出改进建议：
- 新的测试用例
- 功能增强
- 性能优化

---

**祝你测试愉快！** 🎉

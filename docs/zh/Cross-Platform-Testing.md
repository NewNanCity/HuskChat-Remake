# HuskChat Remake 跨平台测试指南

本文档提供了完整的跨平台测试指南，确保玩家状态集成功能在所有支持的平台上正常工作。

## 测试环境要求

### 单服务器测试环境

**Bukkit/Paper 服务器**
- Minecraft 1.19+ 服务器
- HuskChat Remake 插件
- 测试用的示例插件

### 集群测试环境

**Velocity 代理服务器**
- Velocity 3.2.0+
- HuskChat Remake Velocity 版本
- 2-3个后端 Paper 服务器
- 每个后端服务器安装 HuskChat Remake

**BungeeCord 代理服务器**
- BungeeCord 1.19+
- HuskChat Remake BungeeCord 版本
- 2-3个后端 Spigot/Paper 服务器
- 每个后端服务器安装 HuskChat Remake

## 测试用例

### 1. 基础功能测试

#### 1.1 单服务器环境测试

**测试步骤：**
1. 启动 Bukkit/Paper 服务器
2. 安装 HuskChat Remake 和测试插件
3. 玩家加入服务器
4. 验证基础聊天功能

**预期结果：**
- 聊天消息正常发送和接收
- 频道切换功能正常
- 玩家状态正确初始化

#### 1.2 代理服务器环境测试

**测试步骤：**
1. 启动代理服务器（Velocity/BungeeCord）
2. 启动多个后端服务器
3. 玩家通过代理服务器连接
4. 验证跨服务器聊天功能

**预期结果：**
- 跨服务器聊天消息正常传递
- 玩家在不同服务器间切换时状态保持
- 代理服务器正确路由消息

### 2. 玩家状态集成测试

#### 2.1 生命值变化测试

**测试步骤：**
1. 玩家加入服务器
2. 使用命令或环境伤害降低玩家生命值
3. 观察生命值变化事件触发
4. 验证代理服务器接收到状态更新

**验证命令：**
```
/damage @p 10
/effect give @p minecraft:instant_damage 1 1
```

**预期结果：**
- `PlayerHealthChangeEvent` 正确触发
- 生命值数据在代理服务器正确更新
- 低血量状态正确识别和处理

#### 2.2 位置变化测试

**测试步骤：**
1. 玩家在服务器内移动
2. 玩家切换世界
3. 玩家在代理环境中切换服务器
4. 验证位置变化事件

**验证命令：**
```
/tp @p ~ ~10 ~
/execute in minecraft:the_nether run tp @p 0 64 0
```

**预期结果：**
- `PlayerLocationChangeEvent` 正确触发
- 跨世界移动正确检测
- 跨服务器移动在代理服务器正确处理

#### 2.3 游戏状态变化测试

**测试步骤：**
1. 切换游戏模式
2. 切换飞行状态
3. 切换潜行状态
4. 验证状态同步

**验证命令：**
```
/gamemode creative @p
/gamemode survival @p
```

**预期结果：**
- 游戏模式变化正确检测
- 飞行和潜行状态正确同步
- 代理服务器状态缓存正确更新

### 3. 命令执行API测试

#### 3.1 频道切换命令测试

**测试步骤：**
1. 使用API执行频道切换命令
2. 验证命令在不同平台的执行
3. 检查权限验证

**测试代码：**
```java
// 在测试插件中
HuskChatExtendedAPI api = HuskChatExtendedAPI.getInstance();
api.executeChatCommand(player, "/channel", "global")
    .thenAccept(success -> {
        if (success) {
            player.sendMessage("频道切换成功");
        } else {
            player.sendMessage("频道切换失败");
        }
    });
```

**预期结果：**
- 命令在所有平台正确执行
- 权限验证正常工作
- 执行结果正确返回

#### 3.2 私聊命令测试

**测试步骤：**
1. 使用API发送私聊消息
2. 验证跨服务器私聊
3. 检查消息传递

**测试代码：**
```java
api.executeChatCommand(player, "/msg", "target_player", "Hello!")
    .thenAccept(success -> {
        // 验证执行结果
    });
```

### 4. 跨服务器事件传播测试

#### 4.1 状态同步测试

**测试步骤：**
1. 玩家在服务器A修改状态
2. 玩家切换到服务器B
3. 验证状态是否正确同步

**验证方法：**
- 使用 `/playerstatus` 命令查看状态
- 检查代理服务器日志
- 验证插件消息传递

#### 4.2 实时状态更新测试

**测试步骤：**
1. 玩家在服务器A受到伤害
2. 在代理服务器查看玩家状态
3. 验证实时更新

**预期结果：**
- 状态变化立即传播到代理服务器
- 其他服务器的玩家能看到状态更新
- 插件消息正确传递

### 5. 性能和稳定性测试

#### 5.1 高负载测试

**测试步骤：**
1. 模拟多个玩家同时在线
2. 频繁触发状态变化事件
3. 监控服务器性能

**监控指标：**
- CPU使用率
- 内存使用量
- 网络带宽
- 事件处理延迟

#### 5.2 长时间运行测试

**测试步骤：**
1. 服务器连续运行24小时
2. 定期检查功能正常性
3. 监控内存泄漏

## 测试工具和脚本

### 自动化测试脚本

```java
public class HuskChatTestSuite {
    
    @Test
    public void testPlayerHealthChange() {
        // 测试生命值变化
        Player player = getTestPlayer();
        double initialHealth = player.getHealth();
        
        // 模拟伤害
        player.damage(5.0);
        
        // 验证事件触发
        verify(healthChangeListener, timeout(1000)).onHealthChange(any());
    }
    
    @Test
    public void testCrossServerStatusSync() {
        // 测试跨服务器状态同步
        OnlineUser player = getTestPlayer();
        
        // 在服务器A设置状态
        api.updatePlayerStatus(player, StatusType.COMBAT, true, "Test", -1);
        
        // 切换到服务器B
        switchPlayerServer(player, "serverB");
        
        // 验证状态同步
        assertTrue(player.isInCombat());
    }
}
```

### 监控脚本

```bash
#!/bin/bash
# 监控脚本

echo "开始HuskChat跨平台测试监控..."

# 检查服务器状态
check_server_status() {
    local server=$1
    local port=$2
    
    if nc -z localhost $port; then
        echo "✓ $server 服务器运行正常"
    else
        echo "✗ $server 服务器连接失败"
    fi
}

# 检查各服务器
check_server_status "Velocity代理" 25577
check_server_status "后端服务器1" 25565
check_server_status "后端服务器2" 25566

# 检查插件消息传递
echo "检查插件消息传递..."
# 这里可以添加具体的检查逻辑
```

## 故障排除

### 常见问题

#### 1. 插件消息未传递

**症状：**
- 代理服务器未收到后端服务器的状态更新
- 跨服务器事件不触发

**解决方案：**
1. 检查插件消息通道注册
2. 验证网络连接
3. 查看服务器日志

#### 2. 状态同步延迟

**症状：**
- 状态更新有明显延迟
- 玩家切换服务器后状态不正确

**解决方案：**
1. 检查网络延迟
2. 优化事件处理逻辑
3. 调整同步频率

#### 3. 内存使用过高

**症状：**
- 服务器内存持续增长
- 出现内存泄漏警告

**解决方案：**
1. 检查事件监听器清理
2. 优化缓存策略
3. 定期清理过期数据

### 调试技巧

#### 启用详细日志

```yaml
# config.yml
debug:
  enabled: true
  level: FINE
  log_player_status: true
  log_cross_server_events: true
```

#### 使用调试命令

```
/huskchat debug status <player>
/huskchat debug sync <player>
/huskchat debug network
```

## 测试报告模板

### 测试结果记录

```
测试日期: 2024-XX-XX
测试环境: Velocity + 3个Paper服务器
测试版本: HuskChat Remake v2.0.0

功能测试结果:
✓ 基础聊天功能
✓ 玩家状态集成
✓ 跨服务器事件传播
✓ 命令执行API
✗ 高负载性能 (需要优化)

性能指标:
- 平均延迟: 50ms
- 内存使用: 稳定
- CPU使用: 正常

问题记录:
1. 高负载下偶现状态同步延迟
2. 某些边缘情况下事件重复触发

建议:
1. 优化事件处理性能
2. 添加重复事件检测
3. 改进错误处理机制
```

通过这个全面的测试指南，可以确保HuskChat Remake的玩家状态集成功能在所有支持的平台上都能稳定可靠地工作。

package net.william278.huskchat.test;

import lombok.Getter;
import net.william278.huskchat.api.BukkitHuskChatExtendedAPI;
import net.william278.huskchat.test.commands.APITestCommand;
import net.william278.huskchat.test.commands.HCTestCommand;
import net.william278.huskchat.test.listeners.ChatEventTestListener;
import net.william278.huskchat.test.listeners.PlayerStatusTestListener;
import net.william278.huskchat.test.tests.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * HuskChat API测试插件主类
 * 用于全面测试HuskChat Remake的所有API功能
 * 
 * @author HuskChat Test Team
 * @version 1.0.0
 */
@Getter
public class HuskChatAPITestPlugin extends JavaPlugin {
    
    private BukkitHuskChatExtendedAPI huskChatAPI;
    
    // 测试管理器
    private TestManager testManager;
    
    // 各种测试类
    private EventSystemTest eventSystemTest;
    private ChannelManagementTest channelManagementTest;
    private MessageSendingTest messageSendingTest;
    private PlayerStatusTest playerStatusTest;
    private CommandExecutionTest commandExecutionTest;
    private FilterAndReplacerTest filterAndReplacerTest;
    private DiscordIntegrationTest discordIntegrationTest;
    private PermissionTest permissionTest;
    
    @Override
    public void onEnable() {
        // 初始化HuskChat API
        if (!initializeHuskChatAPI()) {
            getLogger().severe("无法初始化HuskChat API，插件将被禁用");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // 初始化测试管理器
        this.testManager = new TestManager(this);
        
        // 初始化各种测试
        initializeTests();
        
        // 注册事件监听器
        registerEventListeners();
        
        // 注册命令
        registerCommands();
        
        // 启动自动测试（如果配置启用）
        scheduleAutomaticTests();
        
        getLogger().info("===================================");
        getLogger().info("HuskChat API测试插件 v1.0.0 已加载");
        getLogger().info("作者：HuskChat Test Team");
        getLogger().info("支持的测试功能：");
        getLogger().info("- 事件系统测试");
        getLogger().info("- 频道管理测试");
        getLogger().info("- 消息发送测试");
        getLogger().info("- 玩家状态测试");
        getLogger().info("- 命令执行测试");
        getLogger().info("- 过滤器测试");
        getLogger().info("- Discord集成测试");
        getLogger().info("- 权限系统测试");
        getLogger().info("使用 /hctest help 查看测试命令");
        getLogger().info("使用 /hcapi help 查看API演示命令");
        getLogger().info("===================================");
    }
    
    @Override
    public void onDisable() {
        // 清理测试资源
        if (testManager != null) {
            testManager.cleanup();
        }
        
        // 注销事件监听器
        unregisterEventListeners();
        
        getLogger().info("HuskChat API测试插件已卸载");
    }
    
    /**
     * 初始化HuskChat API
     */
    private boolean initializeHuskChatAPI() {
        try {
            // 获取HuskChat扩展API实例
            this.huskChatAPI = BukkitHuskChatExtendedAPI.getInstance();
            
            if (huskChatAPI == null) {
                getLogger().severe("无法获取HuskChat扩展API实例");
                return false;
            }
            
            getLogger().info("成功初始化HuskChat扩展API");
            return true;
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "初始化HuskChat API时发生错误", e);
            return false;
        }
    }
    
    /**
     * 初始化各种测试
     */
    private void initializeTests() {
        this.eventSystemTest = new EventSystemTest(this);
        this.channelManagementTest = new ChannelManagementTest(this);
        this.messageSendingTest = new MessageSendingTest(this);
        this.playerStatusTest = new PlayerStatusTest(this);
        this.commandExecutionTest = new CommandExecutionTest(this);
        this.filterAndReplacerTest = new FilterAndReplacerTest(this);
        this.discordIntegrationTest = new DiscordIntegrationTest(this);
        this.permissionTest = new PermissionTest(this);
        
        getLogger().info("所有测试模块已初始化");
    }
    
    /**
     * 注册事件监听器
     */
    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new ChatEventTestListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerStatusTestListener(this), this);
        
        getLogger().info("事件监听器已注册");
    }
    
    /**
     * 注销事件监听器
     */
    private void unregisterEventListeners() {
        // Bukkit会自动注销插件的所有监听器
    }
    
    /**
     * 注册命令
     */
    private void registerCommands() {
        // 注册测试命令
        HCTestCommand testCommand = new HCTestCommand(this);
        getCommand("hctest").setExecutor(testCommand);
        getCommand("hctest").setTabCompleter(testCommand);
        
        // 注册API演示命令
        APITestCommand apiCommand = new APITestCommand(this);
        getCommand("hcapi").setExecutor(apiCommand);
        getCommand("hcapi").setTabCompleter(apiCommand);
        
        getLogger().info("命令已注册");
    }
    
    /**
     * 安排自动测试
     */
    private void scheduleAutomaticTests() {
        // 延迟5秒后开始自动测试，确保所有插件都已加载
        getServer().getScheduler().runTaskLater(this, () -> {
            if (testManager != null) {
                testManager.runBasicTests();
            }
        }, 100L); // 5秒 = 100 ticks
    }
    
    /**
     * 获取HuskChat扩展API实例
     */
    @NotNull
    public BukkitHuskChatExtendedAPI getHuskChatAPI() {
        if (huskChatAPI == null) {
            throw new IllegalStateException("HuskChat API未初始化");
        }
        return huskChatAPI;
    }
    
    /**
     * 记录测试结果
     */
    public void logTestResult(@NotNull String testName, boolean success, @NotNull String details) {
        String status = success ? "✅ 成功" : "❌ 失败";
        getLogger().info(String.format("[测试] %s - %s: %s", testName, status, details));
    }
    
    /**
     * 记录测试错误
     */
    public void logTestError(@NotNull String testName, @NotNull Throwable error) {
        getLogger().log(Level.WARNING, String.format("[测试] %s - 发生错误", testName), error);
    }
}

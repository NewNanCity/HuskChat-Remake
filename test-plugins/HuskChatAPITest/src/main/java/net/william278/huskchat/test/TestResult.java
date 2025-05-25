package net.william278.huskchat.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 测试结果数据类
 * 用于存储单个测试的执行结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResult {
    
    private String testName;
    private boolean success;
    private String message;
    private String details;
    private LocalDateTime timestamp;
    private long executionTimeMs;
    
    /**
     * 创建成功的测试结果
     */
    @NotNull
    public static TestResult success(@NotNull String testName, @NotNull String message) {
        return success(testName, message, null);
    }
    
    /**
     * 创建成功的测试结果（带详细信息）
     */
    @NotNull
    public static TestResult success(@NotNull String testName, @NotNull String message, @Nullable String details) {
        return new TestResult(testName, true, message, details, LocalDateTime.now(), 0);
    }
    
    /**
     * 创建失败的测试结果
     */
    @NotNull
    public static TestResult failure(@NotNull String testName, @NotNull String message) {
        return failure(testName, message, null);
    }
    
    /**
     * 创建失败的测试结果（带详细信息）
     */
    @NotNull
    public static TestResult failure(@NotNull String testName, @NotNull String message, @Nullable String details) {
        return new TestResult(testName, false, message, details, LocalDateTime.now(), 0);
    }
    
    /**
     * 设置执行时间
     */
    @NotNull
    public TestResult withExecutionTime(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
        return this;
    }
    
    /**
     * 获取格式化的时间戳
     */
    @NotNull
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    /**
     * 获取状态图标
     */
    @NotNull
    public String getStatusIcon() {
        return success ? "✅" : "❌";
    }
    
    /**
     * 获取状态文本
     */
    @NotNull
    public String getStatusText() {
        return success ? "成功" : "失败";
    }
    
    /**
     * 获取完整的结果描述
     */
    @NotNull
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] %s %s - %s", 
            getFormattedTimestamp(), getStatusIcon(), testName, message));
        
        if (details != null && !details.trim().isEmpty()) {
            sb.append("\n详细信息: ").append(details);
        }
        
        if (executionTimeMs > 0) {
            sb.append(String.format("\n执行时间: %d ms", executionTimeMs));
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("TestResult{name='%s', success=%s, message='%s', time=%s}", 
            testName, success, message, getFormattedTimestamp());
    }
}

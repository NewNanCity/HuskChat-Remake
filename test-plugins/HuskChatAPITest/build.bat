@echo off
echo ===================================
echo HuskChat API 测试插件构建脚本
echo ===================================

echo.
echo 正在清理项目...
call mvn clean -q
if %errorlevel% neq 0 (
    echo 清理失败！
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
call mvn compile -q
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo.
echo 正在打包项目...
call mvn package -q
if %errorlevel% neq 0 (
    echo 打包失败！
    pause
    exit /b 1
)

echo.
echo ===================================
echo 构建完成！
echo ===================================
echo 生成的插件文件: target\HuskChatAPITest-1.0.0.jar
echo.
echo 安装说明:
echo 1. 确保服务器已安装 HuskChat Remake
echo 2. 将 HuskChatAPITest-1.0.0.jar 放入 plugins 目录
echo 3. 重启服务器
echo 4. 使用 /hctest help 查看测试命令
echo 5. 使用 /hcapi help 查看API演示命令
echo ===================================
pause

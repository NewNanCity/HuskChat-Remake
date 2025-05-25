@echo off
echo ===================================
echo HuskChat Remake Maven 构建脚本
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
echo 正在运行测试...
call mvn test -q
if %errorlevel% neq 0 (
    echo 测试失败！
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
echo.
echo 生成的插件文件：
echo - Bukkit版本: bukkit\target\HuskChat-Bukkit-3.0.4.jar
echo - Velocity版本: velocity\target\HuskChat-Velocity-3.0.4.jar
echo - BungeeCord版本: bungee\target\HuskChat-BungeeCord-3.0.4.jar
echo - Paper版本: paper\target\HuskChat-Paper-3.0.4.jar
echo - 完整版本: plugin\target\HuskChat-3.0.4.jar
echo.
echo 请根据您的服务器类型选择对应的jar文件
echo ===================================
pause

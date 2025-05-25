#!/bin/bash

echo "==================================="
echo "HuskChat Remake Maven 构建脚本"
echo "==================================="

echo
echo "正在清理项目..."
mvn clean -q
if [ $? -ne 0 ]; then
    echo "清理失败！"
    exit 1
fi

echo
echo "正在编译项目..."
mvn compile -q
if [ $? -ne 0 ]; then
    echo "编译失败！"
    exit 1
fi

echo
echo "正在运行测试..."
mvn test -q
if [ $? -ne 0 ]; then
    echo "测试失败！"
    exit 1
fi

echo
echo "正在打包项目..."
mvn package -q
if [ $? -ne 0 ]; then
    echo "打包失败！"
    exit 1
fi

echo
echo "==================================="
echo "构建完成！"
echo "==================================="
echo
echo "生成的插件文件："
echo "- Bukkit版本: bukkit/target/HuskChat-Bukkit-3.0.4.jar"
echo "- Velocity版本: velocity/target/HuskChat-Velocity-3.0.4.jar"
echo "- BungeeCord版本: bungee/target/HuskChat-BungeeCord-3.0.4.jar"
echo "- Paper版本: paper/target/HuskChat-Paper-3.0.4.jar"
echo "- 完整版本: plugin/target/HuskChat-3.0.4.jar"
echo
echo "请根据您的服务器类型选择对应的jar文件"
echo "==================================="

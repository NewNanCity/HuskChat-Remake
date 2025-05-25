#!/bin/bash

echo "==================================="
echo "HuskChat 简化构建脚本"
echo "==================================="

echo
echo "注意：由于依赖问题，我们将使用原始的Gradle构建"
echo

echo "正在使用Gradle构建..."
./gradlew clean build -x test
if [ $? -ne 0 ]; then
    echo "Gradle构建失败！"
    echo
    echo "尝试Maven构建（仅Bukkit模块）..."
    cd bukkit
    mvn clean package -q
    if [ $? -ne 0 ]; then
        echo "Maven构建也失败！"
        cd ..
        exit 1
    fi
    cd ..
    echo "Maven构建成功！"
    echo "生成的文件: bukkit/target/HuskChat-Bukkit-3.0.4.jar"
else
    echo "Gradle构建成功！"
    echo "生成的文件在 target/ 目录中"
fi

echo
echo "==================================="
echo "构建完成！"
echo "==================================="

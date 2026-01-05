#!/bin/bash

# Spring Boot 博客系统启动脚本
# 用于在 WSL 环境中启动应用

echo "=========================================="
echo "   Spring Boot 博客系统启动脚本"
echo "=========================================="
echo ""

# 检查 Java 是否安装
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到 Java，请先安装 JDK 17"
    exit 1
fi

# 检查 Java 版本
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ 错误: Java 版本过低，需要 JDK 17 或更高版本"
    exit 1
fi

echo "✅ Java 版本检查通过: $(java -version 2>&1 | head -n 1)"

# 检查 Maven 是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到 Maven，请先安装 Maven"
    exit 1
fi

echo "✅ Maven 版本: $(mvn -version | head -n 1)"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "📁 工作目录: $SCRIPT_DIR"
echo ""

# 启动应用
echo "🚀 正在启动 Spring Boot 应用..."
echo ""

mvn spring-boot:run


---
title: 自动导入机制详解
author: admin
tags: system, architecture, java
---

# 为什么需要自动导入？

手动录入文章是上个世纪的做法。现代博客系统应该能够识别文件系统中的变化，并自动同步到数据库。

## Front Matter

我们使用 YAML 风格的 Front Matter 来定义元数据：

- **title**: 文章标题
- **author**: 作者（必须是系统里存在的用户）
- **tags**: 标签列表

这样，内容创作者只需要关注 Markdown 文件本身。

## 代码示例

```java
public void import() {
    System.out.println("Importing posts...");
}
```


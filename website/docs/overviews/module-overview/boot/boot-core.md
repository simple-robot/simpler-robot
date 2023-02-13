---
title: boot 核心模块
sidebar_location: 10
---

与 [基础核心模块](../core) 类似，boot核心模块是针对于 [boot](index.md) 模块的基础完整实现模块。



### 启动类

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';


<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin title="MyApp.kt"
suspend fun main(vararg args: String) {
    val context = Simboot.run(MyApp::class, *args)
    context.join()
}
```

</TabItem>
<TabItem value="Java">

```java title="MyApp.java"
public static void main(String[] args) {
    SimbootContext context = Simboot.run(MyApp.class, args)
    context.joinBlocking();
}
```

</TabItem>
</Tabs>




:::danger
TODO
:::

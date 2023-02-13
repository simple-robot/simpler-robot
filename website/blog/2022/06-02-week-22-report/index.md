---
authors: forliy
title: 2022年第22周周报
tags: [2022周报,周报]
---


2022年第22周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## 🐲 端午节
临近端午节，祝愿各位朋友享受假期，多吃粽子，放松身心，不要加班～


## 🚀 版本更新
在本周，[核心库](https://github.com/simple-robot/simpler-robot) 发布了几个版本：
- [**v3.0.0.preview.12.0**](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.12.0)
- [v3.0.0.preview.11.1](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.11.1)
- [v3.0.0.preview.11.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.11.0)（上周末发布）

在这其中，[**v3.0.0.preview.12.0**](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.12.0)
是本周内发布的主要重构版本。在这个版本中，我们主要实现了 `Items` 类型来尝试改善原本过于繁杂的 `Flow`、`Sequance`、`Stream`混搭的情况。
由于是用来改善类型体验的，因此会变更各相关类型的返回值（大概涉及类型有 `Bot` 、`Member`、`Guild`、`Channel`、`Group`），在升级的时候可能需要对代码进行一定的调整。

## 🎸 Items API
如上文所属，在 [**v3.0.0.preview.12.0**](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.12.0)
版本中我们提供了一个新的 `Items` 类型来替代原本不够统一的流式类型。我们希望它能够有效的改善普通开发者对限流操作的使用手感，
也希望它能够有效的改善组件开发者的体验。

对于使用一个 `Items` API可以参考如下示例：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
data class Foo(val name: String)

// 
suspend fun foo(items: Items<Foo>) {
    // 最基础的操作：收集元素.
    items.collect { println(it) } // suspend
    // 或者试试 toList()
}

// 
suspend fun bar(items: Items<Foo>) {
    // 三个预处理函数: limit, offset, batch.
    // 有关预处理函数的描述请参见源码注释或文档
    items.limit(10).offset(10).batch(100)
        .asFlow() // 或者试试 asSequence()
        // do ..?
        .collect { println(it) }
}
```

</TabItem>
<TabItem value='Java'>

```java
class Bar {
    String name;
}

public class Test {

    public void foo(Items<Bar> items) {
        // 收集元素
        // 参数类型问题会在后续版本优化
        items.collect((Consumer<? super Bar>) bar -> {
            System.out.println(bar);
        });

        // 或者试试 items.collectToList()
    }

    public void bar(Items<Bar> items) {
        items.limit(10).offset(10).batch(100)
                .asStream()
                // do ..?
                .forEach(bar -> System.out.println(bar));
    }
}
```

</TabItem>
</Tabs>

:::caution

在 `v3.0.0.preview.13.0` 中会对 `Items` API进行调整更新。

:::

:::tip 反馈!

老实说，`v3.0.0.preview.12.0` 版本发布的的确很是仓促，因此 `Items` API 实际上没有进行很细致的打磨，就鬼使神差的被发布了。

因此 `Items` API 发布后，它很有可能存在各种各样的隐患，如果可以还请仔细体验，并积极反馈问题到 [**issue**](https://github.com/simple-robot/simpler-robot/issues)，
感谢你的协助！

:::

## 📖 文档更新
最近文档更新的工作有在缓慢进行。去看看 [《标准定义》](/docs/definition/) 下的内容吧～ 也许会发现些什么变化呢。
当然了，文档内容仍然十分匮乏。但是这也不是能够瞬间完成的事情，在写之前，我们也没想到要写的东西有那么多... 总之，
工作正在进行就是一个不错的消息，不是吗？

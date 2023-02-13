---
sidebar_position: 20
title: 时间戳
---

**`Timestamp`**，顾名思义，这是一个**时间戳**类型。

在组件中，"时间戳"的非常常见的一个概念。比如 `Event.timestamp` 即代表获取此事件发生的时间。

通常情况下，时间戳都是一个**长整型**数值，一般代表为 **毫秒时间戳** 或 **秒时间戳**。
（当然了，也有小概率出现那种用分钟时间戳的怪胎，尽管目前我从未见过）
但是对于不同的组件实现进行协作时，不同组件中时间戳单位不一致的问题很可能会是一种隐患。

因此simbot提供了 `Timestamp` 作为面向使用者的统一时间戳类型来取代不同时间单位的时间戳。

## 定义

如下为 `Timestamp` 的基本定义（简化版）：

```kotlin
public sealed class Timestamp : Comparable<Timestamp> {

    /**
     * 秒值。
     */
    public abstract val second: Long

    /**
     * 毫秒值。
     */
    public abstract val millisecond: Long

    /**
     * 此时间戳是否是一个被支持的真实时间戳。
     * 如果得到false，则代表此时间戳本质上不存在，且上述秒值和毫秒值最终结果应为-1。
     */
    public abstract fun isSupport(): Boolean


    public object NotSupport : Timestamp() {
       // 细节省略...
    }
}
```


import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';


## 使用

`Timestamp` 实际上的应用也很简单，主要就是用于获取两种时间戳类型。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val timestamp: Timestamp = ...

val second = timestamp.second            // 秒时间戳
val milliSecond = timestamp.millisecond  // 毫秒时间戳
val isSupport = timestamp.isSupport()    // 是否为有效时间
// isSupport 的结果类似于 second >= 0
```

在 Kotlin 中，提供了一些针对 `Timestamp` 的扩展函数：

```kotlin
val instant: Instant = timestamp.instantValue // 转化为 java.time.Instant 对象
```

</TabItem>
<TabItem value="Java">

```java
Timestamp timestamp = ...

final long second = timestamp.getSecond();           // 秒时间戳
final long millisecond = timestamp.getMillisecond(); // 毫秒时间戳
final boolean support = timestamp.isSupport();       // 是否为有效时间
// isSupport 的结果类似于 second >= 0
```

</TabItem>
</Tabs>

## 获取
其实对于普通开发者来讲，主动构建 `Timestamp` 的情况并不多见。`Timestamp` 提供了一些工厂函数来构建实例：


<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
Timestamp.now()                             // 获取当前时间对应的时间戳
Timestamp.byMillisecond(1234567898765L)     // 根据毫秒时间戳构建
Timestamp.bySecond(123456789)               // 根据秒时间戳构建
Timestamp.bySecond(123456789, 123)          // 根据秒时间戳构建
Timestamp.byInstant(Instant.now())          // 根据 java.time.Instant 构建
Instant.now().toTimestamp()                 // 根据 java.time.Instant 构建
Timestamp.notSupport()                      // 得到一个代表"无效"的时间戳
```

</TabItem>
<TabItem value="Java">

```java
Timestamp.now();                            // 获取当前时间对应的时间戳
Timestamp.byMillisecond(1145141919810L);    // 根据毫秒时间戳构建
Timestamp.bySecond(1145141919);             // 根据秒时间戳构建
Timestamp.bySecond(1145141919, 810);        // 根据秒时间戳构建
Timestamp.byInstant(Instant.now());         // 根据 java.time.Instant 构建
Timestamp.notSupport();                     // 得到一个代表"无效"的时间戳
```

</TabItem>
</Tabs>




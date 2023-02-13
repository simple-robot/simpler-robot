---
title: 事件处理上下文
tags: [标准事件]
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

事件处理上下文， 即 **`EventProcessingContext`** ，是整个事件调度流程中的 **核心对象**。


:::note 起源...

一次事件调度中的事件对象（ **`Event`** ）便是来自此上下文。

:::

事件处理上下文创建于事件推送之初、消亡 <small>(指不会再提供给其他流程)</small> 于事件调度结束，是一次完整事件调度流程的代表。

在核心模块下，一个简单的事件监听如下所示：

```kotlin
createSimpleApplication {
    listeners {
        listen(FriendEvent) {
            // (1)
            // highlight-start
            handle { event -> // this: EventListenerProcessingContext
                eventResult()
            }
            // highlight-end
        }
    }
}
```

在上述代码示例中的 **`(1)`** 处，`handle { ... }` 函数中的接收器类型即为 **`EventListenerProcessingContext`** 
(也就是 **`EventProcessingContext`** 在监听函数中的扩展) 。



## EventProcessingContext

**`EventProcessingContext`** 的简化版基本定义大概如下：
```kotlin
public interface EventProcessingContext /* ... */ {
    /**
     * 本次监听流程中的事件主体。
     */
    public val event: Event
    
    /**
     * 已经执行过的所有监听函数的结果。
     *
     * 此列表仅由事件处理器内部操作，是一个对外不可变视图。
     */
    public val results: List<EventResult>
    
    /**
     * 当前事件所处环境中所能够提供的消息序列化模块信息。
     */
    public val messagesSerializersModule: SerializersModule
    
    /**
     * 根据一个 [Attribute] 得到一个属性。
     */
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T?
}
```

可以看得出来，在 `EventProcessingContext` 中，其所提供的主要几个属性为 `event`、`result`、`messagesSerializersModule`。

### event

属性 `event` 是最常用、最基本的对象，它便是本次事件调度流程中的**事件**本体。

### result

`result` 代表每一个监听函数处理结果的集合视图，会随着事件处理流程的推进而逐渐增加。

### messagesSerializersModule

`messagesSerializersModule` 代表为当前事件调度所处application环境中的所有可能的
[**消息对象**](../../message-overview) 的序列化模组。 

<hr />

除了这几个主要属性以外，`EventProcessingContext` 还提供了用于获取/设置瞬时属性的函数：`get(...)` 和 `put(...)` ( `set(...)` )。

这两个属性继承自 `InstantScopeContext`，可以用于在流程中传递属性。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
class Foo   // 保存元素类型

val attribute = attribute<Foo>("foo") // 定义属性键

/** 设置属性示例 */
fun setAttr(context: EventProcessingContext) {
    context[attribute] = Foo()
}

/** 获取属性示例 */
fun useAttr(context: EventProcessingContext) {
    val foo = context[attribute]
    // ...
}
```

</TabItem>
<TabItem value="Java">

```java
public class Example {
    private record Foo(){}  // 保存元素类型
    private static final Attribute<Foo> attribute = Attribute.of("foo"); // 定义属性键
    
    /** 设置属性示例 */
    public void getAttr(EventProcessingContext context) {
        context.put(attribute, new Foo());
    }
    
    /** 获取属性示例 */
    public void useAttr(EventProcessingContext context) {
        final var foo = context.get(attribute);
        // ...
    }
}
```

</TabItem>
</Tabs>



## EventListenerProcessingContext
上文中，除了 `EventProcessingContext`, 我们还提到了一个类型：`EventListenerProcessingContext`。它代表为 _"监听函数处理上下文"_，
是派生自 `EventProcessingContext` 的、以监听函数为单位的上下文对象。

前文提到过：_事件处理上下文创建于事件推送之初、消亡于事件调度结束，是一次完整事件调度流程的代表。_ ，
那么监听函数处理上下文则是创建于监听函数出发之前，消亡于监听函数执行结束，是一次完整监听函数触发流程的代表。

一个 `EventListenerProcessingContext` 的简化版定义如下：

```kotlin
public interface EventListenerProcessingContext : EventProcessingContext {
    /**
     * 当前（将要）被执行的监听函数。
     */
    public val listener: EventListener
    
    /**
     * 当前监听函数的主要文本内容，一般可用于在拦截器、过滤器、监听函数相互组合时进行一些过滤内容匹配。
     *
     * 正常情况下，[textContent] 在 [event] 为 [MessageEvent] 类型的时候，默认为 [MessageContent.plainText],
     * 其他情况下默认为null。
     *
     */
    public var textContent: String?
    
}
```

不难看出，`EventListenerProcessingContext` 相比较于 `EventProcessingContext` 而言，
额外提供了一些属性。

### 





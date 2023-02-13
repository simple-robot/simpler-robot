---
sidebar_position: 30
title: 持续会话
toc_max_heading_level: 4
---

当你需要在一个监听函数中，持续的处理连续或多个事件的时候，或许**持续会话**可以为你提供一些微不足道的帮助。

本章节将会试着向你介绍如何使用 **持续会话上下文(`ContinuousSessionContext`)** 来在一个监听函数中等待并处理其他事件。

持续会话由 **核心模块**( `simbot-core` ) 中的 **作用域**( `SimpleScope` ) 提供，不属于标准API的一部分。

因此通常情况下，持续会话仅支持 **核心模块** 及其衍生模块（包括 **Boot核心模块** ( `simboot-core` )
和 **Spring Boot启动器** ( `simbot-spring-boot-starter` )）。

:::caution 实验性

**持续会话**相关api尚处于**实验阶段**，可能会存在各种问题并且可能会随时变更API。

:::

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

:::note 前情提要

下文介绍中出现的代码示例如非特殊说明则将会有所简化。

<Tabs groupId="code">
<TabItem value='Kotlin'>

在 **Kotlin** 中，将会以相同风格的代码来 **核心模块** 和 **Boot模块** 下的监听函数。

例如下述代码：

```kotlin
suspend fun EventProcessingContext.onEvent(event: Event) {
      // Here ...
}
```

将可以代表为下列情况：

<Tabs groupId="Kotlin-Module">
<TabItem label="核心模块" value='Core'>

```kotlin
suspend fun main() {
    createSimpleApplication {
        listeners {
            listen(Event) {
               // highlight-start
                process { event -> // this: EventListenerProcessingContext
                    // Here ...
                }
                // highlight-end
            }
        }
    }.join()
}
```

_或其他类似的事件监听形式_


</TabItem>
<TabItem label="Boot模块"  value='Boot'>

```kotlin
@Listener
suspend fun EventProcessingContext.onEvent(event: Event) {
      // Here ...
}
```

</TabItem>
</Tabs>



</TabItem>
<TabItem value='Java'>

在 **Java** 中，通常使用的为 **Boot模块** 或 **Spring Boot启动器**。
因此示例代码会以Boot模块下的风格进行展示，例如：

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    // Here ...
}
```

</TabItem>
</Tabs>

:::

## 获取

### 通过 `SimpleScope` 获取

使用之前，最重要的事情就是需要获取它。开篇我们提到，`ContinuousSessionContext` 是由核心模块中的 `SimpleScope` 所提供的，
因此获取持续会话最基本的方式便是通过 **事件处理上下文**( `EventProcessingContext` 或 `EventListenerProcessingContext` )
和 `SimpleScope` 来获取它。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun EventProcessingContext.onEvent(event: Event) {
    val sessionContext: ContinuousSessionContext? = this[SimpleScope.ContinuousSession]
    // ...
}
```

:::tip null?

上述代码中可以看到，通过 `context[...]` 得到的结果是**可能为空**的。当你使用的是第三方提供的实现或者非核心模块或其衍生模块的话，
你可能无法得到所需的结果。

文章的后续我们将会默认将当前环境视为处于**核心模块或其衍生模块**中，并**假定**获取的结果不会为null。
但在正常使用的时候，还是应当多加留意。

:::

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(EventProcessingContext context, FriendEvent event) {
    final ContinuousSessionContext sessionContext = context.get(SimpleScope.ContinuousSession);
}
```

:::tip null?

上述代码中，通过 `context.get(...)` 得到的结果是**可能为空**的。当你使用的是第三方提供的实现或者非核心模块或其衍生模块的话，
你可能无法得到所需的结果。

文章的后续我们将会默认将当前环境视为处于**核心模块或其衍生模块**中，并**假定**获取的结果不会为null。
但在正常使用的时候，还是应当多加留意。

:::

</TabItem>
</Tabs>

### 通过扩展属性获取

核心模块通过 `SimpleScope` 提供了一系列用于简化获取其内属性的**扩展属性**，其中也包括针对于从 `EventProcessingContext`
或 `EventListenerProcessingContext`
中获取 `ContinuousSessionContext` 的属性。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

**`continuousSession`**

获取 `EventProcessingContext` 中的 `ContinuousSessionContext`。当无法获取、不存在或不支持时将会**抛出异常**。

```kotlin
suspend fun EventProcessingContext.onEvent(event: Event) {
    val sessionContext: ContinuousSessionContext = this.continuousSession
}
```

**`continuousSessionOrNull`**

获取 `EventProcessingContext` 中的 `ContinuousSessionContext`。当无法获取、不存在或不支持时将会得到null。

```kotlin
suspend fun EventProcessingContext.onEvent(event: Event) {
    val sessionContext: ContinuousSessionContext? = this.continuousSessionOrNull
}
```

</TabItem>
<TabItem value='Java'>

**`SimpleScope.getContinuousSession`**

获取 `EventProcessingContext` 中的 `ContinuousSessionContext`。当无法获取、不存在或不支持时将会**抛出异常**。

```java
@Listener
public void onEvent(EventProcessingContext context, Event event) {
    final ContinuousSessionContext continuousSession = SimpleScope.getContinuousSession(context);
}
```

**`SimpleScope.getContinuousSessionOrNull`**

获取 `EventProcessingContext` 中的 `ContinuousSessionContext`。当无法获取、不存在或不支持时将会得到null。

```java
@Listener
public void onEvent(EventProcessingContext context, Event event) {
    final ContinuousSessionContext continuousSession = SimpleScope.getContinuousSessionOrNull(context);
}
```

</TabItem>
</Tabs>

### 通过参数注入获取

除了手动获取，你也可以直接将 `ContinuousSessionContext` 作为监听函数参数来自动注入。

:::info 有效范围

参数注入仅在**Boot相关模块**下有效。

:::

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
@Listener
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    // Here ...
}
```

:::note 示例前提

后续如果没有特殊说明，将会以 **通过参数注入获取** 的方式来作为其他示例的基础前提。

但是代码示例中将不会体现 `@Listener` 注解。 

:::

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    // Here ...
}
```

:::info

后续如果没有特殊说明，将会以 **通过参数注入获取** 的方式来作为其他示例的基础前提。

:::

</TabItem>
</Tabs>



## 基本使用

了解了如何获取 `ContinuousSessionContext`，接下来便是如果去**使用**。

### `waiting`

`ContinuousSessionContext` 中的API分为几种类型，其中 `waiting` 是最基本的一种API。
此API代表：等待并获取下一个**结果**。

#### 等待并选择

你可以将 `waiting` 的**回调函数**视为一种内置的、小型的监听函数。
当你使用 `watiing` 的时候，它会监听后续所有推送而来的其他事件，直到你选择出你所需要的内容。

:::note 无条件的

这种等待**不自动区分**任何诸如 `Bot`、组件等属性。

:::

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, Event event) {
    val value: Int = sessionContext.waiting { provider -> // this: EventProcessingContext
        provider.push(1)
    }
}
```

在 `waiting` 的参数函数体中，存在两个参数：`this: EventProcessingContext` 和 `provider: ContinuousSessionProvider<T>`。
其中，`this` 即为触发此回调函数时的事件处理上下文。

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, Event fooEvent) {
    val event: Event = sessionContext.waiting { provider -> // this: EventProcessingContext
        // 当前事件
        val currentEvent: Event = this.event
        provider.push(currentEvent)
    }
}
```

上述示例中，`waiting` 在回调函数中得到了下一个事件处理上下文中的 **事件对象**，并通过 `provider` 推送给了等待处。
也由此可见，`provider` 的作用为向调用 `waiting` 的等待处推送一个 **结果**。

其中，`provider` 推送的类型应当与外部的接收类型一致。

你可以有条件的/选择性的推送：

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, Event event) {
    val value: String = sessionContext.waiting { provider -> // this: EventProcessingContext
        // 当前事件
        val currentEvent: Event = this.event
        if (currentEvent.component.id == "foo") {
            // 如果此事件的所属组件id为'foo', 推送字符串 "Yes"
            provider.push("Yes")
        }
    }
}
```

或者推送一个异常：

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, Event event) {
    val event: Event = sessionContext.waiting { provider -> // this: EventProcessingContext
        // 当前事件
        val currentEvent: Event = this.event
        if (currentEvent.component.id == "foo") {
            // 如果此事件的所属组件id为'foo', 推送异常 IllegalStateException("No")
            provider.pushException(IllegalStateException("No"))
        }
    }
}
```

</TabItem>
<TabItem value='Java'>

:::caution 阻塞

对**Java**来讲，`ContinuousSessionContext` 中的所有API都是 **阻塞** 的。因此在Java中使用时需要更多的考虑一下性能或一个监听函数的长时间阻塞问题。

在有必要时合理的为监听函数提供异步能力，防止影响到其他监听函数。

:::

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    final Integer next = sessionContext.waiting((c, provider) -> {
         provider.push(1);
    });
}
```

在 `waiting` 的参数函数体中，存在两个参数：`EventProcessingContext c` 和 `ContinuousSessionProvider<T> provider`。
其中，`c` 即为触发此回调函数时的事件处理上下文。

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    final Event received = sessionContext.waiting((c, provider) -> {
        final Event currentEvent = c.getEvent();
        provider.push(currentEvent);
    });
}
```

上述示例中，`waiting` 在回调函数中得到了下一个事件处理上下文中的 **事件对象**，并通过 `provider` 推送给了等待处。
也由此可见，`provider` 的作用为向调用 `waiting` 的等待处推送一个 **结果**。

其中，`provider` 推送的类型应当与外部的接收类型一致。

你可以有条件的/选择性的推送：

```java
@Listener
public void onEvent(sessionContext: ContinuousSessionContext, Event event) {
    final String received = sessionContext.waiting((c, provider) -> {
        final Event currentEvent = c.getEvent();
        if ("foo".equals(currentEvent.getComponent().getId())) {
            provider.push("Yes");
        }
    });
}
```

或者推送一个异常：

```java
@Listener
public void onEvent(sessionContext: ContinuousSessionContext, Event event) {
    final String received = sessionContext.waiting((c, provider) -> {
        final Event currentEvent = c.getEvent();
        if ("foo".equals(currentEvent.getComponent().getId())) {
            provider.pushException(new IllegalStateException("No"));
        }
    });
}
```

</TabItem>
</Tabs>


上述代码中，`sessionContext.waiting` 会一直挂起/阻塞，并直到参数中的函数体中使用 `provider` 推送了一个结果。
而函数体会在每一次出现其他事件推送时被触发。

:::note Provider?

有关 `provider` 的内容会在后续讲到。

:::

:::caution 注意!

需要注意，当一个 `ContinuousSessionContext` **已经取用**一个事件时，
这个事件将**不会**参与到正常的事件调度流程中。也因此，通过 `ContinuousSessionContext` 的任何API
**取用** 的事件，将无法触发任何其他的监听函数、拦截器或过滤器等正常监听流程中的内容。

<hr />

同样需要注意的是，上述这种 **取用** 行为，是建立在等待函数体内的 `provider.push` 不是异步发生的前提下。
除了一些你认为必要的场景，你**不应该**在 `ContinuousSessionContext` 的回调函数中通过异步执行 `provider.push`
来推送结果 ———— 这可能会导致事件的调度判定出现混乱。

:::

### `waitingForNext`

`waitingForNext` 是 [waiting](#waiting) 的衍生API。此API代表：等待并获取下一个符合条件的**事件对象**。

#### 等待任何事件

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun EventProcessingContext.onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val event: Event = sessionContext.waitingForNext()
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    final Event next = sessionContext.waitingForNext();
}
```

</TabItem>
</Tabs>

上述代码中，`sessionContext.waitingForNext()` 代表为等待下一个函数的到来，并得到它。

:::note 无条件的

与 [**`waiting`**](#waiting) 类似的，这种等待**不自动区分**任何诸如 `Bot`、组件等属性。

:::

:::tip 衍生

前文说过，`waitingForNext` 是 `waiting` 的衍生API。实际上，上述示例相似于：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val next: Event = sessionContext.waiting { provider -> // this: EventProcessingContext
        provider.push(this.event)
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    final Event received = sessionContext.waiting((c, provider) -> {
        provider.push(c.getEvent());
    });
}
```

</TabItem>
</Tabs>

:::

#### 明确类型的等待事件

通常情况下，你至少也需要一个明确的监听类型作为你的下一个目标。


<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
import love.forte.simbot.event.waitingForNext

suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val event: BarEvent = sessionContext.waitingForNext(BarEvent)
}
```

或者显式的指定事件类型的参数名：

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val event: BarEvent = sessionContext.waitingForNext(key = BarEvent)
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    final BarEvent next = sessionContext.waitingForNext(BarEvent.Key);
}
```

</TabItem>
</Tabs>

你可以通过提供一个事件的 **事件类型** ( `Event.Key` ) 来指定一个具体的事件类型。

:::info 明确的类型!

需要注意，你应当自始至终都使用一个 **明确的** 事件类型，例如：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    // success-start
    val event: BarEvent = sessionContext.waitingForNext(key = BarEvent)
    // success-end
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    // success-start
    final BarEvent next = sessionContext.waitingForNext(BarEvent.Key);
    // success-end
}
```

</TabItem>
</Tabs>

而不是：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    // error-start
    val event: Event = sessionContext.waitingForNext(key = event.key)
    // error-end
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event event) {
    // error-start
    final Event next = sessionContext.waitingForNext(event.getKey());
    // error-end
}
```

</TabItem>
</Tabs>

**为什么?**

一个从监听函数中得到的事件对象可能仅仅只是当前事件类型的**某个子类型**，因此通过 `getKey`
得到的事件类型标识也只是一个类型的**子集**，
并且你无法掌控这些可能存在任何不对外公开内容的类型标识。

例如一个事件 `FooEvent`, 假设它存在两个不对外公开的实现：`AImpl` 和 `BImpl`。

当一次事件触发时，你所得到的 `FooEvent` 只可能是上述两个类型的**其中一个**，而当你使用 `getKey` 时，你是无法明确得知是这两个类型中的具体哪一个的。

因此，`Event.Key` 应当是**绝对明确**的，才能保证你所得到的内容是你预期的结果。

:::

#### 有条件地等待任何事件

当你需要一个事件的时候，通常都是**有条件**的。而上述的几种示例中，你似乎并没有在 `ContinuousSessionContext`
取用一个事件的时候为此行为提供 **条件**。

当你需要提供一个对后续事件的取用条件时：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val next: Event = sessionContext.waitingForNext { event -> // this: EventProcessingContext
        // match ...
        true
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final Event next = sessionContext.waitingForNext((context, event) -> {
        // match ...
        return true;
    });
}
```

</TabItem>
</Tabs>

你可以为 `waitingForNext` 提供一个 **匹配函数**，通过提供的 `EventProcessingContext` 和事件本体，
并根据你的匹配结果来决定是否要**取用**此事件。

当得到过一次 `true` 时，`waitingForNext` 的等待便会结束。

#### 有条件地等待明确类型的事件


当然，你也可以在存在匹配条件的时候明确一个所需的事件类型：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val next: FooEvent = sessionContext.waitingForNext(FooEvent) { event -> // this: EventProcessingContext
        // match ...
        true
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final FooEvent next = sessionContext.waitingForNext(FooEvent.Key, (context, event) -> {
        // match ...
        return true;
    });
}
```

</TabItem>
</Tabs>

### `waitingForNextMessage`

`waitingForNextMessage` 是 [`waitingForNext`](#waitingfornext) 的衍生API。此API代表：等待并获取下一个符合条件的**消息事件的消息**。

与 `waitingForNext` 十分类似，只不过 `waitingForNextMessage` 的目标更为具体：一个**消息事件**，且返回值始终为 **MessageContent** 类型。

:::note 无条件

与 [**`waitingForNext`**](#waitingfornext) 类似，这种等待**不自动区分**任何诸如 `Bot`、组件等属性。

:::

#### 等待任何消息

你可以通过 `waitingForNextMessage` 等待下一个最快出现的消息事件。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val message: MessageContent = sessionContext.waitingForNextMessage()
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final MessageContent message = sessionContext.waitingForNextMessage();
}
```

</TabItem>
</Tabs>


#### 等待指定类型的任何消息

与 [**`waitingForNext`**](#waitingfornext) 类似，你可以指定一个具体的消息事件类型来等待。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val message: MessageContent = sessionContext.waitingForNextMessage(FooMessageEvent)
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final MessageContent message = sessionContext.waitingForNextMessage(FooMessageEvent);
}
```

</TabItem>
</Tabs>


#### 有条件地等待任何消息

你可以提供一个条件匹配函数来有条件的等待一个消息。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val message: MessageContent = sessionContext.waitingForNextMessage { event ->
        // 条件判断
        true
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final MessageContent message = sessionContext.waitingForNextMessage((context, event) -> {
        // 条件判断
        return true;
    });
}
```


</TabItem>
</Tabs>

#### 有条件地等待指定类型的任何消息

你可以提供一个条件匹配函数来有条件的等待一个消息，并且明确等待的事件类型。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
import love.forte.simbot.event.waitingForNextMessage

suspend fun onEvent(sessionContext: ContinuousSessionContext, event: Event) {
    val message: MessageContent = sessionContext.waitingForNextMessage(FooMessageEvent) { event ->
        // 条件判断
        true
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final MessageContent message = sessionContext.waitingForNextMessage(FooMessageEvent.Key, (context, event) -> {
        // 条件判断
        return true;
    });
}
```


</TabItem>
</Tabs>


### `next`

你可能注意到了，上面所提到的 `waitingForXxx` 类型的API，当你需要有条件的去等待时，对于条件的匹配是需要你直接通过编码来处理的。
但是大多数情况下，这种条件可能会有一个“来源”。举个例子，在一个好友消息事件中，你想要监听来自**这个好友**的下一个消息，
而不是一个任意的下一个消息。面对这种情况时，如果使用 `waitingForNextMessage`，那么大致代码逻辑应该是这样的：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
import love.forte.simbot.event.waitingForNextMessage

suspend fun onEvent(sessionContext: ContinuousSessionContext, friendMessageEvent: FriendMessageEvent) {
    val currentFriendId = friendMessageEvent.friend().id
    val currentBotId = friendMessageEvent.bot.id
    
    val message: MessageContent = sessionContext.waitingForNextMessage(FriendMessageEvent) { event ->
        // 需要是来自同一个bot的同一个好友
        event.bot.id == currentBotId && event.friend().id == currentFriendId
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, FriendMessageEvent friendMessageEvent) {
    final ID currentFriendId = friendMessageEvent.getFriend().getId();
    final ID currentBotId = friendMessageEvent.getBot().getId();
    
    final MessageContent message = sessionContext.waitingForNextMessage(FriendMessageEvent.Key, (context, event) -> {
        // 需要是来自同一个bot的同一个好友
        return event.getBot().getId().equals(currentBotId) 
               && event.getFriend().getId().equals(currentFriendId);
    });
}
```

</TabItem>
</Tabs>

未尝不可，但是略显繁琐。因此当处于 `ContinuousSessionContext` 作用域中时，其提供了另外一种类型的API：`next`。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, friendMessageEvent: FriendMessageEvent) {
    sessionContext.apply {
        val next: Event = friendMessageEvent.next()
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, FriendMessageEvent friendMessageEvent) {
    final Event next = sessionContext.next(friendMessageEvent);
}
```

</TabItem>
</Tabs>

#### 作用域

在介绍如何使用 `next` API之前，让我们先来简单介绍一下如何进入 `ContinuousSessionContext` 的"作用域"。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

**Scope functions**

你可能在上述代码中已经注意到了这段代码：

```kotlin
sessionContext.apply {
    val next: Event = friendMessageEvent.next()
}
```

这便是最基本的通过Kotlin的 [**Scope functions**](https://kotlinlang.org/docs/scope-functions.html) 
来达成进入 `ContinuousSessionContext` 作用域的目的。同样的，你也可以通过如下的类似方式来达成相似的效果：

<Tabs>
<TabItem value="run1" label="run (inner)">

```kotlin
sessionContext.run {
    val next: Event = friendMessageEvent.next()
}
```

</TabItem>
<TabItem value="run2" label="run (outer)">

```kotlin
val next: Event = sessionContext.run {
    friendMessageEvent.next()
}
```

</TabItem>
</Tabs>

<Tabs>
<TabItem value="with1" label="with (inner)">

```kotlin
with(sessionContext) {
    val next: Event = friendMessageEvent.next()
}
```

</TabItem>
<TabItem value="with2" label="with (outer)">

```kotlin
val next: Event = with(sessionContext) {
    friendMessageEvent.next()
}
```

</TabItem>
</Tabs>



**`Invoke`**

除了上述的 `Scope functions` 以外，为了方便简化代码，`ContinuousSessionContext` 提供了一个与 `run` 
十分类似的扩展函数：`invoke`。

<Tabs groupId="continuous-session-scope-invoke">
<TabItem value="invoke1" label="invoke (inner)">

```kotlin
sessionContext.invoke {
    val event: Event = friendMessageEvent.next()
}
```

而上述代码可以被简化为：

```kotlin
sessionContext {
    val event: Event = friendMessageEvent.next()
}
```

</TabItem>
<TabItem value="invoke2" label="invoke (outer)">

```kotlin
val event: Event = sessionContext.invoke {
    friendMessageEvent.next()
}
```

而上述代码可以被简化为：

```kotlin
val event: Event = sessionContext {
    friendMessageEvent.next()
}
```

</TabItem>
</Tabs>


**Receiver**

除了在代码中手动进入作用域，你也可以让你的函数在**一开始**就处于作用域当中，仅需将 `ContinuousSessionContext`
类型作为监听函数的接收者类型即可。

```kotlin
suspend fun ContinuousSessionContext.onEvent(friendMessageEvent: FriendMessageEvent) {
    val event: Event = friendMessageEvent.next()
}
```

**EventProcessingContext**

最开始的 [获取](#获取) 篇我们提到过，
`ContinuousSessionContext` 本质上是通过 `EventProcessingContext.get(SimpleScope.ContinuousSession)`
而得到的，因此 `EventProcessingContext` 也提供了一个相对应的扩展函数 `inSession` 来进入其作用域：

<Tabs>
<TabItem value="c1" label="参数 (inner)">

```kotlin
suspend fun onEvent(eventProcessingContext: EventProcessingContext, friendMessageEvent: FriendMessageEvent) {
    eventProcessingContext.inSession {
        val event: Event = friendMessageEvent.next()
    }
}
```

</TabItem>
<TabItem value="c2" label="参数 (outer)">

```kotlin
suspend fun onEvent(eventProcessingContext: EventProcessingContext, friendMessageEvent: FriendMessageEvent) {
    val event: Event = eventProcessingContext.inSession {
        friendMessageEvent.next()
    }
}
```

</TabItem>
<TabItem value="c3" label="receiver (inner)">

```kotlin
suspend fun EventProcessingContext.onEvent(friendMessageEvent: FriendMessageEvent) {
    inSession {
        val event: Event = friendMessageEvent.next()
    }
}
```

</TabItem>
<TabItem value="c4" label="receiver (outer)">

```kotlin
suspend fun EventProcessingContext.onEvent(friendMessageEvent: FriendMessageEvent) {
    val event: Event = inSession {
        friendMessageEvent.next()
    }
}
```

</TabItem>
</Tabs>

<hr />

以上就是大部分进入 `ContinuousSessionContext` 的方式了。后续的示例中会以使用 `invoke` 的方式作为主要的示例方式：

```kotlin
sessionContext {
    // via invoke
}
```

</TabItem>
<TabItem value='Java'>

对于Java来讲，没有什么作用域概念。这主要适用于在Kotlin中的使用。在Java中，只需要将调用者作为第一个参数填入即可。

</TabItem>
</Tabs>


#### 下一个任意相似事件

回到正题，来继续介绍一下 `next` 函数。与 `waitingForNext` 不同，`next` 需要一个具体的 `Event` 或者 `EventProcessingContext` 
作为"基准"：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, event: FooEvent) {
    sessionContext {
        val next: Event = event.next()
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final Event next = sessionContext.next(fooEvent);
}
```

</TabItem>
</Tabs>

其中，`fooEvent` 便是一个"基准"，并以其为准来匹配下一个"相似事件"。

那么"基准"是如何进行匹配的呢？首先见下表：

| 当前事件类型                 | 目标事件同类型                                 |   目标事件不同类型 |
|:-----------------------|-----------------------------------------|-----------:|
| `Event`                | `Event.bot` 的ID要相同                      | _不会出现不同类型_ |
| `OrganizationEvent`    | `OrganizationEvent.organization` 的ID要相同 |         放行 |
| `UserEvent`            | `UserEvent.user` 的ID要相同                 |         放行 |
| `MessageEvent`         | `MessageEvent.source` 的ID要相同            |         放行 |
| `ChatRoomMessageEvent` | `ChatRoomMessageEvent.author` 的ID要相同    |         放行 |

> 表格摘选自 `next` 文档注释

简单来解释一下这个表格所代表的含义。首先以**第二行**为例，它代表：
如果当前事件（即**作为基准**的事件）类型为 `OrganizationEvent`，且在 `next` 中监听到的下一个事件的类型也是 `OrganizationEvent`
类型，则判断两个事件的 `organization.id` 是否相同。 

而结合整个表，它的行为可以大致被翻译成类似于使用 `waitingForNext` 的如下逻辑：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    val currentBot = fooEvent.bot
    sessionContext.waitingForNext { event ->
        val eventBot = event.bot
        // 判断bot是否一致
        if (currentBot !== eventBot && currentBot.isNotMe(eventBot.id)) {
            return@waitingForNext false
        }
    
        // 如果都是 OrganizationEvent
        if (fooEvent is OrganizationEvent && event is OrganizationEvent) {
            if (fooEvent.organization().id != event.organization().id) {
                return@waitingForNext false
            }
        }
    
        // 如果都是 UserEvent
        if (fooEvent is UserEvent && event is UserEvent) {
            if (fooEvent.user().id != event.user().id) {
                return@waitingForNext false
            }
        }
    
        // 如果都是 UserEvent
        if (fooEvent is MessageEvent && event is MessageEvent) {
            if (fooEvent.source().id != event.source().id) {
                return@waitingForNext false
            }
    
            // 如果都是 ChatRoomMessageEvent
            if (fooEvent is ChatRoomMessageEvent && event is ChatRoomMessageEvent) {
                if (fooEvent.author().id != event.author().id) {
                    return@waitingForNext false
                }
            }
        }
        
        true
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent2(ContinuousSessionContext sessionContext, Event fooEvent) {
    final Bot currentBot = fooEvent.getBot();
    sessionContext.waitingForNext((context, event) -> {
        Bot eventBot = event.getBot();
        // 判断bot是否一致
        if (currentBot != eventBot && !currentBot.isMe(eventBot.getId())) {
            return false;
        }

        // 如果都是 OrganizationEvent
        if ((fooEvent instanceof OrganizationEvent) && (event instanceof OrganizationEvent)) {
            if (!((OrganizationEvent) fooEvent).getOrganization().getId().equals(((OrganizationEvent) event).getOrganization().getId())) {
                return false;
            }
        }

        // 如果都是 UserEvent
        if ((fooEvent instanceof UserEvent) && (event instanceof UserEvent)) {
            if (((UserEvent) fooEvent).getUser().getId() != ((UserEvent) event).getUser().getId()) {
                return false;
            }
        }

        // 如果都是 UserEvent
        if ((fooEvent instanceof MessageEvent) && (event instanceof MessageEvent)){
            if (((MessageEvent) fooEvent).getSource().getId() != ((MessageEvent) event).getSource().getId()) {
                return false;
            }

            // 如果都是 ChatRoomMessageEvent
            if ((fooEvent instanceof ChatRoomMessageEvent) && (event instanceof ChatRoomMessageEvent)){
                if (((ChatRoomMessageEvent) fooEvent).getAuthor().getId() != ((ChatRoomMessageEvent) event).getAuthor().getId()) {
                    return false;
                }
            }
        }

        return true;
    });
}
```

</TabItem>
</Tabs>

#### 下一个指定类型的相似事件

你可以为 `next` 提供一个事件类型来提供进一步约束。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    sessionContext {
        fooEvent.next(BarEvent)
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final Event next = sessionContext.next(fooEvent, BarEvent.Key);
}
```

</TabItem>
</Tabs>


### `nextMessage`

就像 `waitingForNext` 和 `waitingForNextMessage` 之间一样，`ContinuousSessionContext` 也为 
`next` 提供了一个类似的变种：`nextMessage`。

#### 下一个指定类型的消息

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    sessionContext {
        val next: MessageContent = fooEvent.nextMessage(BarMessageEvent)
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final MessageContent next = sessionContext.nextMessage(fooEvent, FooMessageEvent.Key);
}
```

</TabItem>
</Tabs>

## 超时处理

很多情况下，当你在使用上述API的时候，很有可能会需要为它们提供一个**超时限制**来防止就那么一直等待下去。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

在Kotlin中，所有的超时处理都很简单：你可以直接使用Kotlin所提供的 
[`withTimeout`](https://kotlinlang.org/docs/cancellation-and-timeouts.html#asynchronous-timeout-and-resources)
来进行超时控制。

<Tabs>
<TabItem value="milli" label="MILLISECONDS">

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    // 5s内得到下一个事件，否则会抛出异常。
    val next = withTimeout(5000) {
        sessionContext.waitingForNext()
    }
}
```

</TabItem>
<TabItem value="duration" label="DURATION">

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    // 5s内得到下一个事件，否则会抛出异常。
    val next = withTimeout(5.seconds) {
        sessionContext.waitingForNext()
    }
}
```

</TabItem>
</Tabs>

</TabItem>
<TabItem value='Java'>

在Java中，`ContinuousSessionContext` 为几乎所有的API的参数中都填充了与超时有关的参数。

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, FriendMessageEvent fooEvent) {
    // 下面都代表要在5s内得到下一个事件，否则抛出异常。
    final Event next1 = sessionContext.waitingForNext(5, TimeUnit.SECONDS);
    final Event next2 = sessionContext.waitingForNext(Duration.ofSeconds(5));
}
```

:::info 略逊一筹 

与Kotlin的 `withTimeout` 相比，在Java中使用超时控制可能不会那么的随心所欲或灵活。
例如下述Kotlin代码：

```kotlin
withTimeout(10.seconds) {
    val next1 = sessionContext.waitingForNext()
    val next2 = sessionContext.waitingForNext()
    val next3 = sessionContext.waitingForNext()
}
```

这代表要在10秒内得到三个后续事件。可以看出，在Kotlin中使用 `withTimeout` 代码块可以控制更大的范围，
而Java中的超时参数只能针对指定的一个API。

:::

</TabItem>
</Tabs>

<hr />

## 持续会话

在 `ContinuousSessionContext` 中，除了直接通过上述各式API进行等待以外，其还提供了几个用于分离结果的推送与获取的
`provider` 和 `receiver`。

### Provider

在 [waiting](#waiting) 中，我们提到了一个 `provider`。

它的具体类型是 `ContinuousSessionProvider<T>`，它会被使用在 `waiting` 等API的参数中，作为参数函数体的参数之一。

:::note T?

泛型 `T` 即代表为此提供者对外提供的类型。

:::

`ContinuousSessionProvider` 代表为一个等待中的持续会话的结果提供者，当你向一个 `provider` 提供了结果，
则对应正在等待的会话也将结束。

正常情况下，使用一个 `provider` 是在 `waiting` 等API的函数参数体内进行的：

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    val value: String = sessionContext.waiting { provider ->
        // 使用 provider
        provider.push("VALUE")
    }
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, Event fooEvent) {
    final String value = sessionContext.waiting((context, provider) -> {
        // 使用 provider
        provider.push("VALUE");
    });
}
```

</TabItem>
</Tabs>

除了通过上述API之外，你可以在进行等待的时候提供一个**id**, 然后在其他地方通过相同的ID来获取 `provider`。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    val value: String = sessionContext.waiting("ID") { 
        // 此处永远不会使用provider, 而是在 [onEvent2] 中使用
    }
}

suspend fun onEvent2(sessionContext: ContinuousSessionContext, barEvent: BarEvent) {
    val provider: ContinuousSessionProvider<String> = sessionContext.getProvider("ID")
            ?: return // 此ID不存在
    
    // 在其他监听函数中推送结果
    provider.push("VALUE")
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, FriendMessageEvent fooEvent) {
    final String value = sessionContext.waiting("ID", (context, provider) -> {
        // 此处永远不会使用provider, 而是在 [onEvent2] 中使用
    });
}

@Listener
public void onEvent2(ContinuousSessionContext sessionContext, FriendMessageEvent fooEvent) {
    final ContinuousSessionProvider<String> provider = sessionContext.getProvider("ID");
    if (provider == null) {
        return;
    }

    provider.push("VALUE");
}
```

</TabItem>
</Tabs>

或者，在同一个监听函数中异步的去使用...?

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    val deferred: Deferred<String> = fooEvent.bot.async {
        sessionContext.waiting("ID") {
            // 此处永远不会使用provider
        }
    }
    
    fooEvent.bot.launch {
       // 得到异步结果时输出到控制台
       println("VALUE: ${deferred.await()}")
    }
    
    // 等待10s后，尝试推送结果
    delay(10.seconds)
    sessionContext.getProvider<String>("ID")?.push("VALUE")
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, FriendMessageEvent fooEvent) {
    final Bot bot = fooEvent.getBot();
    bot.delayAndCompute(0, () -> {
        return sessionContext.waiting("ID", (context, provider) -> {
            // 此处永远不会使用provider
        });
    }).thenAccept(value -> {
        // 得到异步结果时输出到控制台
        System.out.println("VALUE: " + value);
    });

    // 等待10s后，尝试推送结果
    bot.delay(Duration.ofSeconds(10), () -> {
        final ContinuousSessionProvider<String> provider = sessionContext.getProvider("ID");
    });
}
```

</TabItem>
</Tabs>

:::info 警惕类型

需要注意，当通过 `getProvider(String)` 获取 `provider` 的时候，请注意其泛型类型。
如果使用了错误的泛型类型来接收结果会导致出现异常。

:::

### Receiver

与 [`provider`](#provider) 类似，`ContinuousSessionContext` 提供了一个用来获取某个会话结果的类型：
`ContinuousSessionReceiver<T>`。

`ContinuousSessionReceiver` 与 `ContinuousSessionProvider` 相对，它代表用于获取一个指定ID的结果接收器。
你可以通过 `receiver` 来在其他地方接收指定会话的结果。

<Tabs groupId='code'>
<TabItem value='Kotlin'>

```kotlin
suspend fun onEvent(sessionContext: ContinuousSessionContext, fooEvent: Event) {
    val receiver: ContinuousSessionReceiver<String> = sessionContext.getReceiver("ID")
        ?: return
    
    // 挂起并等待结果
    val value = receiver.await()
}
```

</TabItem>
<TabItem value='Java'>

```java
@Listener
public void onEvent(ContinuousSessionContext sessionContext, FriendMessageEvent fooEvent) throws InterruptedException {
    final ContinuousSessionReceiver<String> receiver = sessionContext.getReceiver("ID");
    if (receiver == null) {
        return;
    }

    // 转化为Future来使用
    final Future<String> valueFuture = receiver.asFuture();

    // 阻塞并等待结果
    receiver.waiting();

    // 或:
    // 提供可能的超时时间。
    try {
        receiver.waiting(5, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
        // 如果超时，则会抛出 TimeoutException
        throw new RuntimeException(e);
    }
}
```

</TabItem>
</Tabs>


:::info 警惕类型

需要注意，当通过 `getReceiver(String)` 获取 `receiver` 的时候，请注意其泛型类型。
如果使用了错误的泛型类型来接收结果会导致出现异常。

:::







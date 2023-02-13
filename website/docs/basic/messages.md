---
sidebar_position: 17
title: 消息
---

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';


:::info 定义为先

在阅读本章节之前，你应当已经了解过了 [**消息的定义**](../definition/message-overview) 的相关内容。

:::

消息是在事件处理过程中你可能会频繁接触的东西。

## 消息对象

消息对象是指 `Message` 类型的对象，它们是消息元素 (`Message.Element`) 有一个或多个**不可变的**消息集。

### 构建消息对象

对一个消息对象的构建方式取决于消息类型本身。举几个例子，[标准消息类型](../definition/message-overview/standard-message)
中的 [文本消息](../definition/message-overview/standard-message#text) 
的构建方式是使用其静态工厂方法 `Text.of` 或 `Text.getEmptyText()`：

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val text1: Text = "Hello".toText()
val text2: Text = Text { "World" }
val emptyText: Text = Text()
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final Text text = Text.of("Hello");
final Text emptyText = Text.getEmptyText();
```

</TabItem>
</Tabs>

而 [AT](../definition/message-overview/standard-message#at) 则直接创建一个新实例即可：


<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val at: At = At(123.ID)
val atMember: At = At(event.author().id)
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final At at = new At(Identifies.ID(123));
final At atMember = new At(event.getAuthor().getId());
```

</TabItem>
</Tabs>

对于 [AT所有](../definition/message-overview/standard-message#atall)，其作为一个 `object` 则可以直接使用：


<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val atAll: AtAll = AtAll
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final AtAll atAll = AtAll.INSTANCE;
```

</TabItem>
</Tabs>

因此，具体问题具体分析，不管是[标准消息类型](../definition/message-overview/standard-message)还是由组件或第三方提供的额外消息类型，
阅读其相关文档或注释来了解如果构建它们。

### 构建消息链

上述我们讲的是一个独立消息元素（`Message.Element`）的构建方式。将多个消息元素“组合”起来，便形成了**消息链**（`Messages`）。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

在Kotlin中，合并消息元素为一个消息链的最基本方式便是使用 `plus` 操作符直接进行拼接。不过需要注意，前文我们提到过消息是**不可变**的，消息链也是，
因此通过操作符合并后将会得到一个**新的消息（链）**。

以一个普通的 `文字 + AT` 为例：

```kotlin
val messages = "文字".toText() + At(123.ID)
```

当然，你也可以将一个包含了多个消息元素的集合转化为一个消息链：

```kotlin
val messages = listOf("文字".toText(), At(123.ID), AtAll).toMessages()
```

</TabItem>
<TabItem value="Java" label="Java">

在Java中，你可以通过 `Messages.toMessages(...)` 将多条消息元素合并为一个消息链。

以一个普通的 `文字 + AT` 为例：

```java
final Messages messages = Messages.toMessages(Text.of("文字"), new At(Identifies.ID(123)));
```

</TabItem>
</Tabs>

除了直接转化，核心库也提供了一个用于构建消息链的构建器 `MessageBuilder`。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

在Kotlin中，直接使用 `buildMessages` 来通过构建器构建你的消息链：

```kotlin
val messages = buildMessages {
    // 提供与标准消息类型中对应的API
    at(123.ID)
    atAll()
    text("文本")
    
    // 通过 `unaryPlus` 操作符直接添加一个任意的 `Message.Element` 实例或纯文本
    +"还是文本"
    +At(456.ID)
    
    // 或者使用传统的 append. 效果与 `unaryPlus` 操作符一致。
    append(AtAll)
}
```


</TabItem>
<TabItem value="Java" label="Java">

在Java中，你可以直接构建它并使用：

```java
final MessagesBuilder messagesBuilder = new MessagesBuilder();

final Messages messages = messagesBuilder
        // 提供与标准消息类型中对应的API
        .at(Identifies.ID(123))
        .atAll()
        .text("文本")
        // 通过append追加任意 Message.Element 实例或纯文本
        .append("还是文本")
        .append(new At(Identifies.ID(456)))
        .append(AtAll.INSTANCE, new Face(Identifies.ID(114)))
        // 构建结果
        .build();
```

</TabItem>
</Tabs>

## 消息链使用

你已经了解了消息链如何被构建出来，那么接下来你可能需要了解如何使用这个消息链。

### 遍历消息链

消息链 `Messages` 实现了 `View` 接口，你可以将其视为一个近似于**不可变**列表使用。


<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val messages: Messages = ...

for (element in messages) {
    // 遍历消息链
}

// 索引访问
val secondMessage: Message.Element<*> = messages[1]

// 消息类型访问
val ats: List<At> = messages[At]
```


</TabItem>
<TabItem value="Java" label="Java">

```java
final Messages messages = ...;

for (Message.Element<?> element : messages) {
    // 遍历消息链
}

// 索引访问
final Message.Element<?> secondElement = messages.get(1);

// 消息类型访问
final List<At> ats = messages.get(At.Key);
```

</TabItem>
</Tabs>

### "追加"元素

上文提到过，消息是**不可变**的，因此如果你想要向一个消息链中_**"追加"**_一个消息，其实你需要的是得到一个增加了目标消息元素的新消息链。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val messages: Messages = ...

// 使用 plus 操作符追加并得到一个新的消息链。
val newMessages: Messages = messages + AtAll
```


</TabItem>
<TabItem value="Java" label="Java">

```java
final Messages messages = Messages.messages();

// 使用 plus 追加并得到一个新的消息链。
// 注意：不要使用 .add(...)，这将会引发异常。
final Messages newMessages = messages.plus(AtAll.INSTANCE);
```

</TabItem>
</Tabs>


## 消息发送

大部分消息的发送能力由 [SendSupport](../definition/ability-support#sendsupport) 或 [ReplySupport](../definition/ability-support#replysupport) 提供。

### SendSupport

`SendSupport` 是接口类型，其代表为一个“可以发送消息的目标”，由 `ChatRoom` (聊天室) 和 `Contact` (联系人) 默认实现。`ChatRoom` 是 `Group`、`Guild`、`Channel` 的父类型，而 `Contact` 则为 `Friend`、`Member` 的父类。
简单来讲，基本上常见的可联系对象都是实现了 `SendSupport` 的。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val messages: Messages = ...
val group: Group = ...

val receipt = group.send(messages)
```


</TabItem>
<TabItem value="Java" label="Java">

```java
final Messages messages = ...;
final Group group = ...;

final MessageReceipt receipt = group.sendBlocking(messages);
// 或者
final CompletableFuture<? extends MessageReceipt> future = group.sendAsync(messages);
```

</TabItem>
</Tabs>

### ReplySupport

`ReplySupport` 是接口类型，其代表为一个“可以回复消息的目标”，由 `MessageEvent` 默认实现。与 `SendSupport` 不同，`ReplySupport` 的默认实现是**消息事件**类型。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val messages: Messages = ...
val event: MessageEvent = ...

val receipt = event.reply(messages)
```


</TabItem>
<TabItem value="Java" label="Java">

```java
final Messages messages = ...;
final MessageEvent event = ...;

final MessageReceipt receipt = event.replyBlocking(messages);
// 或者
final CompletableFuture<? extends MessageReceipt> future = event.replyAsync(messages);

```

</TabItem>
</Tabs>

<hr />

_"发送消息"_ 和 _"回复消息"_ 的功能行为类似，但又不太一样。比如在mirai组件中，使用 `reply` 会尝试默认携带一个"引用回复"对象。
在实际开发中，根据你的实际需求和具体语义来选择一个合适的消息发送方式吧。

## 消息回执

当通过 `SenSupport.send` 或 `ReplySupport.reply` 发送消息后，会得到一个 `MessageReceipt` 类型的返回值，这就是 **"消息回执"**。

消息回执通常情况下来用于得知消息发送的情况、它们的标识，和进行删除（撤回）操作。
不过对于不同组件中的实现来讲，也许它们还会提供更多的特殊能力。

### DeleteSupport

`MessageReceipt` 实现接口 `DeleteSupport`，代表其是可以被 _"删除"_ 的。通常情况下，这种删除即代表了常说的**消息撤回**。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val messages: Messages = ...
val event: MessageEvent = ...

val receipt = event.reply(messages)
receipt.delete()
```


</TabItem>
<TabItem value="Java" label="Java">

```java
final Messages messages = ...;
final MessageEvent event = ...;

final MessageReceipt receipt = event.replyBlocking(messages);
receipt.deleteBlocking();

// 或者
final CompletableFuture<? extends MessageReceipt> future = event.replyAsync(messages);
future.thenCompose(DeleteSupport::deleteAsync);
```

</TabItem>
</Tabs>

### 独立与聚合

在不同的组件中，消息发送的具体逻辑可能并不相同。发送的 `Messages` 可能会根据组件的不同而被**拆分**为多条实际消息发送（例如 Kook 组件中发送一个既有图片又有文字的消息）。 
但是一次发送只会得到一个 `MessageReceipt` ，那么应该如何判断此回执中实际的数量呢？

`MessageReceipt` 提供了两个标准子类型：`SingleMessageReceipt` 和 `AggregatedMessageReceipt`。

#### SingleMessageReceipt

如同字面意思，`SingleMessageReceipt` 代表为**一个或零个**实际消息发送后的回执。
`SingleMessageReceipt` 额外提供了一个属性 `id`，其代表这个具体消息发送后的回执标识。



#### AggregatedMessageReceipt

`AggregatedMessageReceipt` 意为**聚合回执**，代表为**多个**实际消息发送后的回执。
首先来简单看一下 `AggregatedMessageReceipt` 的简化版定义：

```kotlin

/**
 * 聚合消息回执，代表多个 [SingleMessageReceipt] 的聚合体。
 *
 * @see StandardMessageReceipt
 * @see SingleMessageReceipt
 * @see aggregation
 */
public abstract class AggregatedMessageReceipt : StandardMessageReceipt(), Iterable<SingleMessageReceipt> {
    
    /**
     * 聚合消息中的 [isSuccess] 代表是否存在**任意**回执的 [MessageReceipt.isSuccess] 为 `true`。
     */
    abstract override val isSuccess: Boolean
    
    /**
     * 当前聚合消息中包含的所有 [MessageReceipt] 的数量。
     */
    public abstract val size: Int
    
    /**
     * 根据索引值获取到指定位置的 [SingleMessageReceipt]。
     */
    public abstract operator fun get(index: Int): SingleMessageReceipt
    
    /**
     * 删除其所代表的所有消息回执。
     */
    override suspend fun delete(): Boolean
    
    /**
     * 删除其所代表的所有消息回执。
     */
    public suspend fun deleteAll(): Int
}
```

可以看出，`AggregatedMessageReceipt` 实现了 `Iterable<SingleMessageReceipt>`，说明 `AggregatedMessageReceipt` 实际上就是多个 `SingleMessageReceipt` 的聚合体。
它提供了一些额外的元素获取API（`get`）和集合属性（`size`），并同样实现 `delete`。

此处的 `delete` 便代表**删除所有**，它会依次遍历内部的所有独立回执，并尝试删除它。

:::info 可能的中断

需要注意的时，如果在删除所有的过程中发生了异常，则可能会导致这个删除所有过程执行的**不完整**。如果想要避免这种情况，则需要自行通过循环来精准控制。

:::

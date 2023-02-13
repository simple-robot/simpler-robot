---
title: 消息概述
tags: [消息]
---

消息是不可或缺的一部分。

在 `simbot2` 中，消息的主要存在形式是以 _猫猫码_ 的实体或字符串为主的，这也是从CQ时代所遗留下来的传统。
猫猫码拥有很高的灵活性（因为其本质可以理解为一种Map或一种特殊规则的字符串），但是也存在诸多劣势（语义、类型不明确以及性能问题）。

在 `simbot3` 中，猫猫码或许会在未来被实现为一种序列化的方式，但将不再是“消息”的注意表现形式与途径。
消息的概念将会由接口类型 `Message` 来进行描述，由核心、组件等所需模块提供实现与解释。



对于 `Message` 消息类型来讲，它只应可能存在两种类型分支：`Messages` 和 `Message.Element` 。


## Message.Element
`Message.Element` 代表一个最小单位的 **消息元素**，是 `Message` 类型的实现之一。
`Message.Element` 类型本身除了规定了一些来源属性以外，并没有实质性的属性或函数可以使用。对于消息的定义与实现是完全自由的。


:::info 相关参考

对于一些常见的消息类型，你可以参考 [标准消息](standard-message.md)。

对于消息的序列化，你可以参考 [序列化](message-serialization.md)。

:::

`Message.Element` 接口本身只规定了如下属性/函数：

| 属性/函数       | 类型                         | 描述                            |
|-------------|----------------------------|-------------------------------|
| key         | [Message.Key](#messagekey) | 此消息的key信息。                    |
| component   | Component                  | 提供此消息实现的组件对象。同 key.component。 |
| toString()  | String                     | 得到此消息的字符串表现。                  |
| equals(Any) | boolean                    | 比较两个元素是否相同。                   |


## Messages
`Messages` 代表了多个 `Message.Element`，你可以将其理解为消息元素的列表，是 `Message` 类型的实现之一。
它可以表示为一个消息元素列表，因为 `Messages` 实现了 `View<Message.Element<*>>`，间接实现了 `Iterable<Message.Element<*>>`。

### 不可变性
`Messages` 在列表的角度上是不可变的，当你要调整一个 `Messages` (增加、删除) 的时候，你需要获取新的实例。


### 构建

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
// 通过用 '+' 拼接消息元素来得到结果。
val messages: Messages = "abc".toText() + At(123.ID) + AtAll
// 同上
val messages2: Messages = "abc".toText() + At(123.ID) + AtAll
// 通过拼接两个消息连来得到新的消息链
val combinedMessage = messages + messages2
val messageElementList = listOf(
    "simbot".toText(),
    At("666".ID),
    AtAll
)
// 通过 "消息列表(List<Message.Element>)" 转化为 "消息列表(Messages)"
val messagesOfList = messageElementList.toMessages()

// 空实现
val emptyMessage = EmptyMessages

// 通过builder
buildMessages {
  // ...
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
List<Message.Element<?>> messageList = new ArrayList<>(3);
messageList.add(Text.of("simbot"));
messageList.add(new At(Identifies.ID(123)));
messageList.add(AtAll.INSTANCE);

// 通过列表得到消息链
final Messages messagesOfList = Messages.listToMessages(messageList);

// 注意! Messages 不允许直接的修改操作
// messagesOfList.add(AtAll.INSTANCE);

// 需要通过 plus 得到新的消息链
final Messages newMessagesOfList = messagesOfList.plus(AtAll.INSTANCE);

// 通过 Messages.toMessages 得到消息链
final Messages messages = Messages.toMessages(
                            Text.of("forte"),
                            new At(Identifies.ID(114514)),
                            AtAll.INSTANCE
                        );
                        
// 通过builder
MessagesBuilder builder = new MessagesBuilder();
// ...
Messages built = builder.build();
```

</TabItem>
</Tabs>


在 `Java` 中，用来构建一条消息链的主要方式为 `Messages.listToMessages` 和 `Messages.toMessages` 以及 `Messages.plus`。



## Message.Key

是一个应该由 Message.Element实现中的伴生对象进行实现的约定类型。

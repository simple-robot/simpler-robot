---
title: 预期能力 
---
不论是事件，还是一个组织、联系人，他们都有可能存在一些模棱两可的能力。<br/>
举个例子，对于一个 `GroupMessageEvent` 群消息事件来讲，理论上它应该是能够拥有 **回复** 能力的， 即针对这个消息事件，回复一个消息，这种功能是十分常见的，例如Mirai组件中的引用回复。<br/>
再比如，对于一个 `Member` 群成员对象来讲，它理应是能够 **发送消息** 的。

对于这种 “理应存在” 或者 “十分常见” 的能力，simbot提供了几个辅助接口，作为对此类能力的期望。

<br />

"预期能力"通常表现为接口类型，由能够满足这些能力的类型进行实现。

举个例子，比如说一个组件叫做 `FooComponent`, 这个组件中代表"群聊 `Group` " 的类型叫做 `FooGroup`。假如说这个 `FooGroup` 允许bot主动的退出， 那么 `FooGroup`
便可以实现 [DeleteSupport](#deletesupport) 来代表退出(删除 `delete` ) 一个群聊。 它大概的样子就是：

```kotlin
public interface FooGroup : Group, DeleteSupport {
    // ...
}
```

## SendSupport

代表 **消息发送能力**。



## ReplySupport

代表 **消息回复能力**。

## MessageReactSupport

代表 **消息回应能力**。

## MuteSupport

代表 **禁言/静音能力**。

## DeleteSupport

代表 **删除能力**。

**删除能力** 代表其实现者拥有执行 `delete` 操作的能力。

实现了 `DeleteSupport` 的类型中，`delete` 可能有不同的含义。比如说，如果是一个 `MessageEvent` (消息事件)实现了 `DeleteSupport`, 那么通常情况下
`delete` 代表对这个事件所产生的消息进行"撤回"。简单来说，就是撤回这个消息。

如果是 `MessageReceipt` 实现了 `DeleteSupport` 那么 `delete` 的含义基本上可以代表为撤回自己发送的这个消息。（因为 `MessageReceipt` 是bot发送消息而产生的）。

如果是一个 `Group` 实现了 `DeleteSupport`, 那么基本上便代表退出对应的群。

如果是一个 `Member` 实现了 `DeleteSupport`, 那么基本上代表将这个成员踢出对应的群聊/频道。类似于 `kick` 。

如果是一个 `Friend` 实现了 `DeleteSupport`, 那么基本上代表删除这个好友。


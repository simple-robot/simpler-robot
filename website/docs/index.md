---
sidebar_position: 1
title: '首页'
---

import HeaderShow from '@site/src/components/HeaderShow';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

<HeaderShow />

此文档的内容为 [Simple Robot](https://github.com/simple-robot/simpler-robot) v3.x 版本的使用手册。


## 概述
Simple Robot(下文简称simbot) 一开始是作为一个机器人标准开发框架所诞生的，而现在，
它是一个保留了作为机器人标准开发框架为主要职责的bot风格事件调度框架，目前处于v3版本。

**simbot3** 基于 [Kotlin](https://kotlinlang.org/) 语言开发，
主要面向于 **Kotlin JVM** (未来可能会面向**Kotlin Multiplatform**) 和 **Java** 开发者。

**simbot3** 提供由基础核心模块到高级boot模块多种风格的模块使用，并且提供[Spring Boot](https://spring.io/projects/spring-boot) Starter来帮助Java开发者快速上手。

## 特性

### 异步高效

**simbot3** 基于 [Kotlin Coroutine](https://kotlinlang.org/docs/coroutines-guide.html) 
提供基本全异步的API，保证整体性能； 并向Java开发者提供兼容API，提供兼容性的同时通过显式的函数语义向开发者描述两套API之间的异同。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val friend: Friend = event.friend()  // suspend api
friend.send("Hey!")                  // suspend api
```

</TabItem>
<TabItem value="Java" label="Java Blocking">

simbot3 提供面向Java兼容的阻塞API。

> 稍微地牺牲掉一点点的异步特性，换来简单明了的使用体验。

```java
Friend friend = event.getFriend();   // blocking api for java
friend.sendBlocking("Hey!");         // blocking api for java
```

</TabItem>
<TabItem value="Java Async">

simbot3 提供面向Java兼容的异步API。这些异步API基于 JDK8 的 `CompletableFuture`，

> 如果你对它足够熟悉，那么就用擅长的方式来进一步发挥异步API的功效吧。

```java
event.getFriendAsync() // async api for Java
    .thenCompose(friend -> friend.sendAsync("Hey!"));
```

</TabItem>
</Tabs>



### 简单明了

**simbot3** 相较于从前的版本，拥有更加简明和更加易用的API。


<Tabs groupId="simbot-history-version">
<TabItem value="simbot3">

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
@Listener
suspend fun myListener(messageEvent: GroupMessageEvent) {
    val group = messageEvent.group()
    val groupId = group.id
    val groupName = group.name
    // ...
    group.send("Hey, simbot3")
}
```

</TabItem>
<TabItem value="Java" label="Java Blocking">

```java
@Listener
public void myListener(GroupMessageEvent messageEvent) {
   Group group = messageEvent.getGroup();
   ID groupId = group.getId();
   String groupName = group.getName();
   // ...
   group.sendBlocking("Hey, simbot3");
}
```

</TabItem>
<TabItem value="Java Async">

```java
@Listener
public CompletableFuture<?> myListener(GroupMessageEvent messageEvent) {
    CompletableFuture<? extends Group> groupAsync = event.getGroupAsync();
    
    CompletableFuture<? extends ID> groupIdAsync = groupAsync.thenApply(Group::getId);
    CompletableFuture<? extends String> groupNameAsync = groupAsync.thenApply(Group::getName);
    
    
    // 返回 CompletableFuture 类型的结果时，则会在结果返回后非阻塞地挂起等待此Future结束，然后才会继续下一个监听函数
    // 如果想要当前监听函数作为一个真正地‘异步监听’（即忽略最终返回值），则不需要返回此结果
    return groupAsync.thenCompose(group -> group.sendAsync("Hello, simbot3"));
}
```

</TabItem>
</Tabs>

</TabItem>

<TabItem value="simbot2">

:::note 实际上...

在**simbot2**中，尽管其已经开始使用**Kotlin**进行开发，
但是所使用的开发模式、风格和思想仍旧停留在**Java**的层面中，
且整体设计相对欠佳。

:::

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
@OnGroup
fun myListener(messageEvent: GroupMsg, sender: Sender) {
    val groupInfo = messageEvent.groupInfo
    val groupCode = groupInfo.groupCode
    val groupName = groupInfo.groupName
    // ...
    sender.SENDER.groupMsg(messageEvent, "Bye, simbot2")
}

```

</TabItem>
<TabItem value="Java">

```java
@OnGroup
public void myListener(GroupMsg messageEvent, Sender sender) {
    GroupInfo groupInfo = messageEvent.getGroupInfo();
    String groupCode = groupInfo.getGroupCode();
    String groupName = groupInfo.getGroupName();
   // ...
   sender.SENDER.sendGroupMsg(messageEvent, "Bye, simbot2");
}
```

</TabItem>
</Tabs>

</TabItem>
</Tabs>

### 组件协同
**组件**是**simbot3**中的核心概念之一，一个**组件**代表了一个目标平台的能力。

:::note 实际上...

实际上在 simbot2 中就存在了**组件**的概念，但是那时的组件实现只有一个，且（就算有多个也）难以实现协同。

:::

因此，**simbot3**中增强了**组件**的概念与能力，更加突出了**组件**中属于它们自己的特色，并支持了组件的协同能力。在 **simbot3**中，
你可以在同一环境下运作着不同组件平台的多个bot。

:::info 并非所有...

组件协同是由**simbot3**支持的基本能力，但是这并不代表任何组件之间都能毫无保留的在同一环境下肆意协同。
由于组件之间的实现不尽相同，因此很有可能会出现诸如依赖冲突等无法规避的情况。所以在进行组件协同前，
应当先充分调查各个组件的依赖以及对其他组件的兼容性。

:::

### 还有更多...
**simbot3** 与 **simbot2** 相比，基本上是推翻重来的。而这个过程使得 **simbot3** 中诞生了很多比曾经更优秀的特性。
也许，你会在使用过程中逐渐发现...

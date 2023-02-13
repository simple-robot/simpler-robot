---
authors: forliy
title: 2022年第38周周报
tags: [2022周报,周报]
---


大概是2022年第38周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import Label from '@site/src/components/Label'

## 🚀 核心库更新..?

本周 [核心库](https://github.com/simple-robot/simpler-robot) 依然保持在 <Label>v3.0.0-beta</Label> 版本，暂时没有新的变更。

这是为什么呢？实际上我们这两周一直在寻找一个针对目前开发中痛点的解决方案。
那么是什么痛点呢？首先来看如下代码：

```kotlin
interface FooEvent {
    suspend fun user(): User
}
```

我们假设上述中的 `FooEvent` 是一个事件类型，这个事件中可以通过 `user()` 得到一个 `User` 对象。
而问题是，`user()` 函数是一个 `suspend` 函数，即挂起函数，因此它并不能直接被Java平台的用户或JS平台的用户使用，
因此我们需要对外再提供一套额外的API，使其变为如下形式：

```kotlin
interface FooEvent {
    @JvmSynthtic
    suspend fun user(): User
    
    // on Java platform
    
    @Api4J
    fun userBlocking(): User = runInBlocking { user() }
    @Api4J
    fun userAsync(): CompletableFuture<out User> = runInAsync { user() }
    
    // or on JS platform
    
    @Api4JS
    fun userAsync(): Promise<out User> = runInAsync { user() }
}
```

但是如果所有的可挂起函数都需要如此额外提供平台兼容性的API，这会使得我们开发中的重复工作量大大提高，严重影响整体的开发效率。
那么怎么办呢？碰巧的是，我们的某位成员在GitHub上不经意间发现了一个[Kotlin编译器插件](https://github.com/ForteScarlet/kotlin-suspend-transform-compiler-plugin)，
它可以满足我们当下的需求。

我们目前正在尝试与此编译器插件进行整合，如果能够达成解决问题的目的，便会正式投入使用并发布下一个版本来观察其具体表现效果。

## ⛰ 黄山
这周，我们的团队成员一同前往了黄山，感受了一下黄山的绝美山景。

![](h/1.jpg)

![](h/2.jpg)

![](h/3.jpg)

![](h/4.jpg)

![](h/5.jpg)

![](h/6.jpg)

![](h/7.jpg)

![](h/8.jpg)

![](h/9.jpg)

![](h/10.jpg)

![](h/11.jpg)

![](h/12.jpg)

![](h/13.jpg)

![](h/14.jpg)

![](h/15.jpg)


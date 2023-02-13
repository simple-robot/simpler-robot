---
authors: [forliy, forte]
title: 语义化版本?
tags: [杂谈]
---

simbot3的版本语义，依旧不尽人意。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import Label from '@site/src/components/Label'

:::tip 阅前参考

对于 **语义化版本** 的概念，你可以参考 [《语义化版本 2.0.0》](https://semver.org/lang/zh-CN/)。

:::

在 [v3.0.0-beta-M3](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M3) 版本发布时，
我们承诺过后续的beta版本不会再出现大面积的不兼容更新。而在此版本发布之后，我们又对simbot3的语义化版本产生了自我质疑。

实际上，从simbot最初的版本直至今日，都没有遵循标准的**语义化版本**规则。不论是simbot、simbot2还是目前正在筹备的simbot3，
它们的版本语义都十分的混乱。

目前，simbot2的坐标大概是这个样子的：

<Tabs groupId="use-dependency">
<TabItem value="Maven">

```xml
<dependency>
  <groupId>love.forte.simple-robot</groupId>
  <artifactId>xxx</artifactId>
  <version>2.x.x</version>
</dependency>
```

</TabItem>

<TabItem value="Gradle Kotlin DSL">

```kotlin
implementation("love.forte.simple-robot:xxx:2.x.x")
```

</TabItem>

<TabItem value="Gradle Groovy">

```groovy
implementation 'love.forte.simple-robot:xxx:2.x.x'
```

</TabItem>
</Tabs>

而目前的simbot3的坐标大概是这个样子的：

<Tabs groupId="use-dependency">
<TabItem value="Maven">

```xml
<dependency>
  <groupId>love.forte.simbot</groupId>
  <artifactId>simbot-xxx</artifactId>
  <version>3.x.x</version>
</dependency>
```

</TabItem>

<TabItem value="Gradle Kotlin DSL">

```kotlin
implementation("love.forte.simbot:simbot-xxx:3.x.x")
```

</TabItem>

<TabItem value="Gradle Groovy">

```groovy
implementation 'love.forte.simbot:simbot-xxx:3.x.x'
```

</TabItem>
</Tabs>

从本质上来讲，simbot2和simbot3实际上属于两个完全不同的框架 ———— 甚至于只是将**主版本号**从 `2` 提升为 `3` 都难以描述二者之间的差异。
因此，我们认为，实际上对于simbot3使用如下形式的定义可能会更加合适：

<Tabs groupId="use-dependency">
<TabItem value="Maven">

```xml
<dependency>
  <groupId>love.forte.simbot3</groupId>
  <artifactId>simbot3-xxx</artifactId>
  <version>0.x.x</version>
</dependency>
```

</TabItem>

<TabItem value="Gradle Kotlin DSL">

```kotlin
implementation("love.forte.simbot3:simbot3-xxx:0.x.x")
```

</TabItem>

<TabItem value="Gradle Groovy">

```groovy
implementation 'love.forte.simbot3:simbot3-xxx:0.x.x'
```

</TabItem>
</Tabs> 

这样一来，我们就可以通过 `love.forte.simbot3` 的 `groupId` 来区分于新旧框架，也可以借助一个从头开始的版本号来更准确的描述版本语义。
但是很可惜的是，这种情况被提出的太晚，以至于simbot3将无法遵循这种方式了。

使用这种方式，不论对于核心库还是对于组件，都是一个充满包容性的新开始。只不过这样的话可能需要重新定义整体的包结构，
与 `love.forte.simbot3` 的 `groupId` 一致。所以这种方式，就让我们留给未来可期的 `simbot4` 吧。

<details>
<summary>未来...?</summary>

:::tip 可期

美好的展望总是一切的开端。

:::

<Tabs groupId="use-dependency">
<TabItem value="Maven">

```xml
<dependency>
  <groupId>love.forte.simbot4</groupId>
  <artifactId>simbot4-xxx</artifactId>
  <version>0.x.x</version>
</dependency>
```

</TabItem>

<TabItem value="Gradle Kotlin DSL">

```kotlin
implementation("love.forte.simbot4:simbot4-xxx:0.x.x")
```

</TabItem>

<TabItem value="Gradle Groovy">

```groovy
implementation 'love.forte.simbot4:simbot4-xxx:0.x.x'
```

</TabItem>
</Tabs> 

</details>




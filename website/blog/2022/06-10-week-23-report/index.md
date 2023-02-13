---
authors: forliy
title: 2022年第23周周报
tags: [2022周报,周报]
---


2022年第23周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## 🚀 版本更新/计划
本周中，[核心库](https://github.com/simple-robot/simpler-robot) 发布了
[v3.0.0.preview.13.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.13.0) ，
主要用于更新优化在 [v3.0.0.preview.12.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.12.0) 
中的 `Items` API。

而在后续计划的 `v3.0.0.preview.14.0` 中，将会有如下的变更计划：

### 🎓 Bot实现变更
在 [#280](https://github.com/simple-robot/simpler-robot/pull/280) 中，`Bot` 的结构被进行了一次调整，
将原来一部分的api拆分为了 _社交关系容器_ 接口类型并由 `Bot` 进行默认实现。

而在之后的 `v3.0.0.preview.14.0` 中，将不再为 `Bot` 实现 `FriendsContainer`，取而代之的则是 `ContactsContainer` 的实现。

也就是说，此版本之后，如下api将不再默认存在：

:::info

`FriendsContainer` 仍然可能由组件中的 `Bot` 实现。只不过不再被 `Bot` 接口**默认实现**。

:::

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
// error-start
- val friends: Items<Friend> = bot.friends
- val friend: Friend? = bot.friend(id)
// error-end
```

</TabItem>
<TabItem value="Java">

```java
// error-start
- Items<Friend> friends = bot.getFriedns()
- Friend friend = bot.getFriend(id)
// error-end
```

</TabItem>
</Tabs>

并在 `Bot` 中增加了如下的默认api实现：

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
// success-start
+ val contacts: Items<Contact> = bot.contacts
+ val Contact: Contact? = bot.contact(id)
// success-end
```

</TabItem>
<TabItem value="Java">

```java
// success-start
+ Items<Contact> contacts = bot.getContacts()
+ Contact contact = bot.getContact(id)
// success-end
```

</TabItem>
</Tabs>

:::info 不兼容

毕竟调整了继承关系，这是一个 **⚠️不兼容更新**。

:::

### 🖋 重命名
对api模块下的基础类型 **`Objectives`** 进行重命名，重命名为 **`Objective`**。

:::info 不兼容

通常来讲 `Objectives` 不会经常被直接使用，不过这依旧是一个 **⚠️不兼容更新**。

:::


### ⛔️ 弃用 sendIfSupportBlocking
弃用API：`Objective.sendIfSupportBlocking`。这本身是针对于 `Java` 等使用者使用的阻塞式API，
定义于 `Objectve` 接口内。但是目前来看，此接口存在的意义已经不大，大部分场景下都能直接判断出 `SendSupport` 的存在。 


## 🫣 更多的不兼容更新？
正如最近的更新内容所表现出来的一样，从 `v3.0.0.preview.10.x` 重构以来，后续的几个版本更新都存在了大量的 **不兼容更新**。
这实际上会极大的影响使用体验，那么这种更新还会有很多吗？还要持续多久？

以目前团队开发情况来看，也许不会再持续很久了。这些大量的不兼容更新都是为了尽可能减少这种问题发生在beta甚至正式版中。
预览版本（`preview`）已经迭代近半年的时间了，我们对于稳定api并进入beta阶段这件事上也是非常的急迫。毕竟社区参与度~~几乎~~为0，
这对我们这种低产能的团队来讲很是困难。

但是目前发现的大部分问题都已经被重构或修正，其他部分内容我们也在斟酌当中。也许距离api的稳定也快了吧。


## 📖 文档更新
一如既往的，文档仍在缓慢的逐步更新当中。

## 🤔 团队成员的未来问题
> *内容已被隐藏。*

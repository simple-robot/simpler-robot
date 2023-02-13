---
authors: forliy
title: 2022年第24周周报
tags: [2022周报,周报]
---


2022年第24周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## 🚀 版本更新/计划
本周中，[核心库](https://github.com/simple-robot/simpler-robot) 发布了
[v3.0.0.preview.14.0][v3.0.0.preview.14.0] ，
并预计很快会再发布 **`v3.0.0.preview.15.0`**。

不出意外的话，**`v3.0.0.preview.15.0`**将会是倒数的 `preview` 版本了。

## 🚩 v3.0.0.preview.14.0
这次的更新也是一如既往的 **⚠️不兼容更新**。在这次更新中，有一些值得注意的内容。

### 👉 [#319](https://github.com/simple-robot/simpler-robot/pull/319) 移除 `Bot.friend(...)`
移除了 `Bot` 中默认实现的 `Bot.friends` 和 `Bot.friend(...)` api, 取而代之的是 `Bot.contacts()` 和 `Bot.contact(...)`。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
  val bot: Bot = ...
  
// error-start
- val friends: Item<Friend> = bot.friends
- val friend: Friend = bot.friend(...)
// error-end
// success-start
+ val contacts: Item<Contact> = bot.contacts
+ val contact: Contact = bot.contact(...)
// success-end
```

</TabItem>
<TabItem value="Java">

```java
  Bot bot = ...;
  
// error-start
- Item<Friend> friends = bot.getFriends();
- Friend friend = bot.getFriend(...);
// error-end
// success-start
+ Item<Contact> contacts = bot.getContacts();
+ Contact contact = bot.getContact(...);
// success-end
```

</TabItem>
</Tabs>

当然，这并不意味着 `friends` api就此消失了。与**"好友"**相关的api存在于容器接口 `FriendsContainer` 中，
并可以由组件进行额外实现。


<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val bot: Bot = ...
if (bot is FriendsContainer) {
    val friends: Item<Friend> = bot.friends
    val friend: Friend = bot.friend(...)
}
```

</TabItem>
<TabItem value="Java">

```java
Bot bot = ...;
if (bot instanceof FriendsContainer) {
   FriendsContainer container = (FriendsContainer) bot;
   Item<Friend> friends = container.getFriends();
   Friend friend = container.getFriend(...);  
}
```

</TabItem>
</Tabs>

### 👉 [#320](https://github.com/simple-robot/simpler-robot/pull/320) `Objectives` 重命名为 `Objective`
字面意思。

<hr/>

更多变更内容可以前往 [v3.0.0.preview.14.0 release][v3.0.0.preview.14.0] 查看。

## 🎏 v3.0.0.preview.15.0
在 **`v3.0.0.preview.15.0`** 中，也有一些需要特别注意的更新内容。

### ⛔️ 移除 `UserStatus`
在 [v3.0.0.preview.14.0][v3.0.0.preview.14.0] 中的 [#328](https://github.com/simple-robot/simpler-robot/pull/328)，
我们将 `UserStatus` 标记过时并准备删除。而在**`v3.0.0.preview.15.0`** 中，我们将会完成删除工作。

### ⛔️ 移除 `@Filter.or` 和 `@Filter.and`
最终，`@Filter` 注解中的 `or` 和 `and` 最终还是无法打破规则。它们破坏了注解的约定，而Kotlin将会在1.9版本将这种行为标记为错误。
因此，我们决定删除这两个参数 ———— 实际上，这两个参数也的确没有非常有用的应用场景。
相关原因参考：
- [**Spring Framework#28012#issuecomment-1154964509**](https://github.com/spring-projects/spring-framework/issues/28012#issuecomment-1154964509)
- [**KT-47932**](https://youtrack.jetbrains.com/issue/KT-47932)


[v3.0.0.preview.14.0]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.14.0


更多内容还请关注后续的详细更新日志。

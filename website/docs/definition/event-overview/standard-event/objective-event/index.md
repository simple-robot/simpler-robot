---
title: 对象事件
tags: [标准事件]
---

对象事件，一些与 `Objectives` 相关的事件类型，是比较基础的事件类型。

通常情况下，你没有必要使用这些事件，它们更多的作用是为其他事件提供用于获取 `Objectives` 的相关api的。

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## ObjectiveEvent
> <Label>无</Label>

是所有对象事件的基础父类型，用于提供给其他相关的对象事件实现。

`ObjectiveEvent` 未实现伴生的 `Event.Key`，因此此事件类型 **不应** 被用于监听。

## UserEvent
> <Label>api.user</Label>
> <a href='#objectiveevent'><Label type='success'>ObjectiveEvent</Label></a>
> <a href='../../../container-overview/#userinfocontainer'><Label type='success'>UserInfoContainer</Label></a>

代表与 **用户** 相关的事件。

继承 `UserInfoContainer` 并提供属性 `user` 的获取。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val user = event.user()
```

kotlin中，`UserEvent` 提供两个针对 `user` 属性的 `in/use` 扩展函数：
```kotlin
event.inUser { // this: User
  // ...
}

event.useUser { user: User -> 
  // ...
}
```

</TabItem>
<TabItem value="Java">

```java
User user = event.getUser();
```

</TabItem>
</Tabs>

## MemberEvent
> <Label>api.member</Label>
> <a href='#userevent'><Label type='success'>UserEvent</Label></a> 
> <a href='../../../container-overview/#memberinfocontainer'><Label type='success'>MemberInfoContainer</Label></a>

代表与 **成员** 有关的事件。

继承 `MemberInfoContainer` 并提供属性 `member` 的获取。通常情况下，`user` 的值与 `member` 的值一致。


<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val member: Member = event.member()
```

kotlin中，`MemberEvent` 提供两个针对 `member` 属性的 `in/use` 扩展函数：

```kotlin
event.inMember { // this: Member
  // ...
}

event.useUser { member: Member -> 
  // ...
}
```

</TabItem>
<TabItem value="Java">

```java
Member member = event.getMember();
```

</TabItem>
</Tabs>

## FriendEvent
> <Label>api.friend</Label>
> <a href='#userevent'><Label type='success'>UserEvent</Label></a> 
> <a href='../../../container-overview/#friendinfocontainer'><Label type='success'>FriendInfoContainer</Label></a> 

代表与 **好友** 有关的事件。

继承 `FriendInfoContainer` 并提供属性 `friend` 的获取。通常情况下，`user` 的值与 `friend` 的值一致。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val friend: Friend = event.friend()
```

kotlin中，`FriendEvent` 提供两个针对 `friend` 属性的 `in/use` 扩展函数：

```kotlin
event.inFriend { // this: Friend
  // ...
}

event.useFriend { friend: Friend -> 
  // ...
}
```

</TabItem>
<TabItem value="Java">

```java
Friend friend = event.getFriend();
```

</TabItem>
</Tabs>

## OrganizationEvent
> <Label>api.organization</Label>
> <a href='#objectiveevent'><Label type='success'>ObjectiveEvent</Label></a>

一个与 **组织** 相关的事件。

提供属性 `organization` 的获取。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val organization: Organization = event.organization()
```

kotlin中，`OrganizationEvent` 提供两个针对 `organization` 属性的 `in/use` 扩展函数：

```kotlin
event.inOrganization { // this: Organization
  // ...
}

event.useOrganization { organization: Organization -> 
  // ...
}
```

</TabItem>
<TabItem value="Java">

```java
Organization organization = event.getOrganization();
```

</TabItem>
</Tabs>

## GroupEvent
> <Label>api.group</Label>
> <a href='#organizationevent'><Label type='success'>OrganizationEvent</Label></a>
> <a href='../../../container-overview/#groupinfocontainer'><Label type='success'>GroupInfoContainer</Label></a>

一个与 **群** 相关的事件。继承 `GroupInfoContainer` 并提供属性 `group` 的获取。通常情况下 `group` 的值与 `organization` 的值一致。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val group: Group = event.group()
```

kotlin中，`GroupEvent` 提供两个针对 `group` 属性的 `in/use` 扩展函数：

```kotlin
event.inGroup { // this: Group
  // ...
}

event.useGroup { group: Group -> 
  // ...
}
```

</TabItem>
<TabItem value="Java">

```java
Group group = event.getGroup();
```

</TabItem>
</Tabs>

## GuildEvent
> <Label>api.guild</Label>
> <a href='#organizationevent'><Label type='success'>OrganizationEvent</Label></a>

一个与 **频道服务器** 相关的事件。提供属性 `guild` 的获取。通常情况下 `guild` 的值与 `organization` 的值一致。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val guild: Guild = event.guild()
```

kotlin中，`GuildEvent` 提供两个针对 `guild` 属性的 `in/use` 扩展函数：

```kotlin
event.inGuild { // this: Guild
  // ...
}

event.useGuild { guild: Guild -> 
  // ...
}
```

</TabItem>
<TabItem value="Java">

```java
Guild guild = event.getGuild();
```

</TabItem>
</Tabs>

## ChannelEvent
> <Label>api.channel</Label>
> <a href='#organizationevent'><Label type='success'>OrganizationEvent</Label></a>

一个与 **子频道** 相关的事件。提供属性 `channel` 的获取。通常情况下 `channel` 的值与 `organization` 的值一致。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val channel: Channel = event.channel()
```

kotlin中，`ChannelEvent` 提供两个针对 `channel` 属性的 `in/use` 扩展函数：

```kotlin
event.inChannel { // this: Channel
  // ...
}

event.useChannel { channel: Channel -> 
  // ...
}
```

</TabItem>
<TabItem value="Java">

```java
Channel channel = event.getChannel();
```

</TabItem>
</Tabs>


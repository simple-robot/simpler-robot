---
title: 请求事件
tags: [标准事件]
---

与请求相关的系列事件。

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## RequestEvent
> <Label>api.request</Label>
> <a href='../../../container-overview/#userinfocontainer'><Label type='success'>UserInfoContainer</Label></a>

**请求事件** 的基础父类接口。

| 属性          | 类型                  | 描述                                                                                                                                     |
|-------------|---------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `message`   | `String?`           | 一个申请事件可能会存在附加的**文本消息** 。                                                                                                               |
| `requester` | `UserInfo`          | 此申请事件的**申请人** 。 对于一个申请者不能保证可以作为完全的 `User` 使用，因此类型仅保留为 `UserInfo`，即仅提供此用户的基础信息获取能力。在一些申请人为 `Bot` 自身的情况时（比如bot被邀请）此属性值可能与 `bot` 属性相同或类似。 |
| `user`      | 同上                  | 同上                                                                                                                                     |
| `type`      | `RequestEvent.Type` | 申请类型                                                                                                                                   |

| API        | 返回值                 | 描述          |
|------------|---------------------|-------------|
| `accept()` | `Boolean`           | 同意/接受 此请求 。 |
| `reject()` | `Boolean`           | 拒绝此请求 。     |

:::tip

在api中，所有的api在Java中的表现都会在名称后追加 `"Blocking"` 后缀。例如 `accept()` 在Java中表现为 `acceptBlocking()`。

:::

### RequestEvent.Type
枚举类型，代表当前请求事件的类型。

| 元素            | 描述            |
|---------------|---------------|
| `APPLICATION` | 主动的申请。        |
| `INVITATION`  | 被动的申请（例如被邀请）。 |


<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val message: String? = event.message
val requester: UserInfo = event.requester()
val user: UserInfo = event.user()
val type: RequestEvent.Type = event.type
//
val isAccpeted: Boolean = event.accept()
val isRejected: Boolean = event.reject()
```

</TabItem>
<TabItem value="Java">

```java
String message = event.getMessage();
UserInfo requester = event.getRequester();
UserInfo user = event.getUser();
RequestEvent.Type type = event.getType();
//
boolean isAccpeted = event.acceptBlocking();
boolean isRejected = event.rejectBlocking();
```

</TabItem>
</Tabs>

## JoinRequestEvent
> <Label>api.join_request</Label>
> <a href='#requestevent'><Label type='success'>RequestEvent</Label></a>

`RequestEvent` 的子类型，泛指一个可以表示为 **加入/添加申请** 的请求事件。常见于入群申请、好友添加申请等场景。

| 属性        | 类型          | 描述                                                            |
|-----------|-------------|---------------------------------------------------------------|
| `inviter` | `UserInfo?` | 本次添加申请人的**邀请人**。只有在组件**支持识别**邀请人并且**实际存在**邀请人的时候才会存在，否则为null。 |

## GuildRequestEvent
> <Label>api.guild_request</Label>
> <a href='#requestevent'><Label type='success'>RequestEvent</Label></a>
> <a href='../../../container-overview/#guildinfocontainer'><Label type='success'>GuildInfoContainer</Label></a>

与 **频道服务器** 相关的请求事件。

## GuildJoinRequestEvent
> <Label>api.guild_join_request</Label>
> <a href='#joinrequestevent'><Label type='success'>JoinRequestEvent</Label></a>
> <a href='#guildrequestevent'><Label type='success'>GuildRequestEvent</Label></a>

与 **加入频道服务器** 相关的请求事件。

## GroupRequestEvent
> <Label>api.group_request</Label>
> <a href='#requestevent'><Label type='success'>RequestEvent</Label></a>
> <a href='../../../container-overview/#groupinfocontainer'><Label type='success'>GroupInfoContainer</Label></a>

与 **群** 相关的请求事件。

## GroupJoinRequestEvent
> <Label>api.group_join_request</Label>
> <a href='#grouprequestevent'><Label type='success'>GroupRequestEvent</Label></a>
> <a href='#joinrequestevent'><Label type='success'>JoinRequestEvent</Label></a>

与 **加入群** 相关的请求事件。

## ChannelRequestEvent
> <Label>api.channel_request</Label>
> <a href='#requestevent'><Label type='success'>RequestEvent</Label></a>
> <a href='../../../container-overview/#channelinfocontainer'><Label type='success'>ChannelInfoContainer</Label></a>

与 **子频道** 相关的请求事件。

## UserRequestEvent
> <Label>api.user_request</Label>
> <a href='#requestevent'><Label type='success'>RequestEvent</Label></a>
> <a href='../../../container-overview/#userinfocontainer'><Label type='success'>UserInfoContainer</Label></a>

与 **用户** 相关的请求事件。

## FriendRequestEvent
> <Label>api.friend_request</Label>
> <a href='#userrequestevent'><Label type='success'>UserRequestEvent</Label></a>
> <a href='../../../container-overview/#friendinfocontainer'><Label type='success'>FriendInfoContainer</Label></a>

与 **好友** 相关的请求事件。此事件中涉及到的 `requester` 或其他属性可能并非 `Firend` 对象。

## FriendAddRequestEvent
> <Label>api.friend_add_request</Label>
> <a href='#joinrequestevent'><Label type='success'>JoinRequestEvent</Label></a>
> <a href='#friendrequestevent'><Label type='success'>FriendRequestEvent</Label></a>

与 **好友添加** 相关的请求事件。此事件代表其他人想要**申请成为**当前bot的好友，
因此此用户此时**并非**好友。


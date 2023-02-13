---
sidebar_position: 20
title: 社交对象
---

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';


目标对象 `Objectives` 是 simbot 中的基础定义类型之一。
`Objectives` 是对与 `Bot` 相关联的对象 （例如一个 _组织_ 或一个具体的 _用户_ ）的统称，或许也可以将他们理解为相对于 Bot 的 **社交对象**。

`Objectives` 的大致定义如下：
```kotlin
public interface Objectives : BotContainer, IDContainer {
    // ...
}
```

在定义中可以看出，`Objectives` 继承了 [BotContainer](../container-overview#botcontainer) 和 [IDContainer](../container-overview#idcontainer) ，
提供了获取 [ID](ID) 和 `Bot` 的能力。


核心库提供了 `Objectives` 标准类型实现，其大致结构如下：

![](@site/static/img/uml/Objectives.png)

可以看到，`User` 和 `Organization` 是 `Objectives` 的主要分支，分别代表了 **用户** 与 **组织** 。

## Organization

**组织**，是以人为中心的实体，常见为一个群聊、一个频道、一个服务器等。有关**组织**的简要介绍，参考 [**组织**](Organization.md)。

可以在上面示例图中看出，**组织** 的主要实体类型有 `Guild`、`Group` 和 `Channel`。

`Organization` 额外实现 [`OrganizationInfo`](Info#organizationinfo) 、`MuteSupport` 、`Structured` ，其具体结构如下所示：

![](@site/static/img/uml/Organization.png)

基于上述结构可以得知，**组织** 是结构化的，并且能够获取到 **组织信息** ，同时_期望中_支持 `mute` API。

组织中所提供的属性与API大致如下 ：

| 属性/API                              | 类型/返回值                                | 描述                                            |
|-------------------------------------|---------------------------------------|-----------------------------------------------|
| `bot`                               | [MemberBot](#memberbot)               | 组织所属bot。此为此bot在当前组织中所扮演的对象。                   |
| `id`                                | [ID](../ID)                           | 组织的唯一标识。                                      |
| `name`                              | `String`                              | 参考 [OrganizationInfo](Info#organizationinfo)。 |
| `icon`                              | `String`                              | 参考 [OrganizationInfo](Info#organizationinfo)。 |
| `description`                       | `String`                              | 参考 [OrganizationInfo](Info#organizationinfo)。 |
| `createTime`                        | `Timestamp`                           | 参考 [OrganizationInfo](Info#organizationinfo)。 |
| `ownerId`                           | `ID`                                  | 参考 [OrganizationInfo](Info#organizationinfo)。 |
| `maximumMember`                     | `Int`                                 | 参考 [OrganizationInfo](Info#organizationinfo)。 |
| `currentMember`                     | `Int`                                 | 参考 [OrganizationInfo](Info#organizationinfo)。 |
| `owner` <Label>suspend</Label>      | [Member](#member)                     | 组织拥有者作为成员的实例。                                 |
| `previous` <Label>suspend</Label>   | `? extends Organization?`             | 尝试获取当前组织的上级组织。                                |
| `children`                          | `Items<Organization>`                 | 尝试获取当前组织的下级组织列表。                              |
| `roles`                             | `Items<Role>`                         | 获取组织内的角色列表。                                   |
| `members`                           | <code>Items<[Member](#member)></code> | 获取当前组织内的成员列表。                                 |
| `member(ID)` <Label>suspend</Label> | <code>[Member](#member)?</code>       | 获取组织内指定ID的成员。                                 |



### Guild

**Guild** 即 **频道服务器**，常见于一些支持频道相关的组件，例如 _**开黑啦**_ 或者 _**QQ频道**_ 。
`Guild` 所代表的为频道结构中的 _**频道服务器**_，一个频道服务器中通常包含了多名成员和多个子频道（ [`Channel`](#channel) ）。

于 [`Organization`](#organization) 相比，`Guild` 中的 `bot` 为 `GuildMemberBot` 类型；
所有涉及到的 `Member` 类型均为 `GuildMember` 类型；
`children` 得到的下级组织均为 `Channel` 类型。


| 属性/API                              | 类型/返回值                                          | 描述                                |
|-------------------------------------|-------------------------------------------------|-----------------------------------|
| `bot`                               | [`MemberBot`](#memberbot)                       | 频道服务器所属bot。此为此bot在当前频道服务器中所扮演的对象。 |
| `owner` <Label>suspend</Label>      | [`GuildMember`](#guildmember)                   | 频道服务器拥有者作为成员的实例。                  |
| `children`                          | <code>Items<[Channel](#channel)></code>         | 获取当前频道服务器的子频道。                    |
| `members`                           | <code>Items<[GuildMember](#guildmember)></code> | 获取当前频道服务器内的成员列表。                  |
| `member(ID)` <Label>suspend</Label> | <code>[GuildMember](#guildmember)?</code>       | 获取频道服务器内指定ID的成员。                  |



### ChatRoom

**ChatRoom** 代表 **聊天室**，用于表示一个可能发生交流的 [Organization](#organization) 。
由 [Group](#group) 和 [Channel](#channel) 继承实现。

`ChatRoom` 实现了 `SendSupport`，因此在预期中，聊天室是应当支持进行 **_发送消息_** 的。

其结构如下：

![](@site/static/img/uml/ChatRoom.png)


### Channel
**Channel** 代表为 [Guild](#guild) 下的 **子频道** 。**Channel** 是 [ChatRoom](#chatroom) 的实现之一，
因此其被期望为一个能够发送消息的组织目标。


| 属性/API                              | 类型/返回值                                          | 描述                                  |
|-------------------------------------|-------------------------------------------------|-------------------------------------|
| `id`                                | [ID](../ID)                                     | 唯一标识。                               |
| `bot`                               | [GuildMemberBot](#guildmemberbot)               | 当前子频道所属bot在当前子频道中所扮演的成员。            |
| `guildId`                           | [ID](../ID)                                     | 参考 [ChannelInfo](info#channelinfo)  |
| `name`                              | `String`                                        | 参考 [ChannelInfo](info#channelinfo)  |
| `icon`                              | `String`                                        | 参考 [ChannelInfo](info#channelinfo)  |
| `description`                       | `String`                                        | 参考 [ChannelInfo](info#channelinfo)  |
| `createTime`                        | `Timestamp`                                     | 参考 [ChannelInfo](info#channelinfo)  |
| `ownerId`                           | [ID](../ID)                                     | 参考 [ChannelInfo](info#channelinfo)  |
| `maximumMember`                     | `Int`                                           | 参考 [ChannelInfo](info#channelinfo)  |
| `currentMember`                     | `Int`                                           | 参考 [ChannelInfo](info#channelinfo)  |
| `owner` <Label>suspend</Label>      | [GuildMember](#guildmember)                     | 参考 [Organization](#organization)    |
| `guild` <Label>suspend</Label>      | [Guild](#guild)                                 | 当前子频道所属的频道服务器。                      |
| `members`                           | <code>Items<[GuildMember](#guildmember)></code> | 当前子频道下的成员列表。大概率与Guild得到的结果一致。       |
| `member(ID)` <Label>suspend</Label> | [GuildMember](#guildmember)                     | 根据ID获取当前子频道下的指定成员。大概率与Guild得到的结果一致。 |



### Group
 

## User


### Bot


### Contact


### Friend


### Member


### GuildMember


### GroupMember



### Stranger



### MemberBot


### GuildMemberBot


### GroupMemberBot

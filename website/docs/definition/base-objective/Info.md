---
sidebar_position: 10
title: 信息类型
---

simbot提供了一组标准的 **基础信息** 类型，用来作为后续章节中会出现的 [**Objectives**](Objectives.md) 的信息载体类型。

对于大多数 **基础信息** 来讲，他们的命名方式都类似于 `XxxInfo`，即以 `Info` 作为结尾，例如 `UserInfo`。
**基础信息** 会提供一些 _预期内_ 能够提供的属性，并约定当属性不支持时的默认值。

对于基础信息中提供的属性，大多数都是一些普通的属性而并非可挂起属性。

## OrganizationInfo
代表 **组织** 的基本信息。

| 属性              | 类型          | 描述                                                   |
|-----------------|-------------|------------------------------------------------------|
| `id`            | [ID](../ID) | 唯一标识。                                                |
| `name`          | `String`    | 名称。                                                  |
| `icon`          | `String`    | 图标/组织头像。如果不支持则可能为空。                                  |
| `description`   | `String`    | 组织的描述。如果不支持则可能为空。                                    |
| `createTime`    | `Timestamp` | 组织创建时间。如果不支持则可能为 `Timestamp.NotSupport`。             |
| `ownerId`       | `ID`        | 组织拥有者/创建者的ID。                                        |
| `maximumMember` | `Int`       | 当前组织内成员最大承载量。如果不支持则为 `-1`。                           |
| `currentMember` | `Int`       | 组织内当前成员数量。如果不能 **直接** 支持则可能会尝试查询所有用户列表；如果不支持则为 `-1`。 |

## GuildInfo

代表 **频道服务器信息**。继承自 [OrganizationInfo](#organizationinfo) 。

## GroupInfo

代表 **群信息**。继承自 [OrganizationInfo](#organizationinfo) 。

## ChannelInfo

代表 **子频道信息**。继承自 [OrganizationInfo](#organizationinfo) 。

| 属性        | 类型          | 描述              |
|-----------|-------------|-----------------|
| `guildId` | [ID](../ID) | 此子频道对应的频道服务器ID。 |


## UserInfo

代表 **用户** 的基本信息。

| 属性         | 类型          | 描述              |
|------------|-------------|-----------------|
| `id`       | [ID](../ID) | 唯一标识。           |
| `username` | `String`    | 用户名。            |
| `avatar`   | `String`    | 头像链接。如不支持则可能为空。 |


## BotInfo

代表 **Bot** 的基本信息。继承自 [UserInfo](#userinfo) 。

## FriendInfo

代表一个bot的 **好友** 信息。继承自 [UserInfo](#userinfo) 。

| 属性         | 类型         | 描述                                   |
|------------|------------|--------------------------------------|
| `remark`   | `String?`  | bot对此好友的备注。不存在、不支持的情况下为null。         |
| `grouping` | `Grouping` | 好友的分组。不存在、不支持的情况下为 `Grouping.Empty`。 |

## MemberInfo

代表一个组织内的 **组织成员** 信息。继承自 [UserInfo](#userinfo) 。

| 属性         | 类型          | 描述                                                           |
|------------|-------------|--------------------------------------------------------------|
| `nickname` | `String`    | 成员在组织中的备注名。不存在、不支持的情况下为空。                                    |
| `joinTime` | `Timestamp` | 成员加入当前组织的时间。不存在、不支持的情况下为 `Timestamp.NotSupport`。_不支持的可能性很大。_ |




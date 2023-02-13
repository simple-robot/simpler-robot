---
sidebar_position: 20
title: 对象变更事件
tags: [标准事件]
---

对象变更事件。代表为一个 `Objectives` 发生了变化的事件。

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## MemberChangedEvent
> <Label>api.member_changed</Label>
> <a href='./#changedevent'><Label type='success'>ChangedEvent</Label></a>
> <a href='../objective-event/#organizationevent'><Label type='success'>OrganizationEvent</Label></a>
> <a href='../objective-event/#memberevent'><Label type='success'>MemberEvent</Label></a>

代表一个组织中发生的 **成员变动** 事件。通常表现为 [成员增加](#memberincreaseevent) 或 [成员减少](#memberdecreaseevent) 。

| 属性         | 类型            | 描述                                                                 |
|------------|---------------|--------------------------------------------------------------------|
| `operator` | `MemberInfo?` | 成员变动的操作者。如果本次变动行为发生的原因在于本次变动的成员自身，则此属性值等同于此成员；在不支持、不存在等情况下可能为null。 |


## MemberIncreaseEvent
> <Label>api.member_increase</Label>
> <a href='./point-changed-event/#increaseevent'><Label type='success'>IncreaseEvent</Label></a>
> <a href='#memberchangedevent'><Label type='success'>MemberChangedEvent</Label></a>

代表一个组织中增加了一个成员。

| 属性           | 类型                        | 描述      |
|--------------|---------------------------|---------|
| `after`      | `Member`                  | 增加的组织成员 |
| `actionType` | [ActionType](#actiontype) | 行为类型    |


## MemberDecreaseEvent
> <Label>api.member_decrease</Label>
> <a href='./point-changed-event/#decreaseevent'><Label type='success'>DecreaseEvent</Label></a>
> <a href='#memberchangedevent'><Label type='success'>MemberChangedEvent</Label></a>

代表一个组织中减少了一个成员。

| 属性           |     | 类型                        | 描述        |
|--------------|:----|---------------------------|-----------|
| `before`     |     | `Member`                  | 离开的组织成员   |
| `actionType` |     | [ActionType](#actiontype) | 行为类型      |


## ActionType
枚举类型，代表为一个行为的类型。

| 元素          | 描述  |
|-------------|-----|
| `PROACTIVE` | 主动的 |
| `PASSIVE`   | 被动的 |


## FriendChangedEvent
> <Label>api.friend_changed</Label>
> <a href='./#changedevent'><Label type='success'>ChangedEvent</Label></a>
> <a href='../objective-event/#friendevent'><Label type='success'>FriendEvent</Label></a>

代表一个bot的好友（数量）发生了变化。

| 属性       | 类型       | 描述         |
|----------|----------|------------|
| `source` | `Bot`    | 发生好友变化的bot |
| `friend` | `Friend` | 变化的好友对象    |




## FriendIncreaseEvent
> <Label>api.friend_increase</Label>
> <a href='./point-changed-event/#increaseevent'><Label type='success'>IncreaseEvent</Label></a>
> <a href='#friendchangedevent'><Label type='success'>FriendChangedEvent</Label></a>

代表一个bot的好友增加了。

| 属性       | 类型       | 描述         |
|----------|----------|------------|
| `source` | `Bot`    | 发生好友变化的bot |
| `friend` | `friend` | 增加的好友对象    |
| `after`  | 同上       | 同上         |



## FriendDecreaseEvent
> <Label>api.friend_decrease</Label>
> <a href='./point-changed-event/#decreaseevent'><Label type='success'>DecreaseEvent</Label></a>
> <a href='#friendchangedevent'><Label type='success'>FriendChangedEvent</Label></a>


代表一个bot的好友减少了。

| 属性       | 类型       | 描述         |
|----------|----------|------------|
| `source` | `Bot`    | 发生好友变化的bot |
| `friend` | `friend` | 减少的好友对象    |
| `before` | 同上       | 同上         |

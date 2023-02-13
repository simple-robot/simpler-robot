---
sidebar_position: 10
title: 端点变更/增减事件
tags: [标准事件]
---

端点变更事件。代表为一个在变更状态处于端点位置的事件。

端点变更事件的概念类似于发生了 **增加** 或 **减少** 的事件。

其中 **增加** 代表为端点事件中的 **起点**，而**减少** 代表为端点事件中的 **终点**。

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## StartPointEvent
> <Label>api.start_point</Label>
> <a href='./#changedevent'><Label type='success'>ChangedEvent</Label></a>

代表一个作为 **起点** 的 [变更事件](./#changedevent) 。 起点事件通常作为其他事件的父类型使用，不需要用来直接监听。

在起点中，属性 `before` 默认情况下应当始终为 `null` 。


## EndPointEvent
> <Label>api.end_point</Label>
> <a href='./#changedevent'><Label type='success'>ChangedEvent</Label></a>

代表一个作为 **终点** 的 [变更事件](./#changedevent) 。 终点事件通常作为其他事件的父类型使用，不需要用来直接监听。

在终点中，属性 `after` 默认情况下应当始终为 `null` 。


## IncreaseEvent
> <Label>api.increase</Label>
> <a href='#startpointevent'><Label type='success'>StartPointEvent</Label></a>

代表一个 **增加/增长** 事件。通常用于描述此事件的 `source` 中某事物被增加了。例如一个群内增加了一名群成员。通常作为其他事件的父类型使用，不需要用来直接监听。


## DecreaseEvent
> <Label>api.decrease</Label>
> <a href='#endpointevent'><Label type='success'>EndPointEvent</Label></a>

代表一个 **减少** 事件。通常用于描述此事件的 `source` 中某事物被减少了。例如一个群内减少了一名群成员。通常作为其他事件的父类型使用，不需要用来直接监听。

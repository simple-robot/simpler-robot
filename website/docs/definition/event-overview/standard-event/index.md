---
title: 标准事件
tags: [标准事件]
---

simbot3核心库所提供的标准事件定义。

## 文档展现
在下述章节中，你可能会看到类似于下面这种的展现形式：

import Label from '@site/src/components/Label'

<hr />

> ### FooEvent
> > <Label>api.foo</Label>
> > <Label type="success">BarEvent</Label> 
> > <Label type="success">TarEvent</Label>
> 
> 这是一段说明

<hr />

这代表文档中描述的 `FooEvent` 的 key.id 为 `api.foo` ，并且它继承/实现了 `BarEvent` 和 `TarEvent`。并且对于此事件的描述，如果没有特殊情况则不会对继承事件中的重复内容进行赘述。
当然，这也不一定是事件类型，它代表的也可能是其他任何可能被实现/继承的类型，例如 <Label type='success'>FooContainer</Label>。

所有事件全部默认实现 <Label type="success">Event</Label> 类型。
因此对于 `Event` 的实现将不会再进行标记。

:::info 出入

如果出现任何文档与实际代码表现不符的情况，优先以实际代码表现为准，
并及时通过 [**issue**](http://github.com/simple-robot-library/simbot3-website/issues/) 进行反馈。

:::

## 章节列表

import DocCardList from '@theme/DocCardList';
import {useCurrentSidebarCategory} from '@docusaurus/theme-common';

<DocCardList items={useCurrentSidebarCategory().items} />


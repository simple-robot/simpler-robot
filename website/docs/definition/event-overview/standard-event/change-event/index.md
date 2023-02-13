---
title: 变更事件
tags: [标准事件]
---

标准事件中与 **变更** 有关的系列事件类型。

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## ChangeEvent
> <Label>api.change</Label>

**变更** 事件的基础父类接口类型。代表一个事物发生了**变化**。这是一种比较 **笼统** 的变化概念，不区分 _变化前_ 或者说 _变化后_ 。

| 属性       | 类型     | 描述                                                            |
|----------|--------|---------------------------------------------------------------|
| `source` | `Any`  | 变更载体。即本次变更所发生的场所或者目标。可能为任何类型，不做约束，但不应为null。                   |
| `before` | `Any?` | 变更行为发生前变化目标的值。当不支持获取、不存在等情况下可能为null。具体情况需要参考具体事件类型的描述。        |
| `after`  | `Any?` | 变更行为发生后变化目标的值。当不支持获取、不存在、尚未发生变更等情况下可能为null。具体情况需要参考具体事件类型的描述。 |


## ChangedEvent
> <Label>api.changed</Label>
> <a href='#changeevent'><Label type='success'>ChangeEvent</Label></a>

[**ChangeEvent**](#changeevent) 的基础子类型，代表为**已经发生了变化**的变化事件。

| 属性            | 类型          | 描述                                             |
|---------------|-------------|------------------------------------------------|
| `changedTime` | `Timestamp` | 变更发生的时间。不保证为具体的服务器时间，在不支持的情况下可能为事件被构造时的本地瞬时时间。 |
| `timestamp`   | 同上          | 同上                                             |


<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val source: Any = event.source
val before: Any? = event.before
val after: Any? = event.after
```

</TabItem>
<TabItem value="Java">

```java
Object source = event.getSource();
Object before = event.getBefore();
Object after = event.getAfter();
```

</TabItem>
</Tabs>




## 其他相关事件

import DocCardList from '@theme/DocCardList';
import {useCurrentSidebarCategory} from '@docusaurus/theme-common';

<DocCardList items={useCurrentSidebarCategory().items} />


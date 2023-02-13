---
authors: forliy
title: 2022年第28周周报
tags: [2022周报,周报]
---


2022年第28周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import Label from '@site/src/components/Label'

## 🚀 版本更新

截止到本周，[核心库](https://github.com/simple-robot/simpler-robot) 发布了如下版本：

- [**v3.0.0-beta-M3**][v3bm3]

### 🚤 v3.0.0-beta-M3

如上一周周报所说，[**v3.0.0-beta-M3**][v3bm3] 存在大量不兼容更新（包路径变更所导致的），且是最后一次存在大量不兼容更新的更新。

<i><s><small>说实话做出这个承诺之后有点儿小后悔</small></s></i> 我们会尽可能遵守这个承诺，不再变更基本API，
并尽快发布 <strong>v3.0.0-beta-RC</strong> 版本。


simbot3版本从开始到现在已经进行了将近1年，我们很是迫切地希望能赶紧稳定下版本。

## 💬 版本语义化

对于当前 simbot3 的版本语义化情况和我们对此情况的想法，可以参考 [《语义化版本?》](semantic-versioning)。

## 😕 组件进展

组件的进展始终缓于核心库，毕竟核心库的稳定才能保证组件可以专注的进行推进。

目前，由我们官方所维护的组件及其基本信息如下：

| 组件名     | 仓库                                                                                            | 状态       |
|---------|-----------------------------------------------------------------------------------------------|----------|
| mirai组件 | [simple-robot/simbot-component-mirai](https://github.com/simple-robot/simbot-component-mirai) | **相对**稳定 |     
| kook组件  | [simple-robot/simbot-component-kook](https://github.com/simple-robot/simbot-component-kook)   | 不稳定      |     
| QQ频道组件  | [simple-robot/simbot-component-kook](https://github.com/simple-robot/simbot-component-kook)   | 不稳定      |

可以看到，三个组件中，**mirai组件** 相对于其他两个组件来讲更加稳定。这是因为 [mirai](https://github.com/mamoe/mirai) 框架本身已十分成熟，
而mirai组件则仅需要对mirai框架进行封装即可。

而其他组件的主要api都是根据其各自对应的官方api进行的封装，并不涉及第三方框架，因此会有更多的工作和细节性问题需要处理，
这也导致了它们存在更多隐患和待解决的问题。

总而言之，就是这样。以目前的形势来看，团队的生产力远远无法弥补当前工作量的缺口，因此我们无法保证此项目的生命力与未来。


[v3bm3]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M3

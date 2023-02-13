---
authors: forliy
title: 2022年第32周周报
tags: [2022周报,周报]
---


2022年第32周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import Label from '@site/src/components/Label'


## 🚀 版本发布

本周, [核心库][core-repo] 发布了版本 [`v3.0.0-beta-RC.2`](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-RC.2)，


此版本中主要进行了一些优化、修复相关的工作。

## 📕 文档更新

对于mirai组件，我们大篇幅的更新了有关bot配置相关的内容，也同时更新了文档。有兴趣的话，不妨去看看。

## 🎯 坐标与仓库

我们一直在考虑，是否要在正式版本发布的时候，使用更符合 `simbot3` 名义的maven坐标。

我们现在的坐标和版本是如下的样子：

```
love.forte.simbot:simbot-core:3.0.0-beta
love.forte.simbot.boot:simboot-core:3.0.0-beta
```

而我们拟定在正式版发布时，统一使用更明确的坐标和版本：

```
love.forte.simbot3:simbot3-core:1.0.0
love.forte.simbot3.boot:simbot3-boot-core:1.0.0
```

:::note 虚妄...

但这仍在考虑中，并且可能大概率**不会施行**。

:::

**_为什么会有这种考虑？_** simbot历届版本都应该这样，比如simbot2实际上应该是：

```
love.forte.simbot2:simbot2-xxx:1.x.x
```

因为每一代的simbot之间都存在天差地别的变化，而这种变化甚至不应简单的使用 `major` 版本号的更新来描述，
而是应该完全作为一个新的内容，从头开始，从1.0.0的版本号开始。

然而实际上，从simbot2开始的版本号便已经没有履行 ———— 或者说难以履行它们用于描述api迭代的职责了。

而现在当我们考虑到这件事的时候，也已经有些晚了。

:::note 讨论

我们在 `2022-07-15` 的时候就发布过一次类似的讨论博客: 《语义化版本？》

:::

<hr />

虽然坐标的变更难以施行，但是对于仓库的重整与转移则会择期进行。

我们后续会将目前的 [核心库][core-repo] 拆分为分别代表了不同**世代**的两个仓库：`simple-robot-g2` 和 `simple-robot-g3`，
并将它们转移到 [`simple-robot` 组织库](http://github.com/simple-robot) 中。

你可能注意到，我上面提到了 **"世代"** 而不是版本，并且仓库的命名也是使用的 `"g2"`、`"g3"` 而不是 `"v2"`、`"v3"`。
这是因为，simbot1、simbot2、simbot3之前，我认为它们除了继承统一意志与名称以外，并没有实际上的"迭代"，因此使用 `version` 来描述它们并不十分妥当。

那么该用什么来描述呢，我们想到了上面的词：**世代**，然后翻译一下便得到了 `Generation`，也就得到了 `"g2"`、`"g3"` 后缀。
已经过去的版本无法再变，而后续，对于 `gx` 的迭代，会根据新版本的特质来决定它会成为 `g3 v2` 还是 `g4 v1`。

但是说真的，`g3` 这种后缀看上去很不习惯，也许我们会继续思考更好的命名方式。

:::tip 意见与建议 

在这项举措尚未施行之际，如果你有更好的想法或者其他意见，非常欢迎与我们交流。

:::

[core-repo]: https://github.com/simple-robot/simpler-robot

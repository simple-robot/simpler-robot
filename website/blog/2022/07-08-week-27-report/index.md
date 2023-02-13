---
authors: forliy
title: 2022年第27周周报
tags: [2022周报,周报]
---


2022年第27周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import Label from '@site/src/components/Label'

## 🚀 版本更新

截止到本周，[核心库](https://github.com/simple-robot/simpler-robot) 发布了如下几个版本：

- [**v3.0.0-beta-M2**][v3bm2] 
- [**v3.0.0.preview.20.0**][v3p20.0]
- [v2.4.0][v2.4.0]

这几个版本更新都是中规中矩，没有什么特别值得一提的细节，如果有兴趣可以去releases中查看对应版本的更新说明。

而真正值得一提的是后续将会发布的 **`v3.0.0-beta-M3`**。

### 🚤 v3.0.0-beta-M3

[**v3.0.0-beta-M3**][v3bm3] 将会很快发布，并且从这个版本开始，将不会再有 `preview` 版本的更新，
而是开始保持API稳定直到beta版本的出现。

但是很不幸的是，**`v3.0.0-beta-M3`** 中又一次出现了较大范围的不兼容更新💔。。。
只不过这一次的不兼容更新是针对**包结构**的，对实际上的功能没有影响。

对于这次的包结构变化的细节内容，你可以参阅 [PR<sup>#378</sup>](https://github.com/simple-robot/simpler-robot/pull/378) 
或者 [《v3.0.0-beta.M3包路径变更公告》](../07-06-announcement-package-rename)。

如果不出意外，**`M3`** 的下一个版本将会是 **`RC`**。


## 📖 文档更新

文档？有啦，有稍微写写的啦。

> *欸，这句话是不是说过？*


[v2.4.0]: https://github.com/simple-robot/simpler-robot/releases/tag/v2.4.0

[v3p20.0]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.20.0

[v3bm2]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M2
[v3bm3]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M3

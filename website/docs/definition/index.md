---
title: 标准定义
---

本章节下介绍 `simbot3` 中的部分**定义介绍**，例如一些通用的定义及其解释等。

**定义介绍**主要以介绍某些常见的、基础的类型中各属性、函数等内容，来帮助你理解这些类型。

:::note 有所出入...

如果文档与实际代码表现之间存在出入，以**实际代码表现**为最终效果，
并将此情况反馈到当前文档的 [**Issues**](https://github.com/simple-robot-library/simbot3-website/issues) 处。

_这可能是代码的表现bug，也可能是文档的描述错误。_

:::

## 阅读建议

《标准定义》章节内的东西并非需要全部阅读了解，它们更倾向于"查阅"；当你遇到了一些不太明白用意的类型时，
可以来寻找此类型的描述。

:::info 最好的选择...

**注意！** 了解一个类型、属性或API的最好方式永远是直接**阅读源码或文档注释**，其次为 **API文档**，
而依赖类似于此章节的"标准定义手册"则为**下选**。

使用手册也许会在为你提供各API的使用上提供有效帮助，但是却不应用来作为定义描述的查阅手册。源码中的文档注释和自动构建的API文档永远最贴合实际情况，也会更加全面。

这一点我可能会在文档各个地方提醒很多次，希望你能明白我们的用意。

_锻炼你的源码阅读能力。_

:::

## API文档

当前网站中的文档内容**永远**也赶不上实际代码中的内容。因此我们更建议你优先前往阅读源码或根据版本发布自动生成的 [API文档](https://docs.simbot.forte.love)，
而仅以此站为**辅助**作用。


## 章节列表

import DocCardList from '@theme/DocCardList';
import {useCurrentSidebarCategory} from '@docusaurus/theme-common';

<DocCardList items={useCurrentSidebarCategory().items}/>

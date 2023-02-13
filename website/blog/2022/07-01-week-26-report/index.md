---
authors: forliy
title: 2022年第26周周报
tags: [2022周报,周报]
---


2022年第26周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

## 🚀 版本更新

从[上周周报](../06-22-week-25-report)截止到本周周报，[核心库](https://github.com/simple-robot/simpler-robot) 发布了如下几个版本：

- [**v3.0.0-beta-M1**][v3bm1]
- [**v3.0.0.preview.18.0**][v3p18.0]
- [v3.0.0.preview.17.1][v3p17.1]
- [v3.0.0.preview.17.0][v3p17.0]
- [v2.3.9][v2.3.9]

这周内的版本更新不少，值得注意的是我们发布了第一个 `beta` 版本的里程碑版本: [**v3.0.0-beta-M1**][v3bm1] 。

### 🚤 v3.0.0-beta-M1

[**v3.0.0-beta-M1**][v3bm1] 版本的代码表现与 [**v3.0.0.preview.18.0**][v3p18.0]
完全一致，它代表了在 **v3.0.0-beta** 版本发布前的第一个预发布里程碑版本。
从这个版本开始，后续更新将会尽量避免出现API的大篇幅变更、或者出现不兼容变更。

### 🎬 Beta

当预发布结束、进入到 `beta` 版本阶段后，我们会尽可能遵守[语义化的版本控制](https://semver.org/lang/zh-CN/)规则，
并尽可能保证API的兼容。

> 需要注意的是，对于组件相关的API而言，我们会更倾向于面向用户的API稳定。

`beta` 阶段会持续更新来进行问题修复与实战测试，然后会择机发布 `v3.0.0` 版本。

### 👟 组件版本发布

除非核心库发布了针对组件API的不兼容更新，组件将不会再严格跟随核心库的版本的发布而更新版本。
对于我们所提供的组件实现来讲， 组件所依赖的核心库版本与当前项目环境中的一致，而不受库依赖影响。
因此，当核心库版本间兼容的情况下，组件无需更新。

## 🪄 开黑啦 -> [Kook](https://kookapp.cn/)

平时会用开黑啦组件的朋友可能发现了一件事：开黑啦更名了。
现在，原**开黑啦**已经更名为 [**Kook**](https://kookapp.cn/)。

说实在的，原来的名字对于组件开发来讲实际上并不友好，如果你有看过代码，
里面有大量以 **`Kaiheila`** 或者 **`Khl`** 开头命名的类型，非常不美观。

而现在，官方变更名称为 **Kook**。具体原因他们肯定有自己的考量，
但是对于我们来说这是重构组件命名的好机会。

因此，开黑啦组件将会重命名为 **Kook组件**，并且会重新命名包括仓库、依赖坐标、
类命名在内的所有内容为新的 **Kook**。具体内容和进度你可以追踪
[**问题#45**](https://github.com/simple-robot/simbot-component-kook/issues/45) 。

## 仓库结构/仓库迁移

近期，我们会优化各代码仓库的代码结构（比如简化、美化gradle脚本），并进而完善各仓库的README或贡献帮助等。

除了代码内的结构，我们还打算重命名[**组织库**](https://github.com/simple-robot)，
由 **`simple-robot`** 更名为 **`simple-robot-projects`**，
并考虑将 [**核心库**](https://github.com/simple-robot/simpler-robot)
由 **`ForteScarlet/simpler-robot`** 迁移至 **`simple-robot-projects/simpler-robot`**。

## 📖 文档更新

文档？有啦，有稍微写写的啦。


[v2.3.9]: https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.9

[v3p17.0]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.17.0

[v3p17.1]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.17.1

[v3p18.0]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.18.0

[v3bm1]: https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M1

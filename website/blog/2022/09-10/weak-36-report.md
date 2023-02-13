---
authors: forliy
title: 2022年第36周周报
tags: [2022周报,周报]
---


2022年第36周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import Label from '@site/src/components/Label'

## 🐱🎉 猫猫码 第二代！

细心的小伙伴们可能已经发现了，就在前不久，我们公开了 [CatCode2](https://github.com/ForteScarlet/CatCode2)
—— 也就是第二代猫猫码库，并发布了几个早期版本。

:::tip 🐱😸😹😻😼😽🙀😿😾😺

即刻前往 👉[**猫猫码2**](https://github.com/ForteScarlet/CatCode2) 查看、支持与贡献！

:::

### 🐱🔧 多平台

猫猫码第二代与第一代最大的不同在于第二代基于 [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
支持多平台（JVM、JS、Native）。除了 [Maven](https://repo1.maven.org/maven2/love/forte/catcode2) 仓库的发布，
也会同步发布到 [NPM包](https://www.npmjs.com/package/@catcode2/core)，并且核心模块提供多个平台的 C API (例如 `linux x64`、`mingw x64`、`macos x64` 等 ) 
。
你可以在更多平台上使用猫猫码了喵。

### 😸🧵 序列化

猫猫码第二代提供了针对 [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) 的序列化实现，
当你使用 `kotlinx-serialization` 时，可以为猫猫码提供实体模板来进行更安全明确的序列化使用喵。

### 😽⚙️ 兼容性

猫猫码第二代并没有变更猫猫码的基本规则与结构，因此理论上它支持解析旧版本的猫猫码。第二代仅仅只是解析库的迭代，
而没有迭代其基本规则喵。

## 🚀 核心库更新

本周 [核心库](https://github.com/simple-robot/simpler-robot) 发布了一个新版本：
[v3.0.0-beta](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta)

鉴于近期并未有什么严重问题的反馈，因此发布了这个核心库的第一个 `BETA` 系列版本。`BETA` 系列版本发布后，
后续版本的更新就不再会想之前的 `preview` 阶段那样的频繁变动了，而是会更多的考虑兼容性。

需要注意的是，在正式版本发布之前，`BETA` 阶段会择机删除**所有的过时API**。这些将会被删除的过时API都是在 
`preview` 阶段遗留下来的内容。

## 🚚 核心库迁移

曾经我们提到过，我们正计划着将 [核心库](https://github.com/simple-robot/simpler-robot) 迁移到 [组织库](http://github.com/simple-robot)
中。此计划_可能_会在近期实行。

## 🏢 社区

伴随着上述的核心库迁移计划，我们在 [组织库](http://github.com/simple-robot) 中借助 github discussions
提供了一个简单的 [社区](https://github.com/orgs/simple-robot/discussions) 。
日后可能会逐步完善此社区，并时不时地更新一些咨询或F&Q来提供更友好地引导，如果有必要的话。

在这之前，[核心库讨论区](https://github.com/simple-robot/simpler-robot/discussions) 一直担任着社区的职责。





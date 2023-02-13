---
sidebar_position: 30
title: 组织
---

一个组织 `Organization` , 一个 **组织结构**。

组织必须是以人为中心的实体，常见为一个群聊、一个频道、一个服务器等。

<br />
<br />

首先，先来参考如下平台间的差异：

:::tip

下述内容仅用于**参考**，并不代表simbot将会提供针对其平台的组件实现。

:::



import NO from '@site/src/components/NO';
import YES from '@site/src/components/YES';


| 平台       | 应用功能  | 能推送消息   | 能接收消息   | 可能存在子集    | 可能存在上级    | 存在分组      | 结构化       | 多层级     | 成员权限管理         |
|----------|-------|---------|---------|-----------|-----------|-----------|-----------|---------|----------------|
| QQ群      | QQ群   | <YES /> | <YES /> | <NO />    | <NO />    | <NO />    | <NO />    | <NO />  | <YES />        |
| QQ频道     | 频道服务器 | <NO />  | <NO />  | <YES />   | <NO />    | <NO />    | <YES />   | <NO />  | <YES />        |
| QQ频道     | 子频道   | <YES /> | <YES /> | <NO />    | <YES />   | <YES />   | <YES />   | <NO />  | <YES />        |
| 开黑啦      | 频道服务器 | <NO />  | <NO />  | <YES />   | <NO />    | <NO />    | <YES />   | <NO />  | <YES />        |
| 开黑啦      | 子频道   | 仅文字频道   | 仅文字频道   | <NO />    | <YES />   | <YES />   | <YES />   | <NO />  | <YES />        |
| YY       | YY    | 仅文字频道   | 仅文字频道   | <YES />   | <YES />   | <YES />   | <YES />   | <YES /> | <YES /><YES /> |
| telegram | tg群   | <YES /> | <YES /> | <NO />    | <NO />    | <NO />    | <NO />    | <NO />  | <YES />        |
| telegram | tg频道  | 视权限     | <YES /> | <NO />    | <NO />    | <NO />    | <NO />    | <NO />  | <YES />        |
| discord  | 频道服务器 | <NO />  | <NO />  | <YES />   | <NO />    | <NO />    | <YES />   | <NO />  | <YES />        |
| discord  | 子频道   | 仅文字频道   | 仅文字频道   | <NO />    | <YES />   | <YES />   | <YES />   | <NO />  | <YES />        |
| 钉钉       | 群     | <YES /> | <YES /> | 根据所属企业结构化 | 根据所属企业结构化 | 根据所属企业结构化 | 根据所属企业结构化 | <NO />  | <YES />        |


从上述列举内容来看，目前比较常见地组织形式有：
- 单群聊天，不存在直接的层级关系，例如 `group` 。目前来看 group 中的信息可能会略多于 `guild` 。
- 多频道服务器（`guild`），存在层级关系，外层 `guild` 囊括内层多个频道 （`channel`）。
- 频道中可能存在多个允许文字交流的频道。
- 频道可能存在分组。


有关组织的其他结构性定义等信息，请参考 [对象#组织](Objectives.md#organization)

---
title: 命名概述
---

对版本、坐标等内容的命名规则的阐述。

:::info

规则版本：`v7`

更新时间：`2022/04/09`

:::

## 模块命名
基本上，主要相关仓库的坐标命名都应符合如下规则：
### 主要模块
#### 基础库相关
group：`love.forte.simbot` <br />
id：`simbot-xxx` <br />
例如：
- 基础api模块：`love.forte.simbot:simbot-api`
- 基础core模块：`love.forte.simbot:simbot-core`

#### boot模块相关
group：`love.forte.simbot.boot` <br />
id：`simboot-xxx` <br />
例如：
- boot核心模块：`love.forte.simbot.boot:simboot-core`

<small><i>仔细看喔，后面的是 <code>simboot</code> 而不是 <code>simbot</code>。</i></small>

### 组件模块
group：`love.forte.simbot.component` <br />
id：`simbot-component-xxx` <br />
例如：
- 腾讯频道组件模块：`love.forte.simbot.component:simbot-component-tencent-guild-core`


## 版本命名
版本将会使用最常见的 MAJOR.MINOR.PATCH[.STATUS][-SNAPSHOT]规则，并规定：
- `MAJOR` 、`MINOR` 、`PATCH` 为非负整数，例如 3.0.0。
- `STATUS` 为可选的状态版本，常见状态有 `preview` 、`alpha` 、`beta` 等。状态字符串后面可以在跟随子版本 `MINOR.PATCH` 。当状态版本存在的时候，通常此版本不视为release版本。例如：3.0.0.preview.2.0、3.0.0.beta.5.1。
- `SNAPSHOT` 为可选标记，标记于版本最后，代表为一个快照版本。

版本的同步更新将会以仓库为标准。由于大部分组件将会使用独立仓库进行版本控制，因此可能存在以下情况：
假如存在使用同一个版本的 `simbot-api v3.0.0` 进行实现的三个组件库 `component-A` 、`component-B` 、`component-C`，他们的版本分别为：`3.0.6.1` 、`3.0.5.beta.0.1` 、`3.0.1.1` 。

所有由simbot进行管理的组件，其 `MAJOR` 与 `MINOR` 会保持一致，均使用 `3` ，但是其他版本标签将会独立维护。
因此，当选择组件使用的时候，请注意观察它们内部的 simbot版本（通常都会在readme或者相关release中进行说明）以避免版本冲突。


## 组件模块

:::info

所有需要对接第三方平台/API的组件，均有可能需要独自进行版本控制。

:::

### 版本规则
如果组件核心的命名规则为 `x.y.z[.STATUS][-SNAPSHOT]` ，例如 `3.0.0` ，
那么对于一个组件的命名规则考虑为 `x.y.a.b[.STATUS][-SNAPSHOT]` ，其中，`x.y` 保持与此组件内部对应的simbot版本号的 `MAJOR` 、`MINOR` 保持一致，
而后面的 a.b则参考对接API的版本号来进行决定。


其中，`a.b` 中，`a` 代表可能存在的不兼容更新或较主要的新API增量更新，而b代表基本兼容的修复、小型增量更新。*或许可以称之为 `patch` 号* 。
在组件中，如果内部的simbot核心库的版本更新，例如从 `v3.0.0` 更新到 `v3.1.0` ，那么对应的组件版本会对应更新 `x.y` 的版本号，但是不会影响 `a.b` 的版本号。
（例如从 `v3.0.2.3` 到 `v3.1.2.3` ）。

也就是说，大部分情况下，组件版本号中的 `x.y` 和 `a.b` 是独立更新的，其中 `x.y` 某种意义上可以合并视作组件的 `MAJOR` 。

不一定所有的组件都需要第三方或独立更新的API，例，假如存在一个用于远程通讯的 `component-server/component-client`，
其为simbot核心本身所提供的组件，则它将会伴随着核心一起更新，不会存在独立版本号。

### 后缀
除了基本的版本号，对于后缀 .STATUS 和 -SNAPSHOT，组件的后缀应该与其内部所对应的simbot核心依赖的后缀一致。

### 例外
不一定所有的组件都需要第三方或独立更新的API，
例如可能会存在一个用于远程通讯的 `component-server/component-client`，即一些由simbot核心本身所提供的组件，将会伴随着主库一起更新，不会存在独立版本号。

### 坐标规则
`love.forte.simbot.component:simbot-component-${component-module}:${component-version}`


### 示例
#### tencent-guild组件 
`love.forte.simbot.component:simbot-component-tencent-guild-api:3.0.0.1` <br />
`love.forte.simbot.component:simbot-component-tencent-guild-stdlib:3.0.0.1` <br />
`love.forte.simbot.component:simbot-component-tencent-guild-core:3.0.0.1` <br />

#### mirai组件 
`love.forte.simbot.component:simbot-component-mirai:3.0.1.2` <br />



## 快照版本
在上述 模块与版本 中描述的基础上，当版本为 快照版本 的时候，版本号应当有且仅有结尾处存在 `-SNAPSHOT` 标记。
在组件版本中，同样只保留结尾处的快照标记，其余标记将会被清除。

举个例子，假如某核心快照版本为 `3.2.1-SNAPSHOT`，则某个组件的快照版本可能为 `3.2.5.0-SNAPSHOT`，其中，组件的快照标记与核心的快照标记一致。

:::info

`STATUS` 标记的情况与快照标记的情况类似，例如核心版本的 `3.1.0.beta` 对应某组件的 `3.1.5.5.beta` 。

:::
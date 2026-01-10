#

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="../.simbot/logo-dark.svg">
  <source media="(prefers-color-scheme: light)" srcset="../.simbot/logo.svg">
  <img alt="simbot logo" src="../.simbot/logo.svg" width="260" />
</picture>
<h2>
    ~ Simple Robot ~ <br/> <small>QQ频道组件</small>
</h2>
<a href="https://github.com/simple-robot/simbot-component-qq-guild/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/simple-robot/simbot-component-qq-guild" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simbot/component/simbot-component-qq-guild-api/" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot.component/simbot-component-qq-guild-api" /></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/simple-robot/simbot-component-qq-guild" />
   <img alt="forks" src="https://img.shields.io/github/forks/simple-robot/simbot-component-qq-guild" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/simple-robot/simbot-component-qq-guild" />
   <img alt="repo size" src="https://img.shields.io/github/repo-size/simple-robot/simbot-component-qq-guild" />
   <img alt="lines" src="https://img.shields.io/tokei/lines/github/simple-robot/simbot-component-qq-guild" />
   <img alt="issues" src="https://img.shields.io/github/issues-closed/simple-robot/simbot-component-qq-guild?color=green" />
   <img alt="last commit" src="https://img.shields.io/github/last-commit/simple-robot/simbot-component-qq-guild" />
   <a href="./COPYING"><img alt="copying" src="https://img.shields.io/github/license/simple-robot/simbot-component-qq-guild" /></a>
   <a href="https://deepwiki.com/simple-robot/simbot-component-qq-guild"><img src="https://deepwiki.com/badge.svg" alt="Ask DeepWiki"></a>

</div>

这是一个 [Kotlin 多平台][KMP]
的 [**QQ机器人官方API**][qg bot doc]
SDK 实现库，
也是 [Simple Robot][simbot4 gh] 标准API下实现的组件库，
异步高效、Java友好！

借助 simbot 核心库提供的能力，它可以支持很多高级功能和封装，比如组件协同、Spring支持等，
助你快速开发机器人应用！

> 序列化和网络请求相关分别基于 [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization)
和 [Ktor](https://ktor.io/).

## 文档与引导

- 手册: [Simple Robot 应用手册](https://simbot.forte.love) 及其中 [**QQ机器人组件**](https://simbot.forte.love/component-qq-guild.html) 部分
- [文档引导站&API文档](https://docs.simbot.forte.love)
- [**社群**](https://simbot.forte.love/communities.html) 文档中也有提供社群信息喔
- 前往 [组织首页](https://github.com/simple-robot/) 了解更多有关组件、文档、以及社群等相关信息！

---

我们欢迎并期望着您的
[反馈](https://github.com/simple-robot/simbot-component-qq-guild/issues)
或
[协助](https://github.com/simple-robot/simbot-component-qq-guild/pulls)，
感谢您的贡献与支持！

## 概述

QQ机器人组件是对 [simbot4核心库](https://github.com/simple-robot/simpler-robot)
的组件实现，
是一个相对高度封装的模块，并提供simbot大部分能力，包括事件监听、多组件协同、Spring Boot Starter 等。

👉 [前往模块](simbot-component-qq-guild-core) 了解更多~

## 命名说明

QQ机器人组件命名为 `simbot-component-qq-guild` ，
因为最早开始QQ并未开放普通个人开发者使用QQ群聊、QQ单聊的功能，
因此此组件当时仅支持QQ频道。
在开放后，其两端可以合并在一起使用，因此QQ群相关的能力才被支持。

> 也许未来会更名为 `simbot-component-qq` ?

## 法欧莉!

如果你想看一看使用QQ频道组件实现的具体作品，
可以前往QQ频道添加亲爱的 [法欧莉斯卡雷特](https://qun.qq.com/qunpro/robot/share?robot_appid=101986850) 来体验喔~


## License

`simbot-component-qq-guild` 使用 `LGPLv3` 许可证开源。

```
This program is free software: you can redistribute it and/or
modify it under the terms of the GNU Lesser General
Public License as published by the Free Software Foundation,
either version 3 of the License, or (at your option)
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this program.
If not, see <https://www.gnu.org/licenses/>.
```


[simbot4 gh]: https://github.com/simple-robot/simpler-robot/tree/v4-dev
[simbot doc]: https://simbot.forte.love
[qg bot doc]: https://bot.q.qq.com/wiki/develop/api/
[KMP]: https://kotlinlang.org/docs/multiplatform.html

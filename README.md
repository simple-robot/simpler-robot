<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
<a href="https://simbot.forte.love/">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset=".simbot/logo-dark.svg">
  <source media="(prefers-color-scheme: light)" srcset=".simbot/logo.svg">
  <img alt="simbot logo" src=".simbot/logo.svg" width="260" />
</picture>
</a>
<h2>
    - Simple Robot -
</h2>
<small>
        ~ simbot ~      
</small>
<br>
    <span>
        <a href="https://github.com/simple-robot/simpler-robot" target="_blank">GitHub</a>
    </span> 
    &nbsp;&nbsp; | &nbsp;&nbsp;
    <span>
        <a href="https://gitee.com/simple-robot/simpler-robot" target="_blank">Gitee</a>
    </span> <br />
    <small> &gt; 感谢 <a href="https://github.com/ForteScarlet/CatCode" target="_blank">CatCode</a> 开发团队成员制作的simbot logo &lt; </small>
    <br>
    <small> &gt; 走过路过，不要忘记点亮一颗⭐喔~ &lt; </small> 
    <br>
   <a href="https://github.com/simple-robot/simpler-robot/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/simple-robot/simpler-robot" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simbot/simbot-api/" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot/simbot-api" /></a>
<a href="https://simbot.forte.love" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-simbot-brightgreen" /></a>
<a href="https://qodana.cloud/projects/p9mmM/reports/79Xen" target="_blank">
  <img alt="Qodana" src="https://github.com/simple-robot/simpler-robot/actions/workflows/qodana_code_quality.yml/badge.svg" /></a>
  <a href="https://deepwiki.com/simple-robot/simpler-robot"><img src="https://deepwiki.com/badge.svg" alt="Ask DeepWiki"></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/simple-robot/simpler-robot" />
   <img alt="forks" src="https://img.shields.io/github/forks/simple-robot/simpler-robot" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/simple-robot/simpler-robot" />
   <img alt="repo-size" src="https://img.shields.io/github/repo-size/simple-robot/simpler-robot" />
   <img alt="code-size" src="https://img.shields.io/github/languages/code-size/simple-robot/simpler-robot" />
   
   <img alt="issues" src="https://img.shields.io/github/issues-closed/simple-robot/simpler-robot?color=green" />
   <img alt="top-language" src="https://img.shields.io/github/languages/top/simple-robot/simpler-robot" />
<a href="./COPYING"><img alt="copying" src="https://img.shields.io/github/license/simple-robot/simpler-robot" /></a>

<br>

</div>

[English](README_en.md)

## 简介

**`Simple Robot`** 是一个基于[Kotlin协程](https://github.com/Kotlin/kotlinx.coroutines)
的[Kotlin多平台](https://kotlinlang.org/docs/multiplatform.html)
**Bot风格**高性能异步事件调度框架（下文简称simbot），
异步高效、Java友好~

simbot提供统一的异步API和易用的风格设计，可以协助你更快速高效的编写**Bot风格**的事件调度应用。
主要应用于对接各种类型的Bot应用平台/框架，并提供部分组件库实现。

> [!note]
> 自 5.0 版本起，所有由我们团队维护且稳定的组件，均合并至当前核心仓库一同维护。
> 
> | 组件库 | 原仓库 | 现目录 |
> | ---- | ---- | ---- |
> | QQ组件 | [simple-robot/simbot-component-qq-guild](https://github.com/simple-robot/simbot-component-qq-guild) | [simbot-component-qq](https://github.com/simple-robot/simpler-robot/tree/dev/simbot-component-qq) |
> | OneBot组件 | [simple-robot/simbot-component-onebot](https://github.com/simple-robot/simbot-component-onebot) | [simbot-component-onebot](https://github.com/simple-robot/simpler-robot/tree/dev/simbot-component-onebot) |
> | KOOK组件 | [simple-robot/simbot-component-kook](https://github.com/simple-robot/simbot-component-kook) | [simbot-component-kook](https://github.com/simple-robot/simpler-robot/tree/dev/simbot-component-kook) |

simbot的**平台功能**由组件驱动，安装不同的组件库来获得不同的功能支持。

举个例子，在simbot中使用 KOOK 和 QQ 组件：

```Kotlin
suspend fun main() {
    launchSimpleApplication { config() }
        .joinWith { module() }
}

fun ApplicationFactoryConfigurer<*, *, *>.config() {
    // 安装 KOOK 和 QQ 组件
    useKook()
    useQQGuild()
}

/**
 * 对已经构建完成的 `Application` 进行配置于应用
 */
suspend fun Application.module() {
    registerBots()
    registerListeners()
}

/**
 * 注册所需的bot
 */
suspend fun Application.registerBots() {
    // ... 注册kook bot，并在此之后可处理到kook的相关事件
    kookBots {
        register(...) { ... }.start()
    }

    // ... 注册QQ频道bot，并在此之后可处理到QQ频道的相关事件
    qqGuildBots {
        register(...) { ... }.start()
    }
}

fun Application.registerListeners() {
    listeners {
        // 注册一个事件处理器
        // ChatChannelMessageEvent 是由simbot API定义的泛用类型，代表所有子频道消息事件
        // 其中就包括QQ频道的公域消息事件, 或者KOOK的频道消息事件
        listen<ChatChannelMessageEvent> {
            println("context: $this")
            println("context.event: $event")

            // 返回事件处理结果
            EventResult.empty()
        }

        // 再注册一个事件处理器
        // 明确监听QQ频道的公域消息事件
        // 使用 process 不需要返回值
        process<QGAtMessageCreateEvent> {
            println("context: $this")
            println("context.event: $event")
        }

        // 再注册一个事件处理器
        // 明确监听KOOK的频道消息事件
        // 使用 process 不需要返回值
        process<KookChannelMessageEvent> {
            println("context: $this")
            println("context.event: $event")
        }
    }
}
```

## 文档与引导

- [组织首页](https://github.com/simple-robot/) 了解更多有关组件、文档、以及社群等相关信息！
- [社群](https://simbot.forte.love/communities.html) 文档中也有提供社群信息喔
- [应用手册][doc-homepage]
- [文档引导站&API文档](https://docs.simbot.forte.love)

## 协助我们
为我们点亮一个 **✨star🌟** 便是能够给予我们继续走下去的最大动力与支持！

- 阅读 [**贡献指南**](docs/CONTRIBUTING_CN.md) 来了解如何贡献你的力量！ 
- 你可以通过 [**讨论区**][discussions] 与其他人或者simbot开发团队相互友好交流。
- 如果你通过此项目创建了一个很酷的开源项目，欢迎通过 [ISSUES][issues]、[讨论区][discussions]
  等方式留下你的开源项目信息，并将你酷酷的项目展示在作品展示区。

## 联系我们
- 如果想要反馈问题、提出建议建议或提出问题，请通过 [**ISSUES**][issues]。
- 如果想要与开发团队交流、与其他开发者交流，请前往 [**讨论区**][discussions]。
- 可以前往 [GitHub 组织首页](https://github.com/simple-robot/) 查看更多**社群信息**。


[pr]: https://github.com/simple-robot/simpler-robot/pulls
[issues]: https://github.com/simple-robot/simpler-robot/issues
[discussions]: https://github.com/orgs/simple-robot/discussions


## 特别鸣谢

<a href="https://www.jetbrains.com/?from=simpler-robot">
<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.png" width="200" alt="jetbrains" />
</a>

感谢 [Jetbrains][jetbrains] 为团队提供的免费授权，也希望大家能够支持 [Jetbrains][jetbrains] 及其产品，支持正版。

[jetbrains]: https://www.jetbrains.com/?from=simpler-robot

## 星星！

[![Star History Chart](https://star-history.dera.page/svg?repos=simple-robot/simpler-robot&type=Date)](https://star-history.dera.page/#simple-robot/simpler-robot&Date)

> powered by [Star History](https://star-history.dera.page)

## License

Simple Robot 使用 [LGPLv3](https://www.gnu.org/licenses/#LGPL) 协议开源。

```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by 
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
Lesser GNU General Public License for more details.

You should have received a copy of the Lesser GNU General Public License 
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

[doc-homepage]: https://simbot.forte.love/

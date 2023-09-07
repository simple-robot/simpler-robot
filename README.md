<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <a href="https://simbot.forte.love/"><img src=".simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " /></a>
    <h2>
        - Simple Robot -
    </h2>
    <small>
        ~ simbot v3 ~      
</small>
<br>
    <span>
        <a href="https://github.com/ForteScarlet/simpler-robot" target="_blank">GitHub</a>
    </span> 
    &nbsp;&nbsp; | &nbsp;&nbsp;
    <span>
        <a href="https://gitee.com/ForteScarlet/simpler-robot" target="_blank">Gitee</a>
    </span> <br />
    <small> &gt; 感谢 <a href="https://github.com/ForteScarlet/CatCode" target="_blank">CatCode</a> 开发团队成员制作的simbot logo &lt; </small>
    <br>
    <small> &gt; 走过路过，不要忘记点亮一颗⭐喔~ &lt; </small> 
    <br>
   <a href="https://github.com/ForteScarlet/simpler-robot/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/ForteScarlet/simpler-robot" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simbot/simbot-api/" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot/simbot-api" /></a>
<a href="https://simbot.forte.love" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-simbot-brightgreen" /></a>
<a href="https://qodana.cloud/projects/p9mmM/reports/79Xen" target="_blank">
  <img alt="Qodana" src="https://github.com/simple-robot/simpler-robot/actions/workflows/qodana_code_quality.yml/badge.svg" /></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/ForteScarlet/simpler-robot" />
   <img alt="forks" src="https://img.shields.io/github/forks/ForteScarlet/simpler-robot" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/ForteScarlet/simpler-robot" />
   <img alt="repo-size" src="https://img.shields.io/github/repo-size/ForteScarlet/simpler-robot" />
   <img alt="total-lines" src="https://img.shields.io/tokei/lines/github/simple-robot/simpler-robot" />
   
   <img alt="issues" src="https://img.shields.io/github/issues-closed/ForteScarlet/simpler-robot?color=green" />
   <img alt="last-commit" src="https://img.shields.io/github/last-commit/ForteScarlet/simpler-robot" />
   <img alt="search-hit" src="https://img.shields.io/github/search/simple-robot/simpler-robot/simbot" />
   <img alt="top-language" src="https://img.shields.io/github/languages/top/ForteScarlet/simpler-robot" />
<a href="./COPYING"><img alt="copying" src="https://img.shields.io/github/license/ForteScarlet/simpler-robot" /></a>

<br>

</div>

<!-- <img alt="lines" src="https://img.shields.io/tokei/lines/github/ForteScarlet/simpler-robot" /> -->

## 简介

**`Simple Robot`** 是一个JVM平台（和多平台）的bot风格事件调度框架（下文简称simbot），提供统一的异步API和易用的风格设计，可以协助你更快速高效的编写bot风格的事件调度应用。
目前主要应用于对接各种类型的bot应用平台/框架，并提供统一的API实现。

**`simbot`** 通过 [Kotlin](https://kotlinlang.org/) 语言开发并兼容Java（jdk8+）等JVM平台语言，且提供Javaer最爱的Spring Boot Starter，协助你快速开发。

<br>
<br>


> _如果你想参考simbot2, 可以参考 [simple-robot-v2](https://github.com/simple-robot/simple-robot-v2)_

## 文档

对于使用文档，可以去看看 [**Simple Robot 3 官方网站**][doc-homepage] (建议开启“魔法”访问)，
而API文档则可以前往 [文档引导站点](https://docs.simbot.forte.love) 寻找并查看。API文档归根随着版本的发布而同步更新。

而这些内容，你都可以在 [**Simple Robot 图书馆**](http://github.com/simple-robot-library) 处找到。

## 组件

在simbot3相关的系列组件中，大部分需要依赖第三方库（也有可能是由simbot团队实现的）的组件，基本上都会使用独立的仓库进行管理，
并且会尽量遵循simbot3的 [命名概述](https://www.yuque.com/simpler-robot/simpler-robot-doc/yqlxig) 中所约定的规则。
<br>
simbot3目前已经实现的组件有：

|                  组件目标                   |    主要成员     |                                                     仓库地址                                                      | 状态  |
|:---------------------------------------:|:-----------:|:-------------------------------------------------------------------------------------------------------------:|:---:|
| [Mirai](https://github.com/mamoe/mirai) | simbot team |         [simple-robot/simbot-component-mirai](https://github.com/simple-robot/simbot-component-mirai)         | 维护中 |
|  [Kook(开黑啦)](https://www.kookapp.cn)   | simbot team |          [simple-robot/simbot-component-kook](https://github.com/simple-robot/simbot-component-kook)          | 维护中 |
|  [QQ频道](https://bot.q.qq.com/wiki)   | simbot team | [simple-robot/simbot-component-tencent-guild](https://github.com/simple-robot/simbot-component-qq-guild) | 维护中 |
|  [米游社大别野](https://open.miyoushe.com)   | simbot team | [simple-robot/simbot-component-tencent-miyoushe](https://github.com/simple-robot/simbot-component-miyoushe) | _计划中_ |

有关于这些组件等simbot附属内容的相关信息，你可以从 [**Simple Robot 附属组织库**](https://github.com/simple-robot) 处查看~


## 快速开始

你可以前往 [**官方网站**][doc-homepage] 的**文档**内查看与快速开始有关的内容。

## 信息资讯
如果你想要时刻关注版本的发布信息，你可以通过GitHub的 **Watch** 功能来订阅包括 
[**Releases**](https://github.com/simple-robot/simpler-robot/releases) 
在内的各种仓库资讯。

如果你感兴趣，可以时不时的去 [**Simple Robot Blog**](https://simbot.forte.love/blog) 看一看，
那里会有基本上每周更新的 [**周报**](https://simbot.forte.love/blog/tags/%E5%91%A8%E6%8A%A5) 
以及一些其他可能不定时更新发布的博客，或者碎碎念！

## 协助我们
为我们点亮一个**✨star🌟**便是能够给予我们继续走下去的最大动力与支持！

- 你可以通过 [**PR**][pr] 为项目代码作出贡献。
- 你可以通过 [**ISSUES**][issues] 提出一个建议或者反馈一个问题。
- 你可以通过 [**讨论区**][discussions] 与其他人或者simbot开发团队相互友好交流。
- 如果你通过此项目创建了一个很酷的项目，欢迎通过 [ISSUES][issues]、[讨论区][discussions]
  等方式联系团队开发人员，并将你酷酷的项目展示在作品展示区。

## 联系我们
- 如果想要反馈问题、提出建议建议或提出问题，请通过 [**ISSUES**][issues]。
- 如果想要与开发团队交流、与其他开发者交流，请前往 [**讨论区**][discussions]。


[pr]: https://github.com/simple-robot/simpler-robot/pulls
[issues]: https://github.com/simple-robot/simpler-robot/issues
[discussions]: https://github.com/orgs/simple-robot/discussions


## 走马观花

**事件监听(标准库)**

```kotlin
suspend fun main() {
    // 构建Application
    val app = createSimpleApplication {  }
    // 注册监听函数
    app.eventListenerManager.listeners {
    // 注册一个 '好友消息' 事件
    FriendMessageEvent { event ->
        event.reply("你也好")
      } onMatch { textContent == "你好" }
    }
    // 挂起并直到被关闭
    app.join()
}
```

**事件监听(BOOT)**

```kotlin
@Listener
@Filter("喵{1,3}")   // match: 喵,喵喵,喵喵喵
suspend fun FriendMessageEvent.onEvent() {
  // ...
}
```

```kotlin
@Listener
@ContentTrim // 匹配前trim
@Filter("喵{1,3}") // match: 喵,喵喵,喵喵喵
suspend fun FriendMessageEvent.onEvent() {
  // ...
}
```

**消息发送**

```kotlin
@Listener
suspend fun GroupMessageEvent.onEvent() {
    group().send(At(114.ID) + "喵!".toText() + Face(514.ID))
}
```

**会话**

```kotlin
@Listener
suspend fun FriendMessageEvent.onEvent(session: ContinuousSessionContext) {
    // import love.forte.simbot.event.invoke
    val nextEvent: FriendMessageEvent = session { next(FriendMessageEvent) }
    session {
        val next1: FriendMessageEvent = next(FriendMessageEvent)
        val next2: FriendMessageEvent = next(FriendMessageEvent)
        val next3: FriendMessageEvent = next(FriendMessageEvent)
        // ...
    }
}
```

**Java(Spring Boot Starter)**

```java
@SpringBootApplication
@EnableSimbot // 启用
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
    
    /** 事件监听 */
    @Listener
    public void onFriendMessage(FriendMessageEvent event) {
        event.replyBlocking("Hello, ");
        event.getFriend().sendAsync("Simbot");
        // 阻塞或异步的不同风格的Java API
    }
}
```



## 特别鸣谢

[<img src=".simbot/jetbrains.png" width="200" alt="jetbrains" />](https://www.jetbrains.com/?from=simpler-robot)

感谢 [Jetbrains][jetbrains] 为团队提供的免费授权，也希望大家能够支持 [Jetbrains][jetbrains] 及其产品，支持正版。

[jetbrains]: https://www.jetbrains.com/?from=simpler-robot

## 贡献你的星星！

[![Star History Chart](https://api.star-history.com/svg?repos=simple-robot/simpler-robot&type=Date)](https://star-history.com/#simple-robot/simpler-robot&Date)

> powered by [Star History](https://star-history.com)

## License

```
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General 
Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) 
any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
details.

You should have received a copy of the GNU Lesser General Public License along with this program. If not, see 
<https://www.gnu.org/licenses/>.
```

**Simple Robot 3** 以 `LGPLv3` 协议开源。


[doc-homepage]: https://simbot.forte.love/

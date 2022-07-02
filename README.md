<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <img src=".simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " />
    <h2>
        - Simply Robot -
    </h2>
    <small>
        ~ simbot v3 ~      
</small>
<br>
    <span>
        <a href="https://github.com/ForteScarlet/simpler-robot" target="_blank">github</a>
    </span> 
    &nbsp;&nbsp; | &nbsp;&nbsp;
    <span>
        <a href="https://gitee.com/ForteScarlet/simpler-robot" target="_blank">gitee</a>
    </span> <br />
    <small> &gt; 感谢 <a href="https://github.com/ForteScarlet/CatCode" target="_blank">CatCode</a> 开发团队成员制作的simbot logo &lt; </small>
    <br>
    <small> &gt; 走过路过，不要忘记点亮一颗⭐喔~ &lt; </small> 
    <br>
   <a href="https://github.com/ForteScarlet/simpler-robot/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/ForteScarlet/simpler-robot" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simbot/simbot-api/" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot/simbot-api" /></a>
<a href="https://www.yuque.com/simpler-robot/simpler-robot-doc" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-yuque-brightgreen" /></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/ForteScarlet/simpler-robot" />
   <img alt="forks" src="https://img.shields.io/github/forks/ForteScarlet/simpler-robot" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/ForteScarlet/simpler-robot" />
   <img alt="repo-size" src="https://img.shields.io/github/repo-size/ForteScarlet/simpler-robot" />
   
   <img alt="issues" src="https://img.shields.io/github/issues-closed/ForteScarlet/simpler-robot?color=green" />
   <img alt="last-commit" src="https://img.shields.io/github/last-commit/ForteScarlet/simpler-robot" />
   <img alt="search-hit" src="https://img.shields.io/github/search/ForteScarlet/simpler-robot/simbot" />
   <img alt="top-language" src="https://img.shields.io/github/languages/top/ForteScarlet/simpler-robot" />
<a href="./COPYING"><img alt="copying" src="https://img.shields.io/github/license/ForteScarlet/simpler-robot" /></a>

<br>
    </div>

<!-- <img alt="lines" src="https://img.shields.io/tokei/lines/github/ForteScarlet/simpler-robot" /> -->

## 简介

**`Simply Robot`** 是一个通用机器人开发框架，是 **`Simple Robot`** 的 3.x 版本命名 (下文简称`simbot3`)。

`simbot3` 是一个JVM平台的通用机器人开发框架，基于simbot核心API并对接开发不同平台的机器人应用，你可以使用相同的代码风格来开发不同平台的机器人。

它提供了丰富的api接口与各种模块以支持机器人开发者与组件开发者使用，对于机器人开发者，你可以通过功能丰富的注解来实现各种较为复杂的事件匹配逻辑。对于组件开发者，你拥有很高的可选择性与灵活性来针对一个平台进行对接。

simbot3相比较于simbot2时代，其(再一次的)完全重构了整体架构，使用全面异步的api提供更加高效更加流畅的使用体验。

<br>
<br>

## ⚠ 注意！！

目前simbot3仍然处于**前期阶段**，如果你想参考simbot2, 可以参考分支: [v2-dev](https://github.com/ForteScarlet/simpler-robot/tree/v2-dev)

## 文档

对于使用文档，可以去看看 [**Simple Robot 3 使用文档**][doc-homepage] (尚在更新，并不完整)，
而API文档则可以前往 [API doc](https://simple-robot-library.github.io/simbot3-main-apiDoc) 看看。API文档归根随着版本的发布而同步更新。

而这些内容，你都可以在 [**Simple Robot 图书馆**](http://github.com/simple-robot-library) 处找到。

除了这些，还有一个可以简单作为参考的[路线图](Roadmap.md)，会简单记录计划中未来可能会进行的工作。

## 组件

在simbot3相关的系列组件中，大部分需要依赖第三方库（也有可能是由simbot团队实现的）的组件，基本上都会使用独立的仓库进行管理，
并且会尽量遵循simbot3的 [命名概述](https://www.yuque.com/simpler-robot/simpler-robot-doc/yqlxig) 中所约定的规则。
<br>
simbot3目前已经实现的组件有：

|                  组件目标                   |    作者/团队    |                                                     仓库地址                                                      | 状态  |
|:---------------------------------------:|:-----------:|:-------------------------------------------------------------------------------------------------------------:|:---:|
| [Mirai](https://github.com/mamoe/mirai) | simbot team |         [simple-robot/simbot-component-mirai](https://github.com/simple-robot/simbot-component-mirai)         | 维护中 |
|  [Kook(开黑啦)](https://www.kookapp.cn/)   | simbot team |          [simple-robot/simbot-component-kook](https://github.com/simple-robot/simbot-component-kook)          | 维护中 |
|  [QQ频道机器人](https://bot.q.qq.com/wiki)   | simbot team | [simple-robot/simbot-component-tencent-guild](https://github.com/simple-robot/simbot-component-tencent-guild) | 维护中 |

有关于这些组件等simbot附属内容的相关信息，你可以从 [**Simple Robot 附属组织库**](https://github.com/simple-robot) 处查看~



## 快速开始

你可以前往 [**文档**][doc-homepage] 查看与快速开始有关的内容。

> 文档尚在编撰完善中...

## 信息资讯
如果你想要时刻关注版本的发布信息，你可以通过GitHub的 **Watch** 功能来订阅包括 
[**Releases**](https://github.com/ForteScarlet/simpler-robot/releases) 
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
simbot不再拥有或推广任何官方管理的社交群聊（包括QQ群、TG群等诸如此类平台）。
- 如果想要反馈问题、提出建议建议或提出问题，请通过 [**ISSUES**][issues]。
- 如果想要与开发团队交流、与其他开发者交流，请前往 [**讨论区**][discussions]。


[pr]: https://github.com/ForteScarlet/simpler-robot/pulls
[issues]: https://github.com/ForteScarlet/simpler-robot/issues
[discussions]: https://github.com/ForteScarlet/simpler-robot/discussions


## 走马观花

> 从core模块的应用程序，到boot模块的监听函数。总而言之，随意看看便好。

**Application**

```kotlin
suspend fun main() {
    createSimpleApplication {                           // 构建simbot application
        listeners {                                     // 配置监听函数
            FriendMessageEvent { event ->               // 监听 「好友消息」 事件
                val receipt = event.reply("mua!")       // 回复一句「mua!」
                delay(3.seconds)                        // 挂起等待3s
                receipt.delete()                        // 撤回刚刚发送的那一句「mua!」
                event.friend().send("I love you~")      // 向这个好友发送一句「I love you~」
                eventResult()                           // 结束事件, 返回一个默认的事件处理结果
            } onMatch { it.friend().id.literal == "1145141919" } // 事件只有当好友的id为「1149159218」的时候才会触发
            
        }
    }.join()
}
```

**事件监听**

```kotlin
suspend fun main() {
    createSimpleApplication {                          // 构建simbot application
        listeners {                                    // 配置监听函数
            // ===== way 1
            FriendMessageEvent { event ->              // 监听 「好友消息」 事件
                // ...                                 // 处理逻辑
                eventResult() // result                // 结束事件, 返回一个默认的事件处理结果
            } onMatch {                                // 此事件触发前的匹配函数
                // match ...                           // 匹配逻辑
                true                                   // 匹配结果, 只有为true时才会触发事件
            } onMatch {                                // 此事件触发前的匹配函数, 与上一个函数是「&&」(逻辑与)的关系
                // and match ...
                true
            }
            
            // ===== way 2
            listen(FriendMessageEvent) {               // 监听 「好友消息」 事件
                match { true } // match ...            // 此事件触发前的匹配函数
                match { true } // and match ...        // 此事件触发前的匹配函数, 与上一个函数是「&&」(逻辑与)的关系
                
                handle { event ->                      // 监听函数处理逻辑
                    // handle..
                    
                    eventResult()                      // 结束事件, 返回一个事件处理结果
                }
            }
            
            // ===== way 3
            
            val listenerInstance: EventListener = createMyCustomListenerInstance()
            
            listener(listenerInstance)                 // 直接注册一个监听函数类型的对象
        }
    }.join()
}

private fun createMyCustomListenerInstance(): EventListener {
    // ...
}
```

**Boot模块下的Application**

```kotlin
@SimbootApplication
class App

suspend fun main(vararg args: String) {
    // import love.forte.simboot.core.invoke
    SimbootApp.run<App>(args = args).join()
}
```

**Boot模块下的事件监听**
```kotlin
@Listener
suspend fun FriendMessageEvent.onEvent() {
    // ...
}
```

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


## 特别鸣谢

[<img src=".simbot/jetbrains.png" width="200" alt="jetbrains" />](https://www.jetbrains.com/?from=simpler-robot)

感谢 [Jetbrains][jetbrains] 为团队提供的免费授权，也希望大家能够支持 [Jetbrains][jetbrains] 及其产品，支持正版。

[jetbrains]: https://www.jetbrains.com/?from=simpler-robot

## 贡献你的星星！

[![Star History Chart](https://api.star-history.com/svg?repos=ForteScarlet/simpler-robot&type=Date)](https://star-history.com/#ForteScarlet/simpler-robot&Date)

> powered by [Star History](https://star-history.com)

## 开源协议

**Simple Robot 3** 以 `LGPL 3.0` 协议开源。详细参见：
* [COPYING](COPYING)
* [COPYING.LESSER](COPYING.LESSER)



[doc-homepage]: https://simbot.forte.love/
<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="../../.simbot/logo-dark.svg">
  <source media="(prefers-color-scheme: light)" srcset="../../.simbot/logo.svg">
  <img alt="simbot logo" src="../../.simbot/logo.svg" width="260" />
</picture>
<h2>
    ~ Simple Robot ~ <br/> <small>OneBot Component</small>
</h2>
<p>
<img src="https://img.shields.io/badge/OneBot-11-black?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHAAAABwCAMAAADxPgR5AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAxQTFRF////29vbr6+vAAAAk1hCcwAAAAR0Uk5T////AEAqqfQAAAKcSURBVHja7NrbctswDATQXfD//zlpO7FlmwAWIOnOtNaTM5JwDMa8E+PNFz7g3waJ24fviyDPgfhz8fHP39cBcBL9KoJbQUxjA2iYqHL3FAnvzhL4GtVNUcoSZe6eSHizBcK5LL7dBr2AUZlev1ARRHCljzRALIEog6H3U6bCIyqIZdAT0eBuJYaGiJaHSjmkYIZd+qSGWAQnIaz2OArVnX6vrItQvbhZJtVGB5qX9wKqCMkb9W7aexfCO/rwQRBzsDIsYx4AOz0nhAtWu7bqkEQBO0Pr+Ftjt5fFCUEbm0Sbgdu8WSgJ5NgH2iu46R/o1UcBXJsFusWF/QUaz3RwJMEgngfaGGdSxJkE/Yg4lOBryBiMwvAhZrVMUUvwqU7F05b5WLaUIN4M4hRocQQRnEedgsn7TZB3UCpRrIJwQfqvGwsg18EnI2uSVNC8t+0QmMXogvbPg/xk+Mnw/6kW/rraUlvqgmFreAA09xW5t0AFlHrQZ3CsgvZm0FbHNKyBmheBKIF2cCA8A600aHPmFtRB1XvMsJAiza7LpPog0UJwccKdzw8rdf8MyN2ePYF896LC5hTzdZqxb6VNXInaupARLDNBWgI8spq4T0Qb5H4vWfPmHo8OyB1ito+AysNNz0oglj1U955sjUN9d41LnrX2D/u7eRwxyOaOpfyevCWbTgDEoilsOnu7zsKhjRCsnD/QzhdkYLBLXjiK4f3UWmcx2M7PO21CKVTH84638NTplt6JIQH0ZwCNuiWAfvuLhdrcOYPVO9eW3A67l7hZtgaY9GZo9AFc6cryjoeFBIWeU+npnk/nLE0OxCHL1eQsc1IciehjpJv5mqCsjeopaH6r15/MrxNnVhu7tmcslay2gO2Z1QfcfX0JMACG41/u0RrI9QAAAABJRU5ErkJggg=="  alt="OneBot11"/>
</p>
   <hr>
</div>

Simple Robot OneBot 组件是一个
[OneBot11](https://github.com/botuniverse/onebot-11)
协议的客户端SDK，是基于
[Simple Robot](http://github.com/simple-robot/simpler-robot) 标准API实现的组件库，
提供simbot中的各项能力。

它是一个 
[Kotlin 多平台](https://kotlinlang.org/docs/multiplatform.html) 
库，Java 友好、异步高效！

借助simbot核心库提供的能力，它可以支持很多高级功能和封装，比如组件协同、Spring支持等，
祝你快速开发 OneBot 客户端应用！

序列化和网络请求相关分别基于 [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization)
和 [Ktor](https://ktor.io/)。

## 文档与引导

- 手册：[Simple Robot 应用手册](https://simbot.forte.love) 
  与手册内的 [OneBot组件](https://simbot.forte.love/component-onebot.html) 部分。
- [文档引导站 & API文档](https://docs.simbot.forte.love)
- [**社群**](https://simbot.forte.love/communities.html) (文档中也有提供社群信息喔~)
- [组织库首页](https://github.com/simple-robot/): 了解更多有关组件、文档、以及社群等相关信息！

---

我们欢迎并期望着您的
[反馈](https://github.com/simple-robot/simpler-robot/issues/new)
或
[协助](https://github.com/simple-robot/simpler-robot/pulls)，
感谢您的贡献与支持！

## 快速开始

前往手册 [OneBot组件部分](https://simbot.forte.love/component-onebot.html) 了解更多、
参考其中的[开始使用OneBot11](https://simbot.forte.love/component-onebot-v11-start-using.html)。


## 事件关系

简单列举一下原始事件与可能对应的组件事件之间的关系。

| 原始事件类型                                              | 组件事件                                   |
|-----------------------------------------------------|----------------------------------------|
| `RawMetaEvent`                                      | `OneBotMetaEvent`                      |
| > `RawLifecycleEvent`                               | > `OneBotLifecycleEvent`               |
| > `RawHeartbeatEvent`                               | > `OneBotHeartbeatEvent`               |
| `RawMessageEvent`                                   | `OneBotMessageEvent`                   |
| > `RawGroupMessageEvent`                            | > `OneBotGroupMessageEvent`            |
| > `RawGroupMessageEvent`                            | > > `OneBotNormalGroupMessageEvent`    |
| > `RawGroupMessageEvent`                            | > > `OneBotAnonymousGroupMessageEvent` |
| > `RawGroupMessageEvent`                            | > > `OneBotNoticeGroupMessageEvent`    |
| > `RawPrivateMessageEvent`                          | > `OneBotPrivateMessageEvent`          |
| > `RawPrivateMessageEvent`                          | > > `OneBotFriendMessageEvent`         |
| > `RawPrivateMessageEvent`                          | > > `OneBotGroupPrivateMessageEvent`   |
| `RawRequestEvent`                                   | `OneBotRequestEvent`                   |
| > `RawFriendRequestEvent`                           | > `OneBotFriendRequestEvent`           |
| > `RawGroupRequestEvent`                            | > `OneBotGroupRequestEvent`            |
| `RawNoticeEvent`                                    | `OneBotNoticeEvent`                    |
| > `RawFriendAddEvent`                               | > `OneBotFriendAddEvent`               |
| > `RawFriendRecallEvent`                            | > `OneBotFriendRecallEvent`            |
| > `RawGroupAdminEvent`                              | > `OneBotGroupAdminEvent`              |
| > `RawGroupBanEvent`                                | > `OneBotGroupBanEvent`                |
| > `RawGroupIncreaseEvent` 或 `RawGroupDecreaseEvent` | > `OneBotGroupChangeEvent`             |
| > `RawGroupIncreaseEvent`                           | > > `OneBotGroupMemberIncreaseEvent`   |
| > `RawGroupDecreaseEvent`                           | > > `OneBotGroupMemberDecreaseEvent`   |
| > `RawGroupRecallEvent`                             | > `OneBotGroupRecallEvent`             |
| > `RawGroupUploadEvent`                             | > `OneBotGroupUploadEvent`             |
| > `RawNotifyEvent`                                  | > `OneBotNotifyEvent`                  |
| > `RawNotifyEvent`                                  | > > `OneBotHonorEvent`                 |
| > `RawNotifyEvent`                                  | > > `OneBotLuckyKingEvent`             |
| > `RawNotifyEvent`                                  | > > `OneBotPokeEvent`                  |
| > `RawNotifyEvent`                                  | > > > `OneBotMemberPokeEvent`          |
| > `RawNotifyEvent`                                  | > > > `OneBotBotSelfPokeEvent`         |
| `UnknownEvent`                                      | > `UnknownEvent`                       |
| 无                                                   | `OneBotBotStageEvent`                  |
| 无                                                   | > `OneBotBotRegisteredEvent`           |
| 无                                                   | > `OneBotBotStartedEvent`              |
| 任意未支持事件                                             | `OneBotUnsupportedEvent`               |

其中，可以通过 `OneBotUnsupportedEvent` 和 `OneBotUnknownEvent`
来间接地监听那些尚未提供组件事件类型的原始事件。


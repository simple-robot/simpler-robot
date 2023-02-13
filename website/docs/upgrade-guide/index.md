---
sidebar_position: 2
title: 升级指南
---

## 对照表

此处会列举出 simbot2 版本中在 simbot3 中相似概念的功能对照表。
对照仅对于“概念”层面的对照，至于API层面，必然存在大量不兼容变更，请自行排查。

如果你有想要知道但是这里没有列举出来的对照，欢迎通过 [pr](https://github.com/simple-robot-library/simbot3-website/pulls) 或 [issues](https://github.com/simple-robot-library/simbot3-website/issues) 进行反馈，非常感谢。



|    描述    |                                     v2内容                                     |                                      v3内容                                      |                             备注                             |
|:--------:|:----------------------------------------------------------------------------:|:------------------------------------------------------------------------------:|:----------------------------------------------------------:|
|  事件总类型   |                 love.forte.simbot.api.message.events.MsgGet                  |                         love.forte.simbot.event.Event                          |                                                            |
| 监听函数定义注解 |                    love.forte.simboot.annotation.Listener                    |                                  仅存在于boot模块下。                                  |                                                            |
|  事件监听注解  | love.forte.simbot.annotation.Listen<br/>love.forte.simbot.annotation.Listens | love.forte.simboot.annotation.Listen<br/>love.forte.simboot.annotation.Listens |                   仅存在于boot模块下，且不再强求此注解。                    |
|  事件过滤注解  | love.forte.simbot.annotation.Filter<br/>love.forte.simbot.annotation.Filters | love.forte.simboot.annotation.Filter<br/>love.forte.simboot.annotation.Filters |                        仅存在于boot模块下。                        |
|   监听函数   |                 love.forte.simbot.listener.ListenerFunction                  |                     love.forte.simbot.event.EventListener                      |                                                            |
|  监听过滤器   |                   love.forte.simbot.filter.ListenerFilter                    |                      love.forte.simbot.event.EventFilter                       |                                                            |
|  监听拦截器   |                love.forte.simbot.listener.ListenerInterceptor                |                    love.forte.simbot.event.EventInterceptor                    |                                                            |
|   Bot    |                          love.forte.simbot.bot.Bot                           |                             love.forte.simbot.Bot                              |                                                            |
|  Bot管理器  |                       love.forte.simbot.bot.BotManager                       |                          love.forte.simbot.BotManager                          |                   由 OriginBotManager 管理。                   |
|   多组件    |                         love.forte.simbot.Component                          |                               2.x对多组件协同的支持并不友好。                                |                                                            |
|   用户类型   |             love.forte.simbot.api.message.containers.AccountInfo             |                       love.forte.simbot.definition.User                        |                                                            |
|    好友    |               love.forte.simbot.api.message.results.FriendInfo               |                      love.forte.simbot.definition.Friend                       |                                                            |
|    群聊    |              love.forte.simbot.api.message.containers.GroupInfo              |                       love.forte.simbot.definition.Group                       |                                                            |
|   群成员    |            love.forte.simbot.api.message.results.GroupMemberInfo             |                      love.forte.simbot.definition.Member                       |                                                            |
|    频道    |                      love.forte.simbot.definition.Guild                      |                             v2中，以群聊（group）模拟频道概念。                              |                                                            |
|   子频道    |                     love.forte.simbot.definition.Channel                     |                                                                                |                                                            |
|   消息体    |                                 猫猫码字符串/猫猫码对象                                 |                                 Message对象各类实现                                  |              v3中Message具有序列化特性，猫猫码是否存在将不影响使用。              |
|   送信器    |                    love.forte.simbot.api.sender.MsgSender                    |                   v3中的api操作都会整合到 definition 中，不再有独立的“送信器”概念。                   |                                                            |
|  Getter  |                     love.forte.simbot.api.sender.Getter                      |                                                                                |                                                            |
|  Setter  |                     love.forte.simbot.api.sender.Setter                      |                                                                                |                                                            |
|  Sender  |                     love.forte.simbot.api.sender.Sender                      |                                                                                |                                                            |
|  事件上下文   |                  love.forte.simbot.listener.ListenerContext                  |                 love.forte.simbot.event.EventProcessingContext                 |                       v2不区分事件和函数上下文。                       |
| 监听函数上下文  |                  love.forte.simbot.listener.ListenerContext                  |             love.forte.simbot.event.EventListenerProcessingContext             |                                                            |
|  瞬时作用域   |                  love.forte.simbot.listener.MapScopeContext                  |                      love.forte.simbot.event.ScopeContext                      |                                                            |
|  全局作用域   |                  love.forte.simbot.listener.MapScopeContext                  |                      love.forte.simbot.event.ScopeContext                      |                                                            |
| 持续会话作用域  |           love.forte.simbot.listener.ContinuousSessionScopeContext           |                love.forte.simbot.event.ContinuousSessionContext                |                   v3中的 持续会话 在用法上与v2完全不同。                   |
|  事件响应体   |                   love.forte.simbot.listener.ListenResult                    |                      love.forte.simbot.event.EventResult                       |                                                            |
| 事件响应处理器  |              love.forte.simbot.processor.ListenResultProcessor               |                              v3的响应体处理可以直接借助拦截器实现。                              |                                                            |
|  日志API   |                                    slf4j                                     |                                     slf4j                                      |                          这倒是没变化。                           |
|   启动入口   |                       love.forte.simbot.core.SimbotApp                       |         love.forte.simbot.core.SimbotApp	love.forte.simboot.SimbootApp         |                      v3的入口仅限于boot模块。                       |
|   启动标记   |               @love.forte.simbot.annotation.SimbotApplication                |               @love.forte.simboot.core.SimbootApplication，或其他可选项               | v3的入口不仅限于提供“标记了注解的class对象”，而是提供了更多可选项。具体可提供的内容可以参考文档或文档注释。 |


import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## 部分功能对照
### 事件监听

:::info
首先你需要明白，simbot2中的以注解形式进行事件监听的方式在simbot3中交由 **boot** 相关模块负责了，
也就是说如果你仅仅只是使用了 `love.forte.simbot:simbot-core` 之类的非 **boot** 相关的模块，那么是没有那些注解的。

下文所有示例代码中可能出现的 `@Beans`、`@Listener` (simbot3的相关示例代码) 等注解均为 **boot** 模块下内容，**core** 模块无需也没有相关注解。
此处为了简化展示，大部分示例默认认为处于 **boot** 模块中。
:::

让我们来看一下对照。首先，假如在simbot2中，你编写的内容如果是：

<Tabs groupId="code" class="shadow--md">
<TabItem value="Kotlin" default>

```kotlin title="simbot2✌"
@Beans
class MyListener {
    @OnPrivate // @Listen(PrivateMsg::class)
    suspend fun PrivateMsg.listen() {
      // do...
    }   
}
```

</TabItem>
<TabItem value="Java">

```java title="simbot2✌"
@Beans
public class MyListener {
    @OnPrivate // @Listen(PrivateMsg::class)
    public void listen(privateMsg PrivateMsg) {
        // do...
    }
}
```

</TabItem>
</Tabs>

那么在simbot3中其表现为（在boot相关模块下）：

<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin title="simbot3👌"
@Beans
class MyListener {
    @Listener
    suspend fun FriendMessageEvent.listen() {
        // do...
    }
}
```

</TabItem>
<TabItem value="KotlinTop" label="Kotlin(Top-Level)">

:::caution 实验性
`boot` 模块下对于Kotlin顶层函数的扫描与加载功能尚处于**实验阶段**。
:::

```kotlin title="simbot3👌"
@Listener
suspend fun FriendMessageEvent.listen() {
    // do...
}
```

</TabItem>
<TabItem value="Java">

```java title="simbot3👌"
@Beans
public class MyListener {
    @Listener
    public void listen(FriendMessageEvent event) {
    	// do...
    }
}
```

</TabItem>
</Tabs>

我们可以注意到如下变化：
- 不再需要通过注解标记需要监听的类型了，而是仅需要一个标记注解 `@Listener`。simbot3中会根据你所需的类型自动判断这个监听函数的监听事件类型。假如标记为了监听函数（标记了 `@Listener` ）的监听函数没有提供任何事件相关的类型，那么代表它监听所有事件。
- Kotlin中，支持扫描顶层函数。（实验阶段）
- 作为依赖注入功能的注解 `@Beans` 的 **名称** 没有变（包路径有变化）。
- 事件名称变了。


### 获取Bot
在simbot2中，你如果需要在非监听函数环境中使用bot或者需要botManager并寻找其他bot，那么你需要借助依赖注入功能：

<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin title="simbot2✌"
@Beans
class External {
	@Depend
    lateinit var botManager: BotManager
    
    fun run() {
    	// use manager..
    }
    
}
```

</TabItem>
<TabItem value="Java">

```java title="simbot2✌"
@Beans
public class External {
	@Depend
    private BotManager botManager;
    
    public void run() {
    	// use manager..
    }
    
}
```

</TabItem>
</Tabs>

而在v3中，因为所有的 `BotManager` 都是由 `OriginBotManager` 进行管理的，因此你可以考虑直接使用 `OriginBotManager`：

<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin title="simbot3👌"
fun useBotManager() {
    OriginBotManager.forEach { manager ->
        manager.all().forEach { bot ->
            println("Bot: $bot")
        }
    }
}
```

</TabItem>
<TabItem value="Java">

```java title="simbot3👌"
public void useBotManager() {
    OriginBotManager.INSTANCE.forEach(manager -> {
        manager.all().forEach(bot -> {
            System.out.println("bot: " + bot);
        });
    }); 
}
```

</TabItem>
</Tabs>

:::caution 注意
`OriginBotManager` 无关任何环境，属于 **全局** 性API。你应该谨慎考虑是否应该使用任何 **全局** 相关的API，并且这些API未来都有可能发生变更。

相关内容可参考 [BOT管理器](../definition/bot-manager) 。
:::

实际上，`OriginBotManager` 并不是特别被建议使用。在 simbot3 中，
你可以通过 `Bot`、`Application` 等多种方式来取代使用 `OriginBotManager`。


<Tabs groupId="code">
<TabItem value="Kotlin" default>

从bot：

```kotlin
@Listener
suspend fun onEvent(event: FooEvent){
    // 通过bot直接得到其所属的botManager
    val botManager = event.bot.manager
    val newBot = botManager.register(...)
    newBot.start()
    // ...
}
```

从application：

```kotlin
@Listener
suspend fun onEvent(context: EventProcessingContext,  event: FooEvent){
    context.application.botManagers.forEach { manager -> 
        if (...) {
            val newBot = manager.register(...)
            newBot.start()
            // ...
        }
    }
}
```

</TabItem>
<TabItem value="Java">

从bot：

```java
@Listener
public void onEvent(FooEvent event) throws InterruptedException {
    final BotManager<? extends Bot> manager = event.getBot().getManager();
    final Bot newBot = manager.register(...);
    newBot.startBlocking();
    // ...
}
```

从application：

```java
@Listener
public void onEvent(EventProcessingContext context, FriendMessageEvent event) throws InterruptedException {
    final Application application = context.getAttribute(ApplicationAttributes.Application);
    if (application != null) {
        for (BotManager<?> manager : application.getBotManagers()) {
            if (...) {
                final Bot bot = manager.register(...);
                bot.startBlocking();
                // ...
            }
        }
    }
}
```

或

```java
@Listener
public void onEvent(EventProcessingContext context, FriendMessageEvent event) throws InterruptedException {
    final Application application = ApplicationAttributes.getApplication(context);
    for (BotManager<?> manager : application.getBotManagers()) {
        if (...) {
            final Bot bot = manager.register(...);
            bot.startBlocking();
            // ...
        }
    }
}
```

</TabItem>
</Tabs>

### 消息对象

:::info

更多请参考 [**消息概述**](../definition/message-overview)。

:::

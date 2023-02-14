---
sidebar_position: 1
title: 使用核心库
description: 使用核心库进行开发。
tags: [快速开始]
---

此章节示例使用基础的核心模块。有关相关模块的说明，可以参考 [核心模块概述](../overviews/module-overview/core)

核心库是更贴近于原生使用习惯的库，能够让你可以更好地控制你所编写的一切。


:::caution 还差一点

simbot核心库本身没有任何平台功能。当你阅读完本章节后，你需要在核心库依赖之外添加一个或多个你所需要的组件。

你可以前往[**《组件》**](../component-overview)章节了解各个由simbot团队提供的组件实现，
比如对接QQ机器人的[**mirai组件**](../component-overview/mirai)。

:::


# 使用依赖

import version from './dpVersion.json'
import QuickStartCoreCodes from './QuickStartCoreCodes';
import QuickStartCoreSnapshotCodes from './QuickStartCoreSnapshotCodes';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';


<QuickStartCoreCodes version={version} />


<details>
<summary>使用快照版本</summary>

:::note 快照

除了使用release版本，你也可以使用快照版本。simbot 的快照版本可以前往 <a href="https://oss.sonatype.org/content/repositories/snapshots/love/forte/simbot/simbot-api/">https://oss.sonatype.org/content/repositories/snapshots/love/forte/simbot/simbot-api/</a> 查询。

:::

<QuickStartCoreSnapshotCodes version={version} />

</details>

## 使用Application

`Application` 是simbot应用程序的门户。在核心模块中提供了一个其工厂的最基础实现：`Simple`。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin title='SimpleApp.kt'
import love.forte.simbot.application.*
import love.forte.simbot.core.application.*

suspend fun main() {
    val launcher: ApplicationLauncher<SimpleApplication> = simbotApplication(Simple) {
        // build...
    }
    
    val application: SimpleApplication = launcher.launch()
    application.join()
}
```

这是一个最基础的写法。你需要从 `simbotApplication` 下的代码块中进行一系列的操作，并得到一个 `ApplicationLauncher<SimpleApplication>`。

然后，启动这个 `launcher`, 得到一个 `Application` 的 `Simple` 实现：`SimpleApplication`。最后，挂起并直到它被终止。

当然，你也可以一步到位，直接构建一个 `Application`。`Simple` 提供了几个额外的扩展函数以供使用，我们择其一作为示例：

```kotlin title='SimpleApp.kt'
import love.forte.simbot.core.application.*

suspend fun main() {
    val application: SimpleApplication = createSimpleApplication {
        // build...
    }
    application.join()
}
```

</TabItem>
<TabItem value="Java">

```java title='SimpleApp.java'
import kotlin.Unit;
import love.forte.simbot.application.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.utils.Lambdas;

public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationLauncher<SimpleApplication> launcher = Applications.simbotApplication(
                Simple.INSTANCE,
                c -> {
                    // config...
                },
                (builder, configuration) -> {
                    // build...
                });

        final SimpleApplication application = launcher.launchBlocking(); // or use launcher.launchAsync()
        
        application.joinBlocking(); // or use application.asFuture().get()
    }
}
```

这是一个最基础的写法。你需要从 `Applications.simbotApplication` 中进行一系列的操作，并得到一个 `ApplicationLauncher<SimpleApplication>`。

然后，启动这个 `launcher`, 得到一个 `Application` 的 `Simple` 实现：`SimpleApplication`。最后，阻塞并直到它被终止。

除了 `Applications.simbotApplication(...)`, 你也可以使用 `Applications.buildSimbotApplication` 来分解这其中的各项操作。

```java title='SimpleApp.java'
import kotlin.Unit;
import love.forte.simbot.application.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.utils.Lambdas;

public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.config(configuration -> {
            // ...
            
        });
        
        appBuilder.build((builder, configuration) -> {
            // build..
        });

        final SimpleApplication application = appBuilder.createBlocking();
        application.joinBlocking();

    }
}
```

</TabItem>
</Tabs>



## 事件监听
上面是安装组件、注册bot的流程，接下来是基础的监听函数注册流程。

监听函数的注册不是 `Application` 所强制要求的功能，但是 `Simple` 提供了它的基础实现。
接下来的代码示例展示通过几种不同的方式实现：当一个好友发送消息 `"喵"` 的时候，bot回复：`"喵喵喵"`


<Tabs groupId="code">
<TabItem value="Kotlin">

<Tabs>

<TabItem value="方式1">

```kotlin title='SimpleApp.kt'
suspend fun main() {
    val application = createSimpleApplication {
        installAll()
    }

    // 注册监听函数
    application.eventListenerManager.listeners {
        listen(FriendMessageEvent) {
            // 匹配函数
            match { event -> "喵" in event.messageContent.plainText.trim() }
            // 处理函数
            handle { event ->
                event.friend().send("喵喵喵")
                EventResult.defaults()
            }
        }
    }
    
    application.join()
}
```

</TabItem>

<TabItem value="方式2">

```kotlin title='SimpleApp.kt'
suspend fun main() {
    val application = createSimpleApplication {
        installAll()
    }

    // 注册监听函数
    application.eventListenerManager.listeners {
        // 匹配逻辑在监听逻辑之后。
        FriendMessageEvent { event ->
            event.friend().send("喵喵喵")
            EventResult.defaults()
        } onMatch { event ->
            "喵" in event.messageContent.plainText.trim()
        }
    }

    application.join()
}
```

</TabItem>

<TabItem value="方式3">

```kotlin title='SimpleApp.kt'
suspend fun main() {
    val application = createSimpleApplication {
        installAll()
    }

    // 注册监听函数
    application.eventListenerManager.listeners {
        // 直接提供一个 EventListener 对象，不通过builder
        // 这里借助 simpleListener 函数构建对象
        listener(simpleListener(FriendMessageEvent, matcher = { event ->
            "喵" in event.messageContent.plainText.trim()
        }) { event ->
            event.friend().send("喵喵喵")
            EventResult.defaults()
        })
    }

    application.join()
}
```

</TabItem>

</Tabs>

</TabItem>

<TabItem value="Java">

```java title='SimpleApp.java'
public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 安装组件和BotManager
            builder.install(...);
            builder.install(...);
        });

        SimpleApplication application = appBuilder.createBlocking();
        
        // 得到监听函数管理器
        SimpleEventListenerManager eventListenerManager = application.getEventListenerManager();

        // 注册一个监听函数。此处通过 SimpleListeners.listener 构建一个简易的监听函数实例并注册
        eventListenerManager.register(SimpleListeners.listener(
                // target
                FriendMessageEvent.Key,

                // 匹配函数
                (context, event) -> {
                    final String textContent = context.getTextContent();
                    return "喵".equals(textContent);
                },

                // 处理函数
                (context, event) -> {
                    event.replyBlocking("喵喵喵");
                    // or use: event.replyAsync("喵喵喵")
                    // or use: event.getFriend().sendBlocking("喵喵喵")
                    // or use: event.getFriend().sendAsync("喵喵喵")
                    // or use: event.getFriendAsync().thenAccept(friend -> friend.sendAsync("喵喵喵"));
                }));

        application.joinBlocking();
    }
}
```

</TabItem>

</Tabs>


<hr />

此时，`Application` 与事件均已装填完毕，接下来便是**安装组件**了。

## 使用组件

我们在开头的时候说过：_**simbot核心库本身没有任何平台功能。当你阅读完本章节后，你需要在核心库依赖之外添加一个或多个你所需要的组件。**_
此时便是时机，你需要选择你想要使用的[组件](../component-overview)，并阅读它们的文档，将它们添加到你的依赖中。



### 安装组件实例

在 `Application` 构建阶段，可以通过 `install(...)` 来安装 `Component` 或 `EventProvider` 的**组件实现**。

:::tip 本是同根生

`BotManager` 是 `EventProvider` 的子类型。

:::

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
createSimpleApplication {
    // 安装 Component 或 EventProvider
    install(...)
}
```

</TabItem>
<TabItem value="Java">

```java
ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
                // 安装 Component 或 EventProvider
                builder.install(...);
        });
```

</TabItem>
</Tabs>

至于 `install(...)` 中应该填什么，就与组件的实现有关了。以 [mirai组件](http://component-mirai.simbot.forte.love/) 为例，
它的实现分别为 `MiraiComponent` 和 `MiraiBotManager`，那么就会是下面这个样子：

<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin
createSimpleApplication {
    install(MiraiComponent)
    install(MiraiBotManager)
}
```

</TabItem>

<TabItem value="Java">

```java
SimpleApplication application = Applications.buildSimbotApplication(Simple.INSTANCE)
        .build((builder, config) -> {
            // 安装mirai组件
            builder.install(MiraiComponent.Factory, (config1, perceivable) -> Unit.INSTANCE);
            builder.install(MiraiBotManager.Factory, (config1, perceivable) -> Unit.INSTANCE);
        }).createBlocking();
```

</TabItem>
</Tabs>

这些具体的安装方式在各组件的手册中应当都有体现。

### 自动安装

如果你希望在添加组件依赖、移除组件依赖的前后过程中不需要频繁的修改配置代码的话，你可以使用**自动安装**的方式，寻找并安装当前依赖环境中所有**支持自动加载**的组件信息。


<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
suspend fun main() {
    createSimpleApplication {
        // 安装所有支持的组件
        installAllComponents(/* classLoader = ... */)
        // 安装所有支持的事件处理器
        installAllEventProviders(/* classLoader = ... */)
    }.join()
}
```

```kotlin
suspend fun main() {
    createSimpleApplication {
        // 同时使用上述两个方法
        installAll(/* classLoader = ... */)
    }.join()
}
```

:::info 你发现了吗

是否有些眼熟呢？`installAll()` 在之前的代码示例中出现过。

:::

</TabItem>
<TabItem value="Java">

```java
public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 尝试安装所有支持的组件
            Components.installAllComponents(builder, SimpleApp.class.getClassLoader());
            
            // 尝试安装所有支持的事件提供者
            EventProviders.installAllEventProviders(builder, SimpleApp.class.getClassLoader());
        });

        appBuilder.createBlocking().joinBlocking();
    }
}
```

```java
public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 尝试安装所有支持自动加载的内容, 既同时使用上述两个方法
            SimbotKt.installAll(builder, SimpleApp.class.getClassLoader());
        });

        appBuilder.createBlocking().joinBlocking();
    }
}
```

:::caution 其实是未命名

`SimbotKt` 在未来可能会更名或被转移，在Java中使用 `SimbotKt.installAll` 并不完全可靠。

:::

</TabItem>
</Tabs>


### 特定Bot注册

不同的组件针对 `BotManager` 和 `Bot` 的实现类型都是不同的，比如 [mirai组件](http://component-mirai.simbot.forte.love/) 中的 `MiraiBotManger` 及其对应的 `MiraiBot`，
它们注册bot的方式是 

```text title='伪代码'
MiraiBot = MiraiBotManager.register(long, String, Config)
```

而其他组件又有各自不同的注册方式。因此对于特定Bot的注册，需要根据特定组件的类型来确定，而这需要阅读它们的**使用手册**或**API文档**，或者借助IDE的**智能提示**。

### 通用Bot注册

<Tabs groupId="code">
<TabItem value="Kotlin">

除了针对于指定的组件进行特定的预注册以外，`Application` 中的 `BotManagers` 也提供了通用的注册函数 `register(BotVerifyInfo)`：

```kotlin title='SimpleApp.kt'
suspend fun main() {
    val application = createSimpleApplication {
        install(...)
    }
    
    // 读取文件为 BotVerifyInfo 类型
    val botVerifyInfo = File("fooBot.bot").toResource().toBotVerifyInfo(StandardBotVerifyInfoDecoderFactory.Json.create())
    application.botManagers.register(botVerifyInfo)
    
    application.join()
}
```

</TabItem>
<TabItem value="Java">

```java title='SimpleApp.java'
public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            builder.install(...);
            builder.install(...);
        });

        SimpleApplication application = appBuilder.createBlocking();

        final File file = new File("bots/foo.bot");
        try (
                final FileResource fileResource = Resource.of(file);
                final BotVerifyInfo botVerifyInfo = BotVerifyInfos.toBotVerifyInfo(fileResource, JsonBotVerifyInfoDecoder.Factory.create(jsonBuilder -> Unit.INSTANCE))
        ) {

            final Bot bot = application.getBotManagers().register(botVerifyInfo);
            if (bot != null) {
                bot.startBlocking();
            } else {
                throw new NoSuchComponentException();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        application.joinBlocking();
    }
}
```

</TabItem>
</Tabs>

但是在 `Core` 中并不建议这种方式，因为这不如直接使用特定组件下的注册函数来的"精确"。因此此方式不在此处进行过多介绍与解释。


## 完整示例
在最后，提供一个 _Kotlin_ 的简单而完整的示例如下（以mirai组件为例）：

```kotlin title='SimpleApp.kt'

import love.forte.simbot.application.Application
import love.forte.simbot.component.mirai.miraiBots
import love.forte.simbot.component.mirai.useMirai
import love.forte.simbot.core.application.SimpleApplicationBuilder
import love.forte.simbot.core.application.createSimpleApplication
import love.forte.simbot.core.event.listeners
import love.forte.simbot.event.FriendMessageEvent


/**
 * main入口。
 */
suspend fun main() {
    createSimpleApplication {
        // 基础配置
        configApplication()
    }.apply {
        // 注册监听函数
        configEventProcessor()
        // 注册bot
        // tips: 最好先注册监听函数在注册bot，这样如果监听函数中存在例如 BotStartedEvent, 其才能正常被触发
        configBots()
    }.join()
}

/**
 * 配置 Simple Application.
 */
private fun SimpleApplicationBuilder.configApplication() {
    configMirai()
}

/**
 * 配置mirai相关内容
 */
private fun SimpleApplicationBuilder.configMirai() {
    useMirai()
}

/**
 * 注册监听函数。如果监听函数很多，最好进行拆分。此处仅作示例
 */
private fun Application.configEventProcessor() {
    eventListenerManager.listeners {
        // 监听好友消息, 如果好友消息中有文本"喵"，回复"喵喵喵"
        listen(FriendMessageEvent) {
            // 匹配函数
            match { event -> "喵" in event.messageContent.plainText.trim() }
            // 处理函数
            process { event ->
                event.friend().send("喵喵喵")
            }

            // or:
            // handle { event ->
            //     event.friend().send("喵喵喵")
            //     EventResult.invalid() // event result.
            // }

        }
    }
}

private suspend fun Application.configBots() {
    miraiBots {
        val bot = register(123, "密码")
        bot.start()
    }
}
```


## 收尾

以上就是最基础的部分了，执行你的main方法，看看效果吧。

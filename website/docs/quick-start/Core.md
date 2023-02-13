---
sidebar_position: 1
title: 使用核心库
description: 使用核心库进行开发。
tags: [快速开始]
---

此章节示例使用基础的核心模块。有关相关模块的说明，可以参考 [核心模块概述](../overviews/module-overview/core)

核心库是更贴近于原生使用习惯的库，能够让你可以更好地控制你所编写的一切。


:::caution 还差一点

simbot核心库本身没有任何平台功能。当你阅读完本章节后，你需要在核心库依赖之外添加一个你所需要的组件。

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


## 组件安装

### 安装组件标识

构建 `Application` 并不能让你直接使用任何组件。你需要手动安装你所需要的**组件标识**（ `Component` ）。
这里让我们以mirai组件为例，你可以在实际应用中将其替换为其他组件，它们的概念是相似的。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    createSimpleApplication {
        install(MiraiComponent)
    }.join()
}
```

通常情况下，每个组件实现都会提供一些扩展函数:

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    createSimpleApplication {
        useMiraiComponent()
    }.join()
}
```

</TabItem>
<TabItem value="Java">

```java title='SimpleApp.java'
import kotlin.Unit;
import love.forte.simbot.application.*;
import love.forte.simbot.component.mirai.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.utils.Lambdas;

public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 安装MiraiComponent
            builder.install(MiraiComponent.Factory, (config, perceivable) -> Unit.INSTANCE);
        });

        appBuilder.createBlocking().joinBlocking();
    }
}
```

</TabItem>
</Tabs>





### 安装BotManager

**组件标识** 通常为作为组件自己的标识以及特殊配置而存在（甚至很多都不需要配置）。 除了组件以外，`Application` 中还需要安装的一种东西为 `EventProvider`。
mirai组件作为与bot相关的组件，通常会提供各自的 `BotManager` 实现，而 `BotManager` 也是 `EventProvider` 的一种。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.kook.*
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    createSimpleApplication {
        useMiraiComponent()
        install(MiraiBotManager)
    }.join()
}
```

同样的，大多数组件也会为这个行为提供进行简化扩展：

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.kook.*
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    createSimpleApplication {
        useMiraiComponent()
        useMiraiBotManager()
    }.join()
}
```

而对于安装**组件标识**和安装**BotManager**的操作，各组件又通常会提供整合扩展。因此上述流程可以简化为：

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.kook.*
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    createSimpleApplication {
        // 安装mirai组件标识，安装miraiBotManager
        useMirai()
    
        // 安装kook组件标识，安装KookBotManager
        useKook()
    }.join()
}
```

如果想要对各自组件下的部分进行配置，可以：

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.kook.*
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    createSimpleApplication {
        // 安装mirai组件标识，安装miraiBotManager
        useMirai {
            component { /* mirai 组件标识配置 */ }
            botManager { /* mirai botManager配置 */ }
        }
    
        // 安装kook组件标识，安装KookBotManager
        useKook {
            component { /* Kook 组件标识配置 */ }
            botManager { /* Kook botManager配置 */ }
        }
    }.join()
}
```

</TabItem>
<TabItem value="Java">

```java title='SimpleApp.java'
import kotlin.Unit;
import love.forte.simbot.application.*;
import love.forte.simbot.component.mirai.*;
import love.forte.simbot.component.mirai.bot.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.utils.Lambdas;

public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 安装MiraiComponent
            builder.install(MiraiComponent.Factory, (config, perceivable) -> Unit.INSTANCE);
            builder.install(MiraiBotManager.Factory, (config, perceivable) -> Unit.INSTANCE);
        });

        appBuilder.createBlocking().joinBlocking();
    }
}
```

</TabItem>
</Tabs>


### 自动安装
当你不关心具体组件，而只希望加载当前环境内所有支持的组件的时候，你可以使用由核心提供的扩展函数来尝试加载当前环境下所有支持自动加载的组件信息。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
import love.forte.simbot.application.*
import love.forte.simbot.core.application.*
import love.forte.simbot.installAll
import love.forte.simbot.installAllComponents

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
import love.forte.simbot.application.*
import love.forte.simbot.core.application.*
import love.forte.simbot.installAll
import love.forte.simbot.installAllComponents

suspend fun main() {
    createSimpleApplication {
        // 同时使用上述两个方法
        installAll(/* classLoader = ... */)
    }.join()
}
```

</TabItem>
<TabItem value="Java">

```java
import love.forte.simbot.Components;
import love.forte.simbot.SimbotKt;
import love.forte.simbot.application.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.utils.Lambdas;

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
import love.forte.simbot.Components;
import love.forte.simbot.SimbotKt;
import love.forte.simbot.application.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.utils.Lambdas;

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

<Tabs groupId="code">
<TabItem value="Kotlin">

既然安装了 `BotManager`, 通常情况下组件实现中会提供对bot的预注册api。

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.kook.*
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    createSimpleApplication {
        // 安装mirai组件标识，安装miraiBotManager
        useMirai {
            botManager {
                register(code = 123456L, password = "PASSWORD") { bot ->
                    // 当 application启动完成后，启动bot
                    it.onCompletion {
                        bot.start()
                    }
                }
            }
        }
    }.join()
}
```

</TabItem>
<TabItem value="Java">

既然安装了对应的 `BotManager` 之后，你便可以寻找并获取它，然后注册一个bot。

```java title='SimpleApp.java'
import kotlin.Unit;
import love.forte.simbot.application.*;
import love.forte.simbot.component.mirai.*;
import love.forte.simbot.component.mirai.bot.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.utils.Lambdas;

public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 安装mirai组件
            builder.install(MiraiComponent.Factory, (config, perceivable) -> Unit.INSTANCE);
            // 安装mirai的bot管理器
            builder.install(MiraiBotManager.Factory, (config, perceivable) -> Unit.INSTANCE);

            // 寻找mirai的bot管理器，并注册bot
            builder.bots(Lambdas.suspendConsumer(botRegistrar -> {
                for (EventProvider provider : botRegistrar.getProviders()) {
                    if (provider instanceof MiraiBotManager) {
                        MiraiBotManager miraiBotManager = (MiraiBotManager) provider;
                        final MiraiBot bot = miraiBotManager.register(123456, "PASSWORD");
                        bot.startBlocking();
                        // or bot.startAsync()

                        break;
                    }
                }
            });

        }));

        appBuilder.createBlocking().joinBlocking();
    }
}
```

</TabItem>
</Tabs>

当然，你也可以在 `Application` 启动完成后再去进行这一操作。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.kook.*
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    val application = createSimpleApplication {
        // 安装mirai组件标识，安装miraiBotManager
        useMirai()
    }
    
    application.miraiBots {
        val bot = register(code = 123456L, password = "PASSWORD")
        bot.start()
    }
    
    application.join()
}
```

</TabItem>
<TabItem value="Java">

```java title='SimpleApp.java'
import kotlin.Unit;
import love.forte.simbot.application.*;
import love.forte.simbot.component.mirai.*;
import love.forte.simbot.component.mirai.bot.*;
import love.forte.simbot.core.application.*;

public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 安装mirai组件
            builder.install(MiraiComponent.Factory, (config, perceivable) -> Unit.INSTANCE);
            // 安装mirai的bot管理器
            builder.install(MiraiBotManager.Factory, (config, perceivable) -> Unit.INSTANCE);
        });

        SimpleApplication application = appBuilder.createBlocking();

        // 寻找mirai的bot管理器，并注册bot
        for (BotManager<?> botManager : application.getBotManagers()) {
            if (botManager instanceof MiraiBotManager miraiBotManager) {
                final MiraiBot bot = miraiBotManager.register(123456, "PASSWORD");
                bot.startBlocking();
                // or bot.startAsync();
                break;
            }
        }

        application.joinBlocking();
    }
}
```

</TabItem>
</Tabs>


### 通用Bot注册

<Tabs groupId="code">
<TabItem value="Kotlin">

除了针对于指定的组件进行特定的预注册以外，`Application` 中的 `BotManagers` 也提供了通用的注册函数 `register(BotVerifyInfo)`：

```kotlin title='SimpleApp.kt'
import love.forte.simbot.component.kook.*
import love.forte.simbot.component.mirai.*
import love.forte.simbot.core.application.*

suspend fun main() {
    val application = createSimpleApplication {
        useMirai()
    }
    
    val botVerifyInfo = File("fooBot.bot").toResource().toBotVerifyInfo(StandardBotVerifyInfoDecoderFactory.Json.create())
    application.botManagers.register(botVerifyInfo)
    
    application.join()
}
```

</TabItem>
<TabItem value="Java">

```java title='SimpleApp.java'
import kotlin.Unit;
import love.forte.simbot.NoSuchComponentException;
import love.forte.simbot.application.*;
import love.forte.simbot.bot.*;
import love.forte.simbot.component.mirai.*;
import love.forte.simbot.component.mirai.bot.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.resources.*;
import love.forte.simbot.utils.Lambdas;

import java.io.File;
import java.io.IOException;

public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 安装mirai组件
            builder.install(MiraiComponent.Factory, (config, perceivable) -> Unit.INSTANCE);
            // 安装mirai的bot管理器
            builder.install(MiraiBotManager.Factory, (config, perceivable) -> Unit.INSTANCE);
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

## 监听函数
上面是安装组件、注册bot的流程，接下来是基础的监听函数注册流程。

> 下文中将会适当**省略**上述已经讲过的内容

监听函数的注册不是 `Application` 所强制要求的功能，但是 `Simple` 提供了它的基础实现。
接下来的代码示例展示通过几种不同的方式实现：当一个好友发送消息 `"喵"` 的时候，bot回复：`"喵喵喵"`

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin title='SimpleApp.kt'
import love.forte.simbot.core.application.*
import love.forte.simbot.core.event.*
import love.forte.simbot.event.*

suspend fun main() {
    val application = createSimpleApplication {
        useMirai()
    }

    // 注册监听函数
    application.eventListenerManager.listeners {
        // 👉 方式一
        listen(FriendMessageEvent) {
            // 匹配函数
            match { event -> "喵" in event.messageContent.plainText.trim() }
            // 处理函数
            handle { event ->
                event.friend().send("喵喵喵")
                EventResult.defaults()
            }
        }

        // 👉 方式二
        // 匹配逻辑在监听逻辑之后。
        FriendMessageEvent { event ->
            event.friend().send("喵喵喵")
            EventResult.defaults()
        } onMatch { event ->
            "喵" in event.messageContent.plainText.trim()
        }

        // 👉 方式三
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
<TabItem value="Java">

```java title='SimpleApp.java'
import kotlin.Unit;
import love.forte.simbot.PriorityConstant;
import love.forte.simbot.application.*;
import love.forte.simbot.core.application.*;
import love.forte.simbot.core.event.SimpleListeners;
import love.forte.simbot.event.FriendMessageEvent;
import love.forte.simbot.utils.Lambdas;
import love.forte.simbot.utils.RandomIDUtil;

public class SimpleApp {
    public static void main(String[] args) {
        final ApplicationDslBuilder<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> appBuilder = Applications.buildSimbotApplication(Simple.INSTANCE);
        appBuilder.build((builder, configuration) -> {
            // 安装mirai组件和BotManager
            builder.install(MiraiComponent.Factory, ($1, $2) -> Unit.INSTANCE);
            builder.install(MiraiBotManager.Factory, ($1, $2) -> Unit.INSTANCE);
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



## 完整示例
在最后，提供一个 _Kotlin_ 的简单而完整的示例如下：

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

---
sidebar_position: 2
tags: [快速开始]
draft: true
---

此章节示例使用基础的 `Boot` 模块。有关相关模块的说明，可以参考 [Boot模块概述](../overviews/module-overview/boot)

:::tip 此非彼

注意，此 `Boot` 并不是指 `SpringBoot` 喔。

:::

:::note 假设

下文将会 **假设** 你想要使用 [**mirai组件**](../component-overview/mirai)。

:::

# 使用依赖

import version from './dpVersion.json';
import QuickStartBootCodes from './QuickStartBootCodes';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<QuickStartBootCodes version={version} />



# 开始

:::caution 包路径

⚠️ 注意：在使用Boot模块的时候，你的启动类至少需要有**一层以上**的包路径结构。

:::


<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin 原始" default>

## 使用Application

`Application` 是simbot应用程序的门户。在Boot模块中提供了一个其工厂的最基础实现：`Boot`。

:::info application

有关 `Application` 的各种使用方式请参考 [**快速开始-Core**](./Core) ，此处不再过多重复。

:::

```kotlin title="example/BootApp.kt"
package example

import love.forte.simboot.core.application.*
import love.forte.simbot.application.*

/** main入口。 */
suspend fun main() {
    val launcher: ApplicationLauncher<BootApplication> = simbotApplication(Boot) {
        // config
    }
    val application: BootApplication = launcher.launch()
    application.join()
}
```

`Boot` 提供了简化的扩展函数：

```kotlin title="example/BootApp.kt"
package example

import love.forte.simboot.core.application.*

/** main入口。 */
suspend fun main() {
    createBootApplication { 
        // config..
    }.join()
}
```

## 组件安装


### 安装组件标识

构建 `Application` 并不能让你直接使用任何组件。你需要手动安装你所需要的**组件标识**（ `Component` ），这里以mirai组件为例：


```kotlin title='example/BootApp.kt'
import love.forte.simbot.component.mirai.*
import love.forte.simboot.core.application.*

suspend fun main() {
    createBootApplication {
        useMiraiComponent()
    }.join()
}
```

### 安装BotManager

**组件标识** 通常为作为组件自己的标识以及特殊配置而存在（甚至很多都不需要配置）。 除了组件以外，`Application` 中还需要安装的一种东西为 `EventProvider`。
mirai组件作为与bot相关的组件，通常会提供各自的 `BotManager` 实现，而 `BotManager` 也是 `EventProvider` 的一种。

```kotlin title='example/BootApp.kt'
import love.forte.simbot.component.mirai.*
import love.forte.simboot.core.application.*

suspend fun main() {
    createBootApplication {
        useMiraiComponent()
        
        useMiraiBotManager()
    }.join()
}
```

合并组件标识和botManager的配置后：

```kotlin title='example/BootApp.kt'
import love.forte.simbot.component.mirai.*
import love.forte.simboot.core.application.*

suspend fun main() {
    createBootApplication {
        useMirai()
    }.join()
}
```



### 自动安装
当你不关心具体组件，而只希望加载当前环境内所有支持的组件的时候，你可以使用由核心提供的扩展函数来尝试加载当前环境下所有支持自动加载的组件信息：

```kotlin title='example/BootApp.kt'
import love.forte.simbot.application.*
import love.forte.simbot.core.application.*
import love.forte.simbot.installAll
import love.forte.simbot.installAllComponents

suspend fun main() {
    createSimpleApplication {
        installAllComponents(/* classLoader = ... */)
        installAllEventProviders(/* classLoader = ... */)
        
        // 同时使用上述两个方法
        installAll(/* classLoader = ... */)
    }.join()
}
```

## 依赖管理
相比于 `Simple` ( `Core` 模块所提供的 ) , `Boot` 模块下提供的 `Application` 支持自动扫描并加载依赖，提供依赖管理的功能：

```kotlin title='example/BootApp.kt'
package example

import love.forte.simboot.core.application.createBootApplication

/** main入口。 */
suspend fun main() {
    createBootApplication(configurator = {
        // 配置依赖扫描的包路径列表
        classesScanPackage = listOf("example.foo", "example.tar")
    }) {
        
        // 在上述 classesScanPackage 配置扫描的范围 **之外**
        // 提供 **额外** 需要管理的bean实例。
        beans {
            // 注册一个唯一标识为 'foo' 的 Foo() 实例
            bean("foo", Foo())
            
            // 注册一个唯一标识为 'tar' 的 Tar 工厂
            bean("tar", Tar::class) { Tar() }
        }
        
        
    }.join()
}

class Foo
class Tar
```

不过，通常情况下你也许不需要使用 `beans { ... }` 配置。

## 自动扫描

`Boot` 模块下的 `Application` 提供了依赖注入功能，因此大多数其他功能都支持 **自动扫描**。
在 `Boot` 模块中，引入了 `@Listener` 和 `@Filter` 注解来配合依赖注入功能实现对监听函数的自动扫描与加载。

接下来，让我们实现两个功能：

1. 接收到**好友消息**的时候，直接复读这条消息。
2. 接收到存在 `"喵"` 文本内容的**好友消息**的时候，回复三句 `"喵喵喵"` 。

```kotlin title='example/BootApp.kt'
package example

import love.forte.simboot.core.application.createBootApplication

/** main入口。 */
suspend fun main() {
    createBootApplication(configurator = {
        // 扫描 example 包下所有类
        classesScanPackage = listOf("example")
    }) {
    
    }.join()
}
```

```kotlin title='example/listener/FooListeners.kt'
package example.listener

import love.forte.di.annotation.Beans
import love.forte.simboot.filter.MatchType
import love.forte.simbot.message.toText
import love.forte.simboot.annotation.*
import love.forte.simbot.event.*

@Beans
class FooListeners {
    
    /**
     * 收到好友消息，直接复读
     */
    @Listener
    suspend fun FriendMessageEvent.fooListener() {
        // 得到本次事件中的好友对象
        val friend = friend()
        // 向他发消息，消息内容就是本次消息内容
        friend.send(messageContent)
    }
    
    /**
     * 当包含 `"喵"` 的时候，回复三句 `"喵喵喵"` 。
     */
    @Listener
    @Filter("喵", matchType = MatchType.TEXT_CONTAINS)
    suspend fun tarListener(event: FriendMessageEvent) {
        // 这里的三句 "喵喵喵" 会使用不同的方式实现
        // 至于他们之间的区别，可以简单的参考源码或者翻阅文档
        // 如果不关心细节，那么平时选择第一种方式即可。
        
        // 第一句。
        event.friend().send("喵喵喵")
        // 第二句。
        event.inFriend { send("喵喵喵") }
        // 第三句。
        event.friend().send("喵喵喵".toText())
    }
}
```

:::info 扫描依据

注意观察 `@Beans`。包路径扫描时，只会扫描加载标记了 `@Beans` 的类。

同样的，注意观察 `@Listener`。只有标记了 `@Listener` 的 **公开函数** 才会被作为监听函数解析。

:::

只要包扫描路径无误，则会从日志中看到监听函数被加载的 debug 日志。

## Bot信息

在 boot 中，在存在多个组件的情况下，你可以通过 `*.bot*` 格式的配置文件来提供不同组件下的bot信息配置。

```kotlin title='example/BootApp.kt'
package example

import love.forte.simboot.core.application.createBootApplication
import love.forte.simbot.resources.Resource.Companion.toResource
import java.io.File

/** main入口。 */
suspend fun main() {
    createBootApplication(configurator = {
        
        // bot 配置文件扫描配置。
        // 如果不配置，默认为 `simbot-bots/*.bot*`。
        // 即：在资源目录下的 `simbot-bots` 目录中，扩展名前缀为 `.bot` 的文件
        // 例如：simbot-bots/foo.bot、simbot-bots/bar.bot.json 等。
        
        // 此处会覆盖默认配置, 代表扫描 simbot-bots 资源目录下所有文件名以 `bot-` 开头的、扩展名前缀为 `.bot` 的文件。
        // 例如：simbot-bots/bot-foo.bot、simbot-bots/bot-bar.bot.json 等。
        botConfigurationResources = listOf("simbot-bots/bot-*.bot*")
        
        // 这里可以配置在扫描之外的额外配置资源。
        botConfigurations = listOf(
            File("bots/tar.bot.properties").toResource()
        )
    }) {
        // ...
    }.join()
}
```

有关 `*.bot` 配置文件内的具体格式、内容或具体应用，参考下文的 **Bot配置** 。

</TabItem>
<TabItem value="Kotlin App">

## 使用SimbootApp

`SimbootApp` 是由 `Boot` 模块所提供的具有兼容性API的启动器入口。
在 `Kotlin` 中有多种方式来使用它：
```kotlin title='example/BootApp.kt'
package example

import love.forte.simboot.core.*

@SimbootApplication
class BootApp

/** main入口。 */
suspend fun main(vararg args: String) {
    SimbootApp.run(BootApp::class, args = args).join()
}
```
```kotlin title='example/BootApp.kt'
package example

import love.forte.simboot.core.*

@SimbootApplication
class BootApp

/** main入口。 */
suspend fun main(vararg args: String) {
    SimbootApp<BootApp>(args = args).join()
}
```

首先，编写一个启动类（示例中为 `BootApp`），然后编写Main方法并通过 `SimbootApp.run(...)` 或者 `SimbootApp(...)` (同 `SimbootApp.invoke(...)` ) 来使用此启动类。

## 依赖扫描
`SimbootApp` 配合 `@SimbootApplication` 隐藏了配置细节。在默认情况下，`SimbootApp` 所启动的 `Application`
将会使用标记了 `@SimbootApplication` 的这个启动类**的包路径**作为根路径进行扫描。

以上述的示例来讲，那么它扫描的路径就是 `example` 包下的所有内容。

:::note

你可以通过 `@SimbootApplication` 注解中的参数来修改各项默认参数。例如：

```java title='example/BootApp.kt'
package example

import love.forte.simboot.core.*

@SimbootApplication(classesPackages = ["example.listeners"])
class BootApp

suspend fun main(vararg args: String) { /* ... */ }
```

:::

## 监听函数
`Boot` 模块会自动解析加载的所有Bean中的监听函数。
在 `Boot` 模块中，引入了 `@Listener` 和 `@Filter` 注解来配合依赖注入功能实现对监听函数的自动扫描与加载。

接下来，让我们实现两个功能：

1. 接收到**好友消息**的时候，直接复读这条消息。
2. 接收到存在 `"喵"` 文本内容的**好友消息**的时候，回复三句 `"喵喵喵"` 。

```java title='example/BootApp.kt'
package example

import love.forte.simboot.core.*

@SimbootApplication
class BootApp

/** main入口。 */
suspend fun main(vararg args: String) {
    SimbootApp.run(BootApp::class, args = args).join()
}
```

```java title='example/listener/FooListeners.kt'
package example.listener

import love.forte.di.annotation.Beans
import love.forte.simboot.filter.MatchType
import love.forte.simbot.message.toText
import love.forte.simboot.annotation.*
import love.forte.simbot.event.*

@Beans
class FooListeners {
    
    /**
     * 收到好友消息，直接复读
     */
    @Listener
    suspend fun FriendMessageEvent.fooListener() {
        // 得到本次事件中的好友对象
        val friend = friend()
        // 向他发消息，消息内容就是本次消息内容
        friend.send(messageContent)
    }
    
    /**
     * 当包含 `"喵"` 的时候，回复三句 `"喵喵喵"` 。
     */
    @Listener
    @Filter("喵", matchType = MatchType.TEXT_CONTAINS)
    suspend fun tarListener(event: FriendMessageEvent) {
        // 这里的三句 "喵喵喵" 会使用不同的方式实现
        // 至于他们之间的区别，可以简单的参考源码或者翻阅文档
        // 如果不关心细节，那么平时选择第一种方式即可。
        
        // 第一句。
        event.friend().send("喵喵喵")
        // 第二句。
        event.inFriend { send("喵喵喵") }
        // 第三句。
        event.friend().send("喵喵喵".toText())
    }
}
```


:::info 扫描依据

注意观察 `@Beans`。包路径扫描时，只会扫描加载标记了 `@Beans` 的类。

同样的，注意观察 `@Listener`。只有标记了 `@Listener` 的 **公开函数** 才会被作为监听函数解析。

:::



## Bot信息

在 boot 中，在存在多个组件的情况下，你可以通过 `*.bot*` 格式的配置文件来提供不同组件下的bot信息配置。

默认情况下，你需要在你的资源路径中的 `simbot-bots` 目录下配置你的 `*.bot*` 配置文件，例如 `simbot-bots/bot1.bot` 、 `simbot-bots/bot2.bot.json` 等。
`Boot` 模块会根据你的配置文件中指定的组件信息寻找当前环境中对应的组件并进行注册。

:::note 自定义扫描

如上文所述，默认的bot配置扫描规则为 `simbot-bots/*.bot*`。但是你同样可以通过 `@SimbootApplication` 的参数修改这一默认值：
```java title='example/BootApp.kt'
@SimbootApplication(botResources = ["bots/bot-*.bot*"])
class BootApp

suspend fun main(vararg args: String) { /* ... */ }
```

如此示例中，将bot资源扫描规则调整为了 "bots目录下、文件名开头为 `bot-`、文件扩展名开头为 `.bot`" 的文件，
例如 `bots/bot-foo.bot` 、 `bots/bot-bar.bot.json` 等。

:::

</TabItem>
<TabItem value="Java" label="Java App">

## 使用SimbootApp

`SimbootApp` 是由 `Boot` 模块所提供的具有兼容性API的启动器入口。当然，虽说是"具有兼容性API"，
实际上它根本就没有几个API。

```java title='example/BootApp.java'
package example;

import love.forte.simboot.core.SimbootApp;
import love.forte.simboot.core.SimbootApplication;
import love.forte.simboot.core.application.BootApplication;
import love.forte.simbot.application.ApplicationLauncher;

@SimbootApplication
public class BootApp {
    public static void main(String[] args) {
        final ApplicationLauncher<BootApplication> launcher = SimbootApp.run(BootApp.class, args);
        final BootApplication application = launcher.launchBlocking();
        application.joinBlocking();
    }
}
```

首先，编写一个启动类（示例中为 `BootApp`），然后编写Main方法并通过 `SimbootApp.run(...)` 来使用此启动类。
当然，如果你不关心 `Launcher` 或者什么 `Application` 云云的，那么也可以稍微简化一下：

```java title='example/BootApp.java'
package example;

import love.forte.simboot.core.SimbootApp;
import love.forte.simboot.core.SimbootApplication;

@SimbootApplication
public class BootApp {
    public static void main(String[] args) {
        SimbootApp.run(BootApp.class, args).launchBlocking().joinBlocking();
    }
}
```

## 依赖扫描

`SimbootApp` 配合 `@SimbootApplication` 隐藏了配置细节。在默认情况下，`SimbootApp` 所启动的 `Application`
将会使用标记了 `@SimbootApplication` 的这个启动类**的包路径**作为根路径进行扫描。

以上述的示例来讲，那么它扫描的路径就是 `example` 包下的所有内容。

:::note 

你可以通过 `@SimbootApplication` 注解中的参数来修改各项默认参数。例如：

```java title='example/BootApp.java'
@SimbootApplication(classesPackages = "example.listeners")
public class BootApp {
    // ...
}
```

:::

## 监听函数
`Boot` 模块会自动解析加载的所有Bean中的监听函数。
在 `Boot` 模块中，引入了 `@Listener` 和 `@Filter` 注解来配合依赖注入功能实现对监听函数的自动扫描与加载。

接下来，让我们实现两个功能：

1. 接收到**好友消息**的时候，直接复读这条消息。
2. 接收到存在 `"喵"` 文本内容的**好友消息**的时候，回复三句 `"喵喵喵"` 。

```java title='example/BootApp.java'
package example;

import love.forte.simboot.core.SimbootApp;
import love.forte.simboot.core.SimbootApplication;


@SimbootApplication(classesPackages = "example.listeners")
public class BootApp {
    public static void main(String[] args) {
        SimbootApp.run(BootApp.class, args).launchBlocking().joinBlocking();
    }
}
```
```java title='example/listener/BarListeners.java'
package example.listener;

import love.forte.simboot.annotation.*;
import love.forte.di.annotation.Beans;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.definition.Friend;
import love.forte.simbot.event.FriendMessageEvent;

@Beans
public class BarListeners {
    // 注意包路径喔！在使用mirai组件的情况下，很有可能出现导错包的情况。

    /**
     * 收到好友消息，直接复读
     */
    @Listener
    public void barListener(FriendMessageEvent event) {
        // 得到事件中的好友对象
        final Friend friend = event.getFriend();
        // 向这个好友发送消息。发送内容即为当前事件中的消息。
        friend.sendBlocking(event.getMessageContent());
    }

    /**
     * 当好友消息包含 {@code "喵"} 的时候，回复三句 {@code "喵喵喵"} 。
     */
    @Listener
    @Filter(value = "喵", matchType = MatchType.TEXT_CONTAINS)
    public void tarListener(FriendMessageEvent event) {
        final Friend friend = event.getFriend();
        // 回复三句"喵喵喵"
        friend.sendBlocking("喵喵喵");
        friend.sendBlocking("喵喵喵");
        friend.sendBlocking("喵喵喵");
    }
}
```

:::info 扫描依据

注意观察 `@Beans`。包路径扫描时，只会扫描加载标记了 `@Beans` 的类。

同样的，注意观察 `@Listener`。只有标记了 `@Listener` 的 **公开函数** 才会被作为监听函数解析。

:::

## Bot信息

在 boot 中，在存在多个组件的情况下，你可以通过 `*.bot*` 格式的配置文件来提供不同组件下的bot信息配置。

默认情况下，你需要在你的资源路径中的 `simbot-bots` 目录下配置你的 `*.bot*` 配置文件，例如 `simbot-bots/bot1.bot` 、 `simbot-bots/bot2.bot.json` 等。
`Boot` 模块会根据你的配置文件中指定的组件信息寻找当前环境中对应的组件并进行注册。

:::note 自定义扫描

如上文所述，默认的bot配置扫描规则为 `simbot-bots/*.bot*`。但是你同样可以通过 `@SimbootApplication` 的参数修改这一默认值：
```java title='example/BootApp.java'
@SimbootApplication(botResources = "bots/bot-*.bot*")
public class BootApp {
    // ...
}
```
如此示例中，将bot资源扫描规则调整为了 "bots目录下、文件名开头为 `bot-`、文件扩展名开头为 `.bot`" 的文件，
例如 `bots/bot-foo.bot` 、 `bots/bot-bar.bot.json` 等。

:::

</TabItem>
</Tabs>



## Bot配置

参考 [BOT配置](../basic/bot-config)


## 启动
执行你的main函数，并观察运行情况。


## 打包
参考文档  [打包](../basic/package)

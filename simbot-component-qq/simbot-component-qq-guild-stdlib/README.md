> [!note]
> 请先阅读 [模块说明](Module.md)

> [!caution]
> stdlib 模块可能会被隐藏、废弃、移除或重组，最好不要直接使用它。

## 使用

**Gradle Kotlin DSL**

`stdlib` 模块是多平台模块，在Gradle中需要使用 `kotlin` 插件来引用正确的平台。这并不代表你需要使用Kotlin语言编写。

```kotlin
plugin {
    java // 你仍然可以使用Java
    kotlin("jvm") version "$KOTLIN_VERSION" // 比如 1.8.10
    // 或者使用其他平台，例如 kotlin("js")
}

dependencies {
    implementation("love.forte.simbot.component:simbot-component-qq-guild-stdlib:$VERSION")

    // 你需要自行选择一个想要使用的 ktor-http-client, 例如 cio 或 okhttp 等
    // 更多选择参考 https://ktor.io/docs/http-client-engines.html
    runtimeOnly("io.ktor:ktor-client-cio:$KTOR_VERSION")
}
```

**Gradle Groovy**

`stdlib` 模块是多平台模块，在Gradle中需要使用 `kotlin` 插件来引用正确的平台。这并不代表你需要使用Kotlin语言编写。

```groovy
plugin {
    java // 你仍然可以使用Java
    id "org.jetbrains.kotlin.jvm" version "$KOTLIN_VERSION" // 比如 1.8.10
    // 或者使用其他平台，例如 kotlin("js")
}

dependencies {
    implementation 'love.forte.simbot.component:simbot-component-qq-guild-stdlib:$VERSION'

    // 你需要自行选择一个想要使用的 ktor-http-client, 例如 cio 或 okhttp 等
    // 更多选择参考 https://ktor.io/docs/http-client-engines.html
    runtimeOnly 'io.ktor:ktor-client-cio:$KTOR_VERSION'
}
```

**Maven**

`api` 模块是多平台模块，在Maven中需要增加 `-jvm` 后缀来使用JVM平台库。

```xml
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-qq-guild-stdlib-jvm</artifactId>
    <version>${VERSION}</version>
</dependency>

<!--
你需要自行选择一个想要使用的 ktor-http-client, 例如 cio 或 okhttp 等
更多选择参考 https://ktor.io/docs/http-client-engines.html
-->

<dependency>
    <groupId>io.ktor</groupId>
    <artifactId>ktor-client-cio-jvm</artifactId>
    <version>${KTOR_VERSION}</version>
    <scope>runtime</scope>
</dependency>
```

## 多平台

api模块是多平台的。支持的平台如下：
- JVM
- JS
- Native ( 前往 [构建脚本](build.gradle.kts) 查看支持的目标，基本与 [ktor中client支持的平台目标](https://ktor.io/docs/client-supported-platforms.html) 一致。 )

其中，在使用native平台时请注意配置 `BotConfiguration` 中相关的引擎的使用。（可参考 [Ktor文档](https://ktor.io/docs/http-client-engines.html#limitations) ）

以 `mingwX64` 目标为例，引入并使用 [WinHttp](https://ktor.io/docs/http-client-engines.html#winhttp) 引擎后进行配置：

```kotlin
val bot = BotFactory.create(
    "APP_ID",
    "SECRET",
    "TOKEN",
) {
    
    // 其他配置...

    // api请求用的引擎。
    apiClientEngineFactory = WinHttp
    // ws使用的引擎，需要选择一个支持ws的引擎
    wsClientEngineFactory = WinHttp
}
```

## 示例

### Kotlin

```kotlin
// 获取一个Bot
val bot = BotFactory.create(
    "APP_ID",
    "SECRET",
    "TOKEN",
) {
    // 一些配置，例如订阅的事件、使用的服务器（比如切换到沙箱环境）
    this.intents = EventIntents.GuildMembers.intents + EventIntents.Guilds.intents // 订阅的事件
    useSandboxServerUrl() // 使用的服务器切换到沙箱环境
    // ...
}

// 订阅事件。事件类型参考 Signal.Dispatch 的实现类类型
// 监听Signal.Dispatch即代表监听所有事件
bot.subscribe<Signal.Dispatch> { raw ->
    // 当前函数 receiver 即为事件本体
    println(this) // -> this: 事件本体
    // 函数体参数为事件的原始JSON字符串
    println(raw)  // -> raw: JSON字符串
}

// 监听频道信息更新事件
bot.subscribe<GuildUpdate> { raw ->
    this.data.opUserId // 操作人ID

    // 事件中请求API
    val member = GetMemberApi.create(this.data.id, this.data.opUserId).requestBy(bot)
    println(member)
}

bot.subscribe { raw ->
    // 订阅所有事件
}

val me = bot.me() // 通过 me 查询bot自身的用户信息

// 启动bot，连接到websocket服务器并开始接收事件
bot.start()
```

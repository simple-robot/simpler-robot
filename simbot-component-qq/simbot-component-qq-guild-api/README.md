# api模块

> **Note**
> 请先阅读 [模块说明](Module.md)

api模块中，所有的API请求封装均在 `love.forte.simbot.qguild.api` 中，它们通常以 `Api` 结尾，例如 `GetGuildApi`。

所有的API构造方法均被隐藏，它们会各自提供自身的工厂函数，绝大多数以 `create` 命名，例如

```kotlin
val api: GetGuildApi = GetGuildApi.create("123")
```

## 使用

**Gradle Kotlin DSL**

`api` 模块是多平台模块，在Gradle中需要使用 `kotlin` 插件来引用正确的平台。这并不代表你需要使用Kotlin语言编写。

```kotlin
plugin {
  java // 你仍然可以使用Java
  kotlin("jvm") version "$KOTLIN_VERSION" // 比如 1.8.10
  // 或者使用其他平台，例如 kotlin("js")
}

dependencies {
    implementation("love.forte.simbot.component:simbot-component-qq-guild-api:$VERSION")

    // 你需要自行选择一个想要使用的 ktor-http-client, 例如 cio 或 okhttp 等
    // 更多选择参考 https://ktor.io/docs/http-client-engines.html
    runtimeOnly("io.ktor:ktor-client-cio:$KTOR_VERSION")
}
```

**Gradle Groovy**

`api` 模块是多平台模块，在Gradle中需要使用 `kotlin` 插件来引用正确的平台。这并不代表你需要使用Kotlin语言编写。

```groovy
plugin {
  java // 你仍然可以使用Java
  id "org.jetbrains.kotlin.jvm" version "$KOTLIN_VERSION" // 比如 1.8.10
  // 或者使用其他平台，例如 kotlin("js")
}

dependencies {
    implementation 'love.forte.simbot.component:simbot-component-qq-guild-api:$VERSION'

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
    <artifactId>simbot-component-qq-guild-api-jvm</artifactId>
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

其中，在使用native平台时请注意配置构造 `HttpClient` 时引擎的使用。（可参考 [Ktor文档](https://ktor.io/docs/http-client-engines.html#limitations) ）

以 `mingwX64` 目标为例，引入并使用 [WinHttp](https://ktor.io/docs/http-client-engines.html#winhttp) 引擎后进行配置：

```kotlin
val client = HttpClient(WinHttp) { ... }
```

## 使用示例

```kotlin
// 用于请求的 client 实例
val client: HttpClient = HttpClient(...) { ... }

val token: String = "你的请求用token" // token    

// 得到一个api请求对象，此处为 获取Guild列表 API
val api = GetBotGuildListApi.create(before = null, after = null, limit = 10)

// api.request 发起请求并得到结果
// 如果失败可以捕获 QQGuildApiException 获取详情
val guildList: List<SimpleGuild> = api.request(
    client = client,
    server = QQGuild.SANDBOX_URL, // 请求server地址. 你可以通过 QQGuild.URL 得到一个官方的正式环境地址，或者其他自定义地址。
    token = token,
    decoder = Json // 可以省略
)

// 使用结果
guildList.forEach { guild ->
    println(guild)
}
```

## API实现情况

下述列表基本与 [QQ频道机器人文档](https://bot.q.qq.com/wiki/develop/api/) 中内容对应，如有遗漏或错误还望谅解并可通过 [issues](https://github.com/simple-robot/simbot-component-qq-guild/issues) 反馈。

- [x] 用户 API
- [x] 频道 API
- [x] 子频道 API
- [x] 成员 API
- [x] 频道身分组 API
- [x] 子频道权限 API
- [x] 消息 API
- [x] 消息频率 API
- [x] 私信 API
- [x] 禁言 API
- [x] 公告 API
- [x] 精华消息 API (未测试)
- [x] 日程 API (未测试)
- [ ] 表情表态 API
- [ ] 音频 API
- [x] 帖子 API
- [x] API接口权限 API
- [x] WebSocket API
- [x] QQ群聊
- [x] C2C单聊

# simple-component-kook-api

> [!note]
> 请先阅读 [模块说明](Module.md)

> [!note]
> 更详细的快速开始建议前往阅读 [组件手册](https://component-kook.simbot.forte.love/docs/quick-start/api) 中的相关章节。


api模块中，所有的API请求封装均在 `love.forte.simbot.kook.api` 中，它们通常以 `Api` 结尾，例如 `GetGuildListApi`。

所有的API构造方法均被隐藏，它们会各自提供自身的工厂函数，绝大多数以 `create` 命名，例如

```kotlin
val api: GetGuildListApi = GetGuildListApi.create(...)
```

## 使用

**Gradle Kotlin DSL**

`api` 模块是多平台模块，在Gradle中需要使用 `kotlin` 插件来引用正确的平台。这并不代表你需要使用Kotlin语言编写。

```kotlin
plugin {
  java // 你仍然可以使用Java
  kotlin("jvm") version "$KOTLIN_VERSION" // 比如 1.9.10
  // 或者使用其他平台，例如 kotlin("js")
}

dependencies {
    implementation("love.forte.simbot.component:simbot-component-kook-api:$VERSION")

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

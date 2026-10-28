# core

## 使用

<details open><summary><b>Gradle Kotlin DSL</b></summary>

```kotlin
implementation("love.forte.simbot:simbot-core:$SIMBOT_VERSION") // 必须显式指定simbot相关依赖比如此核心库或spring-boot-starter
implementation("love.forte.simbot.component:simbot-component-qq-guild-core:$VERSION")
```

</details>


<details><summary><b>Gradle Groovy</b></summary>

```groovy
implementation 'love.forte.simbot:simbot-core:$SIMBOT_VERSION' // 必须显式指定simbot相关依赖，如此核心库或spring-boot-starter
implementation 'love.forte.simbot.component:simbot-component-qq-guild-core:$VERSION'
```

</details>

<details><summary><b>Maven</b></summary>

```xml
<!-- 必须显式指定simbot相关依赖，如此核心库或spring-boot-starter -->
<dependency>
    <groupId>love.forte.simbot</groupId>
    <artifactId>simbot-core-jvm</artifactId>
    <version>${SIMBOT_VERSION}</version>
</dependency>
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-qq-guild-core-jvm</artifactId>
    <version>${VERSION}</version>
</dependency>
```

</details>

## 命名说明

在QQ频道组件中，所有相关的类型（包括但不限于事件、频道、子频道等相关对象）均会以 `QG` 为前缀命名 (例如 `QGGuild` 、`QGBot`)，代表 `QQ-Guild`。
其中除了 `Component` 的实现 `QQGuildComponent`。组件的实现使用全名 `QQGuild` 作为前缀。

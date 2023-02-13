---
sidebar_position: 20
title: 序列化
tags: [消息]
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

simbot通过 [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization) 完成标准序列化。
`kotlinx-serialization` 无关数据结构，你可以通过其将消息序列化为任何 [支持的](https://github.com/Kotlin/kotlinx.serialization/tree/master/formats)
结果。本章节内我们会以 `JSON` 为例。

## 标准消息

首先，如果需要使用 `kotlinx-serialization`，首先你要确定需要序列化对象的范围。毕竟消息对象接口 `Message.Element` 无法预知所有其实现类，
因此所有组件在提供消息实现的同时，也需要提供其对应的多态序列化信息，也就是 **`SerializersModule`**。

在simbot中提供的**标准消息**实现可以通过 `Messages.serializersModule` 获取。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val module = Messages.serializersModule
```

</TabItem>
<TabItem value="Java">

```java
SerializersModule module = Messages.getSerializersModule();
```

</TabItem>
</Tabs>

当你想要序列化一个仅包含了**标准消息**的消息链，那么就可以直接使用了。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val json = Json {
    // 指定序列化信息
    serializersModule = Messages.serializersModule
    // config...
    isLenient = true
}
// 仅包含标准消息的消息链
val messages: Messages = "你好".toText() + AtAll + Face(123.ID)

val jsonStr = json.encodeToString(Messages.serializer, messages)
```

</TabItem>
<TabItem value="Java">

在Java中，想要构建一个 `kotlinx.serialization.json.Json` 会相对比较困难。simbot内提供了一个简易的工具 `MessageSerializationUtil` 
来辅助Java开发者来处理序列化的情况。

```java
MessagesBuilder builder = new MessagesBuilder();
// 构建一个Json序列化器, 并配置序列化信息和Json自身的配置
Json json = MessageSerializationUtil.createJson(null, (config) -> {
    // isLenient = true
    config.setLenient(true);
    // config...
}, Messages.getSerializersModule());

// 仅包含标准消息的消息链
Messages messages = builder.text("你好").atAll().face(Identifies.ID(123)).build();

String jsonStr = json.encodeToString(Messages.getSerializer(), messages);
```

</TabItem>
</Tabs>

上述示例的序列化结果如下：
```json
[{"type":"m.std.text","text":"你好"},{"type":"m.std.atAll"},{"type":"m.std.face","id":"123"}]
```

反之亦然。反序列化也通过 `kotlinx-serialization` 完成。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val json = Json {
    // config...
    isLenient = true
    // 记得指定序列化信息
    serializersModule = Messages.serializersModule
}
val jsonStr = """[{"type":"m.std.text","text":"你好"},{"type":"m.std.atAll"},{"type":"m.std.face","id":"123"}]"""

val messages = json.decodeFromString(Messages.serializer, jsonStr)
```

</TabItem>
<TabItem value="Java">

在Java中，想要构建一个 `kotlinx.serialization.json.Json` 会相对比较困难。simbot内提供了一个简易的工具 `MessageSerializationUtil`
来辅助Java开发者来处理序列化的情况。

```java
String jsonStr = "[{\"type\":\"m.std.text\",\"text\":\"你好\"},{\"type\":\"m.std.atAll\"},{\"type\":\"m.std.face\",\"id\":\"123\"}]";

// 构建一个Json序列化器, 并配置序列化信息和Json自身的配置
Json json = MessageSerializationUtil.createJson(null, (config) -> {
    // isLenient = true
    config.setLenient(true);
    // config...
}, Messages.getSerializersModule());

Messages messages = json.decodeFromString(Messages.getSerializer(), jsonStr);
```

</TabItem>
</Tabs>

你可能注意到了，在进行序列化或反序列化时，总会使用 `Messages.serializer` 并将结果序列化为一个**列表**（如JsonArray）。

`Messages.serializer` 为 `Messages` 的序列化实现，它会将 `Messages` 视为一个 `Message.Element` 的集合来看待。

## 组件消息

每个组件基本上都会有它们自己专属的消息类型实现，因此当你的程序包含一个或多个组件中时，只使用标准消息的序列化信息 `Messages.serializersModule` 是不够的。

**绝大多数情况下**，一个组件会提供其专属的消息序列化信息，并且 `Component` 类型也要求其实现类必须提供 `componentSerializersModule` 属性。
由simbot所提供的组件中，消息序列化信息会通过 `Component` 的实现以及静态属性等多个方式对外提供。举个例子：

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val serializersModule = SerializersModule {
        include(Messages.serializersModule)
        // Kook组件的序列化信息
        include(KookComponent.messageSerializersModule)
        // Mirai组件的序列化信息
        include(MiraiComponent.messageSerializersModule)
    }

val json = Json {
    this.serializersModule = serializersModule
}
```

</TabItem>
<TabItem value="Java">

```java
// 合并标准消息的序列化信息 和 Kook组件的序列化信息
SerializersModule module = SerializersModuleKt.plus(Messages.getSerializersModule(), KookComponent.getMessageSerializersModule());
// 再合并进Mirai组件的序列化信息
module = SerializersModuleKt.plus(module, MiraiComponent.getMessageSerializersModule());
 
// 构建一个Json序列化器, 并配置序列化信息和Json自身的配置
Json json = MessageSerializationUtil.createJson(null, (config) -> {
    // config...
}, module);
```

```java
// 构建一个Json序列化器, 并配置序列化信息和Json自身的配置
Json json = MessageSerializationUtil.createJson(null, (config) -> {
        // config...
    }, 
    Messages.getSerializersModule(),             // 标准消息的序列化信息
    KookComponent.getMessageSerializersModule(), // Kook组件的序列化信息
    MiraiComponent.getMessageSerializersModule() // Mirai组件的序列化信息
);
```

</TabItem>
</Tabs>

除了这种提前定义好的方式以外，也可以在 `Application` 启动后来动态获取所有已加载组件中的序列化信息。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val application = createSimbotApplication(...) {
    // ...
}
// 遍历所有组件
for (component in application.environment.components) {
    // 此组件的序列化信息
    val componentSerializersModule = component.componentSerializersModule
}
```

</TabItem>
<TabItem value="Java">

```java
Application application = Applications.createSimbotApplication(..., null, (b, c) -> {
    // ...
});

List<Component> components = application.getEnvironment().getComponents();
// 遍历所有组件
for (Component component : components) {
    // 此组件的序列化信息
    SerializersModule componentSerializersModule = component.getComponentSerializersModule();
}
```

</TabItem>
</Tabs>



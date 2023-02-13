---
sidebar_position: 10
title: 标准消息
tags: [消息]
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

针对一些十分常见的消息类型，`simbot-api` 提供了一些标准定义或实现。


## PlainText
文本是最常见的消息类型。PlainText是针对于纯文本消息所提供的抽象类型。核心提供了它最基础的实现：[Text](#text)。

## Text
plainText的基础实现，用来描述一个最基础的纯文本消息。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val text1: Text = "Hello".toText()
val text2: Text = Text { "World" }
val emptyText: Text = Text()
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final Text text = Text.of("Hello");
final Text emptyText = Text.getEmptyText();
```

</TabItem>
</Tabs>

## At
`AT`（或者称之为 *艾特*、`@` ）是聊天社交软件平台十分常见的一种消息类型，其代表对某人某事进行针对性的**提醒/通知**消息。

`At` 由核心提供了基础的数据类（ `data class` ）实现，基本可以满足需要。
如果某组件存在较为复杂、无法由 `At` 满足的通知消息类型，则需要由对应组件自行实现另外的消息，并尽可能提供针对于解析 `At` 的兼容。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val at: At = At(123.ID)
val atMember: At = At(event.author().id)
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final At at = new At(Identifies.ID(123));
final At atMember = new At(event.getAuthor().getId());
```

</TabItem>
</Tabs>


## AtAll
书接上文的 `At`，对于大多数聊天社交软件来讲，`AtAll` 与 `At` 同样常见。`AtAll` 的含义与 `At` 类似，只不过 `At` 是针对一个人/事物，但是 `AtAll` 是针对所有。
核心提供的 `AtAll` 是 `object` 类型的单例。
假如某组件存在更为细粒度的“复数At”，那么需要提供额外的实现类型，并尽可能提供针对于解析 `AtAll` 的兼容。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val atAll: AtAll = AtAll
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final AtAll atAll = AtAll.INSTANCE;
```

</TabItem>
</Tabs>


## Image

图片类型也是非常常见的类型之一，代表一个任意的图片消息。核心所提供的 `Image` 类型为抽象类型，无法直接构建。

对于图片来讲，他们通常都需要提供资源（例如网络资源、本地文件资源）后上传的目标服务器，
又或是指定通过一个来自服务器的指定ID，进而再发送。

### 资源图片

核心库提供了一个 `Image` 的标准实现类型：`ResourceImage`，用于提供一个资源对象并作为你想要发送的图片资源。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val image = Path("xxx/image.jpg").toResource().toImage()
```

</TabItem>
<TabItem value="Java" label="Java">

```java
Path path = Paths.get("xxx/image.jpg");
PathResource resource = Resource.of(path);
ResourceImage image = Image.of(resource);
```

</TabItem>
</Tabs>

:::caution 小心

直接使用 `ResourceImage` 作为发送用的图片对象时，你需要注意：此图片对象会在**每次**被发送时发生一次资源读取。
因此如果这个图片资源会被**频繁**使用的话，你需要考虑文件IO与性能问题。

:::

为了避免频繁的IO操作，或许你可以提前将图片文件读取为字节数组。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val image = Path("xxx/image.jpg").let {
    it.readBytes().toResource(it.name).toImage()
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
Path path = Paths.get("xxx/image.jpg");
byte[] bytes = Files.readAllBytes(path);
ByteArrayResource resource = Resource.of(bytes, path.toString());
ResourceImage image = Image.of(resource);
```

</TabItem>
</Tabs>

:::caution 一波未平

这样的确会避免更多的IO操作，但是我想你应当明白：这会占用大量的内存资源。

:::

为了解决上述各方案中出现的问题，最好的解决办法即为先将图片上传到服务器，然后直接使用这个已经上传的图片资源对象。
那么如何上传图片到服务器呢？

**实际上，核心模块并没有为这个行为提供规范操作**。"将图片上传到服务器"（或者说"得到一个可复用的高级图片对象"）的方式与形式完全**由组件自行决定**。

以 [mirai组件](https://github.com/simple-robot/simbot-component-mirai) 为例，
在mirai中上传图片需要提供一个具体的目标对象（例如群对象、好友对象等），因此
将图片上传到服务器的方式是通过 `MiraiSendOnlyImage.upload` 实现的：

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
@Listener
suspend fun onEvent(event: MiraiGroupMessageEvent) {
    val sendOnlyImage = MiraiSendOnlyImage.of(Path("xxx/image.jpg").toResource())
    val uploadedImage = sendOnlyImage.upload(event.group());
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
@Listener
public void onEvent(MiraiGroupMessageEvent event) {
    Path path = Paths.get("xxx/image.jpg");
    MiraiSendOnlyImage sendOnlyImage = MiraiSendOnlyImage.of(Resource.of(path));
    sendOnlyImage.uploadBlocking(event.getGroup());
}
```

</TabItem>
</Tabs>

而以 [Kook组件](https://github.com/simple-robot/simbot-component-kook) 为例，
在 Kook 中图片资源需要提前通过 [媒体模块](https://developer.kookapp.cn/doc/http/asset)
上传为媒体资源，然后再以上传后的链接地址为ID进行发送。这一操作可以通过 `KookComponentBot.uploadAssetImage` 
来完成：

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
@Listener
suspend fun onEvent(event: KookMessageEvent.Channel) {
    val image = event.bot.uploadAssetImage(Path("xxx/image.jpg").toResource())
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
@Listener
public void onEvent(KookMessageEvent.Channel event) {
    Path path = Paths.get("xxx/image.jpg");
    event.getBot().uploadAssetImageBlocking(Resource.of(path));
}
```

</TabItem>
</Tabs>

可以发现，上述各组件示例中监听的事件类型都是一些组件中提供的**专属类型**（比如mirai组件中的 `MiraiGroupMessageEvent` 或 `KookMessageEvent.Channel` 等），
因为这些API都是由组件所提供的额外API。如果你的事件监听使用的是标准API，那么你可能需要多一步类型判断的操作。

:::info 更多的标准API

这没说并不是代表使用标准API不好，相反地，我们一直建议开发者尽可能地去使用标准API（例如 `GroupMessageEvent`），并只在较小范围内
使用针对组件的额外API。更多的使用标准API可以让你的整体代码拥有更高的兼容性。

当你需要增加一个协同的组件或迁移当前程序到另一个组件上时，更多的使用标准API会让这个过程的成本大大降低。

当然，凡事都要辩证的去看待。如果你很明确你的组件使用并且不会出现上述那些可能，放手去做也不会有什么大问题。

:::

总而言之，不同的组件中对于一个具体的、可复用的图片对象的定义与获取方式都是不同的，因此你需要根据你所使用的具体组件来选择最为合适的方案。

:::note 或许能更好?

我们未来可能会继续调整、优化相关的API，说不定未来的版本会有既能够在组件间通用又不会有过多损耗的标准API诞生。

如果你有好的想法或者思路，非常欢迎来[社区](https://github.com/orgs/simple-robot/discussions)点醒我们！

:::

### ID图片

上文中介绍的是通过一个资源发送图片，而在这里则会简单讲述如何直接通过一个具体的ID构建图片消息。

绝大多数情况下，一个具体的图片对象（一般指从服务器接收到的图片、已经上传过的图片等）应当拥有一个**唯一标识**，也就是一个ID。
虽然核心库中没有定义对图片上传的API，但是为 `Bot` 定义了通过 `ID` 解析并构建一个 `Image` 的API：


<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val bot: Bot = ...
val image = bot.resolveImage("xxxx".ID)
```

</TabItem>
<TabItem value="Java" label="Java">

```java
Bot bot = ...;
ID id = Identifies.ID("xxxxx");
Image<?> image = bot.resolveImageBlocking(id);
```

</TabItem>
</Tabs>

那么你可能就会有一个疑问：**图片ID从何而来？** 
这个问题的答案很简单：**不确定**。

一个图片的ID通常代表为它在服务器上的唯一标识，那么能够决定它的只有服务器。不同的组件对接不同的平台，
因此每个组件下对于图片ID的格式、获取方式等都会有所不同，甚至有可能会有不支持ID图片的组件。

以 [mirai组件](https://github.com/simple-robot/simbot-component-mirai) 为例，其支持的图片ID是由mirai框架内进行处理的。
而以 [Kook组件](https://github.com/simple-robot/simbot-component-kook) 为例的话，图片的ID则是通过 [媒体模块](https://developer.kookapp.cn/doc/http/asset)
上传后的链接地址。

因此需要根据所使用的具体组件并结合相关的文档注释说明来使用。不过直接通过 `Bot.resolveImage(ID)` 来构建图片对象的情况并不是很常见。

## Emoji
Emoji是一个 _保留类型_ ，它类似于 [Face](#face)，用来表示一个 `emoji`。但是通常情况下，你并不需要使用 `Emoji` 来发送 `emoji`，
因为从2010年开始 `Unicode` 便开始收录emoji编码，你可以直接将 `emoji` 表情作为字符串发送。

那么 `Emoji` 现在更多的用于表示一些平台下的特殊表情，例如只能在一定范围内使用的 `emoji`，比如 [腾讯频道组件](../../component-overview/tencent-guild) 中用于reaction消息的表情。

:::note

大多数情况下，你可能不会使用 `Emoji` 消息类型。

:::


<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val emoji: Emoji = Emoji(123.ID)
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final Emoji emoji = new Emoji(Identifies.ID(123));
```

</TabItem>
</Tabs>

## Face
一个**表情**。`Face` 所代表的通常为一些对应组件平台下所属的表情。这些表情大多数情况下都可以直接使用 `ID` 来进行表示与定位。
假如平台的表情较为复杂，或者存在很多不同种类的表情消息，则需要由组件提供其他额外的实现，并尽可能提供针对于解析 `Face` 的兼容。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val face: Face = Face(123.ID)
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final Face face = new Face(Identifies.ID(123));
```

</TabItem>
</Tabs>




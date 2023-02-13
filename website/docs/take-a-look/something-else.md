---
sidebar_position: 40
title: 万花丛中
---


import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## 从事件中获取对象
### 好友

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
@Listener
suspend fun FriendEvent.event() {
	val friend = friend()
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
@Listener
public void event(FriendEvent event) {
    Friend friend = event.getFriend();
}
```

</TabItem>
</Tabs>



### 群
<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
@Listener
suspend fun GroupEvent.event() {
 	val group = group()   
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
@Listener
public void event(GroupEvent event) {
    Group group = event.getGroup();
}
```

</TabItem>
</Tabs>


### 频道与子频道
<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
@Listener 
suspend fun GuildEvent.event() {
	val guild = guild()
    val channels: Items<Channel> = gulid.channels 
} 
```

</TabItem>
<TabItem value="Java" label="Java">

```java
@Listener
public void event(GuildEvent event) {
    Guild guild = event.getGuild();
    Items<? extends Channel> channels = guild.getChannels();
} 
```

</TabItem>
</Tabs>


## 从对象中获取属性
### 好友属性
<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val id = friend.id
val username = friend.username
val remark = friend.remark
val avatar = friend.avatar
```

</TabItem>
<TabItem value="Java" label="Java">

```java
ID id = friend.getId();
String username = friend.getUsername();
String remark = friend.getRemark();
String avatar = friend.getAvatar();
```

</TabItem>
</Tabs>



## 延时发送&动态参数
<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
@Filter("我叫{{name}}")
@Listener
suspend fun FriendMessageEvent.listen(@FilterValue("name") name: String) {
    val friend = friend()
    // 当然，不异步也行
    bot.launch {
        delay(3000)
        friend.send("Hello, $name")
    }
}
```

</TabItem>
<TabItem value="Java" label="Java">

:::caution 注意

java中不建议使用 `Thread.sleep(...)` 来达成延迟效果。

:::

```java
@Filter("我叫{{name}}")
@Listener
public void listen(FriendMessageEvent event, @FilterValue("name") String name) throws Exception {
    Friend friend = event.getFriend();
    // 部分类型(比如「Bot」)提供了面向Java用户使用的非阻塞延迟api, 并返回得到 DelayableCompletableFuture 对象.
    // 对于 DelayableCompletableFuture 类型，你可以将它视为一个拥有 `delay` api的CompletableFuture.
    event.getBot()
            .delay(Duration.ofSeconds(3), () -> {
                // 延迟 「3s」, 然后发送消息.
                friend.sendBlocking("Hello, " + name);
            }).delay(3000, () -> {
                // 再延迟「3000ms」, 输出日志
                logger.info("发送消息3秒后");
            });
}
```

</TabItem>
</Tabs>




## 特殊消息
### 上传并发送图片
<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
@Listener
suspend fun FriendMessageEvent.listen() {
    val imgPath = Path("img/example.png")
    val imgResource = Resource.of(imgPath)
    val img = imgResource.toImage()

    // send img to friend
    friend().send(img)
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
@Listener
public void listen(FriendMessageEvent event) {
    PathResource resource = Resource.of(Paths.get("image.png"));
    ResourceImage resourceImage = Image.of(resource);
    event.getFriend().sendBlocking(resourceImage);
}
```

</TabItem>
</Tabs>



### 群里at + 文本
<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
@Listener
suspend fun GroupMessageEvent.listen() {
    val authorId = author().id
    val at = At(authorId)
    
    group().send(at + "你好?".toText())
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
@Listener
public void listen(GroupMessageEvent event) {
    ID authorId = event.getAuthor().getId();
    At at = new At(authorId);
    
    Messages messages = Messages.toMessages(at, Text.of(" 你好?"));
    
    event.getGroup().sendBlocking(messages);
}
```

</TabItem>
</Tabs>







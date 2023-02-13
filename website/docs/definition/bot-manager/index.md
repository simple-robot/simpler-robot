---
title: BOT管理器
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

在simbot3中，所有的 `Bot` 都应该由 `BotManager` 进行管理，所有的 `BotManager` 都应由 `OriginBotManager` 进行管理。

## OriginBotManager
`OriginBotManager` 是所有 `BotManager` 实例的统一管理类，在正常情况下，所有的 `BotManager` 实现类在新建实例的时候（new）都会将自身交由 `OriginBotManager` 进行统一管理。<br />
`OriginBotManager` 内部不会持有这些manager，当一个 `BotManager` 执行了 `cancel` 或被垃圾回收后，`OriginBotManager` 中将无法再获取到它。

通过 `OriginBotManager`，使用者可以在任何事件获取到目前处于活跃状态中的任何 `BotManager`，进而得到任何活跃状态的 `Bot` 实例。

`OriginBotManager` 本身实现了 `Set<BotManager<*>>`，因此你可以将其视为一个 `Set` 使用 —— 当然，你不能直接对它进行修改操作。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
OriginBotManager.forEach {
    println("BotManager: $it")
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java
for(BotManager<?> it : OriginBotManager) {
    System.out.println("BotManager: " + it)
}
```

</TabItem>
</Tabs>


除了Set提供的api以外，`OriginBotManager` 提供了一些额外的API使得可能更快捷的进行某些操作：


<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
// 根据组件获取所有的管理器。假设这里获取所有的Mirai组件。
val managers: List<BotManager<*>> = OriginBotManager.getManagers(MiraiComponent.component)


// 通过ID和组件信息直接定位一个Bot对象。
val bot: Bot? = OriginBotManager.getBot(123.ID, TencentGuildComponent.component)


// 进行关闭操作。注意！关闭操作将会关闭所有被管理的botManager，并且将无法在继续构建新的管理器。
// 除非必要，否则你不需要关心这个方法，这一般是在系统关闭的hook之类的地方使用。
OriginBotManager.cancel()
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final OriginBotManager manager = OriginBotManager.INSTANCE;

// 得到对应组件下的所有manager。
// 实际上SimbotComponent不会有所属botManager，此处仅做示例。
final List<BotManager<?>> managers = manager.getManagers(SimbotComponent.INSTANCE);

// 根据ID和组件信息得到对应的Bot。
final ID botId = Identifies.ID(123); // 你bot的ID，可以是数字，或者字符串等。
final Bot bot = manager.getBot(botId, SimbotComponent.INSTANCE);
```

</TabItem>
</Tabs>


:::caution

`OriginBotManager` 无关任何环境，属于 **全局** 性API。你应该谨慎考虑是否应该使用任何 **全局** 相关的API，并且这些API未来都有可能发生变更。

我们建议你在更合适的环境中获取并使用 `BotManager`。
在simbot3中，`BotManager` 与 `Bot`， 或者说整个启动流程（在基础的 **core** 模块的角度上） 息息相关。

因此你可以通过 `Bot` 很轻易地得到其对应/所属的 `BotManager`，并在尽量避免使用 `OriginBotManager` 的情况下进行操作：


<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin
/** 处理一个群消息事件 */
suspend fun GroupMessageEvent.processEvent() {
    // 得到事件中bot所属的manager
    val manager: BotManager<out Bot> = bot.manager
    // 得到当前管理器的所有bot
    val all: List<Bot> = manager.all()
}
```

</TabItem>
<TabItem value="Java">

```java
/** 处理一个群消息事件 */
public void processEvent(GroupMessageEvent event) {
    // 得到此事件对应的bot
    final Bot bot = event.getBot();

    // 得到bot所属的manager
    final BotManager<? extends Bot> manager = bot.getManager();

    // 得到这个botManager中的所有Bot
    final List<? extends Bot> allBot = manager.all();
}
```

</TabItem>
</Tabs>

:::

## BotManager
`BotManager<B extends Bot>` 顾名思义即对 `Bot` 的管理器，每个bot都应属于一个 `BotManager`。

### 获取API
`BotManager` 提供了一些用于获取 `Bot` 的API: `all` 和 `get`。


<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin
val manager: BotManager<*> = ...

// 获取所有Bot，以序列Sequence的形式返回
val all: List<Bot> = manager.all()

// 获取指定的Bot
val bot: Bot? = manager.get(123.ID)
```

</TabItem>
<TabItem value="Java">

```java
final BotManager<? extends Bot> manager = ...;

// 获取所有的Bot，作为Stream返回
final List<? extends Bot> allBot = manager.all();
        
// 根据ID获取对应的Bot
final Bot bot = manager.get(Identifies.ID(123));
```

</TabItem>
</Tabs>

### 注册API
所有的BotManager都至少提供了一个注册函数 register(BotVerifyInfo)。但实际上并不推荐使用者通过这个函数来注册BOT。
此函数中的参数 BotVerifyInfo 是通过读取 *.bot 文件而得到的以Json配置为主的信息类，但是很多情况下注册一个Bot并不需要一个Json配置。

那么该如何注册呢？因为simbot3支持多组件协同，因此首先你需要知道你要注册bot的目标组件下的BotManager的具体类型，然后获取它。这里以 Mirai组件 为例：

```kotlin
val miraiManager = OriginBotManager.filterIsInstance<MiraiBotManager>().first()
```

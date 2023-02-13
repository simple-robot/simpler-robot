---
sidebar_position: 10
title: Application
---

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

`Application` 即为对一个simbot应用的描述，其包含了一个应用范围内安装的组件、事件提供者以及一些所需属性等信息。

## 定义

了解 `Application` 的第一步，先让我们来简单了解一下 `Application` 的**定义**。

```kotlin
public interface Application : CoroutineScope {

    /**
     * 当前 [Application] 的部分属性。
     */
    public val environment: Environment

    /**
     * 在进行构建时所使用的配置信息。
     * 构建完成后可以得到，但是尽可能不要进行修改操作。这可能没有意义，也可能会导致意外的错误。
     */
    public val configuration: ApplicationConfiguration

    /**
     * 得到当前 [Application] 最终的 [EventListenerManager].
     */
    public val eventListenerManager: EventListenerManager

    /**
     * 当前应用下的所有 [事件提供者][EventProvider]。
     */
    public val providers: List<EventProvider>

    /**
     * 当前应用下的所有 [bot管理器][BotManager]。
     */
    public val botManagers: BotManagers

    /**
     * 挂起此应用直至其被终止。
     */
    public suspend fun join()

    /**
     * 终止当前应用，并关闭其中所有可能的资源。
     * [Application] 被终止后将不能再次启动。
     */
    public suspend fun shutdown(reason: Throwable? = null)
}
```

## 构建

想要构建一个 `Application`, 需要使用 `createSimbotApplication` 函数。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
createSimbotApplication(FACTORY, configurator = { /* config function block */ }) {
    // build function block
}
```

</TabItem>
<TabItem value="Java">

在Java中，`createSimbotApplication` 是 `Applications` 的静态函数。

```java
xxxApplication application = Applications.createSimbotApplication(
        FACTORY,
        (config) -> { 
          // config function block 
        },
        (builder, config) -> {
          // build function block
        });
```

</TabItem>
</Tabs>

可以看出，`createSimbotApplication` 需要三个参数：

第一个为目标 `Application` 的工厂类型；

第二个为进行构建之前的属性配置；

第三个则为针对当前Application的构建内容。

### 构建工厂

我们上文提到了构建`Application`的**"工厂"**，这其实就是代表为 `ApplicationFactory`
类型的实例，simbot默认提供了三个实现，其中最常用的即为 `Simple` 工厂：

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val application = createSimbotApplication(
          // This is success
          Simple, 
          configurator = { /* config function block */ }
      ) {
          // build function block
      }
```

</TabItem>
<TabItem value="Java">

在Java中，`createSimbotApplication` 是 `Applications` 的静态函数。

```java
SimpleApplication application = Applications.createSimbotApplication(
        // This is success
        Simple.INSTANCE,
        (config) -> { 
          // config function block 
        },
        (builder, config) -> {
          // build function block
        });
```

</TabItem>
</Tabs>

`Simple` 工厂构建而得到的 `Application` 为 `SimpleApplication`，也是simbot默认提供的最基础的 `Application`。

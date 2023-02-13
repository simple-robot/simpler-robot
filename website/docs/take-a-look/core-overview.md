---
sidebar_position: 20
title: core概览
---

核心模块下所支持的更贴近原生的使用方式。


import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val application = createSimpleApplication {
    installAll() // 尝试自动加载所有组件和eventProvider
}

application.eventListenerManager.listeners { // this: EventListenerRegistrationDescriptionsGenerator
    // 注册监听函数
}

application.bots { // this: BotManagers
    // 注册bot
}

application.join() // join until the application is cancelled
```

</TabItem>
<TabItem value="Java" label="Java">

```java
// listener manager
SimpleApplication application = Applications.createSimbotApplication(
        Simple.INSTANCE,
        null,
        (builder, config) -> {
            // 尝试自动加载所有组件
            Components.installAllComponents(builder, Main.class.getClassLoader());
            // 尝试自动加载所有EventProvider
            EventProviders.installAllEventProviders(builder, Main.class.getClassLoader());
        });

// 注册监听函数
SimpleEventListenerManager eventListenerManager = application.getEventListenerManager();
eventListenerManager.register(...);

// 注册bot
BotManagers botManagers = application.getBotManagers();
Bot bot = botManagers.register(...);
// ...
bot.startBlocking(); // or startAsync

// 阻塞直到 application 被关闭
application.joinBlocking();
```

</TabItem>
</Tabs>

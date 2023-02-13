---
authors: forliy
title: 2022年第11周周报
tags: [2022周报,周报]
---

2022年第11周周报喵。

<!--truncate-->

## ✨ Simple Robot 版本发布 {#version-release}
在第11周，我们发布了 [Simple Robot](https://github.com/simple-robot/simpler-robot) 
的预览版本：[v3.0.0.preview.5.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.5.0) 。
其中比较重要的几个更新内容如下：

<!--truncate-->

### 组件机制更新
在基础的core模块下，组件将不再是“自动加载”的内容，而是需要你在构建 `coreListenerManager` 的时候主动加入的。

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin
val manager = coreListenerManager {
    install(TestComponent)
    // 或者尝试自动加载
    installAll() // 实验性的，尝试加载当前环境中的所有(支持被自动加载的)组件
}
```

</TabItem>
<TabItem value="Java">

```java
// 配置类
final CoreListenerManagerConfiguration configuration = new CoreListenerManagerConfiguration();
// 注册一个组件。大多数情况下，组件的注册器为组件的伴生对象，因此Java中可以使用 `XxxComponent.Registrar`(或者`XxxComponent.Companion`等)
configuration.install(TestComponent.Registrar);
// 或者尝试自动加载
// 这是实验性的。尝试加载当前环境中的所有(支持被自动加载的)组件
configuration.installAll(); 
CoreListenerManager.newInstance(configuration);
```

</TabItem>
</Tabs>


### 监听函数注册机制更新
现在，在基础的core模块下，更推荐在 `coreListenerManager` 配置范围内完成对监听函数的全部注册。
后期动态注册监听函数的api会暂时保留，但是不保证日后是否会过时并删除。


<Tabs groupId="code">
<TabItem value="Kotlin" default>

```kotlin
coreListenerManager {
    // 配置监听函数相关内容
    listeners {
        // 可以直接注册一个 EventListener 实例
        listener(coreListener { ... })
        // 通过DSL配置一个监听函数
        listener(FriendMessageEvent) {
            // 可以提供部分过滤器逻辑
            filter { true }
            filters {
                filter { true }
                filter { true }
                filter { true }
            }
            // 监听函数的处理逻辑
            handle { context, event ->
                // do...
                null
            }
        }
    }
}
```

</TabItem>
<TabItem value="Java">

```java
// 构建配置类
final CoreListenerManagerConfiguration configuration = new CoreListenerManagerConfiguration();
        
// 构建一个监听函数实例。
EventListener listener1 = CoreListenerUtil.newCoreListener(FriendMessageEvent.Key, (context, event) -> {
    // do..
    return null;
});

// 添加监听函数
// Java中，使用 `addListener` 是相对比较简便的方式。
configuration.addListener(listener1);

// 构建 manager
CoreListenerManager manager = CoreListenerManager.newInstance(configuration);

```

</TabItem>
</Tabs>


### 组件更新
几个组件会随之更新。详情请前往 [v3.0.0.preview.5.0 release](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0.preview.5.0) 查看。

<hr/>

## ⚙️ 开黑啦组件 {#kaiheila-component}
第11周随着上述的预览版本 `v3.0.0.preview.5.0` 更新，同时发布了一个早期阶段的新组件：[开黑啦组件](https://github.com/simple-robot/simbot-component-kaiheila) 。
现在你已经可以在 [Simple Robot Repositories](https://github.com/simple-robot) 首页中看到它的介绍和链接引导了。

开黑啦组件的目前版本 [v3.0.0.preview.5.0-0.0.2](https://github.com/simple-robot/simbot-component-kaiheila/releases/tag/v3.0.0.preview.5.0-0.0.2)
提供的内容有：
- api和stdlib（参考 [腾讯频道组件](https://github.com/simple-robot/simbot-component-tencent-guild) 的类似概念，即基础API的对接定义 ）的基本完整的实现
- 对接simbot的core模块下，针对大部分对象（`Objective`）的实现（比如 `Guild` 、`Channel`、`Member` 等）。
- 对接simbot的core模块下，针对 **消息事件** 的监听实现，针对消息发送的实现。
- 实现simbot-boot支持。

由此可见，上述内容中，开黑啦组件目前大部分内容已建设完成，其中对simbot标准事件的实现仍在继续。

开黑啦组件与腾讯频道组件十分类似，但值得一提的是，开黑啦组件相对于腾讯频道组件，它使用了另外一种 `Objective` 的操作形式。
在腾讯频道中，所有的 `Objective`（比如 `Guild` 、`Channel`、`Member` 等）都是通过与之相对应的挂起api直接调用相对应的http接口来获取的，
虽然对于同一个对象来讲只会初始化一次，但是在频繁使用的情况下仍然可能出现大量调用http api的情况，对网络要求会相对较高。

而对于开黑啦，首先由于其api的 [速率限制](https://developer.kaiheila.cn/doc/rate-limit) 机制，本身就不建议频繁调用api。因此开黑啦组件
会在 `Bot` 启动时（`Bot.start()` 时 ）去优先初始化所有的相关 `Objective` 资源并缓存，并在后续根据所接收到的事件进行相对应的缓存调整
（例如当收到群成员离开频道的事件，则移除缓存内部的对应对象）。
不论你对这种模式是否有一种熟悉的感觉，它的确可以大大减缓对http api的调用速率。
目前所面临的情况便是对这种缓存机制的稳定性测试，毕竟如果处理不当则可能出现缓存更新不及时或者出现缓存不一致的情况。当开黑啦的缓存机制足够稳定后，
腾讯频道组件也会开始考虑更新为此类型机制。

对于缓存的机制细节没有什么过多阐述的必要，只是简单的根据事件类型进行基础的增删改查，并尽可能保证其操作的线程安全性。

<hr/>


## 🏠 新的首页建设 {#homepage-built}
如你所见。当你能看到这则周报，那么你便看到了被建设的首页。未来相关文档将会于此处更新。有关文档构建工具、版权信息、相关链接指引等内容，你可以在
网页的脚注处看到。

<hr/>

## 👀 入群审核 {#group-review}
一段时间之前，我曾说过要借群友之力为入群申请建设一个答题审核功能，参与的群友已经将其构建的差不多了，这里非常感谢他们的贡献与协助！<small><i>等正式发布后我会列举参与成员信息并向他们表示感谢。</i></small>

但是目前有几大因素阻碍它的部署：
- 网站资源有限，现在需要等待资金用于升级服务器
- 项目需要配合法欧莉进行一定程度的改造
- 4月有专升本考试
- 某人截止到现在还没复习所以十分焦虑

因此对于此功能的上线，可能需要静候一段时间。




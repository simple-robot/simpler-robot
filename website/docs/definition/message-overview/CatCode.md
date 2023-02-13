---
sidebar_position: 100
title: 猫猫码?
tags: [消息]
---

猫猫码 ( `CatCode` ) 是一个具有特殊格式的字符串，是CQ码的精神延续。


[CatCode2](https://github.com/ForteScarlet/CatCode2) 重构为基于 [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) 
的多平台序列化库。

其实不难发现，猫猫码的最终数据形式，十分类似于一个存在额外参数 `code_type` 的 `properties`。
我们将 CatCode2 实现为多平台序列化库，并保留核心库及其中的高性能字符串解析器。

simbot3中的消息实体将会基于 [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) 实现序列化，
届时你想要将消息序列化为 `CatCode`、`JSON`、`properties` 或者 `ProtoBuf` , 那就可以完全看你自己的心情了。

:::info 序列化途径

猫猫码不再是simbot3中序列化消息的途径，它仅仅作为一个普通的序列化库发布。

想要在simbot3中实现消息序列化，只需要在注意消息实体是否支持序列化的前提下直接通过 [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
完成。

:::

事实上，simbot3中，所有的消息均以实体形式出现，猫猫码不再必要。有关消息相关的内容，你可以参考 [消息概述](index) 。

## 兼容

不过，对于 [mirai组件](https://github.com/simple-robot/simbot-component-mirai)，
其提供了一个用于简单兼容猫猫码的额外模块：[simbot-component-mirai-extra-catcode](https://github.com/simple-robot/simbot-component-mirai/tree/main/simbot-component-mirai-extra-catcode)。

:::info 只有mirai?

只有mirai组件提供与猫猫码的兼容模块是因为在 simbot2 时，只存在mirai组件这一个组件，因此所有猫猫码的遗留都是通过mirai组件而产生的。

我们更建议在针对新版本的猫猫码序列化库出现之前，直接使用常规的[序列化](message-serialization)方案。

:::

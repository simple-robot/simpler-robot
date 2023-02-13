---
authors: forliy
title: 2022年第33周周报
tags: [2022周报,周报]
---


2022年第33周周报喵。

<!--truncate-->

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import Label from '@site/src/components/Label'

> _今天 ——— 哦不，是昨天，2022年8月19日，又是一个值得纪念的日子。_

## 🚀 版本发布

本周, [Mirai组件][mirai-repo] 发布了版本 [`v3.0.0.0-beta-M2`][v3.0.0.0-beta-M2]，
除了常规的BUG修复以外，这其中有一些值得一提的内容：

#### 转发消息

经常会有人在一些地方发出疑问：_“应该怎么从simbot中发送‘转发消息’呢？”_ 实际上，在当他们提出此问题时，的确没有一个能够直接避免操作Mirai原生对象而进行操作的方式。
只不过，这些发出疑问的_地方_并没有能够传达意志的能力，直到...

直到(第一个)有关此问题的 [议题(#401)](https://github.com/simple-robot/simpler-robot/issues/401) 出现，
一个可能被添加的 [特性](https://github.com/simple-robot/simbot-component-mirai/issues/56) 被提上了日程。

#### 密码配置

在 `v3.0.0.0-beta-M2` 中，我们对配置文件中的**密码**配置项的配置形式做了调整。
以JSON格式为例，旧的配置方式可能是：

<Tabs groupId="weak-33-g1">
<TabItem label="明文密码" value="json-pwd-text">

```json
{
  "component": "simbot.mirai",
  "code": 123456,
  "password": "a1b2c3d4"
}
```

</TabItem>
<TabItem label="MD5密码" value="json-pwd-md5-text">

```json
{
  "component": "simbot.mirai",
  "code": 123456,
  "passwordMD5": "e807f1fcf84d112f3bb018ca6738a19f"
}
```

</TabItem>
</Tabs>

也许你感觉，这就够了，但是实际上，这还不够。对于密码的配置，可能的需求会有很多，除了上述的 **明文** 和 **MD5** 的形式以外，
也许你并不希望将密码直接记录于文本中，不论其形式。通过环境变量、虚拟机参数等方式或许会更加安心。

但是如果根据这种情况，只是在上述配置中继续追加额外的**可选参数项**，例如 `passwordEnvName` 、`passwordPropName` 等诸如此类的属性，
则会导致其中的隐患逐渐增大，例如 `"当同时配置了两个或多个冲突的属性，如何选择或解决冲突？"`、 `"如果继续追加属性，如何处理增加的判断逻辑？"` 等等。
这对于使用或者后续的扩展都不是一个很好的选择。

因此，我们选择改变配置方式，提供 `passwordInfo` 属性替代并表示所有支持的密码配置形式 ([#65](https://github.com/simple-robot/simbot-component-mirai/pull/65)) ：

<Tabs groupId="weak-33-g1">
<TabItem label="明文密码" value="json-pwd-text">

```json
{
  "component": "simbot.mirai",
  "code": 123456,
  "passwordInfo": {
    "type": "text",
    "text": "password"
  }
}
```

</TabItem>
<TabItem label="MD5密码" value="json-pwd-md5-text">

```json
{
  "component": "simbot.mirai",
  "code": 123456,
  "passwordInfo": {
    "type": "md5_text",
    "md5": "e807f1fcf84d112f3bb018ca6738a19f"
  }
}
```

</TabItem>
<TabItem label="MD5密码" value="json-pwd-md5-bytes">

```json
{
  "component": "simbot.mirai",
  "code": 123456,
  "passwordInfo": {
    "type": "md5_bytes",
    "md5": [-24, 7, -15, -4, -14, 45, 18, 47, -101, -80, 24, -54, 102, 56, -95, -97]
  }
}
```

</TabItem>
<TabItem label="明文密码(环境变量)" value="json-pwd-text-env">

```json
{
  "component": "simbot.mirai",
  "code": 123456,
  "passwordInfo": {
    "type": "env_text",
    "prop": "xxx",
    "env": "xxx"
  }
}
```

</TabItem>
<TabItem label="MD5密码(环境变量)" value="json-pwd-md5-text-env">

```json
{
  "component": "simbot.mirai",
  "code": 123456,
  "passwordInfo": {
    "type": "env_md5_text",
    "prop": "xxx",
    "env": "xxx"
  }
}
```

</TabItem>
</Tabs>

通过 `passwordInfo.type` 来选择一个你选择的具体的配置形式，并由此引申出后续你所需要提供的具体属性。
例如当 `type` 值为 `text` 时，你就需要提供一个这个类型的配置所需要的 `text` 属性来提供明文密码。

对于可选的类型和对应属性的相关介绍，你可以查阅源码注释、API文档或者 [Mirai组件-bot配置文件](/docs/component-overview/mirai/bot-config#passwordinfoconfiguration)。

#### 其他...

当然，除了上述变更以外，此版本还提供了很多特性，你可以参考 [更新日志][v3.0.0.0-beta-M2] 来了解它们。

[mirai-repo]: https://github.com/simple-robot/simbot-component-mirai
[v3.0.0.0-beta-M2]: https://github.com/simple-robot/simbot-component-mirai/releases/tag/v3.0.0.0-beta-M2

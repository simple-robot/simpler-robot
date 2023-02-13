---
sidebar_position: 10
title: Bot配置文件
toc_max_heading_level: 4
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import Label from '@site/src/components/Label'

有关bot的配置文件相关内容，请先阅读 [**BOT配置**](../../basic/bot-config) 。

:::danger

mirai组件的[**独立文档网站**](https://component-mirai.simbot.forte.love)已经发布，当前页面将**不再更新**并暂做保留，一段时间后将会移除。

请前往mirai组件的[新文档网站](https://component-mirai.simbot.forte.love/docs/bot-config)阅读。

:::

## 架构

mirai组件为其下的BOT配置文件提供了 [`json-schema`](http://json-schema.org/) 。

> 当前 `schema` 版本：[<Label>0.1.0</Label>](/schema/component/mirai/bot/0.1.0/bot.config.json)

### 架构资源

你可以通过 [此处](/schema/component/mirai/bot/0.1.0/bot.config.json) 下载 `bot.config.json` 文件，
或者使用远程资源路径：

**`$host/schema/component/mirai/bot/0.1.0/bot.config.json`**

:::note

远程资源路径的 `$host` 即为当前站点，例如：

<https://simbot.forte.love/schema/component/mirai/bot/0.1.0/bot.config.json>

:::

### 如何使用

以 [IntelliJ IDEA](https://www.jetbrains.com/?from=simpler-robot) 为例，
对一个 `JSON` 文件使用JSON架构可以参考其 [官方文档](https://www.jetbrains.com/help/idea/json.html#ws_json_using_schemas)。

:::tip

**JSON架构** 的约束适用于 `JSON`、`YAML` 文件。

:::

## 最简配置

### 明文密码

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json title="my-bot.bot.json"
{
    "component": "simbot.mirai",
    "code": 123456789,
    "passwordInfo": {
        "type": "text",
        "text": "你的密码"
    }
}
```

</TabItem>
<TabItem value="YAML">

```yaml title='my-bot.bot.yaml'
component: "simbot.mirai"
code: 123456789
passwordInfo: !<text>
  text: "你的密码"
```

</TabItem>
<TabItem value="Properties">

```properties title='my-bot.properties'
code=123456789
passwordInfo.type=text
passwordInfo.value.text=你的密码
```

</TabItem>
</Tabs>


### MD5密码

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json title="my-bot.bot.json"
{
    "component": "simbot.mirai",
    "code": 123456789,
    "passwordInfo": {
        "type": "md5_text",
        "md5": "e807f1fcf82d132f9bb018ca6738a19f"
    }
}
```

</TabItem>
<TabItem value="YAML">

```yaml title='my-bot.bot.yaml'
component: "simbot.mirai"
code: 123456789
passwordInfo: !<md5_text>
  text: "e807f1fcf82d132f9bb018ca6738a19f"
```

</TabItem>
<TabItem value="Properties">

```properties title='my-bot.properties'
component=simbot.mirai
code=123456789
passwordInfo.type=md5_text
passwordInfo.value.text=e807f1fcf82d132f9bb018ca6738a19f
```

</TabItem>
</Tabs>



<details>
<summary>完整配置参考</summary>

> 仅供参考，以具体代码效果为准。


配置属性 `config` 下的几乎所有属性都是可选的（甚至包括 `config` 属性自己），因此你没有必要书写过于完整的配置文件。

下述的完整配置参考中，`config.deviceInfo` 将会被**省略**。

> 下述部分属性不会提供所有的可能（例如 `passwordInfo`），对所有属性的完整解释参见后续说明。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json title="my-bot.bot.json"
{
  "component": "simbot.mirai",
  "code": 123,
  "passwordInfo": {
     "type": "text",
     "text": "明文密码"
  },
  "config": {
      "workingDir": ".",
      "heartbeatPeriodMillis": 60000,
      "statHeartbeatPeriodMillis": 300000,
      "heartbeatTimeoutMillis": 5000,
      "heartbeatStrategy": "STAT_HB",
      "reconnectionRetryTimes": 2147483647,
      "autoReconnectOnForceOffline": false,
      "protocol": "ANDROID_PHONE",
      "highwayUploadCoroutineCount": 16,
      "deviceInfo": {
         "type": "auto"
      },
      "noNetworkLog": false,
      "noBotLog": false,
      "isShowingVerboseEventLog": false,
      "cacheDir": "cache",
      "contactListCache": {
        "saveIntervalMillis": 60000,
        "friendListCacheEnabled": false,
        "groupMemberListCacheEnabled": false
      },
      "loginCacheEnabled": true,
      "convertLineSeparator": true,
      "recallMessageCacheStrategyConfig": {
        "type": "invalid"
      },
      "accountSecrets": false
  }
}
```


</TabItem>
<TabItem value="YAML">

```yaml title='my-bot.bot.yaml'
component: "simbot.mirai"
code: 123456789
passwordInfo: !<text>
  text: "明文密码"
config:
  deviceInfoSeed: 1
  workingDir: "."
  heartbeatPeriodMillis: 60000
  statHeartbeatPeriodMillis: 300000
  heartbeatTimeoutMillis: 5000
  heartbeatStrategy: "STAT_HB"
  reconnectionRetryTimes: 2147483647
  autoReconnectOnForceOffline: false
  protocol: "ANDROID_PHONE"
  highwayUploadCoroutineCount: 8
  deviceInfo: !<auto>
  noNetworkLog: false
  noBotLog: false
  isShowingVerboseEventLog: false
  cacheDir: "cache"
  contactListCache:
    saveIntervalMillis: 60000
    friendListCacheEnabled: false
    groupMemberListCacheEnabled: false
  loginCacheEnabled: true
  convertLineSeparator: true
  recallMessageCacheStrategyConfig: !<invalid>
  accountSecrets: false
```

</TabItem>
<TabItem value="Properties">

```properties title='my-bot.properties'
component=simbot.mirai
code=123456789
passwordInfo.type=text
passwordInfo.value.text=明文密码
config.deviceInfoSeed=1
config.workingDir=.
config.heartbeatPeriodMillis=60000
config.statHeartbeatPeriodMillis=300000
config.heartbeatTimeoutMillis=5000
config.heartbeatStrategy=STAT_HB
config.reconnectionRetryTimes=2147483647
config.autoReconnectOnForceOffline=false
config.protocol=ANDROID_PHONE
config.highwayUploadCoroutineCount=8
config.deviceInfo.type=auto
config.noNetworkLog=false
config.noBotLog=false
config.isShowingVerboseEventLog=false
config.cacheDir=cache
config.contactListCache.saveIntervalMillis=60000
config.contactListCache.friendListCacheEnabled=false
config.contactListCache.groupMemberListCacheEnabled=false
config.loginCacheEnabled=true
config.convertLineSeparator=true
config.recallMessageCacheStrategyConfig.type=invalid
config.accountSecrets=false
```

</TabItem>
</Tabs>



</details>

## 参数释义

<table>
    <thead><tr><th>参数</th><th>类型</th><th>含义</th></tr></thead>
<tbody>
    <tr>
        <td><b>component</b> <Label>必须</Label> </td>
        <td><Label>const</Label></td>
        <td>固定值：<code>simbot.mirai</code>，代表当前配置文件是针对mirai组件的</td>
    </tr>
    <tr>
        <td><b>code</b> <Label>必须</Label> </td>
        <td><Label>integer</Label></td>
        <td>账号。</td>
    </tr>
    <tr>
        <td><s><b>password</b></s></td>
        <td><Label>string</Label></td>
        <td> <b>已弃用</b> <s>明文密码，与下面的 passwordMD5二选一。</s></td>
    </tr>
    <tr>
        <td><s><b>passwordMD5</b></s></td>
        <td><Label>string</Label></td>
        <td> <b>已弃用</b> <s>MD5加密后的密码，与上面的 password 二选一。</s></td>
    </tr>
    <tr>
        <td><b>passwordInfo</b> <Label>必须</Label> </td>
        <td><a href="#passwordinfoconfiguration"><Label>PasswordInfoConfiguration</Label></a></td>
        <td>密码配置。后续会提供详细解释。</td>
    </tr>
    <tr>
        <td><b>config</b></td>
        <td><Label>Config</Label></td>
        <td>其他详细配置</td>
    </tr>
    <tr>
        <td><s>config.<b>deviceInfoSeed</b></s></td>
        <td><Label>integer</Label></td>
        <td> <b>已弃用</b> <s>mirai配置自定义deviceInfoSeed的时候使用的随机种子。默认为1。</s></td>
    </tr>
    <tr>
        <td>config.<b>workingDir</b></td>
        <td><Label>string</Label></td>
        <td>同原生mirai配置，mirai的工作目录。默认为 <code>"."</code> 。</td>
    </tr>
    <tr>
        <td>config.<b>heartbeatPeriodMillis</b></td>
        <td><Label>integer</Label></td>
        <td>同原生mirai配置，连接心跳包周期。</td>
    </tr>
    <tr>
        <td>config.<b>statHeartbeatPeriodMillis</b></td>
        <td><Label>integer</Label></td>
        <td>同原生mirai配置，状态心跳包周期。</td>
    </tr>
    <tr>
        <td>config.<b>heartbeatTimeoutMillis</b></td>
        <td><Label>integer</Label></td>
        <td>同原生mirai配置，每次心跳时等待结果的时间。</td>
    </tr>
    <tr>
        <td>config.<b>heartbeatStrategy</b></td>
        <td><Label>enum</Label></td>
        <td>
        <p>同原生mirai配置，枚举类型。心跳策略。可选元素：</p>
        <li><code>STAT_HB</code></li> 
        <li><code>REGISTER</code></li> 
        <li><code>NONE</code></li> 
        </td>
</tr>
    <tr>
        <td>config.<b>reconnectionRetryTimes</b></td>
        <td><Label>integer</Label></td>
        <td>同原生mirai配置，最多尝试多少次重连。</td>
    </tr>
    <tr>
        <td>config.<b>autoReconnectOnForceOffline</b></td>
        <td><Label>boolean</Label></td>
        <td>同原生mirai配置，Boolean类型。在被挤下线时 (`BotOfflineEvent.Force`) 自动重连。</td>
    </tr>
    <tr>
        <td>config.<b>protocol</b></td>
        <td><Label>enum</Label></td>
        <td>
           同原生mirai配置，枚举类型。使用协议类型。可选元素： 
            <li><code>ANDROID_PHONE</code></li>
            <li><code>ANDROID_PAD</code></li>
            <li><code>ANDROID_WATCH</code></li>
            <li><code>IPAD</code></li>
            <li><code>MACOS</code></li>  
        </td>
    </tr>
    <tr>
        <td>config.<b>highwayUploadCoroutineCount</b></td>
        <td><Label>integer</Label></td>
        <td>同原生mirai配置，Highway 通道上传图片, 语音, 文件等资源时的协程数量。</td>
    </tr>
    <tr>
        <td>config.<b>deviceInfo</b></td>
        <td><Label>DeviceInfoConfiguration</Label></td>
        <td>使用的自定义设备信息配置，详见下文。</td>
    </tr>
    <tr>
        <td><s>config.<b>simpleDeviceInfo</b></s></td>
        <td><Label>object</Label></td>
        <td> <b>已弃用</b> <s>使用的自定义设备信息的简化可读版。</s></td>
    </tr>
    <tr>
        <td><s>config.<b>deviceInfoFile</b></s></td>
        <td><Label>string</Label></td>
        <td> <b>已弃用</b> <s>指定设备信息文件。</s></td>
    </tr>
    <tr>
        <td>config.<b>noNetworkLog</b></td>
        <td><Label>boolean</Label></td>
        <td>不展示mirai网络日志。默认false</td>
    </tr>
    <tr>
        <td>config.<b>noBotLog</b></td>
        <td><Label>boolean</Label></td>
        <td>不展示mirai Bot日志。默认false</td>
    </tr>
    <tr>
        <td>config.<b>isShowingVerboseEventLog</b></td>
        <td><Label>boolean</Label></td>
        <td>同原生mirai配置，是否显示过于冗长的事件日志。默认false。</td>
    </tr>
    <tr>
        <td>config.<b>cacheDir</b></td>
        <td><Label>string</Label></td>
        <td>同原生mirai配置，缓存数据目录, 相对于 <code>workingDir</code> 。</td>
    </tr>
    <tr>
        <td>config.<b>contactListCache</b></td>
        <td><Label>ContactListCacheConfiguration</Label></td>
        <td>同原生mirai配置，详见下文。</td>
    </tr>
    <tr>
        <td>config.<b>loginCacheEnabled</b></td>
        <td><Label>boolean</Label></td>
        <td>同原生mirai配置，登录缓存。开启后在密码登录成功时会保存秘钥等信息, 在下次启动时通过这些信息登录, 而不提交密码。可以减少验证码出现的频率。<br />
    秘钥信息会由密码加密保存. 如果秘钥过期, 则会进行普通密码登录。默认为true。</td>
    </tr>
    <tr>
        <td>config.<b>convertLineSeparator</b></td>
        <td><Label>boolean</Label></td>
        <td>
            同原生mirai配置，是否处理接受到的特殊换行符, 默认为 true。
            <li>若为 true, 会将收到的 CRLF(\r\n) 和 CR(\r) 替换为 LF(\n)</li>
            <li>若为 false, 则不做处理</li>
        </td>
    </tr>
    <tr>
        <td><s>config.<b>recallMessageCacheStrategy</b></s></td>
        <td><Label>enum</Label></td>
        <td>
            <b>已弃用 </b>  
            <s>
            用于 <b>消息撤回事件(<code>MiraiMessageRecallEvent</code>)</b> 的消息缓存策略。
            可选值为枚举类型 <code>MiraiBotVerifyInfoConfiguration.RecallMessageCacheStrategyType</code> 中的可选元素：
            <table>
                <thead>
                    <tr><th>元素名</th><th>释义</th></tr>
                </thead>
                <tbody>
                    <tr><td><code>INVALID</code></td><td>无效的缓存策略，即<b>不进行缓存。</b></td></tr>
                    <tr><td><code>MEMORY_LRU</code></td><td>基于内存的 LRU 缓存策略</td></tr>
                </tbody>
            </table>
            </s>
        </td>
    </tr>
    <tr>
        <td>config.<b>recallMessageCacheStrategyConfiguration</b></td>
        <td><a href="#recallmessagecachestrategyconfiguration"><Label>RecallMessageCacheStrategyConfiguration</Label></a></td>
        <td>
            用于 <b>消息撤回事件(<code>MiraiMessageRecallEvent</code>)</b> 的消息缓存策略，详见下文。
        </td>
    </tr>
    <tr>
        <td>config.<b>accountSecrets</b></td>
        <td><Label>boolean</Label></td>
        <td>
            是否禁用 `account.secrets` 的保存，默认为 `false`。
            相当于 `BotConfiguration.disableAccountSecretes()`。
        </td>
    </tr>

</tbody>
</table>

### PasswordInfoConfiguration

`PasswordInfoConfiguration` 是用于配置账号密码的配置类型。其大致结构如下：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "passwordInfo": {
    "type": "password_type",
    "paramA": "valueA",
    "paramB": "valueB"
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
passwordInfo: !<password_type>
  paramA: "valueA"
  paramB: "valueB"
```

</TabItem>
<TabItem value="Properties">

```properties
passwordInfo.type=password_type
passwordInfo.value.paramA=valueA
passwordInfo.value.paramB=valueB
```

</TabItem>
</Tabs>


上述示例中可见，`passwordInfo` 一定存在一个 `type` 属性来标记当前配置的类型。`type` 是一个具有固定可选范围的字符串值，并且 `type` 的选择会决定其他的可用属性。
下面会分别介绍所有的type以及它们对应的具体结构。

#### text {#pwd-text}

当 `type` 值为 `text` 时，代表所配置的内容为 **明文密码**。 

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "passwordInfo": {
    "type": "text",
    "text": "password"
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
passwordInfo: !<text>
  text: "password"
```

</TabItem>
<TabItem value="Properties">

```properties
passwordInfo.type=text
passwordInfo.value.text=password
```

</TabItem>
</Tabs>


#### md5_text {#pwd-md5-text}

与 [`type=text`](#pwd-text) 时类似，当 `type` 值为 `md5_text` 时，代表所配置的内容为 **MD5密码(字符串)**。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "passwordInfo": {
    "type": "md5_text",
    "md5": "e807f1fcf84d112f3bb018ca6738a19f"
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
passwordInfo: !<md5_text>
  md5: "e807f1fcf84d112f3bb018ca6738a19f"
```

</TabItem>
<TabItem value="Properties">

```properties
passwordInfo.type=md5_text
passwordInfo.value.md5=e807f1fcf84d112f3bb018ca6738a19f
```

</TabItem>
</Tabs>

#### md5_bytes

与 [`type=md5_text`](#pwd-md5-text) 时类似，当 `type` 值为 `md5_text` 时，代表所配置的内容为 **MD5密码(字节组)**。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "passwordInfo": {
    "type": "md5_bytes",
    "md5": [-24, 7, -15, -4, -14, 45, 18, 47, -101, -80, 24, -54, 102, 56, -95, -97]
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
passwordInfo: !<md5_bytes>
  md5: [-117, 26, -103, 83, -60, 97, 18, -106, -88, 39, -85, -8, -60, 120, 4, -41]
```

</TabItem>
<TabItem value="Properties">

:::tip 还是看看其他吧

`kotlinx-serialization-properties` 的序列化风格使得它并不太适用于大量元素的"数组"格式，就像下面这个示例一样。

:::

```properties
passwordInfo.type=md5_bytes
passwordInfo.value.md5.0=-117
passwordInfo.value.md5.1=26
passwordInfo.value.md5.2=-103
passwordInfo.value.md5.3=83
passwordInfo.value.md5.4=-60
passwordInfo.value.md5.5=97
passwordInfo.value.md5.6=18
passwordInfo.value.md5.7=-106
passwordInfo.value.md5.8=-88
passwordInfo.value.md5.9=39
passwordInfo.value.md5.10=-85
passwordInfo.value.md5.11=-8
passwordInfo.value.md5.12=-60
passwordInfo.value.md5.13=120
passwordInfo.value.md5.14=4
passwordInfo.value.md5.15=-41
```

</TabItem>
</Tabs>

#### env {#pwd-env}

下文几个以 `env_` 开头的配置类型代表那些直接通过虚拟机参数或者环境变量来进行动态配置的方式。
这类配置方式暂且称其为**环境变量类**的配置。

在环境变量配置中，会存在两个属性：`prop` 和 `env`。比如如下**示例**：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "code": 123456,
  "passwordInfo": {
    "type": "env_xxx",
    "prop": "mirai.$CODE$.password",
    "env": "mirai.$CODE$.password"
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
code: 123456
passwordInfo: !<env_xxx>
  prop: "mirai.$CODE$.password"
  env: "mirai.$CODE$.password"
```

</TabItem>
<TabItem value="Properties">

```properties
code=123456
passwordInfo.type=env_xxx
passwordInfo.value.prop=mirai.$CODE$.password
passwordInfo.value.env=mirai.$CODE$.password
```

</TabItem>
</Tabs>


上述配置代表，当前配置的bot的密码，会先通过虚拟机参数，也就是 `System.getProperty("simbot.mirai.123456.password")` 获取。
如果无法获取，则会尝试通过环境变量，也就是 `System.getenv("simbot.mirai.123456.password")` 获取。

带入上述示例，你可以通过如下启动命令来动态提供账号 `123456` 的密码信息。

```shell
java -jar -Dsimbot.mirai.123456.password=myPassword myBot.jar
```

注意，`prop` 是优先于 `env` 进行获取的。

:::tip 占位符? 

也许你发现了，上述配置中存在一串占位符 `$CODE$`。占位符会在 [后续](#mark) 进行简单介绍。

:::

`prop` 和 `env` 本质上都是 **可选** 属性，但是它们二者必须至少 **存在一个** 。

因此下述配置将会引发运行时异常：

:::danger 缺少属性

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "passwordInfo": {
    "type": "env_xxx"
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
code: 123456
passwordInfo: !<env_xxx>
```

</TabItem>
<TabItem value="Properties">

```properties
code=123456
passwordInfo.type=env_xxx
```

</TabItem>
</Tabs>

:::

#### env_text

通过[环境变量属性](#pwd-env)配置 **明文密码**。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "passwordInfo": {
    "type": "env_text",
    "prop": "xxx",
    "env": "xxx"
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
passwordInfo: !<env_text>
  prop: "xxx"
  env: "xxx"
```

</TabItem>
<TabItem value="Properties">

```properties
passwordInfo.type=env_text
passwordInfo.value.prop=xxx
passwordInfo.value.env=xxx
```

</TabItem>
</Tabs>

#### env_md5_text

通过[环境变量属性](#pwd-env)配置 **md5密码(字符串)**。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "passwordInfo": {
    "type": "env_md5_text",
    "prop": "xxx",
    "env": "xxx"
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
passwordInfo: !<env_md5_text>
  prop: "xxx"
  env: "xxx"
```

</TabItem>
<TabItem value="Properties">

```properties
passwordInfo.type=env_md5_text
passwordInfo.value.prop=xxx
passwordInfo.value.env=xxx
```

</TabItem>
</Tabs>

### DeviceInfoConfiguration

`DeviceInfoConfiguration` 是用来配置当前账号所使用的**设备信息(`DeviceInfo`)**的配置类型。其大致结构如下：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "xxx",
      "paramA": "valueA",
      "paramB": "valueB"
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<xxx>
    paramA: "valueA"
    paramB: "valueB"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=xxx
config.deviceInfo.value.paramA=valueA
config.deviceInfo.value.paramB=valueB
```

</TabItem>
</Tabs>


上述示例中可见，`deviceInfo` 一定存在一个 `type` 属性来标记当前配置的类型。`type` 是一个具有固定可选范围的字符串值，并且 `type` 的选择会决定其他的可用属性。 
下面会分别介绍所有的 `type` 可选项以及它们对应的具体结构。


#### random

`random` 代表使用mirai所提供的随机设备信息API来生成并使用一个随机的设备信息实例。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "random",
      "seed": null
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<random>
    seed: null
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=random
# config.deviceInfo.value.seed=
```

</TabItem>
</Tabs>

属性 `seed` 是一个 **可选项**，且 **可为空**，默认值为 `null`。其代表进行随机时所需要使用的随机种子。

#### simbot_random

`simbot_random` 代表使用simbot组件所提供的**随机设备信息**。simbot组件所提供的随机设备信息相比较于mirai原生的随机设备信息而言，
其中替换了部分属性内容，使得设备信息中留下了一些 simbot 的痕迹。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "simbot_random",
      "seed": 1
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<simbot_random>
    seed: 1
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=simbot_random
config.deviceInfo.value.seed=1
```

</TabItem>
</Tabs>

属性 `seed` 是一个 **可选项**，默认值为 `1`。其代表进行随机时所需要使用的随机种子。

#### resource

`resource` 代表寻找并读取一个本地或资源目录中的配置文件。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "resource",
      "paths": ["foo/bar/device.json"]
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<resource>
    paths: 
    - "foo/bar/device.json"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=resource
config.deviceInfo.value.paths.0=foo/bar/device.json
```

</TabItem>
</Tabs>

属性 `path` 是 **必选项**，且元素数量应当 **至少为1**。`path` 代表了需要按照顺序寻找的资源文件列表，因此 `path` 所指向的路径都应当为一个**具体的文件**，而不是目录。

**`path` 是支持 [占位符](#mark) 的**。

下述配置为例：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "resource",
      "paths": ["foo/bar/device-$CODE$.json"]
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<resource>
    paths: 
    - "foo/bar/device-$CODE$.json"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=resource
config.deviceInfo.value.paths.0=foo/bar/device-$CODE$.json
```

</TabItem>
</Tabs>

假设当前bot账号为 `123456`，则上述中配置的路径最终结果为 `"foo/bar/device-123456.json"` 。

`path` 在解析时，会首先尝试寻找本地目录，而后寻找资源目录。例如下述配置中：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "resource",
      "paths": ["foo/device-$CODE$.json"]
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<resource>
    paths: 
    - "foo/device-$CODE$.json"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=resource
config.deviceInfo.value.paths.0=foo/device-$CODE$.json
```

</TabItem>
</Tabs>

解析器首先会去寻找本地目录 `foo/device.json`，也就是项目根目录下 `foo` 目录下的 `device.json` 文件。
假如未寻得，则会通过类加载器尝试加载当前资源目录下的 `foo/device.json` 结果。如果上述过程结束且 `paths` 所有内容都无命中结果，
将会抛出异常。

如果你希望指定具体地寻找范围，比如仅寻找本地路径、仅寻找资源路径或者深层寻找资源路径，你可以通过为元素标记**前缀**来完成：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "resource",
      "paths": [
        "file:device-local.json",
        "classpath:device-resource.json",
        "classpath*:device-any.json"
      ]
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<resource>
    paths:
    - "file:device-local.json"
    - "classpath:device-resource.json"
    - "classpath*:device-any.json"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=resource
config.deviceInfo.value.paths.0=file:device-local.json
config.deviceInfo.value.paths.1=classpath:device-resource.json
config.deviceInfo.value.paths.2=classpath*:device-any.json
```

</TabItem>
</Tabs>

当使用前缀 `file:` 时，指定其后的路径为本地文件。

当使用前缀 `classpath:` 时，指定其后的路径为资源路径。

当使用前缀 `classpath*:` 时，指定其后的路径为资源路径，但是会获取可能得到的**所有**资源并取首个结果。

#### file_based

`file_based` 代表使用类似于mirai原生配置中的 `BotConfiguration.fileBasedDeviceInfo` 来进行配置。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "file_based",
      "file": "device.json",
      "fromResource": null
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<file_based>
    file: "device.json"
    fromResource: null
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=file_based
config.deviceInfo.value.file=device.json
# config.deviceInfo.value.fromResource=device-resource.json
```

</TabItem>
</Tabs>

与 [`resource`](#resource) 不同的是，`file_based` 是基于 `DeviceInfo.loadAsDeviceInfo()` 的，
其最终结果与行为会类似于使用 `BotConfiguration.fileBasedDeviceInfo()`。

`file_based` **仅支持** 使用本地文件，且所需要读取的设备信息文件的格式也与`DeviceInfo` 的结构存在些许不同，
它们是存在"版本号"的信息格式。因此 `file_based` 的所需格式与 [`resource`](#resource)的所需格式可能并不通用。

不过，虽然"仅支持"本地文件，但是它提供了一个可选参数 `fromResource` 来允许在读取文件之前进行检测：
当 `file` 处的文件**不存在**或**内容为空**时，会尝试从资源路径中寻找 `fromResource` 并将其复制到 `file` 处。
如果此行为尝试失败，则会输出警告日志，但不会终止流程。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "file_based",
      "file": "device.json",
      "fromResource": "device-resource.json"
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<file_based>
    file: "device.json"
    fromResource: "device-resource.json"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=file_based
config.deviceInfo.value.file=device.json
config.deviceInfo.value.fromResource=device-resource.json
```

</TabItem>
</Tabs>

上述配置中，如果当前项目根目录中不存在 `device.json` 文件，则会尝试从资源目录中读取 `device-resource.json` 并将其内容复制到项目根目录的 `device.json` 文件中。

<br />

与mirai原生配置 `BotConfiguration.fileBasedDeviceInfo()` 不同的是，`file_based` 的属性 `file` **不会** 被限制在 `workingDir` 中，而是会被**直接使用**。
因此在配置的时候请注意相对路径或绝对路径的使用，以及系统权限等问题。

<br />

`file` 和 `fromResource` 支持占位符替换，例如：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "file_based",
      "file": "device-$CODE$.json",
      "fromResource": "device-resource-$CODE$.json"
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<file_based>
    file: "device-$CODE$.json"
    fromResource: "device-resource-$CODE$.json"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=file_based
config.deviceInfo.value.file=device-$CODE$.json
config.deviceInfo.value.fromResource=device-resource-$CODE$.json
```

</TabItem>
</Tabs>

#### object

`object` 代表直接使用最原本的 `DeviceInfo` 序列化结果对象来作为属性值。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "object",
      "object": {}
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<object>
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=object
```

</TabItem>
</Tabs>

<details>
<summary>完整属性示例</summary>

:::note 仅供参考

下述示例仅为参考，不建议直接使用。属性具体含义请参考mirai `DeviceInfo` 类说明。

:::

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
    "config": {
      "deviceInfo": {
        "type": "object",
        "object": {
          "display": [76, 73, 82, 65, 73, 45, 83, 73, 77, 66, 79, 84, 46, 50, 48, 48, 49, 50, 50, 46, 48, 48, 49],
          "product": [108, 105, 114, 97, 105, 45, 115, 105, 109, 98, 111, 116],
          "device": [108, 105, 114, 97, 105, 45, 115, 105, 109, 98, 111, 116],
          "board": [108, 105, 114, 97, 105, 45, 115, 105, 109, 98, 111, 116],
          "brand": [101, 111, 114, 116, 101],
          "model": [108, 105, 114, 97, 105, 45, 115, 105, 109, 98, 111, 116],
          "bootloader": [117, 110, 107, 110, 111, 119, 110],
          "fingerprint": [109, 97, 109, 111, 101, 47, 109, 105, 114, 97, 105, 47, 109, 105, 114, 97, 105, 58, 49, 48, 47, 77, 73, 82, 65, 73, 46, 50, 48, 48, 49, 50, 50, 46, 48, 48, 49, 47, 54, 53, 56, 51, 55, 54, 48, 58, 117, 115, 101, 114, 47, 114, 101, 108, 101, 97, 115, 101, 45, 107, 101, 121, 115],
          "bootId": [-44, 29, -116, -39, -113, 0, -78, 4, -23, -128, 9, -104, -20, -8, 66, 126],
          "procVersion": [76, 105, 110, 117, 120, 32, 118, 101, 114, 115, 105, 111, 110, 32, 51, 46, 48, 46, 51, 49, 45, 50, 71, 54, 57, 72, 122, 115, 105, 32, 40, 97, 110, 100, 114, 111, 105, 100, 45, 98, 117, 105, 108, 100, 64, 120, 120, 120, 46, 120, 120, 120, 46, 120, 120, 120, 46, 120, 120, 120, 46, 99, 111, 109, 41],
          "baseBand": [],
          "version": {
            "incremental": [53, 56, 57, 49, 57, 51, 56],
            "release": [49, 48],
            "codename": [82, 69, 76],
            "sdk": 29
          },
          "simInfo": [84, 45, 77, 111, 98, 105, 108, 101],
          "osType": [97, 110, 100, 114, 111, 105, 100],
          "macAddress": [48, 50, 58, 48, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48],
          "wifiBSSID": [48, 50, 58, 48, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48],
          "wifiSSID": [60, 117, 110, 107, 110, 111, 119, 110, 32, 115, 115, 105, 100, 62],
          "imsiMd5": [-44, 29, -116, -39, -113, 0, -78, 4, -23, -128, 9, -104, -20, -8, 66, 126],
          "imei": "899752952597699",
          "apn": [119, 105, 102, 105]
        }
      }
    }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<object>
    object:
      display: [77, 73, 82, 65, 73, 46, 55, 48, 52, 52, 55, 48, 46, 48, 48, 49]
      product: [109, 105, 114, 97, 105]
      device: [109, 105, 114, 97, 105]
      board: [109, 105, 114, 97, 105]
      brand: [109, 97, 109, 111, 101]
      model: [109, 105, 114, 97, 105]
      bootloader: [117, 110, 107, 110, 111, 119, 110]
      fingerprint: [109, 97, 109, 111, 101, 47, 109, 105, 114, 97, 105, 47, 109, 105,
                    114, 97, 105, 58, 49, 48, 47, 77, 73, 82, 65, 73, 46, 50, 48, 48, 49, 50,
                    50, 46, 48, 48, 49, 47, 54, 57, 54, 57, 51, 55, 48, 58, 117, 115, 101, 114,
                    47, 114, 101, 108, 101, 97, 115, 101, 45, 107, 101, 121, 115]
      bootId: [52, 54, 48, 55, 67, 54, 49, 53, 45, 70, 54, 67, 49, 45, 49, 53, 50,
               68, 45, 67, 65, 70, 52, 45, 66, 68, 53, 67, 69, 66, 54, 69, 67, 70, 69, 54]
      procVersion: [76, 105, 110, 117, 120, 32, 118, 101, 114, 115, 105, 111, 110,
                    32, 51, 46, 48, 46, 51, 49, 45, 105, 81, 66, 48, 50, 86, 52, 109, 32, 40,
                    97, 110, 100, 114, 111, 105, 100, 45, 98, 117, 105, 108, 100, 64, 120, 120,
                    120, 46, 120, 120, 120, 46, 120, 120, 120, 46, 120, 120, 120, 46, 99, 111,
                    109, 41]
      baseBand: []
      version:
        incremental: [53, 56, 57, 49, 57, 51, 56]
        release: [49, 48]
        codename: [82, 69, 76]
        sdk: 29
      simInfo: [84, 45, 77, 111, 98, 105, 108, 101]
      osType: [97, 110, 100, 114, 111, 105, 100]
      macAddress: [48, 50, 58, 48, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48, 58, 48,
                   48]
      wifiBSSID: [48, 50, 58, 48, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48, 58, 48,
                  48]
      wifiSSID: [60, 117, 110, 107, 110, 111, 119, 110, 32, 115, 115, 105, 100, 62]
      imsiMd5: [-83, -55, -86, 114, 108, -44, -65, 112, 102, 98, 60, -80, 41, -11,
                -38, -66]
      imei: "865456863200190"
      apn: [119, 105, 102, 105]
```

</TabItem>
<TabItem value="Properties">

:::note 如你所见

如上所述。`properties` 格式的文件不适合存在大量多元素数组的场景，
而原始的 `DeviceInfo` 结构中存在大量的 `byte` 数组。
这将会导致 `properties` 文件内容非常的...不可控。

:::

<details>
<summary>一泻千里</summary>

```properties
config.deviceInfo.type=object
config.deviceInfo.value.object.display.0=77
config.deviceInfo.value.object.display.1=73
config.deviceInfo.value.object.display.2=82
config.deviceInfo.value.object.display.3=65
config.deviceInfo.value.object.display.4=73
config.deviceInfo.value.object.display.5=46
config.deviceInfo.value.object.display.6=57
config.deviceInfo.value.object.display.7=53
config.deviceInfo.value.object.display.8=49
config.deviceInfo.value.object.display.9=53
config.deviceInfo.value.object.display.10=49
config.deviceInfo.value.object.display.11=55
config.deviceInfo.value.object.display.12=46
config.deviceInfo.value.object.display.13=48
config.deviceInfo.value.object.display.14=48
config.deviceInfo.value.object.display.15=49
config.deviceInfo.value.object.product.0=109
config.deviceInfo.value.object.product.1=105
config.deviceInfo.value.object.product.2=114
config.deviceInfo.value.object.product.3=97
config.deviceInfo.value.object.product.4=105
config.deviceInfo.value.object.device.0=109
config.deviceInfo.value.object.device.1=105
config.deviceInfo.value.object.device.2=114
config.deviceInfo.value.object.device.3=97
config.deviceInfo.value.object.device.4=105
config.deviceInfo.value.object.board.0=109
config.deviceInfo.value.object.board.1=105
config.deviceInfo.value.object.board.2=114
config.deviceInfo.value.object.board.3=97
config.deviceInfo.value.object.board.4=105
config.deviceInfo.value.object.brand.0=109
config.deviceInfo.value.object.brand.1=97
config.deviceInfo.value.object.brand.2=109
config.deviceInfo.value.object.brand.3=111
config.deviceInfo.value.object.brand.4=101
config.deviceInfo.value.object.model.0=109
config.deviceInfo.value.object.model.1=105
config.deviceInfo.value.object.model.2=114
config.deviceInfo.value.object.model.3=97
config.deviceInfo.value.object.model.4=105
config.deviceInfo.value.object.bootloader.0=117
config.deviceInfo.value.object.bootloader.1=110
config.deviceInfo.value.object.bootloader.2=107
config.deviceInfo.value.object.bootloader.3=110
config.deviceInfo.value.object.bootloader.4=111
config.deviceInfo.value.object.bootloader.5=119
config.deviceInfo.value.object.bootloader.6=110
config.deviceInfo.value.object.fingerprint.0=109
config.deviceInfo.value.object.fingerprint.1=97
config.deviceInfo.value.object.fingerprint.2=109
config.deviceInfo.value.object.fingerprint.3=111
config.deviceInfo.value.object.fingerprint.4=101
config.deviceInfo.value.object.fingerprint.5=47
config.deviceInfo.value.object.fingerprint.6=109
config.deviceInfo.value.object.fingerprint.7=105
config.deviceInfo.value.object.fingerprint.8=114
config.deviceInfo.value.object.fingerprint.9=97
config.deviceInfo.value.object.fingerprint.10=105
config.deviceInfo.value.object.fingerprint.11=47
config.deviceInfo.value.object.fingerprint.12=109
config.deviceInfo.value.object.fingerprint.13=105
config.deviceInfo.value.object.fingerprint.14=114
config.deviceInfo.value.object.fingerprint.15=97
config.deviceInfo.value.object.fingerprint.16=105
config.deviceInfo.value.object.fingerprint.17=58
config.deviceInfo.value.object.fingerprint.18=49
config.deviceInfo.value.object.fingerprint.19=48
config.deviceInfo.value.object.fingerprint.20=47
config.deviceInfo.value.object.fingerprint.21=77
config.deviceInfo.value.object.fingerprint.22=73
config.deviceInfo.value.object.fingerprint.23=82
config.deviceInfo.value.object.fingerprint.24=65
config.deviceInfo.value.object.fingerprint.25=73
config.deviceInfo.value.object.fingerprint.26=46
config.deviceInfo.value.object.fingerprint.27=50
config.deviceInfo.value.object.fingerprint.28=48
config.deviceInfo.value.object.fingerprint.29=48
config.deviceInfo.value.object.fingerprint.30=49
config.deviceInfo.value.object.fingerprint.31=50
config.deviceInfo.value.object.fingerprint.32=50
config.deviceInfo.value.object.fingerprint.33=46
config.deviceInfo.value.object.fingerprint.34=48
config.deviceInfo.value.object.fingerprint.35=48
config.deviceInfo.value.object.fingerprint.36=49
config.deviceInfo.value.object.fingerprint.37=47
config.deviceInfo.value.object.fingerprint.38=54
config.deviceInfo.value.object.fingerprint.39=48
config.deviceInfo.value.object.fingerprint.40=56
config.deviceInfo.value.object.fingerprint.41=49
config.deviceInfo.value.object.fingerprint.42=56
config.deviceInfo.value.object.fingerprint.43=56
config.deviceInfo.value.object.fingerprint.44=55
config.deviceInfo.value.object.fingerprint.45=58
config.deviceInfo.value.object.fingerprint.46=117
config.deviceInfo.value.object.fingerprint.47=115
config.deviceInfo.value.object.fingerprint.48=101
config.deviceInfo.value.object.fingerprint.49=114
config.deviceInfo.value.object.fingerprint.50=47
config.deviceInfo.value.object.fingerprint.51=114
config.deviceInfo.value.object.fingerprint.52=101
config.deviceInfo.value.object.fingerprint.53=108
config.deviceInfo.value.object.fingerprint.54=101
config.deviceInfo.value.object.fingerprint.55=97
config.deviceInfo.value.object.fingerprint.56=115
config.deviceInfo.value.object.fingerprint.57=101
config.deviceInfo.value.object.fingerprint.58=45
config.deviceInfo.value.object.fingerprint.59=107
config.deviceInfo.value.object.fingerprint.60=101
config.deviceInfo.value.object.fingerprint.61=121
config.deviceInfo.value.object.fingerprint.62=115
config.deviceInfo.value.object.bootId.0=69
config.deviceInfo.value.object.bootId.1=70
config.deviceInfo.value.object.bootId.2=65
config.deviceInfo.value.object.bootId.3=52
config.deviceInfo.value.object.bootId.4=51
config.deviceInfo.value.object.bootId.5=70
config.deviceInfo.value.object.bootId.6=53
config.deviceInfo.value.object.bootId.7=48
config.deviceInfo.value.object.bootId.8=45
config.deviceInfo.value.object.bootId.9=70
config.deviceInfo.value.object.bootId.10=65
config.deviceInfo.value.object.bootId.11=70
config.deviceInfo.value.object.bootId.12=52
config.deviceInfo.value.object.bootId.13=45
config.deviceInfo.value.object.bootId.14=54
config.deviceInfo.value.object.bootId.15=54
config.deviceInfo.value.object.bootId.16=57
config.deviceInfo.value.object.bootId.17=70
config.deviceInfo.value.object.bootId.18=45
config.deviceInfo.value.object.bootId.19=48
config.deviceInfo.value.object.bootId.20=53
config.deviceInfo.value.object.bootId.21=53
config.deviceInfo.value.object.bootId.22=57
config.deviceInfo.value.object.bootId.23=45
config.deviceInfo.value.object.bootId.24=65
config.deviceInfo.value.object.bootId.25=48
config.deviceInfo.value.object.bootId.26=49
config.deviceInfo.value.object.bootId.27=56
config.deviceInfo.value.object.bootId.28=55
config.deviceInfo.value.object.bootId.29=48
config.deviceInfo.value.object.bootId.30=51
config.deviceInfo.value.object.bootId.31=48
config.deviceInfo.value.object.bootId.32=55
config.deviceInfo.value.object.bootId.33=68
config.deviceInfo.value.object.bootId.34=66
config.deviceInfo.value.object.bootId.35=70
config.deviceInfo.value.object.procVersion.0=76
config.deviceInfo.value.object.procVersion.1=105
config.deviceInfo.value.object.procVersion.2=110
config.deviceInfo.value.object.procVersion.3=117
config.deviceInfo.value.object.procVersion.4=120
config.deviceInfo.value.object.procVersion.5=32
config.deviceInfo.value.object.procVersion.6=118
config.deviceInfo.value.object.procVersion.7=101
config.deviceInfo.value.object.procVersion.8=114
config.deviceInfo.value.object.procVersion.9=115
config.deviceInfo.value.object.procVersion.10=105
config.deviceInfo.value.object.procVersion.11=111
config.deviceInfo.value.object.procVersion.12=110
config.deviceInfo.value.object.procVersion.13=32
config.deviceInfo.value.object.procVersion.14=51
config.deviceInfo.value.object.procVersion.15=46
config.deviceInfo.value.object.procVersion.16=48
config.deviceInfo.value.object.procVersion.17=46
config.deviceInfo.value.object.procVersion.18=51
config.deviceInfo.value.object.procVersion.19=49
config.deviceInfo.value.object.procVersion.20=45
config.deviceInfo.value.object.procVersion.21=88
config.deviceInfo.value.object.procVersion.22=80
config.deviceInfo.value.object.procVersion.23=74
config.deviceInfo.value.object.procVersion.24=80
config.deviceInfo.value.object.procVersion.25=52
config.deviceInfo.value.object.procVersion.26=50
config.deviceInfo.value.object.procVersion.27=57
config.deviceInfo.value.object.procVersion.28=114
config.deviceInfo.value.object.procVersion.29=32
config.deviceInfo.value.object.procVersion.30=40
config.deviceInfo.value.object.procVersion.31=97
config.deviceInfo.value.object.procVersion.32=110
config.deviceInfo.value.object.procVersion.33=100
config.deviceInfo.value.object.procVersion.34=114
config.deviceInfo.value.object.procVersion.35=111
config.deviceInfo.value.object.procVersion.36=105
config.deviceInfo.value.object.procVersion.37=100
config.deviceInfo.value.object.procVersion.38=45
config.deviceInfo.value.object.procVersion.39=98
config.deviceInfo.value.object.procVersion.40=117
config.deviceInfo.value.object.procVersion.41=105
config.deviceInfo.value.object.procVersion.42=108
config.deviceInfo.value.object.procVersion.43=100
config.deviceInfo.value.object.procVersion.44=64
config.deviceInfo.value.object.procVersion.45=120
config.deviceInfo.value.object.procVersion.46=120
config.deviceInfo.value.object.procVersion.47=120
config.deviceInfo.value.object.procVersion.48=46
config.deviceInfo.value.object.procVersion.49=120
config.deviceInfo.value.object.procVersion.50=120
config.deviceInfo.value.object.procVersion.51=120
config.deviceInfo.value.object.procVersion.52=46
config.deviceInfo.value.object.procVersion.53=120
config.deviceInfo.value.object.procVersion.54=120
config.deviceInfo.value.object.procVersion.55=120
config.deviceInfo.value.object.procVersion.56=46
config.deviceInfo.value.object.procVersion.57=120
config.deviceInfo.value.object.procVersion.58=120
config.deviceInfo.value.object.procVersion.59=120
config.deviceInfo.value.object.procVersion.60=46
config.deviceInfo.value.object.procVersion.61=99
config.deviceInfo.value.object.procVersion.62=111
config.deviceInfo.value.object.procVersion.63=109
config.deviceInfo.value.object.procVersion.64=41
config.deviceInfo.value.object.version.incremental.0=53
config.deviceInfo.value.object.version.incremental.1=56
config.deviceInfo.value.object.version.incremental.2=57
config.deviceInfo.value.object.version.incremental.3=49
config.deviceInfo.value.object.version.incremental.4=57
config.deviceInfo.value.object.version.incremental.5=51
config.deviceInfo.value.object.version.incremental.6=56
config.deviceInfo.value.object.version.release.0=49
config.deviceInfo.value.object.version.release.1=48
config.deviceInfo.value.object.version.codename.0=82
config.deviceInfo.value.object.version.codename.1=69
config.deviceInfo.value.object.version.codename.2=76
config.deviceInfo.value.object.version.sdk=29
config.deviceInfo.value.object.simInfo.0=84
config.deviceInfo.value.object.simInfo.1=45
config.deviceInfo.value.object.simInfo.2=77
config.deviceInfo.value.object.simInfo.3=111
config.deviceInfo.value.object.simInfo.4=98
config.deviceInfo.value.object.simInfo.5=105
config.deviceInfo.value.object.simInfo.6=108
config.deviceInfo.value.object.simInfo.7=101
config.deviceInfo.value.object.osType.0=97
config.deviceInfo.value.object.osType.1=110
config.deviceInfo.value.object.osType.2=100
config.deviceInfo.value.object.osType.3=114
config.deviceInfo.value.object.osType.4=111
config.deviceInfo.value.object.osType.5=105
config.deviceInfo.value.object.osType.6=100
config.deviceInfo.value.object.macAddress.0=48
config.deviceInfo.value.object.macAddress.1=50
config.deviceInfo.value.object.macAddress.2=58
config.deviceInfo.value.object.macAddress.3=48
config.deviceInfo.value.object.macAddress.4=48
config.deviceInfo.value.object.macAddress.5=58
config.deviceInfo.value.object.macAddress.6=48
config.deviceInfo.value.object.macAddress.7=48
config.deviceInfo.value.object.macAddress.8=58
config.deviceInfo.value.object.macAddress.9=48
config.deviceInfo.value.object.macAddress.10=48
config.deviceInfo.value.object.macAddress.11=58
config.deviceInfo.value.object.macAddress.12=48
config.deviceInfo.value.object.macAddress.13=48
config.deviceInfo.value.object.macAddress.14=58
config.deviceInfo.value.object.macAddress.15=48
config.deviceInfo.value.object.macAddress.16=48
config.deviceInfo.value.object.wifiBSSID.0=48
config.deviceInfo.value.object.wifiBSSID.1=50
config.deviceInfo.value.object.wifiBSSID.2=58
config.deviceInfo.value.object.wifiBSSID.3=48
config.deviceInfo.value.object.wifiBSSID.4=48
config.deviceInfo.value.object.wifiBSSID.5=58
config.deviceInfo.value.object.wifiBSSID.6=48
config.deviceInfo.value.object.wifiBSSID.7=48
config.deviceInfo.value.object.wifiBSSID.8=58
config.deviceInfo.value.object.wifiBSSID.9=48
config.deviceInfo.value.object.wifiBSSID.10=48
config.deviceInfo.value.object.wifiBSSID.11=58
config.deviceInfo.value.object.wifiBSSID.12=48
config.deviceInfo.value.object.wifiBSSID.13=48
config.deviceInfo.value.object.wifiBSSID.14=58
config.deviceInfo.value.object.wifiBSSID.15=48
config.deviceInfo.value.object.wifiBSSID.16=48
config.deviceInfo.value.object.wifiSSID.0=60
config.deviceInfo.value.object.wifiSSID.1=117
config.deviceInfo.value.object.wifiSSID.2=110
config.deviceInfo.value.object.wifiSSID.3=107
config.deviceInfo.value.object.wifiSSID.4=110
config.deviceInfo.value.object.wifiSSID.5=111
config.deviceInfo.value.object.wifiSSID.6=119
config.deviceInfo.value.object.wifiSSID.7=110
config.deviceInfo.value.object.wifiSSID.8=32
config.deviceInfo.value.object.wifiSSID.9=115
config.deviceInfo.value.object.wifiSSID.10=115
config.deviceInfo.value.object.wifiSSID.11=105
config.deviceInfo.value.object.wifiSSID.12=100
config.deviceInfo.value.object.wifiSSID.13=62
config.deviceInfo.value.object.imsiMd5.0=27
config.deviceInfo.value.object.imsiMd5.1=108
config.deviceInfo.value.object.imsiMd5.2=-22
config.deviceInfo.value.object.imsiMd5.3=115
config.deviceInfo.value.object.imsiMd5.4=-17
config.deviceInfo.value.object.imsiMd5.5=70
config.deviceInfo.value.object.imsiMd5.6=-10
config.deviceInfo.value.object.imsiMd5.7=26
config.deviceInfo.value.object.imsiMd5.8=-99
config.deviceInfo.value.object.imsiMd5.9=-122
config.deviceInfo.value.object.imsiMd5.10=-104
config.deviceInfo.value.object.imsiMd5.11=-95
config.deviceInfo.value.object.imsiMd5.12=125
config.deviceInfo.value.object.imsiMd5.13=127
config.deviceInfo.value.object.imsiMd5.14=119
config.deviceInfo.value.object.imsiMd5.15=34
config.deviceInfo.value.object.imei=863276296780226
config.deviceInfo.value.object.apn.0=119
config.deviceInfo.value.object.apn.1=105
config.deviceInfo.value.object.apn.2=102
config.deviceInfo.value.object.apn.3=105
```

</details>

</TabItem>
</Tabs>

</details>


#### simple_object

`simple_object` 与上文中的 `object` 很类似，只不过 `simple_object` 代表使用simbot所提供的 `SimpleDeviceInfo` 序列化结果对象来作为属性值。

`SimpleDeviceInfo` 属性名与 `DeviceInfo` 基本一致，只不过将 `DeviceInfo` 中所有原本为字节数组的属性变更为了字符串，使得其属性可以相对表现得更直观。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "simple_object",
      "object": {}
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<simple_object>
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=simple_object
```

</TabItem>
</Tabs>

<details>
<summary>完整属性示例</summary>

:::note 仅供参考

下述示例仅为参考，不建议直接使用。属性具体含义请参考mirai `DeviceInfo` 类说明（ `SimpleDeviceInfo` 字段含义于其一致）。

:::

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
    "config": {
      "deviceInfo": {
        "type": "simple_object",
        "object": {
          "display": "MIRAI.496596.001",
          "product": "mirai",
          "device": "mirai",
          "board": "mirai",
          "brand": "mamoe",
          "model": "mirai",
          "bootloader": "unknown",
          "fingerprint": "mamoe/mirai/mirai:10/MIRAI.200122.001/7041834:user/release-keys",
          "bootId": "8B176CE7-2C0D-554E-440D-1D6FC8F53AD1",
          "procVersion": "Linux version 3.0.31-o66PpLqf (android-build@xxx.xxx.xxx.xxx.com)",
          "baseBand": "",
          "version": {
            "incremental": "5891938",
            "release": "10",
            "codename": "REL",
            "sdk": 29
          },
          "simInfo": "T-Mobile",
          "osType": "android",
          "macAddress": "02:00:00:00:00:00",
          "wifiBSSID": "02:00:00:00:00:00",
          "wifiSSID": "<unknown ssid>",
          "imsiMd5": "5df6e66cc541fe594f228c889cd7828b",
          "imei": "078869213288891",
          "apn": "wifi"
        }
      }
    }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<simple_object>
    object:
      display: "MIRAI.712554.001"
      product: "mirai"
      device: "mirai"
      board: "mirai"
      brand: "mamoe"
      model: "mirai"
      bootloader: "unknown"
      fingerprint: "mamoe/mirai/mirai:10/MIRAI.200122.001/0712600:user/release-keys"
      bootId: "14387C75-A130-9CB8-4058-7F9DA76CC8D8"
      procVersion: "Linux version 3.0.31-D474G48Z (android-build@xxx.xxx.xxx.xxx.com)"
      baseBand: ""
      version:
        incremental: "5891938"
        release: "10"
        codename: "REL"
        sdk: 0
      simInfo: "T-Mobile"
      osType: "android"
      macAddress: "02:00:00:00:00:00"
      wifiBSSID: "02:00:00:00:00:00"
      wifiSSID: "<unknown ssid>"
      imsiMd5: "e919380912a110613d715dbd2a4164f8"
      imei: "865547659069474"
      apn: "wifi"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=simple_object
config.deviceInfo.value.object.display=MIRAI.622716.001
config.deviceInfo.value.object.product=mirai
config.deviceInfo.value.object.device=mirai
config.deviceInfo.value.object.board=mirai
config.deviceInfo.value.object.brand=mamoe
config.deviceInfo.value.object.model=mirai
config.deviceInfo.value.object.bootloader=unknown
config.deviceInfo.value.object.fingerprint=mamoe/mirai/mirai:10/MIRAI.200122.001/2518688:user/release-keys
config.deviceInfo.value.object.bootId=DEF2A279-C7D3-FDD1-F17C-942DCA6871B7
config.deviceInfo.value.object.procVersion=Linux version 3.0.31-52WDrFu6 (android-build@xxx.xxx.xxx.xxx.com)
config.deviceInfo.value.object.baseBand=
config.deviceInfo.value.object.version.incremental=5891938
config.deviceInfo.value.object.version.release=10
config.deviceInfo.value.object.version.codename=REL
config.deviceInfo.value.object.version.sdk=0
config.deviceInfo.value.object.simInfo=T-Mobile
config.deviceInfo.value.object.osType=android
config.deviceInfo.value.object.macAddress=02:00:00:00:00:00
config.deviceInfo.value.object.wifiBSSID=02:00:00:00:00:00
config.deviceInfo.value.object.wifiSSID=<unknown ssid>
config.deviceInfo.value.object.imsiMd5=f6d271da5d965644d0e0b635337ef496
config.deviceInfo.value.object.imei=862054186439299
config.deviceInfo.value.object.apn=wifi
```

</TabItem>
</Tabs>

</details>

:::caution 兼容性

需要注意的是，`SimpleDeviceInfo` 是 simbot 通过对照 `DeviceInfo` 类的结构而手动构造出来的类型，因此其无法保证与 `DeviceInfo` 之间能够保持长久的兼容对照。
假若 `DeviceInfo` 在后续版本产生变更， `SimpleDeviceInfo` 是无法保证能够适配兼容的。因此请**谨慎**使用 `SimpleDeviceInfo`。

:::


#### auto <Label>默认</Label>

`auto` 是在未配置的情况下**默认使用**的类型，其代表会尝试自动寻找配置文件，如果找不到则会使用 [`file_based`](#file_based) 作为生成策略。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "auto",
      "baseDir": null,
      "fileBasedFilename": "device.json"
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<auto>
    baseDir: null
    fileBasedFilename: device.json
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=auto
# config.deviceInfo.value.baseDir=devices
config.deviceInfo.value.fileBasedFilename=device.json
```

</TabItem>
</Tabs>

`baseDir` 是一个**可选的**，且**可为null**。其代表在自动搜寻配置文件时的基础目录。当为 `null` 的时候将**不会搜寻**配置文件。

当 `baseDir` 不为 `null` 时，解析器首先会按照顺序尝试搜寻如下目录：

1. 本地文件: `$baseDir/device-$CODE$.json` 
2. 资源文件: `$baseDir/device-$CODE$.json`
3. 本地文件: `$baseDir/device.json`
4. 资源文件: `$baseDir/device.json`

如下示例中，

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "auto",
      "baseDir": "devices"
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<auto>
    baseDir: "devices"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=auto
config.deviceInfo.value.baseDir=devices
```

</TabItem>
</Tabs>


假设当前 `code` 为 `123456`，则最终寻找的目标路径为：

- `devices/device-123456.json`
- `devices/device.json`


`baseDir` 支持 [占位符](#mark) 。

例如：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
  "config": {
    "deviceInfo": {
      "type": "auto",
      "baseDir": "devices-$CODE$"
    }
  }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  deviceInfo: !<auto>
    baseDir: "devices-$CODE$"
```

</TabItem>
<TabItem value="Properties">

```properties
config.deviceInfo.type=auto
config.deviceInfo.value.baseDir=devices-$CODE$
```

</TabItem>
</Tabs>

假设当前 `code` 为 `123456`, 则上述配置中的的 `baseDir` 最终会被替换为 `devices-123456`，并最终会去寻找如下目标：

- `devices-123456/device-123456.json`
- `devices-123456/device.json`


### ContactListCacheConfiguration

类型结构同原生mirai配置字段 `contactListCache`，其属性如下：

#### saveIntervalMillis

同原生mirai配置，在有修改时自动保存间隔. 默认 `60` 秒. 在每次登录完成后有修改时都会立即保存一次.

#### friendListCacheEnabled

同原生mirai配置，开启好友列表缓存.

#### groupMemberListCacheEnabled

同原生mirai配置，开启群成员列表缓存.

#### 参考配置

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
   "config": {
      "contactListCache": {
        "saveIntervalMillis": 60000,
        "friendListCacheEnabled": false,
        "groupMemberListCacheEnabled": false
      }
   }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  contactListCache:
    saveIntervalMillis: 60000
    friendListCacheEnabled: false
    groupMemberListCacheEnabled: false
```

</TabItem>
<TabItem value="Properties">

```properties
config.contactListCache.saveIntervalMillis=60000
config.contactListCache.friendListCacheEnabled=false
config.contactListCache.groupMemberListCacheEnabled=false
```

</TabItem>
</Tabs>

### RecallMessageCacheStrategyConfiguration

`RecallMessageCacheStrategyConfiguration` 是用于配置对撤回消息内容缓存策略的配置类型。
其大致结构如下：

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
   "config": {
      "recallMessageCacheStrategyConfig": {
        "type": "type",
        "param1": "value1"
      }
   }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  recallMessageCacheStrategyConfig: !<type>
    paramA: "valueA"
```

</TabItem>
<TabItem value="Properties">

```properties
config.recallMessageCacheStrategyConfig.type=type
config.recallMessageCacheStrategyConfig.value.paramA=valueA
```


</TabItem>
</Tabs>

上述示例中可见，`recallMessageCacheStrategyConfig` 一定存在一个 `type` 属性来标记当前配置的类型。`type` 是一个具有固定可选范围的字符串值，并且 `type` 的选择会决定其他的可用属性。 下面会分别介绍所有的 `type` 以及它们对应的具体结构。

#### invalid

代表**无效**的缓存策略，即**不进行**缓存。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
   "config": {
      "recallMessageCacheStrategyConfig": {
        "type": "invalid"
      }
   }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  recallMessageCacheStrategyConfig: !<invalid>
```

</TabItem>
<TabItem value="Properties">

```properties
config.recallMessageCacheStrategyConfig.type=invalid
```

</TabItem>
</Tabs>

#### memory_lru

即通过在内存中使用LRU缓存的策略。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
   "config": {
      "recallMessageCacheStrategyConfig": {
        "type": "memory_lru",
        "groupMaxSize": 1536,
        "friendMaxSize": 96,
        "loadFactor": 0.75
      }
   }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  recallMessageCacheStrategyConfig: !<memory_lru>
  groupMaxSize: 1536
  friendMaxSize: 96
  loadFactor: 0.75
```

</TabItem>
<TabItem value="Properties">

```properties
config.recallMessageCacheStrategyConfig.type=memory_lru
config.recallMessageCacheStrategyConfig.value.groupMaxSize=1536
config.recallMessageCacheStrategyConfig.value.friendMaxSize=96
config.recallMessageCacheStrategyConfig.value.loadFactor=0.75
```

</TabItem>
</Tabs>

`memory_lru` 的本质即在内部通过 `Map` 进行缓存，提供的属性 `loadFactor` 也是使用在Map中的属性。

`groupMaxSize` 和 `friendMaxSize` 分别代表对群消息和好友消息的缓存数量上限。

所有属性均有默认值。

#### custom_properties

指定一个自定义实现类和自定义属性来自行实现缓存效果的配置。

<Tabs groupId="bot-config">
<TabItem value="JSON">

```json
{
   "config": {
      "recallMessageCacheStrategyConfig": {
        "type": "custom_properties",
        "className": "com.example.xxx.CustomPropertiesMiraiRecallMessageCacheStrategyImpl",
        "properties": {
          "foo": "bar"
        }
      }
   }
}
```

</TabItem>
<TabItem value="YAML">

```yaml
config:
  recallMessageCacheStrategyConfig: !<custom_properties>
  className: 'com.example.xxx.CustomPropertiesMiraiRecallMessageCacheStrategyImpl'
  properties:
    foo: bar
```

</TabItem>
<TabItem value="Properties">

```properties
config.recallMessageCacheStrategyConfig.type=custom_properties
config.recallMessageCacheStrategyConfig.value.className=com.example.xxx.CustomPropertiesMiraiRecallMessageCacheStrategyImpl
config.recallMessageCacheStrategyConfig.value.properties.foo=bar
```

</TabItem>
</Tabs>

`custom_properties` 代表使用者自行提供一个实现，并自行解析所有提供的配置属性来实现一个缓存器。
其中 `className` 属性是必须的，且需要保证此实现类**至少存在一个无参公开构造**用来进行实例化。

`properties` 属性为可自定义的键值对配置集，但是需要注意键与值都应为**字符串**格式。



## 占位符替换 {#mark}

在上述详细释义中，你可能会发现有些配置中（尤其是存在 `type` 的多选择配置）会出现占位符 `$CODE$`。其实这个占位符很简单，它会在这个配置进行处理前，将 `$CODE$` 替换为当前bot的`code`信息。

例如你所配置的 `code` 值为 `123456`，那么 `$CODE$` 最终就会被替换为 `123456`。

需要注意的是，并非所有配置都允许这个占位符。如果某个配置项支持，那么在说明中（也包括源码中的文档注释）将会有所体现。

# 2.0.0
## ALPHA.1
first alpha version.


## ALPHA.2
- `MessageEventGet` 更名为 `MessageGet`。

- 优化部分日志输出。
- 调整核心中method-listener的注册方式为全局扫描。

- [core-parent](./core-parent)下新增模块：
    - [http-client-core](./http/client-core) http-client模块核心
    - [http-client-ktor](./http/client-ktor) ktor-http-client模块
    - [serialization-json-core](./serialization/json-core) json序列化模块核心
    - [serialization-json-fastjson](./serialization/json-fastjson) fastjson-json序列化模块
    - [serialization-json-moshi](./serialization/json-moshi) moshi-json序列化模块

- 追加 [Spring-boot-starter](./spring-boot-starter) 模块及其子模块：
    - [core-spring-boot-starter](./spring-boot-starter/core-spring-boot-starter) 模块
    - [component-mirai-Spring-boot-starter](./spring-boot-starter/component-mirai-spring-boot-starter) 模块


## ALPHA.3

### 核心

core-starter移除部分多余控制台输出。

重新定义 [MessageContent](./core-parent/api/src/main/java/love/forte/simbot/api/message/MessageContent.kt) ，
并在 [mirai组件](./component/component-mirai) 中重新实现。 

其中，暂时保留 曾经的`MessageContent.getImages`方法，但是此方法已**不存在任何作用**。

### 组件

mirai组件中优化 `MessageContentBuilder` 中，通过input流和byte数组构建图片实例的相关内容，当group和friend两个目标图片实例均被构建后，会主动调用`close()`，而在其中一方实例化完成后，会 **尝试** 调用`input.reset`。
 
上述调整中，构建group和friend目标图片的时候调整为共用同一个锁。 



### ALPHA.4

- `kotlin` update to `v1.4.20`
- `ktor` update to `v1.4.2`
- `forte-common-ioc` to `v1.0.0-ALPHA.5`

### 核心

- 调整 MessageContentBuilder 的包路径，移出`event`层级。

### 组件

- 追加1.x中的[钉钉组件](./component/component-ding)。

- mirai组件 `mirai` update to `v1.3.3`


### fix

- fix [#1 ](https://github.com/ForteScarlet/simpler-robot/issues/1) (`@Filters` 导致纯文本消息监听失效)
- fix [#2 ](https://github.com/ForteScarlet/simpler-robot/issues/2) (动态参数解析异常)
- fix [#3 ](https://github.com/ForteScarlet/simpler-robot/issues/3) (mirai组件执行一段时间后会自行关闭)


## ALPHA.5

变更开发分支为 `dev`.
`alpha` 分支为目前已部署的alpha最终内容。


存在不兼容更新且本次更新仅包含以下的不兼容内容。
### api
- ※ 不兼容更新：`MessageContent` 包路径变更：`love.forte.simbot.api.message.event.MessageContent` -> `love.forte.simbot.api.message.MessageContent`


## ALPHA.6

### fix
- fix [#5 ](https://github.com/ForteScarlet/simpler-robot/issues/5) (依赖缺失)
- fix [#6 ](https://github.com/ForteScarlet/simpler-robot/issues/6) (无文本事件匹配异常)



## ALPHA.7

- update `hutool` to `v5.5.1`

### core
- 优化过滤器匹配规则，如果事件为 `MessageGet` 类型并且text属性为null, 强制将text视为空字符串进行匹配。

### http-client-core
- get请求支持携带cookies。

### fix 
- fix [#8 ](https://github.com/ForteScarlet/simpler-robot/issues/8) (多bot下群消息重复触发)


## ALPHA.8 

### fix
- fix [#9 ](https://github.com/ForteScarlet/simpler-robot/issues/9) (多bot下监听函数重复缓存)


## ALPHA.9

### lovelycat (未发布)
- 实现登录账号验证
- 实现群聊消息监听

### fix 
- fix [#10 ](https://github.com/ForteScarlet/simpler-robot/issues/10) (@ListenBreak未被实现)


## ALPHA.10

### core
- 移除 `MessageContent.getImages()`。

### [lovelycat](./component/component-lovelycat-httpapi)
- 实现私聊消息监听
- 实现转账事件监听与接受转账
- 实现扫描支付事件监听
- 实现好友变动监听
- 实现群成员增加/减少监听
- 实现新增好友监听（setter-同意申请未实现）
- 实现bot账号登录事件监听
- 实现系统监听
- 实现 sender-送信器

可爱猫组件尚处于预览/尝鲜体验状态，仅实现了Sender, 且不保证其稳定性与效率。


### fix
- fix [#11 ](https://github.com/ForteScarlet/simpler-robot/issues/11) (发送多个图片导致同步锁异常)
- fix [#12 ](https://github.com/ForteScarlet/simpler-robot/issues/12) (为GroupAddRequest追加群容器)


## ALPHA.11

### lovelycat 
- 追加api内置缓存配置。
```properties
# 是否启用组件内置api缓存，默认开启
simbot.component.lovelycat.api.cache.enable=true
# 缓存时效，默认为1
simbot.component.lovelycat.api.cache.duration=1
# 缓存时效的时间单位，默认为分钟。值为 java.util.concurrent.TimeUnit 枚举的元素值。
# 可选值：NANOSECONDS MICROSECONDS MILLISECONDS SECONDS MINUTES HOURS DAYS
# 常用值：MILLISECONDS SECONDS MINUTES 
simbot.component.lovelycat.api.cache.durationUnit=MINUTES
```
- 实现Getter。

### core
- `ListenerContext` 中追加两个获取方法。


### fix 
- [#14](https://github.com/ForteScarlet/simpler-robot/issues/14) 监听函数拦截器拦截范围过大
- [#15](https://github.com/ForteScarlet/simpler-robot/issues/15) `MsgInterceptor`追加可获取监听上下文


## ALPHA.12

### lovelycat
- deploy。
- 实现 Setter。

### mirai
- [#17](https://github.com/ForteScarlet/simpler-robot/issues/17) 实现撤回消息内容获取



## BETA.1

### **注意**
未来(可能会出现)的[全平台猫猫码](https://github.com/ForteScarlet/CatCode-multiplatform) 的jvm平台实现将会变更包路径，届时请注意代码兼容问题。

不过变的路径是前缀路径，一个全局替换就能解决的。


### mirai
- 提供新配置项：
```properties
# 默认值为 DEFAULT 
simbot.component.mirai.loginSolverType=DEFAULT

# 如果不为空，此处代表指定一个 deviceInfo 的 json文件路径。
simbot.component.mirai.deviceInfoFile=
```

配置项`loginSolverType`代表mirai组件所使用的登录验证码处理器，值为 ~~`love.forte.simbot.component.mirai.configuration.MiraiLoginSolverType`~~ 枚举的元素值。
可选项：
  - DEFAULT - mirai使用的默认验证码处理器，会根据当前系统环境判断是使用弹窗验证码还是图形字符验证码。
  - SWING - mirai实现的Swing图形验证码处理器。
  - STANDARD_CHAR_IMAGE - mirai实现的字符图片验证码处理器。但是目前来看可能会造成登录卡死。
  - TEMP_IMAGE - simbot-mirai组件实现的临时图片验证码处理器。会将验证码图片保存至临时文件并等待输入。

配置项`deviceInfoFile`允许指定一个 deviceInfo 的 json文件路径。( [#20](https://github.com/ForteScarlet/simpler-robot/issues/20) )

### jackson-serialization
- 发布[jackson序列化模块](serialization/json-jackson), 实现于 [json序列化核心模块](serialization/json-core)。


### fix
[#21](https://github.com/ForteScarlet/simpler-robot/issues/21) `@Filter(anyAt = true)` 无效


## BETA.2

`kotlin -> v1.4.21`

### mirai

更新mirai至`v2.0-M1-1`。可参考：[mirai-2.0-M1](https://github.com/mamoe/mirai/releases/tag/2.0-M1)、[mirai-2.0-M1-1](https://github.com/mamoe/mirai/releases/tag/2.0-M1-1)

此版本的官方更新中似乎对"**登录提示版本过低**"等问题存在改善与修复。

- 更新后，移除配置项：
```
# 以下两个为mirai缓存策略相关。此配置标记为mirai内部API，因此移除相关配置信息。
simbot.component.mirai.cacheType
simbot.component.mirai.cacheDirectory
```

- 不推荐使用配置项：
```
# 登录验证码处理器。只建议选择 DEFAULT 或 TEMP_IMAGE
simbot.component.mirai.loginSolver
```

- 暂时不提供mirai日志的新配置，可通过[高级配置](https://www.yuque.com/simpler-robot/simpler-robot-doc/lnx5nh#jlb7w) 来自定义mirai配置。



## BETA.3

### fix 
- [#25](https://github.com/ForteScarlet/simpler-robot/issues/25) mirai组件（含starter）登录日志卡死。 
- [#26](https://github.com/ForteScarlet/simpler-robot/issues/26) 整合Springboot时配置文件无法正确解析带“-”的键值。


## BETA.4

- [#27](https://github.com/ForteScarlet/simpler-robot/issues/27) 调整模块间关系：
  
  新增[序列化模块](serialization)
    - [json序列化核心](serialization/json-core)
    - [json序列化-moshi实现](serialization/json-moshi)
    - [json序列化-fastjson实现](serialization/json-fastjson)
    - [json序列化-jackson实现](serialization/json-jackson)
  
  新增[http模块](http)
    - [http客户端核心](http/client-core)
    - [http客户端-ktor实现](http/client-ktor)

### spring-boot-starter

- 调整修复classloader问题。   


## BETA.5

### mirai
- 追加配置项：
```properties
simbot.component.mirai.daemon=false
```
此配置为设置保持mirai的存活线程是否作为**守护线程**。一般可以在与spring-boot-web相整合的时候设置为true。

- 优化发送网络图片/语音时的并发请求。

### http-client-ktor
- 更新ktor版本至`v1.4.3`。


## BETA.6

### 核心
- `Setter` 追加API: `setFriendDelete(friend)` - 删除好友

私聊类型(`PrivateMsg.Type`) 增加一个类型 `SELF`, 代表私聊类型为自己。

### time-task
- 实现定时任务模块 ([#24](https://github.com/ForteScarlet/simpler-robot/issues/24)), 并向下兼容一个版本(`v2.0.0-BETA.5`).
所谓向下兼容，即第一个发布的版本为上一个版本，因此你可以直接通过上一个版本的版本号使用此模块，但是无法想要进行统一版本控制，需要手动填入版本号。

  目前定时任务相关模块为：
    - [定时任务核心模块](./time-task/time-task-core)
      - [定时任务模块quartz实现](./time-task/time-task-quartz)
  
使用(以quartz模块为例)：
```xml
  <dependency>
    <groupId>love.forte.simple-robot.time-task</groupId>
    <artifactId>time-task-quartz</artifactId>
</dependency>
```

虽然发布的兼容版本只有上一个版本，但是理论上其他`BETA`旧版本(`2.0.0-BETA.x`)也可以使用。


### mirai
- 更新mirai版本到 [v2.0-M2](https://github.com/mamoe/mirai/releases/tag/2.0-M2) 。
  - 其中，mirai更新了对 `滑动验证` 的支持，可以参考 [mirai文档-登录](https://github.com/mamoe/mirai/blob/dev/docs/Bots.md#2-%E7%99%BB%E5%BD%95) 、 [mirai-login-solver-selenium](https://github.com/project-mirai/mirai-login-solver-selenium) 。
  mirai组件同样不强制引用此依赖，如果需要请添加相关依赖: `net.mamoe:mirai-login-solver-selenium:${version}`
    
- **移除配置项** `simbot.component.mirai.loginSolverType`。

- 使用CatCode发送图片的时候，如果存在id参数，则会通过`Image($id)`构建图片。因此如果id未知，则不要使用id。

- 提供mirai的特殊监听事件 `love.forte.simbot.component.mirai.message.event.MiraiGroupHonorChanged` 荣耀变动事件。
监听方式：`@Listen(MiraiGroupHonorChanged.class)`。
  
- 提供mirai的特殊监听事件 `love.forte.simbot.component.mirai.message.event.MiraiGroupTalkativeChanged` 群龙王变动事件。
监听方式：`@Listen(MiraiGroupTalkativeChanged.class)`。
  
- 提供mirai的特殊监听事件 `love.forte.simbot.component.mirai.message.event.MiraiOtherClientOnline` 其他设备上线事件。
  监听方式：`@Listen(MiraiOtherClientOnline.class)`。
  
- 提供mirai的特殊监听事件 `love.forte.simbot.component.mirai.message.event.MiraiOtherClientOffline` 其他设备下线事件。
  监听方式：`@Listen(MiraiOtherClientOffline.class)`。
  
- 提供mirai的特殊监听事件 `love.forte.simbot.component.mirai.message.event.MiraiOtherClientMessage` 其他客户端发送消息给Bot事件。
  监听方式：此事件会被处理为一个 **私聊消息(`PrivateMsg`)**。你也可以使用mirai的专属事件监听： `@Listen(MiraiOtherClientMessage.class)`
  
- 提供mirai的特殊监听事件 `love.forte.simbot.component.mirai.message.event.MiraiGroupMessageSync` 客户端发送群消息时的同步。
  监听方式：此事件 **不会** 被处理为一个 **群消息(`GroupMsg`)**。使用mirai的专属事件监听：`@Listen(MiraiGroupMessageSync.class)`



### fix
- [#28](https://github.com/ForteScarlet/simpler-robot/issues/28) GroupMsgRecall 获取群信息


## BETA.7

### 核心
追加配置：
```properties
# 是否启用启动时的tips。默认启动
simbot.core.tips.enable=true
# 使用tips时的数据源。可选：GITHUB、GITEE、LOCAL。其中，其他数据源如果失败，最终会尝试使用LOCAL。
# 默认GITEE源。
simbot.core.tips.resource=GITHUB
# 是否展示启动时的logo。默认开启。
simbot.core.logo.enable=true
```

### 新模块
- [http-client-restTemplate](./http/client-restTemplate) 使用spring-web的`restTemplate`的http客户端模块，适用于在整合springboot的时候减少项目环境中的额外依赖。
- [serialization-json-jackson](./serialization/json-jackson) 使用jackson的json序列化模块。一般可用作在整合springboot的时候减少项目环境中的额外依赖。
- [lovelycat-springboot-starter](./spring-boot-starter/component-lovelycat-httpapi-spring-boot-starter) 可爱猫的springboot-starter。使用的时候注意参考文档：https://www.yuque.com/simpler-robot/simpler-robot-doc/rv3ux2


### 新实现
- [#29](https://github.com/ForteScarlet/simpler-robot/issues/29) `@Filter` 和 `@Filters` 提供了一个 `target` 参数以允许使用其他的过滤目标。
  例如`text`则使用`MsgGet.getText`进行过滤，而`msg`则使用`MessageGet.getMsg`进行过滤。
  可以通过操作 `love.forte.simbot.filter.FilterTargetManager` 类的实例来实现追加、移除解析器。
  提供了一个 `FilterTargets` 常量类来降低一些target目标手写的出错概率。
  具体描述可参考 `love.forte.simbot.annotation.Filter#target` 的文档注释或等待文档相关章节的更新。

- [#33](https://github.com/ForteScarlet/simpler-robot/issues/33) 现在 `@Listens`、`@Listen` 和对应的模板注解允许标记在类上。

### fix
- 自定义Filter无法被使用
- [#34](https://github.com/ForteScarlet/simpler-robot/issues/34) mirai组件下动态注册的bot无法触发监听
- [#35](https://github.com/ForteScarlet/simpler-robot/issues/35) 可爱猫组件获取群信息异常


## BETA.8


### 可爱猫
- 优化springboot-starter，如果不加 `@EnableSimbot` 不会再报错了。


### feature
- [#36](https://github.com/ForteScarlet/simpler-robot/issues/36) 优化事件触发机制，当不存在对应的监听函数时，不应当执行事件流程（例如触发拦截器）。

### fix 
- [#39](https://github.com/ForteScarlet/simpler-robot/issues/39) 监听函数优先级失效

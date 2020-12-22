# 2.0.0
## ALPHA.1
first alpha version.


## ALPHA.2
- `MessageEventGet` 更名为 `MessageGet`。

- 优化部分日志输出。
- 调整核心中method-listener的注册方式为全局扫描。

- [core-parent](./core-parent)下新增模块：
    - [http-client-core](./core-parent/http-client-core) http-client模块核心
    - [http-client-ktor](./core-parent/http-client-ktor) ktor-http-client模块
    - [serialization-json-core](./core-parent/serialization-json-core) json序列化模块核心
    - [serialization-json-fastjson](./core-parent/serialization-json-fastjson) fastjson-json序列化模块
    - [serialization-json-moshi](./core-parent/serialization-json-moshi) moshi-json序列化模块

- 追加 [Spring-boot-starter](./spring-boot-starter) 模块及其子模块：
    - [core-spring-boot-starter](./spring-boot-starter/core-spring-boot-starter) 模块
    - [component-mirai-Spring-boot-starter](./spring-boot-starter/component-mirai-spring-boot-starter) 模块


## ALPHA.3

### 核心

core-starter移除部分多余控制台输出。

重新定义 [MessageContent](./core-parent/api/src/main/java/love/forte/simbot/api/message/MessageContent.kt) ，
并在 [mirai组件](./component-parent/component-mirai) 中重新实现。 

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

- 追加1.x中的[钉钉组件](./component-parent/component-ding)。

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

### [lovelycat](./component-parent/component-lovelycat-httpapi)
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

配置项`loginSolverType`代表mirai组件所使用的登录验证码处理器，值为 `love.forte.simbot.component.mirai.configuration.MiraiLoginSolverType` 枚举的元素值。
可选项：
  - DEFAULT - mirai使用的默认验证码处理器，会根据当前系统环境判断是使用弹窗验证码还是图形字符验证码。
  - SWING - mirai实现的Swing图形验证码处理器。
  - STANDARD_CHAR_IMAGE - mirai实现的字符图片验证码处理器。但是目前来看可能会造成登录卡死。
  - TEMP_IMAGE - simbot-mirai组件实现的临时图片验证码处理器。会将验证码图片保存至临时文件并等待输入。

配置项`deviceInfoFile`允许指定一个 deviceInfo 的 json文件路径。( [#20](https://github.com/ForteScarlet/simpler-robot/issues/20) )

### jackson-serialization
- 发布[jackson序列化模块](core-parent/serialization-json-jackson), 实现于 [json序列化核心模块](core-parent/serialization-json-core)。


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

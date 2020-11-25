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

重新定义 [MessageContent](./core-parent/api/src/main/java/love/forte/simbot/api/message/events/MessageContent.kt) ，
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


## ALPHA.9(NOW)

### lovelycat 
- 实现登录账号验证
- 实现群聊消息监听
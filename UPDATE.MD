# 2.0.0
## ALPHA.1
first alpha version.


## ALPHA.2
`MessageEventGet` 更名为 `MessageGet`。

优化部分日志输出。
调整核心中method-listener的注册方式为全局扫描。

[core-parent](./core-parent)下新增模块：
- [http-client-core](./core-parent/http-client-core) http-client模块核心
- [http-client-ktor](./core-parent/http-client-ktor) ktor-http-client模块
- [serialization-json-core](./core-parent/serialization-json-core) json序列化模块核心
- [serialization-json-fastjson](./core-parent/serialization-json-fastjson) fastjson-json序列化模块
- [serialization-json-moshi](./core-parent/serialization-json-moshi) moshi-json序列化模块

追加 [Spring-boot-starter](./spring-boot-starter) 模块及其子模块：
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



### ALPHA.4(now)

### 核心

调整 MessageContentBuilder 的包路径，移出`event`层级。

### 组件

- 追加1.x中的[钉钉组件](./component-parent/component-ding)。






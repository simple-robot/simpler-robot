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
core-starter移除部分多余控制台输出。

# 2.0.0
## ALPHA.1
first alpha version.

## now(ALPHA.2)
`MessageEventGet` 更名为 `MessageGet`。

优化部分日志输出。
调整核心中method-listener的注册方式为全局扫描。

[core-parent](./core-parent)下新增模块：
- [http-client-core](./core-parent/http-client-core)
- [http-client-ktor](./core-parent/http-client-ktor)
- [serialization-json-core](./core-parent/serialization-json-core)
- [serialization-json-fastjson](./core-parent/serialization-json-fastjson)
- [serialization-json-moshi](./core-parent/serialization-json-moshi)

追加 [Spring-boot-starter](./spring-boot-starter) 模块及其子模块：
- [core-Spring-boot-starter](./spring-boot-starter/core-spring-boot-starter) 模块


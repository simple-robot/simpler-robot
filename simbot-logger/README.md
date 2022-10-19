# simbot-logger

用于为其他模块提供简单的日志API的多平台日志模块。


## 平台支持

simbot-logger 是基于 Kotlin Multiplatform 的多平台模块，其支持**所有**可用目标，包括JVM、JS、Native。

- `JVM` 平台中依赖并实现 `slf4j-api`。
- `JS` 平台中基于 `console API`  实现日志输出。
- `Native` 平台通过简单的标准输出 (`println`) 实现日志输出。
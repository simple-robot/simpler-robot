# simbot-logger

用于为其他模块提供简单的日志API的多平台日志模块。


## 平台支持

simbot-logger 是基于 Kotlin Multiplatform 的多平台模块，其支持**所有**可用目标，包括JVM、JS、Native。

### JVM

`JVM` 平台中依赖并实现 `slf4j-api`，但是不提供 `slf4j-api` 的实现。因此在JVM平台中如果只引用 `simbot-logger` 模块则无效果，
需要额外引用其他的 `slf4j-api` 实现，或者使用 [simbot-logger-slf4j-impl](../simbot-logger-slf4j-impl)。

### JS

`JS` 平台中基于 [`console API`](https://developer.mozilla.org/en/DOM/console) 实现日志输出。

### Native

`Native` 平台通过简单的标准输出 (`println`) 实现日志输出。

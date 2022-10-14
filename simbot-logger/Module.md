# Module simbot-logger

simbot3的多平台日志实现，为其它模块提供统一的日志API。

在JVM中，日志API将会委托予 `slf4j-api`。

在JS中，将会使用console API。

而在native目标中，将会通过Kotlin基于最简单的控制台输出实现。

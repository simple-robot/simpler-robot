# Module simbot-logger-slf4j-impl

基于 [simbot-logger](../simbot-logger) 模块的 [SLF4J](http://www.slf4j.org/) API 实现，可以应用于任何使用了 SLF4J API 的地方，
包括 [simbot-logger](../simbot-logger)。


## 配置文件

支持读取配置文件。在项目根路径或资源根路径创建文件 `simbot-logger-slf4j.properties`

```properties
# level 为默认全局等级
level=DEBUG
# 代表前缀为 love.forte.foo1 的日志等级为 TRACE
level.love.forte.foo1=TRACE
# 代表 **控制台输出的日志** 前缀为 love.forte.foo2 时等级为 INFO。
# 某个特定的处理器（例如此处的 console 日志处理器）优先级高于全局配置。
console.level.love.forte.foo2=INFO

# 日志处理的调度模式
# 默认：基于 disruptor 的高性能异步调度
dispatcher=DISRUPTOR
# 或：同步处理，非异步
# dispatcher=SYNC
# 或：普通的基于线程池的异步处理
# dispatcher=SYNC
```

更多可用的配置相关内容参考 [`SimbotLoggerConfiguration`](src/main/kotlin/love/forte/simbot/logger/slf4j/SimbotLoggerConfiguration.kt)。

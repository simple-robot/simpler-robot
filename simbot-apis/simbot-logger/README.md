## LOGGER

Logger模块，提供在simbot环境下默认使用的 [SLF4J API](https://www.slf4j.org/) 日志实现。

`simbot-logger` 提供简易的高性能异步实现，总之会比你使用 `System.out` 要强点儿。

## 日志级别

`simbot-logger` 暂不提供任何配置文件，如果想要调整全局日志等级，添加JVM参数: `simbot.logger.level=LEVEL`。

`LEVEL` 可选值为 `org.slf4j.event.Level` 中的枚举值：

- **`ERROR`**
- **`WARN`**
- **`INFO`**
- **`DEBUG`**
- **`TRACE`**

日志级别默认为 `INFO`。

示例：

```shell
java -jar foo.jar -Dsimbot.logger.level=DEBUG
```

## 日志处理

`simbot-logger` 对于日志的处理全部交由所有的 `love.forte.simbot.logger.SimbotLoggerProcessor` 实现处理。
需要使用的所有 `love.forte.simbot.logger.SimbotLoggerProcessor` 将会由 `love.forte.simbot.logger.SimbotLoggerProcessorsFactory`
的第一个可用的服务实现来提供。

具体参考上述相关接口的文档注释。

当前环境中未提供任何日志处理实现的时候会默认使用控制台输出处理器。
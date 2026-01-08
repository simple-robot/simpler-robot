# Module simbot-component-qq-guild-stdlib

基于api模块，对[QQ频道API](https://bot.q.qq.com/wiki/develop/api/)的基本功能实现（事件接收与处理）。

除了api模块中对API的基本封装之外，stdlib模块还提供了针对BOT功能的**基本**实现，例如BOT鉴权、事件接收与处理等。stdlib模块仅对BOT
相关功能进行**较低限度**的实现，不会有过多封装，更多的保留了整体事件流程的原生性。

如果希望使用一个没有过多的封装、仅有较低限度的事件处理能力的模块，则考虑使用 stdlib模块。

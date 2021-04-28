# now

为 `MsgInterceptChainFactory` 和 `ListenerInterceptChainFactory` 增加一个 `isEmpty` 用于判断当前是否存在相关拦截，
以解决在用户未使用任何拦截器的情况下依旧创建了大量拦截器所需类的问题，以节约资源使用。

简单调整 mirai组件内部的 `BotAliveThread` 终止方式。
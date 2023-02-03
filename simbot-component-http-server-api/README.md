# Component Http Server

用于抽象并提供与 **Http服务器** 有关的事件和Bot类型，以允许事件和Bot支持与Http服务器相关的联动。


## 目标
- 提供的 `HttpServerBot` 来表示一个运行中的Http服务器信息。
- 提供的 `HttpRequestEvent` 来以事件的类型表示Http服务器所接收到的请求。
- 至少能满足可以搭建一个基本的轻量级restful服务器。

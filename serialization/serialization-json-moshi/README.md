# moshi 序列化模块

使用 [moshi](https://github.com/square/moshi) 实现 [序列化核心模块](../serialization-json-core)

moshi对kotlin的空安全更加友好，但是属于需要提前根据类型构建解析器的那种，
并且内置的解析器并不是很丰富，有时候需要自行注入一个 `Moshi.Builder()` 实例到依赖管理中实现自定义解析器。

模块内默认情况下使用的是未经加工的 `new Moshi.Builder()`实例。

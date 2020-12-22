# fastjson 序列化模块

使用 [fastjson](https://github.com/alibaba/fastjson) 实现 [序列化核心模块](../json-core)

相比较 [moshi序列化](../serialization-json-moshi) 而言，fastJson应该是大部分人更加熟悉的json序列化库。
fastjson本质上并不存在提前预设类型解析器，相对而言会更加灵活，因此在[json序列化模块](../json-core)下的实现出错率也会更低一些。

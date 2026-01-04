用于生成：'根据Event的两个子类型定位一个具体的事件类型的解析API' 的处理器。

```kotlin
// 生成 👇

fun resolveEventSerializer(postType: String, subType: String): KSerializer<out Event>? {
    return when (postType) {
        "message" -> when (subType) {
            "private" -> PrivateMessageEvent.serializer()
            "group" -> GroupMessageEvent.serializer()
            else -> null
        }
        // "" -> ...
        // "" -> ...
        // "" -> ...
        else -> null
    }
}

fun resolveEventType(postType: String, subType: String): KClass<out Event>? {
    return ...
}
```

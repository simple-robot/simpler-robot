# Module simbot-extension-continuous-session

> [!warning]
> 尚在试验阶段，随时可能删除或被调整

仍在考虑中。

**持续会话需要解决什么？**

一个监听函数内、连贯的逻辑中，持续处理多次事件。
例如：

```kotlin
val e1 = awaitEvent()
val e2 = awaitEvent()
    ...
```

**持续会话与普通事件处理器之间如何协调？**
由于持续会话需要持续接收事件，因此在等待**下一个**事件时会处于挂起状态，
这时对于后续的其他事件处理器会有较大影响。

因此持续会话这个**会话**本身应当是处于**异步**的。

因此会话开启 -> 异步中开始处理事件，此时又涉及到几个问题：

- 事件唯一标识？
  也许需要一个唯一标识来允许重新获取、分门别类，并允许超时、终止等行为。
- 事件从哪儿来？
  也许需要用户在事件处理器中主动向某个**会话**推送事件？
  
- 事件会话每次推送的响应？
  需要想办法处理每次 `awaitEvent` 时的响应结果，并由此决定时候继续向后传递此事件。


```kotlin
suspend fun inSession(event: Event, sessionContext: ContinuousSessionContext): EventResult {
    val key: ... // 字符串？还是 object？
    
    val sessionProvider = sessionContext.session(key) { // this: sessionReceiver
        // 异步中、可挂起
        val e = await { it.toResult() } // event
    }
    
    // 向会话推送，然后得到一个结果？
    // 比如
    //   不符合预期的结果 invalid
    //   成功的空结果 empty
    //   需要阻断后续事件处理的结果 truncate = true
    val result: EventResult = sessionProvider.push(event)
    
    // 主要逻辑在 session 中，这里只需要返回结果
    return result
}

```


**持续会话的条件过滤？**
持续会话大多有它的条件。例如，我想要连续接收同一个人的事件。

****

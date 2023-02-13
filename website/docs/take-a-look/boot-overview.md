---
sidebar_position: 30
title: boot概览
---

`boot-core` 模块下支持相对更加工程化的使用方式


import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';



<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin title="ExampleListener.kt"
// 监听函数
@Listener
suspend fun ChannelMessageEvent.myListener1() {
    reply(AtAll + "Hello World".toText())

}

// 有标准过滤器的监听函数
@Filter(value = ".*Hi", 
        matchType = MatchType.REGEX_CONTAINS, 
        conponent = "simbot.tencentguild")
@Listener
suspend fun GroupMessageEvent.myListener2() {
    
    group().send("I Love You.".toText() + At(author.id))

}


// 拦截器。此处为监听函数拦截器
@Interceptor
suspend fun myInterceptor() = listenerInterceptor("abc") { context ->
    logger.info("Interceptor {}", context)
    context.proceed() // pass
}
```

</TabItem>
<TabItem value="Java" label="Java">

```java title="ExampleListener.java"
@Beans
public class ExampleListener {
    
    // 监听函数
    @Listener
    public void myListener1(ChannelMessageEvent event) {
        // reply
        final Messages messages = Messages.toMessages(
                    AtAll.INSTANCE,
                    Text.of("Hello World")
            );
        event.replyBlocking(messages);
    }
    
   
    @Listener
    public void myListener2(TcgChannelMessageEvent event) {
     
        // ...
    }
    
    
    // 有标准过滤器的监听函数
    @Listener 
    @Filter(value = ".*Hi", matchType = MatchType.REGEX_MATCHES, conponent = "simbot.tencentguild")
    public void myListener3(GroupMessageEvent event) {
        final Messages messages = Messages.toMessages(
                Text.of("I Love You."),
                new At(event.getAuthor().getId())
        );

        // send to group
        event.getGroup().sendBlocking(messages);

    }
}
```

</TabItem>
</Tabs>




package love.forte.simbot.core.event


/**
 *
 * 核心实现的事件拦截器。
 *
 * @author ForteScarlet
 */
public class CoreEventInterceptor {

}

/*
    事件触发流程：

          全局事件拦截器
              |
              | -------> 事件拦截器A
              |             |
              |             |
              |             | --------> 事件拦截器B
              |             |               |
              |             |               |----> 过滤器A
              |             |               |        |
              |             |               |        | ---test--->  事件监听器A
              |             |               |        |                  |
              |             |               |        |                  |
              |             |               |        | <--invoke-- EventResult
              |             |               |<-------|
              |             |<-EventResult--|
              |             |
              |<------------|
              |
    EventProcessingResult




 */
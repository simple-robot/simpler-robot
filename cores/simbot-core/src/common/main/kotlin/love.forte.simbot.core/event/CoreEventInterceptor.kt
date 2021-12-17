/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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
/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import love.forte.simbot.ID


/**
 *
 * 监听函数容器。
 *
 * @author ForteScarlet
 */
public interface EventListenerContainer {

    /**
     * 通过一个ID得到一个当前监听函数下的对应函数。
     */
    public operator fun get(id: ID): EventListener?

}


/**
 * 监听函数注册器
 */
public interface EventListenerRegistrar {

    /**
     * 注册一个监听函数。
     *
     * @throws IllegalStateException 如果出现ID重复
     */
    public fun register(listener: EventListener)

}


/**
 * 事件监听器管理器标准接口。
 */
public interface EventListenerManager :
    EventProcessor, EventListenerContainer, EventListenerRegistrar

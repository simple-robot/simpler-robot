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
     * 注册监听函数并不一定是原子的，可能会存在注册结果的滞后性（注册不一定会立即生效）。
     *
     * ## 滞后性
     * 例如你在时间点A注册一个监听函数，而时间点B（=时间点A + 10ms）时推送了一个事件，
     *
     * 这时候无法保证在时间点A之后的这个事件能够触发此监听函数。
     *
     * @throws IllegalStateException 如果出现ID重复
     */
    public fun register(listener: EventListener)

}


/**
 * 事件监听器管理器标准接口。
 *
 *
 */
public interface EventListenerManager :
    EventProcessor, EventListenerContainer, EventListenerRegistrar

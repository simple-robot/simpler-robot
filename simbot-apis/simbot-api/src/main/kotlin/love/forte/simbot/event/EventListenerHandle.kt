/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.event


/**
 * 被注册后监听函数的句柄.
 *
 *
 * @author ForteScarlet
 */
public interface EventListenerHandle {
    
    /**
     * 将当前监听函数移除于目标容器中.
     *
     * @return 是否移除成功. 如果目标容器中已经不存在当前句柄所描述的监听函数则会得到 `false`.
     */
    public fun dispose(): Boolean
    
    /**
     * 判断当前句柄所描述的监听函数是否存在于目标容器中.
     */
    public val isExists: Boolean
    
    /**
     * 此句柄所属的 [EventListenerContainer].
     */
    public val container: EventListenerContainer
    
}



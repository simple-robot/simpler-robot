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

package love.forte.simbot.application

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.BotManager
import love.forte.simbot.Component
import love.forte.simbot.event.EventListenerManager


/**
 * `Simple Robot` 的应用.
 *
 * 一个 [simbot应用][Application] 代表了已经配置完毕的运行时作用域，此作用域内包含了有关事件处理、事件推送的相关内容.
 *
 * 每个 [Application] 中，应当包括一个 [事件处理器][EventListenerManager] 、一些 [bot管理器][BotManager] 和一些注册的组件。
 *
 *
 * @author ForteScarlet
 */
public interface Application : CoroutineScope {
    /*
        应用程序的存在意义是提供一个作用域
        作用域涵盖事件的处理，与(可能的)事件的收发

        应用程序的整体流程是
        构建 listener manager, 构建事件监听器, 用作事件处理，首要工作
        构建 bot manager, 向其提供事件处理器, 并在之后用于向事件处理器推送事件

        其中，事件监听需要涉及“组件”。组件应该安装在。。？
        安装在 Application?

        configuration?

        安装组件 组件 factory?

        构建event manager, 提供安装的组件？ manager factory?

        构建 bot managers, manager 和 安装的组件？ managers factory?

     */

    /**
     *
     */
    public val environment: Environment



    /**
     * 当前应用的环境内容，如事件处理器、bot管理器等。
     */
    public interface Environment {
        /**
         * 当前应用下的事件处理器。
         */
        public val eventListenerManager: EventListenerManager

        /**
         * 当前应用下的bot管理器的 **列表视图**。
         */
        public val botManagers: List<BotManager<*>>

        /**
         * 当前应用程序安装的所有组件的 **列表视图**。
         */
        public val components: List<Component>
    }





    /**
     * 挂起此应用直至其被终止。
     */
    public suspend fun join()

    /**
     * 终止当前应用，并关闭其中所有可能的资源。
     */
    public suspend fun shutdown()
}















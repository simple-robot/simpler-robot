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
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.*
import love.forte.simbot.event.EventListenerManager
import love.forte.simbot.utils.runInBlocking


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
    
    /**
     * 当前 [Application] 的环境属性。
     */
    public val environment: Environment
    
    
    /**
     * 在进行构建时所使用的配置信息。
     * 构建完成后可以得到，但是尽可能不要进行修改操作。这可能没有意义，也可能会导致意外的错误。
     */
    public val configuration: ApplicationConfiguration
    
    
    /**
     * 当前应用的组件环境内容。
     */
    public interface Environment {
        
        /**
         * 当前应用程序安装的所有组件的 **列表视图**。
         */
        public val components: List<Component>
        
        /**
         * 尝试根据ID获取一个指定的组件对象。如果未找到则会抛出 [NoSuchComponentException].
         *
         * @throws NoSuchComponentException 当没有找到目标ID的组件时
         */
        public fun getComponent(id: ID): Component
        
        /**
         * 尝试根据ID获取一个指定的组件对象。如果未找到则会返回null。
         */
        public fun getComponentOrNull(id: ID): Component?
        
        
        /**
         * 得到所有组件注册所最终汇总出来的 [SerializersModule] 实例，可用于构建序列化器。
         */
        public val serializersModule: SerializersModule
    }
    
    /**
     * 得到当前 [Application] 最终的 [EventListenerManager].
     */
    public val eventListenerManager: EventListenerManager
    
    /**
     * 当前应用下的 [事件提供者][EventProvider] 的 **列表视图**。
     */
    public val providers: List<EventProvider>
    
    /**
     * 挂起此应用直至其被终止。
     */
    @JvmSynthetic
    public suspend fun join()
    
    
    /**
     * 阻塞的 [join] 当前应用直到其被关闭。
     */
    @Api4J
    public fun joinBlocking(): Unit = runInBlocking { join() }
    
    
    /**
     * 终止当前应用，并关闭其中所有可能的资源。
     *
     * [Application] 被终止后将不能再次启动。
     *
     */
    @JvmSynthetic
    public suspend fun shutdown(reason: Throwable? = null)
    
    /**
     * 终止当前应用，并关闭其中所有可能的资源。
     *
     * [Application] 被终止后将不能再次启动。
     *
     */
    @Api4J
    public fun shutdownBlocking(reason: Throwable?): Unit = runInBlocking { shutdown(reason) }
    
    /**
     * 终止当前应用，并关闭其中所有可能的资源。
     *
     * [Application] 被终止后将不能再次启动。
     *
     */
    @Api4J
    public fun shutdownBlocking(): Unit = runInBlocking { shutdown() }
}


/**
 * 得到目标环境参数中的所有 [BotManager] 实例。
 */
public inline val Application.botManagers: List<BotManager<*>> get() = providers.filterIsInstance<BotManager<*>>()













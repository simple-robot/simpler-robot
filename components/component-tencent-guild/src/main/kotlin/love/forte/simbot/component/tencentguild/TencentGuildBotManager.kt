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

package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import love.forte.simbot.BotManager
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotManagerImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 * QQ频道BOT的bot管理器。
 *
 * @author ForteScarlet
 */
public abstract class TencentGuildBotManager : BotManager<TencentGuildBot>() {


    public companion object {
        @JvmStatic
        public fun newInstance(configuration: TencentGuildBotManagerConfiguration): TencentGuildBotManager {
            return TencentGuildBotManagerImpl(configuration)
        }
    }
}

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class TencentGuildBMDsl


/**
 * 得到一个BotManager.
 */
@TencentGuildBMDsl
public fun tencentGuildBotManager(block: TencentGuildBotManagerConfiguration.() -> Unit): TencentGuildBotManager {
    return TencentGuildBotManager.newInstance(TencentGuildBotManagerConfiguration().also(block))
}


/**
 * [TencentGuildBotManager] 使用的配置类。
 */
@Suppress("MemberVisibilityCanBePrivate")
public class TencentGuildBotManagerConfiguration {


}


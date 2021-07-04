/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.core.strict

import love.forte.simbot.api.SimbotInternalApi
import love.forte.simbot.core.strict.CoreStrictManager.init


/**
 * For lazy enum
 */
private enum class Strict(internal val strict: Boolean) {
    ENABLE(true), DISABLE(false)
}

private fun Boolean.toStrict() : Strict = if (this) Strict.ENABLE else Strict.DISABLE


/**
 * [StrictManager] 的核心实现，用于管理核心与各组件的严格模式开启情况。
 *
 * 需要由核心进行 [初始化][init] 并注入到环境中。
 *
 */
public object CoreStrictManager : StrictManager {

    private lateinit var coreStrict: Strict

    @SimbotInternalApi
    @Synchronized
    fun init(initializer: CoreStrictManagerInitializer = CoreStrictManagerInitializer()) {
        coreStrict = initializer.coreStrict.toStrict()
    }

    override fun coreStrict(): Boolean = coreStrict.strict
}


/**
 * 通过 [CoreStrictManager.init] 进行初始化。
 */
@OptIn(SimbotInternalApi::class)
public inline fun CoreStrictManager.init(initializerBlock: CoreStrictManagerInitializer.() -> Unit) {
    CoreStrictManagerInitializer().also(initializerBlock).let { initializer -> this.init(initializer) }
}


public class CoreStrictManagerInitializer {
    var coreStrict: Boolean = true
}

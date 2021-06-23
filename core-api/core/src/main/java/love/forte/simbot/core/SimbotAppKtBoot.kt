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

@file:JvmName("SimbotAppKtBoot")

package love.forte.simbot.core

import love.forte.common.configuration.Configuration
import love.forte.common.ioc.DependBeanFactory


public inline fun <reified T> runSimbot(
    loader: ClassLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader(),
    parentDependBeanFactory: DependBeanFactory? = null,
    defaultConfiguration: Configuration? = null,
    vararg args: String,
) = SimbotApp.run(
    appType = T::class.java,
    loader = loader,
    parentDependBeanFactory = parentDependBeanFactory,
    defaultConfiguration = defaultConfiguration,
    args = args
)


@Suppress("NOTHING_TO_INLINE")
public inline fun runSimbot(
    app: Any,
    loader: ClassLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader(),
    parentDependBeanFactory: DependBeanFactory? = null,
    defaultConfiguration: Configuration? = null,
    vararg args: String,
) = SimbotApp.run(
    app = app,
    loader = loader,
    parentDependBeanFactory = parentDependBeanFactory,
    defaultConfiguration = defaultConfiguration,
    args = args
)




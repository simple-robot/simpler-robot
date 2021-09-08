/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

@file:JvmName("SimbotResources")
package love.forte.simbot.core

import cn.hutool.core.lang.JarClassLoader
import love.forte.common.collections.emptyIterator
import love.forte.common.collections.plus
import love.forte.common.utils.scanner.ResourcesScanner
import love.forte.simbot.utils.newInputStream
import love.forte.simbot.utils.toProperties
import org.slf4j.Logger
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


/**
 * 记录默认加载的配置类的资源文件路径。
 */
private const val AUTO_CONFIG_PATH = "META-INF/simbot.factories"

private const val AUTO_CONFIG_KEY = "simbot.autoconfigure.configs"
private const val AUTO_SCAN_PACKAGE_KEY = "simbot.autoconfigure.package"

private val JARS_RESOURCES = arrayOf("lib", "SIMBOT-INF/lib")


/**
 * 自动装配信息数据类。
 */
internal data class AutoConfiguresData(val classes: Set<Class<*>>, val packages: Set<String>)


/**
 * 读取所有需要自动装配的类信息。
 */
internal fun autoConfigures(loader: ClassLoader, logger: Logger = simbotAppLogger): AutoConfiguresData {

    // get auto load classes

    val jarResources: Iterator<URL> = JarClassLoader().runCatching {
        ResourcesScanner().apply {
            kotlin.runCatching {
                JARS_RESOURCES.forEach { scan(it) { u -> u.toString().endsWith(".jar") } }
            }
        }.collection.forEach {
            addURL(it.toURL())
        }
        getResources(AUTO_CONFIG_PATH)?.iterator()
    }.getOrElse { e ->
        logger.debug("JarResources load failed. This is an unexpected exception. {}", e.localizedMessage)
        null
    } ?: emptyIterator()


    val loaderResources = loader.getResources(AUTO_CONFIG_PATH)

    val resources = loaderResources.iterator()

    // class list.
    val classSet = mutableSetOf<Class<*>>()
    val packageSet = mutableSetOf<String>()

    (jarResources + resources).forEach { url ->
        logger.debugf("load auto configure resource: {}", url)
        // Properties().apply {
        //     load(BufferedReader(InputStreamReader(url.newInputStream(), StandardCharsets.UTF_8)))
        // }
            url.toProperties().apply {
            getProperty(AUTO_CONFIG_KEY)?.split(",")
                ?.filter { it.isNotBlank() }
                ?.map { Class.forName(it) }
                ?.forEach { classSet.add(it) }

            getProperty(AUTO_SCAN_PACKAGE_KEY)?.split(",")
                ?.filter { it.isNotBlank() }
                ?.forEach { packageSet.add(it) }
        }
    }




    return AutoConfiguresData(classSet, packageSet)
}



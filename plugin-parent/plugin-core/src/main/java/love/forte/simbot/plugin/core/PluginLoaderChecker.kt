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

@file:JvmName("PluginLoaders")
@file:JvmMultifileClass

package love.forte.simbot.plugin.core

import love.forte.common.exception.ResourceException
import love.forte.simbot.utils.getAnnotation
import love.forte.simbot.utils.toProperties
import java.util.*


private const val PLUGIN_INFO_RESOURCE = "META-INF/simbot.factories"
private const val PLUGIN_INFO_KEY = "simbot.plugin.details"


/**
 * 从当前loader中提取 [PluginInfo] 信息。
 */
public fun PluginLoader.extractDetails(): PluginDetails {
    val infoResource = this.getResource(PLUGIN_INFO_RESOURCE) ?: this.getResource("/$PLUGIN_INFO_RESOURCE")
    ?: throw ResourceException("Cannot found plugin factories resource $PLUGIN_INFO_RESOURCE")


    val infoProperties = infoResource.toProperties()
    // Properties().also { p ->
    //     infoResource.openStream().bufferedReader().use { r -> p.load(r) }
    // }

    val detailsClassPath = infoProperties.getProperty(PLUGIN_INFO_KEY)
        ?: throw ResourceException("Cannot found required property '$PLUGIN_INFO_KEY' in plugin factories resource $PLUGIN_INFO_RESOURCE")

    return loadClass(detailsClassPath).newInstance() as PluginDetails
    // return if (infoResource == null) {
    //     val id = pluginDefinition.id
    //     val name = id.split('.').last()
    //     val description = null
    //     val version = "unknown"
    //     val developers: List<Developer> = listOf(UnknownDeveloper)
    //
    //     PluginInfo(id, name, description, version, developers)
    // } else Json.decodeFromString(infoResource.readText())
}


public fun PluginDetails.extractInformation(): PluginInfo {
    return this::class.getAnnotation<SimbotPlugin>()?.toPluginInfo()
        ?: throw IllegalStateException("Cannot found @SimbotPlugin annotation from plugin details: $this (${this::class.java})")
}

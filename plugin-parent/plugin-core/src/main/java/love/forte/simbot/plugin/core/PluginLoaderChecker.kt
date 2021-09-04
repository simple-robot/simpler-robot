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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


const val PLUGIN_INFO_RESOURCE = "META-INF/simbot-plugin.json"


/**
 * 从当前loader中提取 [PluginInfo] 信息。
 */
@OptIn(ExperimentalSerializationApi::class)
public fun PluginLoader.extractInformation(pluginDefinition: PluginDefinition): PluginInfo {
    val infoResource = this.getResource(PLUGIN_INFO_RESOURCE) ?: this.getResource("/$PLUGIN_INFO_RESOURCE")
    return if (infoResource == null) {
        val id = pluginDefinition.id
        val name = id.split('.').last()
        val description = null
        val version = "unknown"
        val developers: List<Developer> = listOf(UnknownDeveloper)

        PluginInfo(id, name, description, version, developers)
    } else Json.decodeFromString(infoResource.readText())
}



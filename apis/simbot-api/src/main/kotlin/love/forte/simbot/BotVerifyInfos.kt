@file:JvmName("BotVerifyInfoUtil")
package love.forte.simbot

import java.util.*


public fun Map<String, String>.asBotVerifyInfo(component: String? = null): BotVerifyInfo {
    return if (component == null) MapBotVerifyInfo(this) else MapBotVerifyInfo(this, component)
}

public fun Properties.asBotVerifyInfo(component: String? = null): BotVerifyInfo {
    val map: MutableMap<String, String> = mutableMapOf()
    for (name in stringPropertyNames()) {
        map[name] = getProperty(name)
    }
    return map.toMap().asBotVerifyInfo(component)
}


private class MapBotVerifyInfo(
    map: Map<String, String>,
    override val component: String = map.find("component", "component_name", "component_id") ?: throw SimbotIllegalArgumentException("Cannot found component.")
) : BotVerifyInfo, Map<String, String> by map


private fun <K, V> Map<K, V>.find(vararg keys: K): V? {
    for (key in keys) {
        val v = get(key)
        if (v != null) return v
    }
    return null
}
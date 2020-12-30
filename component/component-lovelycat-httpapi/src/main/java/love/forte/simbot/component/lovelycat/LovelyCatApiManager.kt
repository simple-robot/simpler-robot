/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatApiManager.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

@file:JvmName("LovelyCatApiManagers")
package love.forte.simbot.component.lovelycat


/**
 * 可爱猫api管理器。
 */
public interface LovelyCatApiManager {
    /** 根据botCode获取api实例。 */
    fun getApi(code: String): LovelyCatApiTemplate?

    /** 记录一条api信息。 */
    fun setApi(code: String, api: LovelyCatApiTemplate)
}



public operator fun LovelyCatApiManager.get(code: String) = getApi(code)
public operator fun LovelyCatApiManager.set(code: String, api: LovelyCatApiTemplate) = setApi(code, api)


/**
 * api记录器。
 */
public class LovelyCatApiMapManager(private val map: MutableMap<String, LovelyCatApiTemplate>) : LovelyCatApiManager {

    /** 根据botCode获取api实例。 */
    override fun getApi(code: String): LovelyCatApiTemplate? = map[code]

    /** 记录一条api信息。 */
    override fun setApi(code: String, api: LovelyCatApiTemplate) {
        map[code] = api
    }
}




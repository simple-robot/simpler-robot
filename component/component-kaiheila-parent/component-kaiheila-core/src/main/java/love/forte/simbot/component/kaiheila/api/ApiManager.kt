/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     ApiManager.kt
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

package love.forte.simbot.component.kaiheila.api


/**
 *
 * 开黑啦API管理器，组件在流程中进行阿皮信息的注册。
 *
 * 目前apiManager的主要作用为管理（可能）存在的多实现api。
 *
 * 截止到目前 (2021/5/25) 为止，开黑啦api还只有一个 `v3` 版本。
 *
 *
 * @author ForteScarlet
 */
public interface ApiManager {

    fun registerApi(api: Api, /* TODO 对应的sender工厂 */)


}


/**
 * 一个开黑啦的API信息。其主要记录（注册）一个API的版本信息，并将其注册到 [ApiManager] 中。
 *
 * [version] 属性将会作为 [Api] 的唯一标识。
 *
 */
public interface Api {
    /**
     * api的版本编号。以 `v3` 为例，版本号则为 `3`。
     */
    val versionNumber: Int

    /**
     * 版本号。以 `v3` 为例，得到的就是 `v3`。
     */
    @JvmDefault
    val version: String get() = "v$versionNumber"

    /**
     * 得到当前版本的API请求路径。
     * 默认情况下即为 `${BASE_URL}/${version}`的形式。
     */
    @JvmDefault
    val apiUrl: String get() = "$BASE_URL/$version"


    companion object {
        /**
         * 开黑啦API的[基础url][https://developer.kaiheila.cn/doc/reference]。
         */
        const val BASE_URL = "https://www.kaiheila.cn/api"
    }
}


/**
 * [Api]基础实现。
 */
public data class ApiData(override val versionNumber: Int): Api


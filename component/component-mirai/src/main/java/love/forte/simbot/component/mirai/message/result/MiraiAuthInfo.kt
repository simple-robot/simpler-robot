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

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.api.message.results.AuthInfo
import love.forte.simbot.api.message.results.asCookies
import love.forte.simbot.component.mirai.sender.Cookies
import love.forte.simbot.thing.StructuralThingWithName

/**
 * mirai权限信息
 * @see Cookies
 */
open class MiraiAuthInfo(private val _cookies: Cookies) : AuthInfo {
    private val _cookiesMap: Map<String, String> by
    lazy(LazyThreadSafetyMode.PUBLICATION) {
        _cookies.toCookiesMap()
    }

    /** cookies信息。 */
    override val cookies: AuthInfo.Cookies get() = auths.asCookies()

    override val auths: AuthInfo.Auths by lazy(LazyThreadSafetyMode.PUBLICATION) {
        MiraiAuthInfoCookies()
    }

    /** 这里的token是bkn */
    override val token: String = _cookies.bkn.toString()
    override val originalData: String = _cookies.toString()

    internal inner class MiraiAuthInfoCookies : AuthInfo.Cookies, AuthInfo.Auths {
        override val name: String
            get() = "COOKIES"
        override val value: String
            get() = ""
        override val children: List<StructuralThingWithName<String>>
            get() = _cookies.cookies

        override fun get(key: String): String? =
            when (key) {
                "bkn" -> _cookies.bkn.toString()
                "gTk" -> _cookies.bkn.toString()
                else -> _cookiesMap[key]
            }

        override fun toMap(): MutableMap<String, String> = _cookiesMap.toMutableMap()
        override fun toString(): String = _cookies.toString()
    }


}



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

package love.forte.simbot.utils

import java.security.MessageDigest

public object MD5 {
    /**
     * 取MD5。
     */
    operator fun get(value: String): String {
        return runCatching {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest: ByteArray = instance.digest(value.toByteArray())
            // val sb = StringBuilder()
            buildString {
                for (b in digest) {
                    //获取低八位有效值
                    val i: Int = b.toInt() and 0xff
                    //将整数转化为16进制
                    val hexString = Integer.toHexString(i)
                    if (hexString.length < 2) {
                        //如果是一位的话，补0
                        append('0')
                        // hexString = "0" + hexString
                    }
                    append(hexString)
                }
            }
        }.getOrDefault("")
    }
}
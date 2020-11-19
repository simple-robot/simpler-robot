/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     HttpRequestException.kt
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

package love.forte.simbot.http


/**
 * http 请求异常
 */
public class HttpTemplateException : IllegalStateException {
    constructor(status: Int, err: String?) : super("status: $status, message: ${err ?: ""}")
    constructor(status: Int, err: String?, e: Throwable) : super("status: $status, message: ${err ?: ""}", e)
}
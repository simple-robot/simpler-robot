/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Communicator.kt
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

package love.forte.simbot.api.sender

import kotlinx.coroutines.runBlocking
import love.forte.simbot.api.message.results.Result


/**
 * 通讯器接口，为三个送信器的统一父接口。
 *
 *
 * 通讯器接口定义一个额外执行 [additionalExecute] 入口以支持组件对各自的特殊api进行实现与支持。
 *
 *
 * @see Setter
 * @see Getter
 * @see Sender
 */
public interface Communicator {

    fun <R : Result> additionalExecute(additionalApi: AdditionalApi<R>): R {
        return runBlocking { execute(additionalApi) }
        // return additionalApi.defaultValue ?: throw SimbotAdditionalApiException("Additional api '${additionalApi.additionalApiName}' not support.")
    }

    /**
     * 执行一个额外的API。
     *
     * 对于返回值的null可能性，由 [AdditionalApi] 的实现进行决定。
     *
     * @throws love.forte.simbot.SimbotRuntimeException 可能存在任何错误，例如不支持的额外api、api执行异常等。
     * @throws SimbotAdditionalApiException 可能存在任何错误，例如不支持的额外api、api执行异常等。
     */
    @JvmSynthetic
    suspend fun <R : Result> execute(additionalApi: AdditionalApi<R>): R {
        return additionalApi.defaultValue ?: throw SimbotAdditionalApiException("Additional api '${additionalApi.additionalApiName}' not support.")
    }

}
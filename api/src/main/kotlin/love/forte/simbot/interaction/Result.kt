/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmName("ResultUtil")

package love.forte.simbot.interaction

import kotlinx.serialization.Serializable

/**
 * 一次交互的结果。
 *
 * @author ForteScarlet
 */
public sealed interface Result<T> {

    /**
     * 这次的 **结果**。 假若出现了异常，直接获取此 [valueOr] 会抛出当时的异常。
     * 你可以在获取 [valueOr] 之前对 [exception] 进行判断，并处理可能存在的异常。
     *
     * @throws Exception 当 [exception] 不为空，即在交互时得到错误的时候。
     *
     * @see getValueOrNull
     * @see Result.valueOr
     */
    public val value: T


    /**
     * 结果有可能会存在失败后的异常，且在失败的时候应当必然存在异常。
     */
    public val exception: Throwable?

    /**
     * 当 [exception] 不为null的时候，得到null，否则得到 [valueOr]。
     */
    public fun getValueOrNull(): T? = valueOr { null }
}


public inline fun <T, R : T> Result<R>.valueOr(exceptionProcessor: (Throwable) -> T = { e -> throw e }): T =
    exception?.let(exceptionProcessor) ?: value


public object EmptyResult : Result<Any?> {
    override val value: Any? get() = null
    override val exception: Throwable? get() = null
}

/**
 * 一次失败的结果，始终会得到一个 [异常][exception]。
 */
public data class ErrorResult(override val exception: Throwable) :
    Result<Nothing> {
    override val value: Nothing
        get() = throw exception
}

/**
 * 一个成功的结果，不存在 [异常][exception].
 */
@Serializable
public data class SuccessResult<T>(override val value: T) : Result<T> {
    override val exception: Throwable?
        get() = null
}





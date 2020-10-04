/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Context.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.intercept


/**
 *
 * 上下文接口，用于在[Interceptor]中作为参数。
 *
 * 上下文中存在一个对应的 [mainValue] 代表当前拦截中的拦截主体。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface Context<T> {

    /**
     * 主体信息。
     */
    val mainValue: T?
}


/**
 * [Context] 的抽象实现类。
 */
public abstract class BaseContext<T>(override val mainValue: T) : Context<T>

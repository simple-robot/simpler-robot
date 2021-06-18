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

package love.forte.simbot


/**
 *
 * **上下文** 接口。
 * 所谓上下文，一般情况下指代某个流程或者功能所能提供的（或许存在各种关联性的）参数。
 *
 * 一个上下文接口代表其实现者中存在一个 [主体信息][mainValue]。
 *
 * 例如, 上下文接口使用与 [拦截器][love.forte.simbot.intercept.Interceptor] 或 [处理器][love.forte.simbot.processor.Processor] 接口中以规定他们各自实现类的 **主体信息**。
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

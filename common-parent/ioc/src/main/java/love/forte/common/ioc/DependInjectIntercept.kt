/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     DependInjectIntercept.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc


/**
 * depend 注入前的拦截。
 */
public interface DependInjectIntercept<B> {

    fun intercept(beanDepend: BeanDepend<B>) : BeanDepend<B>

}

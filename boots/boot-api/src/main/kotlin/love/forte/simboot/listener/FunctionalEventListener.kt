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

package love.forte.simboot.listener

import kotlin.reflect.KFunction

/**
 *
 * 基于函数体 [KFunction] 的监听函数执行器。
 *
 * @author ForteScarlet
 */
public abstract class FunctionalEventListener<R> : GenericBootEventListener {
    protected abstract val caller: KFunction<R>
}
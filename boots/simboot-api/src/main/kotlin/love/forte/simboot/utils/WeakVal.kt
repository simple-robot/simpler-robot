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

package love.forte.simboot.utils

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

public class WeakVal<T>(init: Boolean, private val getFunc: () -> T) {

    private var weak = WeakReference<T>(if (init) getFunc() else null)

    public operator fun getValue(instance: Any, property: KProperty<*>): T {
        return weak.get() ?: synchronized(this) {
            weak.get() ?: getFunc().also { weak = WeakReference(it) }
        }
    }
}
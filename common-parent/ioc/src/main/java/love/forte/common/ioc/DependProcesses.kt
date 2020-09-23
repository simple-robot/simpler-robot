/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     DependProcesss.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc

import java.io.Closeable

/**
 * close, 当Depend被close的时候会执行此函数.
 */
public interface CloseProcesses : Closeable {

    /**
     * 执行close操作。
     * 应当处理所有可能出现的异常，而不是向上抛出。
     */
    override fun close()
}

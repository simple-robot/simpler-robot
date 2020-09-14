/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     NekoLoggerFactory.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.nekolog

import org.slf4j.ILoggerFactory
import org.slf4j.Logger


/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface NekoLoggerFactory : ILoggerFactory




public class BaseNekoLoggerFactory : NekoLoggerFactory {
    override fun getLogger(name: String?): Logger {
        TODO("Not yet implemented")
    }
}

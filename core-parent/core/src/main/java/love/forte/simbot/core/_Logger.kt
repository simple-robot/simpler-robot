/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     _Logger.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core

import love.forte.nekolog.NekoLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory


public abstract class CPLogger(name: String) {
    val logger: Logger = LoggerFactory.getLogger(name)
}


class Test1 {
    companion object : CPLogger("Test1~name")

}


fun main() {


    Test1.logger.info("233")
    Test1.logger.debug("233")
    Test1.logger.error("233")
    Test1.logger.warn("233")
    Test1.logger.trace("233")


}







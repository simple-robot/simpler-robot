/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simboot.spring.autoconfigure.bk

import love.forte.simboot.SimbootEntranceContext
import love.forte.simboot.factory.ConfigurationFactory
import org.springframework.core.env.ConfigurableEnvironment

// region Configurations
public open class SpringConfigurationFactory(
    private val environment: ConfigurableEnvironment,
) : ConfigurationFactory {
    override fun invoke(context: SimbootEntranceContext): love.forte.simboot.Configuration {
        return SpringEnvironment(environment)
    }
}


private class SpringEnvironment(private val environment: ConfigurableEnvironment) : love.forte.simboot.Configuration {
    
    override fun getString(key: String): String? {
        return environment.getProperty(key) ?: environment.getProperty(resetKey(key))
    }
    
    private fun resetKey(key: String): String {
        val sb = StringBuilder(key.length)
        // 将驼峰转化为短杠
        for (c: Char in key) {
            if (c.isUpperCase()) {
                sb.append('-').append(c.lowercaseChar())
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }
}
// endregion

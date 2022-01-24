/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.autoconfigure

import love.forte.simboot.SimbootEntranceContext
import love.forte.simboot.factory.ConfigurationFactory
import org.springframework.core.env.ConfigurableEnvironment

//region Configurations
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
//endregion

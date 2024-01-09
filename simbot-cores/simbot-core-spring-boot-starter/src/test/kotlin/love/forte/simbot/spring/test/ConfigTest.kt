/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.spring.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component


@Component
open class Runner(@Autowired(required = false) val provider: MyProvider?) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println("provider: $provider")
        provider?.run()
    }
}


@Import(DefaultMyProviderConfiguration::class)
annotation class EnableIncludeTest

interface MyProvider {
    fun run()
}

@Configuration
@Import(DefaultSubConfiguration::class)
@ConditionalOnMissingBean(MyProvider::class)
open class DefaultMyProviderConfiguration {
    @Bean
    @ConditionalOnMissingBean(MyProvider::class)
    open fun defaultProvider(): DefaultMyProvider = DefaultMyProvider
}

object DefaultMyProvider : MyProvider {
    override fun run() {
        println("DEFAULT PROVIDER!")
    }
}

@Configuration(proxyBeanMethods = false)
open class DefaultSubConfiguration {
    @Bean
    open fun hello() = Hello("forte")
}

data class Hello(val name: String)

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

package love.forte.simbot.spring.test.main

import love.forte.simbot.spring.test.EnableIncludeTest
import love.forte.simbot.spring.test.Hello
import love.forte.simbot.spring.test.MyProvider
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


/**
 *
 * @author ForteScarlet
 */
@SpringBootApplication
@EnableIncludeTest
open class MainConfigApp

fun main() {
    runApplication<MainConfigApp>()
}

@Component
open class Runner(
    @Autowired(required = false) private val provider: MyProvider?,
    @Autowired(required = false) private val hello: Hello?
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        Assertions.assertNotNull(provider)
        Assertions.assertNotNull(hello)
    }
}


@Configuration(proxyBeanMethods = false)
open class OtherProviderConfiguration {
    @Bean
    open fun otherProvider(): MyProvider = object : MyProvider {
        override fun run() {
            println("Other Provider!")
        }

    }
}

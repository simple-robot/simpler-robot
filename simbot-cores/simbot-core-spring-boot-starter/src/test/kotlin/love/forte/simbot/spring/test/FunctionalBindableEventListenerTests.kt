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

import kotlinx.coroutines.delay
import love.forte.simbot.application.ApplicationLauncher
import love.forte.simbot.quantcat.common.annotations.Listener
import love.forte.simbot.quantcat.common.binder.BinderManager
import love.forte.simbot.spring.EnableSimbot
import love.forte.simbot.spring.configuration.listener.SimbotEventListenerResolver
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component


@SpringBootTest(
    classes = [FunctionalBindableEventListenerTests::class, ListenerContainer::class]
)
@EnableSimbot
class FunctionalBindableEventListenerTests {


    @Test
    fun test(
        @Autowired launcher: ApplicationLauncher<*>,
        @Autowired binderManager: BinderManager,
        @Autowired(required = false) eventListenerResolvers: List<SimbotEventListenerResolver>?
    ) {
        println(binderManager)
        println(eventListenerResolvers)
        println(launcher)

        // eventListenerResolvers?.forEach { r ->
        //     r.resolve(launchSimpleApplication {  })
        // }
    }

}

@Component
private class ListenerContainer {
    @Listener
    suspend fun runner() {
        delay(1)
    }
}

/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.spring.test

import kotlinx.coroutines.runBlocking
import love.forte.simbot.application.Application
import love.forte.simbot.spring.EnableSimbot
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


/**
 *
 * @author ForteScarlet
 */
@SpringBootApplication
@EnableSimbot
class Main

fun main(args: Array<String>) {
    val context = runApplication<Main>(*args)
    val app = context.getBean<Application>()
    runBlocking { app.join() }
}

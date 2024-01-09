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

package love.forte.simbot.spring.configuration.application

import kotlinx.coroutines.runBlocking
import love.forte.simbot.application.Application
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.spring.application.ApplicationLaunchMode.NONE
import love.forte.simbot.spring.application.ApplicationLaunchMode.THREAD
import love.forte.simbot.spring.application.SpringApplication
import love.forte.simbot.spring.application.SpringApplicationConfigurationProperties
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.annotation.Order


/**
 *
 * @author ForteScarlet
 */
@Order(Int.MAX_VALUE)
public open class SimbotApplicationRunner(
    private val application: SpringApplication,
    private val properties: SpringApplicationConfigurationProperties,
    private val applicationProcessor: SimbotApplicationProcessor
) : ApplicationRunner, ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext
    private var launchThread: ApplicationLaunchThread? = null

    override fun run(args: ApplicationArguments?) {
        applicationProcessor.process(application)
        registerOnComplete()
        launchApp()
    }

    private fun registerOnComplete() {
        application.onCompletion { cause ->
            if (cause != null) {
                logger.info("Application {} was on completion with cause: {}", application, cause.message)
                logger.debug("Application {} was on completion with cause: {}", application, cause.message, cause)
            } else {
                logger.info("Application {} was on completion", application)
            }
        }
    }

    private fun launchApp() {
        when (properties.application.applicationLaunchMode) {
            THREAD -> {
                launchThread = ApplicationLaunchThread(application).also { it.start() }
            }
            NONE -> {
                // nothing.
            }
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    private class ApplicationLaunchThread(private val app: Application) : Thread("Simbot-Spring-Launch-Thread") {
        override fun run() {
            try {
                runBlocking { app.join() }
                logger.info("ApplicationLaunchThread done.")
            } catch (e: Throwable) {
                logger.info("ApplicationLaunchThread done on failure: {}", e.localizedMessage, e)
            }
        }
    }

    public companion object {
        private val logger = LoggerFactory.logger<SimbotApplicationRunner>()
    }
}

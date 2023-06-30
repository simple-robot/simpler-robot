/*
 * Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forli.test

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import love.forte.simboot.spring.autoconfigure.EnableSimbot
import love.forte.simbot.Attribute
import love.forte.simbot.ComponentFactory
import love.forte.simbot.application.Application
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@EnableSimbot
@SpringBootApplication
open class SpringBootApp


fun main(vararg args: String) {
    val app = runApplication<SpringBootApp>(args = args)

    val simbotApp = app.getBean<Application>()

    println(simbotApp.environment.components)

    println(simbotApp.environment.serializersModule)

    // TODO Json decode BUG
    println(Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule = simbotApp.environment.serializersModule
    }.decodeFromString<Root>("""{"config": {"a": 1}}"""))
}

@Configuration
open class CompConfig {

    @Bean
    open fun myCp() = MyComponent
}

@Serializable
data class Root(val config: Config)

@Serializable
sealed class Config {
    @Serializable
    @SerialName("def")
    class Default : Config()

    @Serializable
    @SerialName("value")
    data class Value(val value: Int) : Config()
}

class MyComponent : love.forte.simbot.Component {
    override val id: String = "simbot.test"
    override val componentSerializersModule: SerializersModule = SerializersModule {
        polymorphicDefaultDeserializer(Config::class) { Config.Default.serializer() }
    }

    companion object Factory : ComponentFactory<MyComponent, Unit> {
        override val key: Attribute<MyComponent> = Attribute.Companion.of("simbot.test")

        override suspend fun create(configurator: Unit.() -> Unit): MyComponent = MyComponent()
    }
}

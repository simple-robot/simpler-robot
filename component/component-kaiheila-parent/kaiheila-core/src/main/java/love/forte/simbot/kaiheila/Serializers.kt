/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("SerializerModuleRegistrars")

package love.forte.simbot.kaiheila

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import love.forte.simbot.component.kaiheila.objects.*
import java.util.concurrent.CopyOnWriteArraySet


public interface SerializerModuleRegistrar {
    fun SerializersModuleBuilder.serializerModule()
}


internal val serializerModuleRegistrars = CopyOnWriteArraySet<SerializerModuleRegistrar>()


internal fun MutableCollection<SerializerModuleRegistrar>.init() {
    add(Role)
    add(Channel)
    add(User)
    add(Guild)
    add(Quote)
    add(Attachments)
    add(KMarkdown)
    // add(Card) TODO
}


public data class KaiheilaJson(val json: Json)


// @get:TestOnly
public val khlJson: Json by lazy {
    Json {
        serializerModuleRegistrars.init()
        serializersModule = SerializersModule {
            serializerModuleRegistrars.forEach {
                it.apply {
                    serializerModule()
                }
            }
        }
        isLenient = true
        ignoreUnknownKeys = true
        classDiscriminator = "#KHLT"

    }
}

internal object BooleanAsIntSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BooleanAsInt", PrimitiveKind.INT)
    override fun deserialize(decoder: Decoder): Boolean = decoder.decodeInt() == 0
    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeInt(if (value) 0 else 1)
    }
}
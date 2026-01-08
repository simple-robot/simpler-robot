/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package test

import io.ktor.client.request.forms.*
import io.ktor.http.content.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.qguild.api.message.QGMessageForSending
import love.forte.simbot.qguild.model.Message


/**
 *
 * @author ForteScarlet
 */
class QGMessageForSendingToFormDataTest {
    
//    @Test
    fun serializerTest() {
        val param = QGMessageForSending(
            "content",
            image = "image",
            embed = Message.Embed("title", "desc", Message.Embed.Thumbnail("URL"), emptyList())
        )
        
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
        
        val ser = QGMessageForSending.serializer()
        
        val formData = formData {
            val decoder = TencentMessageForSendingFormDataDecoder(
                json.serializersModule,
                json,
                this
            )
            
            ser.serialize(decoder, param)
        }
        
        
        println(formData)
        println()
        formData.forEach {
            println("name = ${it.name}")
            if (it is PartData.FormItem) {
                println("value = ${it.value}")
            }
        }
    }
    
}

@OptIn(ExperimentalSerializationApi::class)
private class TencentMessageForSendingFormDataDecoder(
    override val serializersModule: SerializersModule,
    private val json: Json,
    private val formBuilder: FormBuilder,
) : Encoder, CompositeEncoder {
    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value.toString())
    }
    
    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }
    
    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value.toString())
    }
    
    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }
    
    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }
    
    @ExperimentalSerializationApi
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        return this
    }
    
    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }
    
    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }
    
    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        if (value != null) {
            val name = descriptor.getElementName(index)
            formBuilder.append(name, json.encodeToString(serializer, value))
        }
    }
    
    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        encodeNullableSerializableElement(descriptor, index, serializer, value)
    }
    
    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }
    
    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }
    
    override fun endStructure(descriptor: SerialDescriptor) {
    }
    
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return this
    }
    
    
    override fun encodeBoolean(value: Boolean) {
    }
    
    override fun encodeByte(value: Byte) {
    }
    
    override fun encodeChar(value: Char) {
    }
    
    override fun encodeDouble(value: Double) {
    }
    
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
    }
    
    override fun encodeFloat(value: Float) {
    }
    
    @ExperimentalSerializationApi
    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        return this
    }
    
    override fun encodeInt(value: Int) {
    }
    
    override fun encodeLong(value: Long) {
    }
    
    @ExperimentalSerializationApi
    override fun encodeNull() {
    }
    
    override fun encodeShort(value: Short) {
    }
    
    override fun encodeString(value: String) {
    }
}

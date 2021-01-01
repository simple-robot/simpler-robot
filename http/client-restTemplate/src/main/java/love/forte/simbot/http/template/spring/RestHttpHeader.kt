/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     RestHttpHeader.java
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */
package love.forte.simbot.http.template.spring

import love.forte.simbot.http.template.HttpHeaders
import org.springframework.http.HttpHeaders as RestTemplateHttpHeaders

/**
 * [HttpHeaders] 针对于 [RestTemplateHttpHeaders] 的委托实现.
 * @author ForteScarlet
 */
public class RestHttpHeader(private val delegate: RestTemplateHttpHeaders) : HttpHeaders {
    override val size: Int
        get() = delegate.size

    override fun containsKey(key: String): Boolean {
        return delegate.containsKey(key)
    }

    override fun get(key: String): MutableList<String>? {
        return delegate[key]
    }

    override fun remove(key: String): MutableList<String>? {
        return delegate.remove(key)
    }

    override val keys: MutableSet<String>
        get() = delegate.keys

    override val values: MutableCollection<MutableList<String>>
        get() = delegate.values
    override val entries: MutableSet<MutableMap.MutableEntry<String, MutableList<String>>>
        get() = delegate.entries


    override fun getFirst(headerKey: String): String? {
        return delegate.getFirst(headerKey)
    }

    override fun getIndexed(headerKey: String, index: Int): String? {
        val header = delegate[headerKey] ?: throw NullPointerException("Header '$headerKey' is null. ")
        return header[index]
    }

    override fun add(header: String, value: String) {
        delegate.add(header, value)
    }

    override fun addMultiple(header: String, headerValues: Collection<String>) {
        delegate.addAll(header, headerValues as List<String>)
    }

    override operator fun set(header: String, value: String) {
        delegate[header] = value
    }

    /**
     * IsEmpty.
     */
    override fun isEmpty(): Boolean {
        return delegate.isEmpty()
    }

    /**
     * Clear.
     */
    override fun clear() {
        delegate.clear()
    }

    /**
     * Associates the specified [value] with the specified [key] in the map.
     *
     * @return the previous value associated with the key, or `null` if the key was not present in the map.
     */
    override fun put(key: String, value: MutableList<String>): MutableList<String>? {
        return delegate.put(key, value)
    }

    /**
     * Updates this map with key/value pairs from the specified map [from].
     */
    override fun putAll(from: Map<out String, MutableList<String>>) {
        return delegate.putAll(from)
    }

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    override fun containsValue(value: MutableList<String>): Boolean {
        return delegate.containsValue(value)
    }



}

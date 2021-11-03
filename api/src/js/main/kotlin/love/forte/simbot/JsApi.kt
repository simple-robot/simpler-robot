package love.forte.simbot

import love.forte.simbot.resources.Resource

/**
 * @author ForteScarlet
 */
public object JsApi


public class MyRes : Resource {
    override val name: String
        get() = "My"
    override val bytes: ByteArray
        get() = byteArrayOf()

}
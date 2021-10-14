package love.forte.simbot.component.kaiheila.v3.configuration

import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.MessageContentBuilder
import love.forte.simbot.api.message.MessageContentBuilderFactory
import love.forte.simbot.kaiheila.event.message.TextOnlyMessageContent
import love.forte.simbot.processor.RemoteResourceInProcessor
import java.io.InputStream

/**
 *
 * @author ForteScarlet
 */
public class KhlMessageContentBuilderFactory(
    private val remoteResourceInProcessor: RemoteResourceInProcessor,
) : MessageContentBuilderFactory {
    override fun getMessageContentBuilder(): MessageContentBuilder {
        return KhlMessageContentBuilder(remoteResourceInProcessor)
    }
}



public class KhlMessageContentBuilder(
    private val remoteResourceInProcessor: RemoteResourceInProcessor
) : MessageContentBuilder {

    private val stringBuilder = StringBuilder()

    override fun text(text: CharSequence) = also {
        stringBuilder.append(text)
    }

    override fun atAll() = also {
        TODO("Not yet implemented")
    }

    override fun at(code: String) = also {
        TODO("Not yet implemented")
    }

    override fun face(id: String) = also {
        TODO("Not yet implemented")
    }

    override fun imageLocal(path: String, flash: Boolean) = also {
        TODO("Not yet implemented")
    }

    override fun imageUrl(url: String, flash: Boolean) = also {
        TODO("Not yet implemented")
    }

    override fun image(input: InputStream, flash: Boolean) = also {
        TODO("Not yet implemented")
    }

    override fun image(imgData: ByteArray, flash: Boolean) = also {
        TODO("Not yet implemented")
    }

    override fun build(): MessageContent {
        // TODO("Not yet implemented")
        return TextOnlyMessageContent(stringBuilder.toString())
    }

    override fun clear() = also {
        stringBuilder.clear()
    }
}
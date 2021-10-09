package love.forte.simbot.api.message

import love.forte.simbot.Simbot
import love.forte.simbot.api.Component
import love.forte.simbot.api.ComponentContainer


/**
 *
 * [Messages] 构建器。
 *
 * @author ForteScarlet
 */
public class MessagesBuilder(
    component: Component? = null,
    private val list: MutableList<AbsoluteMessage> = mutableListOf()
) {
    private lateinit var component: Component
    init {
        if (component != null) {
            this.component = component
        }
    }

    private fun initOrCheckComponent(componentContainer: ComponentContainer) {
        if (!::component.isInitialized) {
            component = componentContainer.component
        } else {
            Simbot.check(component == componentContainer.component) { "All components in Messages must be consistent. Builder current component was $component, but ${componentContainer.component} ." }
        }
    }


    /**
     * 追加一个 [AbsoluteMessage].
     */
    public fun append(message: Message): MessagesBuilder = also {
        initOrCheckComponent(message)

        when (message) {
            is AbsoluteMessage -> appendAbsoluteMessage(message)
            is Messages -> when (val messages: Messages = message) {
                EmptyMessages -> return@also // Do nothing.
                is SingleOnlyMessage -> appendSingleOnlyMessage(messages)
                is MessagesImpl -> appendMessagesImpl(messages)
            }
        }
    }

    private fun appendMessagesImpl(messages: MessagesImpl) {

        TODO()
    }

    private fun appendSingleOnlyMessage(singleOnlyMessage: SingleOnlyMessage) {

        TODO()
    }

    private fun appendAbsoluteMessage(message: AbsoluteMessage) {
        if (list.isEmpty()) {
            list.add(message)
            // addKey(message)
        } else {
            var addable = true

            for (i in list.indices.reversed()) {
                val m = list[i]
                if (m.key conflict message.key) {
                    when (m) {
                        is UniqueMessage<*> -> {
                            addable = m.key.solve(m, message).option(list, i, message)
                            break
                        }
                        is PluralMessage -> TODO("可重复消息的冲突处理")
                    }
                }

            }

            if (addable) {
                list.add(message)
            }
        }
    }




    public fun build(): Messages {
        if (list.isEmpty()) return emptyMessages()


        return MessagesImpl(component, list.toList())
    }
}
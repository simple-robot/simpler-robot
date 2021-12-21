/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package jobtest

import love.forte.di.annotation.Beans
import love.forte.simboot.annotation.Listener
import love.forte.simboot.factory.BotRegistrarFactory
import love.forte.simbot.*
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.MessageEvent

/**
 *
 * @author ForteScarlet
 */
@Beans
class MyListener {

    @Listener
    fun MessageEvent.listener() {

    }

}

@Beans
class MyRegistrar : BotRegistrarFactory {
    override fun invoke(p1: EventProcessor): BotRegistrar {
        println("Processor: $p1")
        return object : BotRegistrar {
            override fun register(verifyInfo: BotVerifyInfo): Bot {

                TODO("Not yet implemented")
            }

            override val component: Component = SimbotComponent
        }
    }
}
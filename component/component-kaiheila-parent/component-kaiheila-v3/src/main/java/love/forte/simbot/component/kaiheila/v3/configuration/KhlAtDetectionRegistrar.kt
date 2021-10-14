package love.forte.simbot.component.kaiheila.v3.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.PrePass
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.core.filter.CatAtDetectionFactory
import love.forte.simbot.filter.AlwaysRefuseAtDetection
import love.forte.simbot.filter.AtDetection
import love.forte.simbot.filter.AtDetectionFactory
import love.forte.simbot.filter.AtDetectionRegistrar
import love.forte.simbot.kaiheila.botCode
import love.forte.simbot.kaiheila.event.message.MessageEvent

/**
 *
 * @author ForteScarlet
 */
@ConfigBeans("kaiheilaAtDetectionRegistrar")
public class KhlAtDetectionRegistrar {
    /**
     * 注册一个 [CatAtDetectionFactory]。
     */
    @PrePass(priority = PriorityConstant.COMPONENT_TENTH)
    fun registerMiraiAtDetectionFactory(registrar: AtDetectionRegistrar) {
        registrar.registryAtDetectionFirst(KhlAtDetectionFactory)
    }

}

private object KhlAtDetectionFactory : AtDetectionFactory {
    override fun getAtDetection(msg: MsgGet): AtDetection {
        return if (msg is MessageEvent<*>) KhlAtDetection(msg)
        else AlwaysRefuseAtDetection
    }
}


private class KhlAtDetection(private val event: MessageEvent<*>) : AtDetection {
    private val botCode = event.bot.botCode
    override fun atBot(): Boolean = event.extra.mention.contains(botCode)
    override fun atAll(): Boolean = event.extra.mentionAll

    override fun atAny(): Boolean = with(event.extra) {
        mentionHere || mention.isNotEmpty() || mentionRoles.isNotEmpty()
    }

    override fun at(codes: Array<String>): Boolean = with(event.extra) {
        mention.all { men -> men in codes }
                || mentionRoles.all { roleMen -> roleMen.toString() in codes }
    }

}
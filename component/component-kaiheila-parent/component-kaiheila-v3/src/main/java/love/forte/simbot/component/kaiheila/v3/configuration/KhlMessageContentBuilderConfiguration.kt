package love.forte.simbot.component.kaiheila.v3.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.processor.RemoteResourceInProcessor

/**
 *
 * @author ForteScarlet
 */
@ConfigBeans("khlMessageContentBuilderConfiguration")
class KhlMessageContentBuilderConfiguration {
    /**
     * mirai的content builder factory.
     */
    @ComponentBeans(value = "miraiMessageContentBuilderFactory", init = false)
    fun miraiMessageContentBuilderFactory(
        remoteResourceInProcessor: RemoteResourceInProcessor,
    ) = KhlMessageContentBuilderFactory(remoteResourceInProcessor)

}
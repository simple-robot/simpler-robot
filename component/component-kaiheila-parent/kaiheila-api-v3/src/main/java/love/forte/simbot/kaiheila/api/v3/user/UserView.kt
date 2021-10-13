package love.forte.simbot.kaiheila.api.v3.user

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.component.kaiheila.api.*
import love.forte.simbot.component.kaiheila.objects.User


/**
 *
 * 查询用户信息
 *
 */
public class UserViewReq(
    private val userId: String,
    private val guildId: String,
) : BaseApiDataReq<ObjectResp<User>>(Key), GetUserApiReq<ObjectResp<User>> {
    companion object Key : BaseApiDataKey("user", "view") {
        private val dataSerializer = objectResp<User>()
    }
    override val dataSerializer: DeserializationStrategy<ObjectResp<User>>
        get() = Key.dataSerializer

    override fun RouteInfoBuilder.doRoute() {
        parameters {
            append("user_id", userId)
            append("guild_id", guildId)
        }
    }

    override fun createBody(): Any? = null
}

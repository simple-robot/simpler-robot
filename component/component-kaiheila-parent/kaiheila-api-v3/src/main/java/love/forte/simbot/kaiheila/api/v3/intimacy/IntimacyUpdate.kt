package love.forte.simbot.kaiheila.api.v3.intimacy

import io.ktor.http.cio.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.BaseApiDataReq
import love.forte.simbot.component.kaiheila.api.EmptyResp


/**
 * [更新用户亲密度](https://developer.kaiheila.cn/doc/http/intimacy#更新用户亲密度)
 *
 * `/api/v3/intimacy/update`
 *
 * method POST
 */
public class IntimacyUpdateReq(
    private val userId: String,
    private val score: Int? = null,
    private val socialInfo: String? = null,
    private val imgId: String? = null
) :
    EmptyRespPostIntimacyApiReq,
    BaseApiDataReq<EmptyResp>(Key) {
    companion object Key : BaseApiDataKey("intimacy", "update")

    init {
        require(score in 0..2200) { "Score must in 0 .. 2200" }
        require(socialInfo?.length?.let { it <= 500 } ?: true) { "Social info must <= 500." }
    }

    override fun createBody(): Any = Body(userId, score, socialInfo, imgId)

    @Serializable
    private data class Body(
        @SerialName("user_id")
        private val userId: String,
        private val score: Int?,
        @SerialName("social_info")
        private val socialInfo: String?,
        @SerialName("img_id")
        private val imgId: String?
    )

}

/*
user_id	string	POST	是	用户的 id
score	int	POST	否	亲密度，0-2200
social_info	string	POST	否	机器人与用户的社交信息，500 字以内
img_id	int	POST	否	id必须在用户亲密度接口返回的 img_list 中
 */
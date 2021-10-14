package love.forte.simbot.kaiheila.api.v3.intimacy

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.*


/**
 *
 * [获取用户亲密度](https://developer.kaiheila.cn/doc/http/intimacy#获取用户亲密度)
 *
 * `/api/v3/intimacy/index`
 *
 * method GET
 *
 */
public class IntimacyIndexReq(
    /**
     * 用户ID
     */
    private val userId: String,
): GetIntimacyApiReq<ObjectResp<IntimacyIndex>>,
    BaseApiDataReq<ObjectResp<IntimacyIndex>>(Key) {
        companion object Key : BaseApiDataKey("intimacy", "index") {
            private val DATA_SERIALIZER = objectResp<IntimacyIndex>()
        }

    override val dataSerializer: DeserializationStrategy<ObjectResp<IntimacyIndex>>
        get() = DATA_SERIALIZER

    override fun createBody(): Any? = null

    override fun RouteInfoBuilder.doRoute() {
        parameters {
            append("user_id", userId)
        }
    }
}


/**
 * 好感度信息
 *
 *
 */
@Serializable
public data class IntimacyIndex(
    /**
     * 机器人给用户显示的形象图片地址
     */
    @SerialName("img_url")
    val imgUrl: String,
    /**
     * 机器人显示给用户的社交信息
     */
    @SerialName("social_info")
    val socialInfo: String,
    @SerialName("last_read")
    val lastRead: Int,
    /**
     * 形象图片的总列表
     */
    @SerialName("img_list")
    val imgList: List<Img>
) : IntimacyApiRespData() {

    /**
     * 好感度形象图片实例
     */
    @Serializable
    public data class Img(
        /**
         * 形象图片的 id
         */
        val id: String,
        /**
         * 形象图片的地址
         */
        val url: String
        )

}




/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCat.kt
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

package love.forte.simbot.component.lovelycat

import love.forte.simbot.component.lovelycat.message.RobotHeadImgUrl
import love.forte.simbot.component.lovelycat.message.RobotNameResult
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.http.template.assertBody




/**
 * lovely cat api template.
 */
public interface LovelyCatApiTemplate {
    /**
     * 功能=取登录账号昵称
     * robot_wxid, 文本型
     * api=GetRobotName
     */
    fun getRobotName(robotWxid: String): RobotNameResult


    /**
     * 功能=取登录账号头像
     * robot_wxid, 文本型
     * api=GetRobotHeadimgurl
     * @return RobotHeadImgUrl
     */
    fun getRobotHeadImgUrl(robotWxid: String): RobotHeadImgUrl


}



/**
 * loveCat api模板.
 * 其对照httpapi文档提供模板方法映射。
 * @property httpTemplate HttpTemplate
 * @constructor
 */
public class LovelyCatApiTemplateImpl
constructor(
    private val httpTemplate: HttpTemplate,
    private val url: String
) : LovelyCatApiTemplate {

    @JvmOverloads
    constructor(
        httpTemplate: HttpTemplate,
        ip: String,
        host: Int,
        // 默认路径，一般情况下不可修改也不需要修改
        path: String = "/httpAPI"
    ) : this(httpTemplate, "http://$ip:$host$path")


    private inline fun <reified T> post(vararg pair: Pair<String, *>): T {
        return post(mapOf(*pair))
    }

    /**
     * @throws love.forte.simbot.http.HttpTemplateException
     * @param requestBody Any?
     * @return T
     */
    private inline fun <reified T> post(requestBody: Any?): T {
        val resp = httpTemplate.post(url = url, headers = null, requestBody = requestBody, responseType = T::class.java)
        return resp.assertBody()
    }


    /**
     * 功能=取登录账号昵称
     * robot_wxid, 文本型
     * api=GetRobotName
     */
    override fun getRobotName(robotWxid: String): RobotNameResult {
        return post(
            "api" to "GetRobotName",
            "robot_wxid" to robotWxid
        )
    }


    /**
     * 功能=取登录账号头像
     * robot_wxid, 文本型
     * api=GetRobotHeadimgurl
     * @return RobotHeadImgUrl
     */
    override fun getRobotHeadImgUrl(robotWxid: String): RobotHeadImgUrl {
        return post(
            "api" to "GetRobotHeadimgurl",
            "robot_wxid" to robotWxid
        )
    }
}







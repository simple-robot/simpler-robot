/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     dingSecret.kt
 * Date  2020/8/8 下午6:45
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.ding.sceret

import love.forte.simbot.component.ding.utils.DingSecretUtil


/**
 * 密钥计算器
 */
interface DingSecretCalculator {
    /**
     * 通过时间戳和密钥计算签名
     * @param timestamp 当前时间戳，单位是毫秒，与请求调用时间误差不能超过1小时
     * @param secret 密钥，机器人安全设置页面，加签一栏下面显示的SEC开头的字符串
     */
    fun calculate(timestamp: Long, secret: String): SecretCalculationResults
}

/**
 * 密钥的计算结果承载类
 * @param timestamp 计算时用的时间值
 * @param sign 计算后的签名结果
 */
data class SecretCalculationResults(val timestamp: Long, val sign: String)


/**
 * 默认的计算实现类。
 * 通过 [DingSecretUtil] 实现
 */
object DefaultDingSecretCalculator: DingSecretCalculator {
    override fun calculate(timestamp: Long, secret: String): SecretCalculationResults {
        val sign = DingSecretUtil.secret(timestamp, secret)
        return SecretCalculationResults(timestamp, sign)
    }

}

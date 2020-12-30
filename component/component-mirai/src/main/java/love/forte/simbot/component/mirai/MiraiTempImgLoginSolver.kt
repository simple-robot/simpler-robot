/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     MiraiTempImgLoginSolver.kt
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

package love.forte.simbot.component.mirai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.LoginSolver
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.writeBytes


/**
 * 临时文件验证码处理器。
 */
public class MiraiTempImgLoginSolver(
    input: suspend () -> String,
) : LoginSolver() {
    private val input: suspend () -> String = suspend {
        withContext(Dispatchers.IO) { input() }
    }

    @ExperimentalPathApi
    override suspend fun onSolvePicCaptcha(bot: Bot, data: ByteArray): String? {
        try {
            val log = bot.logger
            log.warning("${bot.id} 需要填写验证码。即将保存验证码于临时文件...")
            val tempFile: Path = kotlin.io.path.createTempFile(suffix = ".png")
            withContext(Dispatchers.IO) {
                // write
                tempFile.writeBytes(data)
            }
            log.warning("临时验证码图片文件路径：$tempFile")
            log.warning("请将验证码内容输入至控制台")

            return input().takeUnless { it.isEmpty()  }.also {
                log.info("正在提交验证码信息[$it]...")
            }
        } catch (throwable: Throwable) {
            throw RuntimeException("创建验证图片错误，请重试或尝试切换验证方式。", throwable)
        }
    }

    override suspend fun onSolveSliderCaptcha(bot: Bot, url: String): String {
        val logger = bot.logger
        logger.info("需要滑动验证码")
        logger.info("请在任意浏览器中打开以下链接并完成验证码. ")
        logger.info("完成后请输入任意字符 ")
        logger.info(url)
        return input().also { logger.info("正在提交中...") }
    }

    override suspend fun onSolveUnsafeDeviceLoginVerify(bot: Bot, url: String): String? {
        val logger = bot.logger
        logger.info("需要进行账户安全认证")
        logger.info("该账户有[设备锁]/[不常用登录地点]/[不常用设备登录]的问题")
        logger.info("完成以下账号认证即可成功登录|理论本认证在mirai每个账户中最多出现1次")
        logger.info("请将该链接在QQ浏览器中打开并完成认证, 成功后输入任意字符")
        logger.info("这步操作将在后续的版本中优化")
        logger.info("\t\t —— by mirai")
        logger.info(url)
        return input().also {
            logger.info("正在提交中...")
        }
    }
}
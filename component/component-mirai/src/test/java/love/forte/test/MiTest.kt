/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MiTest.kt
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

package love.forte.test

import kotlinx.coroutines.runBlocking
import love.forte.simbot.bot.BotRegisterInfo
import love.forte.simbot.component.mirai.DefaultMiraiBotConfigurationFactory
import love.forte.simbot.component.mirai.configuration.MiraiConfiguration
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.utils.RemoteFile.Companion.uploadFile
import java.io.File

fun main() {
    runBlocking {

        val conf = DefaultMiraiBotConfigurationFactory.getMiraiBotConfiguration(BotRegisterInfo("2370606773", "LiChengYang9983."), MiraiConfiguration())

        val b = BotFactory.newBot(2370606773, "LiChengYang9983.", conf)
        b.login()

        val f = "F:\\for study\\教学学习\\PIC_1495967657370.JPG"


        b.getGroupOrFail(1043409458).uploadFile("/test", File(f))


        println("upload file suc.")

        b.close()

    }
}
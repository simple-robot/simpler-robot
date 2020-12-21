/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.test.config;


import love.forte.common.ioc.annotation.ConfigBeans;
import love.forte.simbot.bot.BotRegisterInfo;
import love.forte.simbot.component.mirai.MiraiBotConfigurationFactory;
import love.forte.simbot.component.mirai.configuration.MiraiConfiguration;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.SwingSolver;
import org.jetbrains.annotations.NotNull;

/**
 * @author ForteScarlet
 */
@ConfigBeans
public class VerifyConfigFactory implements MiraiBotConfigurationFactory {

    @NotNull
    @Override
    public BotConfiguration getMiraiBotConfiguration(@NotNull BotRegisterInfo botInfo, @NotNull MiraiConfiguration simbotMiraiConfig) {
        BotConfiguration conf = simbotMiraiConfig.getBotConfiguration().invoke(botInfo.getCode());
        // 使用图形验证码处理器
        conf.setLoginSolver(SwingSolver.INSTANCE);
        return conf;
    }
}


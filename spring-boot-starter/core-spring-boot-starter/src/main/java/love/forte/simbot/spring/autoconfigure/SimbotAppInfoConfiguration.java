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

package love.forte.simbot.spring.autoconfigure;

import love.forte.simbot.SimbotArgsEnvironment;
import love.forte.simbot.SimbotEnvironment;
import love.forte.simbot.SimbotPackageScanEnvironment;
import love.forte.simbot.SimbotResourceEnvironment;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.listener.MsgGetProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Configuration
@Import({SimbotAppConfiguration.class})
public class SimbotAppInfoConfiguration {


    @Bean("simbotBotManager")
    public BotManager botManager(SimbotContext simbotContext){
        return simbotContext.getBotManager();
    }


    @Bean("simbotSimbotEnvironment")
    public SimbotEnvironment simbotEnvironment(SimbotContext simbotContext){
        return simbotContext.getEnvironment();
    }

    @Bean("simbotResourceEnvironment")
    public SimbotResourceEnvironment resourceEnvironment(SimbotContext simbotContext){
        return simbotContext.getEnvironment().getResourceEnvironment();
    }

    @Bean("simbotArgsEnvironment")
    public SimbotArgsEnvironment argsEnvironment(SimbotContext simbotContext){
        return simbotContext.getEnvironment().getArgsEnvironment();
    }

    @Bean("simbotPackageScanEnvironment")
    public SimbotPackageScanEnvironment packageScanEnvironment(SimbotContext simbotContext){
        return simbotContext.getEnvironment().getPackageScanEnvironment();
    }

    @Bean("simbotMsgGetProcessor")
    public MsgGetProcessor msgGetProcessor(SimbotContext simbotContext){
        return simbotContext.getMsgProcessor();
    }

    @Bean("simbotConfiguration")
    public love.forte.common.configuration.Configuration configuration(SimbotContext simbotContext){
        return simbotContext.getConfiguration();
    }

    @Bean("simbotMessageContentBuilderFactory")
    public MessageContentBuilderFactory messageContentBuilderFactory(SimbotContext simbotContext){
        return simbotContext.get(MessageContentBuilderFactory.class);
    }


}

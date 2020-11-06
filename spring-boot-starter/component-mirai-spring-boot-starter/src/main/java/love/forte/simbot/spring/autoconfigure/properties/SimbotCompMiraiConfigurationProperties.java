/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     SimbotCompMiraiConfigurationProperties.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.spring.autoconfigure.properties;

import love.forte.common.configuration.annotation.ConfigInject;
import love.forte.simbot.component.mirai.configuration.MiraiCacheType;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Component
@ConfigurationProperties(prefix = "simbot.component.mirai")
@lombok.Getter
@lombok.Setter
public class SimbotCompMiraiConfigurationProperties {

    private static BotConfiguration getDef() {
        return BotConfiguration.getDefault();
    }

    /**
     * mirai心跳周期. 过长会导致被服务器断开连接. 单位毫秒
     *
     * @see BotConfiguration#heartbeatPeriodMillis
     */
    @ConfigInject
    private long heartbeatPeriodMillis = getDef().getHeartbeatPeriodMillis();

    /**
     * 每次心跳时等待结果的时间.
     * 一旦心跳超时, 整个网络服务将会重启 (将消耗约 1s). 除正在进行的任务 (如图片上传) 会被中断外, 事件和插件均不受影响.
     *
     * @see BotConfiguration#heartbeatTimeoutMillis
     */
    @ConfigInject
    private long heartbeatTimeoutMillis = getDef().getHeartbeatTimeoutMillis();

    /**
     * 心跳失败后的第一次重连前的等待时间。
     */
    @ConfigInject
    private long firstReconnectDelayMillis = getDef().getFirstReconnectDelayMillis();

    /**
     * 重连失败后, 继续尝试的每次等待时间。
     */
    @ConfigInject
    private long reconnectPeriodMillis = getDef().getReconnectPeriodMillis();

    /** 最多尝试多少次重连。 */
    @ConfigInject
    private int reconnectionRetryTimes = getDef().getReconnectionRetryTimes();


    /** 使用协议类型。 */
    @ConfigInject
    private BotConfiguration.MiraiProtocol protocol = getDef().getProtocol();

    /** 关闭mirai的bot logger */
    @ConfigInject
    private boolean noBotLog = false;

    /** 关闭mirai网络日志 */
    @ConfigInject
    private boolean noNetworkLog = false;

    /** mirai bot log切换使用simbot的log */
    @ConfigInject
    private boolean useSimbotBotLog = true;

    /** mirai 网络log 切换使用simbot的log */
    @ConfigInject
    private boolean useSimbotNetworkLog = true;

    /**
     *  mirai配置自定义deviceInfoSeed的时候使用的随机种子。默认为1.
     *  实质上这个参数影响并不大。
     */
    @ConfigInject
    private long deviceInfoSeed = 1L;

    /**
     * mirai缓存策略。
     */
    @ConfigInject
    private MiraiCacheType cacheType = MiraiCacheType.FILE;

    /**
     * 如果mirai缓存为文件，且此参数不为空，则使用此参数中的文件路径作为缓存路径。
     */
    @ConfigInject
    private String cacheDirectory = "";
}

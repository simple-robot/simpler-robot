/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.spring.autoconfigure.properties;

import love.forte.common.configuration.annotation.ConfigInject;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * simbot配置文件对应实例。实际上没有被使用到，仅用作提供springboot的配置文件快捷提醒。
 *
 * 真正使用到的配置类为 {@link love.forte.simbot.component.mirai.configuration.MiraiConfiguration}
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SuppressWarnings("JavadocReference")
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
    private long heartbeatPeriodMillis = getDef().getHeartbeatPeriodMillis();

    /**
     * 每次心跳时等待结果的时间.
     * 一旦心跳超时, 整个网络服务将会重启 (将消耗约 1s). 除正在进行的任务 (如图片上传) 会被中断外, 事件和插件均不受影响.
     *
     * @see BotConfiguration#heartbeatTimeoutMillis
     */
    private long heartbeatTimeoutMillis = getDef().getHeartbeatTimeoutMillis();

    /**
     * mirai 心跳策略.
     */
    private BotConfiguration.HeartbeatStrategy heartbeatStrategy  = getDef().getHeartbeatStrategy();

    /** 最多尝试多少次重连。 */
    private int reconnectionRetryTimes = getDef().getReconnectionRetryTimes();

    /** 使用协议类型。 */
    private BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;

    /**
     * 是否将保持bot存活的线程作为 “守护线程”
     */
    private boolean daemon = false;

    /** 关闭mirai的bot logger */
    private boolean noBotLog = false;

    /** 关闭mirai网络日志 */
    private boolean noNetworkLog = false;

    /** mirai bot log切换使用simbot的log */
    private boolean useSimbotBotLog = true;

    /** mirai 网络log 切换使用simbot的log */
    private boolean useSimbotNetworkLog = true;

    /**
     *  mirai配置自定义deviceInfoSeed的时候使用的随机种子。默认为1.
     *  实质上这个参数影响并不大。
     */
    private long deviceInfoSeed = 1L;

    /**
     * 如果mirai缓存为文件，且此参数不为空，则使用此参数中的文件路径作为缓存路径。
     */
    private String cacheDirectory = "";

    /**
     * mirai设备信息文件路径。
     */
    private String deviceInfoFile = "";
}

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

package love.forte.simbot.http.configuration;

import love.forte.common.configuration.annotation.AsConfig;
import love.forte.common.configuration.annotation.ConfigInject;
import love.forte.common.ioc.annotation.ConfigBeans;

/**
 * @author ForteScarlet
 */
@ConfigBeans
@AsConfig(prefix = "simbot.http")
public class HttpProperties {

    /**
     * http请求的超时时间。默认为 5000 ms。
     */
    @ConfigInject(orDefault = "5000")
    private Long requestTimeout;
    /**
     * http连接超时时间。默认为 5000 ms。
     */
    @ConfigInject(orDefault = "5000")
    private Long connectTimeout;


    public Long getRequestTimeout() {
        return requestTimeout == null ? 5000 : requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Long getConnectTimeout() {
        return connectTimeout == null ? 5000 : connectTimeout;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}

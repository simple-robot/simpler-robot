/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationBuilder.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

import java.util.Map;
import java.util.Properties;

/**
 *
 * {@link Configuration} 的构建器。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConfigurationBuilder {

    /**
     * 追加 多项配置信息。
     *
     * @param configMap configMap
     * @return builder
     */
    ConfigurationBuilder append(Map<String, Object> configMap);


    /**
     * 追加单项配置信息。
     * @param key   key
     * @param value config value
     *
     * @return this builder
     */
    ConfigurationBuilder append(String key, Object value);



    /**
     * 构建一个 {@link Configuration} 实例。
     * @return {@link Configuration} 实例
     */
    Configuration build();

}

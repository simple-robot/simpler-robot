/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationParserManagerBuilder.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

/**
 *
 * {@link ConfigurationParserManager} 的构建器，用于构建一个 {@link ConfigurationParserManager} 实例。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConfigurationParserManagerBuilder {

    /**
     * 注册一个解析器
     * @param type   类型
     * @param parser 解析器
     * @return this builder
     */
    ConfigurationParserManagerBuilder register(String type, ConfigurationParser parser);


    /**
     * 构建一个 {@link ConfigurationParserManager} 实例。
     * @return {@link ConfigurationParserManager} 实例。
     */
    ConfigurationParserManager build();

}

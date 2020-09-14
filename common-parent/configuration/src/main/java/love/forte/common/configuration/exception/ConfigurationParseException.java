/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationParseException.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.exception;

/**
 * 配置文件解析异常
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class ConfigurationParseException extends ConfigurationException {
    public ConfigurationParseException() {
    }
    public ConfigurationParseException(String message) {
        super(message);
    }
    public ConfigurationParseException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigurationParseException(Throwable cause) {
        super(cause);
    }
    public ConfigurationParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

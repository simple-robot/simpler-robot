/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationInjectException.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.exception;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class ConfigurationInjectException extends ConfigurationException {
    public ConfigurationInjectException() {
    }
    public ConfigurationInjectException(String message) {
        super(message);
    }
    public ConfigurationInjectException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigurationInjectException(Throwable cause) {
        super(cause);
    }
    public ConfigurationInjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

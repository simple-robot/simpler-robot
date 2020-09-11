/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     LanguageInitException.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.language;

/**
 * 语言加载器初始化异常。
 * 一般会抛出此异常的时候，都是语言还没有初始化完成的时候。
 *
 * @author ForteScarlet
 */
public class LanguageInitException extends RuntimeException {
    public LanguageInitException() {
    }
    public LanguageInitException(String message) {
        super(message);
    }

    public LanguageInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public LanguageInitException(Throwable cause) {
        super(cause);
    }

    public LanguageInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

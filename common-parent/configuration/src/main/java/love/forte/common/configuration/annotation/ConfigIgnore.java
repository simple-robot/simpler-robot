/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigIgnore.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.annotation;

import java.lang.annotation.*;

/**
 *
 * 当 [AsConfig] 的 [AsConfig.allField] 设置为true，但是有些字段想要被排除的时候，
 * 则可以通过在字段上标注此注解来忽略一个字段。
 *
 * @author [ ForteScarlet ](https://github.com/ForteScarlet)
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.FIELD}) //接口、类、枚举、注解、方法
@Documented
public @interface ConfigIgnore {
}

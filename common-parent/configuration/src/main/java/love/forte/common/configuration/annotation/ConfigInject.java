/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigInject.java
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
 * 标注一个字段或方法为通过 {@link love.forte.common.configuration.Configuration} 进行注入。
 *
 * 如果使用在了 方法 上，则方法参数有且仅只能有一个，且此方法必须是 {@code public} 的。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.FIELD, ElementType.METHOD}) //接口、类、枚举、注解、方法
@Documented
public @interface ConfigInject {

    /**
     * 当前配置名称的键。例如：'user.name'。如果不填则默认通过字段名或者方法名判断名称。
     */
    String value() default "";

    /**
     * 如果有，忽略类上的前缀。
     */
    boolean ignorePrefix() default false;

    /**
     * 如果有，忽略类上的后缀。
     */
    boolean ignoreSuffix() default false;

    /**
     * 优先通过 setter 注入配置值。
     *
     * 只有当标注在字段上时才会生效。
     *
     * 如果为true，则会尝试获取类中 {@code set + fieldName大写(field类型)} 的方法。
     * 如果找不到则会继续使用字段注入。
     *
     */
    boolean bySetter() default true;


    /**
     * 如果找不到对应的配置，则注入null。默认会抛出异常。
     */
    boolean orNull() default false;

}

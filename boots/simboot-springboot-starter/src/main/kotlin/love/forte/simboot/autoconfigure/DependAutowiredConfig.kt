/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.autoconfigure

import love.forte.di.annotation.Depend
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor


private fun getMyAutowiredAnnotationBeanPostProcessor(): AutowiredAnnotationBeanPostProcessor =
    AutowiredAnnotationBeanPostProcessor().also {
        it.setAutowiredAnnotationType(Depend::class.java)
    }

/**
 * 使 [Depend] 支持 autowired.
 * @author ForteScarlet
 */
public open class AutowiredConfig :
    SmartInstantiationAwareBeanPostProcessor
    by getMyAutowiredAnnotationBeanPostProcessor()
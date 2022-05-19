package love.forte.simboot.spring.autoconfigure

import org.springframework.context.annotation.Import

/**
 * The default configures.
 *
 * @author ForteScarlet
 */
@Import(
    DefaultBinderFactoryConfigure::class,
    SimbotSpringBootListenerAutoRegisterBuildConfigure::class,
    SimbotSpringBootBotAutoRegisterBuildConfigure::class,
    SimbotSpringBootComponentAutoInstallBuildConfigure::class,
    SimbotSpringBootEventProviderAutoInstallBuildConfigure::class,
    SimbotSpringBootInterceptorsAutoConfigure::class,
    SimbotSpringBootListenerAutoRegisterBuildConfigure::class,
)
public open class SimbotSpringBootDefaultConfigures
package love.forte.simboot.spring.autoconfigure

import org.springframework.context.annotation.Import

/**
 * The default configures.
 * @author ForteScarlet
 */
@Import(
    SimbotSpringBootListenerAutoRegisterBuildConfigure::class,
    SimbotSpringBootBotAutoRegisterBuildConfigure::class,
    SimbotSpringBootComponentAutoInstallBuildConfigure::class,
    SimbotSpringBootEventProviderAutoInstallBuildConfigure::class,
)
public open class SimbotSpringBootDefaultConfigures
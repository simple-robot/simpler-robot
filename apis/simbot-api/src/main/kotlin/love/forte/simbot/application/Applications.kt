package love.forte.simbot.application


// TODO
public fun
        <
                CBuilder : ComponentsBuilder,
                MConfig : Any,
                BMBuilder : BotManagersBuilder,
                Env : Application.Environment,
                AppBuilder : ApplicationEnvironmentBuilder<CBuilder, MConfig, BMBuilder, Env>,
                AppConfig : ApplicationConfiguration,
                >
        simbotApplication(
    factory: ApplicationEnvironmentFactory<CBuilder, MConfig, BMBuilder, Env, AppBuilder, AppConfig>,
    config: AppConfig.() -> Unit = {},
    builder: AppBuilder.() -> Unit = {},
) {
    val created = factory.create(config, builder)

    TODO()

}

// ApplicationEnvironmentBuilder<CBuilder, MConfig, BMBuilder, Env>
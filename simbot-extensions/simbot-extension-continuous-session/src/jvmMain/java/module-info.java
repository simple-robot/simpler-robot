module simbot.extension.continuous.session {
    requires kotlin.stdlib;
    requires simbot.api;
    requires kotlinx.coroutines.core;
    requires static simbot.common.annotations;
    requires static kotlinx.coroutines.reactor;
    requires static reactor.core;

    exports love.forte.simbot.extension.continuous.session;
}

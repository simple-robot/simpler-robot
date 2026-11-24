module simbot.extension.continuous.session {
    requires kotlin.stdlib;
    requires simbot.api;
    requires kotlinx.coroutines.core;
    requires static simbot.common.annotations;
    requires static kotlinx.coroutines.reactive;
    requires static kotlinx.coroutines.reactor;
    requires static reactor.core;
    requires static org.reactivestreams;

    exports love.forte.simbot.extension.continuous.session;
}

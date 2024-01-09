module simbot.common.suspendrunner {
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core;
    requires static simbot.common.annotations;
    requires simbot.logger;
    // reactive transformers
    requires static kotlinx.coroutines.reactor;
    requires static kotlinx.coroutines.rx2;
    requires static kotlinx.coroutines.rx3;
    requires static reactor.core;
    requires static io.reactivex.rxjava2;
    requires static io.reactivex.rxjava3;

    exports love.forte.simbot.suspendrunner;
    exports love.forte.simbot.suspendrunner.reserve;
}

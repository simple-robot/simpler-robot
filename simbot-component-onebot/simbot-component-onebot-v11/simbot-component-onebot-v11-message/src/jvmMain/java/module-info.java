module simbot.component.onebot11v.message {
    requires kotlin.stdlib;
    requires simbot.api;
    requires simbot.component.onebot.common;
    requires static simbot.common.annotations;
    requires transitive kotlinx.serialization.core;
    requires simbot.common.suspendrunner;
    requires kotlinx.coroutines.core;

    exports love.forte.simbot.component.onebot.v11.message;
    exports love.forte.simbot.component.onebot.v11.message.segment;
}

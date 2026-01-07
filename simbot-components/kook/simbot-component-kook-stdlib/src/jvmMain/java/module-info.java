module simbot.component.kook.stdlib {
    requires kotlin.stdlib;
    requires static simbot.common.annotations;
    requires transitive simbot.component.kook.api;
    requires simbot.common.stageloop;
    requires simbot.common.atomic;
    requires io.ktor.websockets;
    requires static org.jetbrains.annotations;
    requires transitive kotlinx.coroutines.core;

    exports love.forte.simbot.kook.stdlib;
}

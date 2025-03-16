package com.cooper

import com.cooper.game.GameState
import com.cooper.game.LobbyGameState
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = Duration.INFINITE
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = JacksonWebsocketContentConverter()
    }

    routing {
        ws()
    }
}

val messageResponseFlow = MutableSharedFlow<OutboundMessage>()
val sharedFlow = messageResponseFlow.asSharedFlow()

private fun Routing.ws() {
    webSocket("/ws") {
        val name = call.request.queryParameters["name"]?.trim() ?: throw IllegalArgumentException("No name provided")
        if (name.length > 15 || name.length < 3) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Name length should be in 3..15"))
            return@webSocket
        }

        val job = launch {
            sharedFlow.collect { message ->
                send(
                    converter!!.serialize(
                        charset = Charsets.UTF_8,
                        typeInfo = TypeInfo(OutboundMessage::class),
                        value = message,
                    )
                )
            }
        }

        runCatching {
            incoming.consumeEach { frame ->
                if (frame !is Frame.Text) return@consumeEach

                val message = converter!!.deserialize(
                    charset = Charsets.UTF_8,
                    typeInfo = TypeInfo(InboundMessage::class),
                    content = frame,
                )

                if (message == null || message !is InboundMessage) {
                    return@consumeEach
                }

                //                    messageResponseFlow.emit(messageResponse)
            }
        }.onFailure { exception ->
            println("WebSocket exception: ${exception.localizedMessage}")
        }.also {
            job.cancel()
        }
    }
}

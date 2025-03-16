package com.cooper

import com.cooper.game.*
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import io.ktor.serialization.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = Duration.INFINITE
        maxFrameSize = 1024 * 1024
        masking = false
        contentConverter = JacksonWebsocketContentConverter()
    }

    routing {
        ws()
    }
}

class OutgoingSerializerHelper(
    private val channel: SendChannel<Frame>,
    private val converter: WebsocketContentConverter,
    private val typeInfo: TypeInfo,
) {
    suspend fun send(message: OutboundMessage) {
        channel.send(
            converter.serialize(
                charset = Charsets.UTF_8,
                typeInfo = typeInfo,
                value = message,
            )
        )
    }
}

private fun Routing.ws() {
    webSocket("/ws") {
        val name = call.request.queryParameters["name"]?.trim() ?: throw IllegalArgumentException("No name provided")
        if (name.length > 15 || name.length < 3) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Name length should be in 3..15"))
            return@webSocket
        }

        val channel = OutgoingSerializerHelper(
            channel = outgoing,
            converter = converter!!,
            typeInfo = TypeInfo(OutboundMessage::class),
        )

        val oneShot = Channel<Result<SessionId>>(1)
        globalInnerApplicationChannel.send(
            PlayerJoinInnerApplicationMessage(
                playerName = name,
                channel = channel,
                callback = oneShot,
            )
        )

        val oneShotResponse = oneShot.receive()
        if (oneShotResponse.isFailure) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, oneShotResponse.exceptionOrNull()!!.toString()))
            return@webSocket
        }

        val sessionId = oneShotResponse.getOrThrow()

        runCatching {
            incoming.consumeEach { frame ->
                if (frame !is Frame.Text) return@consumeEach

                val message = converter!!.deserialize(
                    charset = Charsets.UTF_8,
                    typeInfo = TypeInfo(InboundMessage::class),
                    content = frame,
                ) as? InboundMessage ?: return@consumeEach

                globalInnerApplicationChannel.send(PlayerInboundApplicationMessage(sessionId, message))
            }
        }.onFailure { exception ->
            println("WebSocket exception: $exception")
        }.also {
            globalInnerApplicationChannel.send(PlayerDisconnectInnerApplicationMessage(name, sessionId))
        }
    }
}

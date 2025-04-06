package com.cooper

import com.cooper.game.*
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.ServerOutboundMessage
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.serialization.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
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

        val objectMapper = jacksonObjectMapper()
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        objectMapper.registerModule(
            SimpleModule()
                .addSerializer(PlayerIcon::class.java, PlayerIconSerializer())
        )

        contentConverter = JacksonWebsocketContentConverter(objectmapper = objectMapper)

    }

    routing {
        ws()
        serverWs()
    }
}

class SocketContentConverterSender<M>(
    private val channel: SendChannel<Frame>,
    private val converter: WebsocketContentConverter,
    private val typeInfo: TypeInfo,
) {
    suspend fun send(message: M) {
        channel.send(
            converter.serialize(
                charset = Charsets.UTF_8,
                typeInfo = typeInfo,
                value = message,
            )
        )
    }

    fun close(cause: Throwable? = null): Boolean = channel.close(cause)
}

val alphaNumericRegex by lazy { Regex("^[a-zA-Z0-9_]*$") }

private fun Routing.ws() {
    webSocket("/ws") {
        val name = call.request.queryParameters["name"]?.trim() ?: throw IllegalArgumentException("No name provided")
        println("Player $name init connection")
        if (name.length > 15 || name.length < 3) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Name length should be in 3..15"))
            return@webSocket
        }

        if (!name.matches(alphaNumericRegex)) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Name had bad characters in it"))
            return@webSocket
        }

        println("Player $name connected")

        val channel = SocketContentConverterSender<OutboundMessage>(
            channel = outgoing,
            converter = converter!!,
            typeInfo = TypeInfo(OutboundMessage::class),
        )

        val completable = CompletableDeferred<Result<SessionId>>()
        globalInnerApplicationChannel.send(
            InnerApplicationMessage.PlayerJoin(
                playerName = name,
                channel = channel,
                completable = completable,
            )
        )

        println("Player $name waiting for one shot...")
        val oneShotResponse = completable.await()
        if (oneShotResponse.isFailure) {
            println("Player $name failed to join: ${oneShotResponse.exceptionOrNull()}")
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, oneShotResponse.exceptionOrNull()!!.toString()))
            return@webSocket
        }

        val sessionId = oneShotResponse.getOrThrow()
        println("Player $name joined with session ID $sessionId")

        runCatching {
            incoming.consumeEach { frame ->
                if (frame !is Frame.Text) return@consumeEach

                val message = converter!!.deserialize(
                    charset = Charsets.UTF_8,
                    typeInfo = TypeInfo(InboundMessage::class),
                    content = frame,
                ) as? InboundMessage ?: return@consumeEach

                //@formatter:off
                globalInnerApplicationChannel.send(InnerApplicationMessage.PlayerInboundMessageInner(sessionId, message))
                //@formatter:on
            }
        }.onFailure { exception ->
            println("WebSocket exception: ${exception.message}")
        }.also {
            globalInnerApplicationChannel.send(InnerApplicationMessage.PlayerDisconnect(name, sessionId))
        }
    }
}

private fun Routing.serverWs() {
    webSocket("/server-ws") {
        val channel = SocketContentConverterSender<ServerOutboundMessage>(
            channel = outgoing,
            converter = converter!!,
            typeInfo = TypeInfo(ServerOutboundMessage::class),
        )

        globalInnerApplicationChannel.send(InnerApplicationMessage.NewServerConnection(channel))

        println("AFTER")
        runCatching {
            incoming.consumeEach { frame ->
                if (frame !is Frame.Text) return@consumeEach

                val message = converter!!.deserialize(
                    charset = Charsets.UTF_8,
                    typeInfo = TypeInfo(ServerInboundMessage::class),
                    content = frame,
                ) as? ServerInboundMessage ?: return@consumeEach

                globalInnerApplicationChannel.send(InnerApplicationMessage.ServerInboundMessageInner(message))
            }
        }.onFailure { exception ->
            println("WebSocket exception: ${exception.message}")
        }
    }
}
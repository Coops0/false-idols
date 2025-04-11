package com.cooper

import com.cooper.game.InnerApplicationMessage
import com.cooper.game.PlayerIcon
import com.cooper.game.PlayerIconSerializer
import com.cooper.game.SessionId
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.ServerOutboundMessage
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets(innerApplicationFlow: MutableSharedFlow<InnerApplicationMessage>) {
    install(WebSockets) {
        timeout = 15.seconds
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
        ws(innerApplicationFlow)
        serverWs(innerApplicationFlow)
    }
}

val alphaNumericRegex by lazy { Regex("^[a-zA-Z0-9_]*$") }

private fun Routing.ws(innerApplicationFlow: MutableSharedFlow<InnerApplicationMessage>) {
    webSocket("/ws") {
        val name = call.request.queryParameters["name"]?.trim() ?: throw IllegalArgumentException("No name provided")
        if (name.length > 15 || name.length < 3) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Name length should be in 3..15"))
            return@webSocket
        }

        if (!name.matches(alphaNumericRegex)) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Name had bad characters in it"))
            return@webSocket
        }

        println("Player $name connected")

        val messageResponseFlow = MutableSharedFlow<OutboundMessage>()
        val sharedFlow = messageResponseFlow.asSharedFlow()

        val job = launch {
            sharedFlow.collect { message ->
                if (message is OutboundMessage.Disconnect) {
                    close(CloseReason(CloseReason.Codes.NORMAL, "Player disconnected"))
                    return@collect
                }

                send(converter!!.serialize(charset = Charsets.UTF_8, typeInfo = TypeInfo(OutboundMessage::class), value = message))
            }
        }

        val completable = CompletableDeferred<Result<SessionId>>()

        println("Player $name sending off join request...")
        innerApplicationFlow.emit(
            InnerApplicationMessage.PlayerJoin(
                playerName = name,
                flow = messageResponseFlow,
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

                innerApplicationFlow.emit(InnerApplicationMessage.PlayerInboundMessageInner(sessionId, message))
            }
        }.onFailure { exception ->
            println("WebSocket exception: ${exception.message}")
        }.also {
            job.cancel()
            try {
                innerApplicationFlow.emit(InnerApplicationMessage.PlayerDisconnect(name, sessionId))
            } catch (_: Throwable) {
                /* ignored */
            }

            println("player $name disconnect $sessionId")
        }
    }
}

private fun Routing.serverWs(innerApplicationFlow: MutableSharedFlow<InnerApplicationMessage>) {
    webSocket("/server-ws") {
        val messageResponseFlow = MutableSharedFlow<ServerOutboundMessage>()
        val sharedFlow = messageResponseFlow.asSharedFlow()

        val job = launch {
            sharedFlow.collect { message ->
                send(converter!!.serialize(charset = Charsets.UTF_8, typeInfo = TypeInfo(ServerOutboundMessage::class), value = message))
            }
        }

        innerApplicationFlow.emit(InnerApplicationMessage.NewServerConnection(messageResponseFlow))

        runCatching {
            incoming.consumeEach { frame ->
                if (frame !is Frame.Text) return@consumeEach

                val message = converter!!.deserialize(
                    charset = Charsets.UTF_8,
                    typeInfo = TypeInfo(ServerInboundMessage::class),
                    content = frame,
                ) as? ServerInboundMessage ?: return@consumeEach

                innerApplicationFlow.emit(InnerApplicationMessage.ServerInboundMessageInner(message))
            }
        }.onFailure { exception ->
            println("WebSocket exception: ${exception.message}")
        }.also {
            job.cancel()

            try {
                innerApplicationFlow.emit(InnerApplicationMessage.NewServerConnection(null))
            } catch (_: Throwable) {
                /* ignored */
            }

            println("server disconnect")
        }
    }
}
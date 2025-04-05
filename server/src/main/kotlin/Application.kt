package com.cooper

import com.cooper.game.InnerApplicationMessage
import com.cooper.game.globalInnerApplicationChannel
import com.cooper.game.launchGameActor
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedSendChannelException

@DelicateCoroutinesApi fun main() {
    val server = embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)

    GlobalScope.launch { launchGameActor() }

    server.addShutdownHook {
        println("Shutting down...")
        runBlocking {
            val completable = CompletableDeferred<Unit>()

            try {
                globalInnerApplicationChannel.send(InnerApplicationMessage.Shutdown(completable))
            } catch (_: ClosedSendChannelException) {
                println("Channel already closed")
                return@runBlocking
            }

            println("Waiting for shutdown to complete")
            completable.await()
        }

        println("Shut down successful")
    }


    server.start(wait = true)
}

val SecurityCheckPlugin = createApplicationPlugin(name = "SecurityCheckPlugin") {
    onCall { call ->
        if (call.request.uri != "/ok") {
            val authCookie = call.request.cookies["auth"]
            if (authCookie == null || authCookie != "mhm") {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

val DisableCorsPlugin = createApplicationPlugin(name = "DisableCorsPlugin") {
    onCall { call ->
        call.response.header(HttpHeaders.AccessControlAllowCredentials, "true")
        call.response.header(HttpHeaders.AccessControlAllowOrigin, "*")
        call.response.header(HttpHeaders.AccessControlAllowMethods, "GET, POST, PUT, DELETE, OPTIONS")
        call.response.header(HttpHeaders.AccessControlAllowHeaders, "Content-Type")

        if (call.request.httpMethod == HttpMethod.Options) {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Application.module() {
    if (developmentMode) {
        log.info("Disabled CORS")
        install(DisableCorsPlugin)
    }

    install(SecurityCheckPlugin)

    install(Compression) {
        gzip()
    }

    install(ContentNegotiation) {
        jackson {
            setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        }
    }

    configureSockets()
    configureRouting()
}

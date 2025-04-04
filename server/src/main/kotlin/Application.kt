package com.cooper

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

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
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

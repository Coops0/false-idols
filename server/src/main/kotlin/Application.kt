package com.cooper

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Compression)
    install(CORS) {
        anyHost() // TODO Remove this
    }

    configureSockets()

    install(ContentNegotiation) {
        jackson {
            setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        }
    }

    configureRouting()
}

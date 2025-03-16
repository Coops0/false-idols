package com.cooper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*

fun Application.configureRouting() {
    install(Webjars) {
        path = "/webjars"
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error(cause.message)
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        staticResources("/static", "static")

        get("/webjars") {

        }
    }
}

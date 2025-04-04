package com.cooper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val clientFile by lazy {
    Application::class.java.classLoader.getResourceAsStream("static/client.html")!!.use { resource ->
        resource.readBytes()
    }
}

val hostFile by lazy {
    Application::class.java.classLoader.getResourceAsStream("static/host-client.html")!!.use { resource ->
        resource.readBytes()
    }
}

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error(cause.message)
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        staticResources("/assets/", "static/assets", index = null) {
            preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP)
        }

        get("/ok") {
            call.response.cookies.append(
                Cookie(
                    name = "auth",
                    value = "mhm",
                    maxAge = 2592000
                )
            )

            call.respondRedirect("/", permanent = false)
        }

        get("/") {
            call.respondBytes(clientFile, ContentType.Text.Html)
        }

        get("/host") {
            call.respondBytes(hostFile, ContentType.Text.Html)
        }
    }
}

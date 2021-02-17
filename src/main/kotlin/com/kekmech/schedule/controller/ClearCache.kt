package com.kekmech.schedule.controller

import com.kekmech.schedule.atStartOfWeek
import com.kekmech.schedule.moscowLocalDate
import com.kekmech.schedule.scheduleRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

@Location("/system/clearCache/{selector}/{secretKey}")
class ClearCache(val selector: String, val secretKey: String)

fun Route.clearCache() {
    delete<ClearCache> { request ->
        if (request.secretKey != System.getenv("CACHE_CLEAR_SECRET")) {
            call.respond(HttpStatusCode.Unauthorized, "Fuck you asshole bitch fucking fuck :)")
        } else {
            val selector = String(Base64.getDecoder().decode(request.selector))
            val (groupOrPersonNumber, offset) = selector.split("_").let { it[0] to it[1].toInt() }
            val requestedWeekStart = offset.let {
                if (it == 0) {
                    moscowLocalDate().atStartOfWeek()
                } else {
                    moscowLocalDate().plusWeeks(it.toLong()).atStartOfWeek()
                }
            }
            scheduleRepository.clearCache(groupOrPersonNumber, requestedWeekStart)
            call.respond(HttpStatusCode.OK, "Cache cleared successfully")
        }
    }
}
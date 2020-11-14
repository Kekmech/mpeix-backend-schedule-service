package com.kekmech.schedule

object Endpoint {
    const val getGroupId = "/getGroupId"
    const val getGroupSchedule = "/getGroupSchedule"
    const val health = "/health"

    object Mpei {
        object Ruz {
            const val search = "http://ts.mpei.ru/api/search"
            const val schedule = "http://ts.mpei.ru/api/schedule/group" // + /$groupId
        }
    }
}

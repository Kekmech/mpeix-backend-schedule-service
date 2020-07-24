package com.kekmech

object Endpoint {
    const val getGroupId = "/getGroupId"
    const val getSchedule = "/getSchedule"

    object Mpei {
        object Timetable {
            const val mainPage = "https://mpei.ru/Education/timetable/Pages/default.aspx"
            const val scheduleViewPage = "https://mpei.ru/Education/timetable/Pages/table.aspx"
        }
    }
}
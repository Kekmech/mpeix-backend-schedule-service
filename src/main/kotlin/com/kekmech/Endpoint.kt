package com.kekmech

object Endpoint {
    const val getGroupId = "/getGroupId"
    const val getSchedule = "/getSchedule"

    object Mpei {
        object Timetable {
            const val mainPage = "https://mpei.ru/Education/timetable/Pages/default.aspx"
            const val scheduleViewPage = "https://mpei.ru/Education/timetable/Pages/table.aspx"
        }
        object Ruz {
            // GET
            // queries:
            // - term=STRING_ENCODED  --  group name
            // - type=group|person
            // return: list of results
            const val search = "http://ts.mpei.ru/api/search"
            // GET
            // queries:
            // - start  --  date yyyy.mm.dd
            // - finish  --  date yyyy.mm.dd
            const val schedule = "http://ts.mpei.ru/api/schedule/group"

        }
    }
}
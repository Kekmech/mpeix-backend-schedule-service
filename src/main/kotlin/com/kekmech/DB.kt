package com.kekmech

object DB {
    const val NAME = "mpeix"
    object Tables {
        object GroupsInfo {
            const val NAME = "groups_info"

            object Columns {
                const val MPEI_SCHEDULE_ID = "mpei_schedule_id"
                const val GROUP_NUMBER = "group_number"
                const val GROUP_ID = "group_id"
            }
        }

        object GroupSchedule {
            const val NAME = "group_schedule"
        }
    }
}
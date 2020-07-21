package com.kekmech

import org.intellij.lang.annotations.*

object SqlRequests {

    @Language("SQL")
    fun getGroupIdByGroupNumber(groupNumber: String) =
        "select group_id from groups_info where group_number='$groupNumber' limit 1"
}
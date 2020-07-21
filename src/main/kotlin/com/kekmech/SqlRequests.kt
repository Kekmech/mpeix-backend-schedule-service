package com.kekmech

import org.intellij.lang.annotations.*

object SqlRequests {

    @Language("SQL")
    fun getGroupIdByGroupNumber(groupNumber: String) =
        "select * from groups_info where number='$groupNumber' limit 1"
}
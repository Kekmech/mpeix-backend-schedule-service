package com.kekmech.helpers

import com.kekmech.*
import org.intellij.lang.annotations.*
import org.jooq.*


@Language("SQL")
fun DSLContext.getGroupIdByGroupNumber(groupNumber: String) = fetch(
    "select group_id from groups_info where group_number='$groupNumber' limit 1"
)
    .firstOrNull()
    ?.getValue(DB.Tables.GroupsInfo.Columns.GROUP_ID)
    ?.toString()

@Language("SQL")
fun DSLContext.getMpeiScheduleIdByGroupNumber(groupNumber: String) = fetch(
    "select mpei_schedule_id from groups_info where group_number='$groupNumber' limit 1"
)
    .firstOrNull()
    ?.getValue(DB.Tables.GroupsInfo.Columns.MPEI_SCHEDULE_ID)
    ?.toString()

@Language("SQL")
fun DSLContext.insertNewGroupInfo(groupNumber: String, mpeiScheduleId: String) = fetch(
    "insert into groups_info (group_number, mpei_schedule_id) values ('$groupNumber', $mpeiScheduleId);"
)

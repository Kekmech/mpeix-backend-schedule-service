package com.kekmech.helpers

import org.intellij.lang.annotations.*
import org.jooq.*


@Language("SQL")
fun DSLContext.getGroupIdByGroupNumber(groupNumber: String) = fetch(
    "select group_id from groups_info where group_number='$groupNumber' limit 1"
)
    .firstOrNull()
    ?.getValue("group_id")
    ?.toString()

@Language("SQL")
fun DSLContext.getMpeiScheduleIdByGroupNumber(groupNumber: String) = fetch(
    "select mpei_schedule_id from groups_info where group_number='$groupNumber' limit 1"
)
    .firstOrNull()
    ?.getValue("mpei_schedule_id")
    ?.toString()

@Language("SQL")
fun DSLContext.insertNewGroupInfo(groupNumber: String, mpeiScheduleId: String) = fetch(
    "insert into groups_info (group_number, mpei_schedule_id) values ('$groupNumber', $mpeiScheduleId);"
)

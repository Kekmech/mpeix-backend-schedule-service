package com.kekmech.schedule

fun String?.checkIsValidGroupNumber(): String {
    if (!(this != null && matches("[а-яА-Я0-9-]{5,20}".toRegex())))
        throw ValidationException("Invalid group number")
    return this.toUpperCase().let {
        if (it.matches(".*-\\d[^0-9]*-.*".toRegex())) it.replaceFirst("-", "-0") else it
    }
}

fun String?.checkIsValidPersonName(): String {
    if (this.isNullOrBlank()) throw ValidationException("Invalid person name")
    if (!this.matches("([а-яА-Я]+\\s?){1,3}".toRegex())) throw ValidationException("Invalid person name")
    return this
        .split("\\s+".toRegex())
        .joinToString(separator = " ") { it.toLowerCase().capitalize() }
}
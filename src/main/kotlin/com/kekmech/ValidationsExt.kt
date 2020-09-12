package com.kekmech

fun String?.checkIsValidGroupNumber(): String {
    if (!(this != null && matches("[а-яА-Я0-9-]{5,20}".toRegex())))
        throw ValidationException("Invalid group number")
    return this.toUpperCase().let {
        if (it.matches(".*-\\d-.*".toRegex())) it.replaceFirst("-", "-0") else it
    }
}
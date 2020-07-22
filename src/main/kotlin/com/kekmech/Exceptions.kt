package com.kekmech

class InvalidArgumentException(message: String = "INVALID_ARGUMENT") : Throwable(message)

class MpeiBackendUnexpectedBehaviorException(message: String = "SOMETHING_WRONG_WITH_MPEI_BACKEND") : Throwable(message)
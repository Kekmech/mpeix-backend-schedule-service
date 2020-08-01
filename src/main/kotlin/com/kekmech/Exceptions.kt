package com.kekmech

import com.kekmech.dto.*

class InvalidArgumentException(message: String = "INVALID_ARGUMENT") : Throwable(message)

class MpeiBackendUnexpectedBehaviorException(message: String = "SOMETHING_WRONG_WITH_MPEI_BACKEND") : Throwable(message)

class ScheduleExpiredByRequestCount(val schedule: Schedule) : Throwable("It's time to update schedule from remote")
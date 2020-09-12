package com.kekmech

sealed class BaseException(message: String) : RuntimeException(message)
class LogicException(message: String) : BaseException("LogicException; message: $message;")
class ExternalException(message: String) : BaseException("ExternalException; message: $message;")
class ValidationException(message: String) : BaseException("ValidationException; message: $message;")
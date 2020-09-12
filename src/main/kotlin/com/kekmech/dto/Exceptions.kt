package com.kekmech.dto

sealed class BaseException(message: String) : RuntimeException(message)
class BusinessException(message: String) : BaseException("BusinessException; message: $message;")
class ExternalException(message: String) : BaseException("ExternalException; message: $message;")
class ValidationException(message: String) : BaseException("ValidationException; message: $message;")
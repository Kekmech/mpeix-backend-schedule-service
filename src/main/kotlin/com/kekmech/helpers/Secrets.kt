package com.kekmech.helpers

import java.io.*

class SecretProvider(private val secretName: () -> String) : Lazy<String> {
    private var _value: String? = null

    override val value: String
        get() {
            if (_value == null) {
                _value = File("./${secretName()}_SECRET").readText()
            }
            @Suppress("UNCHECKED_CAST")
            return _value as String
        }

    override fun isInitialized(): Boolean = _value != null

    override fun toString(): String = if (isInitialized()) value else "Secret value not initialized."
}

fun provideSecret(secretName: String) = SecretProvider { secretName }
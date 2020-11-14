package com.kekmech.schedule

import org.junit.jupiter.api.Test

class ValidationsExtTest {

    @Test
    fun validateGroupNumberTest() {
        assert("С-6-18".checkIsValidGroupNumber() == "С-06-18")
        assert("Э-1м-19".checkIsValidGroupNumber() == "Э-01М-19")
    }
}

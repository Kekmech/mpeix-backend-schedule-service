import com.kekmech.*
import org.junit.jupiter.api.*

class ValidationsExtTest {

    @Test
    fun validateGroupNumberTest() {
        assert("С-6-18".checkIsValidGroupNumber() == "С-06-18")
        assert("Э-1м-19".checkIsValidGroupNumber() == "Э-01м-19")
    }
}
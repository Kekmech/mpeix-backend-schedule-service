import com.kekmech.*
import org.junit.jupiter.api.*
import java.time.*
import java.util.*

class TimeExtTest {

    @Test
    fun atStartOfWeekTest() {
        val datesSet = listOf(
            LocalDate.of(2020, Month.SEPTEMBER, 1),
            LocalDate.of(2020, Month.AUGUST, 1)
        ).map { it.atStartOfWeek() }
        val startOfWeekSet = listOf(
            LocalDate.of(2020, Month.AUGUST, 31),
            LocalDate.of(2020, Month.JULY, 27)
        )
        assertEquals(datesSet.map { it.atStartOfWeek() }, startOfWeekSet)
    }

    @Test
    fun atSaturdayOfWeek() {
        val datesSet = listOf(
            LocalDate.of(2020, Month.AUGUST, 31),
            LocalDate.of(2020, Month.DECEMBER, 30),
            LocalDate.of(2020, Month.NOVEMBER, 30)
        ).map { it.atSaturdayOfWeek() }
        val saturdayOfWeekSet = listOf(
            LocalDate.of(2020, Month.SEPTEMBER, 5),
            LocalDate.of(2021, Month.JANUARY, 2),
            LocalDate.of(2020, Month.DECEMBER, 5)
        )
        assertEquals(datesSet, saturdayOfWeekSet)
    }

    @Test
    fun weekOfSemesterTest() {
        val mockLocale = Locale.GERMAN // any locale where monday is first day of week
        val weekSet = listOf(
            LocalDate.of(2020, Month.SEPTEMBER, 3),
            LocalDate.of(2020, Month.SEPTEMBER, 9),
            LocalDate.of(2019, Month.SEPTEMBER, 1),
            LocalDate.of(2019, Month.SEPTEMBER, 2),
            LocalDate.of(2020, Month.MAY, 20)
        ).map { it.weekOfSemester(mockLocale) }
        val semesterNumberSet = listOf(1, 2, -1, 1, 16)

        assertEquals(weekSet, semesterNumberSet)
    }

    private fun<T : Any> assertEquals(real: List<T>, expected: List<T>) {
        println("expected:  $expected")
        println("real data: $real")
        assert(real.size == expected.size)
        real.forEachIndexed { index, localDate -> assert(localDate == expected[index]) }
    }

    @Test
    fun testSerialize() {
        assert(
            "2020.09.11".formatFromMpei() == LocalDate.of(2020, 9, 11)
        )
    }

    @Test
    fun testDeserialize() {
        assert(
            LocalDate.of(2020, 9, 11).formatToMpei() == "2020.09.11"
        )
    }
}
import com.kekmech.*
import org.junit.jupiter.api.*
import java.time.*

class TimeExtTests {

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


    private fun assertEquals(listOfDate1: List<LocalDate>, listOfDate2: List<LocalDate>) {
        println("list 1: $listOfDate1")
        println("list 2: $listOfDate2")
        assert(listOfDate1.size == listOfDate2.size)
        listOfDate1.forEachIndexed { index, localDate -> assert(localDate == listOfDate2[index]) }
    }
}
import com.google.gson.*
import com.kekmech.di.*
import com.kekmech.gson.*
import com.kekmech.helpers.*
import com.kekmech.parser.*
import org.junit.jupiter.api.*
import org.koin.core.context.*
import java.io.*
import java.text.*
import java.time.*

class ScheduleParserTest {

    @Test
    @DisplayName("Parse schedule_sample.html")
    fun parseSample() {
        startKoin {
            modules(AppModule())
        }
        val gson = GsonBuilder().apply {
            setDateFormat(DateFormat.LONG)
            registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
        }.create()

        val html = File("./src/test/resources/shedule_sample.html").readText()
        val schedule = ScheduleParser(LocalDate.parse("2020-05-20")).parseWeek(html)
        assert(SCHEDULE_SAMPLE == gson.toJson(schedule))
    }

    companion object {
        val SCHEDULE_SAMPLE = "{\"weekOfYear\":21,\"weekOfSemester\":16,\"firstDayOfWeek\":\"2020-05-18\",\"days\":[{\"dayOfWeek\":3,\"date\":\"2020-05-20\",\"classes\":[{\"name\":\"Основы мехатроники и робототехники\",\"type\":\"LECTURE\",\"place\":\"С-215 (Корпус С)\",\"groups\":\"С-12-16\",\"person\":\"доц. Адамов Б.И.\",\"time\":{\"start\":\"09:20:00\",\"end\":\"10:55:00\"},\"number\":1},{\"name\":\"Основы мехатроники и робототехники\",\"type\":\"LAB\",\"place\":\"С-213 (Корпус С)\",\"groups\":\"С-12-16\",\"person\":\"ст.преп. Гавриленко А.Б.\",\"time\":{\"start\":\"11:10:00\",\"end\":\"12:45:00\"},\"number\":2},{\"name\":\"Динамика мехатронных систем\",\"type\":\"COURSE\",\"place\":\"С-213 (Корпус С)\",\"groups\":\"С-12-16\",\"person\":\"доц. Капустина О.М.\",\"time\":{\"start\":\"13:45:00\",\"end\":\"15:20:00\"},\"number\":3},{\"name\":\"Динамика мехатронных систем\",\"type\":\"LECTURE\",\"place\":\"С-215 (Корпус С)\",\"groups\":\"С-12-16\",\"person\":\"проф. Кобрин А.И.\",\"time\":{\"start\":\"15:35:00\",\"end\":\"17:10:00\"},\"number\":4}]},{\"dayOfWeek\":4,\"date\":\"2020-05-21\",\"classes\":[{\"name\":\"Датчики и системы управления\",\"type\":\"LAB\",\"place\":\"Б-418 (Корпус Б)\",\"groups\":\"С-12-16\",\"person\":\"ст.преп. Астахов С.В.\",\"time\":{\"start\":\"13:45:00\",\"end\":\"15:20:00\"},\"number\":3},{\"name\":\"Экология\",\"type\":\"PRACTICE\",\"place\":\"К-504 (Корпус КИЖ)\",\"groups\":\"С-12-16\",\"person\":\"\",\"time\":{\"start\":\"15:35:00\",\"end\":\"17:10:00\"},\"number\":4}]},{\"dayOfWeek\":5,\"date\":\"2020-05-22\",\"classes\":[{\"name\":\"Вычислительные методы компьютерного моделирования в механике\",\"type\":\"LECTURE\",\"place\":\"С-215 (Корпус С)\",\"groups\":\"С-12-16\",\"person\":\"ст.преп. Маслов А.Н.\",\"time\":{\"start\":\"09:20:00\",\"end\":\"10:55:00\"},\"number\":1},{\"name\":\"Вычислительные методы компьютерного моделирования в механике\",\"type\":\"PRACTICE\",\"place\":\"С-215 (Корпус С)\",\"groups\":\"С-12-16\",\"person\":\"ст.преп. Маслов А.Н.\",\"time\":{\"start\":\"11:10:00\",\"end\":\"12:45:00\"},\"number\":2},{\"name\":\"Датчики и системы управления\",\"type\":\"LECTURE\",\"place\":\"С-215 (Корпус С)\",\"groups\":\"С-12-16\",\"person\":\"проф. Подалков В.В.\",\"time\":{\"start\":\"13:45:00\",\"end\":\"15:20:00\"},\"number\":3}]}]}"
    }
}
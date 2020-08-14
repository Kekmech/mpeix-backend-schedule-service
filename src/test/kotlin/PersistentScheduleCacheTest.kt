import com.google.gson.*
import com.kekmech.cache.*
import com.kekmech.dto.*
import com.kekmech.gson.*
import io.netty.util.internal.logging.*
import kekmech.ru.common_network.gson.*
import org.junit.jupiter.api.*
import java.io.*
import java.text.*
import java.time.*
import java.util.*
import kotlin.random.Random

class PersistentScheduleCacheTest {

    lateinit var persistentCache: PersistentScheduleCache

    private fun init() {
        val gson = GsonBuilder().apply {
            setDateFormat(DateFormat.LONG)
            registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
            registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        }.create()
        persistentCache = PersistentScheduleCache(
            gson = gson,
            maxEntries = MAX_ENTRIES,
            expirationRequestCount = EXP_REQUEST_COUNT,
            cacheDir = File("./cache/schedule_test"),
            log = Slf4JLoggerFactory.getInstance("SCHEDULE")
        )
    }

    @Test
    fun testPutAndGet() {
        init()
        keys.forEachIndexed { i, k ->
            persistentCache.put(k, schedules[i])
            assert(persistentCache.get(k) == schedules[i])
        }
    }

    @Test
    fun testPutAndGetConcurrent() {
        init()
        val thread1 = Thread {
            for (j in 1..1000) {
                keys.forEachIndexed { i, k ->
                    persistentCache.put(k, schedules[i])
                    assert(persistentCache.get(k) == schedules[i])
                }
            }
        }
        val thread2 = Thread {
            for (j in 1..1000) {
                keys.forEachIndexed { i, k ->
                    persistentCache.put(k, schedules[i])
                    assert(persistentCache.get(k) == schedules[i])
                }
            }
        }
        thread1.isDaemon = false
        thread2.isDaemon = false
        thread1.start()
        thread2.start()
        thread1.join()
        thread2.join()
    }

    companion object {
        const val MAX_ENTRIES = 3
        const val EXP_REQUEST_COUNT = 3

        val key1 = PersistentScheduleCache.Key("C-12-16", 36)
        val key2 = PersistentScheduleCache.Key("C-12-16", 37)
        val key3 = PersistentScheduleCache.Key("C-12-17", 38)
        val key4 = PersistentScheduleCache.Key("C-12-16", 39)
        val key5 = PersistentScheduleCache.Key("C-12-17", 40)

        val key6 = PersistentScheduleCache.Key("A-8-19", 36)
        val key7 = PersistentScheduleCache.Key("A-8-18", 37)
        val key8 = PersistentScheduleCache.Key("A-8-19", 38)
        val key9 = PersistentScheduleCache.Key("A-8-18", 39)
        val key10 = PersistentScheduleCache.Key("A-8-19", 40)

        val keys = listOf(key1, key2, key3, key4, key5, key6, key7, key8, key9, key10)

        val schedules = Array(10) { randomSchedule() }.toList()

        private fun randomSchedule(): Schedule {
            val groupNumber = listOf("C-12-16","C-12-17","A-8-19","A-8-18").random()
            return Schedule(
                groupNumber = groupNumber,
                groupId = groupNumber.hashCode().toString(),
                weeks = listOf(
                    Week(
                        weekOfYear = Random.nextInt(36, 40),
                        weekOfSemester = -1,
                        firstDayOfWeek = LocalDate.now(),
                        days = Array(3) {
                            Day(
                                dayOfWeek = it,
                                date = LocalDate.now().plusDays(it - 1L),
                                classes = Array(4) {
                                    Classes(
                                        name = UUID.randomUUID().toString(),
                                        type = ClassesType.values().asList().random(),
                                        place = UUID.randomUUID().toString(),
                                        groups = UUID.randomUUID().toString(),
                                        person = "",
                                        time = Time(),
                                        number = it
                                    )
                                }.toList()
                            )
                        }.toList()
                    )
                )
            )
        }
    }
}
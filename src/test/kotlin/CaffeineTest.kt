import com.github.benmanes.caffeine.cache.*
import org.junit.jupiter.api.*

class CaffeineTest {

    @Test
    fun concurrentTest() {
        val cache: Cache<Int, String> = Caffeine.newBuilder()
            .maximumSize(10)
            .build()
        cache.put(1, "1")
        cache.put(2, "2")
        cache.put(3, "3")
        cache.put(4, "4")
        cache.put(5, "5")
        val thread1 = Thread {
            println(cache.get(6) { k -> getExpensiveValue(k) })
        }
        val thread2 = Thread {
            for (i in 1..5) {
                Thread.sleep(1000)
                println(cache.get(i) { k -> getExpensiveValue(k) })
            }
        }
        thread1.start()
        thread2.start()
        thread1.join()
        thread2.join()
    }

    private fun getExpensiveValue(v: Int): String {
        Thread.sleep(5000)
        return v.toString()
    }
}
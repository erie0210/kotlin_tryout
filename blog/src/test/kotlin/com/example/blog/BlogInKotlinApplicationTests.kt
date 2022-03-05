package com.example.blog

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.Redisson
import org.redisson.client.RedisClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import redis.embedded.RedisServer
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest
class BlogInKotlinApplicationTests {
    // val: 불변 var: 가변
    var count_limit: Int = 10
    var total_prize_price: Int = 0
    val PRIZE_PRICE: Int = 500
    val logger: Logger = LoggerFactory.getLogger(BlogInKotlinApplicationTests::class.java.name)

    private val testSample: Sample = Sample()

    @BeforeEach
    fun setup() {
        count_limit = 10
        total_prize_price = 0
    }

    @Test
    fun kotlinSampleTest() {
        val expected: Int = 42
        assertEquals(expected, testSample.sum(30, 12))
        assertNotEquals(expected, testSample.sum(30, 13))
    }

    @Test
    fun uncontrolled() {
        var threads = ArrayList<Thread>()
        for (place: Int in 0 until 100) {
            threads.add(thread(start = false) {
                // api 통신에 50ms
                Thread.sleep(50)

                try {
                    if (count_limit > 0) {
                        count_limit--
                        total_prize_price += PRIZE_PRICE
                    }
                } catch (e: NumberFormatException) {
                    null
                }
            })
        }

        for (t in threads) {
            t.start()
        }
        for (t in threads) {
            t.join()
        }
        // logger.info("total_prize_price: $total_prize_price")
    }

    @Test
    fun semaphoreTest() {
        val sem = Semaphore(1)
        var threads = ArrayList<Thread>(5000)

        for (place: Int in 0 until 100) {
            threads.add(thread(start = false) {
                try {
                    if (sem.tryAcquire(30000, TimeUnit.MILLISECONDS)) {
                        // logger.info("${Thread.currentThread().name}/ place:$place was permitted.")
                        if (count_limit > 0) {
                            // api 통신에 50ms
                            Thread.sleep(50)
                            count_limit--
                            total_prize_price += PRIZE_PRICE
                            // logger.info("place: $place // total prize price: $total_prize_price")
                        }
                    }
                } catch (e: Exception) {
                    logger.error(e.toString())
                } finally {
                    // logger.info("${Thread.currentThread().name} try release.")
                    sem.release()
                }
            })
        }
        for (t in threads) {
            t.start()
        }
        for (t in threads) {
            t.join()
        }
        // logger.info("total_prize_price: $total_prize_price")
    }

    @Test
    fun redisTest() {
        var threads = ArrayList<Thread>(5000)
        var redis = RedisServer()
        redis.start()
        Thread.sleep(5000)
        var redisson = Redisson.create()
        var lock = redisson.getLock("myLock")


        for (place: Int in 0 until 100) {
            threads.add(thread(start = false) {
                try {
                    var isLocked = lock.tryLock(30000, 2000, TimeUnit.MILLISECONDS)
                    if (!isLocked) {
                        logger.warn("Redis Lock fail");
                    }
                     logger.info("${Thread.currentThread().name}/ place:$place was permitted.")
                    if (count_limit > 0) {
                        // api 통신에 50ms
                        Thread.sleep(50)
                        count_limit--
                        total_prize_price += PRIZE_PRICE
                         logger.info("place: $place // total prize price: $total_prize_price")
                    }
                } catch (e: Exception) {
                    throw java.lang.RuntimeException(e)
                } finally {
                    logger.info("${Thread.currentThread().name} try release.")
                    lock.unlock()
                }
            })
        }
        for (t in threads) {
            t.start()
        }
        for (t in threads) {
            t.join()
        }
         logger.info("total_prize_price: $total_prize_price")
        redis.stop()
    }
}

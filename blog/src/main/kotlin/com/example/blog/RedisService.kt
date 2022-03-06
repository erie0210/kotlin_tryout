package com.example.blog

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(val redisson: RedissonClient) {

    private val DEFAULT_WAIT_TIME: Long = 15_000
    private val DEFAULT_LEASE_TIME: Long = 5_000

    // 편의를 위해 기본값 셋팅한 함수
    fun tryLock(name: String): RLock {
        return tryLock(name, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS)
    }

    fun tryLock(name: String, waitTime: Long, leaseTime: Long, timeUnit: TimeUnit): RLock {
        val lock = redisson.getLock(name)
        if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
            return lock
        } else {
            throw IllegalStateException("Lock 획득에 실패했어요. key: $name")
        }
    }

    fun release(lock: RLock) {
        lock.unlock()
    }
}
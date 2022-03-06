package com.example.blog.service

import org.springframework.stereotype.Service

@Service
class CouponService(
    private val redisService: RedisService
) {

    fun getCoupon(couponName: String, userId: Long): Boolean {
        val lock = redisService.tryLock(couponName)
        try {
            // Use UserId do something
        } finally {
            redisService.release(lock)
        }

        return false
    }
}
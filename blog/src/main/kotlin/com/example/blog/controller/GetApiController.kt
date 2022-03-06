package com.example.blog.controller

import com.example.blog.service.CouponService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jy")
class GetApiController(
    private val couponService: CouponService
) {

    @GetMapping("/hello")
    fun getHello(): String {
        return "hello jaeyoung"
    }

    @GetMapping("/coupon")
    fun getCoupon(
        @RequestParam("coupon_name") couponName: String,
        @RequestParam("user_id") userId: Long
    ): ResponseEntity<String> {
        val success = couponService.getCoupon(couponName, userId)

        if (!success) {
            throw IllegalStateException("이미 발급이 끝난 쿠폰이거나, 쿠폰 발급 과정 중 오류가 발생했어요. couponName: $couponName, userId: $userId")
        }

        return ResponseEntity.ok("쿠폰이 발급되었습니다. couponName: $couponName, userId: $userId")
    }
}
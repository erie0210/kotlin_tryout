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
            return ResponseEntity.status(400)
                .body("이미 발급이 끝난 쿠폰이에요. couponName: $couponName, userId: $userId")

            // 이렇게 표현할 수도 있어요
            // return ResponseEntity.badRequest()
            //    .body("이미 발급이 끝난 쿠폰이에요. couponName: $couponName, userId: $userId")
        }

        return ResponseEntity.status(200)
            .body("쿠폰이 발급되었습니다. couponName: $couponName, userId: $userId")

        // 이렇게 표현할 수도 있어요
        // return ResponseEntity.ok("쿠폰이 발급되었습니다. couponName: $couponName, userId: $userId")
    }
}
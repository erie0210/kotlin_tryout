package com.example.blog.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jy")
class GetApiController {

    @GetMapping("hello")
    fun getHello(): String {
        return "hello jaeyoung";
    }
}
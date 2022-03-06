package com.example.blog.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer
import javax.annotation.PreDestroy

@Configuration
class RedisConfig {
    lateinit var redisServer: RedisServer

    @Bean
    fun redisson(): RedissonClient {
        // Local에 Embedded Redis를 띄운다.
        redisServer = RedisServer()
        redisServer.start()

        val config = Config()
        config.useSingleServer().setAddress("redis://127.0.0.1:6379")

        val result = Redisson.create(config)
        return result
    }

    @PreDestroy
    fun destroy() {
        redisServer.stop()
    }
}
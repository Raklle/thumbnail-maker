package kkk.to.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RequestController {

    @GetMapping("/test")
    fun testEndpoint(): String {
        return "Testing"
    }

    @GetMapping("/api")
    fun endpoint() {

    }
}
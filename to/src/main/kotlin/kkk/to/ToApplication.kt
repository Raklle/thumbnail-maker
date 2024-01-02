package kkk.to

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class ToApplication

fun main(args: Array<String>) {
	runApplication<ToApplication>(*args)
}

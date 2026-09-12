package com.quick

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuickApplication

fun main(args: Array<String>) {
	runApplication<QuickApplication>(*args)
}

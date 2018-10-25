package com.jahnelgroup.util.activedirectoryproxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@EnableZuulProxy
@SpringBootApplication
class ActiveDirectoryProxyApplication

fun main(args: Array<String>) {
    runApplication<ActiveDirectoryProxyApplication>(*args)
}

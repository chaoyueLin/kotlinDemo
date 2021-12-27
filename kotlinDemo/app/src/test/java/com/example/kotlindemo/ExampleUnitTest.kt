package com.example.kotlindemo

import com.example.kotlindemo.cor.CorContextDemo
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val demo: CorContextDemo = CorContextDemo()
        demo.exceptionWithJob()
    }
}

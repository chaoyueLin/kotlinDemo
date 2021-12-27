/*****************************************************************
 * * File: - CorContextDemo
 * * Description:
 * * Version: 1.0
 * * Date : 2020/12/22
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/22    1.0         create
 ******************************************************************/
package com.example.kotlindemo.cor

import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/*****************************************************************
 * * File: - CorContextDemo
 * * Description:
 * * Version: 1.0
 * * Date : 2020/12/22
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/22    1.0         create
 ******************************************************************/
public class CorContextDemo {
    companion object {
        const val TAG = "CorContextDemo"
    }

    fun test() {
        var context = Job() + Dispatchers.IO + CoroutineName("aa")
        Log.d(TAG, "$context, ${context[CoroutineName]}")
        context = context.minusKey(Job)
        Log.d(TAG, "$context")

    }

    fun main() = runBlocking {
        val job = GlobalScope.launch { // launch 根协程
            println("Throwing exception from launch")
            throw IndexOutOfBoundsException() // 我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
        }
        job.join()
        println("Joined failed job")
        val deferred = GlobalScope.async { // async 根协程
            println("Throwing exception from async")
            throw ArithmeticException() // 没有打印任何东西，依赖用户去调用等待
        }
        try {
            deferred.await()
            println("Unreached")
        } catch (e: ArithmeticException) {
            println("Caught ArithmeticException")
        }
    }

    fun exceptionWithJob() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        val mainScope = CoroutineScope(Dispatchers.Default + Job() + exceptionHandler)
        mainScope.launch {
            println("job1 start")
            delay(2000)
            println("job1 end")
        }

        mainScope.launch {
            println("job2 start")
            delay(1000)
            1 / 0
            println("job2 end")
        }


        mainScope.launch {
            println("job3 start")
            delay(2000)
            println("job3 end")
        }
    }

    fun exceptionWithSupervisorJob() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        val mainScope = CoroutineScope(Dispatchers.Default + SupervisorJob() + exceptionHandler)
        mainScope.launch {
            println("job1 start")
            delay(2000)
            println("job1 end")
        }

        mainScope.launch {
            println("job2 start")
            delay(1000)
            1 / 0
            println("job2 end")
        }


        mainScope.launch {
            println("job3 start")
            delay(2000)
            println("job3 end")
        }
    }

    fun cancelExceptionWithJob() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        val mainScope = CoroutineScope(Dispatchers.Default + Job() + exceptionHandler)
        mainScope.launch {
            println("job1 start")
            delay(2000)
            println("job1 end")
        }

        mainScope.launch {
            println("job2 start")
            delay(1000)
            throw CancellationException()
            println("job2 end")
        }


        mainScope.launch {
            println("job3 start")
            delay(2000)
            println("job3 end")
        }
    }

    fun exceptionParentWithJob() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        val childExceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("chlid CoroutineExceptionHandler got $exception")
        }
        val mainScope = CoroutineScope(Dispatchers.Default + Job() + exceptionHandler)
        mainScope.launch {
            println("job4 start")
            delay(2000)
            println("job4 end")
        }

        mainScope.launch {
            launch(Job(coroutineContext[Job]) + childExceptionHandler) {
                println("job5 start")
                delay(1000)
                1 / 0
                println("job5 end")
            }
        }


        mainScope.launch {
            println("job6 start")
            delay(2000)
            println("job6 end")
        }
    }

    fun exceptionChildWithJob() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        val childExceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("chlid CoroutineExceptionHandler got $exception")
        }
        val mainScope = CoroutineScope(Dispatchers.Default + Job() + exceptionHandler)
        mainScope.launch {
            println("job4 start")
            delay(2000)
            println("job4 end")
        }

        mainScope.launch {
            launch(SupervisorJob(coroutineContext[Job]) + childExceptionHandler) {
                println("job5 start")
                delay(1000)
                1 / 0
                println("job5 end")
            }
        }


        mainScope.launch {
            println("job6 start")
            delay(2000)
            println("job6 end")
        }
    }

}
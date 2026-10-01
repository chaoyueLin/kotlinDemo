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

    /**
     * coroutineScope 作用域构建器：一个子协程失败，兄弟协程全部取消，异常抛给调用者
     */
    fun coroutineScopeBuilder() {
        runBlocking {
            try {
                coroutineScope {
                    launch {
                        println("job1 start")
                        delay(2000)
                        println("job1 end")
                    }
                    launch {
                        println("job2 start")
                        delay(1000)
                        1 / 0
                        println("job2 end")
                    }
                    launch {
                        println("job3 start")
                        delay(2000)
                        println("job3 end")
                    }
                }
            } catch (e: ArithmeticException) {
                println("coroutineScope threw $e")
            }
        }
    }

    /**
     * supervisorScope 作用域构建器：一个子协程失败，不影响其他兄弟协程
     */
    fun supervisorScopeBuilder() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        runBlocking(exceptionHandler) {
            supervisorScope {
                launch {
                    println("job1 start")
                    delay(2000)
                    println("job1 end")
                }
                launch {
                    println("job2 start")
                    delay(1000)
                    1 / 0
                    println("job2 end")
                }
                launch {
                    println("job3 start")
                    delay(2000)
                    println("job3 end")
                }
            }
            println("supervisorScope finished")
        }
    }

    /**
     * 取消是协作式的：delay 是挂起点，取消在挂起点生效，finally 依然会执行
     */
    fun cancelJob() {
        runBlocking {
            val job = launch {
                try {
                    repeat(5) { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500)
                    }
                } finally {
                    println("job: I'm running finally")
                }
            }
            delay(1300)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // 等价于 job.cancel() + job.join()
            println("main: Now I can quit. isCancelled=${job.isCancelled}")
        }
    }

    /**
     * 没有挂起点时取消不生效，需要用 while (isActive)、ensureActive() 或 yield() 配合
     */
    fun cancelWithoutSuspensionPoint() {
        runBlocking {
            val job = launch(Dispatchers.Default) {
                var i = 0
                while (i < 5) {
                    Thread.sleep(500) // 阻塞而不是挂起，取消打不断它
                    println("job: I'm sleeping $i ...")
                    i++
                }
            }
            delay(1300)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin()
            println("main: Now I can quit.")
        }
    }

    /**
     * 取消后 finally 里再挂起会立刻抛 CancellationException，用 NonCancellable 兜住清理逻辑
     */
    fun nonCancellableDemo() {
        runBlocking {
            val job = launch {
                try {
                    repeat(5) { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500)
                    }
                } finally {
                    withContext(NonCancellable) {
                        println("job: releasing resources, isActive=$isActive")
                        delay(1000)
                        println("job: resources released")
                    }
                }
            }
            delay(1300)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin()
            println("main: Now I can quit.")
        }
    }

    /**
     * withTimeout 超时抛 TimeoutCancellationException，withTimeoutOrNull 超时返回 null
     */
    fun timeoutDemo() {
        runBlocking {
            try {
                withTimeout(1000) {
                    repeat(5) { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                println("caught $e")
            }

            val result = withTimeoutOrNull(1000) {
                delay(2000)
                "done"
            }
            println("result = $result")
        }
    }
}
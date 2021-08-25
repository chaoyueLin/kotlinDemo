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
class CorContextDemo {
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

}
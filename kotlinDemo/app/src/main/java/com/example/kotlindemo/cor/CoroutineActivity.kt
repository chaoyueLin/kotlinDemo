package com.example.kotlindemo.cor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.kotlindemo.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import java.lang.Runnable
import java.lang.Thread.sleep


class CoroutineActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    companion object {
        val TAG = "CoroutineActivity"
    }

    private lateinit var mScope: CoroutineScope

    val task1: () -> String = {
        sleep(2000)
        "Hello".also { println("task1 finished: $it") }
    }

    val task2: () -> String = {
        sleep(2000)
        "World".also { println("task2 finished: $it") }
    }

    val task3: (String, String) -> String = { p1, p2 ->
        sleep(2000)
        "$p1 $p2".also { println("task3 finished: $it") }
    }

    val ThreadPool =
        newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors() * 2, "ThreadPoolTest")

    private fun taskLaunch(delayTime: Long = 0, job: suspend () -> Unit) =
        mainScope.launch(ThreadPool) {
            delay(delayTime)
            job()
        }

    private fun <T> taskAsync(delayTime: Long = 0, job: suspend () -> T) =
        mainScope.async(ThreadPool) {
            delay(delayTime)
            job()
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coruntine)

        mScope = CoroutineScope(ThreadPool)
        mScope.launch(CoroutineName("initCoroutine")) {
            start()
        }

        val flow1 = flow<String> { emit(task1()) }
        val flow2 = flow<String> { emit(task2()) }
        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
//            release()
//            GlobalScope.launch(Dispatchers.IO) {
//                val c1 = async(Dispatchers.IO) {
//                    task1()
//                }
//
//                val c2 = async(Dispatchers.IO) {
//                    task2()
//                }
//
//                task3(c1.await(), c2.await())
//            }

            GlobalScope.launch(Dispatchers.IO) {
                flow1.zip(flow2) { t1, t2 ->
                    task3(t1, t2)
                }.collect { value -> println(value) }
            }


        }
//        CorContextDemo().test()
//        CorContextDemo().main()


    }

    private suspend fun start(): Int {
        return withContext(Dispatchers.Default) {
            Log.d(TAG, Thread.currentThread().name + " sleep before")
            Thread(Runnable {
                Log.d(TAG, Thread.currentThread().name + " new thread sleep before")
                Thread.sleep(15000)
                Log.d(TAG, Thread.currentThread().name + " new thread  sleep after")
            }).start()
            delay(15000)
            Log.d(TAG, Thread.currentThread().name + " sleep after")
        }

    }

    fun release() {
        try {
            // 1.if init coroutine has not been finished, cancel it's jobs
            Log.d(TAG, Thread.currentThread().name + " mScope.isActive=" + mScope.isActive)
            if (mScope.isActive) {
                mScope.cancel("release engine", null)
                Log.d(
                    TAG,
                    Thread.currentThread().name + "cancel mScope.isActive=" + mScope.isActive
                )
            }
        } catch (e: IllegalStateException) {
            Log.d(TAG, Thread.currentThread().name + " IllegalStateException" + e.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

}

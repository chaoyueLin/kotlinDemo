package com.example.kotlindemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.coroutines.*
import java.lang.Runnable


class CoruntineActivity : AppCompatActivity() {
    companion object {
        val TAG = "CoruntineActivity"
    }

    private lateinit var mScope: CoroutineScope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coruntine)


        mScope = CoroutineScope(Dispatchers.Default)
        mScope.launch(CoroutineName("initCoroutine")) {
            start()
        }
        val button: Button = findViewById(R.id.button)
        button.setOnClickListener(View.OnClickListener {
                release()
        })
        CorContextDemo().test()
//        CorContextDemo().main()
    }

    suspend fun start() {
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
                Log.d(TAG, Thread.currentThread().name + "cancel mScope.isActive=" + mScope.isActive)
            }
        } catch (e: IllegalStateException) {
            Log.d(TAG, Thread.currentThread().name + " IllegalStateException" + e.message)
        }
    }

}

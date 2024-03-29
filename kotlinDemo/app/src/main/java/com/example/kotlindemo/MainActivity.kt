package com.example.kotlindemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.kotlindemo.cor.CoroutineActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button2: Button = findViewById(R.id.button2)
        button2.setOnClickListener {
            val intent: Intent = Intent(this@MainActivity, CoroutineActivity::class.java)
            startActivity(intent)
        }
    }
}

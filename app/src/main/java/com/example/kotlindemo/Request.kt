package com.example.kotlindemo

import android.util.Log
import java.net.URL

/**
 * 创建人：linchaoyue
 * 创建时间：2020-03-22 23:27
 * 描述：
 */
public class Request(val url:String){
    fun run(){
        val forecastJsonStr=URL(url).readText()
        Log.d(javaClass.simpleName,forecastJsonStr)
    }
}
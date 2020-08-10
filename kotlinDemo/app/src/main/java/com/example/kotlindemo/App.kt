package com.example.kotlindemo

import android.app.Application
import kotlin.properties.Delegates

/**
 * 创建人：linchaoyue
 * 创建时间：2020-03-29 16:59
 * 描述：
 */
class App :Application(){
    companion object{
         var instance:App by Delegates.notNull()
    }
    override fun onCreate() {
        super.onCreate()
        instance=this
    }
}
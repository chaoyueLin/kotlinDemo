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
package com.example.kotlindemo

import android.util.Log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

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

}
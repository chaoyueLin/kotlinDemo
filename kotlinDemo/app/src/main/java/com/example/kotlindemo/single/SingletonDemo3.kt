/*****************************************************************
 * * File: - SingleDemo3
 * * Description:
 * * Version: 1.0
 * * Date : 2020/9/2
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/2    1.0         create
 ******************************************************************/
package com.example.kotlindemo.single

import android.content.Context

/*****************************************************************
 * * File: - SingleDemo3
 * * Description:
 * * Version: 1.0
 * * Date : 2020/9/2
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/2    1.0         create
 ******************************************************************/
class SingleDemo3 private constructor(context: Context) {
    init {

    }

    companion object : SingleHolder<SingleDemo3, Context>(::SingleDemo3)
}

open class SingleHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null
    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

class FileSingleton private constructor(path: String) {
    companion object : SingleHolder<FileSingleton, String>(::FileSingleton)
}
/*****************************************************************
 * * File: - Singleton
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

/*****************************************************************
 * * File: - Singleton
 * * Description:
 * * Version: 1.0
 * * Date : 2020/9/2
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/2    1.0         create
 ******************************************************************/
class SingletonDemo private constructor() {
    companion object {
        val instance =
            SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = SingletonDemo()
    }
//    companion object Factory {
//        fun create(): SingletonDemo = SingletonDemo()
//    }

//    companion object{
//
//    }
}


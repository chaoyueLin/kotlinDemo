/*****************************************************************
 * * File: - SingletonDemo
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
 * * File: - SingletonDemo
 * * Description:
 * * Version: 1.0
 * * Date : 2020/9/2
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/2    1.0         create
 ******************************************************************/
class SingletonDemo1 private constructor() {
    companion object {
        val instance: SingletonDemo1 by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SingletonDemo1()
        }
    }

//    companion object {
//        val instance: SingletonDemo1 = SingletonDemo1()
//    }
}

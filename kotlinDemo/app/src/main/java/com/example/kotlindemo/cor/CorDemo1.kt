/*****************************************************************
 * * File: - CorDemo1
 * * Description:
 * * Version: 1.0
 * * Date : 2020/12/17
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/17    1.0         create
 ******************************************************************/
package com.example.kotlindemo.cor

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*****************************************************************
 * * File: - CorDemo1
 * * Description:
 * * Version: 1.0
 * * Date : 2020/12/17
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/17    1.0         create
 ******************************************************************/
class CorDemo1 : ViewModel() {
    private val length = 0
    fun loadCardListLength() {
        viewModelScope.launch {
            val res = requestCardListLength2()

        }
    }

    private fun requestCardListLength(): Int =
        length + 2

    private suspend fun requestCardListLength1(): Int =
        length + 2

    private suspend fun requestCardListLength2(): Int = withContext(Dispatchers.IO) {
        delay(2000)
        return@withContext length + 2
    }
}
package com.example.kotlindemo.domain.model



/**
 * 创建人：linchaoyue
 * 创建时间：2020-03-23 09:55
 * 描述：
 */
data class ForecastList (val city:String,val country:String,val dailyForecast:List<Forecast>){
    operator fun get(position:Int):Forecast=dailyForecast[position]

    fun size():Int=dailyForecast.size
}
data class Forecast(val date:String,val descreiption:String,val hight:Int,val low:Int)
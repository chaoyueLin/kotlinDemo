package com.example.kotlindemo

import java.util.*

/**
 * 创建人：linchaoyue
 * 创建时间：2020-03-23 09:56
 * 描述：
 */
data class ForecastResult(val city:City,val list:List<Forecast>)
data class City(val id:Long,val name:String,val corrd:Coordinates,val country:String,val population:Int)
data class Coordinates(val long:Float,val lat:Float)
data class Forecast(val data: Long, val temperature:Temperature, val pressure:Float, val humidity:Int, val  weather:List<Weather>,
                    val speed:Float, val deg:Int, val clouds:Int, val rain:Float)
data class Temperature(val day:Float,val min:Float,val max:Float,val night:Float,val eve:Float,val morn:Float)
data class Weather(val id:Long,val main:String,val description:String,val icon:String)
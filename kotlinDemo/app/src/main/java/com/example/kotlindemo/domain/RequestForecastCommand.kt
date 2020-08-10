package com.example.kotlindemo.domain

import com.example.kotlindemo.ForecastRequest

import com.example.kotlindemo.domain.model.ForecastList

/**
 * 创建人：linchaoyue
 * 创建时间：2020-03-23 10:12
 * 描述：
 */
class RequestForecastCommand (val zipCode:String):Command<ForecastList>{
    override fun execute(): ForecastList {
       val forecastRequest=ForecastRequest(zipCode)
        return ForecastDataMapper().convertForemDataModel(forecastRequest.execute())
    }

}
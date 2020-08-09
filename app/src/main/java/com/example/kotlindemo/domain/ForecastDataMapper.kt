package com.example.kotlindemo.domain

import com.example.kotlindemo.Forecast
import com.example.kotlindemo.ForecastResult
import com.example.kotlindemo.domain.model.ForecastList
import java.text.DateFormat
import java.util.*
import com.example.kotlindemo.domain.model.Forecast as ModelForecast
/**
 * 创建人：linchaoyue
 * 创建时间：2020-03-23 09:58
 * 描述：
 */
public class ForecastDataMapper{
    fun convertForemDataModel(forecast:ForecastResult): ForecastList {
        return ForecastList(
            forecast.city.name,
            forecast.city.country,
            convertForecastListToDomain(forecast.list)
        )
    }

    private fun convertForecastListToDomain(list: List<Forecast>): List<ModelForecast> {
        return list.map { convertForecastItemToDomain(it) }
    }

    private fun convertForecastItemToDomain(forecast: Forecast) :ModelForecast{
        return ModelForecast(convertData(forecast.data),forecast.weather[0].description,forecast.temperature.max.toInt(),forecast.temperature.min.toInt())
    }

    private fun convertData(date: Long): String {
        val df=DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return df.format(date*1000)
    }

}
package com.example.kotlindemo


import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindemo.domain.model.ForecastList

/**
 * 创建人：linchaoyue
 * 创建时间：2020-03-22 16:53
 * 描述：
 */
class ForecastListAdapter (val weekForecast:ForecastList,val itemClick:(Forecast)->Unit): RecyclerView.Adapter<ForecastListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(TextView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       with(weekForecast[position]){
           holder.textView.text="$date-$descreiption"
       }
    }

    override fun getItemCount(): Int {
        return  weekForecast.size()
    }


    class ViewHolder(val textView:TextView):RecyclerView.ViewHolder(textView)

    public interface OnItemClickListener{
        operator fun invoke(forecast:Forecast)
    }
}
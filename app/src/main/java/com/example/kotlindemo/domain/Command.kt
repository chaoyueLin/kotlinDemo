package com.example.kotlindemo.domain

/**
 * 创建人：linchaoyue
 * 创建时间：2020-03-23 09:53
 * 描述：
 */
interface Command<T> {
    fun execute():T
}
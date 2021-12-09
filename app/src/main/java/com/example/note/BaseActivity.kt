package com.example.note

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNightMode()
    }

    //设置主题
    fun setNightMode() {
        setTheme(R.style.DayTheme)
    }
}
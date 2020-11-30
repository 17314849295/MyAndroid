package com.zhoujie.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhoujie.myandroid.log
import com.zhoujie.myandroid.view.NumberView

private const val videoUri = "content://media/external/video/media/25"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val numberView = findViewById<NumberView>(R.id.numberView)
        numberView.setTimeCallback(object : NumberView.TimeCallback {
            override fun onTimeChanged(time: Long) {
                time.log()
            }
        })
    }
}
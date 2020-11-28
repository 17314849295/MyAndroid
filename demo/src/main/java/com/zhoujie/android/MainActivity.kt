package com.zhoujie.android

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhoujie.myandroid.log
import com.zhoujie.myandroid.view.VideoRangeSlider

private const val videoUri = "content://media/external/video/media/25"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val videoRangeSlider = findViewById<VideoRangeSlider>(R.id.videoRangeSlider)
        videoRangeSlider.setDataSource(Uri.parse(videoUri))
        videoRangeSlider.setVideoCallback {
            onVideoRangeChanged { startTime, endTime ->
                startTime.log()
                endTime.log()
            }
        }
    }
}
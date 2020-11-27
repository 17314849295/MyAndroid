package com.zhoujie.myandroid

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import androidx.annotation.RequiresApi

@RequiresApi(21)
class VideoDecoder {

    private val mediaExtractor = MediaExtractor()
    private var mediaFormat: MediaFormat? = null
    private val mediaCodecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)

    fun decode(context: Context, videoUri: Uri) {
        mediaExtractor.apply {
            setDataSource(context, videoUri, null)
            for (index in 0..trackCount) {
                mediaFormat = getTrackFormat(index)
                mediaFormat?.let {
                    val mime = it.getString(MediaFormat.KEY_MIME)
                    if (mime != null && mime == "video") {
                        selectTrack(index)
                    }
                }
            }
        }
        val codecName = mediaCodecList.findDecoderForFormat(mediaFormat)
        val codec = MediaCodec.createByCodecName(codecName)
        codec.setCallback(object : MediaCodec.Callback() {
            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                TODO("Not yet implemented")
            }

            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                TODO("Not yet implemented")
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                TODO("Not yet implemented")
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                TODO("Not yet implemented")
            }
        })
    }
}
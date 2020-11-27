package com.zhoujie.myandroid.advertising

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

class VideoFrameExtractor {

//    fun extract(context: Context, videoUri: Uri, fps: Int) {
//        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
//            setDataSource(context, videoUri)
//            val duration = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//            val frameDuration = if (duration != null) {
//                duration.toLong() / fps
//            } else {
//                0L
//            }
//            val bitmap = getFrameAtTime(frameDuration, MediaMetadataRetriever.OPTION_CLOSEST)
//        }
//    }

    fun extract(context: Context, videoUri: Uri, fps: Int) {
        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(context, videoUri)
            val duration = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val frameDuration = if (duration != null) {
                duration.toLong() / fps
            } else {
                0L
            }
            val bitmap = getFrameAtTime(frameDuration, MediaMetadataRetriever.OPTION_CLOSEST)
        }
    }
}
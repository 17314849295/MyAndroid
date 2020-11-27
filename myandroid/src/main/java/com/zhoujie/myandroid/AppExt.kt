package com.zhoujie.myandroid

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

fun Any.log(tag: String = "测试日志") {
    Log.d(tag, this.toString())
}

fun SharedPreferences.open(block: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.block()
    editor.apply()
}

fun Int.px(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}
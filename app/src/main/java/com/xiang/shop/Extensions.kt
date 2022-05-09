package com.xiang.shop

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity

fun Activity.setNickname(nickName : String) {
    getSharedPreferences("shop", AppCompatActivity.MODE_PRIVATE)
        .edit()
        .putString("NICK_NAME", nickName)
        .apply()
}

fun Activity.getNickname() =
    getSharedPreferences("shop",AppCompatActivity.MODE_PRIVATE)
        .getString("NICK_NAME", "")
package com.example.mysocialandroidapp.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.api.TIMEOUT

object ImageSetter {
    fun set(imageView: ImageView, url: String?, circleCrop: Boolean){
        if (circleCrop) {
            Glide.with(imageView)
                .load(url)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .timeout(TIMEOUT)
                .circleCrop()
                .into(imageView)
        } else {
            Glide.with(imageView)
                .load(url)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .timeout(TIMEOUT)
                .into(imageView)
        }
    }
}
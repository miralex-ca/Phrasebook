package com.online.languages.study.lang.utils

import android.widget.ImageView
import com.online.languages.study.lang.adapters.RoundedTransformation
import com.squareup.picasso.Picasso

fun ImageView.loadAssetPicCircle(path: String) {
    Picasso.with(context)
        .load("file:///android_asset/pics/$path")
        .fit()
        .centerCrop()
        .transform(RoundedTransformation(0, 0))
        .into(this)
}

fun ImageView.loadImage(path: String) {
    Picasso.with(context)
        .load(path)
        .fit()
        .centerCrop()
        .into(this)
}